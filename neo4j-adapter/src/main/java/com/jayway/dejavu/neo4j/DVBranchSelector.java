package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.traversal.BranchSelector;

public class DVBranchSelector {

    BranchSelector branchSelector;

    DVBranchSelector( BranchSelector branchSelector ) {
        this.branchSelector = branchSelector;
    }

    @Impure( integrationPoint = "neo4j" )
    public DVTraversalBranch next( DVTraversalContext metadata ) {
        return new DVTraversalBranch( branchSelector.next( metadata.traversalContext ) );
    }
}
