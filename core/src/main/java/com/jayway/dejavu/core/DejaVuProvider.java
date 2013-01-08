package com.jayway.dejavu.core;

import com.jayway.dejavu.core.impl.ValueProvider;
import com.jayway.dejavu.core.value.Value;

public class DejaVuProvider implements Provider<Object,Value> {

    private ValueProvider provider;

    public DejaVuProvider( ValueProvider provider ) {
        this.provider = provider;
    }

    @Override
    public Value request(Object input) {
        return provider.getNext();
    }
}
