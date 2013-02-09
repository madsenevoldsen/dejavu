package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.Relationship;

import java.util.Iterator;

public class RelationshipIterator {

    private Iterator<Relationship> iterator;

    RelationshipIterator(Iterator<Relationship> iterator) {
        this.iterator = iterator;
    }

    @Impure( integrationPoint = "neo4j" )
    public Boolean hasNext() {
        return iterator.hasNext();
    }

    @Impure( integrationPoint = "neo4j" )
    public DVRelationship next() {
        return new DVRelationship(iterator.next());
    }

    @Impure( integrationPoint = "neo4j" )
    public void remove() {
        iterator.remove();
    }
}
