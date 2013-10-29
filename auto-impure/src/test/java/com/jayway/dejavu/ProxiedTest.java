package com.jayway.dejavu;

import com.jayway.dejavu.core.AutoImpureTraceValueHandler;
import com.jayway.dejavu.core.DejaVuEngine;
import com.jayway.dejavu.core.Pure;
import com.jayway.dejavu.core.TraceBuilder;
import com.jayway.dejavu.core.interfaces.Trace;
import com.jayway.dejavu.impl.FileReading;
import com.jayway.dejavu.impl.RandomProxyExample;
import com.jayway.dejavu.impl.RuntimeExceptionValueHandler;
import com.jayway.dejavu.impl.TraceCallbackImpl;
import com.jayway.dejavu.unittest.Marshaller;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ConcurrentModificationException;
import java.util.List;

public class ProxiedTest {

    private TraceCallbackImpl callback;

    @Before
    public void setup() {
        callback = new TraceCallbackImpl();
        DejaVuEngine.initialize(callback);
    }

    @Test
    public void randomProxy() throws Throwable {
        DejaVuEngine.setValueHandlerClasses( AutoImpureTraceValueHandler.class);
        RandomProxyExample example = new RandomProxyExample();

        int result = example.invoke();

        Trace trace = callback.getTrace();
        Assert.assertNotNull( trace );

        String test = new Marshaller().marshal(trace);
        System.out.println( test );
        Integer result2 = new DejaVuEngine().replay(trace, new AutoImpureTraceValueHandler());

        System.out.println(result + " and " + result2);
        Assert.assertEquals(result, result2.intValue());
    }

    @Test
    public void notReadingFile() throws Throwable {
        DejaVuEngine.setValueHandlerClasses( AutoImpureTraceValueHandler.class);
        // now read the file in test mode where it only produces one line
        TraceBuilder builder = DejaVuEngine.createTraceBuilder( "traceId")
                .startMethod(FileReading.class)
                .startArguments("not a filename");

        builder.add(Pure.PureFileReader, Pure.PureBufferedReader, "ONLY LINE", null);

        List<String> lines = new DejaVuEngine().replay(builder.build(), new AutoImpureTraceValueHandler());

        Assert.assertEquals( 1, lines.size());
        Assert.assertEquals( "ONLY LINE", lines.get(0));
    }

    @Test
    public void notReadingFileException() throws Throwable {
        DejaVuEngine.setValueHandlerClasses( RuntimeExceptionValueHandler.class );
        // now read the file in test mode where it only produces one line
        TraceBuilder builder = DejaVuEngine.createTraceBuilder( "traceId")
                .startMethod(FileReading.class)
                .startArguments("nonexisting.xyz");

        // reading the first line of the Buffered reader
        // will produce a ConcurrentModificationException
        builder.add(ConcurrentModificationException.class );

        try {
            new DejaVuEngine().replay(builder.build(), new RuntimeExceptionValueHandler());
            Assert.fail();
        } catch (ConcurrentModificationException e ) {
            // it must throw IOException
        }
    }
}
