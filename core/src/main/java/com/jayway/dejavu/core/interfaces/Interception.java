package com.jayway.dejavu.core.interfaces;

import java.lang.reflect.Method;

public interface Interception {

    Method getMethod();

    Object proceed() throws Throwable;

    Object[] getArguments();

    void setArguments( Object[] arguments);

    String integrationPoint();

    String threadId();

    void threadId( String threadId );
}
