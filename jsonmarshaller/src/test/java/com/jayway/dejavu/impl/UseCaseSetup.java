package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.Trace;
import com.jayway.dejavu.core.UseCase;
import com.jayway.dejavu.core.UseCaseRunner;
import com.jayway.dejavu.core.repository.TraceRepository;
import com.jayway.dejavu.core.value.Value;

public class UseCaseSetup implements TraceRepository {

    private UseCaseRunner runner;
    private Trace trace;

    public UseCaseSetup() {
        runner = new UseCaseRunner();

        runner.addProvider( RandomUUID.class);
        runner.addProvider( Timestamp.class);
        runner.addProvider( ExceptingProvider.class );

        runner.setTraceRepository(this);
    }

    public <I extends Value,O> O run( Class<? extends UseCase<I,O>> clazz, I input ) {
        return runner.run( clazz, input );
    }

    @Override
    public void storeTrace( RuntimeException e, Trace trace) {
        this.trace = trace;
    }

    public Trace getTrace() {
        return trace;
    }
}
