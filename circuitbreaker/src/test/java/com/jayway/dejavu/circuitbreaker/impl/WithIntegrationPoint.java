package com.jayway.dejavu.circuitbreaker.impl;

import com.jayway.dejavu.core.annotation.Impure;
import com.jayway.dejavu.core.annotation.Traced;

public class WithIntegrationPoint {

    @Traced
    public void run(Integer input) {
        poke(input);
    }

    @Impure(integrationPoint = "cb1")
    private void poke(int input) {
        if ( input == 1 ) {
            throw new MyOwnException();
        }
    }
}
