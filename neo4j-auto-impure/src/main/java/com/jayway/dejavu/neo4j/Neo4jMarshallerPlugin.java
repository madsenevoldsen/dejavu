package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.marshaller.MarshallerPlugin;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.helpers.collection.PagingIterator;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.easymock.EasyMock.createMock;

public class Neo4jMarshallerPlugin implements MarshallerPlugin  {

    private static final Set<String> neo4jClasses;

    static {
        Set<String> classes = new HashSet<String>();
        // so far these are the supported classes
        classes.add( GraphDatabaseService.class.getName() );
        classes.add( Transaction.class.getName() );
        classes.add( Node.class.getName() );
        classes.add( IndexManager.class.getName() );
        classes.add( Index.class.getName() );
        classes.add( IndexHits.class.getName());
        classes.add( PagingIterator.class.getName());

        neo4jClasses = Collections.unmodifiableSet( classes );
    }

    @Override
    public Object unmarshal(Class<?> clazz, String marshalValue ) {
        if ( clazz == Class.class && neo4jClasses.contains( marshalValue )) {
            // create mock instance
            try {
                return createMock( Class.forName(marshalValue) );
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Could not find class: "+marshalValue);
            }
        }
        return null;
    }

    @Override
    public String marshalObject(Object value) {
        if ( neo4jClasses.contains( value.getClass().getName()) ) {
            // all neo4j classes serializes to the default value
            return value.getClass().getSimpleName() + ".class";
        }
        return null;
    }

}
