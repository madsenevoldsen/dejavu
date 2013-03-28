package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.Path;

import java.util.Iterator;

public class PathIterator implements Iterator<DVPath>, Iterable<DVPath> {

    private Iterator<Path> iterator;

    PathIterator(Iterator<Path> iterator) {
        this.iterator = iterator;
    }

    @Override
    @Impure( integrationPoint = "neo4j" )
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    @Impure( integrationPoint = "neo4j" )
    public DVPath next() {
        return new DVPath(iterator.next());
    }

    @Override
    @Impure( integrationPoint = "neo4j" )
    public void remove() {
        iterator.remove();
    }

    @Override
    public Iterator<DVPath> iterator() {
        return this;
    }
}
