package com.jayway.dejavu.core;

import java.util.concurrent.Callable;

class AttachedCallable implements Callable {

    private final Callable callable;
    private final RunningTrace trace;
    private final String threadId;

    public AttachedCallable(Callable callable, RunningTrace trace, String threadId) {
        this.callable = callable;
        this.trace = trace;
        this.threadId = threadId;
    }

    @Override
    public Object call() throws Exception {
        trace.threadStarted( threadId );
        try {
            return callable.call();
        } catch( Exception exception ) {
            trace.threadThrowable( exception );
            throw exception;
        } finally {
            trace.threadCompleted();
        }
    }
}
