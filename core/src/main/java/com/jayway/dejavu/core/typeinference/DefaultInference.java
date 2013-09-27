package com.jayway.dejavu.core.typeinference;

import com.jayway.dejavu.core.DejaVuInterception;

public class DefaultInference implements TypeInference {

    @Override
    public Class<?> inferType(Object instance, DejaVuInterception interception) {
        return interception.getReturnType();
    }
}
