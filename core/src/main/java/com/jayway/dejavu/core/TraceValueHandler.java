package com.jayway.dejavu.core;

// handler chain for the traced values
public interface TraceValueHandler {

    Object record(Object value);

    Object replay(Object value);
}
