package com.jayway.dejavu.core;

import com.jayway.dejavu.core.chainer.ChainBuilder;
import com.jayway.dejavu.core.repository.Tracer;

import java.util.*;
import java.util.concurrent.Callable;

public class RunningTrace {

    private static ThreadLocal<String> threadId = new ThreadLocal<String>();
    private static ThreadLocal<Boolean> threadLocalIgnore = new ThreadLocal<Boolean>();
    private static ThreadLocal<Boolean> threadLocalInImpure = new ThreadLocal<Boolean>();

    private static List<ImpureHandler> impureHandlers = new ArrayList<ImpureHandler>();
    private static List<TraceValueHandler> traceValueHandlers = new ArrayList<TraceValueHandler>();

    private DejaVuPolicy policy;
    private ImpureHandler impureHandler;
    private Set<String> attachedThreads;
    private Set<String> completedThreads;
    private Throwable throwable;
    private List<ThreadThrowable> threadThrowables;
    private Tracer tracer;

    protected RunningTrace( DejaVuPolicy policy, Tracer tracer ) {
        this.policy = policy;
        this.tracer = tracer;
        tracer.setTraceValueHandlerChain(ChainBuilder.compose(TraceValueHandler.class).add(traceValueHandlers).build());
        threadId.set(tracer.getTrace().getId());

        impureHandler = ChainBuilder.all(ImpureHandler.class).add(new ImpureHandler() {
            public void before(RunningTrace runningTrace, String integrationPoint) {
                threadLocalInImpure.set( true );
            }
            public void after(RunningTrace runningTrace, Object result) {
                threadLocalInImpure.set( false );
            }
        }).add( impureHandlers).build();
    }

    public static void initialize() {
        impureHandlers.clear();
        traceValueHandlers.clear();
    }

    public static void addImpureHandler( ImpureHandler handler ) {
        impureHandlers.add(handler);
    }

    public static void addTraceHandler( TraceValueHandler handler ) {
        traceValueHandlers.add(handler);
    }

    public synchronized void threadAttached( String id ) {
        if ( attachedThreads == null ) {
            attachedThreads = new HashSet<String>();
            completedThreads = new HashSet<String>();
        }
        attachedThreads.add(id);
    }

    public void callbackIfFinished() {
        callbackIfFinished(null);
    }

    public void callbackIfFinished(Throwable t) {
        throwable = t;
        if ( attachedThreadsCompleted() ) {
            policy.completed(tracer.getTrace(), t, threadThrowables);
        }
        threadId.remove();
        threadLocalInImpure.remove();
    }

    public void threadStarted( String threadId ) {
        RunningTrace.threadId.set(threadId);
        policy.setPolicyForCurrentThread();
    }

    public synchronized void threadCompleted() {
        completedThreads.add(threadId.get());
        policy.removePolicyForCurrentThread();
        callbackIfFinished(throwable);
    }

    public synchronized void threadThrowable(Throwable throwable) {
        if ( threadThrowables == null ) {
            threadThrowables = new ArrayList<ThreadThrowable>();
        }
        threadThrowables.add(new ThreadThrowable(threadId.get(), throwable));
    }

    protected synchronized boolean attachedThreadsCompleted() {
        return attachedThreads == null || attachedThreads.size() == completedThreads.size();
    }

    protected Object aroundImpure(DejaVuInterception interception, String integrationPoint ) throws Throwable {
        try {
            impureHandler.before( this, integrationPoint );
            Object result = tracer.nextValue(threadId.get(), interception);
            impureHandler.after( this, result);
            return result;
        } catch (Throwable throwable) {
            impureHandler.after(this, throwable);
            throw throwable;
        }
    }

    protected boolean shouldProceed() {
        // we are in an impure call calling another impure call
        // so this call should proceed
        return inImpure() || ignore();
    }

    // if runnable is passed as argument to @AttachThread and not
    // run, the trace will not stop
    public Runnable toBeAttached(Runnable runnable){
        String childThreadId = tracer.getNextChildThreadId(threadId.get());
        threadAttached(childThreadId);
        return new AttachedRunnable(runnable, this, childThreadId);
    }

    public Callable toBeAttached(Callable callable ) {
        String childThreadId = tracer.getNextChildThreadId(threadId.get());
        threadAttached(childThreadId);
        return new AttachedCallable(callable, this, childThreadId);
    }

    private boolean ignore() {
        return threadLocalIgnore.get() != null && threadLocalIgnore.get();
    }

    private boolean inImpure() {
        return threadLocalInImpure.get() == null ? false : threadLocalInImpure.get();
    }

    public static String generateId() {
        threadLocalIgnore.set( true );
        String id = UUID.randomUUID().toString();
        threadLocalIgnore.set( false );
        return id;
    }
}
