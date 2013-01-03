package com.jayway.dejavu.core.impl;

import com.jayway.dejavu.core.TraceEndedException;
import com.jayway.dejavu.core.TracedElement;
import com.jayway.dejavu.value.Value;

import java.util.List;

public class ValueProvider {

    private List<TracedElement> tracedElements;
    private int index;

    public ValueProvider( List<TracedElement> tracedElements ) {
        this.tracedElements = tracedElements;
        index = 0;
    }

    public Value getNext() {
        checkIndex();
        Value value = tracedElements.get(index).getValue();
        index++;
        return value;
    }

    private void checkIndex() {
        if (index >= tracedElements.size()) {
            throw new TraceEndedException();
        }
    }
}
