package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.Node;

import java.util.Iterator;

public class NodeIterator {

    private Iterator<Node> iterator;

    NodeIterator(Iterator<Node> iterator) {
        this.iterator = iterator;
    }

    @Impure
    public Boolean hasNext() {
        return iterator.hasNext();
    }

    @Impure
    public DVNode next() {
        return new DVNode(iterator.next());
    }

    @Impure
    public void remove() {
        iterator.remove();
    }
}
