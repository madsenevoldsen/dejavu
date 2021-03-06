package com.jayway.dejavu;

import com.jayway.dejavu.core.DejaVuEngine;
import com.jayway.dejavu.core.TraceElement;
import com.jayway.dejavu.core.interfaces.Trace;
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
        DejaVuEngine.initialize(callback);
    }

    @Test
    public void first_real() throws Throwable {
        HowItShouldBe real = new HowItShouldBe();
        String result = real.myStartPoint("From a real call");

        String dejaVuRun = new DejaVuEngine().replay(callback.getTrace());

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
                new DejaVuEngine().replay(callback.getTrace());
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
                new DejaVuEngine().replay(callback.getTrace());
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
        String reRun = new DejaVuEngine().replay(callback.getTrace());

        Assert.assertEquals( result, reRun );
        System.out.println( result );
    }


    @Test
    public void integration_point_calling_integration_point() throws Throwable {
        Long time = new IPCallingIP().getTime();
        Trace trace = callback.getTrace();
        int size = 0;
        for (TraceElement traceElement : trace) {
            size++;
        }
        Assert.assertEquals( 1, size );

        Long second = new DejaVuEngine().replay(trace);
        Assert.assertEquals( time, second );
    }

}
