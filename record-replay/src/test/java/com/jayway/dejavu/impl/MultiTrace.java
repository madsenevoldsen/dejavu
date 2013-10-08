package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.annotation.Impure;
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


    @Impure
    private String random() {
        return UUID.randomUUID().toString();
    }

}
