package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.Relationship;

import java.util.Iterator;

public class RelationshipIterator implements Iterator<DVRelationship>, Iterable<DVRelationship> {

    private Iterator<Relationship> iterator;

    RelationshipIterator(Iterator<Relationship> iterator) {
        this.iterator = iterator;
    }

    @Override
    @Impure( integrationPoint = "neo4j" )
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    @Impure( integrationPoint = "neo4j" )
    public DVRelationship next() {
        return new DVRelationship(iterator.next());
    }

    @Override
    @Impure( integrationPoint = "neo4j" )
    public void remove() {
        iterator.remove();
    }

    @Override
    public Iterator<DVRelationship> iterator() {
        return this;
    }
}
