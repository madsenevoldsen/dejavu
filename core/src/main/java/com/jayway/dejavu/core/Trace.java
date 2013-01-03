package com.jayway.dejavu.core;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Trace {
    private String id;
    private Date time;
    private List<TracedElement> tracedElements;
    private Class<? extends UseCase<?,?>> useCaseClass;
    private Boolean resolved;

    public Trace(List<TracedElement> tracedElements, Class<? extends UseCase<?,?>> useCaseClass ) {
        id = UUID.randomUUID().toString();
        time = new Date();
        this.tracedElements = tracedElements;
        this.useCaseClass = useCaseClass;
        resolved = false;
    }
    public Date getTime() {
        return new Date( time.getTime() );
    }

    public List<TracedElement> getTracedElements() {
        return Collections.unmodifiableList(tracedElements);
    }

    public Class<? extends UseCase<?, ?>> getUseCaseClass() {
        return useCaseClass;
    }

    public Boolean getResolved() {
        return resolved;
    }

    public void setResolved(Boolean resolved) {
        this.resolved = resolved;
    }

    public String getId() {
        return id;
    }
}
