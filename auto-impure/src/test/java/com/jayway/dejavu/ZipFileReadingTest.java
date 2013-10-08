package com.jayway.dejavu;

import com.jayway.dejavu.core.AutoImpureTraceValueHandler;
import com.jayway.dejavu.core.DejaVuPolicy;
import com.jayway.dejavu.core.Pure;
import com.jayway.dejavu.impl.ZipFileReader;
import com.jayway.dejavu.recordreplay.RecordReplayFactory;
import com.jayway.dejavu.recordreplay.TraceBuilder;
import org.junit.Test;

public class ZipFileReadingTest {

    @Test
    public void read_from_zip_file() throws Throwable {
        AutoImpureTraceValueHandler.initialize();
        DejaVuPolicy.setFactory(new RecordReplayFactory());
        TraceBuilder builder = TraceBuilder.build()
                .setMethod(ZipFileReader.class)
                .addMethodArguments("myZipFile.zip");

        builder.add(Pure.PureZipFile, true, Pure.PureZipEntry, "entry.txt", Pure.PureInputStream,
                Pure.PureInputStreamReader, Pure.PureBufferedReader, "line1", "line2", null, null, false );

        builder.run();
    }
}
