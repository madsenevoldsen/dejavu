package com.jayway.dejavu.core.marshaller;

import com.jayway.dejavu.core.ThrownThrowable;
import com.jayway.dejavu.core.TraceValueHandler;

/**
 * Mainly usable for test purposes.
 *
 * Makes it easier to simulate an exception from an
 * impure method
 */
public class RuntimeExceptionValueHandler implements TraceValueHandler {

    /*@Override
    public Object unmarshal(Class<?> clazz, String marshalValue) {
        if ( clazz == Class.class ) {
            try {
                Class<?> aClass = Class.forName(marshalValue);
                if (Exception.class.isAssignableFrom( aClass )) {
                    // try instantiating it
                    ThrownThrowable throwable = new ThrownThrowable();
                    throwable.setThrowable((Throwable) aClass.newInstance());
                    return throwable;
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public String marshalObject(Object value) {
        if ( value instanceof Exception ) {
            return value.getClass().getSimpleName() + ".class";
        }
        return null;
    }*/

    @Override
    public Object handle(Object value) {
        if ( value instanceof Class ) {
            if ( RuntimeException.class.isAssignableFrom((Class<?>) value)) {
                try {
                    RuntimeException r = (RuntimeException) ((Class<?>) value).newInstance();
                    ThrownThrowable throwable = new ThrownThrowable();
                    throwable.setThrowable(r);
                    return throwable;
                } catch (Exception e) {
                    // log
                }
            }
        }
        return value;
    }
}
