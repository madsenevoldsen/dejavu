package com.jayway.dejavu.core;

public class CouldNotFindProviderException extends RuntimeException {

    public CouldNotFindProviderException( String provider ) {
        super("Could not find a provider named: "+provider);
    }
}
