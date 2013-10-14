package com.jayway.dejavu.recordreplay;

import com.jayway.dejavu.core.*;
import com.jayway.dejavu.core.repository.Tracer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ReplayTracer implements Tracer {

    private Map<String, LinkedList<String>> childThreads = new HashMap<String, LinkedList<String>>();
    private int current;
    private TraceValueHandler traceValueHandler;
    private Trace trace;

    public ReplayTracer( Trace trace ) {
        this.trace = trace;
        current = 0;
        childThreads = new HashMap<String, LinkedList<String>>();
        for (TraceElement element : trace) {
            String threadId = element.getThreadId();
            if ( threadId.contains(".") ) {
                // this is from a child thread
                String parent = threadId.substring(0, threadId.lastIndexOf("."));
                if ( !childThreads.containsKey( parent) ) {
                    childThreads.put( parent, new LinkedList<String>() );
                }
                if ( !childThreads.get(parent).contains( threadId) ) {
                    childThreads.get(parent).addLast( threadId );
                }
            }
        }
    }

    @Override
    public synchronized Object nextValue(String threadId, DejaVuInterception interception) throws Throwable{
        while (true) {
            if (current >= trace.impureValueCount()) {
                throw new RuntimeException("Trace ended!");
            }
            TraceElement result = trace.get(current);
            if ( threadId.equals(result.getThreadId()) ) {
                current++;
                notifyAll();
                if ( result.getValue() instanceof ThrownThrowable ) {
                    throw ((ThrownThrowable) result.getValue()).getThrowable();
                }
                return traceValueHandler.handle(result.getValue());
            } else {
                try {
                    // we need to wait for the value to be ready
                    wait();
                } catch (InterruptedException e) {
                    // ignore. Continue waiting
                }
            }
        }
    }

    @Override
    public String getNextChildThreadId(String parentThreadId ) {
        return childThreads.get(parentThreadId).removeFirst();
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
