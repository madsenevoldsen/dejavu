package com.jayway.dejavu.core.interfaces;

import java.lang.reflect.Method;

public class InterceptionAdapter implements Interception {

    private Interception interception;

    public InterceptionAdapter( Interception interception ) {
        this.interception = interception;
    }

    @Override
    public Method getMethod() {
        return interception.getMethod();

    }

    @Override
    public Object proceed() throws Throwable {
        return interception.proceed();
    }

    @Override
    public Object[] getArguments() {
        return interception.getArguments();
    }

    @Override
    public void setArguments(Object[] arguments) {
        interception.setArguments( arguments );
    }

    @Override
    public String integrationPoint() {
        return interception.integrationPoint();
    }

    @Override
    public String threadId() {
        return interception.threadId();
    }

    @Override
    public void threadId(String threadId) {
        interception.threadId( threadId );
    }
}
