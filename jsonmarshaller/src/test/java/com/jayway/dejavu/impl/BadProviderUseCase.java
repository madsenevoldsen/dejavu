package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.annotation.Autowire;
import com.jayway.dejavu.core.Provider;
import com.jayway.dejavu.core.UseCase;
import com.jayway.dejavu.core.value.LongValue;
import com.jayway.dejavu.core.value.StringValue;
import com.jayway.dejavu.core.value.VoidValue;

public class BadProviderUseCase extends UseCase<VoidValue, Void>{

    @Autowire("Timestamp") Provider<Void, LongValue> timestamp;
    @Autowire("ExceptingProvider") Provider<Void, StringValue> badProvider;

    @Override
    public Void run(VoidValue input) {
        Long value = timestamp.request(null).getValue();
        String string = badProvider.request(null).getString();
        return null;
    }
}
