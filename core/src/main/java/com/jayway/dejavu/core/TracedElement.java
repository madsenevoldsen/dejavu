package com.jayway.dejavu.core;

import com.jayway.dejavu.core.value.Value;

public class TracedElement {
    private Class<?> clazz;
    private Value value;

    public TracedElement( Class<?> clazz, Value value ) {
        this.clazz = clazz;
        this.value = value;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Value getValue() {
        return value;
    }
}
