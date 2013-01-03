package com.jayway.dejavu.core.impl;

import com.jayway.dejavu.core.Provider;
import com.jayway.dejavu.core.Tracer;
import com.jayway.dejavu.value.Value;

public class DelegatingProvider<I, O extends Value> implements Provider<I,O> {
    private final Tracer tracer;
    private final Provider<I,O> provider;

    public DelegatingProvider( Tracer tracer, Provider<I,O> provider) {
        this.tracer = tracer;
        this.provider = provider;
    }

    @Override
    public O request(I input) {
        return tracer.provide(provider, input);
    }
}
