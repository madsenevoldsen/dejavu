package com.jayway.dejavu.neo4j.impl;

import com.jayway.dejavu.JacksonMarshallerPlugin;
import com.jayway.dejavu.core.DejaVuTrace;
import com.jayway.dejavu.core.Trace;
import com.jayway.dejavu.core.TraceElement;
import com.jayway.dejavu.core.marshaller.Marshaller;
import com.jayway.dejavu.neo4j.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DatabaseTraceTest {


    @Test
    public void databasetracetest() throws Throwable {

        Marshaller marshaller = new Marshaller( new Neo4jMarshallerPlugin(), new JacksonMarshallerPlugin() );
        Trace trace = new Trace();
        String id = "mainThread";
        trace.setId(id);
        trace.setStartPoint(DatabaseTrace.class.getDeclaredMethod("createNodesAndRelations"));
        List<TraceElement> values = new ArrayList<TraceElement>();
        values.add( new TraceElement(id, marshaller.unmarshal(DVGraphDatabaseService.class, "")));
        values.add( new TraceElement(id, marshaller.unmarshal(DVTransaction.class, "")));
        values.add( new TraceElement(id, marshaller.unmarshal(DVNode.class, "")));
        values.add( new TraceElement(id, null));
        values.add( new TraceElement(id, marshaller.unmarshal(Long.class, "44")));
        values.add( new TraceElement(id, marshaller.unmarshal(DVNode.class, "")));
        values.add( new TraceElement(id, null));
        values.add( new TraceElement(id, marshaller.unmarshal(DVRelationship.class, "")));
        values.add( new TraceElement(id, null));
        values.add( new TraceElement(id, null));
        values.add( new TraceElement(id, null));
        values.add( new TraceElement(id, marshaller.unmarshal(DVNode.class, "")));
        values.add( new TraceElement(id, marshaller.unmarshal(RelationshipIterator.class, "")));
        values.add( new TraceElement(id, marshaller.unmarshal(Boolean.class, "true")));
        values.add( new TraceElement(id, marshaller.unmarshal(DVRelationship.class, "")));
        values.add( new TraceElement(id, marshaller.unmarshal(DVRelationshipType.class, "")));
        values.add( new TraceElement(id, marshaller.unmarshal(String.class, "\"owner\"")));
        values.add( new TraceElement(id, marshaller.unmarshal(DVNode.class, "")));
        values.add( new TraceElement(id, marshaller.unmarshal(String.class, "\"Second node\"")));
        trace.setValues( values );

        DejaVuTrace.run(trace);
    }
}
