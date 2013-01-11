package com.jayway.dejavu;

import com.jayway.dejavu.core.DejaVuUseCase;
import com.jayway.dejavu.core.Trace;
import com.jayway.dejavu.core.value.*;
import com.jayway.dejavu.dto.TraceDTO;
import com.jayway.dejavu.impl.*;
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ExampleFromSerialized {

    @Test
    public void run() {
        Marshaller marshaller = new Marshaller();
        List<Value> values = new ArrayList<Value>();
        values.add( marshaller.unmarshal(VoidValue.class, null));
        values.add( marshaller.unmarshal(LongValue.class, "{ \"value\": 349013193767909 }"));
        values.add( marshaller.unmarshal(LongValue.class, "{ \"value\": 349013194166199 }"));
        values.add( marshaller.unmarshal(StringValue.class, "{ \"string\": \"d09c2893-2835-4cbe-8c8e-4c790c268ed0\" }"));

        DejaVuUseCase dejaVu = new DejaVuUseCase(ExampleUseCase.class, values);
        try {
            dejaVu.run();
            Assert.fail();
        } catch ( ArithmeticException e ) {

        }
    }


    @Test
    public void generateTrace() {
        UseCaseSetup setup = new UseCaseSetup();
        try {
            setup.run( ExampleUseCase.class, null );
        } catch (ArithmeticException e ) {
            Marshaller marshaller = new Marshaller();
            Trace original = setup.getTrace();
            TraceDTO marshal = marshaller.marshal(original);

            Trace trace = marshaller.unmarshal(marshal);
            Assert.assertEquals( original.getTracedElements().size(), trace.getTracedElements().size() );
        }
    }

    @Test
    public void non_deterministic() {
        UseCaseSetup setup = new UseCaseSetup();
        try {
            while ( true ) {
                setup.run( AlmostWorkingUseCase.class, null );
            }
        } catch (Exception e ) {
            // this is an uncommon exception

            TestGenerator generator = new TestGenerator();
            Trace trace = setup.getTrace();
            String test = generator.generateTest("com.jayway.dejavu.Generated", trace);
            // generate test that reproduces the hard bug
            System.out.println( test );
        }
    }

    @Test
    public void test() {
        Marshaller marshaller = new Marshaller();
        List<Value> values = new ArrayList<Value>();
        values.add(marshaller.unmarshal(VoidValue.class, "null"));
        values.add(marshaller.unmarshal(LongValue.class, "{\"value\":395972346776495}"));
        values.add(marshaller.unmarshal(ExceptionValue.class, "{\"value\":\"com.jayway.dejavu.impl.NotFound\"}"));

        DejaVuUseCase dejaVu = new DejaVuUseCase(BadProviderUseCase.class, values);
        try {
            dejaVu.run();
            Assert.fail( "Must throw NotFound" );
        } catch (NotFound e ) {
            // this is what we expect
        }
    }


}
