package com.jayway.dejavu.core;

public class InitializationException extends RuntimeException {

    public InitializationException(String provider) {
        super("A provider is already registered with name: "+provider);
    }
}
