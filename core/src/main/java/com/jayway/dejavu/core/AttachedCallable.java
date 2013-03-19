package com.jayway.dejavu.core;

import java.util.concurrent.Callable;

class AttachedCallable implements Callable {

    private final Callable callable;
    private final String traceId;
    private final String threadId;

    public AttachedCallable(Callable callable, String traceId, String threadId) {
        this.callable = callable;
        this.traceId = traceId;
        this.threadId = threadId;
    }

    @Override
    public Object call() throws Exception {
        DejaVuAspect.threadStarted( threadId, traceId );
        try {
            return callable.call();
        } catch( Exception exception ) {
            DejaVuAspect.threadThrowable( exception );
            throw exception;
        } finally {
            DejaVuAspect.threadCompleted();
        }
    }
}
