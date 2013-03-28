package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.index.IndexHits;
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
    public PropertyContainerIterator get( String key, Object value ) {
        IndexHits indexHits = readableIndex.get(key, value);
        return new PropertyContainerIterator( indexHits.iterator());
    }


    @Impure( integrationPoint = "neo4j" )
    public RelationshipIterator getR( String key, Object value ) {
        IndexHits indexHits = readableIndex.get(key, value);
        return new RelationshipIterator(indexHits.iterator());
    }

    @Impure( integrationPoint = "neo4j" )
    public NodeIterator getN( String key, Object value ) {
        IndexHits indexHits = readableIndex.get(key, value);
        return new NodeIterator( indexHits.iterator() );
    }

    @Impure( integrationPoint = "neo4j" )
    public <T extends DVPropertyContainer> DVIndexHits<T> query( Object queryOrQueryObject ) {
        return new DVIndexHits<T>( readableIndex.query( queryOrQueryObject ));
    }

    @Impure( integrationPoint = "neo4j" )
    public <T extends DVPropertyContainer> DVIndexHits<T> query( String key, Object queryOrQueryObject ) {
        return new DVIndexHits<T>( readableIndex.query( key, queryOrQueryObject ));
    }
}
