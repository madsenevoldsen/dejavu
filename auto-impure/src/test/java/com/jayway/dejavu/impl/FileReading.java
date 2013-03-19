package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.annotation.Traced;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileReading {

    @Traced
    public List<String> readFile( String fileName ) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));

        List<String> fileLines = new ArrayList<String>();
        String line;
        while ( null != (line = br.readLine())) {
            fileLines.add( line );
        }
        return fileLines;
    }
}
