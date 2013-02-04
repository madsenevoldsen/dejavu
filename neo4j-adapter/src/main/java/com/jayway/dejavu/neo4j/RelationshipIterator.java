package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.Relationship;

import java.util.Iterator;

public class RelationshipIterator {

    private Iterator<Relationship> iterator;

    RelationshipIterator(Iterator<Relationship> iterator) {
        this.iterator = iterator;
    }

    @Impure
    public Boolean hasNext() {
        return iterator.hasNext();
    }

    @Impure
    public DVRelationship next() {
        return new DVRelationship(iterator.next());
    }

    @Impure
    public void remove() {
        iterator.remove();
    }
}
