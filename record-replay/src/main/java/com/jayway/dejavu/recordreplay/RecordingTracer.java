package com.jayway.dejavu.recordreplay;

import com.jayway.dejavu.core.*;
import com.jayway.dejavu.core.repository.Tracer;

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
                trace.add( new TraceElement(threadId, traceValueHandler.handle(result)));
                return result;
            } catch (Throwable t) {
                trace.add( new TraceElement(threadId, traceValueHandler.handle(new ThrownThrowable(t))));
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
