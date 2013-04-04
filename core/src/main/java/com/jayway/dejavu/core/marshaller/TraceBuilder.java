package com.jayway.dejavu.core.marshaller;

import com.jayway.dejavu.core.DejaVuTrace;
import com.jayway.dejavu.core.Trace;
import com.jayway.dejavu.core.TraceElement;
import com.jayway.dejavu.core.annotation.Traced;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TraceBuilder {

    private Marshaller marshaller;
    private Trace trace;
    private List<String> threadIds;

    public static TraceBuilder build( MarshallerPlugin... plugins ) {
        return build( "TraceId", plugins );
    }

    public static TraceBuilder build( String traceId, MarshallerPlugin... plugins ) {
        TraceBuilder traceBuilder = new TraceBuilder(traceId);
        traceBuilder.marshaller = new Marshaller( plugins );
        traceBuilder.threadIds= new ArrayList<String>();
        traceBuilder.threadIds.add( traceBuilder.trace.getId() );
        return traceBuilder;
    }

    private TraceBuilder( String traceId ) {
        trace = new Trace();
        trace.setId( traceId );
        trace.setValues( new ArrayList<TraceElement>() );
    }

    /**
     * Add ids of all other threads than the main one
     */
    public TraceBuilder threadIds( String... ids ) {
        for (String id : ids) {
            threadIds.add( trace.getId() + "." + id );
        }
        return this;
    }

    /**
     * convenience method. Set the start method to be the first
     * method discovered in the target class annotated with @Traced
     */
    public TraceBuilder setMethod( Class<?> target ) {
        for (Method method : target.getDeclaredMethods()) {
            for (Annotation annotation : method.getAnnotations()) {
                if ( Traced.class.isAssignableFrom( annotation.getClass() ) ) {
                    // set this method to be the starting point of the trace
                    trace.setStartPoint( method );
                    return this;
                }
            }
        }
        throw new RuntimeException( "No methods annotated @Traced could be found in class: "+target.getName() );
    }

    public TraceBuilder setMethod( Class<?> target, String name, Class<?>... arguments ) throws NoSuchMethodException {
        trace.setStartPoint( target.getDeclaredMethod(name, arguments));
        return this;
    }

    public TraceBuilder addMethodArguments( Object... arguments ) {
        if ( arguments == null ) {
            trace.setStartArguments( new Object[]{ null });
            return this;
        }
        List<Object> args = new ArrayList<Object>();
        for ( int i=0; i<arguments.length; i++) {
            Object argument = arguments[i];
            if ( argument == null ) {
                args.add(null);
            } else if ( argument instanceof Class ) {
                // peek next element
                if ( i+1 < arguments.length && arguments[i + 1] instanceof String) {
                    // this must be a serialized version
                    String next = (String) arguments[i + 1];
                    args.add(marshaller.unmarshal((Class<?>) argument, next));
                    i++;
                } else {
                    args.add( marshaller.unmarshal((Class<?>) argument, ""));
                }
            } else {
                args.add( argument );
            }
        }
        trace.setStartArguments(args.toArray(new Object[args.size()]));
        return this;
    }

    private TraceBuilder add( int threadIdx, Class<?> clazz ) {
        return add( threadIdx, clazz, "");
    }

    private TraceBuilder add( int threadIdx, Class<?> clazz, String marshaled ) {
        trace.getValues().add(new TraceElement(threadIds.get(threadIdx), marshaller.unmarshal(clazz, marshaled)));
        return this;
    }

    public TraceBuilder add( Object... arguments ) {
        return addT(0, arguments);
    }

    /**
     * helper method for adding arguments. Arguments comes in
     * pairs of Class, "serialized", where Class denotes the
     * type of the serialized string. There is only a Class argument
     * with no corresponding string the empty string will be added as serialized
     * version.
     * <p></p>
     * Nulls will be treated like calling <code>.addNull()</code>
     * <p></p>
     * Simple argument types, i.e. Integer, Long, Double, Float,
     * Boolean is added as e.g. Boolean.class, true
     *
     * @return this TraceBuilder instance to make a fluent API
     */
    public TraceBuilder addT( int threadIdx, Object... arguments ) {
        if ( arguments == null ) {
            addNull( threadIdx );
            return this;
        }
        for ( int i=0; i<arguments.length; i++) {
            Object argument = arguments[i];
            if ( argument == null ) {
                addNull();
            } else if ( argument instanceof Class ) {
                // peek next element
                if ( i+1 < arguments.length && arguments[i + 1] instanceof String) {
                    // this must be a serialized version
                    String next = (String) arguments[i + 1];
                    add( threadIdx, (Class)argument, next);
                    i++;
                } else {
                    // throw exception
                    add( threadIdx, (Class) argument );
                }
            } else {
                // argument that can be handled without type
                trace.getValues().add( new TraceElement(threadIds.get( threadIdx), argument ));
                //trace.getValues().add( new TraceElement(threadIds.get( threadIdx), argument ));
            }
        }
        return this;
    }

    public TraceBuilder addNull() {
        trace.getValues().add(new TraceElement(threadIds.get( 0 ), null));
        return this;
    }

    public TraceBuilder addNull( int threadIdx ) {
        trace.getValues().add(new TraceElement(threadIds.get(threadIdx), null));
        return this;
    }

    public Object run() throws Throwable {
        return DejaVuTrace.run( trace );
    }

}
