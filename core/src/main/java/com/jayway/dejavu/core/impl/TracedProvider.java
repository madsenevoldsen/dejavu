package com.jayway.dejavu.core.impl;

import com.jayway.dejavu.core.Provider;
import com.jayway.dejavu.core.value.Value;

public class TracedProvider implements Provider {
    private final Tracer tracer;
    private final Provider provider;

    public TracedProvider(Tracer tracer, Provider provider) {
        this.tracer = tracer;
        this.provider = provider;
    }

    @Override
    public Value request(Object input) {
        return tracer.provide(provider, input);
    }
}
