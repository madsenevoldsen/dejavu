package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.annotation.IntegrationPoint;
import com.jayway.dejavu.core.annotation.Traced;

import java.util.UUID;

public class MultiTrace {

    @Traced
    public String first() {
        return random() + "_AND_" + second();
    }

    @Traced
    public String second() {
        return random();
    }


    @IntegrationPoint
    private String random() {
        return UUID.randomUUID().toString();
    }

}
