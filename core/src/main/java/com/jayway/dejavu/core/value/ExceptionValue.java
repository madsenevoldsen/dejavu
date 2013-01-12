package com.jayway.dejavu.core.value;

public class ExceptionValue implements Value {
    private String value;
    private String message;
    public ExceptionValue() {}
    public ExceptionValue(String value, String message ) {
        this.value = value;
        this.message = message;
    }

    public String getValue() {
        return value;
    }
    public String getMessage() {
        return message;
    }
}
