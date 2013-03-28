package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class ConnectionManager {

    private static GraphDatabaseService graphDb;

    public static void initialize( String pathToDatabase ) {
        GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(pathToDatabase);
        registerShutdownHook(graphDb);
        ConnectionManager.graphDb = graphDb;
    }

    @Impure( integrationPoint = "neo4j" )
    public static GraphDatabaseService graphDb() {
        return graphDb;
    }

    public static void run( WithTransaction withTransaction ) throws RuntimeException {
        GraphDatabaseService graphDb = graphDb();
        Transaction tx = graphDb.beginTx();
        try {
            withTransaction.invoke(graphDb);
            tx.success();
        } catch ( RuntimeException e ) {
            withTransaction.failed();
            tx.failure();
            throw e;
        } finally {
            tx.finish();
        }
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
}
