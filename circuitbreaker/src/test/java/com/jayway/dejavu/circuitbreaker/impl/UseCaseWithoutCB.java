package com.jayway.dejavu.circuitbreaker.impl;

import com.jayway.dejavu.core.Provider;
import com.jayway.dejavu.core.UseCase;
import com.jayway.dejavu.core.annotation.Autowire;
import com.jayway.dejavu.core.value.IntegerValue;
import com.jayway.dejavu.core.value.VoidValue;

public class UseCaseWithoutCB extends UseCase<IntegerValue, Long> {

    @Autowire("NormalProvider") Provider<Integer, VoidValue> sick;

    private String provider;

    public Long run(IntegerValue value ) {
        sick.request( value.getValue() );
        return 42L;
    }
}
