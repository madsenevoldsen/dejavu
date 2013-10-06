package com.jayway.dejavu.core;

public interface ImpureHandler {

    void before(RunningTrace runningTrace, String integrationPoint);

    void after(RunningTrace runningTrace, Object result);
}
