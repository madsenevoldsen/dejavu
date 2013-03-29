package com.jayway.dejavu.core.marshaller;

import com.jayway.dejavu.core.Trace;
import com.jayway.dejavu.core.TraceElement;

import java.util.*;

public class Marshaller {

    private final MarshallerChain chain;

    public Marshaller( MarshallerPlugin... plugins) {
        this.chain = MarshallerChain.build( plugins );
    }

    public Object unmarshal( Class<?> clazz, String marshaled ) {
        return chain.unmarshal( clazz, marshaled );
    }

    public String marshalObject( Object value ) {
        return chain.marshalObject( value );
    }

    protected String asTraceBuilderArgument( TraceElement element ) {
        return chain.asTraceBuilderArgument( element );
    }

    public String marshal( final Trace trace ) {
        String testClassName = trace.getStartPoint().getDeclaringClass().getCanonicalName() + "Test";
        int index = testClassName.lastIndexOf('.');
        String classSimpleName = testClassName.substring(index+1);
        String packageName = testClassName.substring(0, index);

        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(packageName).append(";\n\n");
        // imports
        addImport(sb, Marshaller.class, Trace.class, TraceBuilder.class, trace.getStartPoint().getDeclaringClass() );
        addImport(sb, "org.junit.Test");

        Set<String> imports = new HashSet<String>();
        Set<String> threads = new LinkedHashSet<String>();
        for (TraceElement element : trace.getValues()) {
            threads.add( element.getThreadId() );
            addImport( sb, imports, element.getType() );
        }
        for (Object arg : trace.getStartArguments()) {
            addImport( sb, imports, arg );
        }
        List<Class> classes = chain.getClasses(new ArrayList<Class>());
        addImport(sb, classes.toArray(new Class[classes.size()]));
        String marshallerArgs = join(classes,new Join<Class>() {
            public String element(Class aClass) {
                return "new "+aClass.getSimpleName()+"()";
            }
        });

        threads.remove( trace.getId() );
        Map<String, Integer> threadIds = new HashMap<String, Integer>();
        int idx = 1;
        for (String thread : threads) {
            threadIds.put( thread, idx++);
        }
        sb.append("\n");

        // class
        add(sb, "public class " + classSimpleName + "{", 0);
        sb.append("\n");
        add(sb, "@Test", 1);
        add(sb, "public void " + classSimpleName.toLowerCase() + "() throws Throwable {", 1);
        add(sb, "TraceBuilder builder = TraceBuilder.", 2);
        if ( threads.isEmpty() ) {
            add(sb, "build(" + marshallerArgs +").", 4);
        } else {
            if ( marshallerArgs.isEmpty() ) {
                add(sb, "build(\"" +trace.getId()+"\").", 4);
            } else {
                add(sb, "build(\"" +trace.getId()+"\"," + marshallerArgs +").", 4);
            }
        }

        // TODO fix if not only one in method
        // TODO fix proper argument elements (with type)
        add(sb, "setMethod("+trace.getStartPoint().getDeclaringClass().getSimpleName()+".class);",4);
        if (trace.getStartPoint().getParameterTypes().length > 0 ) {
            String params = join(new Join<Object>() {
                public String element(Object element ) {
                return asTraceBuilderArgument( new TraceElement("0", element) );
                }
            },trace.getStartArguments());
            add(sb, "builder.addMethodArguments("+params+");", 2);
        }
        if ( !threads.isEmpty() ) {
            String threadArguments = join( threads, new Join<String>() {
                public String element(String s) {
                    return "\"" + s.substring( trace.getId().length()+1 ) + "\"";
                }
            });
            add( sb, "builder.threadIds("+threadArguments + ");", 2);
        }
        sb.append("\n");

        // TODO fix threadIds

        /*
        // figure out input types
        Class<?>[] types = trace.getStartPoint().getParameterTypes();
        if ( types == null || types.length == 0 ) {
            add(sb, "trace.setStartPoint(" + trace.getStartPoint().getDeclaringClass().getSimpleName()
                    + ".class.getDeclaredMethod(\"" + trace.getStartPoint().getName() + "\"));", 2);
        } else {
            StringBuilder argTypes = new StringBuilder();
            StringBuilder argValues = new StringBuilder();
            first = true;
            Object[] startArguments = trace.getStartArguments();
            for (int i = 0; i < startArguments.length; i++) {
                Object instance = startArguments[i];
                if (!first) {
                    argTypes.append(", ");
                    argValues.append(", ");
                } else {
                    first = false;
                }
                String className = trace.getStartPoint().getParameterTypes()[i].getSimpleName() + ".class";
                String value = StringEscapeUtils.escapeJava(marshalObject(instance));
                argTypes.append(className);
                argValues.append(String.format("marshaller.unmarshal(%s, \"%s\");", className, value));
            }
            add(sb, "trace.setStartPoint(" + trace.getStartPoint().getDeclaringClass().getSimpleName()
                    + ".class.getDeclaredMethod(\"" + trace.getStartPoint().getName() + "\", " + argTypes + "));", 2);
            // actual inputs
            add(sb, "trace.setStartArguments(new Object[]{" + argValues + "});", 2);
        } */

        // append values
        List<StringBuilder> valueRows = new ArrayList<StringBuilder>();
        String thread = null;
        boolean preferNewLine = true;
        for (TraceElement element : trace.getValues()) {
            StringBuilder current;
            if ( thread != null ) {
                if ( !thread.equals( element.getThreadId()) ) {
                    // we have to start a new line because this
                    // element is from a different thread
                    preferNewLine = true;
                }
            }

            if ( preferNewLine ) {
                if ( threadIds.isEmpty() ) {
                    valueRows.add( new StringBuilder("add(") );
                } else {
                    valueRows.add( new StringBuilder( "addT("+threadIds.get( element.getThreadId() ) + ", ") );
                }
                thread = element.getThreadId();
                preferNewLine = false;
            } else {
                valueRows.get( valueRows.size()-1 ).append( ", ");
            }
            current = valueRows.get( valueRows.size()-1 );
            current.append(asTraceBuilderArgument(element));
            if ( current.length() > 80 ) {
                preferNewLine = true;
            }
        }
        if ( trace.getValues().size() > 0 ) {
            for (int i=0;i<valueRows.size(); i++) {
                StringBuilder valueRow = valueRows.get(i);
                String end = ".";
                if ( i+1==valueRows.size() ) {
                    end = ";";
                }
                if ( i == 0 ) {
                    add( sb, "builder."+valueRow.toString()+")"+end, 2);
                } else {
                    add( sb, valueRow.toString() + ")"+end, 5);
                }
            }
        }
        sb.append("\n");
        add(sb, "builder.run();", 2);
        add(sb, "}", 1);
        add(sb, "}", 0);
        return sb.toString();
    }

    private void addImport( StringBuilder sb, Set<String> imports, Class<?> argument ) {
        if ( argument.isPrimitive() ) return;
        String name = argument.getName();
        if ( !imports.contains( name ) ) {
            addImport(sb, name);
            imports.add(name);
        }
    }

    private void addImport( StringBuilder sb, Set<String> imports, Object argument ) {
        if ( argument == null ) return;
        addImport( sb, imports, argument.getClass() );
    }

    private void addImport( StringBuilder sb, String importName ) {
        if ( !importName.startsWith("java.lang."))
        sb.append("import ").append(importName).append(";\n");
    }
    private void addImport( StringBuilder sb, Class<?>... classes ) {
        for (Class<?> aClass : classes) {
            if ( !aClass.getPackage().getName().startsWith( "java.lang" ) ) {
                sb.append( "import ").append( aClass.getName() ).append(";\n");
            }
        }
    }
    private void add(StringBuilder sb, String line, int scope) {
        String scopeOne = "   ";
        for (int i=0; i<scope; i++) {
            sb.append( scopeOne );
        }
        sb.append( line ).append("\n");
    }
    private <T> String join( Join<T> join, T... list ) {
        return join(Arrays.asList( list ), join);
    }
    private <T> String join( Collection<T> list, Join<T> join ) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (T t : list) {
            if ( first ) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append( join.element( t));
        }
        return sb.toString();
    }

    interface Join<T> {
        String element(T t);
    }
}
