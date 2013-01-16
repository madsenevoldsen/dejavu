package com.jayway.dejavu.circuitbreaker;

import com.jayway.dejavu.circuitbreaker.annotation.CircuitBreaker;
import com.jayway.dejavu.core.Provider;
import com.jayway.dejavu.core.UseCase;
import com.jayway.dejavu.core.UseCaseTracer;
import com.jayway.dejavu.core.impl.CircuitBreakerProvider;
import com.jayway.dejavu.core.impl.TracedProvider;
import com.jayway.dejavu.core.value.Value;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class CircuitBreakerUseCaseTracer extends UseCaseTracer {

    public CircuitBreakerUseCaseTracer(Class<? extends UseCase> clazz, CircuitBreakerUseCaseRunner runner, Value input) {
        super(clazz, runner, input );
    }

    private CircuitBreakerUseCaseRunner getRunner() {
        return (CircuitBreakerUseCaseRunner) runner;
    }

    @Override
    protected Provider addDecoration(Provider provider) {
        return provider;
    }

    @Override
    protected Provider getFieldProvider(Field field) throws IllegalAccessException, InstantiationException {
        Provider provider = super.getFieldProvider(field);

        if ( provider != null ) {
            for ( Annotation providerAnnotation : provider.getClass().getAnnotations() ) {
                if (CircuitBreaker.class.isAssignableFrom( providerAnnotation.getClass())) {
                    CircuitBreaker cbAnnotation = (CircuitBreaker) providerAnnotation;
                    CircuitBreakerHandler breaker = getRunner().getCircuitBreakerHandler(cbAnnotation.value());
                    return new CircuitBreakerProvider( new TracedProvider( this, provider), breaker, this );
                }
            }
            // for circuit breakers the decoration is added here
            return new TracedProvider( this, provider);
        }

        return null;
    }
}
