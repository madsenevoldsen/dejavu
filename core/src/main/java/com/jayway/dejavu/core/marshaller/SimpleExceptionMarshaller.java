package com.jayway.dejavu.core.marshaller;

import com.jayway.dejavu.core.ThrownThrowable;

/**
 * Mainly usable for test purposes.
 *
 * Makes it easier to simulate an exception from an
 * impure method
 */
public class SimpleExceptionMarshaller implements MarshallerPlugin  {

    @Override
    public Object unmarshal(Class<?> clazz, String marshalValue) {
        if ( Exception.class.isAssignableFrom( clazz ) && marshalValue.isEmpty() ) {
            // try instantiating it
            try {
                ThrownThrowable throwable = new ThrownThrowable();
                throwable.setThrowable((Throwable) clazz.newInstance());
                return throwable;
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

    @Override
    public String asTraceBuilderArgument(Object value) {
        return marshalObject(value);
    }
}
