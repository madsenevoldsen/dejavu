package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.Provider;
import com.jayway.dejavu.core.value.LongValue;

public class Timestamp implements Provider<Void, LongValue> {

    @Override
    public LongValue request(Void input) {
        return new LongValue( System.nanoTime() );
    }
}
