package com.jayway.dejavu;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.dejavu.core.Trace;
import com.jayway.dejavu.core.TracedElement;
import com.jayway.dejavu.core.UseCase;
import com.jayway.dejavu.core.value.Value;
import com.jayway.dejavu.core.value.VoidValue;
import com.jayway.dejavu.dto.TraceDTO;
import com.jayway.dejavu.dto.TracedElementDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Marshaller {

    private Logger log = LoggerFactory.getLogger( Marshaller.class);

    public TraceDTO marshal( Trace trace ) {
        TraceDTO dto = new TraceDTO();
        dto.setUseCaseClass( trace.getUseCaseClass().getCanonicalName() );
        List<TracedElementDTO> elements = new ArrayList<TracedElementDTO>();
        for (TracedElement element : trace.getTracedElements()) {
            elements.add( new TracedElementDTO( marshal( element.getValue()), element.getClazz().getCanonicalName()));
        }
        dto.setTracedElements( elements );
        return dto;
    }

    public Trace unmarshal( TraceDTO dto ) {
        try {
            Class<? extends UseCase<?,?>> aClass = (Class<? extends UseCase<?, ?>>) Class.forName(dto.getUseCaseClass());
            List<TracedElement> elements = new ArrayList<TracedElement>();
            for (TracedElementDTO element : dto.getTracedElements()) {
                Class<? extends Value> valueClass = (Class<? extends Value>) Class.forName(element.getValueClass());
                elements.add( new TracedElement(valueClass, unmarshal(valueClass, element.getJsonValue()) ));
            }
            return new Trace( elements, aClass );
        } catch (ClassNotFoundException e) {
            log.error("could not find class",e);
            return null;
        }
    }

    public Value unmarshal( Class<? extends Value> clazz, String valueString ) {
        if ( clazz.equals(VoidValue.class)) return null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue( valueString, clazz );
        } catch (IOException e) {
            log.error( "could not unmarshal", e );
            return null;
        }
    }

    public String marshal( Value value ) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.error("could not marshal", e);
            return null;
        }
    }
}
