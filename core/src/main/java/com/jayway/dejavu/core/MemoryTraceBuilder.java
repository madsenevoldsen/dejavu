package com.jayway.dejavu.core;

import com.jayway.dejavu.core.interfaces.Trace;

import java.util.ArrayList;
import java.util.List;

class MemoryTraceBuilder extends TraceBuilder {

    private final List<TraceElement> values;

    public MemoryTraceBuilder(String traceId ) {
        super(traceId);
        values = new ArrayList<TraceElement>();
    }

    @Override
    protected void addElement(TraceElement element) {
        synchronized (values) {
            values.add( element );
        }
    }

    @Override
    public Trace build() {
        return new MemoryTrace(getTraceId(), values, getStartMethod(), getStartArguments() );
    }
}
