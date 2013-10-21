package com.jayway.dejavu.circuitbreaker;

import com.jayway.dejavu.core.interfaces.ImpureHandler;

public class CircuitBreakerImpureHandler implements ImpureHandler {

    private ThreadLocal<CircuitBreakerWrapper> threadLocal = new ThreadLocal<CircuitBreakerWrapper>();

    @Override
    public void before(String integrationPoint) {
        CircuitBreakerWrapper cb = new CircuitBreakerWrapper(integrationPoint);
        threadLocal.set(cb);
        cb.verify();
    }

    @Override
    public void after(Object result) {
        CircuitBreakerWrapper wrapper = threadLocal.get();
        if ( result instanceof Throwable ) {
            wrapper.failure((Throwable) result);
        } else {
            wrapper.success();
        }
    }
}
