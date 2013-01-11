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

    protected <Input extends Value,Output> TracedUseCase<Input,Output> tracedUseCase(Class<? extends UseCase<Input,Output>> clazz) {
        return new TracedUseCase<Input, Output>(clazz, this);
    }

    public <Input extends Value,Output> Output run( Class<? extends UseCase<Input,Output>> clazz, Input input ) {
        TracedUseCase<Input, Output> useCase = tracedUseCase( clazz );
        try {
            Output output = useCase.run(input);
            traceRepository.storeTrace( null, new Trace(useCase.getTrace(),(Class<? extends UseCase<?,?>>)useCase.getUseCase().getClass() ));
            return output;
        } catch ( RuntimeException e) {
            Trace trace = new Trace( useCase.getTrace(), (Class<? extends UseCase<?,?>>)useCase.getUseCase().getClass() );
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
