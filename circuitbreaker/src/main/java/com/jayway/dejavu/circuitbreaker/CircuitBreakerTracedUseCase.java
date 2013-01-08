package com.jayway.dejavu.circuitbreaker;

import com.jayway.dejavu.circuitbreaker.annotation.CircuitBreaker;
import com.jayway.dejavu.circuitbreaker.impl.CircuitBreakerProvider;
import com.jayway.dejavu.core.Provider;
import com.jayway.dejavu.core.TracedUseCase;
import com.jayway.dejavu.core.UseCase;
import com.jayway.dejavu.core.value.Value;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class CircuitBreakerTracedUseCase<Input extends Value, Output> extends TracedUseCase<Input, Output> {

    private CircuitBreakerUseCaseRunner cbRunner;

    public CircuitBreakerTracedUseCase(Class<? extends UseCase<Input, Output>> clazz, CircuitBreakerUseCaseRunner runner ) {
        super(clazz, runner);
        this.cbRunner = runner;
    }

    @Override
    protected Provider getFieldDecoration(Field field) throws IllegalAccessException, InstantiationException {
        Provider provider = super.getFieldDecoration(field);

        for ( Annotation providerAnnotation : provider.getClass().getAnnotations() ) {
            if (CircuitBreaker.class.isAssignableFrom( providerAnnotation.getClass())) {
                CircuitBreaker cbAnnotation = (CircuitBreaker) providerAnnotation;
                CircuitBreakerHandler breaker = cbRunner.getCircuitBreakerHandler(cbAnnotation.value());
                return new CircuitBreakerProvider( provider, breaker, this );
            }
        }
        return provider;
    }
}
