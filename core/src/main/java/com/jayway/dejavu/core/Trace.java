package com.jayway.dejavu.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Trace {
    private String id;
    private List<TraceElement> values;
    private Method startPoint;
    private Object[] startArguments;

    public Trace() {
        values = new ArrayList<TraceElement>();
    }

    public Trace( String id, Method startPoint, Object[] startArguments ) {
        this();
        this.id = id;
        this.startPoint = startPoint;
        this.startArguments = startArguments;
    }

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

    public List<TraceElement> getValues() {
        return values;
    }

    public void setValues(List<TraceElement> values) {
        this.values = values;
    }

    public void addValue( TraceElement element ) {
        values.add( element );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
