package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.DejaVuPolicy;
import com.jayway.dejavu.core.RunningTrace;
import com.jayway.dejavu.neo4j.impl.DatabaseInteraction;
import com.jayway.dejavu.neo4j.impl.DatabasePagesQuery;
import com.jayway.dejavu.neo4j.impl.DatabaseQuery;
import com.jayway.dejavu.neo4j.impl.TraceCallbackImpl;
import com.jayway.dejavu.recordreplay.RecordReplayFactory;
import com.jayway.dejavu.recordreplay.RecordReplayer;
import com.jayway.dejavu.recordreplay.TraceBuilder;
import org.junit.Before;
import org.junit.Test;

public class Neo4jAutoImpureTest {

    private TraceCallbackImpl callback;

    @Before
    public void before(){
        callback = new TraceCallbackImpl();
        RunningTrace.initialize();
        DejaVuPolicy.initialize(callback);
        DejaVuPolicy.setFactory(new RecordReplayFactory());
        Neo4jTraceValueHandler.initialize();
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
        RecordReplayer.replay(callback.getTrace());
    }

    @Test
    public void create_node() throws Throwable {
        TraceBuilder builder = TraceBuilder.
                builder().setMethod(DatabaseInteraction.class);
        builder.addMethodArguments("First node");

        builder.add(Neo4jPure.Neo4jGraphDatabaseService, Neo4jPure.Neo4jTransaction, Neo4jPure.Neo4jNode, null).
                add(415L, null, null, Neo4jPure.Neo4jGraphDatabaseService, Neo4jPure.Neo4jNode, "First node");

        RecordReplayer.replay( builder.build() );
    }

    @Test
    public void query_test() throws Throwable {
        TraceBuilder builder = TraceBuilder.
                builder().setMethod(DatabaseQuery.class);

        builder.add(Neo4jPure.Neo4jGraphDatabaseService, Neo4jPure.Neo4jTransaction, Neo4jPure.Neo4jNode, Neo4jPure.Neo4jIndexManager).
                add(Neo4jPure.Neo4jIndex, null, Neo4jPure.Neo4jNode, null, null, null, Neo4jPure.Neo4jGraphDatabaseService ).
                add(Neo4jPure.Neo4jIndexManager, Neo4jPure.Neo4jIndex, Neo4jPure.Neo4jIndexHits, 1);

        RecordReplayer.replay( builder.build() );
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
        Marshaller marshaller = new Marshaller();
        System.out.println(marshaller.marshal(trace));

        DejaVuPolicy.replay(trace);
    } */

    @Test
    public void query_paginate() throws Throwable {
        TraceBuilder builder = TraceBuilder.
                builder().setMethod(DatabasePagesQuery.class);

        builder.add(Neo4jPure.Neo4jGraphDatabaseService, Neo4jPure.Neo4jTransaction, Neo4jPure.Neo4jIndexManager, Neo4jPure.Neo4jIndex).
                add(Neo4jPure.Neo4jNode, null, null, Neo4jPure.Neo4jNode, null, null, Neo4jPure.Neo4jNode, null, null, Neo4jPure.Neo4jNode).
                add(null, null, Neo4jPure.Neo4jNode, null, null, Neo4jPure.Neo4jNode, null, null, Neo4jPure.Neo4jNode, null, null).
                add(Neo4jPure.Neo4jNode, null, null, null, null, Neo4jPure.Neo4jGraphDatabaseService, Neo4jPure.Neo4jIndexManager).
                add(Neo4jPure.Neo4jIndex, Neo4jPure.Neo4jIndexHits, Neo4jPure.Neo4jPagingIterator, 0, Neo4jPure.Neo4jNode, "indexed dd", Neo4jPure.Neo4jNode).
                add("indexed gg", Neo4jPure.Neo4jNode, "indexed rr", false);

        RecordReplayer.replay( builder.build() );
    }
}
