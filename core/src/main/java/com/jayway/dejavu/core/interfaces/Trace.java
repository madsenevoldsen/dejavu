package com.jayway.dejavu.core.interfaces;

import com.jayway.dejavu.core.TraceElement;

import java.lang.reflect.Method;

public interface Trace extends Iterable<TraceElement> {

    public String getId();

    public Method getStartPoint();

    public Object[] getStartArguments();
}
