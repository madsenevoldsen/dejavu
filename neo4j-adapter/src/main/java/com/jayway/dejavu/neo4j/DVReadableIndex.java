package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.index.ReadableIndex;

public class DVReadableIndex {
    transient ReadableIndex readableIndex;

    DVReadableIndex(ReadableIndex readableIndex ) {
        this.readableIndex = readableIndex;
    }

    @Impure( integrationPoint = "neo4j" )
    public Class getEntityType() {
        return readableIndex.getEntityType();
    }

    @Impure( integrationPoint = "neo4j" )
    public String getName() {
        return readableIndex.getName();
    }

    @Impure( integrationPoint = "neo4j" )
    public Boolean isWriteable() {
        return readableIndex.isWriteable();
    }

    @Impure( integrationPoint = "neo4j" )
    public DVGraphDatabaseService getGraphDatabase() {
        return new DVGraphDatabaseService( readableIndex.getGraphDatabase() );
    }

    @Impure( integrationPoint = "neo4j" )
    public DVIndexHits get( String key, Object value ) {
        return new DVIndexHits( readableIndex.get( key, value ) );
    }

    @Impure( integrationPoint = "neo4j" )
    public DVIndexHits query( Object queryOrQueryObject ) {
        return new DVIndexHits( readableIndex.query( queryOrQueryObject ));
    }

    @Impure( integrationPoint = "neo4j" )
    public DVIndexHits query( String key, Object queryOrQueryObject ) {
        return new DVIndexHits( readableIndex.query( key, queryOrQueryObject ));
    }
}
