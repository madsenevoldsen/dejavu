package com.jayway.dejavu.core.value;

public class IntegerValue implements Value {
    private Integer value;
    public IntegerValue() {}
    public IntegerValue(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
