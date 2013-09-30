package com.jayway.dejavu.core;

public class DejaVuImpureHandler implements ImpureHandler {

    @Override
    public void before(RunningTrace runningTrace, String integrationPoint) {
        runningTrace.enterImpure();
    }

    @Override
    public void success(RunningTrace runningTrace, Object result, Class returnType) {
        runningTrace.exitImpure();
        runningTrace.add( result, returnType );
    }

    @Override
    public void failure(RunningTrace runningTrace, Throwable t) {
        runningTrace.exitImpure();
        runningTrace.add( new ThrownThrowable(t), ThrownThrowable.class );
    }
}
