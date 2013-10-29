package com.jayway.dejavu.core;

import com.jayway.dejavu.core.chainer.ChainBuilder;
import com.jayway.dejavu.core.interfaces.Trace;
import com.jayway.dejavu.core.interfaces.TraceValueHandler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

class ReplayTraceBuilder extends TraceBuilder {

    private Trace trace;
    private TraceValueHandler handler;
    private Map<String, LinkedList<String>> childThreads = new HashMap<String, LinkedList<String>>();

    public ReplayTraceBuilder( Trace trace, TraceValueHandler... handlers ) {
        super(trace.getId());
        this.trace = trace;
        handler = ChainBuilder.compose( TraceValueHandler.class).add( handlers ).build();
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
    public TraceBuilder addValue(String threadId, Object value) {
        return this;
    }

    @Override
    protected void addElement(TraceElement element) {
        handler.handle( element.getValue() );
    }

    @Override
    public Trace build() {
        return trace;
    }

    @Override
    public String getNextChildThreadId(String parentThreadId) {
        return childThreads.get(parentThreadId).removeFirst();
    }
}
