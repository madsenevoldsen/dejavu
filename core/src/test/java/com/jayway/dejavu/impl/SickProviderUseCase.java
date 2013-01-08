package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.annotation.Autowire;
import com.jayway.dejavu.core.Provider;
import com.jayway.dejavu.core.UseCase;
import com.jayway.dejavu.core.value.VoidValue;

public class SickProviderUseCase extends UseCase<VoidValue, Void>{

    @Autowire("SickProvider") Provider<Void, VoidValue> sick;

    @Override
    public Void run(VoidValue input ) {
        sick.request(null);
        return null;
    }
}
