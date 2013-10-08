package com.jayway.dejavu.core;

import com.jayway.dejavu.core.repository.RecordingTracer;
import com.jayway.dejavu.core.repository.ReplayTracer;
import com.jayway.dejavu.core.repository.Tracer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class RecordReplayer extends DejaVuPolicy {

    protected boolean done = false;

    public static <T> T replay( final Trace trace ) throws Throwable {
        RecordReplayer replayer = new RecordReplayer() {
            @Override
            public Tracer createTracer(DejaVuInterception interception) {
                return new ReplayTracer(trace);
            }
        };
        replayer.setPolicyForCurrentThread();
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
            replayer.removePolicyForCurrentThread();
        }
    }

    protected boolean isDone() {
        return done;
    }

    @Override
    public Tracer createTracer(DejaVuInterception interception) {
        Trace trace = new Trace(interception.getMethod(), interception.getArguments());
        trace.setId( RunningTrace.generateId());
        return new RecordingTracer(trace);
    }

    @Override
    public void completed(Trace trace, Throwable t, List<ThreadThrowable> threadThrowables) {
        done = true;
        super.completed(trace, t, threadThrowables);
    }
}