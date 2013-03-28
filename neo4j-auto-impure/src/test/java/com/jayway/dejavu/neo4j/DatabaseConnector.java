package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.DejaVuAspect;
import com.jayway.dejavu.core.Trace;
import com.jayway.dejavu.core.marshaller.Marshaller;
import com.jayway.dejavu.neo4j.impl.DatabaseTrace;
import com.jayway.dejavu.neo4j.impl.TraceCallbackImpl;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseConnector {

    Logger log = LoggerFactory.getLogger( DatabaseConnector.class );

    @Test
    public void queryDb() {
        TraceCallbackImpl callback = new TraceCallbackImpl();
        DejaVuAspect.initialize(callback);

        new DatabaseTrace().createNodesAndRelations();

        Trace trace = callback.getTrace();
        String test = new Marshaller( new Neo4jMarshallerPlugin() ).marshal(trace);

        log.info(test);
    }

}
