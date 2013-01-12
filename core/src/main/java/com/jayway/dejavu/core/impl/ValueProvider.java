package com.jayway.dejavu.core.impl;

import com.jayway.dejavu.core.Trace;
import com.jayway.dejavu.core.TraceEndedException;
import com.jayway.dejavu.core.TracedElement;
import com.jayway.dejavu.core.value.ExceptionValue;
import com.jayway.dejavu.core.value.Value;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ValueProvider {

    private List<Value> values;
    private int index;

    public ValueProvider( Trace trace ) {
        values = new ArrayList<Value>( trace.getTracedElements().size() );
        for (TracedElement element : trace.getTracedElements()) {
            values.add( element.getValue() );
        }
        index = 0;
    }
    public ValueProvider( List<Value> values ) {
        this.values = values;
        index = 0;
    }

    public Value getNext() {
        checkIndex();
        Value value = values.get(index);
        if ( value instanceof ExceptionValue ) {
            ExceptionValue val = (ExceptionValue) value;
            RuntimeException e;

            try {
                // 1. try to construct with message
                Constructor<?> constructor = Class.forName(val.getValue()).getConstructor(String.class);
                e = (RuntimeException) constructor.newInstance( val.getMessage() );
            } catch (Exception exception ) {
                try {
                    // 2. try default constructor
                    e = (RuntimeException) Class.forName( val.getValue() ).newInstance();
                } catch (Exception ee ) {
                    // 3. throw generic runtime exception
                    throw new RuntimeException("Original Exception: "+val.getValue() + ". Message: "+val.getMessage());
                }
            }
            throw e;
        }
        index++;
        return value;
    }

    private void checkIndex() {
        if (index >= values.size()) {
            throw new TraceEndedException();
        }
    }
}
