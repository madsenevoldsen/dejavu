package com.jayway.dejavu.circuitbreaker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class CircuitBreakerHandler {

    private Logger log = LoggerFactory.getLogger(CircuitBreakerHandler.class);

    private enum State {
        Closed, // calls passes through
        Half_open, // let is pass but depending on the result either close or open
        Open // throw exception
    }

    private int exceptionThreshold;
    private int timeoutMilliSeconds;
    private State state;
    private String name;
    private int exceptionCount;
    private Date openTime;

    public CircuitBreakerHandler(String name, int timeoutMilliSeconds, int exceptionThreshold) {
        state = State.Closed;
        this.timeoutMilliSeconds = timeoutMilliSeconds;
        this.exceptionThreshold = exceptionThreshold;
        this.name = name;
        exceptionCount = 0;
    }

    public String getName() {
        return name;
    }

    public String getState() {
        ensureState();
        return state.name();
    }

    public void forceClose() {
        state = State.Closed;
    }

    public void success() {
        exceptionCount=0;
        state = State.Closed;
    }

    public void exceptionOccurred( RuntimeException e ) {
        exceptionCount++;

        if ( state == State.Half_open || exceptionCount >= exceptionThreshold ) {
            state = State.Open;
            openTime = new Date();
            String message = String.format("Circuit breaker '%s' open for %d seconds", name, timeoutMilliSeconds );
            log.error( message, e );
        } else {
            log.error( String.format("Circuit breaker '%s' got exception but still closed", name), e);
        }
    }

    private void ensureState() {
        if ( state == State.Open ) {
            // are we passed the timeout?
            if (new Date().after(new Date(openTime.getTime() + timeoutMilliSeconds))) {
                state = State.Half_open;
            }
        }
    }
}
