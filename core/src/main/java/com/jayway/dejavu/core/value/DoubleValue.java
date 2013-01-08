package com.jayway.dejavu.core.value;

public class DoubleValue implements Value {
    private Double value;
    public DoubleValue() {}
    public DoubleValue(Double value) {
        this.value = value;
    }

    public Double getValue() {
        return value;
    }
}
