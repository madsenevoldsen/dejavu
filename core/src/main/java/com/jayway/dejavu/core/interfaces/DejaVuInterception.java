package com.jayway.dejavu.core.interfaces;

import java.lang.reflect.Method;

public interface DejaVuInterception {

    Method getMethod();

    Class getReturnType();

    Object proceed() throws Throwable;

    Object proceed(Object[] changedArguments) throws Throwable;

    Object[] getArguments();

}
