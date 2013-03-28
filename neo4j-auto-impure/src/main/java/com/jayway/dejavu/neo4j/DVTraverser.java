package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.traversal.Traverser;

public class DVTraverser {

    private Traverser traverser;

    DVTraverser( Traverser traverser ) {
        this.traverser = traverser;
    }

    @Impure( integrationPoint = "neo4j" )
    public PathIterator iterator() {
        return new PathIterator(traverser.iterator());
    }

    @Impure( integrationPoint = "neo4j" )
    public DVTraversalMetadata metadata() {
        return new DVTraversalMetadata( traverser.metadata() );
    }

    @Impure( integrationPoint = "neo4j" )
    public NodeIterator nodes() {
        return new NodeIterator( traverser.nodes().iterator() );
    }

    @Impure( integrationPoint = "neo4j" )
    public RelationshipIterator relationships() {
        return new RelationshipIterator( traverser.relationships().iterator() );
    }
}
