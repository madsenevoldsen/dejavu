package com.jayway.dejavu.core.marshaller;

import com.jayway.dejavu.core.Trace;
import com.jayway.dejavu.core.TraceElement;
import com.jayway.dejavu.core.marshaller.dto.TraceDTO;
import com.jayway.dejavu.core.marshaller.dto.TracedElementDTO;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;

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
        addImport( sb, "com.jayway.dejavu.core.marshaller.Marshaller");
        addImport( sb, "com.jayway.dejavu.core.Trace");
        addImport( sb, "com.jayway.dejavu.core.TraceElement");
        addImport( sb, "com.jayway.dejavu.core.DejaVuTrace");
        addImport(sb, trace.getStartPoint().getDeclaringClass().getName());
        addImport(sb, "org.junit.Test");
        addImport(sb, "java.util.ArrayList");
        addImport(sb, "java.util.List");
        addImport(sb, "java.util.Map");
        addImport(sb, "java.util.HashMap");

        Set<String> imports = new HashSet<String>();
        Set<String> threads = new HashSet<String>();
        for (TraceElement element : trace.getValues()) {
            threads.add( element.getThreadId() );
            addImport( sb, imports, element.getValue() );
        }
        for (Object arg : trace.getStartArguments()) {
            addImport( sb, imports, arg );
        }
        List<Class> classes = chain.getClasses(new ArrayList<Class>());
        for (Class aClass : classes) {
            addImport( sb, aClass.getName() );
        }
        StringBuilder newMarshaller = new StringBuilder();
        boolean first = true;
        for (Class aClass : classes) {
            if ( first ) {
                newMarshaller.append("Marshaller marshaller = new Marshaller(");
                first = false;
            } else {
                newMarshaller.append(", ");
            }
            newMarshaller.append("new ").append(aClass.getSimpleName()).append("()");
        }
        newMarshaller.append(");");

        Map<String, Integer> threadIds = new HashMap<String, Integer>();
        int idx = 0;
        for (String thread : threads) {
            threadIds.put( thread, idx++);
        }
        sb.append("\n");

        // class
        add(sb, "public class " + classSimpleName + "{", 0);
        sb.append("\n");
        add(sb, "@Test", 1);
        add(sb, "public void " + classSimpleName.toLowerCase() + "() throws Throwable {", 1);
        // add new all the chain instances (except MarshallerPlugin)
        add(sb, newMarshaller.toString(), 2);
        add(sb, "Trace trace = new Trace();", 2);
        add(sb, "trace.setId(\""+trace.getId()+"\");",2);

        add(sb, "Map<Integer, String> threadIds = new HashMap<Integer, String>();", 2);
        for (Map.Entry<String, Integer> entry : threadIds.entrySet()) {
            add(sb, "threadIds.put("+entry.getValue()+", \"" + entry.getKey() + "\");", 2);
        }

        // figure out input types
        Class<?>[] types = trace.getStartPoint().getParameterTypes();
        if ( types == null || types.length == 0 ) {
            add(sb, "trace.setStartPoint(" + trace.getStartPoint().getDeclaringClass().getSimpleName()
                    + ".class.getDeclaredMethod(\"" + trace.getStartPoint().getName() + "\"));", 2);
        } else {
            StringBuilder argTypes = new StringBuilder();
            StringBuilder argValues = new StringBuilder();
            first = true;
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
                argValues.append(String.format("marshaller.unmarshal(%s, \"%s\");", className, value));
            }
            add(sb, "trace.setStartPoint(" + trace.getStartPoint().getDeclaringClass().getSimpleName()
                    + ".class.getDeclaredMethod(\"" + trace.getStartPoint().getName() + "\", " + argTypes + "));", 2);
            // actual inputs
            add(sb, "trace.setStartArguments(new Object[]{" + argValues + "});", 2);
        }
        add(sb, "List<TraceElement> values = new ArrayList<TraceElement>();", 2);
        // append values
        for (TraceElement element : trace.getValues()) {
            if ( element.getValue() == null ) {
                addElement(sb, threadIds.get(element.getThreadId()) + "), null", 2);
            } else {
                String cName = element.getValue().getClass().getSimpleName();
                String value = StringEscapeUtils.escapeJava( marshalObject( element.getValue() ) );
                String stm = String.format( "marshaller.unmarshal(%s.class, \"%s\"", cName, value );
                addElement(sb, threadIds.get(element.getThreadId()) + "), " + stm+")", 2);
            }
        }
        add(sb, "trace.setValues( values );", 2);
        sb.append("\n");
        add(sb, "DejaVuTrace.run( trace );", 2);
        add(sb, "}", 1);
        add(sb, "}", 0);
        return sb.toString();
    }

    private void addImport( StringBuilder sb, Set<String> imports, Object argument ) {
        if ( argument == null ) return;

        String name = argument.getClass().getName();
        if ( !imports.contains( name ) ) {
            addImport(sb, name);
            imports.add(name);
        }
    }

    private void addImport( StringBuilder sb, String importName ) {
        if ( !importName.startsWith("java.lang."))
        sb.append("import ").append(importName).append(";\n");
    }

    private void add(StringBuilder sb, String line, int scope) {
        String scopeOne = "   ";
        for (int i=0; i<scope; i++) {
            sb.append( scopeOne );
        }
        sb.append( line ).append("\n");
    }

    private void addElement( StringBuilder sb, String threadIdAndElement, int scope ) {
        add( sb, "values.add( new TraceElement( threadIds.get(" + threadIdAndElement + "));" , scope );
    }
}
