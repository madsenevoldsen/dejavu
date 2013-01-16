package com.jayway.dejavu.circuitbreaker;

import com.jayway.dejavu.circuitbreaker.impl.MySpecificException;
import com.jayway.dejavu.circuitbreaker.impl.UseCaseSetup;
import com.jayway.dejavu.circuitbreaker.impl.UseCaseWithoutCB;
import com.jayway.dejavu.core.DejaVuTrace;
import com.jayway.dejavu.core.value.IntegerValue;
import junit.framework.Assert;
import org.junit.Test;

public class WithoutCircuitBreaker {

    @Test
    public void fail_without_circuit_breaker_annotation() {
        CircuitBreakerHandler breaker = new CircuitBreakerHandler( "ExampleHandler", 500, 10 );
        UseCaseSetup setup = new UseCaseSetup(breaker);

        try {
            setup.run(UseCaseWithoutCB.class, new IntegerValue(1));
            Assert.fail("Should crash");
        } catch (MySpecificException e ) {
            DejaVuTrace trace = new DejaVuTrace(setup.getTrace());
            try {
                trace.run();
                Assert.fail("Must throw same exception");
            } catch ( MySpecificException again ) {
            }
        }
    }
}
