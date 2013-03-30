package com.jayway.dejavu.neo4j.impl;

import com.jayway.dejavu.core.annotation.Traced;
import com.jayway.dejavu.neo4j.WithTransaction;
import junit.framework.Assert;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import static com.jayway.dejavu.neo4j.ConnectionManager.graphDb;
import static com.jayway.dejavu.neo4j.ConnectionManager.run;

public class DatabaseInteraction {

    @Traced
    public void createAndVerify( final String name ) {
        final long[] id = new long[1];
        run(new WithTransaction() {
            public void invoke(GraphDatabaseService graphDb) {
                Node node = graphDb.createNode();
                node.setProperty("name", name);
                id[0] = node.getId();
            }
        });

        Node node = graphDb().getNodeById( id[0] );

        Assert.assertNotNull( node );
        Assert.assertEquals( name, node.getProperty("name") );
    }

}