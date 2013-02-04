package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.Path;

import java.util.Iterator;

public class PathIterator {

    private Iterator<Path> iterator;

    PathIterator(Iterator<Path> iterator) {
        this.iterator = iterator;
    }

    @Impure
    public Boolean hasNext() {
        return iterator.hasNext();
    }

    @Impure
    public DVPath next() {
        return new DVPath(iterator.next());
    }

    @Impure
    public void remove() {
        iterator.remove();
    }
}
