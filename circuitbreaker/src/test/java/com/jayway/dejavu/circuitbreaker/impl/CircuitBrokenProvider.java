package com.jayway.dejavu.circuitbreaker.impl;

import com.jayway.dejavu.circuitbreaker.annotation.CircuitBreaker;
import com.jayway.dejavu.core.Provider;
import com.jayway.dejavu.core.value.VoidValue;

@CircuitBreaker("ExampleHandler")
public class CircuitBrokenProvider implements Provider<Integer, VoidValue> {

    @Override
    public VoidValue request(Integer input) {
        if ( input == 1 ) {
            throw new MySpecificException();
        }
        return null;
    }

}
