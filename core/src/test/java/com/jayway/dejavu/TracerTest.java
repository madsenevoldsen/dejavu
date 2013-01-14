package com.jayway.dejavu;

import com.jayway.dejavu.core.DejaVuTrace;
import com.jayway.dejavu.impl.ExampleUseCase;
import com.jayway.dejavu.impl.SickProviderUseCase;
import com.jayway.dejavu.impl.UseCaseSetup;
import junit.framework.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TracerTest {

    private Logger log = LoggerFactory.getLogger( TracerTest.class );

    @Test
    public void crash_in_step() {
        UseCaseSetup setup = new UseCaseSetup();
        try {
            setup.run(ExampleUseCase.class, null);
            Assert.fail("use case is expected to fail");
        } catch (ArithmeticException e ) {
            DejaVuTrace dejaVu = new DejaVuTrace(setup.getTrace());
            log.info("==== Deja vu ====");
            try {
                dejaVu.run();
                Assert.fail("must throw A.E.");
            } catch (ArithmeticException ee ) {

            }
        }
    }

    @Test
    public void crash_in_provider() {
        UseCaseSetup setup = new UseCaseSetup();
        try {
            setup.run(SickProviderUseCase.class, null);
            Assert.fail("Sick provider must throw npe");
        } catch (NullPointerException e ) {
            DejaVuTrace dejaVu = new DejaVuTrace(setup.getTrace());
            try {
                dejaVu.run();
                Assert.fail("Must throw the same exception as the provider did");
            } catch (NullPointerException ee ) {

            }
        }
    }


}
