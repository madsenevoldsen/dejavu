package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.Provider;
import com.jayway.dejavu.core.value.StringValue;

import java.util.UUID;

public class RandomUUID implements Provider<Void, StringValue> {

    @Override
    public StringValue request(Void input) {
        return new StringValue( UUID.randomUUID().toString() );
    }
}
