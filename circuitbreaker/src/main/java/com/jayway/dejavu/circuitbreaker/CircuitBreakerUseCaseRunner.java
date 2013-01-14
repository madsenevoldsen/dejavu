package com.jayway.dejavu.circuitbreaker;

import com.jayway.dejavu.core.*;
import com.jayway.dejavu.core.value.Value;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CircuitBreakerUseCaseRunner extends UseCaseRunner {

    private Map<String, CircuitBreakerHandler> circuitBreakers;
    public CircuitBreakerUseCaseRunner() {
        this.circuitBreakers = new HashMap<String, CircuitBreakerHandler>();
    }

    @Override
    protected UseCaseTracer tracedUseCase(Class<? extends UseCase> clazz, Value value) {
        return new CircuitBreakerUseCaseTracer( clazz, this, value );
    }

    public void addCircuitBreakerHandler( CircuitBreakerHandler handler ) {
        if ( circuitBreakers.containsKey( handler.getName() )) throw new InitializationException("A circuit breaker handler is already registered with name: "+handler.getName() );
        circuitBreakers.put( handler.getName(), handler );
    }
    public CircuitBreakerHandler getCircuitBreakerHandler( String name ) {
        CircuitBreakerHandler handler = circuitBreakers.get(name);
        if ( handler == null ) {
            throw new NotFoundException( "Could not find circuit breaker handler named: "+name);
        }
        return handler;
    }

    public Map<String,CircuitBreakerHandler> getCircuitBreakers() {
        return Collections.unmodifiableMap(circuitBreakers);
    }
}
