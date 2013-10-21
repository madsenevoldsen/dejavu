package com.jayway.dejavu.neo4j.impl;

import com.jayway.dejavu.core.ThreadThrowable;
import com.jayway.dejavu.core.interfaces.Trace;
import com.jayway.dejavu.core.interfaces.TraceCallback;

import java.util.List;

public class TraceCallbackImpl implements TraceCallback {

    private Trace trace;

    @Override
    public void traced(Trace trace, Throwable cause, List<ThreadThrowable> threadThrowables) {
        this.trace = trace;
    }

    public Trace getTrace() {
        return trace;
    }
}
