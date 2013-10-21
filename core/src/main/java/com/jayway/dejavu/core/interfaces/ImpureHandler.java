package com.jayway.dejavu.core.interfaces;

public interface ImpureHandler {

    void before(String integrationPoint);

    void after(Object result);
}
