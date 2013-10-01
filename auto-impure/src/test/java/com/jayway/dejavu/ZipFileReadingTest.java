package com.jayway.dejavu;

import com.jayway.dejavu.core.AutoImpure;
import com.jayway.dejavu.core.Pure;
import com.jayway.dejavu.core.marshaller.TraceBuilder;
import com.jayway.dejavu.impl.ZipFileReader;
import org.junit.Test;

public class ZipFileReadingTest {

    @Test
    public void read_from_zip_file() throws Throwable {
        AutoImpure.initialize();
        TraceBuilder builder = TraceBuilder.build()
                .setMethod(ZipFileReader.class)
                .addMethodArguments("myZipFile.zip");

        builder.add(Pure.PureZipFile, true, Pure.PureZipEntry, "entry.txt", Pure.PureInputStream,
                Pure.PureInputStreamReader, Pure.PureBufferedReader, "line1", "line2", null, null, false );

        builder.run();
    }
}
