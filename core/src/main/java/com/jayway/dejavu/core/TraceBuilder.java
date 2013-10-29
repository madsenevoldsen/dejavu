package com.jayway.dejavu.core;

import com.jayway.dejavu.core.annotation.Traced;
import com.jayway.dejavu.core.interfaces.Trace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class TraceBuilder {

    private static Logger log = LoggerFactory.getLogger( TraceBuilder.class );

    private String traceId;
    private List<String> threadIds;
    private Method startMethod;
    private Object[] startArguments;

    public TraceBuilder( String traceId ) {
        this.traceId = traceId;
        threadIds = new ArrayList<String>();
        threadIds.add( traceId );
    }

    public void setTraceId( String traceId ) {
        this.traceId = traceId;
    }

    public String getTraceId() {
        return traceId;
    }

    /**
     * Add ids of all other threads than the main one
     */
    public TraceBuilder threadIds( String... ids ) {
        for (String id : ids) {
            threadIds.add( getTraceId() + "." + id );
        }
        return this;
    }

    public TraceBuilder addThreadId( String threadId ) {
        threadIds.add( threadId );
        return this;
    }

    protected List<String> getThreadIds() {
        return threadIds;
    }

    public TraceBuilder startMethod( Class<?> target ) {
        for (Method method : target.getDeclaredMethods()) {
            for (Annotation annotation : method.getAnnotations()) {
                if ( Traced.class.isAssignableFrom( annotation.getClass() ) ) {
                    // set this method to be the starting point of the trace
                    startMethod = method;
                    //trace.setStartPoint( method );
                    return this;
                }
            }
        }
        throw new RuntimeException( "No methods annotated @Traced could be found in class: "+target.getName() );
    }

    public TraceBuilder startMethod( Method startMethod ) {
        this.startMethod = startMethod;
        return this;
    }

    public TraceBuilder startMethod( Class<?> target, String name, Class<?>... arguments ) throws NoSuchMethodException {
        startMethod = target.getDeclaredMethod(name, arguments);
        return this;
    }

    public Method getStartMethod(){
        return startMethod;
    }

    public TraceBuilder startArguments( Object... arguments ) {
        startArguments = arguments;
        return this;
    }

    protected Object[] getStartArguments() {
        return startArguments;
    }

    public TraceBuilder addValue( String threadId, Object value ) {
        int i = threadIds.indexOf(threadId);
        if ( i == -1 ) {
            log.error("Thread with id '"+threadId+"' was not found! Thread ids: " + threadIds);
        }
        return addT( i, value );
    }

    public TraceBuilder add( Object... values ) {
        return addT(0, values );
    }

    public TraceBuilder addT( int threadIdx, Object... values ) {
        if ( values == null ) {
            return addNull( threadIdx );
        }
        for (Object value : values) {
            if ( value == null ) {
                addNull(threadIdx);
            } else {
                addElement(new TraceElement(threadIds.get(threadIdx), value));
            }
        }
        return this;
    }


    protected abstract void addElement( TraceElement element );

    public TraceBuilder addNull(int threadIdx) {
        addElement( new TraceElement(threadIds.get(threadIdx), null));
        return this;
    }

    public String getNextChildThreadId(String parentThreadId) {
        String childThreadId = parentThreadId + "." + DejaVuEngine.generateId();
        addThreadId(childThreadId);
        return childThreadId;
    }

    public abstract Trace build();
}
