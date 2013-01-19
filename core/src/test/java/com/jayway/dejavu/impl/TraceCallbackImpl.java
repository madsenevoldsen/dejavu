package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.Trace;
import com.jayway.dejavu.core.repository.TraceCallback;

public class TraceCallbackImpl implements TraceCallback {

    private Trace trace;
    private Throwable cause;

    @Override
    public void traced(Trace trace, Throwable cause) {
        this.trace = trace;
        this.cause = cause;
    }

    public Throwable getCause() {
        return cause;
    }

    public Trace getTrace() {
        return trace;
    }
}
