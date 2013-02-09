package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.traversal.BranchOrderingPolicy;

public class DVBranchOrderingPolicy {

    BranchOrderingPolicy branchOrderingPolicy;

    DVBranchOrderingPolicy( BranchOrderingPolicy branchOrderingPolicy ) {
        this.branchOrderingPolicy = branchOrderingPolicy;
    }

    @Impure( integrationPoint = "neo4j" )
    public DVBranchSelector	create( DVTraversalBranch startBranch, DVPathExpander expander) {
        return new DVBranchSelector( branchOrderingPolicy.create( startBranch.traversalBranch, expander.pathExpander ));
    }

}
