package com.jayway.dejavu.core.impl;

import com.jayway.dejavu.core.Provider;
import com.jayway.dejavu.repository.CircuitBreakerRepository;
import com.jayway.dejavu.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class CircuitBreakerProvider<I,O extends Value> implements Provider<I,O> {
    private Logger log = LoggerFactory.getLogger(CircuitBreakerProvider.class);

    private Provider<I,O> provider;
    private CircuitBreakerRepository repository;

    public CircuitBreakerProvider( Provider<I,O> provider, CircuitBreakerRepository repository) {
        this.provider = provider;
        this.repository = repository;
    }

    @Override
    public O request(I input) {
        O result = null;
        handleBlackout();
        if ( !repository.isBlackout() ) {
            try {
                result = provider.request( input );
                forcePowerOn();
                repository.setCurrentException( null );
            } catch( Exception e ) {
                handleFuseBlown( e );
            }
        }
        return result;
    }

    private void handleBlackout() {
        int seconds = repository.getBlackOutSeconds();
        Date lastError = repository.getExceptionTime();
        boolean blackout = repository.isBlackout();
        if (blackout && new Date().after(new Date(lastError.getTime() + (seconds * 1000)))) {
            repository.setBlackout( false );
        }
    }

    private void handleFuseBlown( Exception e ) {
        String message = String.format("Circuit breaker '%s' broken for %d seconds", repository.getName(), repository.getBlackOutSeconds());
        log.error(message, e);
        repository.setCurrentException(e);
        repository.setExceptionTime( new Date());
        repository.setBlackout( true );
    }

    public void forcePowerOn() {
        repository.setBlackout( false );
    }
}
