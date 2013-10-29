package com.jayway.dejavu.helper;

import com.jayway.dejavu.core.DejaVuEngine;
import com.jayway.dejavu.core.TraceBuilder;
import com.jayway.dejavu.impl.ExampleTrace;
import junit.framework.Assert;
import org.junit.Test;

public class TraceBuilderTest {

    @Test
    public void builder() throws Throwable {
        TraceBuilder builder = DejaVuEngine.createTraceBuilder("traceId").startMethod(ExampleTrace.class);

        builder.add( 349013193767909L, "d09c2893-2835-4cbe-8c8e-4c790c268ed0", 349013194166199L);

        try {
            new DejaVuEngine().replay(builder.build());
            Assert.fail();
        } catch (ArithmeticException e) {

        }
    }

    @Test
    public void simple_types_test() throws Throwable {
        TraceBuilder builder = DejaVuEngine.createTraceBuilder("traceId").startMethod(AllSimpleTypes.class);

        builder.add( "string", 1.1F, true, 2.2, 1L, 1 );

        String result = new DejaVuEngine().replay(builder.build());

        Assert.assertEquals("string1.1true2.211", result );
    }
}
