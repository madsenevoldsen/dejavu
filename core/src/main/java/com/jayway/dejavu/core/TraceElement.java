package com.jayway.dejavu.core;

public class TraceElement {
    private String threadId;
    private Object value;

    public TraceElement() {}
    public TraceElement( String threadId, Object value ) {
        this.threadId = threadId;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }
}
