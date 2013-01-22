package com.jayway.dejavu.core;

import com.jayway.dejavu.core.annotation.IntegrationPoint;
import com.jayway.dejavu.core.exception.CircuitOpenException;
import com.jayway.dejavu.core.exception.NoSuchCircuitBreaker;
import com.jayway.dejavu.core.repository.TraceCallback;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Aspect
public class DejaVuAspect {

    private static TraceCallback callback;
    private static boolean traceMode = true;
    private static ThreadLocal<Trace> threadLocalTrace = new ThreadLocal<Trace>();
    private static Map<String, CircuitBreaker> circuitBreakerHandlers;

    public static void setCallback( TraceCallback cb ) {
        callback = cb;
    }
    public static void setTraceMode( boolean mode ) {
        traceMode = mode;
    }
    public static void addCircuitBreaker( CircuitBreaker handler ) {
        if ( circuitBreakerHandlers == null ) {
            circuitBreakerHandlers = new HashMap<String, CircuitBreaker>();
        }
        circuitBreakerHandlers.put( handler.getName(), handler);
    }
    public static void addCircuitBreaker( String name, int timeout, int exceptionThreshold ) {
        if ( circuitBreakerHandlers == null ) {
            circuitBreakerHandlers = new HashMap<String, CircuitBreaker>();
        }
        circuitBreakerHandlers.put( name, new CircuitBreaker(name, timeout, exceptionThreshold ));
    }

    @Around("execution(@com.jayway.dejavu.core.annotation.Traced * *(..))")
    public Object traced( ProceedingJoinPoint proceed ) throws Throwable {
        if ( traceMode && threadLocalTrace.get() == null ) {
            Trace trace = new Trace();
            MethodSignature signature = (MethodSignature) proceed.getSignature();
            trace.setStartPoint( signature.getMethod() );
            trace.setStartArguments( proceed.getArgs() );
            trace.setValues( new ArrayList<Object>());

            threadLocalTrace.set( trace );
            try {
                Object result = proceed.proceed();
                callback.traced( trace, null );
                threadLocalTrace.remove();
                return result;
            } catch ( Throwable t) {
                callback.traced( trace, t );
                threadLocalTrace.remove();
                throw t;
            }
        } else {
            // we are either in test mode or already tracing, so just proceed
            return proceed.proceed();
        }
    }

    @Around("execution(@com.jayway.dejavu.core.annotation.IntegrationPoint * *(..)) && @annotation(integrationPoint)")
    public Object integrationPoint( ProceedingJoinPoint proceed, IntegrationPoint integrationPoint) throws Throwable {
        Trace trace = threadLocalTrace.get();
        if ( traceMode && trace == null ) {
            // we are outside of a trace so call the method normally
            return proceed.proceed();
        }

        if ( traceMode ) {
            CircuitBreaker handler = null;

            String breaker = integrationPoint.circuitBreaker();
            if ( breaker.length() > 0  ) {
                // a circuit breaker is expected to guard this call
                if ( circuitBreakerHandlers != null &&
                        circuitBreakerHandlers.containsKey(breaker )) {
                    handler = circuitBreakerHandlers.get(breaker);
                    if ( handler.getState().equals( "Open" )) {
                        CircuitOpenException exception = new CircuitOpenException( "Circuit breaker '"+breaker+"' is open");
                        trace.getValues().add(exception);
                        throw exception;
                    }
                } else {
                    NoSuchCircuitBreaker exception = new NoSuchCircuitBreaker("Could not find circuit breaker '"+breaker+"'");
                    trace.getValues().add(exception);
                    throw exception;
                }
            }

            try {
                Object result = proceed.proceed();
                trace.getValues().add( result );
                if ( handler != null ) {
                    handler.success();
                }
                return result;
            } catch (Throwable t ) {
                trace.getValues().add( t );
                if ( handler != null ) {
                    handler.exceptionOccurred( t );
                }
                throw t;
            }
        } else {
            return DejaVuTrace.nextValue();
        }
    }
}
