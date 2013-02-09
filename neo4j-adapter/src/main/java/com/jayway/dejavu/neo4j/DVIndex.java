package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.index.Index;

public class DVIndex extends DVReadableIndex {
    transient Index index;

    DVIndex( Index index ) {
        super( index );
        this.index = index;
    }

    @Impure( integrationPoint = "neo4j" )
    public void add( DVPropertyContainer entity, String key, Object value ) {
        index.add( entity.container, key, value );
    }

    @Impure( integrationPoint = "neo4j" )
    public void delete() {
        index.delete();
    }

    @Impure( integrationPoint = "neo4j" )
    public DVPropertyContainer putIfAbsent( DVPropertyContainer entity, String key, Object value ) {
        return new DVPropertyContainer( index.putIfAbsent( entity.container, key, value ) );
    }

    @Impure( integrationPoint = "neo4j" )
    public void remove( DVPropertyContainer entity ) {
        index.remove( entity.container );
    }

    @Impure( integrationPoint = "neo4j" )
    public void remove( DVPropertyContainer entity, String key ) {
        index.remove( entity.container, key );
    }

    @Impure( integrationPoint = "neo4j" )
    public void remove( DVPropertyContainer entity, String key, Object value ) {
        index.remove( entity.container, key, value );
    }
}
