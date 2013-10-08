package com.jayway.dejavu.core;

import com.jayway.dejavu.core.repository.TraceCallback;
import com.jayway.dejavu.core.repository.Tracer;

import java.util.List;
import java.util.concurrent.Callable;

public abstract class DejaVuPolicy {

    private RunningTrace runningTrace;

    private static ThreadLocal<DejaVuPolicy> dejaVuPolicyThreadLocal = new ThreadLocal<DejaVuPolicy>();

    public void setPolicyForCurrentThread() {
        dejaVuPolicyThreadLocal.set(this);
    }

    public static DejaVuPolicy getPolicyForCurrentThread() {
        return dejaVuPolicyThreadLocal.get();
    }

    public void removePolicyForCurrentThread() {
        dejaVuPolicyThreadLocal.remove();
    }

    private static TraceCallback callback;

    public static void initialize( TraceCallback callback ) {
        DejaVuPolicy.callback = callback;
    }

    private static PolicyFactory factory = new PolicyFactory() {
        @Override
        public DejaVuPolicy getPolicy() {
            throw new RuntimeException("Policy factory not set up! Call DejaVuPolicy.setFactory(..)");
        }
    };

    public static void setFactory(PolicyFactory factory) {
        DejaVuPolicy.factory = factory;
    }

    public static Object traced( DejaVuInterception interception) throws Throwable {
        DejaVuPolicy policy = getPolicyForCurrentThread();
        if ( policy == null ) {
            policy = factory.getPolicy();
        }
        return policy.aroundTraced(interception);
    }

    public static Object impure(DejaVuInterception interception, String integrationPoint) throws Throwable {
        DejaVuPolicy policy = getPolicyForCurrentThread();
        if (policy == null || policy.runningTrace == null) {
            return interception.proceed();
        } else {
            return policy.aroundImpure(interception, integrationPoint);
        }
    }

    public static Object attach(DejaVuInterception interception) throws Throwable {
        DejaVuPolicy policy = getPolicyForCurrentThread();
        if ( policy != null ) {
            policy.attachThread(interception);
            return null;
        } else {
            return interception.proceed();
        }
    }

    public abstract Tracer createTracer( DejaVuInterception interception );

    public TraceCallback callback() {
        return callback;
    }

    public Object aroundTraced( DejaVuInterception interception ) throws Throwable {
        setPolicyForCurrentThread();
        if ( runningTrace != null) {
            // setup is done, just proceed
            return interception.proceed();
        }

        RunningTrace trace = new RunningTrace(this, createTracer(interception));
        runningTrace = trace;
        try {
            Object result = interception.proceed();
            trace.callbackIfFinished();
            return result;
        } catch ( Throwable t) {
            trace.callbackIfFinished(t);
            throw t;
        }
    }

    public Object aroundImpure(DejaVuInterception interception, String integrationPoint) throws Throwable {
        if ( runningTrace.shouldProceed() ) {
            return interception.proceed();
        }

        return runningTrace.aroundImpure(interception, integrationPoint);
    }

    public void attachThread( DejaVuInterception interception) throws Throwable {
        if ( runningTrace == null ) {
            interception.proceed();
        } else {
            Object[] args = interception.getArguments();
            patch(runningTrace, args);
            interception.proceed(args);
        }
    }

    public void patchForAttachThread(Object[] args) {
        if ( runningTrace != null ) {
            patch(runningTrace, args);
        }
    }

    private void patch(RunningTrace trace, Object[] args){
        for (int i=0; i<args.length; i++) {
            Object arg = args[i];
            if ( arg instanceof Runnable ) {
                args[i] = trace.toBeAttached((Runnable) arg);
            } else if ( arg instanceof Callable) {
                args[i] = trace.toBeAttached((Callable) arg);
            }
        }
    }

    public void completed(Trace trace, Throwable t, List<ThreadThrowable> threadThrowables) {
        TraceCallback cb = callback();
        if ( cb != null  ) {
            cb.traced(trace, t, threadThrowables);
        }
        runningTrace = null;
        removePolicyForCurrentThread();
    }
}
