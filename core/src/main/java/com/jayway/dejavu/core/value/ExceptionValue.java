package com.jayway.dejavu.core.value;

public class ExceptionValue implements Value {
    private String value;
    public ExceptionValue() {}
    public ExceptionValue(String value ) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
