package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.annotation.Impure;
import com.jayway.dejavu.core.annotation.Traced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlmostWorking {

    @Traced
    public void getLucky() {
        Long value = timeStamp();
        Long luckyNumber = 2304432 / ( value % 1001 );
        //System.out.println( "My lucky number is: "+luckyNumber);
    }

    @Impure
    private Long timeStamp() {
        return System.nanoTime();
    }
}
