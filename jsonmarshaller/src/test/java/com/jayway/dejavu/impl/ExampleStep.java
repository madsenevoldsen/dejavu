package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.Provider;
import com.jayway.dejavu.core.annotation.Autowire;
import com.jayway.dejavu.core.value.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExampleStep {
    private Logger log = LoggerFactory.getLogger(ExampleStep.class);

    @Autowire("RandomUUID") Provider<Void, StringValue> uuid;

    public void run(String input) {
        String randomString = uuid.request(null).getString();
        log.info( "Random string is: "+randomString);

        // we will crash here
        double impossible = 4 / 0;
    }
}
