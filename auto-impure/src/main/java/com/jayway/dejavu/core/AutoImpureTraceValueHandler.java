package com.jayway.dejavu.core;

import com.jayway.dejavu.core.interfaces.TraceValueHandler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.easymock.EasyMock.createMock;

public class AutoImpureTraceValueHandler implements TraceValueHandler {

    @Override
    public Object handle(Object value) {
        if ( value instanceof Random) {
            return Pure.PureRandom;
        } else if ( value instanceof ZipEntry) {
            return Pure.PureZipEntry;
        } else if ( value instanceof InputStream) {
            return Pure.PureInputStream;
        } else if ( value instanceof FileReader) {
            return Pure.PureFileReader;
        } else if ( value instanceof BufferedReader ) {
            return Pure.PureBufferedReader;
        } else if (value instanceof ZipFile) {
            return Pure.PureZipFile;
        } else if ( value instanceof InputStreamReader) {
            return Pure.PureInputStreamReader;
        } else if ( value instanceof Pure) {
            Pure pure = (Pure) value;
            switch (pure) {
                case PureRandom:
                    return createMock(Random.class);
                case PureZipEntry:
                    return createMock(ZipEntry.class);
                case PureInputStream:
                    return createMock(InputStream.class);
                case PureFileReader:
                    return createMock(FileReader.class);
                case PureBufferedReader:
                    return createMock(BufferedReader.class);
                case PureZipFile:
                    return createMock(ZipFile.class);
                case PureInputStreamReader:
                    return createMock(InputStreamReader.class);
            }
        }

        return value;
    }
}
