package com.jayway.dejavu.core;

public interface ImpureHandler {

    void before(RunningTrace runningTrace, String integrationPoint);

    Object success(RunningTrace runningTrace, Object result);

    Throwable failure(RunningTrace runningTrace, Throwable t);
}
