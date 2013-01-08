package com.jayway.dejavu.core;

import com.jayway.dejavu.core.repository.TraceRepository;
import com.jayway.dejavu.core.repository.UseCaseTestRepository;
import com.jayway.dejavu.core.value.Value;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UseCaseRunner {

    public UseCaseRunner() {
        providers = new HashMap<String, Class<? extends Provider>>();
        exceptionWhiteList = new HashSet<String>();
    }

    private TraceRepository traceRepository;
    private UseCaseTestRepository useCaseTestRepository;

    protected <Input extends Value,Output> TracedUseCase<Input,Output> tracedUseCase(Class<? extends UseCase<Input,Output>> clazz) {
        return new TracedUseCase<Input, Output>(clazz, this);
    }

    public <Input extends Value,Output> Output run( Class<? extends UseCase<Input,Output>> clazz, Input input ) {
        TracedUseCase<Input, Output> useCase = tracedUseCase( clazz );
        try {
            Output output = useCase.run(input);
            if ( useCaseTestRepository != null && useCaseTestRepository.isTraceEnabled( clazz )) {
                Trace trace = new Trace(useCase.getTrace(), (Class<? extends UseCase<?, ?>>) useCase.getUseCase().getClass());
                trace.setIsIntegrationTest( true );
                useCaseTestRepository.addTrace( trace );
                useCaseTestRepository.setEnableTraceForUseCase( clazz, false );
            }
            return output;
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
        if ( providers.containsKey( name ) ) throw new InitializationException("A provider is already registered with name: "+name);
        providers.put( name, clazz );
    }
    public void addException( Class<? extends Exception> clazz ) {
        exceptionWhiteList.add( clazz.getCanonicalName() );
    }
    private Map<String, Class<? extends Provider>> providers;
    private Set<String> exceptionWhiteList;

    protected Class<? extends Provider> provider( String name ) {
        Class<? extends Provider> aClass = providers.get(name);
        if ( aClass == null ) throw new NotFoundException( "Could not find a provider named: "+name );
        return aClass;
    }
    private boolean isWhitelisted( Exception e ) {
        return exceptionWhiteList.contains(e.getClass().getCanonicalName());
    }

    public UseCaseTestRepository getUseCaseTestRepository() {
        return useCaseTestRepository;
    }

    public void setUseCaseTestRepository(UseCaseTestRepository useCaseTestRepository) {
        this.useCaseTestRepository = useCaseTestRepository;
    }
}
