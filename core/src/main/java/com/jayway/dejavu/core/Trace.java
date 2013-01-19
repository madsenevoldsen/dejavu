package com.jayway.dejavu.core;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

public class Trace {
    private List<Object> values;
    private Method startPoint;
    private Object[] startArguments;

    public Method getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Method startPoint) {
        this.startPoint = startPoint;
    }

    public Object[] getStartArguments() {
        return startArguments;
    }

    public void setStartArguments(Object[] startArguments) {
        this.startArguments = startArguments;
    }

    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }
}
