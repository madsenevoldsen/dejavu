package com.jayway.dejavu.circuitbreaker.impl;

import com.jayway.dejavu.circuitbreaker.CircuitBreakerHandler;
import com.jayway.dejavu.circuitbreaker.CircuitOpenException;
import com.jayway.dejavu.core.Provider;
import com.jayway.dejavu.core.Tracer;
import com.jayway.dejavu.core.value.ExceptionValue;
import com.jayway.dejavu.core.value.Value;

public class CircuitBreakerProvider<I,O extends Value> implements Provider<I,O> {
    private Provider<I,O> provider;
    private CircuitBreakerHandler breaker;
    private Tracer tracer;

    public CircuitBreakerProvider( Provider<I,O> provider, CircuitBreakerHandler breaker, Tracer tracer ) {
        this.provider = provider;
        this.breaker = breaker;
        this.tracer = tracer;
    }

    @Override
    public O request(I input) {
        if ( breaker.getState().equals( "Open" )) {
            tracer.provided( new ExceptionValue( CircuitOpenException.class.getCanonicalName() ));
            throw new CircuitOpenException();
        }
        try {
            O result = provider.request( input );
            breaker.success();
            return result;
        } catch( RuntimeException e ) {
            breaker.exceptionOccurred( e );
            throw e;
        }
    }
}