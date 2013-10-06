package com.jayway.dejavu.core.repository;

import com.jayway.dejavu.core.*;

public class RecordingTracer implements Tracer {

    private final Trace trace;
    private TraceValueHandler traceValueHandler;

    public RecordingTracer( Trace trace ) {
        this.trace = trace;
    }

    @Override
    public Object nextValue(String threadId, DejaVuInterception interception) throws Throwable {
        synchronized (trace){
            try {
                Object result = interception.proceed();
                trace.getValues().add( new TraceElement(threadId, traceValueHandler.record(result)));
                return result;
            } catch (Throwable t) {
                trace.getValues().add( new TraceElement(threadId, traceValueHandler.record(new ThrownThrowable(t))));
                throw t;
            }
        }
    }

    @Override
    public String getNextChildThreadId(String parentThreadId) {
        return parentThreadId + "." + RunningTrace.generateId();
    }

    @Override
    public void setTraceValueHandlerChain(TraceValueHandler traceValueHandler) {
        this.traceValueHandler = traceValueHandler;
    }

    @Override
    public Trace getTrace() {
        return trace;
    }
}
