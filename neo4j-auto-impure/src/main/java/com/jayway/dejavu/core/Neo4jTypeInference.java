package com.jayway.dejavu.core;

import com.jayway.dejavu.core.typeinference.TypeInference;
import org.aspectj.lang.reflect.MethodSignature;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public class Neo4jTypeInference implements TypeInference {

    @Override
    public Class<?> inferType(Object instance, MethodSignature signature) {
        if ( signature.getMethod().getDeclaringClass() == Neo4jIterator.class ) {
            if ( signature.getName().equals("next") ) {
                if ( instance instanceof Node) {
                    return Node.class;
                }
                if ( instance instanceof Relationship ) {
                    return Relationship.class;
                }
            }
        }
        return null;
    }
}
