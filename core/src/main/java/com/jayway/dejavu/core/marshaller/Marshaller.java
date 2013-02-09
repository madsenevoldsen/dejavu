package com.jayway.dejavu.core.marshaller;

import com.jayway.dejavu.core.Trace;
import com.jayway.dejavu.core.TraceElement;
import com.jayway.dejavu.core.marshaller.dto.TraceDTO;
import com.jayway.dejavu.core.marshaller.dto.TracedElementDTO;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Marshaller {

    private Logger log = LoggerFactory.getLogger( Marshaller.class);
    private final MarshallerChain chain;

    public Marshaller( MarshallerPlugin... plugins) {
        if ( plugins == null ) log.error( "No marshal implementation supplied!" );
        this.chain = MarshallerChain.build( plugins );
    }

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
            values.add( new TracedElementDTO( value.getThreadId(), marshalObject(value.getValue()), value.getValue().getClass().getName()));
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
                    startArgument[i] = unmarshal(argumentTypes[i], dto.getArgumentJsonValues()[i]);
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
        return chain.unmarshal( clazz, jsonValue );
    }

    public String marshalObject( Object value ) {
        return chain.marshalObject( value );
    }

    public String generateTest( Trace trace ) {
        String testClassName = trace.getStartPoint().getDeclaringClass().getCanonicalName() + "Test";
        int index = testClassName.lastIndexOf('.');
        String classSimpleName = testClassName.substring(index+1);
        String packageName = testClassName.substring(0, index);

        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(packageName).append(";\n\n");
        // imports
        sb.append("import com.jayway.dejavu.Marshaller;\n");
        sb.append("import com.jayway.dejavu.core.Trace;\n");
        sb.append("import com.jayway.dejavu.core.DejaVuTrace;\n\n");
        sb.append("import ").append(trace.getStartPoint().getDeclaringClass().getName()).append(";\n");
        Set<String> imports = new HashSet<String>();
        for (Object element : trace.getValues()) {
            String name = element.getClass().getName();
            if ( !imports.contains( name ) && !name.startsWith("java.lang.")) {
                sb.append("import ").append(name).append(";\n");
                imports.add(name);
            }
        }
        for (Object arg : trace.getStartArguments()) {
            String name = arg.getClass().getName();
            if ( !imports.contains( name ) && !name.startsWith("java.lang.")) {
                sb.append("import ").append(name).append(";\n");
                imports.add(name);
            }
        }
        sb.append("\nimport org.junit.Test;\n");
        sb.append("import java.util.ArrayList;\n");
        sb.append("import java.util.List;\n\n");

        // class
        sb.append("public class ").append(classSimpleName).append(" {\n\n");
        sb.append("    @Test\n");
        sb.append("    public void ").append(classSimpleName.toLowerCase()).append("() throws Throwable {\n");
        // add new all the chain instances (except MarshallerPlugin)
        sb.append("        Marshaller marshaller = new Marshaller();\n");
        sb.append("        Trace trace = new Trace();\n");

        // figure out input types
        Class<?>[] types = trace.getStartPoint().getParameterTypes();
        if ( types == null || types.length == 0 ) {
            sb.append("        trace.setStartPoint(").append( trace.getStartPoint().getDeclaringClass().getSimpleName())
                    .append(".class.getDeclaredMethod(\"").append(trace.getStartPoint().getName())
                    .append("\"));\n");
        } else {
            StringBuilder argTypes = new StringBuilder();
            StringBuilder argValues = new StringBuilder();
            boolean first = true;
            for (Object instance : trace.getStartArguments()) {
                if ( !first ) {
                    argTypes.append(", ");
                    argValues.append(", ");
                } else {
                    first = false;
                }
                String className = instance.getClass().getSimpleName() + ".class";
                String value = StringEscapeUtils.escapeJava( marshalObject(instance));
                argTypes.append( className );
                argValues.append(String.format("marshaller.unmarshal(%s, \"%s\")", className, value));
            }
            sb.append("        trace.setStartPoint(").append( trace.getStartPoint().getDeclaringClass().getSimpleName())
                    .append(".class.getDeclaredMethod(\"").append(trace.getStartPoint().getName())
                    .append("\", ").append( argTypes.toString()).append("));\n");

            // actual inputs
            sb.append("        trace.setStartArguments(new Object[]{").append( argValues.toString() ).append("});\n");
        }

        sb.append("        List<Object> values = new ArrayList<Object>();\n");
        // append values
        for (TraceElement element : trace.getValues()) {
            if ( element.getValue() == null ) {
                sb.append(              "        values.add(null);\n");
            } else {
                String cName = element.getValue().getClass().getSimpleName();
                String value = StringEscapeUtils.escapeJava( marshalObject( element.getValue() ) );
                sb.append(String.format("        values.add(marshaller.unmarshal(%s.class, \"%s\"));\n", cName, value));
            }
        }
        sb.append("        trace.setValues( values );\n");
        sb.append("\n");
        sb.append("        DejaVuTrace.begin(trace);\n");
        sb.append("    }\n");
        sb.append("}\n");
        return sb.toString();
    }
}
