package com.jayway.dejavu.core.impl;

import com.jayway.dejavu.core.Provider;
import com.jayway.dejavu.core.TracedElement;
import com.jayway.dejavu.core.value.ExceptionValue;
import com.jayway.dejavu.core.value.Value;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class Tracer {

    protected List<TracedElement> trace;

    public Tracer() {
        trace = new ArrayList<TracedElement>();
    }

    protected <I,O extends Value> O provide(Provider<I,O> provider, I input) {
        try {
            O output = provider.request(input);
            if ( output != null ) {
                Class<?> clazz = output.getClass();
                trace.add( new TracedElement(clazz, output));
            } else {
                trace.add(new TracedElement(Object.class, null));
            }
            return output;
        } catch (RuntimeException e ) {
            // provider threw runtime exception
            trace.add( new TracedElement(ExceptionValue.class, new ExceptionValue( e.getClass().getCanonicalName(), e.getMessage() )));
            throw e;
        }
    }

    protected void provided(Value value) {
        trace.add( new TracedElement(value.getClass(), value ));
    }

    protected <T> T instance(Class<T> clazz ) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException("Could not instantiate class: "+clazz.getSimpleName() );
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not access: "+clazz.getSimpleName());
        }
    }

    public <T> T  wireDependencies( T instance ) {
        try {
            for (Field field : instance.getClass().getDeclaredFields()) {
                Provider dProvider = getFieldProvider( field );
                if ( dProvider != null ) {
                    field.setAccessible(true);
                    field.set( instance, addDecoration( dProvider ));
                }
            }
        } catch (InstantiationException e) {
            throw new RuntimeException("Could not wire dependencies: " + e.getMessage() );
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not wire dependencies: "+e.getMessage());
        }
        return instance;
    }

    protected abstract Provider getFieldProvider( Field field ) throws IllegalAccessException, InstantiationException;
    protected abstract Provider addDecoration( Provider provider );
}
