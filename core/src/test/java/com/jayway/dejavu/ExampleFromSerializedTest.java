package com.jayway.dejavu;

import com.jayway.dejavu.core.DejaVuPolicy;
import com.jayway.dejavu.core.Trace;
import com.jayway.dejavu.core.marshaller.Marshaller;
import com.jayway.dejavu.impl.ClassArguments;
import com.jayway.dejavu.impl.ExampleFailingIntegrationPoint;
import com.jayway.dejavu.impl.RecurseAndExcept;
import com.jayway.dejavu.impl.TraceCallbackImpl;
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
        DejaVuPolicy.initialize(callback);
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

        DejaVuPolicy.replay(trace);
        Class testClass = compileAndClassLoad("com.jayway.dejavu.impl.ClassArgumentsTest", test);
        Object o = testClass.newInstance();

        Method method = testClass.getDeclaredMethod("classargumentstest");
        method.invoke( o );
    }

}
