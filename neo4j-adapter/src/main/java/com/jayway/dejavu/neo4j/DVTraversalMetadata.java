package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.traversal.TraversalMetadata;

public class DVTraversalMetadata {
    private TraversalMetadata metadata;

    DVTraversalMetadata( TraversalMetadata metadata ) {
        this.metadata = metadata;
    }

    @Impure
    public Integer getNumberOfPathsReturned() {
        return metadata.getNumberOfPathsReturned();
    }

    @Impure
    public Integer getNumberOfRelationshipsTraversed() {
        return metadata.getNumberOfRelationshipsTraversed();
    }
}
