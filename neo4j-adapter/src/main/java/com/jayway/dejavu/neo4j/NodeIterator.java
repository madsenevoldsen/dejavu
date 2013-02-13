package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.Node;

import java.util.Iterator;

public class NodeIterator implements Iterator<DVNode>, Iterable<DVNode> {

    private Iterator<Node> iterator;

    NodeIterator(Iterator<Node> iterator) {
        this.iterator = iterator;
    }

    @Override
    @Impure( integrationPoint = "neo4j" )
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    @Impure( integrationPoint = "neo4j" )
    public DVNode next() {
        return new DVNode(iterator.next());
    }

    @Override
    @Impure( integrationPoint = "neo4j" )
    public void remove() {
        iterator.remove();
    }

    @Override
    public Iterator<DVNode> iterator() {
        return this;
    }
}
