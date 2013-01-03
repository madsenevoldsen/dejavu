package com.jayway.dejavu.value;

public class IntegerValue implements Value {
    private Integer value;
    public IntegerValue(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
