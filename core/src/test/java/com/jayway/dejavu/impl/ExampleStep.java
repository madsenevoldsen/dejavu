package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.Provider;
import com.jayway.dejavu.core.annotation.Autowire;
import com.jayway.dejavu.core.value.StringValue;

public class ExampleStep {

    @Autowire("RandomUUID") Provider<Void, StringValue> uuid;

    public String getRandomString() {
        return uuid.request(null).getString();
    }
}
