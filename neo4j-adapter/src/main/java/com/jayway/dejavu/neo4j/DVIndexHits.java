package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.index.IndexHits;

import java.util.Iterator;

public class DVIndexHits<T extends DVPropertyContainer> implements Iterable<T>, Iterator<T> {

    transient IndexHits<PropertyContainer> indexHits;

    DVIndexHits( IndexHits<PropertyContainer> indexHits ) {
        this.indexHits = indexHits;
    }

    @Override
    public Iterator<T> iterator() {
        return this;
    }

    @Override
    @Impure( integrationPoint = "neo4j" )
    public boolean hasNext() {
        return indexHits.hasNext();
    }

    @Override
    @Impure( integrationPoint = "neo4j" )
    public T next() {
        PropertyContainer next = indexHits.next();
        return (T) DVPropertyContainer.construct( next );
    }

    @Override
    @Impure( integrationPoint = "neo4j" )
    public void remove() {
        indexHits.remove();
    }

    @Impure( integrationPoint = "neo4j" )
    public void close(){
        indexHits.close();
    }

    @Impure( integrationPoint = "neo4j" )
    public Float currentScore() {
        return indexHits.currentScore();
    }

    @Impure( integrationPoint = "neo4j" )
    public T getSingle() {
        PropertyContainer container = indexHits.getSingle();
        return (T) DVPropertyContainer.construct( container );
    }

    @Impure( integrationPoint = "neo4j" )
    public Integer size() {
        return indexHits.size();
    }
}
