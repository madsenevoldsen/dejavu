package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.Transaction;

public class DVTransaction {

    private Transaction transaction;

    DVTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    @Impure
    public void success() {
        transaction.success();
    }

    @Impure
    public void finish() {
        transaction.finish();
    }

    @Impure
    public void failure() {
        transaction.failure();
    }
}
