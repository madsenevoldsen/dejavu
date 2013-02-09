package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.RelationshipIndex;

public class DVRelationshipIndex extends DVIndex {
    transient RelationshipIndex relationshipIndex;

    DVRelationshipIndex(RelationshipIndex index) {
        super( index );
        this.relationshipIndex = index;
    }

    @Impure( integrationPoint = "neo4j" )
    public DVIndexHits get( String key, Object valueOrNull, DVNode startNodeOrNull, DVNode endNodeOrNull ) {
        Node start = startNodeOrNull == null ? null : startNodeOrNull.node;
        Node end = endNodeOrNull== null ? null : endNodeOrNull.node;
        return new DVIndexHits( relationshipIndex.get( key, valueOrNull, start, end ) );
    }

    @Impure( integrationPoint = "neo4j" )
    public DVIndexHits query(Object queryOrQueryObjectOrNull, DVNode startNodeOrNull, DVNode endNodeOrNull ) {
        Node start = startNodeOrNull == null ? null : startNodeOrNull.node;
        Node end = endNodeOrNull == null ? null : endNodeOrNull.node;
        return new DVIndexHits( relationshipIndex.query( queryOrQueryObjectOrNull, start, end ));
    }

    @Impure( integrationPoint = "neo4j" )
    public DVIndexHits query( String key, Object queryOrQueryObjectOrNull, DVNode startNodeOrNull, DVNode endNodeOrNull ) {
        Node start = startNodeOrNull == null ? null : startNodeOrNull.node;
        Node end = endNodeOrNull == null ? null : endNodeOrNull.node;
        return new DVIndexHits( relationshipIndex.query( key, queryOrQueryObjectOrNull, start, end ));
    }
}
