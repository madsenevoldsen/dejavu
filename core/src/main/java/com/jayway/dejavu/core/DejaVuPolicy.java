package com.jayway.dejavu.core;

import com.jayway.dejavu.core.chainer.ChainBuilder;
import com.jayway.dejavu.core.exception.TraceEndedException;
import com.jayway.dejavu.core.repository.TraceCallback;
import com.jayway.dejavu.core.typeinference.DefaultInference;
import com.jayway.dejavu.core.typeinference.ExceptionInference;
import com.jayway.dejavu.core.typeinference.TypeInference;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class DejaVuPolicy {

    protected static ThreadLocal<RunningTrace> runningTrace = new ThreadLocal<RunningTrace>();
    private static TraceCallback callback;


    private static List<TypeInference> typeHelpers = new ArrayList<TypeInference>();
    private static TypeInference typeInference;

    public static void initialize( TraceCallback cb ) {
        callback = cb;
        ChainBuilder<TypeInference> builder = ChainBuilder.chain(TypeInference.class).add(new ExceptionInference());
        for (TypeInference typeHelper : typeHelpers) {
            builder.add( typeHelper );
        }
        typeInference = builder.add( new DefaultInference() ).build();
    }


    private static BeforeRunCallback beforeRunCallback;

    public static void setBeforeRunCallback( BeforeRunCallback beforeRunCallback ) {
        DejaVuPolicy.beforeRunCallback = beforeRunCallback;
    }

    public interface BeforeRunCallback {
        void beforeRun( Trace trace );
    }

    public static <T> T replay( Trace trace ) throws Throwable {
        if ( beforeRunCallback != null ) {
            beforeRunCallback.beforeRun( trace );
        }
        RunningTrace running = new RunningTrace(trace, false);
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
            while (!running.completed()) {
                // wait until finished
                Thread.sleep(500);
            }
        }
    }

    protected static void addTypeHelper( TypeInference typeInference ) {
        typeHelpers.add( typeInference );
    }

    public static void destroy() {
        callback = null;
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
            trace = new RunningTrace(new Trace(interception.getMethod(), interception.getArguments() ), true);
            runningTrace.set( trace );
        }

        try {
            Object result = interception.proceed();
            trace.callbackIfFinished(DejaVuPolicy.callback, trace.getTrace(), null);
            return result;
        } catch ( Throwable t) {
            trace.setThrowable( t );
            trace.callbackIfFinished(DejaVuPolicy.callback, trace.getTrace(), t);
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
            running.success( running, result, typeInference.inferType(result, interception));
            return result;
        } catch (Throwable throwable) {
            running.failure(running, throwable);
            throw throwable;
        }
    }

    protected boolean justProceed() throws Throwable {
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

    public static void callback(Trace trace, Throwable t) {
        callback.traced(trace, t);
    }


    public boolean isRecording() {
        RunningTrace trace = runningTrace.get();
        if ( trace == null ) return false;
        return trace.isRecording();
    }

    public void setIgnore( boolean ignore ) {
        RunningTrace trace = runningTrace.get();
        if ( trace != null ) {
            trace.setIgnore( ignore );
        }
    }

    public void patch(Object[] args) {
        RunningTrace trace = runningTrace.get();
        if ( trace != null ) {
            trace.patch(args);
        }
    }
}
