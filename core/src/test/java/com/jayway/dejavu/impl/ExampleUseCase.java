package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.Provider;
import com.jayway.dejavu.core.UseCase;
import com.jayway.dejavu.core.annotation.Autowire;
import com.jayway.dejavu.core.value.LongValue;
import com.jayway.dejavu.core.value.StringValue;
import com.jayway.dejavu.core.value.VoidValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleUseCase extends UseCase<VoidValue, Void>{

    private Logger log = LoggerFactory.getLogger(ExampleUseCase.class);

    @Autowire("RandomUUID") Provider<Void, StringValue> uuid;
    @Autowire("Timestamp") Provider<Void, LongValue> timeStamp;

    @Override
    public Void run(VoidValue input) {
        Long value = timeStamp.request(null).getValue();
        log.info("First  nano time is: " + value);

        value = timeStamp.request(null).getValue();
        log.info( "Second nano time is: " + value );

        String randomString = uuid.request(null).getString();
        log.info( "Random string is: "+randomString);

        // we will crash here
        double impossible = 4 / 0;
        return null;
    }
}
