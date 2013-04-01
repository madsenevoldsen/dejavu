package com.jayway.dejavu.core.typeinference;

import com.jayway.dejavu.core.ThrownThrowable;
import org.aspectj.lang.reflect.MethodSignature;

public class ExceptionInference implements TypeInference {

    @Override
    public Class<?> inferType(Object instance, MethodSignature signature) {
        if ( instance instanceof ThrownThrowable ) {
            return ThrownThrowable.class;
        }
        return null;
    }
}
