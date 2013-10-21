package com.jayway.dejavu.circuitbreaker;

import com.jayway.dejavu.circuitbreaker.impl.MyOwnException;
import com.jayway.dejavu.circuitbreaker.impl.TraceCallbackImpl;
import com.jayway.dejavu.circuitbreaker.impl.WithIntegrationPoint;
import com.jayway.dejavu.core.DejaVuEngine;
import com.jayway.dejavu.recordreplay.RecordReplayFactory;
import com.jayway.dejavu.recordreplay.RecordReplayer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CircuitBreakerTest {

    private TraceCallbackImpl callback;

    @Before
    public void setup() {
        callback = new TraceCallbackImpl();
        DejaVuEngine.initialize(callback);
        DejaVuEngine.setEngineFactory(new RecordReplayFactory());
        DejaVuEngine.clearImpureHandlers();
        CircuitBreakerPolicy.initialize();
    }

    @Test
    public void with_circuit_breaker() throws Throwable {
        // exactly as before but with a circuit breaker
        CircuitBreakerPolicy.addCircuitBreaker("cb1", 500, 1);
        WithIntegrationPoint example = new WithIntegrationPoint();
        try {
            example.run(1);
        } catch (MyOwnException e) {
            try {
                // repeat but get different exception
                example.run(1);
            } catch (CircuitOpenException ee ) {
                try {
                    RecordReplayer.replay(callback.getTrace());
                    Assert.fail("Must throw CircuitOpenException");
                } catch (CircuitOpenException eee ) {

                }
            }
        }
    }

    @Test
    public void exceed_threshold() {

        CircuitBreaker breaker = new CircuitBreaker("cb1", 500, 2);
        CircuitBreakerPolicy.addCircuitBreaker(breaker);
        WithIntegrationPoint example = new WithIntegrationPoint();
        try {
            Assert.assertTrue( breaker.isClosed());
            example.run(1);
            Assert.fail("first crash");
        } catch (MyOwnException e ) {
            Assert.assertTrue( "breaker must still be closed", breaker.isClosed());
            try {
                example.run(1);
                Assert.fail("second crash");
            } catch (MyOwnException ee ) {
                Assert.assertTrue( breaker.isOpen());
                try {
                    example.run(1);
                    Assert.fail("third must be of type CircuitOpenException");
                } catch (CircuitOpenException eee ) {
                    Assert.assertTrue( breaker.isOpen());
                    try {
                        Thread.sleep( 600 );
                        // should now be set to half open
                        Assert.assertTrue( breaker.isHalfOpen());
                        // should succeed
                        example.run(0);
                        Assert.assertTrue( breaker.isClosed() );
                    } catch (InterruptedException e1) {
                        Assert.fail();
                    }
                }
            }
        }
    }

}
