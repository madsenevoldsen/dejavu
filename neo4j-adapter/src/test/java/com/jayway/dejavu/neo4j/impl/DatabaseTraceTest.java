package com.jayway.dejavu.neo4j.impl;


import com.jayway.dejavu.core.marshaller.Marshaller;
import com.jayway.dejavu.core.Trace;
import com.jayway.dejavu.core.TraceElement;
import com.jayway.dejavu.core.DejaVuTrace;
import com.jayway.dejavu.neo4j.impl.DatabaseTrace;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.jayway.dejavu.neo4j.DVGraphDatabaseService;
import com.jayway.dejavu.neo4j.DVTransaction;
import com.jayway.dejavu.neo4j.DVNode;
import com.jayway.dejavu.neo4j.DVRelationship;
import com.jayway.dejavu.neo4j.RelationshipIterator;
import com.jayway.dejavu.neo4j.DVRelationshipType;
import com.jayway.dejavu.neo4j.Neo4jMarshallerPlugin;
import com.jayway.dejavu.JacksonMarshallerPlugin;

public class DatabaseTraceTest{

    @Test
    public void databasetracetest() throws Throwable {
        Marshaller marshaller = new Marshaller(new Neo4jMarshallerPlugin(), new JacksonMarshallerPlugin());
        Trace trace = new Trace();
        trace.setId("ef83196a-8f24-46e2-a88d-3480dc248683");
        Map<Integer, String> threadIds = new HashMap<Integer, String>();
        threadIds.put(0, "ef83196a-8f24-46e2-a88d-3480dc248683");
        trace.setStartPoint(DatabaseTrace.class.getDeclaredMethod("createNodesAndRelations"));
        List<TraceElement> values = new ArrayList<TraceElement>();
        values.add( new TraceElement( threadIds.get(0), marshaller.unmarshal(DVGraphDatabaseService.class, "")));
        values.add( new TraceElement( threadIds.get(0), marshaller.unmarshal(DVTransaction.class, "")));
        values.add( new TraceElement( threadIds.get(0), marshaller.unmarshal(DVNode.class, "")));
        values.add( new TraceElement( threadIds.get(0), null));
        values.add( new TraceElement( threadIds.get(0), marshaller.unmarshal(Long.class, "41030")));
        values.add( new TraceElement( threadIds.get(0), marshaller.unmarshal(DVNode.class, "")));
        values.add( new TraceElement( threadIds.get(0), null));
        values.add( new TraceElement( threadIds.get(0), marshaller.unmarshal(DVRelationship.class, "")));
        values.add( new TraceElement( threadIds.get(0), null));
        values.add( new TraceElement( threadIds.get(0), null));
        values.add( new TraceElement( threadIds.get(0), null));
        values.add( new TraceElement( threadIds.get(0), marshaller.unmarshal(DVNode.class, "")));
        values.add( new TraceElement( threadIds.get(0), marshaller.unmarshal(RelationshipIterator.class, "")));
        values.add( new TraceElement( threadIds.get(0), marshaller.unmarshal(Boolean.class, "true")));
        values.add( new TraceElement( threadIds.get(0), marshaller.unmarshal(DVRelationship.class, "")));
        values.add( new TraceElement( threadIds.get(0), marshaller.unmarshal(DVRelationshipType.class, "")));
        values.add( new TraceElement( threadIds.get(0), marshaller.unmarshal(String.class, "\"owner\"")));
        values.add( new TraceElement( threadIds.get(0), marshaller.unmarshal(DVNode.class, "")));
        values.add( new TraceElement( threadIds.get(0), marshaller.unmarshal(String.class, "\"Second node\"")));
        trace.setValues( values );

        DejaVuTrace.run( trace );
    }
}
