package com.jayway.dejavu.value;

public class BooleanValue implements Value {
    private Boolean value;
    public BooleanValue( Boolean value ) {
        this.value = value;
    }

    public Boolean getValue() {
        return value;
    }
}
