package com.jayway.dejavu.core;

import com.jayway.dejavu.core.value.Value;

public abstract class UseCase<Input extends Value,Output> {

    private Tracer tracer;
    void setTracer( Tracer tracer ) {
        this.tracer = tracer;
    }

    public <I,O> O run( Class<? extends Step<I,O>> clazz, I input ) {
        return tracer.step(clazz, input);
    }

    abstract public Output run(Input input);
}
