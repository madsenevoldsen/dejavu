package com.jayway.dejavu.core;

import com.jayway.dejavu.core.exception.TraceEndedException;
import com.jayway.dejavu.core.repository.TraceCallback;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DejaVuPolicy {

    protected static ThreadLocal<RunningTrace> runningTrace = new ThreadLocal<RunningTrace>();
    private static TraceCallback callback;

    public static void initialize( TraceCallback callback ) {
        DejaVuPolicy.callback = callback;
    }

    public static <T> T replay( Trace trace ) throws Throwable {
        RunningTrace running = new RunningTrace(trace, callback, false);
        runningTrace.set(running);
        Method method = trace.getStartPoint();
        Class<?> aClass = method.getDeclaringClass();

        try {
            Object instance = aClass.newInstance();
            // wait until trace ended???
            return (T) method.invoke(instance, trace.getStartArguments());
        } catch (TraceEndedException e ) {
            // the trace has ended so
            // we can do nothing but return null
            return null;
        } catch (InvocationTargetException ee ) {
            throw ee.getTargetException();
        } finally {
            while (!running.attachedThreadsCompleted()) {
                // wait until finished
                Thread.sleep(500);
            }
        }
    }

    public Object aroundTraced( DejaVuInterception interception ) throws Throwable {
        RunningTrace trace = runningTrace.get();
        if ( trace != null && trace.isRecording()) {
            // just proceed, setup is done and we are tracing
            return interception.proceed();
        }

        if ( trace == null ) {
            // a @Traced method has been called and there is no
            // RunningTrace so create one
            trace = new RunningTrace(new Trace(interception.getMethod(), interception.getArguments() ),callback, true);
            runningTrace.set( trace );
        }

        try {
            Object result = interception.proceed();
            trace.callbackIfFinished(null);
            return result;
        } catch ( Throwable t) {
            trace.callbackIfFinished(t);
            throw t;
        } finally {
            runningTrace.remove();
        }
    }

    public Object aroundImpure(DejaVuInterception interception, String integrationPoint) throws Throwable {
        if ( justProceed() ) {
            return interception.proceed();
        }

        RunningTrace running = runningTrace.get();
        if (!running.isRecording()) {
            return running.nextValue();
        }
        try {
            running.before( running, integrationPoint );
            Object result = interception.proceed();
            running.success(running, result);
            return result;
        } catch (Throwable throwable) {
            running.failure(running, throwable);
            throw throwable;
        }
    }

    private boolean justProceed() throws Throwable {
        if ( runningTrace.get() == null ) {
            // we are outside of a trace so call the method normally
            return true;
        }
        RunningTrace running = runningTrace.get();
        if ( running.inImpure() ) {
            // we are in a trace and in an impure call
            // called from another impure call, so don't
            // trace this call
            return true;
        }
        return running.ignore();
    }

    public void attachThread( DejaVuInterception interception) throws Throwable {
        RunningTrace trace = runningTrace.get();
        if ( trace == null ) {
            interception.proceed();
        } else {
            Object[] args = interception.getArguments();
            trace.patch(args);
            interception.proceed(args);
        }
    }

    public static void patchForAttachThread(Object[] args) {
        RunningTrace trace = runningTrace.get();
        if ( trace != null ) {
            trace.patch(args);
        }
    }
}
