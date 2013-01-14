package com.jayway.dejavu.core;

import com.jayway.dejavu.core.repository.TraceRepository;
import com.jayway.dejavu.core.value.Value;

import java.util.HashMap;
import java.util.Map;

public class UseCaseRunner {

    public UseCaseRunner() {
        providers = new HashMap<String, Class<? extends Provider>>();
    }

    private TraceRepository traceRepository;

    protected UseCaseTracer tracedUseCase(Class<? extends UseCase> clazz, Value value) {
        return new UseCaseTracer(clazz, this, value);
    }

    public <T> T run( Class<? extends UseCase> clazz, Value input ) {
        UseCaseTracer tracer = tracedUseCase( clazz, input );
        try {
            Object output = tracer.getUseCase().run(input);
            traceRepository.storeTrace( null, new Trace(tracer.getTrace(),tracer.getUseCase().getClass() ));
            return (T) output;
        } catch ( RuntimeException e) {
            Trace trace = new Trace( tracer.getTrace(), tracer.getUseCase().getClass() );
            traceRepository.storeTrace( e, trace );
            throw e;
        }
    }

    public void setTraceRepository( TraceRepository traceRepository ) {
        this.traceRepository = traceRepository;
    }
    public void addProvider( Class<? extends Provider> clazz ) {
        addProvider(clazz.getSimpleName(), clazz);
    }
    public void addProvider( String name, Class<? extends Provider> clazz ) {
        if ( providers.containsKey( name ) ) throw new InitializationException("A provider is already registered with name: "+name);
        providers.put(name, clazz);
    }
    private Map<String, Class<? extends Provider>> providers;

    protected Class<? extends Provider> provider( String name ) {
        Class<? extends Provider> aClass = providers.get(name);
        if ( aClass == null ) throw new NotFoundException( "Could not find a provider named: "+name );
        return aClass;
    }
}
