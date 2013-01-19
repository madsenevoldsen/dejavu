package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.annotation.IntegrationPoint;
import com.jayway.dejavu.core.annotation.Traced;

import java.util.UUID;

public class HowItShouldBe {

    @Traced
    public String myStartPoint( String argument ) {
        // ...
        String random = randomString();

        return argument + random;
    }

    @IntegrationPoint
    private String randomString() {
        return UUID.randomUUID().toString();
    }

}
