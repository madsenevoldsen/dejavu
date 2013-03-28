package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

public class DVRelationship extends DVPropertyContainer {
    private Relationship relationship;

    DVRelationship(Relationship relationship) {
        super( relationship );
        this.relationship = relationship;
    }

    @Impure
    public void delete() {
        relationship.delete();
    }

    @Impure
    public DVNode getEndNode() {
        return new DVNode( relationship.getEndNode() );
    }

    @Impure
    public Long getId() {
        return relationship.getId();
    }

    @Impure
    public DVNode[] getNodes() {
        Node[] nodes = relationship.getNodes();
        DVNode[] dvNodes = new DVNode[2];
        dvNodes[0] = new DVNode( nodes[0]);
        dvNodes[1] = new DVNode( nodes[1]);
        return dvNodes;
    }

    @Impure
    public DVNode getOtherNode( DVNode node ) {
        return new DVNode( relationship.getOtherNode( node.node ));
    }

    @Impure
    public DVNode getStartNode() {
        return new DVNode( relationship.getStartNode() );
    }

    @Impure
    public DVRelationshipType getType() {
        return new DVRelationshipType( relationship.getType() );
    }

    @Impure
    public Boolean isType( RelationshipType type ) {
        return relationship.isType( type );
    }
}
