package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.ThrownThrowable;
import com.jayway.dejavu.core.interfaces.TraceValueHandler;

public class RuntimeExceptionValueHandler implements TraceValueHandler {

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
