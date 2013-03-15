package com.jayway.dejavu;

import com.jayway.dejavu.core.CircuitBreaker;
import com.jayway.dejavu.core.DejaVuAspect;
import com.jayway.dejavu.core.DejaVuTrace;
import com.jayway.dejavu.core.Trace;
import com.jayway.dejavu.core.exception.CircuitOpenException;
import com.jayway.dejavu.core.marshaller.Marshaller;
import com.jayway.dejavu.impl.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TracerTest {

    private Logger log = LoggerFactory.getLogger( TracerTest.class );

    private TraceCallbackImpl callback;

    @Before
    public void setup() {
        callback = new TraceCallbackImpl();
        DejaVuAspect.initialize( callback );
    }

    @Test
    public void first_real() throws Throwable {
        HowItShouldBe real = new HowItShouldBe();
        String result = real.myStartPoint("From a real call");

        String dejaVuRun = DejaVuTrace.run(callback.getTrace());

        Assert.assertEquals(result, dejaVuRun);
    }


    @Test
    public void example() throws Throwable {
        ExampleTrace aCase = new ExampleTrace();
        try {
            aCase.run();
            Assert.fail("Must throw ArithmeticException");
        } catch (ArithmeticException e) {
            try {
                log.info("==== Deja vu ====");
                DejaVuTrace.run(callback.getTrace());
                Assert.fail("Must throw ArithmeticException again");
            } catch (ArithmeticException ee ) {
            }
        }
    }

    @Test
    public void failing_integration_point() throws Throwable {
        SickProviderUseCase aCase = new SickProviderUseCase();
        try {
            aCase.run();
            Assert.fail("Must throw MyOwnException");
        } catch (MyOwnException e ) {
            try {
                DejaVuTrace.run(callback.getTrace());
                Assert.fail("Must throw MyOwnException again");
            } catch (MyOwnException ee ) {
            }
        }
    }

    @Test
    public void with_circuit_breaker() throws Throwable {
        // exactly as before but with a circuit breaker
        DejaVuAspect.addCircuitBreaker( "cb1", 500, 1);
        WithIntegrationPoint example = new WithIntegrationPoint();
        try {
            example.run(1);
        } catch (MyOwnException e) {
            try {
                // repeat but get different exception
                example.run(1);
            } catch (CircuitOpenException ee ) {
                try {
                    DejaVuTrace.run(callback.getTrace());
                    Assert.fail("Must throw CircuitOpenException");
                } catch (CircuitOpenException eee ) {

                }
            }
        }
    }

    @Test
    public void exceed_threshold() {
        CircuitBreaker breaker = new CircuitBreaker("cb1", 500, 2);
        DejaVuAspect.addCircuitBreaker( breaker );
        WithIntegrationPoint example = new WithIntegrationPoint();
        try {
            Assert.assertTrue( breaker.isClosed());
            example.run(1);
            Assert.fail("first crash");
        } catch (MyOwnException e ) {
            Assert.assertTrue( breaker.isClosed());
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


    @Test
    public void multiTrace() throws Throwable {
        MultiTrace multiTrace = new MultiTrace();
        String result = multiTrace.first();
        log.info("==== Deja vu ====");
        String reRun = DejaVuTrace.run(callback.getTrace());

        Assert.assertEquals( result, reRun );
        System.out.println( result );
    }


    @Test
    public void integration_point_calling_integration_point() throws Throwable {
        Long time = new IPCallingIP().getTime();
        Trace trace = callback.getTrace();
        Assert.assertEquals( 1, trace.getValues().size() );

        Long second = DejaVuTrace.run(trace);
        Assert.assertEquals( time, second );
    }


    @Test
    public void non_deterministic() {
        try {
            AlmostWorking useCase = new AlmostWorking();
            while ( true ) {
                useCase.run();
            }
        } catch (Exception e ) {
            // this is an uncommon exception
            String test = new Marshaller().marshal(callback.getTrace());
            // generate test that reproduces the hard bug
            System.out.println( test );
        }
    }

    @Test
    public void randomProxy() throws Throwable {
        RandomProxyExample example = new RandomProxyExample();

        int result = example.invoke();

        Trace trace = callback.getTrace();
        Assert.assertNotNull( trace );

        String test = new Marshaller().marshal(trace);
        System.out.println( test );
        int result2 = DejaVuTrace.run(trace);

        System.out.println( result + " and " +result2 );
        Assert.assertEquals( result, result2);
    }
}
