package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.DejaVuPolicy;
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
        DejaVuPolicy.initialize( callback );
    }

    @Test
    public void real_run() throws Throwable {
        // create a real connection to the database
        ConnectionManager.initialize( "testdb/" );

        String name = "a Node";
        new DatabaseInteraction().createAndVerify(name);

        // shutdown the database
        ConnectionManager.graphDb().shutdown();

        // now re-run without database
        DejaVuPolicy.replay(callback.getTrace());
    }

    @Test
    public void create_node() throws Throwable {
        TraceBuilder builder = TraceBuilder.
                build(new Neo4jMarshallerPlugin()).
                setMethod(DatabaseInteraction.class);
        builder.addMethodArguments("First node");

        builder.add(GraphDatabaseService.class, Transaction.class, Node.class, null, 415L, null, null).
                add(GraphDatabaseService.class, Node.class, "First node");

        builder.run();
    }

    @Test
    public void query_test() throws Throwable {
        TraceBuilder builder = TraceBuilder.
                build(new Neo4jMarshallerPlugin()).
                setMethod(DatabaseQuery.class);

        builder.add(GraphDatabaseService.class, Transaction.class, Node.class, IndexManager.class).
                add(Index.class, null, Node.class, null, null, null, GraphDatabaseService.class, IndexManager.class).
                add(Index.class, IndexHits.class, 1);

        builder.run();
    }

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

        DejaVuPolicy.replay(trace);
    } */

    @Test
    public void query_paginate() throws Throwable {
        TraceBuilder builder = TraceBuilder.
                build(new Neo4jMarshallerPlugin()).
                setMethod(DatabasePagesQuery.class);

        builder.add(GraphDatabaseService.class, Transaction.class, IndexManager.class, Index.class).
                add(Node.class, null, null, Node.class, null, null, Node.class, null, null, Node.class).
                add(null, null, Node.class, null, null, Node.class, null, null, Node.class, null, null).
                add(Node.class, null, null, null, null, GraphDatabaseService.class, IndexManager.class).
                add(Index.class, IndexHits.class, 0, Node.class, "indexed dd", Node.class).
                add("indexed gg", Node.class, "indexed rr", false);

        builder.run();
    }
}
