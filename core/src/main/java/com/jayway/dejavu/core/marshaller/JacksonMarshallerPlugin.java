package com.jayway.dejavu.core.marshaller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.dejavu.core.TraceElement;
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
        try {
            return "\""+ StringEscapeUtils.escapeJava( mapper.writeValueAsString(value) ) + "\"";
        } catch (JsonProcessingException e) {
            log.error("could not marshal", e);
            return null;
        }
    }

    @Override
    public String asTraceBuilderArgument( TraceElement element ) {
        String className;
        String value;
        if ( element.getType() == null ) {
            className = element.getValue().getClass().getSimpleName() + ".class";
            value = marshalObject( element.getValue() );
        } else {
            className = element.getType().getSimpleName() + ".class";
            value = marshalObject( element.getValue() );
        }
        return String.format( "new Value(%s, %s)",className,value);
    }
}
