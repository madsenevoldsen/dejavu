package com.jayway.dejavu;

import com.jayway.dejavu.core.DejaVuPolicy;
import com.jayway.dejavu.core.Trace;
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
        DejaVuPolicy.initialize( callback );
    }

    @Test
    public void first_real() throws Throwable {
        HowItShouldBe real = new HowItShouldBe();
        String result = real.myStartPoint("From a real call");

        String dejaVuRun = DejaVuPolicy.replay(callback.getTrace());

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
                DejaVuPolicy.replay(callback.getTrace());
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
                DejaVuPolicy.replay(callback.getTrace());
                Assert.fail("Must throw MyOwnException again");
            } catch (MyOwnException ee ) {
            }
        }
    }

    @Test
    public void multiTrace() throws Throwable {
        MultiTrace multiTrace = new MultiTrace();
        String result = multiTrace.first();
        log.info("==== Deja vu ====");
        String reRun = DejaVuPolicy.replay(callback.getTrace());

        Assert.assertEquals( result, reRun );
        System.out.println( result );
    }


    @Test
    public void integration_point_calling_integration_point() throws Throwable {
        Long time = new IPCallingIP().getTime();
        Trace trace = callback.getTrace();
        Assert.assertEquals( 1, trace.getValues().size() );

        Long second = DejaVuPolicy.replay(trace);
        Assert.assertEquals( time, second );
    }


    @Test
    public void non_deterministic() {
        try {
            AlmostWorking useCase = new AlmostWorking();
            while ( true ) {
                useCase.getLucky();
            }
        } catch (Exception e ) {
            // this is an uncommon exception
            String test = new Marshaller().marshal(callback.getTrace());
            // generate test that reproduces the hard bug
            System.out.println( test );
        }
    }
}
