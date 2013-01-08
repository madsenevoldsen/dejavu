package com.jayway.dejavu.core.value;

public class StringValue implements Value {
    private String string;
    public StringValue() {}
    public StringValue( String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }
}
