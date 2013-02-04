package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.annotation.Impure;
import com.jayway.dejavu.core.annotation.Traced;

import java.util.Date;
import java.util.Random;

public class IPCallingIP {

    @Traced
    public Long getTime() {
        return randomTime();
    }

    @Impure
    private long randomTime() {
        return new Date( randomInt() ).getTime();
    }

    @Impure
    private Long randomInt() {
        return new Random().nextLong();
    }
}
