package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.annotation.IntegrationPoint;
import com.jayway.dejavu.core.annotation.Traced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.UUID;

public class ExampleUseCase {

    private Logger log = LoggerFactory.getLogger(ExampleUseCase.class);

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

    @IntegrationPoint
    private long timeStamp() {
        return new Date().getTime();
    }

    @IntegrationPoint
    private String randomUUID() {
        return UUID.randomUUID().toString();
    }
}
