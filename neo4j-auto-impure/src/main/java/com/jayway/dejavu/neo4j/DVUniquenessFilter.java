package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.traversal.UniquenessFilter;

public class DVUniquenessFilter {

    UniquenessFilter uniquenessFilter;

    DVUniquenessFilter(UniquenessFilter uniquenessFilter ) {
        this.uniquenessFilter = uniquenessFilter;
    }

    @Impure( integrationPoint = "neo4j" )
    public Boolean check( DVTraversalBranch branch ) {
        return uniquenessFilter.check( branch.traversalBranch );
    }

    @Impure( integrationPoint = "neo4j" )
    public Boolean checkFirst( DVTraversalBranch branch ) {
        return uniquenessFilter.checkFirst( branch.traversalBranch );
    }

}
