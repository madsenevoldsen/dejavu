package com.jayway.dejavu.core.typeinference;

import org.aspectj.lang.reflect.MethodSignature;

public class DefaultInference implements TypeInference {

    @Override
    public Class<?> inferType(Object instance, MethodSignature signature) {
        return signature.getReturnType();
    }
}
