package com.jayway.dejavu;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.dejavu.core.Trace;
import com.jayway.dejavu.core.TraceElement;
import com.jayway.dejavu.dto.TraceDTO;
import com.jayway.dejavu.dto.TracedElementDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Marshaller {

    private Logger log = LoggerFactory.getLogger( Marshaller.class);

    public TraceDTO marshal( Trace trace ) {
        TraceDTO dto = new TraceDTO();
        Method method = trace.getStartPoint();
        dto.setMethodName( method.getName() );
        dto.setClassName(method.getDeclaringClass().getName());
        if ( !(trace.getStartArguments() == null || trace.getStartArguments().length == 0) ) {
            int length = trace.getStartArguments().length;
            String[] args = new String[length];
            String[] argsValue = new String[length];
            for (int i=0; i<length; i++) {
                args[i] = trace.getStartArguments()[i].getClass().getName();
                argsValue[i] = marshalObject(trace.getStartArguments()[i]);
            }
            dto.setArgumentClasses( args );
            dto.setArgumentJsonValues( argsValue );
        }
        List<TracedElementDTO> values = new ArrayList<TracedElementDTO>();
        for (TraceElement value : trace.getValues()) {
            values.add( new TracedElementDTO( value.getThreadId(), marshalObject( value), value.getClass().getName()));
        }
        dto.setValues( values );
        return dto;
    }

    public Trace unmarshal( TraceDTO dto ) {
        try {
            Trace trace = new Trace();
            Class<?> aClass = Class.forName(dto.getClassName());
            if ( dto.getArgumentClasses() != null && dto.getArgumentClasses().length > 0 ) {
                int size = dto.getArgumentClasses().length;
                Class[] argumentTypes = new Class[size];
                Object[] startArgument = new Object[size];
                for (int i=0; i<size; i++) {
                    argumentTypes[i] = Class.forName( dto.getArgumentClasses()[i]);
                    startArgument[i] = unmarshal( argumentTypes[i], dto.getArgumentJsonValues()[i] );
                }
                trace.setStartPoint( aClass.getDeclaredMethod( dto.getMethodName(), argumentTypes) );
                trace.setStartArguments( startArgument );
            } else {
                trace.setStartPoint(aClass.getDeclaredMethod(dto.getMethodName()));
            }

            trace.setValues( new ArrayList<TraceElement>() );
            for (TracedElementDTO elementDTO : dto.getValues()) {
                Object value = unmarshal(Class.forName(elementDTO.getValueClass()), elementDTO.getJsonValue());
                trace.getValues().add(new TraceElement( elementDTO.getThreadId(), value ));
            }
            return trace;
        } catch (ClassNotFoundException e) {
            log.error("could not find class",e);
            return null;
        } catch (NoSuchMethodException e) {
            log.error("could not find method",e);
            return null;
        }
    }

    public Object unmarshal( Class<?> clazz, String jsonValue ) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue( jsonValue, clazz );
        } catch (IOException e) {
            log.error( "could not unmarshal", e );
            return null;
        }
    }

    public String marshalObject( Object value ) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.error("could not marshal", e);
            return null;
        }
    }
}
