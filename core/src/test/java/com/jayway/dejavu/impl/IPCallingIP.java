package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.annotation.IntegrationPoint;
import com.jayway.dejavu.core.annotation.Traced;

import java.util.Date;
import java.util.Random;

public class IPCallingIP {

    @Traced
    public Long getTime() {
        return randomTime();
    }

    @IntegrationPoint
    private long randomTime() {
        return new Date( randomInt() ).getTime();
    }

    @IntegrationPoint
    private Long randomInt() {
        return new Random().nextLong();
    }
}
