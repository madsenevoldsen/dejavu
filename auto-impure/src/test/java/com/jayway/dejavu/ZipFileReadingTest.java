package com.jayway.dejavu;

import com.jayway.dejavu.core.*;
import com.jayway.dejavu.core.memorytrace.MemoryTraceBuilder;
import com.jayway.dejavu.impl.ZipFileReader;
import com.jayway.dejavu.recordreplay.RecordReplayFactory;
import com.jayway.dejavu.recordreplay.RecordReplayer;
import org.junit.Test;

public class ZipFileReadingTest {

    @Test
    public void read_from_zip_file() throws Throwable {
        DejaVuEngine.setEngineFactory(new RecordReplayFactory());
        TraceBuilder builder = new MemoryTraceBuilder(new AutoImpureTraceValueHandler())
                .startMethod(ZipFileReader.class)
                .startArguments("myZipFile.zip");

        builder.add(Pure.PureZipFile, true, Pure.PureZipEntry, "entry.txt", Pure.PureInputStream,
                Pure.PureInputStreamReader, Pure.PureBufferedReader, "line1", "line2", null, null, false );

        RecordReplayer.replay(builder.build());
    }
}
