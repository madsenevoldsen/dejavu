package com.jayway.dejavu.core;

class AttachedRunnable implements Runnable {

    private final Runnable runnable;
    private final RunningTrace trace;
    private final String threadId;

    public AttachedRunnable( Runnable runnable, RunningTrace trace, String threadId ) {
        this.runnable = runnable;
        this.trace = trace;
        this.threadId = threadId;
    }

    @Override
    public void run() {
        trace.threadStarted(threadId);
        try {
            runnable.run();
        } catch( Throwable throwable) {
            trace.threadThrowable( throwable );
        } finally {
            trace.threadCompleted();
        }
    }
}
