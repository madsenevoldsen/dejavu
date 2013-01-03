package com.jayway.dejavu;

import com.jayway.dejavu.core.Provider;
import com.jayway.dejavu.value.StringValue;

public class SickProvider implements Provider<Void, StringValue> {

    @Override
    public StringValue request(Void input) {
        throw new NullPointerException();
    }
}
