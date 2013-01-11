package com.jayway.dejavu.core;

import java.util.Collections;
import java.util.List;

public class Trace {
    private List<TracedElement> tracedElements;
    private Class<? extends UseCase<?,?>> useCaseClass;

    public Trace(List<TracedElement> tracedElements, Class<? extends UseCase<?,?>> useCaseClass ) {
        this.tracedElements = tracedElements;
        this.useCaseClass = useCaseClass;
    }
    public List<TracedElement> getTracedElements() {
        return Collections.unmodifiableList(tracedElements);
    }
    public Class<? extends UseCase<?, ?>> getUseCaseClass() {
        return useCaseClass;
    }
}
