package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.index.IndexManager;

import java.util.Map;

public class DVIndexManager {

    private IndexManager indexManager;

    DVIndexManager(IndexManager indexManager) {
        this.indexManager = indexManager;
    }

    @Impure( integrationPoint = "neo4j" )
    public Boolean existsForNodes( String nodes ) {
        return indexManager.existsForNodes(nodes);
    }

    @Impure( integrationPoint = "neo4j" )
    public Boolean existsForRelationships( String relations ) {
        return indexManager.existsForRelationships(relations);
    }

    @Impure( integrationPoint = "neo4j" )
    public DVIndex forNodes( String index ) {
        return new DVIndex( indexManager.forNodes(index) );
    }

    @Impure( integrationPoint = "neo4j" )
    public DVIndex forNodes( String indexName, Map<String, String> customConfiguration ) {
        return new DVIndex( indexManager.forNodes( indexName, customConfiguration ));
    }

    @Impure( integrationPoint = "neo4j" )
    public DVRelationshipIndex forRelationships(String indexName) {
        return new DVRelationshipIndex( indexManager.forRelationships( indexName ) );
    }

    @Impure( integrationPoint = "neo4j" )
    public DVRelationshipIndex forRelationships(String indexName, Map<String,String> customConfiguration) {
        return new DVRelationshipIndex( indexManager.forRelationships( indexName, customConfiguration ));
    }

    @Impure( integrationPoint = "neo4j" )
    public Map<String, String> getConfiguration( DVIndex index ) {
        return indexManager.getConfiguration( index.index );
    }

    /*
 AutoIndexer<Node>	getNodeAutoIndexer()

 RelationshipAutoIndexer	getRelationshipAutoIndexer()

*/
    @Impure( integrationPoint = "neo4j" )
    public String[] nodeIndexNames() {
        return indexManager.nodeIndexNames();
    }

    @Impure( integrationPoint = "neo4j" )
    public String[] relationshipIndexNames() {
        return indexManager.relationshipIndexNames();
    }

    @Impure( integrationPoint = "neo4j" )
    public String removeConfiguration(DVIndex index, String key) {
        return indexManager.removeConfiguration( index.index, key );
    }

    @Impure( integrationPoint = "neo4j" )
    public String setConfiguration( DVIndex index, String key, String value) {
        return indexManager.setConfiguration( index.index, key, value );
    }
}

