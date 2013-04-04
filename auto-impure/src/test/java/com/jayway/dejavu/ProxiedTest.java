package com.jayway.dejavu;

import com.jayway.dejavu.core.DejaVuAspect;
import com.jayway.dejavu.core.DejaVuTrace;
import com.jayway.dejavu.core.Trace;
import com.jayway.dejavu.core.marshaller.Marshaller;
import com.jayway.dejavu.core.marshaller.SimpleExceptionMarshaller;
import com.jayway.dejavu.core.marshaller.TraceBuilder;
import com.jayway.dejavu.impl.FileReading;
import com.jayway.dejavu.impl.RandomProxyExample;
import com.jayway.dejavu.impl.TraceCallbackImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.List;

public class ProxiedTest {

    private Logger log = LoggerFactory.getLogger( ProxiedTest.class );

    private TraceCallbackImpl callback;

    @Before
    public void setup() {
        callback = new TraceCallbackImpl();
        DejaVuAspect.initialize(callback);
    }

    @Test
    public void randomProxy() throws Throwable {
        RandomProxyExample example = new RandomProxyExample();

        int result = example.invoke();

        Trace trace = callback.getTrace();
        Assert.assertNotNull( trace );

        String test = new Marshaller().marshal(trace);
        System.out.println( test );
        int result2 = DejaVuTrace.run(trace);

        System.out.println( result + " and " +result2 );
        Assert.assertEquals( result, result2);
    }

    /*@Test
    public void fileReading() throws Throwable {
        FileReading reading = new FileReading();

        List<String> list = reading.readFile("src/test/resources/example.txt");

        Assert.assertEquals( list.size(), 4);
        Assert.assertEquals( "first line", list.get(0));
        Assert.assertEquals( "second line", list.get(1));
        Assert.assertEquals( "third line", list.get(2));
        Assert.assertEquals( "done!", list.get(3));

        Trace trace = callback.getTrace();
        String test = new Marshaller().marshal(trace);
        System.out.println(test );
    } */

    @Test
    public void notReadingFile() throws Throwable {
        // now read the file in test mode where it only produces one line
        TraceBuilder builder = TraceBuilder.build()
                .setMethod(FileReading.class)
                .addMethodArguments( "not a filename");

        builder.add("ONLY LINE", null);

        List<String> lines = (List<String>) builder.run();

        Assert.assertEquals( 1, lines.size());
        Assert.assertEquals( "ONLY LINE", lines.get(0));
    }

    @Test
    public void notReadingFileException() throws Throwable {
        // now read the file in test mode where it only produces one line
        TraceBuilder builder = TraceBuilder.build(new SimpleExceptionMarshaller())
                .setMethod(FileReading.class)
                .addMethodArguments( "nonexisting.xyz");

        // reading the first line of the Buffered reader
        // will produce a ConcurrentModificationException
        builder.add(ConcurrentModificationException.class );

        try {
            builder.run();
            Assert.fail();
        } catch (ConcurrentModificationException e ) {
            // it must throw IOException
        }
    }
}
