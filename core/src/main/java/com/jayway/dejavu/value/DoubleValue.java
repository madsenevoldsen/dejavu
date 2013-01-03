package com.jayway.dejavu.value;

public class DoubleValue implements Value {
    private Double value;
    public DoubleValue(Double value) {
        this.value = value;
    }

    public Double getValue() {
        return value;
    }
}
