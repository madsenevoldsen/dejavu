package com.jayway.dejavu.core;

import com.jayway.dejavu.core.annotation.Impure;
import com.jayway.dejavu.core.exception.CircuitOpenException;
import com.jayway.dejavu.core.repository.TraceCallback;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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

    private static Map<String, RunningTrace> runningTraces = new HashMap<String, RunningTrace>();
    private static Map<String, CircuitBreaker> circuitBreakers;

    public static void initialize( TraceCallback cb ) {
        callback = cb;
        circuitBreakers = new HashMap<String, CircuitBreaker>();
    }

    public static void destroy() {
        callback = null;
        circuitBreakers = null;
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
                callbackIfFinished(trace, null);
                return result;
            } catch ( Throwable t) {
                runningTraces.get( trace.getId() ).setThrowable( t );
                callbackIfFinished(trace, t);
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
        boolean inIntegratonPoint = threadLocalInIntegrationPoint.get() == null ? false : threadLocalInIntegrationPoint.get();
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
                    //MethodSignature signature = (MethodSignature) proceed.getSignature();
                    //if ( result instanceof ThrownThrowable || !signature.getReturnType().toString().equals( "void" ) ) {
                        add(trace, result);
                    //}
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
            //MethodSignature signature = (MethodSignature) proceed.getSignature();
            //if ( !signature.getReturnType().toString().equals("void") ) {
                return DejaVuTrace.nextValue( threadId.get() );
            //} else {
            //    return null;
            //}

        }
    }

    private CircuitBreaker getCircuitBreaker( String integrationPoint ) {
        if (!circuitBreakers.containsKey(integrationPoint)) {
            circuitBreakers.put( integrationPoint, new CircuitBreaker(integrationPoint, timeout, exceptionThreshold));
        }
        return circuitBreakers.get( integrationPoint );
    }

    @Around("execution(@com.jayway.dejavu.core.annotation.AttachThread * *(..))")
    public void attach( ProceedingJoinPoint proceed ) throws Throwable {
        Object[] args = proceed.getArgs();
        if ( threadId.get() != null ) {
            patchRunnables( args );

        }
        proceed.proceed( args );
    }

    private String getChildThreadId() {
        if ( traceMode ) {
            // generate id for new thread that will begin this runnable
            return threadId.get() + "." + UUID.randomUUID().toString();
        } else {
            // when in deja vu mode threadId is already set
            return DejaVuTrace.nextChildThreadId( threadId.get() );
        }
    }

    private void patchRunnables(Object[] args) {
        if ( args == null || args.length == 0 ) {
            return;
        }
        for (int i=0; i<args.length; i++) {
            Object arg = args[i];
            if ( arg instanceof Runnable ) {
                String childThreadId = getChildThreadId();
                args[i] = new AttachedRunnable((Runnable) arg, traceId.get(), childThreadId);
                // if runnable is passed as argument to @AttachThread and not
                // run, the trace will not stop
                runningTraces.get( traceId.get() ).threadAttached( childThreadId );
            }
        }
    }

    private void add( Trace trace, Object value ) {
        TraceElement element = new TraceElement(threadId.get(), value);
        trace.addValue(element);
    }

    private static void callbackIfFinished(Trace trace, Throwable t) {
        RunningTrace runningTrace = runningTraces.get(trace.getId());
        if ( runningTrace.completed() ) {
            if ( traceMode ) callback.traced(trace, t);
            runningTraces.remove( trace.getId() );
        }
        traceId.remove();
        threadId.remove();
        threadLocalInIntegrationPoint.remove();
    }

    public static void threadStarted( String threadId, String traceId ) {
        DejaVuAspect.threadId.set( threadId );
        DejaVuAspect.traceId.set( traceId );
    }

    public static void threadCompleted() {
        RunningTrace runningTrace = DejaVuAspect.runningTraces.get( traceId.get() );
        runningTrace.threadCompleted(threadId.get());
        callbackIfFinished(runningTrace.getTrace(), runningTrace.getThrowable());
    }

    public static void threadThrowable(Throwable throwable) {
        if ( traceMode ) {
            Trace trace = runningTraces.get(traceId.get()).getTrace();
            synchronized ( trace ) {
                trace.addThreadThrowable( new ThreadThrowable( threadId.get(), throwable ));
            }
        }
    }

    public static boolean traceCompleted( String traceId ) {
        return runningTraces.get( traceId ) == null;
    }
}
