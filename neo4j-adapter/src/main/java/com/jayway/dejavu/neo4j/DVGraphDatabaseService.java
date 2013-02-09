package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class DVGraphDatabaseService {

    private transient GraphDatabaseService graphDb;

    DVGraphDatabaseService( GraphDatabaseService graphDb ) {
        this.graphDb = graphDb;
    }

    @Impure( integrationPoint = "neo4j" )
    public static DVGraphDatabaseService connectEmbedded( String pathToDatabase ) {
        GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(pathToDatabase);
        registerShutdownHook( graphDb );
        return new DVGraphDatabaseService( graphDb );
    }

    private static void registerShutdownHook( final GraphDatabaseService graphDb ) {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running example before it's completed)
        Runtime.getRuntime().addShutdownHook( new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });
    }

    @Impure( integrationPoint = "neo4j" )
    public DVTransaction beginTx() {
        return new DVTransaction( graphDb.beginTx() );
    }

    @Impure( integrationPoint = "neo4j" )
    public DVNode createNode() {
        return new DVNode( graphDb.createNode());
    }

    @Impure( integrationPoint = "neo4j" )
    public DVNode getNodeById(long id) {
        return new DVNode(graphDb.getNodeById(id));
    }

    @Impure( integrationPoint = "neo4j" )
    public DVRelationship getRelationshipById(long id) {
        return new DVRelationship( graphDb.getRelationshipById(id) );
    }

    @Impure( integrationPoint = "neo4j" )
    public void shutdown() {
        graphDb.shutdown();
    }

    @Impure( integrationPoint = "neo4j" )
    public DVIndexManager index() {
        return new DVIndexManager( graphDb.index() );
    }

}
