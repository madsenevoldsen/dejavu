package com.jayway.dejavu;

import com.jayway.dejavu.core.DejaVuEngine;
import com.jayway.dejavu.core.DejaVuEngine;
import com.jayway.dejavu.core.interfaces.Trace;
import com.jayway.dejavu.core.TraceElement;
import com.jayway.dejavu.unittest.Marshaller;
import com.jayway.dejavu.helper.WithSimpleTypes;
import com.jayway.dejavu.impl.*;
import com.jayway.dejavu.recordreplay.RecordReplayFactory;
import com.jayway.dejavu.recordreplay.RecordReplayer;
import junit.framework.Assert;
import org.abstractmeta.toolbox.compilation.compiler.JavaSourceCompiler;
import org.abstractmeta.toolbox.compilation.compiler.impl.JavaSourceCompilerImpl;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

public class ExampleFromSerializedTest {

    private TraceCallbackImpl callback;

    @Before
    public void setup(){
        callback = new TraceCallbackImpl();
        DejaVuEngine.initialize(callback);
        DejaVuEngine.setFactory(new RecordReplayFactory());
    }

    @Test
    public void with_serialized_exception() throws Throwable {
        try {
            new ExampleFailingIntegrationPoint().run( "first", "second" );
        } catch ( ArithmeticException e ) {
            String test = new Marshaller().marshal( callback.getTrace() );
            System.out.println( test );

            Class testClass = compileAndClassLoad("com.jayway.dejavu.impl.ExampleFailingIntegrationPointTest", test);
            Object o = testClass.newInstance();

            try {
                Method method = testClass.getDeclaredMethod("examplefailingintegrationpointtest");
                method.invoke( o );
                Assert.fail();
            } catch (Throwable t ) {
                Assert.assertEquals("/ by zero", e.getMessage() );
                Assert.assertEquals( e.getStackTrace().length, t.getCause().getStackTrace().length);
                Assert.assertEquals( e.getStackTrace()[0].toString(), e.getStackTrace()[0].toString() );
            }
        }
    }

    private Class compileAndClassLoad( String className, String sourceCode ) throws ClassNotFoundException {
        JavaSourceCompiler compiler = new JavaSourceCompilerImpl();
        JavaSourceCompiler.CompilationUnit compilationUnit = compiler.createCompilationUnit();
        compilationUnit.addJavaSource(className, sourceCode );
        ClassLoader classLoader = compiler.compile(compilationUnit);
        return classLoader.loadClass(className);
    }


    @Test
    public void properly_exception_trace_in_rerun() throws Throwable {
        try {
            new RecurseAndExcept().recurse( 0 );
        } catch (RuntimeException e ) {
            String test = new Marshaller().marshal( callback.getTrace() );
            System.out.println(test);

            Class testClass = compileAndClassLoad("com.jayway.dejavu.impl.RecurseAndExceptTest", test);
            Object o = testClass.newInstance();

            //Method method = testClass.getDeclaredMethod("recurseandexcepttest");
            //method.invoke( o );
        }
    }

    @Test
    public void class_arguments() throws Throwable {
        ClassArguments obj = new ClassArguments();
        obj.withClass( ClassArguments.class );

        Trace trace = callback.getTrace();
        String test = new Marshaller().marshal( trace );
        System.out.println( test );

        RecordReplayer.replay(trace);
        Class testClass = compileAndClassLoad("com.jayway.dejavu.impl.ClassArgumentsTest", test);
        Object o = testClass.newInstance();

        Method method = testClass.getDeclaredMethod("classargumentstest");
        method.invoke( o );
    }

    @Test
    public void verify_generated_test() throws Throwable {
        TraceCallbackImpl callback = new TraceCallbackImpl();
        RecordReplayer.initialize(callback);

        final Integer origResult = new WithSimpleTypes().simple();

        Trace original = callback.getTrace();
        String test = new Marshaller().marshal(original);
        System.out.println( test );

        JavaSourceCompiler compiler = new JavaSourceCompilerImpl();
        JavaSourceCompiler.CompilationUnit compilationUnit = compiler.createCompilationUnit();
        compilationUnit.addJavaSource("com.jayway.dejavu.helper.WithSimpleTypesTest", test );
        ClassLoader classLoader = compiler.compile(compilationUnit);
        Class testClass = classLoader.loadClass("com.jayway.dejavu.helper.WithSimpleTypesTest");

        Object o = testClass.newInstance();
        Method method = testClass.getDeclaredMethod("withsimpletypestest");
        method.invoke( o );
        Trace newRun = callback.getTrace();
        Assert.assertNotSame( "We except a different result now", original, newRun );
        // validate trace
        int result = 1;
        int size = 0;
        int firstElement = 0;
        for ( TraceElement element: newRun ) {
            size++;
            if (size == 1) {
                firstElement = (Integer) element.getValue();
                continue;
            }
            result *= (Integer) element.getValue();
        }
        Assert.assertEquals( firstElement+1, size );
        Assert.assertEquals( origResult.intValue(), result );
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
