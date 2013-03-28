package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.PathExpander;

public class DVPathExpander<STATE> {

    PathExpander<STATE> pathExpander;

    DVPathExpander( PathExpander<STATE> pathExpander ) {
        this.pathExpander = pathExpander;
    }

    @Impure( integrationPoint = "neo4j" )
    public RelationshipIterator expand( DVPath path, DVBranchState<STATE> branchState ) {
        return new RelationshipIterator( pathExpander.expand(path.path, branchState.branchState ).iterator() );
    }

    @Impure( integrationPoint = "neo4j" )
    public <STATE> DVPathExpander<STATE> reverse() {
        return new DVPathExpander( pathExpander.reverse() );
    }
}
