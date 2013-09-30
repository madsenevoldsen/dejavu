package com.jayway.dejavu.circuitbreaker;

public class CircuitOpenException extends RuntimeException {

    public CircuitOpenException( String message ) {
        super(message);
    }
}
