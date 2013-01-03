package com.jayway.dejavu;

import com.jayway.dejavu.core.Provider;
import com.jayway.dejavu.value.LongValue;

public class Timestamp implements Provider<Void, LongValue> {

    @Override
    public LongValue request(Void input) {
        return new LongValue( System.nanoTime() );
    }
}
