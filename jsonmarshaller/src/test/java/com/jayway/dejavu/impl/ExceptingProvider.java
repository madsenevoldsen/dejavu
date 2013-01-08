package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.Provider;
import com.jayway.dejavu.core.value.StringValue;

public class ExceptingProvider implements Provider<Void, StringValue> {

    @Override
    public StringValue request(Void input) {
        throw new NotFound();
    }
}
