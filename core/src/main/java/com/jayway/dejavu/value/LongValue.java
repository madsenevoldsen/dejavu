package com.jayway.dejavu.value;

public class LongValue implements Value {
    private Long value;
    public LongValue(Long value) {
        this.value = value;
    }

    public Long getValue() {
        return value;
    }
}
