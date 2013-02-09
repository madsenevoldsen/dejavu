package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.Node;

import java.util.Iterator;

public class NodeIterator {

    private Iterator<Node> iterator;

    NodeIterator(Iterator<Node> iterator) {
        this.iterator = iterator;
    }

    @Impure( integrationPoint = "neo4j" )
    public Boolean hasNext() {
        return iterator.hasNext();
    }

    @Impure( integrationPoint = "neo4j" )
    public DVNode next() {
        return new DVNode(iterator.next());
    }

    @Impure( integrationPoint = "neo4j" )
    public void remove() {
        iterator.remove();
    }
}
