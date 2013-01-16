package com.jayway.dejavu.circuitbreaker.impl;

import com.jayway.dejavu.circuitbreaker.CircuitBreakerUseCaseRunner;
import com.jayway.dejavu.circuitbreaker.CircuitBreakerHandler;
import com.jayway.dejavu.core.Trace;
import com.jayway.dejavu.core.UseCase;
import com.jayway.dejavu.core.repository.TraceRepository;
import com.jayway.dejavu.core.value.Value;

public class UseCaseSetup implements TraceRepository {

    private CircuitBreakerUseCaseRunner runner;
    private Trace trace;

    public UseCaseSetup( CircuitBreakerHandler handler ) {
        runner = new CircuitBreakerUseCaseRunner();

        runner.addProvider( CircuitBrokenProvider.class );
        runner.addProvider( NormalProvider.class );

        runner.addCircuitBreakerHandler( handler );

        runner.setTraceRepository(this);
    }


    public <I extends Value,O> O run( Class<? extends UseCase<I,O>> clazz, I input ) {
        return runner.run( clazz, input );
    }

    @Override
    public void storeTrace(RuntimeException e, Trace trace) {
        this.trace = trace;
    }

    public Trace getTrace() {
        return trace;
    }
}
