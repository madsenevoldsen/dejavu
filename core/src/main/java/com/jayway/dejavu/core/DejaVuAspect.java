package com.jayway.dejavu.core;

import com.jayway.dejavu.core.annotation.Impure;
import com.jayway.dejavu.core.exception.CircuitOpenException;
import com.jayway.dejavu.core.repository.TraceCallback;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Aspect
public class DejaVuAspect {

    private static TraceCallback callback;
    private static boolean traceMode = true;
    private static int timeout = 10 * 60 * 1000; // ten minutes
    private static int exceptionThreshold = 10;

    private static ThreadLocal<String> traceId = new ThreadLocal<String>();
    private static ThreadLocal<String> threadId = new ThreadLocal<String>();
    private static ThreadLocal<Boolean> threadLocalInIntegrationPoint = new ThreadLocal<Boolean>();

    private static Map<String, CircuitBreaker> circuitBreakers;
    private static Map<String, RunningTrace> runningTraces;
    private static Map<Runnable, String> threadIdMap;

    public static void initialize( TraceCallback cb ) {
        callback = cb;
        circuitBreakers = new HashMap<String, CircuitBreaker>();
        runningTraces = new HashMap<String, RunningTrace>();
        threadIdMap = new HashMap<Runnable, String>();
    }

    public static void destroy() {
        callback = null;
        circuitBreakers = null;
        runningTraces = null;
        threadIdMap = null;
    }

    protected static void setTraceMode( boolean mode ) {
        traceMode = mode;
    }
    public static void addCircuitBreaker( CircuitBreaker handler ) {
        circuitBreakers.put(handler.getName(), handler);
    }
    public static void addCircuitBreaker( String name, int timeout, int exceptionThreshold ) {
        circuitBreakers.put(name, new CircuitBreaker(name, timeout, exceptionThreshold));
    }

    public static void setDefaultCircuitBreakerSettings( int timeoutMillis, int exceptionThreshold) {
        timeout = timeoutMillis;
        DejaVuAspect.exceptionThreshold = exceptionThreshold;
    }

    @Around("execution(@com.jayway.dejavu.core.annotation.Traced * *(..))")
    public Object traced( ProceedingJoinPoint proceed ) throws Throwable {
        if ( traceId.get() == null ) {
            Trace trace = trace( proceed );

            traceId.set( trace.getId() );
            threadId.set( trace.getId() );
            runningTraces.put(trace.getId(), new RunningTrace(trace));

            threadLocalInIntegrationPoint.set( false );
            try {
                Object result = proceed.proceed();
                ifFinished(trace, null);
                return result;
            } catch ( Throwable t) {
                runningTraces.get( trace.getId() ).setThrowable( t );
                ifFinished(trace, t);
                throw t;
            }
        } else {
            // setup already done just proceed
            return proceed.proceed();
        }
    }

    private static Trace trace( ProceedingJoinPoint proceed ) {
        if ( traceMode ) {
            MethodSignature signature = (MethodSignature) proceed.getSignature();
            return new Trace(UUID.randomUUID().toString(), signature.getMethod(), proceed.getArgs() );
        } else {
            return DejaVuTrace.getTrace();
        }
    }

    @Around("execution(@com.jayway.dejavu.core.annotation.Impure * *(..)) && @annotation(impure)")
    public Object integrationPoint( ProceedingJoinPoint proceed, Impure impure) throws Throwable {
        if ( traceMode && traceId.get() == null ) {
            // we are outside of a trace so call the method normally
            return proceed.proceed();
        }
        Trace trace = runningTraces.get( traceId.get() ).getTrace();
        Boolean inIntegratonPoint = threadLocalInIntegrationPoint.get();
        if ( traceMode && inIntegratonPoint ) {
            // we are in a trace and in an integration point
            // called from another integration point, so don't
            // trace this call
            return proceed.proceed();
        }

        if ( traceMode ) {
            synchronized (trace) {
                CircuitBreaker handler = null;
                Object result = null;
                try {
                    threadLocalInIntegrationPoint.set( true );
                    String integrationPoint = impure.integrationPoint();
                    if ( !integrationPoint.isEmpty() ) {
                        // a circuit breaker is guarding this call
                        handler = getCircuitBreaker( integrationPoint );
                        if ( handler.isOpen() ) {
                            throw new CircuitOpenException( "Circuit breaker '"+integrationPoint+"' is open");
                        }
                    }
                    result = proceed.proceed();
                    return result;
                } catch (Throwable t ) {
                    result = new ThrownThrowable( t );
                    throw t;
                } finally {
                    add( trace, result );
                    if ( handler != null ) {
                        if ( result instanceof ThrownThrowable ) {
                            handler.exceptionOccurred(((ThrownThrowable) result).getThrowable());
                        } else {
                            handler.success();
                        }
                    }
                    threadLocalInIntegrationPoint.set( false );
                }
            }
        } else {
            return DejaVuTrace.nextValue( threadId.get() );
        }
    }

    private CircuitBreaker getCircuitBreaker( String integrationPoint ) {
        if (!circuitBreakers.containsKey(integrationPoint)) {
            circuitBreakers.put( integrationPoint, new CircuitBreaker(integrationPoint, timeout, exceptionThreshold));
        }
        return circuitBreakers.get( integrationPoint );
    }

    @Before("execution(@com.jayway.dejavu.core.annotation.AttachThread * *(..))")
    public void attach( JoinPoint proceed ) {
        Runnable runnable = findRunnable(proceed.getArgs());
        if ( runnable == null || threadId.get() == null ) {
            return;
        }
        String childThreadId;
        if ( traceMode ) {
            // generate id for new thread that will begin this runnable
            childThreadId = threadId.get() + "." + UUID.randomUUID().toString();
        } else {
            // when in deja vu mode threadId is already set
            childThreadId = DejaVuTrace.nextChildThreadId( threadId.get() );
        }
        threadIdMap.put( runnable, childThreadId);
        runningTraces.get( traceId.get() ).threadAttached( childThreadId );
    }

    @Around("execution(* java.lang.Runnable.run(..))")
    public Object run( ProceedingJoinPoint proceed ) throws Throwable {
        threadLocalInIntegrationPoint.set( false );
        String threadId = threadIdMap.get(proceed.getThis());
        if ( threadId != null ) {
            DejaVuAspect.threadId.set( threadId );
            String[] split = threadId.split("\\.");
            traceId.set( split[0] );
            threadIdMap.remove( proceed.getThis() );
        }
        try {
            return proceed.proceed();
        } finally {
            // clean up
            RunningTrace runningTrace = runningTraces.get(traceId.get());
            runningTrace.threadCompleted(threadId);
            ifFinished(runningTrace.getTrace(), runningTrace.getThrowable());
        }
    }

    private Runnable findRunnable(Object[] args) {
        for (Object arg : args) {
            if ( arg instanceof Runnable ) {
                return (Runnable) arg;
            }
        }
        return null;
    }

    private void add( Trace trace, Object value ) {
        TraceElement element = new TraceElement(threadId.get(), value);
        trace.addValue(element);
    }

    private void ifFinished(Trace trace, Throwable t) {
        RunningTrace runningTrace = runningTraces.get(trace.getId());
        if ( runningTrace.completed() ) {
            callback.traced(trace, t);
            runningTraces.remove( trace.getId() );
        }
        traceId.remove();
        threadId.remove();
        threadLocalInIntegrationPoint.remove();
    }
}
