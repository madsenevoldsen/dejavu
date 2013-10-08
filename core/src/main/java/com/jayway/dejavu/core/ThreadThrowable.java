package com.jayway.dejavu.core;

public class ThreadThrowable {
    private String threadId;
    private Throwable throwable;

    public ThreadThrowable(String threadId, Throwable throwable) {
        this.threadId = threadId;
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }
}
