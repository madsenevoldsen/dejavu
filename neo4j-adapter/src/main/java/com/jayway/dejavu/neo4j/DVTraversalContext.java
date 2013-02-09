package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.TraversalContext;

public class DVTraversalContext {

    TraversalContext traversalContext;

    DVTraversalContext( TraversalContext traversalContext ) {
        this.traversalContext = traversalContext;
    }

    @Impure( integrationPoint = "neo4j" )
    public Evaluation evaluate( DVTraversalBranch branch, DVBranchState state ) {
        return traversalContext.evaluate( branch.traversalBranch, state.branchState );
    }

    @Impure( integrationPoint = "neo4j" )
    public Boolean isUnique( DVTraversalBranch branch ) {
        return traversalContext.isUnique( branch.traversalBranch );
    }
    @Impure( integrationPoint = "neo4j" )
    public Boolean isUniqueFirst(DVTraversalBranch branch) {
        return traversalContext.isUniqueFirst( branch.traversalBranch );
    }

    @Impure( integrationPoint = "neo4j" )
    public void relationshipTraversed() {
        traversalContext.relationshipTraversed();
    }

    @Impure( integrationPoint = "neo4j" )
    public void unnecessaryRelationshipTraversed() {
        traversalContext.unnecessaryRelationshipTraversed();
    }
}
