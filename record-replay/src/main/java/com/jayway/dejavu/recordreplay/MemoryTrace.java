package com.jayway.dejavu.recordreplay;

import com.jayway.dejavu.core.Trace;
import com.jayway.dejavu.core.TraceElement;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MemoryTrace implements Trace {
    private String id;
    private List<TraceElement> values;
    private Method startPoint;
    private Object[] startArguments;

    public MemoryTrace( Method startPoint, Object[] startArguments ) {
        values = new ArrayList<TraceElement>();
        this.startPoint = startPoint;
        this.startArguments = startArguments;
    }

    public Method getStartPoint() {
        return startPoint;
    }

    @Override
    public void setStartPoint(Method startPoint) {
        this.startPoint = startPoint;
    }

    public Object[] getStartArguments() {
        return startArguments;
    }

    @Override
    public void setStartArguments(Object[] startArguments) {
        this.startArguments = startArguments;
    }

    @Override
    public TraceElement get( int index ) {
        return values.get( index );
    }

    @Override
    public void add( TraceElement element ) {
        values.add(element);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int impureValueCount() {
        return values.size();
    }

    @Override
    public Iterator<TraceElement> iterator() {
        return values.iterator();
    }
}
