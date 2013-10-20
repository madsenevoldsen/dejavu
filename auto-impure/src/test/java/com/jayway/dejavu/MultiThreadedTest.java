package com.jayway.dejavu;

import com.jayway.dejavu.core.*;
import com.jayway.dejavu.unittest.Marshaller;
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
        DejaVuPolicy.setFactory(new RecordReplayFactory());
    }

    @Test
    public void with_three_child_threads() throws Throwable {
        WithThreads withThreads = new WithThreads();
        int threads = 5;
        withThreads.begin( threads );
        waitForCompletion();


        Trace trace = callback.getTrace();
        System.out.println(new Marshaller(new AutoImpureTraceValueHandler()).marshal(trace));
        int size = 0;
        for (TraceElement traceElement : trace) {
            size++;
        }

        final List<TraceElement> values = new ArrayList<TraceElement>();
        RecordReplayer.replay(trace, new TraceValueHandler() {
            @Override
            public Object handle(Object value) {
                values.add(new TraceElement(Thread.currentThread().getName(), value));
                return value;
            }
        });

        Map<String, String> threadNameMap = new HashMap<String, String>();
        Assert.assertEquals("Trace and replay must have same amount of values", size, values.size());
        int i=0;
        for (TraceElement element : trace ) {
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
            i++;
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
