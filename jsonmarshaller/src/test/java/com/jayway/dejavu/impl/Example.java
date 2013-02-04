package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.annotation.Impure;
import com.jayway.dejavu.core.annotation.Traced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class Example {

    private Logger log = LoggerFactory.getLogger(Example.class);

    @Traced
    public void run() {
        Long value = timeStamp();
        log.info("First  nano time is: " + value);

        value = timeStamp();
        log.info( "Second nano time is: " + value );

        String randomString = randomUUID();
        log.info( "Random string is: "+randomString);

        double impossible = 4 /0;
    }

    @Impure
    private Long timeStamp() {
        return System.nanoTime();
    }

    @Impure
    private String randomUUID() {
        return UUID.randomUUID().toString();
    }
}
