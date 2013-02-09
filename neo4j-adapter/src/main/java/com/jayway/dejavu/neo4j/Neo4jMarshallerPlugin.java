package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.marshaller.MarshallerPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Neo4jMarshallerPlugin implements MarshallerPlugin  {

    @Override
    public Object unmarshal(Class<?> clazz, String marshalValue ) {
        if ( neo4jDejaVuClass( clazz )) {
            try {
                Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
                constructor.setAccessible( true );
                return constructor.newInstance(new Object[]{null});
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String marshalObject(Object value) {
        if ( neo4jDejaVuClass( value.getClass() ) ) {
            return "";
        }
        // else continue
        return null;
    }

    private boolean neo4jDejaVuClass( Class<?> clazz ) {
        return clazz.getPackage().getName().equals( DVGraphDatabaseService.class.getPackage().getName());
    }
}
