package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.Transaction;

public class DVTransaction {

    private transient Transaction transaction;

    DVTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    @Impure( integrationPoint = "neo4j" )
    public void success() {
        transaction.success();
    }

    @Impure( integrationPoint = "neo4j" )
    public void finish() {
        transaction.finish();
    }

    @Impure( integrationPoint = "neo4j" )
    public void failure() {
        transaction.failure();
    }
}
