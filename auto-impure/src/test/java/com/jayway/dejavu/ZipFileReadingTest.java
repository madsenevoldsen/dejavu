package com.jayway.dejavu;

import com.jayway.dejavu.core.AutoImpureTraceValueHandler;
import com.jayway.dejavu.core.DejaVuEngine;
import com.jayway.dejavu.core.Pure;
import com.jayway.dejavu.core.TraceBuilder;
import com.jayway.dejavu.impl.ZipFileReader;
import com.jayway.dejavu.recordreplay.RecordReplayFactory;
import com.jayway.dejavu.recordreplay.RecordReplayer;
import org.junit.Test;

public class ZipFileReadingTest {

    @Test
    public void read_from_zip_file() throws Throwable {
        DejaVuEngine.setEngineFactory(new RecordReplayFactory());
        TraceBuilder builder = DejaVuEngine.createTraceBuilder("traceId", new AutoImpureTraceValueHandler())
                .startMethod(ZipFileReader.class)
                .startArguments("myZipFile.zip");

        builder.add(Pure.PureZipFile, true, Pure.PureZipEntry, "entry.txt", Pure.PureInputStream,
                Pure.PureInputStreamReader, Pure.PureBufferedReader, "line1", "line2", null, null, false );

        RecordReplayer.replay(builder.build());
    }
}
