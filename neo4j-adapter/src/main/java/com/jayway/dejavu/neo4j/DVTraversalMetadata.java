package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.traversal.TraversalMetadata;

public class DVTraversalMetadata {
    private TraversalMetadata metadata;

    DVTraversalMetadata( TraversalMetadata metadata ) {
        this.metadata = metadata;
    }

    @Impure( integrationPoint = "neo4j" )
    public Integer getNumberOfPathsReturned() {
        return metadata.getNumberOfPathsReturned();
    }

    @Impure( integrationPoint = "neo4j" )
    public Integer getNumberOfRelationshipsTraversed() {
        return metadata.getNumberOfRelationshipsTraversed();
    }
}
