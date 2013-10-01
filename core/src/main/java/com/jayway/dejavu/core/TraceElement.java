package com.jayway.dejavu.core;

public class TraceElement {
    private String threadId;
    private Object value;

    public TraceElement( String threadId, Object value ) {
        this.threadId = threadId;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public String getThreadId() {
        return threadId;
    }
}
