package com.jayway.dejavu.core;

class AttachedRunnable implements Runnable {

    private final Runnable runnable;
    private final String traceId;
    private final String threadId;

    public AttachedRunnable( Runnable runnable, String traceId, String threadId ) {
        this.runnable = runnable;
        this.traceId = traceId;
        this.threadId = threadId;
    }

    @Override
    public void run() {
        DejaVuAspect.threadStarted( threadId, traceId );
        try {
            runnable.run();
        } finally {
            DejaVuAspect.threadCompleted();
        }
    }
}
