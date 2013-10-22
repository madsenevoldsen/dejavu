package com.jayway.dejavu.core;

import com.jayway.dejavu.core.interfaces.Trace;
import com.jayway.dejavu.core.interfaces.TraceValueHandler;

import java.util.ArrayList;
import java.util.List;

class MemoryTraceBuilder extends TraceBuilder {

    private List<TraceElement> values;

    public MemoryTraceBuilder(String traceId, TraceValueHandler... handlers ) {
        super(traceId, handlers);
    }

    @Override
    protected void addElement(TraceElement element) {
        if ( values == null ) {
            values = new ArrayList<TraceElement>();
        }
        values.add( element );
    }

    @Override
    public Trace build() {
        return new MemoryTrace(getTraceId(), values, getStartMethod(), getStartArguments() );
    }
}
