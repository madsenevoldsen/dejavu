package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.GraphDatabaseService;

public abstract class DVGraphDatabaseService {

    private GraphDatabaseService graphDb;

    @Impure
    public abstract void assignGraphDb();

    @Impure
    public DVTransaction beginTx() {
        return new DVTransaction( graphDb.beginTx() );
    }

    @Impure
    public DVNode createNode() {
        return new DVNode( graphDb.createNode());
    }

    @Impure
    public DVNode getReferenceNode() {
        return new DVNode( graphDb.getReferenceNode() );
    }

    @Impure
    public DVNode getNodeById(long id) {
        return new DVNode(graphDb.getNodeById(id));
    }

    @Impure
    public DVRelationship getRelationshipById(long id) {
        return new DVRelationship( graphDb.getRelationshipById(id) );
    }

    @Impure
    public void shutdown() {
        graphDb.shutdown();
    }

    @Impure
    public DVIndexManager index() {
        return new DVIndexManager( graphDb.index() );
    }

}
