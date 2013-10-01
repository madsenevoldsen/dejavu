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
    public Object success(RunningTrace runningTrace, Object result) {
        threadLocal.get().success();
        threadLocal.remove();
        return result;
    }

    @Override
    public Throwable failure(RunningTrace runningTrace, Throwable t) {
        threadLocal.get().failure(t);
        threadLocal.remove();
        return t;
    }
}
