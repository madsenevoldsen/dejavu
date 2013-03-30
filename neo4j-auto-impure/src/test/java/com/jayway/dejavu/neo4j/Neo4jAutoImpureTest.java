package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.DejaVuAspect;
import com.jayway.dejavu.core.DejaVuTrace;
import com.jayway.dejavu.core.Neo4jTypeInference;
import com.jayway.dejavu.core.Trace;
import com.jayway.dejavu.core.marshaller.Marshaller;
import com.jayway.dejavu.core.marshaller.TraceBuilder;
import com.jayway.dejavu.neo4j.impl.DatabaseInteraction;
import com.jayway.dejavu.neo4j.impl.DatabasePagesQuery;
import com.jayway.dejavu.neo4j.impl.DatabaseQuery;
import com.jayway.dejavu.neo4j.impl.TraceCallbackImpl;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;

public class Neo4jAutoImpureTest {

    private TraceCallbackImpl callback;

    @Before
    public void before(){
        callback = new TraceCallbackImpl();
        DejaVuAspect.initialize(callback, new Neo4jTypeInference());
    }

    /*@Test
    public void createNode() throws Throwable {
        // create a real connection to the database
        ConnectionManager.initialize( "src/test/neo4j-testdb/" );

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

    /*@Test
    public void create_node() throws Throwable {
        TraceBuilder builder = TraceBuilder.
                build(new Neo4jMarshallerPlugin()).
                setMethod(DatabaseInteraction.class);
        builder.addMethodArguments(String.class, "First node");

        builder.add(GraphDatabaseService.class, Transaction.class, Node.class, null, 415L, null, null).
                add(GraphDatabaseService.class, Node.class, String.class, "First node");

        builder.run();
    } */

    /*@Test
    public void query() throws Throwable {
        // create a real connection to the database
        ConnectionManager.initialize( "testdb/" );

        new DatabaseQuery().query();

        // shutdown the database
        ConnectionManager.graphDb().shutdown();

        // now re-run without database
        Trace trace = callback.getTrace();
        Marshaller marshaller = new Marshaller(new Neo4jMarshallerPlugin());
        System.out.println(marshaller.marshal(trace));

        DejaVuTrace.run(trace);
    } */

    /*@Test
    public void queryPages() throws Throwable {
        // create a real connection to the database
        ConnectionManager.initialize( "testdb/" );

        new DatabasePagesQuery().query();

        // shutdown the database
        ConnectionManager.graphDb().shutdown();

        // now re-run without database
        Trace trace = callback.getTrace();
        Marshaller marshaller = new Marshaller(new Neo4jMarshallerPlugin());
        System.out.println(marshaller.marshal(trace));

        DejaVuTrace.run(trace);
    } */

    @Test
    public void queryPages() throws Throwable {
        TraceBuilder builder = TraceBuilder.
                build(new Neo4jMarshallerPlugin()).
                setMethod(DatabasePagesQuery.class);

        builder.add(GraphDatabaseService.class, Transaction.class, IndexManager.class, Index.class).
                add(Node.class, null, null, Node.class, null, null, Node.class, null, null, Node.class).
                add(null, null, Node.class, null, null, Node.class, null, null, Node.class, null, null).
                add(Node.class, null, null, null, null, GraphDatabaseService.class, IndexManager.class).
                add(Index.class, IndexHits.class, 0, Node.class, String.class, "indexed dd", Node.class).
                add(String.class, "indexed gg", Node.class, String.class, "indexed rr", false);

        builder.run();
    }
}
