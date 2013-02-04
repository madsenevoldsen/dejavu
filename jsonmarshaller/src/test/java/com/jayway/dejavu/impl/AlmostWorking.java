package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.annotation.Impure;
import com.jayway.dejavu.core.annotation.Traced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlmostWorking {

    private Logger log = LoggerFactory.getLogger( AlmostWorking.class);

    @Traced
    public void run() {
        Long value = timeStamp();
        Long luckyNumber = 2304432 / ( value % 1001 );
        log.info( "My lucky number is: "+luckyNumber);
    }

    @Impure
    private Long timeStamp() {
        return System.nanoTime();
    }
}
