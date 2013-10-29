package com.jayway.dejavu.circuitbreaker;

import com.jayway.dejavu.core.ThrownThrowable;
import com.jayway.dejavu.core.interfaces.AroundImpure;
import com.jayway.dejavu.core.interfaces.Interception;

public class CircuitBreakerImpureHandler implements AroundImpure {

    @Override
    public Object proceed(Interception interception) throws Throwable {
        CircuitBreakerWrapper cb = new CircuitBreakerWrapper(interception.integrationPoint());
        cb.verify();
        Object result = interception.proceed();

        if ( result instanceof ThrownThrowable ) {
            cb.failure(((ThrownThrowable) result).getThrowable());
        } else {
            cb.success();
        }

        return result;
    }
}
