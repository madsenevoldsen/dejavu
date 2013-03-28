package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;

public class DVPropertyContainer {
    transient PropertyContainer container;

    DVPropertyContainer( PropertyContainer container ) {
        this.container = container;
    }


    @Impure( integrationPoint = "neo4j" )
    public static DVPropertyContainer construct( PropertyContainer container ) {
        if ( container instanceof Node ) {
            return new DVNode((Node) container);
        } else if ( container instanceof Relationship) {
            return new DVRelationship((Relationship) container);
        }
        return new DVPropertyContainer( container );
    }


    @Impure( integrationPoint = "neo4j" )
    public Object getProperty( String key ) {
        return container.getProperty( key );
    }

    @Impure( integrationPoint = "neo4j" )
    public Object getProperty( String key, Object defaultValue ) {
        return container.getProperty(key,  defaultValue );
    }

    @Impure( integrationPoint = "neo4j" )
    public Iterable<String> getPropertyKeys() {
        return container.getPropertyKeys();
    }

    @Impure( integrationPoint = "neo4j" )
    public Boolean hasProperty( String key ) {
        return container.hasProperty( key );
    }

    @Impure( integrationPoint = "neo4j" )
    public Object removeProperty(String key) {
        return container.removeProperty( key );
    }

    @Impure( integrationPoint = "neo4j" )
    public void	setProperty(String key, Object value) {
        container.setProperty( key, value );
    }
}
