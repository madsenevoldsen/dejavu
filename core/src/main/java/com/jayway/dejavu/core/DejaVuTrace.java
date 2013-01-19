package com.jayway.dejavu.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class DejaVuTrace {

    private static List<Object> values;
    private static int index;
    public static Object nextValue() throws Throwable {
        if (index >= values.size()) {
            throw new TraceEndedException();
        }
        Object result = values.get(index);
        index++;
        if ( result instanceof Throwable ) {
            // this means it will handle a return of Throwable wrong!
            throw (Throwable) result;
        }
        return result;
    }

    public static <T> T run( Trace trace ) throws Throwable {
        DejaVuAspect.setTraceMode( false );
        values = trace.getValues();
        index = 0;
        Method method = trace.getStartPoint();
        Class<?> aClass = method.getDeclaringClass();

        try {
            Object instance = aClass.newInstance();
            return (T) method.invoke(instance, trace.getStartArguments());
        } catch (TraceEndedException e ) {
            // the trace has ended so
            // we can do nothing but return null
            return null;
        } catch (InvocationTargetException ee ) {
            throw ee.getTargetException();
        } finally {
            DejaVuAspect.setTraceMode( true );
        }
    }

}