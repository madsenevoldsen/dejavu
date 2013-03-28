package com.jayway.dejavu.core;

public class TraceElement {
    private String threadId;
    private Object value;
    private Class type;

    public TraceElement( String threadId, Object value ) {
        this.threadId = threadId;
        this.value = value;
    }
    public TraceElement( String threadId, Object value, Class<?> type ) {
        this.threadId = threadId;
        this.value = value;
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public String getThreadId() {
        return threadId;
    }

    public Class getType() {
        return type;
    }
}
