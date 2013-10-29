package com.jayway.dejavu.core;

import com.jayway.dejavu.core.chainer.ChainBuilder;
import com.jayway.dejavu.core.interfaces.AroundImpure;
import com.jayway.dejavu.core.interfaces.Interception;
import com.jayway.dejavu.core.interfaces.InterceptionAdapter;
import com.jayway.dejavu.core.interfaces.TraceValueHandler;

import java.util.*;
import java.util.concurrent.Callable;

class RunningTrace {

    private static ThreadLocal<String> threadId = new ThreadLocal<String>();
    private static ThreadLocal<Boolean> threadLocalIgnore = new ThreadLocal<Boolean>();
    private static ThreadLocal<Boolean> threadLocalInImpure = new ThreadLocal<Boolean>();

    private DejaVuEngine policy;
    private Set<String> attachedThreads;
    private Set<String> completedThreads;
    private Throwable throwable;
    private List<ThreadThrowable> threadThrowables;
    private TraceValueHandler valueHandler;
    private List<AroundImpure> impure;
    private TraceBuilder builder;

    protected RunningTrace( TraceBuilder builder, DejaVuEngine policy, List<AroundImpure> impure, List<TraceValueHandler> valueHandlers ) {
        this.builder = builder;
        this.policy = policy;
        this.impure = impure;
        threadId.set(builder.getTraceId());
        valueHandler = ChainBuilder.compose( TraceValueHandler.class ).add( valueHandlers ).build();
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
            policy.completed(builder.build(), t, threadThrowables);
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

    protected Object aroundImpure(Interception interception ) throws Throwable {
        LinkedList<AroundImpure> handlers = new LinkedList<AroundImpure>( impure );
        interception.threadId( threadId.get() );
        handlers.addFirst(new AroundImpure() {
            public Object proceed(Interception interception) throws Throwable {
                threadLocalInImpure.set(true);
                Object result = interception.proceed();
                threadLocalInImpure.set(false);
                return result;
            }
        });
        Object result = buildAroundHandler( interception, handlers ).proceed( interception );
        builder.addValue( threadId.get(), valueHandler.handle( result ) );
        if ( result instanceof ThrownThrowable ) {
            throw ((ThrownThrowable) result).getThrowable();
        }
        return result;
    }

    private AroundImpure buildAroundHandler( final Interception inner, final LinkedList<AroundImpure> handlers ) {
        return new AroundImpure() {

            @Override
            public Object proceed(Interception interception) throws Throwable {
                if ( handlers.isEmpty() ) {
                    try {
                        return inner.proceed();
                    } catch (Throwable throwable) {
                        return new ThrownThrowable( throwable);
                    }
                }

                try {
                    return handlers.removeFirst().proceed(new InterceptionAdapter(inner) {
                        @Override
                        public Object proceed() throws Throwable {
                            return buildAroundHandler(inner, handlers).proceed(inner);
                        }
                    });
                } catch (Throwable throwable) {
                    return new ThrownThrowable(throwable);
                }
            }
        };
    }

    protected boolean shouldProceed() {
        // we are in an impure call calling another impure call
        // so this call should proceed
        return inImpure() || ignore();
    }

    // if runnable is passed as argument to @AttachThread and not
    // run, the trace will not stop
    public Runnable toBeAttached(Runnable runnable){
        String childThreadId = builder.getNextChildThreadId(threadId.get());
        threadAttached(childThreadId);
        return new AttachedRunnable(runnable, this, childThreadId);
    }

    public Callable toBeAttached(Callable callable ) {
        String childThreadId = builder.getNextChildThreadId(threadId.get());
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
