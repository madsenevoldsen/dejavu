package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.Path;

public class DVPath {

    Path path;

    DVPath( Path path ) {
        this.path = path;
    }

    @Impure
    public DVNode endNode() {
        return new DVNode( path.endNode() );
    }

    @Impure
    public PropertyContainerIterator iterator() {
        return new PropertyContainerIterator( path.iterator() );
    }

    @Impure
    public DVRelationship lastRelationship() {
        return new DVRelationship( path.lastRelationship() );
    }

    @Impure
    public Integer length() {
        return path.length();
    }

    @Impure
    public NodeIterator nodes() {
        return new NodeIterator( path.nodes().iterator() );
    }

    @Impure
    public RelationshipIterator relationships() {
        return new RelationshipIterator( path.relationships().iterator() );
    }

    @Impure
    public NodeIterator reverseNodes() {
        return new NodeIterator( path.reverseNodes().iterator() );
    }

    @Impure
    public RelationshipIterator reverseRelationships() {
        return new RelationshipIterator( path.reverseRelationships().iterator() );
    }

    @Impure
    public DVNode startNode() {
        return new DVNode( path.startNode() );
    }

    @Impure
    public String toString() {
        return path.toString();
    }
}
