package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.Path;

import java.util.Iterator;

public class PathIterator {

    private Iterator<Path> iterator;

    PathIterator(Iterator<Path> iterator) {
        this.iterator = iterator;
    }

    @Impure( integrationPoint = "neo4j" )
    public Boolean hasNext() {
        return iterator.hasNext();
    }

    @Impure( integrationPoint = "neo4j" )
    public DVPath next() {
        return new DVPath(iterator.next());
    }

    @Impure( integrationPoint = "neo4j" )
    public void remove() {
        iterator.remove();
    }
}
