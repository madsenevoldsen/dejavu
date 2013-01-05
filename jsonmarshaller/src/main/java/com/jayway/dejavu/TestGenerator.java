package com.jayway.dejavu;


import com.jayway.dejavu.core.Trace;
import com.jayway.dejavu.core.TracedElement;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;

public class TestGenerator {

    private Logger log = LoggerFactory.getLogger( TestGenerator.class );

    public void generateTest( String testClassName, Trace trace, OutputStream os ) {
        try {
            if ( !testClassName.contains(".") ) {
                throw new RuntimeException("Cannot generate test in default package");
            }
            int index = testClassName.lastIndexOf('.');
            String classSimpleName = testClassName.substring(index+1);
            String packageName = testClassName.substring(0, index);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
            writer.write("package "+packageName+";\n\n");
            // imports
            writer.write("import com.jayway.dejavu.core.DejaVuUseCase;\n\n");
            writer.write("import "+ trace.getUseCaseClass().getCanonicalName()+";\n");
            writer.write("import com.jayway.dejavu.value.Value;\n");
            Set<String> valueImports = new HashSet<String>();
            for (TracedElement element : trace.getTracedElements()) {
                String name = element.getClazz().getCanonicalName();
                if ( !valueImports.contains( name )) {
                    writer.write( "import "+ name +";\n");
                    valueImports.add( name );
                }
            }
            writer.write("\nimport org.junit.Test;\n");
            writer.write("import java.util.ArrayList;\n");
            writer.write("import java.util.List;\n\n");

            // class
            writer.write("public class "+classSimpleName+" {\n\n");
            writer.write("    @Test\n");
            writer.write("    public void "+classSimpleName.toLowerCase()+"() {\n");
            writer.write("        Marshaller marshaller = new Marshaller();\n");
            writer.write("        List<Value> values = new ArrayList<Value>();\n");

            Marshaller marshaller = new Marshaller();
            for (TracedElement element : trace.getTracedElements()) {
                String cName = element.getClazz().getSimpleName();
                String value = StringEscapeUtils.escapeJava( marshaller.marshal( element.getValue() ) );
                writer.write( String.format("        values.add(marshaller.unmarshal(%s.class, \"%s\"));\n",cName, value));
            }
            writer.write("\n");
            String name = trace.getUseCaseClass().getSimpleName();
            writer.write("        DejaVuUseCase dejaVu = new DejaVuUseCase("+name+".class, values);\n");
            writer.write("        dejaVu.run();\n");
            writer.write("    }\n");
            writer.write("}\n");

            writer.flush();

        } catch (IOException e) {
            log.error( "Could not write to output stream", e);
        }
    }

}
