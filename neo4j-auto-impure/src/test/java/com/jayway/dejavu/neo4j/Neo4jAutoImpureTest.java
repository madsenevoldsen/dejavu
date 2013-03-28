package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.DejaVuAspect;
import com.jayway.dejavu.core.marshaller.TraceBuilder;
import com.jayway.dejavu.neo4j.impl.DatabaseInteraction;
import com.jayway.dejavu.neo4j.impl.TraceCallbackImpl;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

public class Neo4jAutoImpureTest {

    private TraceCallbackImpl callback;

    @Before
    public void before(){
        callback = new TraceCallbackImpl();
        DejaVuAspect.initialize(callback);
    }

    /*@Test
    public void createNode() throws Throwable {
        // create a real connection to the database
        ConnectionManager.initialize( "neo4j-adapter/src/test/neo4j-testdb/" );

        String name = "First node";
        new DatabaseInteraction().createAndVerify(name);

        // shutdown the database
        ConnectionManager.graphDb().shutdown();

        // now re-run without database
        Trace trace = callback.getTrace();
        Marshaller marshaller = new Marshaller(new Neo4jMarshallerPlugin());
        System.out.println(marshaller.marshal(trace));

        DejaVuTrace.run( trace );
    } */

    @Test
    public void databaseinteractiontest() throws Throwable {
        TraceBuilder builder = TraceBuilder.
                build(new Neo4jMarshallerPlugin()).
                setMethod(DatabaseInteraction.class);
        builder.addMethodArguments(String.class, "First node");

        builder.add(GraphDatabaseService.class, Transaction.class, Node.class, null, 415L, null, null).
                add(GraphDatabaseService.class, Node.class, String.class, "First node");

        builder.run();
    }

}
