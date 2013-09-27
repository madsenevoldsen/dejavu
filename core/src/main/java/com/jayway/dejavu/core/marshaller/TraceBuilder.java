package com.jayway.dejavu.core.marshaller;

import com.jayway.dejavu.core.DejaVuPolicy;
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
        trace.setStartPoint(target.getDeclaredMethod(name, arguments));
        return this;
    }

    public TraceBuilder addMethodArguments( Object... arguments ) {
        if ( arguments == null ) {
            trace.setStartArguments( new Object[]{ null });
            return this;
        }
        List<Object> args = new ArrayList<Object>();
        for (Object argument : arguments) {
            args.add( unmarshalArgument( argument ));
        }
        trace.setStartArguments(args.toArray(new Object[args.size()]));
        return this;
    }

    private Object unmarshalArgument( Object argument ) {
        if (argument instanceof Value) {
            Value value = (Value) argument;
            return marshaller.unmarshal(value.getClazz(), value.getSerialValue());
        } else if (argument instanceof Class ) {
            return marshaller.unmarshal(Class.class, ((Class) argument).getName());
            //return marshaller.unmarshal((Class<?>) argument, "");
        } else {
            // fall through means simple type
            return argument;
        }
    }

    public TraceBuilder add( Object... arguments ) {
        return addT(0, arguments);
    }

    /**
     * helper method for adding arguments. Nulls will be treated like calling <code>.addNull()</code>
     *
     * @return this TraceBuilder instance to make a fluent API
     */
    public TraceBuilder addT( int threadIdx, Object... arguments ) {
        if ( arguments == null ) {
            return addNull( threadIdx );
        }
        for (Object argument : arguments) {
            trace.getValues().add( new TraceElement(threadIds.get(threadIdx), unmarshalArgument(argument)));
        }
        return this;
    }

    public TraceBuilder addNull( int threadIdx ) {
        trace.getValues().add(new TraceElement(threadIds.get(threadIdx), null));
        return this;
    }

    public Object run() throws Throwable {
        return DejaVuPolicy.replay(trace);
    }
}
