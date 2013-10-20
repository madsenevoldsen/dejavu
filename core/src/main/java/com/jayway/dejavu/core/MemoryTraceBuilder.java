package com.jayway.dejavu.core;

import java.util.ArrayList;
import java.util.List;

public class MemoryTraceBuilder extends TraceBuilder {

    private List<TraceElement> values;

    public MemoryTraceBuilder(String traceId) {
        super(traceId, new TraceValueHandler() {
            @Override
            public Object handle(Object value) {
                return value;
            }
        });
    }

    public MemoryTraceBuilder(TraceValueHandler... handlers) {
        super("traceId", handlers);
    }

    public MemoryTraceBuilder() {
        this("traceId");
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
