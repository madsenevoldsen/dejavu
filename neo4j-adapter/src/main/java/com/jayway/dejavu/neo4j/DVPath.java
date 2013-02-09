package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.Path;

public class DVPath {

    transient Path path;

    DVPath( Path path ) {
        this.path = path;
    }

    @Impure( integrationPoint = "neo4j" )
    public DVNode endNode() {
        return new DVNode( path.endNode() );
    }

    @Impure( integrationPoint = "neo4j" )
    public PropertyContainerIterator iterator() {
        return new PropertyContainerIterator( path.iterator() );
    }

    @Impure( integrationPoint = "neo4j" )
    public DVRelationship lastRelationship() {
        return new DVRelationship( path.lastRelationship() );
    }

    @Impure( integrationPoint = "neo4j" )
    public Integer length() {
        return path.length();
    }

    @Impure( integrationPoint = "neo4j" )
    public NodeIterator nodes() {
        return new NodeIterator( path.nodes().iterator() );
    }

    @Impure( integrationPoint = "neo4j" )
    public RelationshipIterator relationships() {
        return new RelationshipIterator( path.relationships().iterator() );
    }

    @Impure( integrationPoint = "neo4j" )
    public NodeIterator reverseNodes() {
        return new NodeIterator( path.reverseNodes().iterator() );
    }

    @Impure( integrationPoint = "neo4j" )
    public RelationshipIterator reverseRelationships() {
        return new RelationshipIterator( path.reverseRelationships().iterator() );
    }

    @Impure( integrationPoint = "neo4j" )
    public DVNode startNode() {
        return new DVNode( path.startNode() );
    }

    @Impure( integrationPoint = "neo4j" )
    public String toString() {
        return path.toString();
    }
}
