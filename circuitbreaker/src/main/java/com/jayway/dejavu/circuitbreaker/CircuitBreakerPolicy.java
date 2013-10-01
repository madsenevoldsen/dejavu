package com.jayway.dejavu.circuitbreaker;

import com.jayway.dejavu.core.RunningTrace;

import java.util.HashMap;
import java.util.Map;

public class CircuitBreakerPolicy {

    static {
        RunningTrace.addImpureHandler(new CircuitBreakerImpureHandler());
        circuitBreakers = new HashMap<String, CircuitBreaker>();
    }

    private static int timeout = 10 * 60 * 1000; // ten minutes
    private static int exceptionThreshold = 10;

    private static Map<String, CircuitBreaker> circuitBreakers;

    public static void addCircuitBreaker( CircuitBreaker handler ) {
        circuitBreakers.put(handler.getName(), handler);
    }
    public static void addCircuitBreaker( String name, int timeout, int exceptionThreshold ) {
        circuitBreakers.put(name, new CircuitBreaker(name, timeout, exceptionThreshold));
    }

    public static void setDefaultCircuitBreakerSettings( int timeoutMillis, int exceptionThreshold) {
        timeout = timeoutMillis;
        CircuitBreakerPolicy.exceptionThreshold = exceptionThreshold;
    }


    static CircuitBreaker getCircuitBreaker( String integrationPoint ) {
        if (!circuitBreakers.containsKey(integrationPoint)) {
            circuitBreakers.put( integrationPoint, new CircuitBreaker(integrationPoint, timeout, exceptionThreshold));
        }
        return circuitBreakers.get( integrationPoint );
    }

}
