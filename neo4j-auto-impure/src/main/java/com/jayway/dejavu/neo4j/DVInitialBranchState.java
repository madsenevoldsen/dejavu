package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.traversal.InitialBranchState;

public class DVInitialBranchState<STATE> {

    InitialBranchState<STATE> initialBranchState;


    DVInitialBranchState(InitialBranchState<STATE> initialBranchState) {
        this.initialBranchState = initialBranchState;
    }

    @Impure( integrationPoint = "neo4j" )
    public DVInitialBranchState<STATE> reverse() {
        return new DVInitialBranchState<STATE>( initialBranchState.reverse() );
    }

}
