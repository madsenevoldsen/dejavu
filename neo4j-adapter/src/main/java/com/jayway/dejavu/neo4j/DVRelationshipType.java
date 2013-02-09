package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.RelationshipType;

public class DVRelationshipType {
    transient RelationshipType relationshipType;

    DVRelationshipType( RelationshipType relationshipType ) {
        this.relationshipType = relationshipType;
    }

    @Impure( integrationPoint = "neo4j" )
    public String name() {
        return relationshipType.name();
    }

}
