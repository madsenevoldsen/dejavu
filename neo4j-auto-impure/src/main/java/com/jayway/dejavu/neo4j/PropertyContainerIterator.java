package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.PropertyContainer;

import java.util.Iterator;

public class PropertyContainerIterator implements Iterator<DVPropertyContainer>, Iterable<DVPropertyContainer> {

    private Iterator<PropertyContainer> iterator;

    PropertyContainerIterator(Iterator<PropertyContainer> iterator) {
        this.iterator = iterator;
    }

    @Override
    @Impure( integrationPoint = "neo4j" )
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    @Impure( integrationPoint = "neo4j" )
    public DVPropertyContainer next() {
        return new DVPropertyContainer(iterator.next());
    }

    @Override
    @Impure( integrationPoint = "neo4j" )
    public void remove() {
        iterator.remove();
    }

    @Override
    public Iterator<DVPropertyContainer> iterator() {
        return this;
    }
}
