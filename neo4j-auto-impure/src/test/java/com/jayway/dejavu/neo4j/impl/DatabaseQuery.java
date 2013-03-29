package com.jayway.dejavu.neo4j.impl;

import com.jayway.dejavu.core.annotation.Traced;
import com.jayway.dejavu.neo4j.WithTransaction;
import junit.framework.Assert;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;

import static com.jayway.dejavu.neo4j.ConnectionManager.graphDb;
import static com.jayway.dejavu.neo4j.ConnectionManager.run;

public class DatabaseQuery {

    @Traced
    public void query() {
        run(new WithTransaction() {
            public void invoke(GraphDatabaseService graphDb) {
                Node node = graphDb.createNode();
                Index<Node> index = graphDb.index().forNodes("nodeIndex");
                index.add( node, "name", "john");

                node = graphDb.createNode();
                index.add( node, "name", "james" );
            }
        });

        Index<Node> index = graphDb().index().forNodes("nodeIndex");
        IndexHits<Node> hits = index.query("name", "john");

        Assert.assertEquals( 1, hits.size() );
    }

}