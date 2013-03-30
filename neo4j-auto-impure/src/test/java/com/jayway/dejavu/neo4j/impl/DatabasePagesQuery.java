package com.jayway.dejavu.neo4j.impl;

import com.jayway.dejavu.core.annotation.Traced;
import com.jayway.dejavu.neo4j.WithTransaction;
import junit.framework.Assert;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.helpers.collection.PagingIterator;
import org.neo4j.index.lucene.QueryContext;

import java.lang.String;
import java.util.Iterator;

import static com.jayway.dejavu.neo4j.ConnectionManager.graphDb;
import static com.jayway.dejavu.neo4j.ConnectionManager.run;

public class DatabasePagesQuery {

    @Traced
    public void query() {
        run(new WithTransaction() {
            public void invoke(GraphDatabaseService graphDb) {
                Index<Node> index = graphDb.index().forNodes("nodeIndex");
                addNode( graphDb, index, "xx");
                addNode( graphDb, index, "zz");
                addNode( graphDb, index, "bb");
                addNode( graphDb, index, "gg");
                addNode( graphDb, index, "rr");
                addNode( graphDb, index, "dd");
                addNode( graphDb, index, "aa");
                addNode( graphDb, index, "cc");
            }
        });

        Index<Node> index = graphDb().index().forNodes("nodeIndex");
        IndexHits<Node> hits = index.query("name", new QueryContext("indexed*").sort("name"));

        PagingIterator<Node> iterator = new PagingIterator<Node>(hits, 3);
        iterator.page(1);
        Iterator<Node> nodes = iterator.nextPage();

        Assert.assertEquals( "indexed dd", nodes.next().getProperty("name") );
        Assert.assertEquals( "indexed gg", nodes.next().getProperty("name") );
        Assert.assertEquals( "indexed rr", nodes.next().getProperty("name") );
        Assert.assertFalse( nodes.hasNext() );
    }

    private void addNode( GraphDatabaseService graphDb, Index index, String postFix ) {
        Node node = graphDb.createNode();
        node.setProperty( "name", "indexed "+postFix);
        index.add( node, "name", "indexed "+postFix);
    }
}