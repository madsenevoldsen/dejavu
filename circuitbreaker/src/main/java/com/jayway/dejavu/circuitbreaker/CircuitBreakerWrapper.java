package com.jayway.dejavu.circuitbreaker;

class CircuitBreakerWrapper {

    private String integrationPoint;
    private CircuitBreaker breaker;

    CircuitBreakerWrapper( String integrationPoint ) {
        this.integrationPoint = integrationPoint;
    }

    void verify() {
        if ( !integrationPoint.isEmpty() ) {
            // a circuit breaker is guarding this call
            breaker = CircuitBreakerPolicy.getCircuitBreaker(integrationPoint);
            if ( breaker.isOpen() ) {
                throw new CircuitOpenException( "Circuit breaker '"+integrationPoint+"' is open");
            }
        }
    }

    public void success() {
        if ( breaker != null ) {
            breaker.success();
        }
    }

    public void failure(Throwable t) {
        if ( breaker != null ) {
            breaker.exceptionOccurred(t);
        }
    }
}
