package com.jayway.dejavu.core;

import com.jayway.dejavu.core.typeinference.TypeInference;
import org.aspectj.lang.reflect.MethodSignature;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public class Neo4jTypeInference implements TypeInference {

    protected Neo4jTypeInference() {}

    @Override
    public Class<?> inferType(Object instance, MethodSignature signature) {
        // inside the neo4j API an iterator can be returned. The
        // type information for this iterator is erased on runtime and therefore
        // we have to use this class to infer the specific type
        if ( signature.getMethod().getDeclaringClass() == Neo4jIterator.class ) {
            if ( signature.getName().equals("next") ) {
                if ( instance instanceof Node) {
                    return Node.class;
                }
                if ( instance instanceof Relationship ) {
                    return Relationship.class;
                }
                // other types are defined by runtime type
                return instance.getClass();
            }
        }
        return null;
    }
}
