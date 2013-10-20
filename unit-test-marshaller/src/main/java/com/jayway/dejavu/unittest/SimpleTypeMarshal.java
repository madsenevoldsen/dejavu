package com.jayway.dejavu.unittest;

import com.jayway.dejavu.core.TraceValueHandler;

class SimpleTypeMarshal implements TraceValueHandler {

    @Override
    public Object handle(Object value) {
        if (value instanceof Enum) {
            return value.getClass().getSimpleName() + "." + value.toString();
        }

        if ( value instanceof Class) {
            return ((Class) value).getSimpleName() + ".class";
        }


        if (value == null ) return "null";
        if ( value instanceof Long ) {
            return value + "L";
        } else if ( value instanceof Float ) {
            return value + "F";
        }
        if ( value instanceof Double || value instanceof Integer || value instanceof Boolean ) {
            return value.toString();
        }
        if ( value instanceof String ) {
            return "\""+value+"\"";
        }

        return value;
    }
}
