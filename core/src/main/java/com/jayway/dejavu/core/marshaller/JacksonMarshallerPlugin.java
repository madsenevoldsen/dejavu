package com.jayway.dejavu.core.marshaller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Can be used to marshal arbitrary objects but requires structure
 * dictated by Jackson. Can marshal/un-marshal exceptions and is
 * therefore always added at the end of the marshaller chain
 */
public class JacksonMarshallerPlugin implements MarshallerPlugin {

    private static Logger log = LoggerFactory.getLogger( JacksonMarshallerPlugin.class );

    @Override
    public Object unmarshal( Class<?> clazz, String jsonValue ) {
        // try all plugins first or try
        if ( jsonValue == null ) return null;

        if ( clazz == Class.class ) {
            try {
                return Class.forName(jsonValue);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Could not find class: "+e.getMessage());
            }
        }
        /*if ( clazz instanceof Class<?> && !ThrownThrowable.class.isAssignableFrom( clazz )) {
            return clazz;
        }*/

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonValue, clazz);
        } catch (IOException e) {
            log.error( "could not unmarshal", e );
            return null;
        }
    }

    @Override
    public String marshalObject( Object value ) {
        ObjectMapper mapper = new ObjectMapper();
        if ( value instanceof Class ) {
            return ((Class) value).getSimpleName() + ".class";
        }

        String className = value.getClass().getSimpleName() + ".class";
        String serial;
        try {
            serial =  "\""+ StringEscapeUtils.escapeJava( mapper.writeValueAsString(value) ) + "\"";
        } catch (JsonProcessingException e) {
            log.error("could not marshal", e);
            return null;
        }
        return String.format( "new Value(%s, %s)",className,serial);
    }
}
