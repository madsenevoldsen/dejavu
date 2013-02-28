package com.jayway.dejavu.core.marshaller;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

class SimpleTypeMarshaller implements MarshallerPlugin  {

    private static final Set<Class<?>> types;

    static {
        HashSet<Class<?>> set = new HashSet<Class<?>>();
        set.add( Long.class );
        set.add( Float.class );
        set.add( Double.class );
        set.add( Integer.class );
        set.add( Boolean.class );
        set.add( String.class );

        types = Collections.unmodifiableSet( set );
    }

    @Override
    public Object unmarshal(Class<?> clazz, String marshalValue) {
        if ( !types.contains( clazz ) ) return null;
        if ( Double.class == clazz ) {
            return Double.parseDouble( marshalValue );
        } else if (Float.class == clazz ) {
            return Float.parseFloat( marshalValue );
        } else if ( Double.class == clazz ) {
            return Double.parseDouble( marshalValue );
        } else if ( Integer.class == clazz ) {
            return Integer.parseInt( marshalValue );
        } else if ( Boolean.class == clazz ) {
            return Boolean.parseBoolean( marshalValue );
        } else if ( Long.class == clazz ) {
            return Long.parseLong( marshalValue );
        } else if ( String.class == clazz ) {
            return marshalValue;
        }
        return null;
    }

    @Override
    public String marshalObject(Object value) {
        if ( value instanceof Long ) {
            return value + "L";
        } else if ( value instanceof Float ) {
            return value + "F";
        }
        if ( value instanceof Double || value instanceof Integer || value instanceof Boolean || value instanceof String ) {
            return value.toString();
        }
        return null;
    }

    @Override
    public String asTraceBuilderArgument(Object value) {
        if ( value instanceof String ) return "String.class, \""+value+"\"";
        return marshalObject( value );
    }
}
