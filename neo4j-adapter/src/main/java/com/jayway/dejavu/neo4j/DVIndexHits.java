package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.index.IndexHits;

import java.util.Iterator;

public class DVIndexHits<T> implements Iterable<T>, Iterator<T> {

    transient IndexHits<T> indexHits;

    DVIndexHits( IndexHits<T> indexHits ) {
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
        return indexHits.next();
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
        return indexHits.getSingle();
    }

    @Impure( integrationPoint = "neo4j" )
    public Integer size() {
        return indexHits.size();
    }
}
