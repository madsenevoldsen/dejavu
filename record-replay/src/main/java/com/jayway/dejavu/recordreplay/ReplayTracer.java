package com.jayway.dejavu.recordreplay;

import com.jayway.dejavu.core.*;
import com.jayway.dejavu.core.chainer.ChainBuilder;
import com.jayway.dejavu.core.repository.Tracer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class ReplayTracer implements Tracer {

    private Map<String, LinkedList<String>> childThreads = new HashMap<String, LinkedList<String>>();
    private Iterator<TraceElement> iterator;
    private TraceElement current;
    private Trace trace;
    private TraceValueHandler handlerChain;

    public ReplayTracer( Trace trace, TraceValueHandler... handlers ) {
        this.trace = trace;
        handlerChain = ChainBuilder.compose( TraceValueHandler.class).add( handlers).build();
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
        iterator = trace.iterator();
    }

    @Override
    public synchronized Object nextValue(String threadId, DejaVuInterception interception) throws Throwable{
        while (true) {
            if ( current == null ) {
                current = iterator.next();
            }
            if ( threadId.equals( current.getThreadId()) ) {
                TraceElement element = current;
                current = null;
                notifyAll();
                Object result = handlerChain.handle(element.getValue());
                if ( result instanceof ThrownThrowable ) {
                    throw ((ThrownThrowable) result).getThrowable();
                }
                return result;
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
    public Trace getTrace() {
        return trace;
    }
}
