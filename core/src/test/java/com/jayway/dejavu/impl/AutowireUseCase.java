package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.UseCase;
import com.jayway.dejavu.core.value.VoidValue;

public class AutowireUseCase extends UseCase<VoidValue, String>{

    @Override
    public String run(VoidValue input ) {
        // other instances can be wire by calling this method
        String randomString = wireDependencies(new ExampleStep()).getRandomString();
        // or alternatively:
        String otherRandomString = getTracer().wireDependencies( new ExampleStep()).getRandomString();

        return randomString + otherRandomString;
    }
}
