package com.jayway.dejavu.core;

import com.jayway.dejavu.core.exception.CircuitOpenException;

class CircuitBreakerWrapper {

    private String integrationPoint;
    private CircuitBreaker breaker;

    CircuitBreakerWrapper( String integrationPoint ) {
        this.integrationPoint = integrationPoint;
    }

    void verify() {
        if ( !integrationPoint.isEmpty() ) {
            // a circuit breaker is guarding this call
            breaker = DejaVuPolicy.getCircuitBreaker(integrationPoint);
            if ( breaker.isOpen() ) {
                throw new CircuitOpenException( "Circuit breaker '"+integrationPoint+"' is open");
            }
        }
    }


    public void result(Object result) {
        if ( breaker != null ) {
            if ( result instanceof ThrownThrowable ) {
                breaker.exceptionOccurred(((ThrownThrowable) result).getThrowable());
            } else {
                breaker.success();
            }
        }
    }
}
