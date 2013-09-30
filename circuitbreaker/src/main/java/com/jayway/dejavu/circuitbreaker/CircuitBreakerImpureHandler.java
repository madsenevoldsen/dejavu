package com.jayway.dejavu.circuitbreaker;

import com.jayway.dejavu.core.ImpureHandler;
import com.jayway.dejavu.core.RunningTrace;

public class CircuitBreakerImpureHandler implements ImpureHandler {

    private ThreadLocal<CircuitBreakerWrapper> threadLocal = new ThreadLocal<CircuitBreakerWrapper>();

    @Override
    public void before(RunningTrace runningTrace, String integrationPoint) {
        CircuitBreakerWrapper cb = new CircuitBreakerWrapper(integrationPoint);
        threadLocal.set(cb);
        cb.verify();
    }

    @Override
    public void success(RunningTrace runningTrace, Object result, Class returnType) {
        threadLocal.get().success();
        threadLocal.remove();
    }

    @Override
    public void failure(RunningTrace runningTrace, Throwable t) {
        threadLocal.get().failure(t);
        threadLocal.remove();
    }
}
