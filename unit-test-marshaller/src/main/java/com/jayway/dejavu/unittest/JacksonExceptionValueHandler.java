package com.jayway.dejavu.unittest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.dejavu.core.ThrownThrowable;
import com.jayway.dejavu.core.TraceValueHandler;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JacksonExceptionValueHandler implements TraceValueHandler {

    private static Logger log = LoggerFactory.getLogger( JacksonExceptionValueHandler.class );

    @Override
    public Object handle(Object value) {
        if ( value instanceof ThrownThrowable ) {
            Throwable throwable = ((ThrownThrowable) value).getThrowable();
            ObjectMapper mapper = new ObjectMapper();

            String className = throwable.getClass().getSimpleName() + ".class";
            String serial;
            try {
                serial =  "\""+ StringEscapeUtils.escapeJava( mapper.writeValueAsString(throwable) ) + "\"";
            } catch (JsonProcessingException e) {
                log.error("could not marshal", e);
                return throwable;
            }
            return String.format( "new SerialThrownThrowable(%s, %s)",className,serial);
        }
        return value;
    }

}
