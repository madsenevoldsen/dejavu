package com.jayway.dejavu.core.exception;

public class CircuitOpenException extends RuntimeException {

    public CircuitOpenException( String message ) {
        super(message);
    }
}
