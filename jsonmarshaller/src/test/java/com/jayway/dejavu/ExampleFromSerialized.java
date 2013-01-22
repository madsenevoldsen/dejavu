package com.jayway.dejavu;

import com.jayway.dejavu.core.DejaVuAspect;
import com.jayway.dejavu.core.DejaVuTrace;
import com.jayway.dejavu.core.Trace;
import com.jayway.dejavu.dto.TraceDTO;
import com.jayway.dejavu.impl.*;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ExampleFromSerialized {

    private TraceCallbackImpl callback;

    @Before
    public void setup(){
        callback = new TraceCallbackImpl();
        DejaVuAspect.setCallback(callback);
    }

    @Test
    public void run() throws Throwable {
        Marshaller marshaller = new Marshaller();
        List<Object> values = new ArrayList<Object>();
        values.add( marshaller.unmarshal(Long.class, "349013193767909"));
        values.add( marshaller.unmarshal(Long.class, "349013194166199"));
        values.add( marshaller.unmarshal(String.class, "\"d09c2893-2835-4cbe-8c8e-4c790c268ed0\""));

        Trace trace = new Trace();
        trace.setStartPoint(Example.class.getDeclaredMethod("run"));
        trace.setValues( values );
        try {
            DejaVuTrace.run(trace);
            Assert.fail();
        } catch (ArithmeticException e) {

        }

    }


    @Test
    public void generateTrace() {
        try {
            new Example().run();
        } catch (ArithmeticException e ) {
            Marshaller marshaller = new Marshaller();
            Trace original = callback.getTrace();
            TraceDTO marshal = marshaller.marshal(original);

            Trace trace = marshaller.unmarshal(marshal);
            //Assert.assertEquals(original.getTracedElements().size(), trace.getTracedElements().size());
        }
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

            TestGenerator generator = new TestGenerator();
            Trace trace = callback.getTrace();
            String test = generator.generateTest( trace);
            // generate test that reproduces the hard bug
            System.out.println( test );
        }
    }


    @Test
    public void failing_integration_point() {
        ExampleFailingIntegrationPoint point = new ExampleFailingIntegrationPoint();
        try {
            point.run("first", "second");
        } catch (ArithmeticException e ) {
            TestGenerator generator = new TestGenerator();
            String test = generator.generateTest(callback.getTrace());
            System.out.println(test);
        }
    }

    @Test
    public void with_annotation() {
        WithAnnotation withAnnotation = new WithAnnotation();
        withAnnotation.run( WithAnnotation.State.second );

        Marshaller marshaller = new Marshaller();
        TraceDTO dto = marshaller.marshal(callback.getTrace());
        WithAnnotation.State beforeMarshal = (WithAnnotation.State) callback.getTrace().getStartArguments()[0];

        Trace trace = marshaller.unmarshal(dto);
        WithAnnotation.State afterMarshal = (WithAnnotation.State) trace.getStartArguments()[0];
        Assert.assertEquals( beforeMarshal, afterMarshal );
    }
}
