package com.jayway.dejavu.neo4j.impl;

import com.jayway.dejavu.core.annotation.Traced;
import com.jayway.dejavu.neo4j.*;
import junit.framework.Assert;
import org.neo4j.graphdb.RelationshipType;

public class DatabaseTrace {

    @Traced
    public void createNodesAndRelations() {
        DVGraphDatabaseService graphDb = DVGraphDatabaseService.connectEmbedded("/home/mads/Development/neo4j-community-1.8.1/data");
        DVTransaction transaction = graphDb.beginTx();

        DVNode node = graphDb.createNode();
        node.setProperty( "name", "First node" );
        Long id = node.getId();

        DVNode other = graphDb.createNode();
        other.setProperty( "name", "Second node");


        DVRelationship relationship = node.createRelationshipTo(other, new RelationshipType() {

            @Override
            public String name() {
                return "owner";
            }
        });

        relationship.setProperty("property", "owner relation");

        transaction.success();
        transaction.finish();

        // connect and read
        DVNode nodeById = graphDb.getNodeById(id);
        Assert.assertNotNull(nodeById);

        RelationshipIterator relationships = nodeById.getRelationships();

        Assert.assertTrue( relationships.hasNext() );
        DVRelationship next = relationships.next();

        Assert.assertEquals( "owner", next.getType().name() );
        Assert.assertEquals( "Second node", next.getEndNode().getProperty( "name") );

    }

}
