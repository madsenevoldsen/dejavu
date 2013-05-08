package com.jayway.dejavu.core;

import com.jayway.dejavu.core.annotation.Impure;

import java.util.Iterator;

public class Neo4jIterator implements Iterator, Iterable {

    private Iterator iterator;

    public Neo4jIterator( Iterator iterator ) {
        this.iterator = iterator;
    }

    @Override
    @Impure( integrationPoint = "neo4j" )
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    @Impure( integrationPoint = "neo4j" )
    public Object next() {
        return iterator.next();
    }

    @Override
    @Impure( integrationPoint = "neo4j" )
    public void remove() {
        iterator.remove();
    }

    @Override
    public Iterator iterator() {
        return this;
    }
}
