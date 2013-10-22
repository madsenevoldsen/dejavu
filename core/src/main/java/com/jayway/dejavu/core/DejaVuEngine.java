package com.jayway.dejavu.core;

import com.jayway.dejavu.core.chainer.ChainBuilder;
import com.jayway.dejavu.core.interfaces.*;
import com.jayway.dejavu.core.MemoryTraceBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public abstract class DejaVuEngine {

    private RunningTrace runningTrace;

    private static ThreadLocal<DejaVuEngine> dejaVuEngineThreadLocal = new ThreadLocal<DejaVuEngine>();
    private static List<ImpureHandler> impureHandlers = new ArrayList<ImpureHandler>();

    public static void clearImpureHandlers() {
        impureHandlers.clear();
    }

    public static void addImpureHandler( ImpureHandler handler ) {
        impureHandlers.add(handler);
    }

    public static ImpureHandler createImpureHandlerChain( ImpureHandler defaultHandler ) {
        return ChainBuilder.all(ImpureHandler.class).add(defaultHandler).add( impureHandlers ).build();
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

    private static EngineFactory engineFactory = new EngineFactory() {
        @Override
        public DejaVuEngine getEngine() {
            throw new RuntimeException("EngineFactory not set up! Call DejaVuEngine.setEngineFactory(..)");
        }
    };

    public static void setEngineFactory(EngineFactory engineFactory) {
        DejaVuEngine.engineFactory = engineFactory;
    }

    private static TraceBuilderFactory traceBuilderFactory = new TraceBuilderFactory() {
        @Override
        public TraceBuilder createTraceBuilder(String traceId, TraceValueHandler... handlers) {
            return new MemoryTraceBuilder(traceId, handlers);
        }
    };

    public static void setTraceBuilderFactory( TraceBuilderFactory traceBuilderFactory) {
        DejaVuEngine.traceBuilderFactory = traceBuilderFactory;
    }

    public static TraceBuilder createTraceBuilder() {
        return traceBuilderFactory.createTraceBuilder(generateId());
    }

    public static TraceBuilder createTraceBuilder(String traceId, TraceValueHandler... handlers ) {
        return traceBuilderFactory.createTraceBuilder(traceId, handlers);
    }

    public static Object traced( DejaVuInterception interception) throws Throwable {
        DejaVuEngine engine = getEngineForCurrentThread();
        if ( engine == null ) {
            engine = engineFactory.getEngine();
            engine.setEngineForCurrentThread();
        }
        return engine.aroundTraced(interception);
    }

    public static Object impure(DejaVuInterception interception, String integrationPoint) throws Throwable {
        DejaVuEngine engine = getEngineForCurrentThread();
        if (engine == null || engine.runningTrace == null) {
            return interception.proceed();
        } else {
            return engine.aroundImpure(interception, integrationPoint);
        }
    }

    public static Object attach(DejaVuInterception interception) throws Throwable {
        DejaVuEngine engine = getEngineForCurrentThread();
        if ( engine != null ) {
            engine.attachThread(interception);
            return null;
        } else {
            return interception.proceed();
        }
    }

    public abstract Tracer createTracer( DejaVuInterception interception );

    public TraceCallback callback() {
        return callback;
    }

    public Object aroundTraced( DejaVuInterception interception ) throws Throwable {
        if ( runningTrace != null) {
            // this must be a traced call calling a second traced method, just proceed
            return interception.proceed();
        }

        RunningTrace trace = new RunningTrace(this, createTracer(interception));
        runningTrace = trace;
        try {
            Object result = interception.proceed();
            trace.callbackIfFinished();
            return result;
        } catch ( Throwable t) {
            trace.callbackIfFinished(t);
            throw t;
        }
    }

    public Object aroundImpure(DejaVuInterception interception, String integrationPoint) throws Throwable {
        if ( runningTrace.shouldProceed() ) {
            return interception.proceed();
        }

        return runningTrace.aroundImpure(interception, integrationPoint);
    }

    public void attachThread( DejaVuInterception interception) throws Throwable {
        if ( runningTrace == null ) {
            interception.proceed();
        } else {
            Object[] args = interception.getArguments();
            patch(runningTrace, args);
            interception.proceed(args);
        }
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
        TraceCallback cb = callback();
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
