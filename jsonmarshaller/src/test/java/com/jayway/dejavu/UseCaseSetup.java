package com.jayway.dejavu;

import com.jayway.dejavu.core.Trace;
import com.jayway.dejavu.core.UseCase;
import com.jayway.dejavu.core.UseCaseRunner;
import com.jayway.dejavu.repository.TraceRepository;
import com.jayway.dejavu.value.Value;

public class UseCaseSetup implements TraceRepository {

    private UseCaseRunner runner;
    private Trace trace;

    public UseCaseSetup() {
        runner = new UseCaseRunner();

        runner.addProvider( RandomUUID.class);
        runner.addProvider( Timestamp.class);

        runner.setTraceRepository(this);
    }

    public <I extends Value,O> O run( Class<? extends UseCase<I,O>> clazz, I input ) {
        return runner.run( clazz, input );
    }

    @Override
    public void storeTrace(Trace trace) {
        this.trace = trace;
    }

    public Trace getTrace() {
        return trace;
    }
}
