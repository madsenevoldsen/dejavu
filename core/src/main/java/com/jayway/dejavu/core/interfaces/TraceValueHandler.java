package com.jayway.dejavu.core.interfaces;

// handler chain for the traced values
public interface TraceValueHandler {

    Object handle( Object value);
}
