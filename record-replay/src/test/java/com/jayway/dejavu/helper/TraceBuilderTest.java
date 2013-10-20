package com.jayway.dejavu.helper;

import com.jayway.dejavu.core.*;
import com.jayway.dejavu.core.marshaller.Marshaller;
import com.jayway.dejavu.impl.ExampleTrace;
import com.jayway.dejavu.impl.TraceCallbackImpl;
import com.jayway.dejavu.recordreplay.RecordReplayFactory;
import com.jayway.dejavu.recordreplay.RecordReplayer;
import junit.framework.Assert;
import org.abstractmeta.toolbox.compilation.compiler.JavaSourceCompiler;
import org.abstractmeta.toolbox.compilation.compiler.impl.JavaSourceCompilerImpl;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

public class TraceBuilderTest {

    @Before
    public void setup() {
        DejaVuPolicy.setFactory( new RecordReplayFactory());
    }

    @Test
    public void builder() throws Throwable {
        TraceBuilder builder = new MemoryTraceBuilder().startMethod(ExampleTrace.class);

        builder.add( 349013193767909L, "d09c2893-2835-4cbe-8c8e-4c790c268ed0", 349013194166199L);

        try {
            RecordReplayer.replay(builder.build());
            Assert.fail();
        } catch (ArithmeticException e) {

        }
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
    public void simple_types_test() throws Throwable {
        TraceBuilder builder = new MemoryTraceBuilder().startMethod(AllSimpleTypes.class);

        builder.add( "string", 1.1F, true, 2.2, 1L, 1 );

        String result = RecordReplayer.replay(builder.build());

        Assert.assertEquals("string1.1true2.211", result );
    }
}