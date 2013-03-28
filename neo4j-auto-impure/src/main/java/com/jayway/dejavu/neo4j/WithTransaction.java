package com.jayway.dejavu.neo4j;

import org.neo4j.graphdb.GraphDatabaseService;

public abstract class WithTransaction {
    public abstract void invoke( GraphDatabaseService graphDb );
    public void failed() {}
}
