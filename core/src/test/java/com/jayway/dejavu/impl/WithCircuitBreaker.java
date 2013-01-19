package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.annotation.IntegrationPoint;
import com.jayway.dejavu.core.annotation.Traced;

public class WithCircuitBreaker {

    @Traced
    public void run(Integer input) {
        poke(input);
    }

    @IntegrationPoint(circuitBreaker = "cb1")
    private void poke(int input) {
        if ( input == 1 ) {
            throw new MyOwnException();
        }
    }
}
