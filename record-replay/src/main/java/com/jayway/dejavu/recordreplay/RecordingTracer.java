package com.jayway.dejavu.recordreplay;

import com.jayway.dejavu.core.*;
import com.jayway.dejavu.core.interfaces.DejaVuInterception;
import com.jayway.dejavu.core.interfaces.Trace;
import com.jayway.dejavu.core.interfaces.Tracer;

public class RecordingTracer implements Tracer {

    private final TraceBuilder trace;

    public RecordingTracer( TraceBuilder trace ) {
        this.trace = trace;
    }

    @Override
    public Object nextValue(String threadId, DejaVuInterception interception) throws Throwable {
        synchronized (trace){
            try {
                Object result = interception.proceed();
                trace.addValue( threadId, result);
                return result;
            } catch (Throwable t) {
                trace.addValue( threadId, new ThrownThrowable(t));
                throw t;
            }
        }
    }

    @Override
    public String getNextChildThreadId(String parentThreadId) {
        String childThreadId = parentThreadId + "." + DejaVuEngine.generateId();
        trace.addThreadId( childThreadId );
        return childThreadId;
    }

    @Override
    public Trace getTrace() {
        return trace.build();
    }
}
