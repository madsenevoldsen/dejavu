package com.jayway.dejavu.core.marshaller;

import com.jayway.dejavu.core.ThrownThrowable;
import com.jayway.dejavu.core.TraceElement;

/**
 * Mainly usable for test purposes.
 *
 * Makes it easier to simulate an exception from an
 * impure method
 */
public class SimpleExceptionMarshaller implements MarshallerPlugin  {

    @Override
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
    }
}
