package com.jayway.dejavu;

import com.jayway.dejavu.core.*;
import com.jayway.dejavu.core.marshaller.Marshaller;
import com.jayway.dejavu.core.marshaller.RuntimeExceptionValueHandler;
import com.jayway.dejavu.impl.FileReading;
import com.jayway.dejavu.impl.RandomProxyExample;
import com.jayway.dejavu.impl.TraceCallbackImpl;
import com.jayway.dejavu.recordreplay.RecordReplayFactory;
import com.jayway.dejavu.recordreplay.RecordReplayer;
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
        RunningTrace.initialize();
        DejaVuPolicy.initialize(callback);
        DejaVuPolicy.setFactory(new RecordReplayFactory());
    }

    @Test
    public void randomProxy() throws Throwable {
        RandomProxyExample example = new RandomProxyExample();

        int result = example.invoke();

        Trace trace = callback.getTrace();
        Assert.assertNotNull( trace );

        String test = new Marshaller(new AutoImpureTraceValueHandler()).marshal(trace);
        System.out.println( test );
        Integer result2 = RecordReplayer.replay(trace);

        System.out.println(result + " and " + result2);
        Assert.assertEquals(result, result2.intValue());
    }

    @Test
    public void notReadingFile() throws Throwable {
        // now read the file in test mode where it only produces one line
        TraceBuilder builder = new MemoryTraceBuilder( new AutoImpureTraceValueHandler())
                .startMethod(FileReading.class)
                .startArguments("not a filename");

        builder.add(Pure.PureFileReader, Pure.PureBufferedReader, "ONLY LINE", null);

        List<String> lines = RecordReplayer.replay(builder.build());

        Assert.assertEquals( 1, lines.size());
        Assert.assertEquals( "ONLY LINE", lines.get(0));
    }

    @Test
    public void notReadingFileException() throws Throwable {
        // now read the file in test mode where it only produces one line
        TraceBuilder builder = new MemoryTraceBuilder(
            new RuntimeExceptionValueHandler())
                .startMethod(FileReading.class)
                .startArguments("nonexisting.xyz");

        // reading the first line of the Buffered reader
        // will produce a ConcurrentModificationException
        builder.add(ConcurrentModificationException.class );

        try {
            RecordReplayer.replay(builder.build());
            Assert.fail();
        } catch (ConcurrentModificationException e ) {
            // it must throw IOException
        }
    }
}
