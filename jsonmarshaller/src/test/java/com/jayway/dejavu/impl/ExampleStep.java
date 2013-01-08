package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.annotation.Autowire;
import com.jayway.dejavu.core.Provider;
import com.jayway.dejavu.core.Step;
import com.jayway.dejavu.core.value.LongValue;
import com.jayway.dejavu.core.value.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExampleStep extends Step<String, Void> {
    private Logger log = LoggerFactory.getLogger(ExampleStep.class);

    @Autowire("RandomUUID") Provider<Void, StringValue> uuid;
    @Autowire("Timestamp") Provider<Void, LongValue> timeStamp;

    @Override
    public Void run(String input) {
        String randomString = uuid.request(null).getString();
        log.info( "Random string is: "+randomString);

        // we will crash here
        double impossible = 4 / 0;
        return null;
    }
}
