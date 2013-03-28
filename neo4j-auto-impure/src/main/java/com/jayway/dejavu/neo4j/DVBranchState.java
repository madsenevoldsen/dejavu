package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.traversal.BranchState;

public class DVBranchState<STATE> {

    BranchState<STATE> branchState;

    DVBranchState( BranchState<STATE> branchState ) {
        this.branchState = branchState;
    }

    @Impure( integrationPoint = "neo4j" )
    public STATE getState() {
        return branchState.getState();
    }

    @Impure( integrationPoint = "neo4j" )
    public void setState( STATE state ) {
        branchState.setState( state );
    }

}
