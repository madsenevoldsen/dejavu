package com.jayway.dejavu.core;

import com.jayway.dejavu.core.chainer.ChainBuilder;
import com.jayway.dejavu.core.interfaces.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Callable;

public class DejaVuEngine {

    private RunningTrace runningTrace;

    private static ThreadLocal<DejaVuEngine> dejaVuEngineThreadLocal = new ThreadLocal<DejaVuEngine>();
    private static Set<Class<? extends AroundImpure>> aroundClasses = new LinkedHashSet<Class<? extends AroundImpure>>();
    private static Set<Class<? extends TraceValueHandler>> valueHandlerClasses = new LinkedHashSet<Class<? extends TraceValueHandler>>();

    public static void setAroundClasses( Class<? extends AroundImpure>... classes ) {
        aroundClasses.clear();
        Collections.addAll(aroundClasses, classes);
    }

    public static void setValueHandlerClasses( Class<? extends TraceValueHandler>... classes ) {
        valueHandlerClasses.clear();
        Collections.addAll(valueHandlerClasses, classes);
    }

    protected void setEngineForCurrentThread() {
        dejaVuEngineThreadLocal.set(this);
    }

    public static DejaVuEngine getEngineForCurrentThread() {
        return dejaVuEngineThreadLocal.get();
    }

    protected void removeEngineForCurrentThread() {
        dejaVuEngineThreadLocal.remove();
    }

    private static TraceCallback callback;

    public static void initialize( TraceCallback callback ) {
        DejaVuEngine.callback = callback;
    }

    private static TraceBuilderFactory traceBuilderFactory = new TraceBuilderFactory() {
        @Override
        public TraceBuilder createTraceBuilder(String traceId) {
            return new MemoryTraceBuilder(traceId);
        }
    };

    private static <T> List<T> instantiate( Set<Class<? extends T>> classes ) {
        List<T> instances = new ArrayList<T>();
        for (Class<? extends T> aClass : classes) {
            try {
                instances.add( aClass.newInstance() );
            } catch (Exception e) {
                // log warning
            }
        }
        return instances;
    }

    public static void setTraceBuilderFactory( TraceBuilderFactory traceBuilderFactory) {
        DejaVuEngine.traceBuilderFactory = traceBuilderFactory;
    }

    public static TraceBuilder createTraceBuilder(String traceId) {
        return traceBuilderFactory.createTraceBuilder(traceId);
    }

    public static Object traced( Interception interception) throws Throwable {
        DejaVuEngine engine = getEngineForCurrentThread();
        if ( engine == null ) {
            engine = new DejaVuEngine();
            engine.setEngineForCurrentThread();
        }
        return engine.aroundTraced(interception);
    }

    public static Object impure(Interception interception) throws Throwable {
        DejaVuEngine engine = getEngineForCurrentThread();
        if (engine == null || engine.runningTrace == null) {
            return interception.proceed();
        } else {
            return engine.aroundImpure(interception);
        }
    }

    public static Object attach(Interception interception) throws Throwable {
        DejaVuEngine engine = getEngineForCurrentThread();
        if ( engine != null ) {
            engine.attachThread(interception);
            return null;
        } else {
            return interception.proceed();
        }
    }

    public <T> T replay( final Trace trace, final TraceValueHandler... handlers ) throws Throwable {
        final Iterator<TraceElement> iterator = trace.iterator();
        final List<AroundImpure> impure = new ArrayList<AroundImpure>();
        final TraceValueHandler valueHandler = ChainBuilder.compose( TraceValueHandler.class).add( handlers ).build();
        impure.add(new AroundImpure() {
            TraceElement current;

            public synchronized Object proceed(Interception interception) throws Throwable {
                while (true) {
                    if ( current == null ) {
                        current = iterator.next();
                    }
                    if ( interception.threadId().equals(current.getThreadId()) ) {
                        TraceElement element = current;
                        current = null;
                        notifyAll();
                        Object result = valueHandler.handle(element.getValue());
                        if ( result instanceof ThrownThrowable ) {
                            throw ((ThrownThrowable) result).getThrowable();
                        }
                        return result;
                    } else {
                        try {
                            // we need to wait for the value to be ready
                            wait();
                        } catch (InterruptedException e) {
                            // ignore. Continue waiting
                        }
                    }
                }
            }
        });
        DejaVuEngine replayEngine = new DejaVuEngine() {
            @Override
            protected RunningTrace createRunningTrace(Interception interception) {
                return new RunningTrace( new ReplayTraceBuilder(trace, handlers), this, impure, null );
            }
        };
        replayEngine.setEngineForCurrentThread();
        Method method = trace.getStartPoint();
        Class<?> aClass = method.getDeclaringClass();

        try {
            Object instance = aClass.newInstance();
            return (T) method.invoke(instance, trace.getStartArguments());
        } catch (InvocationTargetException ee ) {
            throw ee.getTargetException();
        } finally {
            removeEngineForCurrentThread();
        }
    }

    public Object aroundTraced( Interception interception ) throws Throwable {
        if ( runningTrace != null) {
            // this must be a traced call calling a second traced method, just proceed
            return interception.proceed();
        }

        runningTrace = createRunningTrace(interception);
        try {
            Object result = interception.proceed();
            runningTrace.callbackIfFinished();
            return result;
        } catch ( Throwable t) {
            runningTrace.callbackIfFinished(t);
            throw t;
        }
    }

    protected RunningTrace createRunningTrace(Interception interception) {
        TraceBuilder builder = createTraceBuilder(generateId());
        builder.startMethod( interception.getMethod() );
        builder.startArguments( interception.getArguments() );
        return new RunningTrace(builder, this, instantiate( aroundClasses),
                instantiate( valueHandlerClasses));
    }

    public Object aroundImpure(Interception interception) throws Throwable {
        if ( runningTrace.shouldProceed() ) {
            return interception.proceed();
        }

        return runningTrace.aroundImpure(interception);
    }

    public void attachThread( Interception interception) throws Throwable {
        if (runningTrace != null) {
            Object[] args = interception.getArguments();
            patch(runningTrace, args);
            interception.setArguments(args);
        }
        interception.proceed();
    }

    public void patchForAttachThread(Object[] args) {
        if ( runningTrace != null ) {
            patch(runningTrace, args);
        }
    }

    private void patch(RunningTrace trace, Object[] args){
        for (int i=0; i<args.length; i++) {
            Object arg = args[i];
            if ( arg instanceof Runnable ) {
                args[i] = trace.toBeAttached((Runnable) arg);
            } else if ( arg instanceof Callable) {
                args[i] = trace.toBeAttached((Callable) arg);
            }
        }
    }

    public void completed(Trace trace, Throwable t, List<ThreadThrowable> threadThrowables) {
        TraceCallback cb = callback;
        if ( cb != null  ) {
            cb.traced(trace, t, threadThrowables);
        }
        runningTrace = null;
        removeEngineForCurrentThread();
    }

    public static String generateId() {
        return RunningTrace.generateId();
    }
}
