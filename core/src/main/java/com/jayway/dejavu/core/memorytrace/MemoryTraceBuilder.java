package com.jayway.dejavu.core.memorytrace;

import com.jayway.dejavu.core.TraceBuilder;
import com.jayway.dejavu.core.TraceElement;
import com.jayway.dejavu.core.interfaces.Trace;
import com.jayway.dejavu.core.interfaces.TraceValueHandler;

import java.util.ArrayList;
import java.util.List;

public class MemoryTraceBuilder extends TraceBuilder {

    private List<TraceElement> values;

    public MemoryTraceBuilder(String traceId) {
        super(traceId);
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
