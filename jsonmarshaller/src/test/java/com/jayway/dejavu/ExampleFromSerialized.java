package com.jayway.dejavu;

import com.jayway.dejavu.core.DejaVuUseCase;
import com.jayway.dejavu.core.Trace;
import com.jayway.dejavu.value.LongValue;
import com.jayway.dejavu.value.StringValue;
import com.jayway.dejavu.value.Value;
import com.jayway.dejavu.value.VoidValue;
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
        dejaVu.run();
    }


    @Test
    public void generateTrace() {
        UseCaseSetup setup = new UseCaseSetup();
        try {
            setup.run( ExampleUseCase.class, null );
        } catch (ArithmeticException e ) {
            Marshaller marshaller = new Marshaller();
            Trace original = setup.getTrace();
            String marshal = marshaller.marshal(original);

            Trace trace = marshaller.unmarshal(marshal);
            Assert.assertEquals( original.getId(), trace.getId() );
        }
    }

    @Test
    public void hardBug() {
        UseCaseSetup setup = new UseCaseSetup();
        try {
            while ( true ) {
                setup.run( AlmostWorkingUseCase.class, null );
            }
        } catch (Exception e ) {
            // this is an uncommon exception

            TestGenerator generator = new TestGenerator();
            Trace trace = setup.getTrace();
            generator.generateTest("com.jayway.dejavu.Generated" + trace.getTime().getTime(), trace, System.out);
            // generate test that reproduces the hard bug
        }
    }

}
