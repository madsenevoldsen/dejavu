package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;
import org.neo4j.helpers.collection.PagingIterator;

import java.util.Iterator;

public class DVPagingIterator {
    protected transient PagingIterator<? extends PropertyContainer> pagingIterator;

    DVPagingIterator( PagingIterator<? extends PropertyContainer> pagingIterator ) {
        this.pagingIterator = pagingIterator;
    }

    @Impure( integrationPoint = "neo4j" )
    public static DVPagingIterator construct( DVIndexHits source, int pageSize ) {
        return new DVPagingIterator( new PagingIterator<PropertyContainer>( source.indexHits, pageSize));
    }

    @Impure( integrationPoint = "neo4j" )
    public int page() {
        return pagingIterator.page();
    }

    @Impure( integrationPoint = "neo4j" )
    public int page( int newPage ) {
        return pagingIterator.page( newPage );
    }

    @Impure( integrationPoint = "neo4j" )
    public NodeIterator nextPageN() {
        Iterator<? extends PropertyContainer> iterator = pagingIterator.nextPage();
        return new NodeIterator( (Iterator<Node>) iterator );
    }

    @Impure( integrationPoint = "neo4j" )
    public RelationshipIterator nextPageR() {
        Iterator<? extends PropertyContainer> iterator = pagingIterator.nextPage();
        return new RelationshipIterator( (Iterator<Relationship>) iterator );
    }
}
