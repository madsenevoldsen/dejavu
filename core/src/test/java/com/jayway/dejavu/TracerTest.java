package com.jayway.dejavu;

import com.jayway.dejavu.core.DejaVuUseCase;
import com.jayway.dejavu.core.TraceEndedException;
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
            DejaVuUseCase dejaVu = new DejaVuUseCase(setup.getTrace());
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
            DejaVuUseCase dejaVu = new DejaVuUseCase(setup.getTrace());
            try {
                dejaVu.run();
                Assert.fail("must throw a trace ended exception since a sick provider was called");
            } catch (TraceEndedException ee ) {

            }
        }
    }

    @Test
    public void crash_behind_circuit_breaker() {
        UseCaseSetup setup = new UseCaseSetup();
        try {
            setup.run(CircuitBreakerUseCase.class, null);
        } catch ( NullPointerException e ) {
            DejaVuUseCase useCase = new DejaVuUseCase(setup.getTrace());
            try {
                useCase.run();
                Assert.fail("runs as before so must throw npe");
            } catch (NullPointerException ee ) {

            }
        }

    }
}
