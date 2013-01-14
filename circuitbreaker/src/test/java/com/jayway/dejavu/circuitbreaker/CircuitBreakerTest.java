package com.jayway.dejavu.circuitbreaker;

import com.jayway.dejavu.circuitbreaker.impl.CircuitBreakerUseCase;
import com.jayway.dejavu.circuitbreaker.impl.UseCaseSetup;
import com.jayway.dejavu.core.DejaVuTrace;
import com.jayway.dejavu.core.value.IntegerValue;
import junit.framework.Assert;
import org.junit.Test;

public class CircuitBreakerTest {

    @Test
    public void repeat_crash_in_provider() {
        CircuitBreakerHandler breaker = new CircuitBreakerHandler("ExampleHandler", 500, 1);
        UseCaseSetup setup = new UseCaseSetup( breaker );
        try {
            setup.run(CircuitBreakerUseCase.class, new IntegerValue(1));
        } catch ( NullPointerException e ) {
            DejaVuTrace useCase = new DejaVuTrace(setup.getTrace());
            try {
                useCase.run();
                Assert.fail("runs as before so must throw npe");
            } catch (NullPointerException ee ) {
            }
        }
    }

    @Test
    public void repeat_crash_in_provider_circuit_open() {
        CircuitBreakerHandler breaker = new CircuitBreakerHandler("ExampleHandler", 500, 1);
        UseCaseSetup setup = new UseCaseSetup( breaker );
        try {
            setup.run(CircuitBreakerUseCase.class, new IntegerValue(1));
        } catch ( NullPointerException e ) {
            try {
                setup.run(CircuitBreakerUseCase.class, new IntegerValue(1));
            } catch (CircuitOpenException ee ) {
                try {
                    new DejaVuTrace(setup.getTrace()).run();
                    Assert.fail("runs as before so must throw CircuitOpenException");
                } catch (CircuitOpenException eee ) {
                }
            }
        }
    }

    @Test
    public void exceed_threshold() {
        CircuitBreakerHandler breaker = new CircuitBreakerHandler( "ExampleHandler", 500, 2 );
        UseCaseSetup setup = new UseCaseSetup( breaker );
        try {
            Assert.assertEquals( "Closed", breaker.getState() );
            setup.run(CircuitBreakerUseCase.class, new IntegerValue(1));
            Assert.fail("first crash");
        } catch (NullPointerException e ) {
            Assert.assertEquals( "Closed", breaker.getState() );
            try {
                setup.run(CircuitBreakerUseCase.class, new IntegerValue(1) );
                Assert.fail("second crash");
            } catch (NullPointerException ee ) {
                Assert.assertEquals( "Open", breaker.getState() );
                try {
                    setup.run(CircuitBreakerUseCase.class, new IntegerValue(1) );
                    Assert.fail("third must be of type CircuitOpenException");
                } catch (CircuitOpenException eee ) {
                    Assert.assertEquals( "Open", breaker.getState() );
                    try {
                        Thread.sleep( 600 );
                        // should now be set to half open
                        Assert.assertEquals("Half_open", breaker.getState() );
                        // should succeed
                        setup.run(CircuitBreakerUseCase.class, new IntegerValue(0 ));
                        Assert.assertEquals("Closed", breaker.getState() );
                    } catch (InterruptedException e1) {
                        Assert.fail();
                    }
                }
            }
        }
    }

}
