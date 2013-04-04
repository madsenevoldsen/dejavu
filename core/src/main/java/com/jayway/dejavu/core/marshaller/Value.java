package com.jayway.dejavu.core.marshaller;

public class Value {
    private Class<?> clazz;
    private String serialValue;

    public Value( Class<?> clazz, String serialValue ) {
        this.clazz = clazz;
        this.serialValue = serialValue;
    }

    public String getSerialValue() {
        return serialValue;
    }

    public void setSerialValue(String serialValue) {
        this.serialValue = serialValue;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }
}
