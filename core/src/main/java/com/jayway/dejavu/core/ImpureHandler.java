package com.jayway.dejavu.core;

public interface ImpureHandler {

    void before(RunningTrace runningTrace, String integrationPoint);

    void success(RunningTrace runningTrace, Object result, Class returnType);

    void failure(RunningTrace runningTrace, Throwable t);
}
