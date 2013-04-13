package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.annotation.Traced;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipFileReader {

    @Traced
    public void readFile(String fileName) throws IOException {
        ZipFile file = null;
        try {
            file = new ZipFile(fileName);
            Enumeration<? extends ZipEntry> entries = file.entries();
            while ( entries.hasMoreElements() ) {
                ZipEntry entry = entries.nextElement();
                if ( entry.getName().equals("entry.txt") ) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(entry), "UTF-8"));
                    String object;
                    while ( null != ( object = reader.readLine())) {
                        try {
                            System.out.println( object );
                        } catch (RuntimeException e) {
                            throw new RuntimeException(object);
                        }
                    }
                }
            }
        } finally {
            if ( file != null ) {
                file.close();
            }
        }
    }
}
