package com.jayway.dejavu.helper;

import com.jayway.dejavu.core.DejaVuEngine;
import com.jayway.dejavu.core.DejaVuEngine;
import com.jayway.dejavu.core.memorytrace.MemoryTraceBuilder;
import com.jayway.dejavu.core.TraceBuilder;
import com.jayway.dejavu.impl.ExampleTrace;
import com.jayway.dejavu.recordreplay.RecordReplayFactory;
import com.jayway.dejavu.recordreplay.RecordReplayer;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

public class TraceBuilderTest {

    @Before
    public void setup() {
        DejaVuEngine.setFactory(new RecordReplayFactory());
    }

    @Test
    public void builder() throws Throwable {
        TraceBuilder builder = new MemoryTraceBuilder().startMethod(ExampleTrace.class);

        builder.add( 349013193767909L, "d09c2893-2835-4cbe-8c8e-4c790c268ed0", 349013194166199L);

        try {
            RecordReplayer.replay(builder.build());
            Assert.fail();
        } catch (ArithmeticException e) {

        }
    }

    @Test
    public void simple_types_test() throws Throwable {
        TraceBuilder builder = new MemoryTraceBuilder().startMethod(AllSimpleTypes.class);

        builder.add( "string", 1.1F, true, 2.2, 1L, 1 );

        String result = RecordReplayer.replay(builder.build());

        Assert.assertEquals("string1.1true2.211", result );
    }
}
