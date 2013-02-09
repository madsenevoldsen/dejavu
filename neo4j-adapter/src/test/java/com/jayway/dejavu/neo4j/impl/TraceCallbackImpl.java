package com.jayway.dejavu.neo4j.impl;

import com.jayway.dejavu.core.Trace;
import com.jayway.dejavu.core.repository.TraceCallback;

public class TraceCallbackImpl implements TraceCallback {


    private Trace trace;

    @Override
    public void traced(Trace trace, Throwable cause) {
        this.trace = trace;
    }

    public Trace getTrace() {
        return trace;
    }
}
