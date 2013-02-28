package com.jayway.dejavu;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.dejavu.core.marshaller.MarshallerPlugin;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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
    public String asTraceBuilderArgument(Object value) {
        return value.getClass().getSimpleName() + ".class, " + marshalObject( value );
    }
}
