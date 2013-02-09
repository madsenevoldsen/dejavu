package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.TraversalBranch;

public class DVTraversalBranch {

    TraversalBranch traversalBranch;

    DVTraversalBranch(TraversalBranch traversalBranch) {
        this.traversalBranch = traversalBranch;
    }

    @Impure( integrationPoint = "neo4j" )
    public Boolean continues() {
        return traversalBranch.continues();
    }

    @Impure( integrationPoint = "neo4j" )
    public void evaluation( Evaluation evaluation ) {
        traversalBranch.evaluation( evaluation );
    }

    @Impure( integrationPoint = "neo4j" )
    public Integer expanded() {
        return traversalBranch.expanded();
    }

    @Impure( integrationPoint = "neo4j" )
    public Boolean includes() {
        return traversalBranch.includes();
    }

    @Impure( integrationPoint = "neo4j" )
    public void	initialize( DVPathExpander expander, DVTraversalContext metadata ) {
        traversalBranch.initialize( expander.pathExpander, metadata.traversalContext );
    }

    @Impure( integrationPoint = "neo4j" )
    public DVTraversalBranch next( DVPathExpander expander, DVTraversalContext metadata ) {
        return new DVTraversalBranch( traversalBranch.next( expander.pathExpander, metadata.traversalContext ) );
    }

    @Impure( integrationPoint = "neo4j" )
    public DVTraversalBranch parent() {
        return new DVTraversalBranch( traversalBranch.parent() );
    }

    @Impure( integrationPoint = "neo4j" )
    public void prune() {
        traversalBranch.prune();
    }

    @Impure( integrationPoint = "neo4j" )
    public Object state() {
        return traversalBranch.state();
    }
}
