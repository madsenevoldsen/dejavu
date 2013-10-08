package com.jayway.dejavu;

import com.jayway.dejavu.core.*;
import com.jayway.dejavu.core.marshaller.Marshaller;
import com.jayway.dejavu.impl.TraceCallbackImpl;
import com.jayway.dejavu.impl.WithThreads;
import com.jayway.dejavu.recordreplay.RecordReplayFactory;
import com.jayway.dejavu.recordreplay.RecordReplayer;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiThreadedTest {

    private TraceCallbackImpl callback;

    @Before
    public void setup() {
        callback = new TraceCallbackImpl();
        DejaVuPolicy.initialize(callback);
        RunningTrace.initialize();
        AutoImpureTraceValueHandler.initialize();
        DejaVuPolicy.setFactory(new RecordReplayFactory());
    }

    @Test
    public void with_three_child_threads() throws Throwable {
        WithThreads withThreads = new WithThreads();
        int threads = 5;
        withThreads.begin( threads );
        waitForCompletion();


        Trace trace = callback.getTrace();
        System.out.println(new Marshaller().marshal(trace));

        final List<TraceElement> values = new ArrayList<TraceElement>();
        RunningTrace.addTraceHandler(new TraceValueHandlerAdapter() {
            public Object replay(Object value) {
                values.add(new TraceElement(Thread.currentThread().getName(), value));
                return value;
            }
        });
        RecordReplayer.replay(trace);

        Map<String, String> threadNameMap = new HashMap<String, String>();
        Assert.assertEquals("Trace and replay must have same amount of values", trace.impureValueCount(), values.size());
        for (int i=0; i<trace.impureValueCount(); i++ ) {
            TraceElement element = trace.get(i);
            TraceElement rerunElement = values.get(i);
            if ( !(element.getValue() instanceof Pure) ) {
                Assert.assertEquals(element.getValue(), rerunElement.getValue());
            }
            if ( threadNameMap.containsKey(element.getThreadId()) ) {
                Assert.assertEquals( threadNameMap.get( element.getThreadId()), rerunElement.getThreadId());
            } else {
                // first encounter of two threadIds, must be same for all other
                threadNameMap.put( element.getThreadId(), rerunElement.getThreadId() );
            }
        }

        // exactly three threads are expected to have run
        Assert.assertEquals( threads, threadNameMap.size() );
    }

    private void waitForCompletion() {
        // wait for the trace to be done
        while ( callback.getTrace() == null ) {
            try {
                Thread.sleep( 500 );
            } catch (InterruptedException e ){
                // ignore
            }
        }
    }
}
