package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.annotation.Autowire;
import com.jayway.dejavu.core.Provider;
import com.jayway.dejavu.core.UseCase;
import com.jayway.dejavu.core.value.LongValue;
import com.jayway.dejavu.core.value.VoidValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleUseCase extends UseCase<VoidValue, Void>{

    private Logger log = LoggerFactory.getLogger(ExampleUseCase.class);
    @Autowire("Timestamp") Provider<Void, LongValue> timeStamp;

    @Override
    public Void run(VoidValue input) {
        Long value = timeStamp.request(null).getValue();
        log.info("First  nano time is: " + value);

        value = timeStamp.request(null).getValue();
        log.info( "Second nano time is: " + value );

        ExampleStep step = new ExampleStep();
        getTracer().wireDependencies( step );
        step.run( "ignored" );

        return null;
    }
}
