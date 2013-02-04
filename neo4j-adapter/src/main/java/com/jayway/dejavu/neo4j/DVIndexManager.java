package com.jayway.dejavu.neo4j;

import org.neo4j.graphdb.index.IndexManager;

public class DVIndexManager {

    private IndexManager indexManager;

    DVIndexManager(IndexManager indexManager) {
        this.indexManager = indexManager;
    }
}
