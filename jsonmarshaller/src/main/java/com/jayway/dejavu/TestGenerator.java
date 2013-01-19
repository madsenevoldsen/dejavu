package com.jayway.dejavu;

import com.jayway.dejavu.core.Trace;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.HashSet;
import java.util.Set;

public class TestGenerator {

    public String generateTest( Trace trace ) {
        return generateTest( trace.getStartPoint().getDeclaringClass().getCanonicalName() + "Test", trace );
    }

    public String generateTest( String testClassName, Trace trace ) {
        StringBuilder sb = new StringBuilder();
        if ( !testClassName.contains(".") ) {
            throw new RuntimeException("Cannot generate test in default package");
        }
        int index = testClassName.lastIndexOf('.');
        String classSimpleName = testClassName.substring(index+1);
        String packageName = testClassName.substring(0, index);
        sb.append("package ").append(packageName).append(";\n\n");
        // imports
        sb.append("import com.jayway.dejavu.Marshaller;\n");
        sb.append("import com.jayway.dejavu.core.Trace;\n");
        sb.append("import com.jayway.dejavu.core.DejaVuTrace;\n\n");
        sb.append("import ").append(trace.getStartPoint().getDeclaringClass().getCanonicalName()).append(";\n");
        Set<String> imports = new HashSet<String>();
        for (Object element : trace.getValues()) {
            String name = element.getClass().getCanonicalName();
            if ( !imports.contains( name ) && !name.startsWith("java.lang.")) {
                sb.append("import ").append(name).append(";\n");
                imports.add(name);
            }
        }
        for (Object arg : trace.getStartArguments()) {
            String name = arg.getClass().getCanonicalName();
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
        sb.append("        Marshaller marshaller = new Marshaller();\n");
        sb.append("        Trace trace = new Trace();\n");

        Marshaller marshaller = new Marshaller();
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
                String value = StringEscapeUtils.escapeJava( marshaller.marshalObject( instance ) );
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
        for (Object element : trace.getValues()) {
            String cName = element.getClass().getSimpleName();
            String value = StringEscapeUtils.escapeJava( marshaller.marshalObject( element ) );
            sb.append(String.format("        values.add(marshaller.unmarshal(%s.class, \"%s\"));\n", cName, value));
        }
        sb.append("        trace.setValues( values );\n");
        sb.append("\n");
        sb.append("        DejaVuTrace.run(trace);\n");
        sb.append("    }\n");
        sb.append("}\n");
        return sb.toString();
    }

}
