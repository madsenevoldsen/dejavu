package com.jayway.dejavu.core;

public class TraceValueHandlerAdapter implements TraceValueHandler {

    @Override
    public Object record(Object value) {
        return value;
    }

    @Override
    public Object replay(Object value) {
        return value;
    }
}
