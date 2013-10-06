package com.jayway.dejavu.core;

import com.jayway.dejavu.core.exception.TraceEndedException;
import com.jayway.dejavu.core.repository.RecordingTracer;
import com.jayway.dejavu.core.repository.ReplayTracer;
import com.jayway.dejavu.core.repository.TraceCallback;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class DejaVuPolicy {

    protected static ThreadLocal<RunningTrace> runningTrace = new ThreadLocal<RunningTrace>();
    private static TraceCallback callback;

    public static void initialize( TraceCallback callback ) {
        DejaVuPolicy.callback = callback;
    }

    public static <T> T replay( Trace trace ) throws Throwable {
        RunningTrace running = new RunningTrace( new ReplayTracer(trace), callback);
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
            trace = new RunningTrace(new RecordingTracer(new Trace(interception.getMethod(), interception.getArguments()) ),callback);
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

        return runningTrace.get().aroundImpure( interception, integrationPoint);
    }

    private boolean justProceed() throws Throwable {
        if ( runningTrace.get() == null ) {
            // we are outside of a trace so call the method normally
            return true;
        }
        return runningTrace.get().shouldProceed();
    }

    public void attachThread( DejaVuInterception interception) throws Throwable {
        RunningTrace trace = runningTrace.get();
        if ( trace == null ) {
            interception.proceed();
        } else {
            Object[] args = interception.getArguments();
            patch(trace, args);
            interception.proceed(args);
        }
    }

    public static void patchForAttachThread(Object[] args) {
        RunningTrace trace = runningTrace.get();
        if ( trace != null ) {
            patch(trace, args);
        }
    }

    private static void patch(RunningTrace trace, Object[] args){
        for (int i=0; i<args.length; i++) {
            Object arg = args[i];
            if ( arg instanceof Runnable ) {
                args[i] = trace.toBeAttached((Runnable) arg);
            } else if ( arg instanceof Callable) {
                args[i] = trace.toBeAttached((Callable) arg);
            }
        }
    }
}
