package com.jayway.dejavu.core;

import com.jayway.dejavu.repository.CircuitBreakerRepository;
import com.jayway.dejavu.repository.TraceRepository;
import com.jayway.dejavu.value.Value;

import java.util.*;

public class UseCaseRunner {

    public UseCaseRunner() {
        providers = new HashMap<String, Class<? extends Provider>>();
        circuitBreakers = new HashMap<String, Class<? extends CircuitBreakerRepository>>();
        exceptionWhiteList = new HashSet<String>();
    }

    public Map<String, Class<? extends CircuitBreakerRepository>> getCircuitBreakers() {
        return Collections.unmodifiableMap( circuitBreakers );
    }

    private TraceRepository traceRepository;

    public <Input extends Value,Output> Output run( Class<? extends UseCase<Input,Output>> clazz, Input input ) {
        TracedUseCase<Input, Output> useCase = new TracedUseCase<Input, Output>(clazz, this);
        try {
            return useCase.run(input);
        } catch ( RuntimeException e) {
            if ( !isWhitelisted( e ) ) {
                // this is an unexpected exception
                Trace trace = new Trace(useCase.getTrace(), (Class<? extends UseCase<?,?>>)useCase.getUseCase().getClass() );
                traceRepository.storeTrace( trace );
            }
            throw e;
        }
    }
    public void setTraceRepository( TraceRepository traceRepository ) {
        this.traceRepository = traceRepository;
    }
    public void addProvider( Class<? extends Provider> clazz ) {
        addProvider( clazz.getSimpleName(), clazz );
    }
    public void addProvider( String name, Class<? extends Provider> clazz ) {
        if ( providers.containsKey( name ) ) throw new NamedProviderAlreadyExistsException( name );
        providers.put( name, clazz );
    }
    public void addException( Class<? extends Exception> clazz ) {
        exceptionWhiteList.add( clazz.getCanonicalName() );
    }
    public void addCircuitBreaker( String name, Class<? extends CircuitBreakerRepository> clazz ) {
        circuitBreakers.put( name, clazz );
    }
    private Map<String, Class<? extends Provider>> providers;
    private Map<String, Class<? extends CircuitBreakerRepository>> circuitBreakers;
    private Set<String> exceptionWhiteList;

    protected Class<? extends CircuitBreakerRepository> circuitBreaker( String name ) {
        return circuitBreakers.get( name );
    }
    protected Class<? extends Provider> provider( String name ) {
        Class<? extends Provider> aClass = providers.get(name);
        if ( aClass == null ) throw new CouldNotFindProviderException( name );
        return aClass;
    }
    private boolean isWhitelisted( Exception e ) {
        return exceptionWhiteList.contains(e.getClass().getCanonicalName());
    }
}
