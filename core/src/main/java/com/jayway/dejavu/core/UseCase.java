package com.jayway.dejavu.core;

import com.jayway.dejavu.core.impl.Tracer;
import com.jayway.dejavu.core.value.Value;

public abstract class UseCase<Input extends Value,Output> {

    private Tracer tracer;
    void setTracer( Tracer tracer ) {
        this.tracer = tracer;
    }

    protected <T> T wireDependencies( T t ) {
        return tracer.wireDependencies( t );
    }
    protected Tracer getTracer() {
        return tracer;
    }

    abstract public Output run(Input input);
}
