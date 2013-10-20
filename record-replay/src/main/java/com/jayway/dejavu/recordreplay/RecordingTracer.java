package com.jayway.dejavu.recordreplay;

import com.jayway.dejavu.core.*;
import com.jayway.dejavu.core.repository.Tracer;

public class RecordingTracer implements Tracer {

    private final TraceBuilder trace;
    //private TraceValueHandler traceValueHandler;

    public RecordingTracer( /*String traceId, DejaVuInterception interception, */TraceBuilder trace ) {
        this.trace = trace;
        //trace = new MemoryTraceBuilder(traceId);
        //trace.startMethod( interception.getMethod());
        //trace.startArguments( interception.getArguments() );
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
        String childThreadId = parentThreadId + "." + RunningTrace.generateId();
        trace.addThreadId( childThreadId );
        return childThreadId;
    }

    /*@Override
    public void setTraceValueHandlerChain(TraceValueHandler traceValueHandler) {
        this.traceValueHandler = traceValueHandler;
    }*/

    @Override
    public Trace getTrace() {
        return trace.build();
    }
}
