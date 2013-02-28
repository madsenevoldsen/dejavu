package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.server.WrappingNeoServerBootstrapper;

public class DVWrappingNeoServerBootstrapper {
    private transient WrappingNeoServerBootstrapper wrapper;

    public DVWrappingNeoServerBootstrapper( DVGraphDatabaseService service ) {
        if ( service.graphDb instanceof GraphDatabaseAPI ) {
            wrapper = new WrappingNeoServerBootstrapper((GraphDatabaseAPI) service.graphDb);
        } else {
            throw new IllegalArgumentException("Can only construct with a DVGraphDatabaseService of type GraphDatabaseAPI");
        }
    }

    @Impure( integrationPoint = "neo4j" )
    public void start() {
        wrapper.start();
    }

    @Impure( integrationPoint = "neo4j" )
    public void stop() {
        wrapper.stop();
    }
}
