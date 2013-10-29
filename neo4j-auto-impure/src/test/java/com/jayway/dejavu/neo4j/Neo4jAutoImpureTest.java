package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.DejaVuEngine;
import com.jayway.dejavu.core.TraceBuilder;
import com.jayway.dejavu.neo4j.impl.DatabaseInteraction;
import com.jayway.dejavu.neo4j.impl.DatabasePagesQuery;
import com.jayway.dejavu.neo4j.impl.DatabaseQuery;
import com.jayway.dejavu.neo4j.impl.TraceCallbackImpl;
import org.junit.Before;
import org.junit.Test;

public class Neo4jAutoImpureTest {

    private TraceCallbackImpl callback;

    @Before
    public void before(){
        callback = new TraceCallbackImpl();
        DejaVuEngine.initialize(callback);
    }

    @Test
    public void real_run() throws Throwable {
        DejaVuEngine.setValueHandlerClasses( Neo4jTraceValueHandler.class );
        // create a real connection to the database
        ConnectionManager.initialize( "testdb/" );

        String name = "a Node";
        new DatabaseInteraction().createAndVerify(name);

        // shutdown the database
        ConnectionManager.graphDb().shutdown();

        // now re-run without database
        new DejaVuEngine().replay(callback.getTrace(), new Neo4jTraceValueHandler());
    }

    @Test
    public void create_node() throws Throwable {
        DejaVuEngine.setValueHandlerClasses( Neo4jTraceValueHandler.class );
        TraceBuilder builder = DejaVuEngine.createTraceBuilder("traceId").startMethod(DatabaseInteraction.class);
        builder.startArguments("First node");

        builder.add(Neo4jPure.Neo4jGraphDatabaseService, Neo4jPure.Neo4jTransaction, Neo4jPure.Neo4jNode, null).
                add(415L, null, null, Neo4jPure.Neo4jGraphDatabaseService, Neo4jPure.Neo4jNode, "First node");

        new DejaVuEngine().replay(builder.build(), new Neo4jTraceValueHandler());
    }

    @Test
    public void query_test() throws Throwable {
        DejaVuEngine.setValueHandlerClasses( Neo4jTraceValueHandler.class );
        TraceBuilder builder = DejaVuEngine.createTraceBuilder("traceId").startMethod(DatabaseQuery.class);

        builder.add(Neo4jPure.Neo4jGraphDatabaseService, Neo4jPure.Neo4jTransaction, Neo4jPure.Neo4jNode, Neo4jPure.Neo4jIndexManager).
                add(Neo4jPure.Neo4jIndex, null, Neo4jPure.Neo4jNode, null, null, null, Neo4jPure.Neo4jGraphDatabaseService ).
                add(Neo4jPure.Neo4jIndexManager, Neo4jPure.Neo4jIndex, Neo4jPure.Neo4jIndexHits, 1);

        new DejaVuEngine().replay(builder.build(), new Neo4jTraceValueHandler());
    }

    @Test
    public void query_paginate() throws Throwable {
        DejaVuEngine.setValueHandlerClasses( Neo4jTraceValueHandler.class );
        TraceBuilder builder = DejaVuEngine.createTraceBuilder("traceId").
                startMethod(DatabasePagesQuery.class);

        builder.add(Neo4jPure.Neo4jGraphDatabaseService, Neo4jPure.Neo4jTransaction, Neo4jPure.Neo4jIndexManager, Neo4jPure.Neo4jIndex).
                add(Neo4jPure.Neo4jNode, null, null, Neo4jPure.Neo4jNode, null, null, Neo4jPure.Neo4jNode, null, null, Neo4jPure.Neo4jNode).
                add(null, null, Neo4jPure.Neo4jNode, null, null, Neo4jPure.Neo4jNode, null, null, Neo4jPure.Neo4jNode, null, null).
                add(Neo4jPure.Neo4jNode, null, null, null, null, Neo4jPure.Neo4jGraphDatabaseService, Neo4jPure.Neo4jIndexManager).
                add(Neo4jPure.Neo4jIndex, Neo4jPure.Neo4jIndexHits, Neo4jPure.Neo4jPagingIterator, 0, Neo4jPure.Neo4jNode, "indexed dd", Neo4jPure.Neo4jNode).
                add("indexed gg", Neo4jPure.Neo4jNode, "indexed rr", false);

        new DejaVuEngine().replay(builder.build(), new Neo4jTraceValueHandler());
    }
}
