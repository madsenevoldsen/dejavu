package com.jayway.dejavu;

import com.jayway.dejavu.core.DejaVuAspect;
import com.jayway.dejavu.core.DejaVuTrace;
import com.jayway.dejavu.core.Trace;
import com.jayway.dejavu.core.TraceElement;
import com.jayway.dejavu.core.marshaller.Marshaller;
import com.jayway.dejavu.core.marshaller.dto.TraceDTO;
import com.jayway.dejavu.impl.*;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ExampleFromSerializedTest {

    private TraceCallbackImpl callback;
    private Marshaller marshaller;

    @Before
    public void setup(){
        callback = new TraceCallbackImpl();
        DejaVuAspect.initialize(callback);
        marshaller = new Marshaller( new JacksonMarshallerPlugin() );
    }

    @Test
    public void run() throws Throwable {
        List<TraceElement> values = new ArrayList<TraceElement>();
        values.add( new TraceElement( "TEST_TRACE", marshaller.unmarshal(Long.class, "349013193767909")));
        values.add( new TraceElement( "TEST_TRACE", marshaller.unmarshal(Long.class, "349013194166199")));
        values.add( new TraceElement( "TEST_TRACE", marshaller.unmarshal(String.class, "\"d09c2893-2835-4cbe-8c8e-4c790c268ed0\"")));

        Trace trace = new Trace();
        trace.setId("TEST_TRACE");
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
            Trace trace = callback.getTrace();
            String test = marshaller.generateTest( trace);
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
            String test = marshaller.generateTest(callback.getTrace());
            System.out.println(test);
        }
    }

    @Test
    public void with_annotation() {
        WithAnnotation withAnnotation = new WithAnnotation();
        withAnnotation.run( WithAnnotation.State.second );

        TraceDTO dto = marshaller.marshal(callback.getTrace());
        WithAnnotation.State beforeMarshal = (WithAnnotation.State) callback.getTrace().getStartArguments()[0];

        Trace trace = marshaller.unmarshal(dto);
        WithAnnotation.State afterMarshal = (WithAnnotation.State) trace.getStartArguments()[0];
        Assert.assertEquals( beforeMarshal, afterMarshal );
    }
}
