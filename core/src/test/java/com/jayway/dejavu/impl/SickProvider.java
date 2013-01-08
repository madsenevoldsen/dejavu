package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.Provider;
import com.jayway.dejavu.core.value.VoidValue;

public class SickProvider implements Provider<Void, VoidValue> {

    @Override
    public VoidValue request(Void input) {
        throw new NullPointerException();
    }

}
