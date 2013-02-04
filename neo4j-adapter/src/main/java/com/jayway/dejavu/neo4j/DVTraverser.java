package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.traversal.Traverser;

public class DVTraverser {

    private Traverser traverser;

    DVTraverser( Traverser traverser ) {
        this.traverser = traverser;
    }

    @Impure
    public PathIterator iterator() {
        return new PathIterator(traverser.iterator());
    }

    @Impure
    public DVTraversalMetadata metadata() {
        return new DVTraversalMetadata( traverser.metadata() );
    }

    @Impure
    public NodeIterator nodes() {
        return new NodeIterator( traverser.nodes().iterator() );
    }

    @Impure
    public RelationshipIterator relationships() {
        return new RelationshipIterator( traverser.relationships().iterator() );
    }
}
