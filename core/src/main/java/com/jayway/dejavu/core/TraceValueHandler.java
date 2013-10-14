package com.jayway.dejavu.core;

// handler chain for the traced values
public interface TraceValueHandler {

    Object handle( Object value);
}
