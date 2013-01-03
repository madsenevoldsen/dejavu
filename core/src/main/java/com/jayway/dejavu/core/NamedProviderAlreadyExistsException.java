package com.jayway.dejavu.core;

public class NamedProviderAlreadyExistsException extends RuntimeException {

    public NamedProviderAlreadyExistsException(String provider) {
        super("A provider is already registered with name: "+provider);
    }
}
