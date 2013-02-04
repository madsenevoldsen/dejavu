package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

public class DVNode extends DVPropertyContainer {
    Node node;

    DVNode(Node node) {
        super( node );
        this.node = node;
    }

    @Impure
    public DVRelationship createRelationshipTo( DVNode to, RelationshipType relationshipType ) {
        return new DVRelationship( node.createRelationshipTo( to.node, relationshipType ) );
    }

    @Impure
    public void delete() {
        node.delete();
    }

    @Impure
    public Long getId() {
        return node.getId();
    }

    @Impure
    public RelationshipIterator getRelationships() {
        return new RelationshipIterator( node.getRelationships().iterator() );
    }

    @Impure
    public RelationshipIterator getRelationships( Direction direction ) {
        return new RelationshipIterator( node.getRelationships( direction ).iterator() );
    }

    @Impure
    public RelationshipIterator getRelationships( Direction direction, RelationshipType... types  ) {
        return new RelationshipIterator( node.getRelationships( direction, types ).iterator() );
    }

    @Impure
    public RelationshipIterator getRelationships( RelationshipType... types ) {
        return new RelationshipIterator( node.getRelationships( types ).iterator() );
    }

    @Impure
    public RelationshipIterator getRelationships( RelationshipType type, Direction direction ) {
        return new RelationshipIterator( node.getRelationships( type, direction ).iterator() );
    }

    @Impure
    public DVRelationship getSingleRelationship(RelationshipType relationshipType, Direction direction ) {
        return new DVRelationship( node.getSingleRelationship(relationshipType, direction) );
    }

    @Impure
    public Boolean hasRelationship() {
        return node.hasRelationship();
    }

    @Impure
    public Boolean hasRelationship( Direction direction ) {
        return node.hasRelationship( direction );
    }

    @Impure
    public Boolean hasRelationship( RelationshipType relationshipType, Direction direction ) {
        return node.hasRelationship( relationshipType, direction );
    }

    @Impure
    public Boolean hasRelationship( Direction direction, RelationshipType... types ) {
        return node.hasRelationship( direction, types );
    }


    @Impure
    public Boolean hasRelationship( RelationshipType... types ) {
        return node.hasRelationship( types );
    }

}
