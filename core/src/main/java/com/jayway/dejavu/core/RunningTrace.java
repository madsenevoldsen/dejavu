package com.jayway.dejavu.core;

import com.jayway.dejavu.core.interfaces.DejaVuInterception;
import com.jayway.dejavu.core.interfaces.ImpureHandler;
import com.jayway.dejavu.core.interfaces.Tracer;

import java.util.*;
import java.util.concurrent.Callable;

class RunningTrace {

    private static ThreadLocal<String> threadId = new ThreadLocal<String>();
    private static ThreadLocal<Boolean> threadLocalIgnore = new ThreadLocal<Boolean>();
    private static ThreadLocal<Boolean> threadLocalInImpure = new ThreadLocal<Boolean>();

    private DejaVuEngine policy;
    private ImpureHandler impureHandler;
    private Set<String> attachedThreads;
    private Set<String> completedThreads;
    private Throwable throwable;
    private List<ThreadThrowable> threadThrowables;
    private Tracer tracer;

    protected RunningTrace( DejaVuEngine policy, Tracer tracer ) {
        this.policy = policy;
        this.tracer = tracer;
        threadId.set(tracer.getTrace().getId());

        impureHandler = DejaVuEngine.createImpureHandlerChain(new ImpureHandler() {
            public void before(String integrationPoint) {
                threadLocalInImpure.set(true);
            }

            public void after(Object result) {
                threadLocalInImpure.set(false);
            }
        });
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
        policy.setEngineForCurrentThread();
    }

    public synchronized void threadCompleted() {
        completedThreads.add(threadId.get());
        policy.removeEngineForCurrentThread();
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
            impureHandler.before( integrationPoint );
            Object result = tracer.nextValue(threadId.get(), interception);
            impureHandler.after( result);
            return result;
        } catch (Throwable throwable) {
            impureHandler.after(throwable);
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

    protected static String generateId() {
        threadLocalIgnore.set( true );
        String id = UUID.randomUUID().toString();
        threadLocalIgnore.set( false );
        return id;
    }
}
