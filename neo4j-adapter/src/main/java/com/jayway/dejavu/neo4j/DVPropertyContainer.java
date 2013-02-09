package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.PropertyContainer;

public class DVPropertyContainer {
    transient PropertyContainer container;

    DVPropertyContainer( PropertyContainer container ) {
        this.container = container;
    }

    @Impure
    public Object getProperty( String key ) {
        return container.getProperty( key );
    }

    @Impure
    public Object getProperty( String key, Object defaultValue ) {
        return container.getProperty(key,  defaultValue );
    }

    @Impure
    public Iterable<String> getPropertyKeys() {
        return container.getPropertyKeys();
    }

    @Impure
    public Boolean hasProperty( String key ) {
        return container.hasProperty( key );
    }

    @Impure
    public Object removeProperty(String key) {
        return container.removeProperty( key );
    }

    @Impure
    public void	setProperty(String key, Object value) {
        container.setProperty( key, value );
    }
}
