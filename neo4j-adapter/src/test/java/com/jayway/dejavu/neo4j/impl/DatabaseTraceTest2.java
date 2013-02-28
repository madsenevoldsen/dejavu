package com.jayway.dejavu.neo4j.impl;

import com.jayway.dejavu.JacksonMarshallerPlugin;
import com.jayway.dejavu.core.marshaller.TraceBuilder;
import com.jayway.dejavu.neo4j.*;
import org.junit.Test;

public class DatabaseTraceTest2 {

    @Test
    public void databasetracetest() throws Throwable {
        TraceBuilder builder = TraceBuilder.
                build(new Neo4jMarshallerPlugin(), new JacksonMarshallerPlugin()).
                setMethod(DatabaseTrace.class);

        builder.add( DVGraphDatabaseService.class, DVTransaction.class, DVNode.class, null, 41030L ).
                add( DVNode.class, null, DVRelationship.class, null, null, null, DVNode.class ).
                add( RelationshipIterator.class, true, DVRelationship.class, DVRelationshipType.class).
                add( String.class, "\"owner\"", DVNode.class, String.class, "\"Second node\"");

        builder.run();
    }
}
