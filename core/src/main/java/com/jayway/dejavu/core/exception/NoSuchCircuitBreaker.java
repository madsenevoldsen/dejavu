package com.jayway.dejavu.core.exception;

public class NoSuchCircuitBreaker extends RuntimeException {

    public NoSuchCircuitBreaker(String message) {
        super(message);
    }
}
