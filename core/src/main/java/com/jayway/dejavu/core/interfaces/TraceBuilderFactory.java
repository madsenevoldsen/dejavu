package com.jayway.dejavu.core.interfaces;

import com.jayway.dejavu.core.TraceBuilder;

public interface TraceBuilderFactory {

    TraceBuilder createTraceBuilder( String traceId );
}
