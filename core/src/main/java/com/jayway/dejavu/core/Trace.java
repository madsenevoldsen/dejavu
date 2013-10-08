package com.jayway.dejavu.core;

import java.lang.reflect.Method;

public interface Trace extends Iterable<TraceElement> {

    public void setStartPoint(Method startPoint);

    public Method getStartPoint();

    public void setStartArguments(Object[] startArguments);

    public Object[] getStartArguments();

    public TraceElement get( int index );

    public void add( TraceElement element );

    public String getId();

    public void setId(String id);

    public int impureValueCount();
}
