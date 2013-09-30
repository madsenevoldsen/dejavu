package com.jayway.dejavu.circuitbreaker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Circuit breaker guarding the call to @Impure methods.
 *
 * Configured by name, timeout, and exception threshold.
 *
 * Example:
 *
 * \@Impure( integrationPoint = "mySql" )
 * public void connect() {
 *     // setup connection to database
 * }
 *
 * public void setup() {
 *     DejaVuAspect.addCircuitBreaker( "mySql", 1000, 2 );
 * }
 *
 */
public class CircuitBreaker {

    private Logger log = LoggerFactory.getLogger(CircuitBreaker.class);

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

    public CircuitBreaker(String name, int timeoutMilliSeconds, int exceptionThreshold) {
        state = State.Closed;
        this.timeoutMilliSeconds = timeoutMilliSeconds;
        this.exceptionThreshold = exceptionThreshold;
        this.name = name;
        exceptionCount = 0;
    }

    public String getName() {
        return name;
    }

    public boolean isOpen() {
        ensureState();
        return state == State.Open;
    }
    public boolean isClosed() {
        ensureState();
        return state == State.Closed;
    }
    public boolean isHalfOpen() {
        ensureState();
        return state == State.Half_open;
    }

    public void forceClose() {
        state = State.Closed;
    }

    public void success() {
        exceptionCount=0;
        state = State.Closed;
    }

    public void exceptionOccurred( Throwable t ) {
        exceptionCount++;

        if ( state == State.Half_open || exceptionCount >= exceptionThreshold ) {
            state = State.Open;
            openTime = new Date();
            String message = String.format("Circuit breaker '%s' open for %d milli seconds", name, timeoutMilliSeconds );
            log.error( message, t );
        } else {
            log.error( String.format("Circuit breaker '%s' got exception but still closed", name), t);
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
