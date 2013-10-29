package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.annotation.Impure;
import com.jayway.dejavu.core.annotation.Traced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.UUID;

public class ExampleTrace {

    private Logger log = LoggerFactory.getLogger(ExampleTrace.class);

    @Traced
    public void run() {
        Long value = timeStamp();
        log.info("First  nano time is: " + value);

        String randomString = randomUUID();
        log.info( "Random string is: "+randomString);

        value = timeStamp();
        log.info( "Second nano time is: " + value );

        // we will crash here
        double impossible = 4 / 0;
    }

    @Impure
    private long timeStamp() {
        return new Date().getTime();
    }

    @Impure
    private String randomUUID() {
        return UUID.randomUUID().toString();
    }
}
