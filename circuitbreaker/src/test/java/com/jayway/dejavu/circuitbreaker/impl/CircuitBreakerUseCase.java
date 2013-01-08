package com.jayway.dejavu.circuitbreaker.impl;

import com.jayway.dejavu.core.annotation.Autowire;
import com.jayway.dejavu.core.Provider;
import com.jayway.dejavu.core.UseCase;
import com.jayway.dejavu.core.value.IntegerValue;
import com.jayway.dejavu.core.value.VoidValue;

public class CircuitBreakerUseCase extends UseCase<IntegerValue, Long>{

    @Autowire("CircuitBrokenProvider") Provider<Integer, VoidValue> sick;

    @Override
    public Long run(IntegerValue value ) {
        sick.request( value.getValue() );
        return 42L;
    }
}
