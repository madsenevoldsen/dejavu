package com.jayway.dejavu;

import com.jayway.dejavu.core.AutoImpureMarshallerPlugin;
import com.jayway.dejavu.core.marshaller.TraceBuilder;
import com.jayway.dejavu.impl.ZipFileReader;
import org.junit.Test;

import java.io.InputStream;
import java.util.zip.ZipEntry;

public class ZipFileReadingTest {


    @Test
    public void read_from_zip_file() throws Throwable {
        TraceBuilder builder = TraceBuilder.build( new AutoImpureMarshallerPlugin() )
                .setMethod(ZipFileReader.class)
                .addMethodArguments("myZipFile.zip");

        builder.add( true, ZipEntry.class, "entry.txt", InputStream.class, "line1", "line2", null, null, false );

        builder.run();
    }
}
