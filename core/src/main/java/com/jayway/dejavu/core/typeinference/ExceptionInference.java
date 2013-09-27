package com.jayway.dejavu.core.typeinference;

import com.jayway.dejavu.core.DejaVuInterception;
import com.jayway.dejavu.core.ThrownThrowable;
import org.aspectj.lang.reflect.MethodSignature;

public class ExceptionInference implements TypeInference {

    @Override
    public Class<?> inferType(Object instance, DejaVuInterception interception) {
        if ( instance instanceof ThrownThrowable ) {
            return ThrownThrowable.class;
        }
        return null;
    }
}
