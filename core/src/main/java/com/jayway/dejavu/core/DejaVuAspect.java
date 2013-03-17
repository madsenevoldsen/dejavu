package com.jayway.dejavu.core;

import com.jayway.dejavu.core.annotation.Impure;
import com.jayway.dejavu.core.exception.CircuitOpenException;
import com.jayway.dejavu.core.repository.TraceCallback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InterfaceMaker;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Aspect
public class DejaVuAspect {

    private static TraceCallback callback;
    private static boolean traceMode = true;
    private static int timeout = 10 * 60 * 1000; // ten minutes
    private static int exceptionThreshold = 10;

    private static ThreadLocal<String> traceId = new ThreadLocal<String>();
    private static ThreadLocal<String> threadId = new ThreadLocal<String>();
    private static ThreadLocal<Boolean> threadLocalInImpure = new ThreadLocal<Boolean>();
    private static ThreadLocal<Boolean> threadLocalIgnore = new ThreadLocal<Boolean>();

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

            threadLocalInImpure.set(false);
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
        if ( fallThrough() ) {
            return proceed.proceed();
        }
        return handle(proceed, null, impure.integrationPoint() );
    }

    public static Object handle(ProceedingJoinPoint proceed, ProxyMethod proxyMethod, String integrationPoint) throws Throwable {
        Trace trace = runningTraces.get( traceId.get() ).getTrace();
        if ( traceMode ) {
            CircuitBreaker handler = null;
            Object result = null;
            try {
                threadLocalInImpure.set(true);
                if ( !integrationPoint.isEmpty() ) {
                    // a circuit breaker is guarding this call
                    handler = getCircuitBreaker( integrationPoint );
                    if ( handler.isOpen() ) {
                        throw new CircuitOpenException( "Circuit breaker '"+integrationPoint+"' is open");
                    }
                }
                result = proceed == null ? proxyMethod.invoke() : proceed.proceed();
                return result;
            } catch (Throwable t ) {
                result = new ThrownThrowable( t );
                throw t;
            } finally {
                add(trace, result);
                if ( handler != null ) {
                    if ( result instanceof ThrownThrowable ) {
                        handler.exceptionOccurred(((ThrownThrowable) result).getThrowable());
                    } else {
                        handler.success();
                    }
                }
                threadLocalInImpure.set(false);
            }
        } else {
            return DejaVuTrace.nextValue(threadId.get());
        }
    }

    public static boolean fallThrough() throws Throwable {
        if ( traceMode && traceId.get() == null ) {
            // we are outside of a trace so call the method normally
            return true;
        }
        boolean inIntegratonPoint = threadLocalInImpure.get() == null ? false : threadLocalInImpure.get();
        if ( traceMode && inIntegratonPoint ) {
            // we are in a trace and in an integration point
            // called from another integration point, so don't
            // trace this call
            return true;
        }
        if ( threadLocalIgnore.get() != null && threadLocalIgnore.get() ) {
            return true;
        }
        return false;
    }

    private static CircuitBreaker getCircuitBreaker( String integrationPoint ) {
        if (!circuitBreakers.containsKey(integrationPoint)) {
            circuitBreakers.put( integrationPoint, new CircuitBreaker(integrationPoint, timeout, exceptionThreshold));
        }
        return circuitBreakers.get( integrationPoint );
    }

    @Around("call(java.util.Random.new(..))")
    public Object random(ProceedingJoinPoint proceed ) throws Throwable {
        return impureProxy( Random.class, proceed );
    }

    @Around("call(java.io.FileReader.new(..))")
    public Object fileReader(ProceedingJoinPoint proceed ) throws Throwable {
        // if already inside an @impure just proceed
        if ( fallThrough() ) {
            return proceed.proceed();
        }
        threadLocalIgnore.set(true);
        String fileName = (String) proceed.getArgs()[0];
        if ( !traceMode ) {
            // read file known to exist it will never
            // be read because this is test mode
            fileName = this.getClass().getResource( "DejaVuAspect.class" ).getPath();
        }
        Enhancer enhancer = new Enhancer();
        enhancer.setCallback( new AllImpureProxy());
        enhancer.setSuperclass(FileReader.class);
        // if test mode it can only be a mock
        Object proxy = enhancer.create( new Class[]{String.class}, new Object[]{ fileName });
        threadLocalIgnore.set(false);
        return proxy;
    }

    @Around("call(java.io.BufferedReader.new(..))")
    public Object buffered(ProceedingJoinPoint proceed ) throws Throwable {
        // if already inside an @impure just proceed
        if ( fallThrough() ) {
            return proceed.proceed();
        }
        threadLocalIgnore.set(true);
        Enhancer enhancer = new Enhancer();
        enhancer.setCallback( new AllImpureProxy());
        enhancer.setSuperclass(BufferedReader.class);
        Object proxy = enhancer.create( new Class[]{Reader.class}, new Object[]{proceed.getArgs()[0]});
        threadLocalIgnore.set(false);
        return proxy;
    }

    private Object impureProxy( Class<?> clazz, ProceedingJoinPoint proceed ) throws Throwable {
        // if already inside an @impure just proceed
        if ( fallThrough() ) {
            return proceed.proceed();
        }
        threadLocalIgnore.set(true);
        Object proxy = Enhancer.create(clazz, new AllImpureProxy());
        threadLocalIgnore.set(false);
        return proxy;
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

    private static void add( Trace trace, Object value ) {
        synchronized (trace) {
            TraceElement element = new TraceElement(threadId.get(), value);
            trace.addValue(element);
        }
    }

    private static void callbackIfFinished(Trace trace, Throwable t) {
        RunningTrace runningTrace = runningTraces.get(trace.getId());
        if ( runningTrace.completed() ) {
            if ( traceMode ) callback.traced(trace, t);
            runningTraces.remove( trace.getId() );
        }
        traceId.remove();
        threadId.remove();
        threadLocalInImpure.remove();
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
