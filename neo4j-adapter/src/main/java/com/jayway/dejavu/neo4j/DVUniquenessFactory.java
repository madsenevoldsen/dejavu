package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.traversal.UniquenessFactory;

public class DVUniquenessFactory {
    
    UniquenessFactory uniquenessFactory;

    DVUniquenessFactory( UniquenessFactory uniquenessFactory ) {
        this.uniquenessFactory = uniquenessFactory;
    }

    @Impure( integrationPoint = "neo4j" )
    public DVUniquenessFilter create( Object object ) {
        return new DVUniquenessFilter( uniquenessFactory.create( object ) );
    }

}
