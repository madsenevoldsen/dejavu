package com.jayway.dejavu.core.marshaller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.dejavu.core.ThrownThrowable;

import java.io.IOException;

public class SerialThrownThrowable extends ThrownThrowable {

    public SerialThrownThrowable( Class<?> clazz, String serialException ) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            setThrowable((Throwable) mapper.readValue(serialException, clazz));
        } catch (IOException e) {
            //log.error( "could not unmarshal", e );
        }
    }

}
