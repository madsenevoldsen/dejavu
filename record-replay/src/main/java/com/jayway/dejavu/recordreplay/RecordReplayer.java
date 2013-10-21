package com.jayway.dejavu.recordreplay;

import com.jayway.dejavu.core.DejaVuEngine;
import com.jayway.dejavu.core.ThreadThrowable;
import com.jayway.dejavu.core.TraceBuilder;
import com.jayway.dejavu.core.interfaces.DejaVuInterception;
import com.jayway.dejavu.core.interfaces.Trace;
import com.jayway.dejavu.core.interfaces.TraceValueHandler;
import com.jayway.dejavu.core.interfaces.Tracer;
import com.jayway.dejavu.core.memorytrace.MemoryTraceBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class RecordReplayer extends DejaVuEngine {

    protected boolean done = false;

    public static <T> T replay( final Trace trace, final TraceValueHandler... handlers  ) throws Throwable {
        RecordReplayer replayer = new RecordReplayer() {
            @Override
            public Tracer createTracer(DejaVuInterception interception) {
                return new ReplayTracer(trace, handlers);
            }
        };
        replayer.setEngineForCurrentThread();
        Method method = trace.getStartPoint();
        Class<?> aClass = method.getDeclaringClass();

        try {
            Object instance = aClass.newInstance();
            // wait until trace ended???
            return (T) method.invoke(instance, trace.getStartArguments());
        } catch (InvocationTargetException ee ) {
            replayer.done = true;
            throw ee.getTargetException();
        } finally {
            while (!replayer.isDone()) {
                // wait until finished
                Thread.sleep(500);
            }
            replayer.removeEngineForCurrentThread();
        }
    }

    protected boolean isDone() {
        return done;
    }

    @Override
    public Tracer createTracer(DejaVuInterception interception) {
        TraceBuilder builder = new MemoryTraceBuilder(DejaVuEngine.generateId());
        builder.startMethod(interception.getMethod());
        builder.startArguments(interception.getArguments());
        return new RecordingTracer( builder );
    }

    @Override
    public void completed(Trace trace, Throwable t, List<ThreadThrowable> threadThrowables) {
        done = true;
        super.completed(trace, t, threadThrowables);
    }
}