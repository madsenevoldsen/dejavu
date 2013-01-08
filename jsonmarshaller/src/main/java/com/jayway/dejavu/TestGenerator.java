package com.jayway.dejavu;


import com.jayway.dejavu.core.Trace;
import com.jayway.dejavu.core.TracedElement;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class TestGenerator {

    private Logger log = LoggerFactory.getLogger( TestGenerator.class );

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
        sb.append("import com.jayway.dejavu.core.DejaVuUseCase;\n\n");
        sb.append("import ").append(trace.getUseCaseClass().getCanonicalName()).append(";\n");
        sb.append("import com.jayway.dejavu.core.value.Value;\n");
        Set<String> valueImports = new HashSet<String>();
        for (TracedElement element : trace.getTracedElements()) {
            String name = element.getClazz().getCanonicalName();
            if ( !valueImports.contains( name )) {
                sb.append("import ").append(name).append(";\n");
                valueImports.add( name );
            }
        }
        sb.append("\nimport org.junit.Test;\n");
        sb.append("import java.util.ArrayList;\n");
        sb.append("import java.util.List;\n\n");

        // class
        sb.append("public class ").append(classSimpleName).append(" {\n\n");
        sb.append("    @Test\n");
        sb.append("    public void ").append(classSimpleName.toLowerCase()).append("() {\n");
        sb.append("        Marshaller marshaller = new Marshaller();\n");
        sb.append("        List<Value> values = new ArrayList<Value>();\n");

        Marshaller marshaller = new Marshaller();
        for (TracedElement element : trace.getTracedElements()) {
            String cName = element.getClazz().getSimpleName();
            String value = StringEscapeUtils.escapeJava( marshaller.marshal( element.getValue() ) );
            sb.append(String.format("        values.add(marshaller.unmarshal(%s.class, \"%s\"));\n", cName, value));
        }
        sb.append("\n");
        String name = trace.getUseCaseClass().getSimpleName();
        sb.append("        DejaVuUseCase dejaVu = new DejaVuUseCase(").append(name).append(".class, values);\n");
        sb.append("        dejaVu.run();\n");
        sb.append("    }\n");
        sb.append("}\n");
        return sb.toString();
    }

}
