package com.jayway.dejavu.core;

import com.jayway.dejavu.core.exception.TraceEndedException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DejaVuTrace {

    private static List<TraceElement> values;
    private static int index;
    private static Map<String, LinkedList<String>> childThreads;
    private static Trace trace;
    private static NextValueCallback cb;

    public static void setNextValueCallback( NextValueCallback cb ) {
        DejaVuTrace.cb = cb;
    }

    public interface NextValueCallback {
        void nextValue( Object value );
    }

    public synchronized static Object nextValue( String threadId ) throws Throwable {
        while (true) {
            if (index >= values.size()) {
                throw new TraceEndedException();
            }
            TraceElement result = values.get(index);
            if ( threadId.equals( result.getThreadId() ) ) {
                index++;
                DejaVuTrace.class.notifyAll();
                if ( result.getValue() instanceof ThrownThrowable ) {
                    throw ((ThrownThrowable) result.getValue()).getThrowable();
                }
                Object value = result.getValue();
                if ( cb != null ) {
                    cb.nextValue( value );
                }
                return value;
            } else {
                try {
                    // we need to wait for the value to be ready
                    DejaVuTrace.class.wait();
                } catch (InterruptedException e) {
                    // ignore. Continue waiting
                }
            }
        }
    }

    private static synchronized boolean done() {
        if ( index >= values.size() ) return true;
        DejaVuTrace.class.notifyAll();
        return false;
    }

    public static <T> T run( Trace trace ) throws Throwable {
        initialize( trace );
        Method method = trace.getStartPoint();
        Class<?> aClass = method.getDeclaringClass();

        try {
            DejaVuAspect.setTraceMode(false);
            Object instance = aClass.newInstance();
            // wait until trace ended???
            return (T) method.invoke(instance, trace.getStartArguments());
        } catch (TraceEndedException e ) {
            // the trace has ended so
            // we can do nothing but return null
            return null;
        } catch (InvocationTargetException ee ) {
            throw ee.getTargetException();
        } finally {
            while (!done() ) {
                // wait until finished
                Thread.sleep(500);
            }
            DejaVuAspect.setTraceMode( true );
        }
    }

    private static void initialize( Trace trace ) {
        DejaVuTrace.trace = trace;
        values = trace.getValues();
        index = 0;
        childThreads = new HashMap<String, LinkedList<String>>();
        for (TraceElement element : trace.getValues()) {
            String threadId = element.getThreadId();
            if ( threadId.contains(".") ) {
                // this is from a child thread
                String parent = threadId.substring(0, threadId.lastIndexOf("."));
                if ( !childThreads.containsKey( parent) ) {
                    childThreads.put( parent, new LinkedList<String>() );
                }
                if ( !childThreads.get(parent).contains( threadId) ) {
                    childThreads.get(parent).addLast( threadId );
                }
            }
        }
    }

    public static String nextChildThreadId( String threadId ) {
        return childThreads.get(threadId).removeFirst();
    }

    protected static Trace getTrace() {
        return trace;
    }

}