package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.TraceElement;
import com.jayway.dejavu.core.marshaller.MarshallerPlugin;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.easymock.EasyMock.createMock;

public class Neo4jMarshallerPlugin implements MarshallerPlugin  {

    private static final Set<Class<?>> neo4jClasses;

    static {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add( GraphDatabaseService.class );
        classes.add( Transaction.class );
        classes.add(Node.class);

        neo4jClasses = Collections.unmodifiableSet( classes );
    }

    @Override
    public Object unmarshal(Class<?> clazz, String marshalValue ) {
        if ( neo4jClasses.contains( clazz )) {
            // create mock instance
            return createMock( clazz );
        }
        /*if ( neo4jDejaVuClass( clazz )) {
            try {
                Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
                constructor.setAccessible( true );
                return constructor.newInstance(new Object[]{null});
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }*/
        return null;
    }

    @Override
    public String marshalObject(Object value) {
        if ( neo4jClasses.contains( value.getClass()) ) {
            // all neo4j classes serializes to the default value
            return "";
        }
        /*if ( neo4jDejaVuClass(value.getClass()) ) {
            return "";
        }*/
        // else continue
        return null;
    }

    @Override
    public String asTraceBuilderArgument(TraceElement element ) {
        if ( element.getType() == null ) {
            return element.getValue().getClass().getSimpleName() + ".class";
        } else {
            return element.getType().getSimpleName() + ".class";
        }
    }

    /*private boolean neo4jDejaVuClass( Class<?> clazz ) {
        return clazz.getPackage().getName().equals( DVGraphDatabaseService.class.getPackage().getName());
    }*/
}
