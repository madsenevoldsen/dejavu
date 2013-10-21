package com.jayway.dejavu.core.memorytrace;

import com.jayway.dejavu.core.interfaces.Trace;
import com.jayway.dejavu.core.TraceElement;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class MemoryTrace implements Trace {
    private String id;
    private List<TraceElement> values;
    private Method startPoint;
    private Object[] startArguments;

    public MemoryTrace( String id, List<TraceElement> values, Method startPoint, Object[] startArguments  ) {
        this.id = id;
        if ( values == null ) {
            this.values = Collections.EMPTY_LIST;
        } else {
            this.values = values;
        }
        this.startPoint = startPoint;
        this.startArguments = startArguments;
    }

    public Method getStartPoint() {
        return startPoint;
    }

    public Object[] getStartArguments() {
        return startArguments;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Iterator<TraceElement> iterator() {
        return values.iterator();
    }
}
