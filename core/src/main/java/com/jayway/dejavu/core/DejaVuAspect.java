package com.jayway.dejavu.core;

import com.jayway.dejavu.core.annotation.IntegrationPoint;
import com.jayway.dejavu.core.repository.TraceCallback;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.lang.reflect.Method;
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
            Class[] args = null;
            int argLength = proceed.getArgs().length;
            if ( argLength > 0 ) {
                args = new Class[ argLength ];
                for (int i=0; i<argLength; i++) {
                    args[ i ] = proceed.getArgs()[i].getClass();
                }
            }
            Class<?> aClass = Class.forName(proceed.getSignature().getDeclaringTypeName());
            Method method = aClass.getDeclaredMethod(proceed.getSignature().getName(), args);

            Trace trace = new Trace();
            trace.setStartPoint( method );
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
        if ( traceMode ) {
            CircuitBreaker handler = null;
            Trace trace = threadLocalTrace.get();

            String cbHandlerName = integrationPoint.circuitBreaker();
            if ( cbHandlerName.length() > 0  ) {
                // a circuit breaker is expected to guard this call
                if ( circuitBreakerHandlers != null &&
                        circuitBreakerHandlers.containsKey(cbHandlerName )) {
                    handler = circuitBreakerHandlers.get(cbHandlerName);
                    if ( handler.getState().equals( "Open" )) {
                        trace.getValues().add( new CircuitOpenException() );
                        //tracer.provided( new ExceptionValue( CircuitOpenException.class.getCanonicalName(), "Circuit breaker '"+breaker.getName()+"' is open" ));
                        throw new CircuitOpenException();
                    }
                } else {
                    // throw ConfigurationException(...)
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
