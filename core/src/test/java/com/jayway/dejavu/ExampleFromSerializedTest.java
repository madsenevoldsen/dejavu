package com.jayway.dejavu;

import com.jayway.dejavu.core.DejaVuAspect;
import com.jayway.dejavu.core.marshaller.Marshaller;
import com.jayway.dejavu.impl.ExampleFailingIntegrationPoint;
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
        DejaVuAspect.initialize(callback);
    }

    @Test
    public void with_serialized_exception() throws Throwable {
        try {
            new ExampleFailingIntegrationPoint().run( "first", "second" );
        } catch ( ArithmeticException e ) {
            String test = new Marshaller().marshal( callback.getTrace() );
            System.out.println( test );

            JavaSourceCompiler compiler = new JavaSourceCompilerImpl();
            JavaSourceCompiler.CompilationUnit compilationUnit = compiler.createCompilationUnit();
            compilationUnit.addJavaSource("com.jayway.dejavu.impl.ExampleFailingIntegrationPointTest", test );
            ClassLoader classLoader = compiler.compile(compilationUnit);
            Class testClass = classLoader.loadClass("com.jayway.dejavu.impl.ExampleFailingIntegrationPointTest");

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
}
