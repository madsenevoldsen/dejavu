package com.jayway.dejavu.core.value;

public class BooleanValue implements Value {
    private Boolean value;
    public BooleanValue() {}
    public BooleanValue( Boolean value ) {
        this.value = value;
    }

    public Boolean getValue() {
        return value;
    }
}
