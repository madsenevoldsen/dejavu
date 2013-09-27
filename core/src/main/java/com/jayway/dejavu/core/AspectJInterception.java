package com.jayway.dejavu.core;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

public class AspectJInterception implements DejaVuInterception {

    private ProceedingJoinPoint proceedingJoinPoint;

    public AspectJInterception(ProceedingJoinPoint proceedingJoinPoint ) {
        this.proceedingJoinPoint = proceedingJoinPoint;
    }

    @Override
    public Method getMethod() {
        return ((MethodSignature)proceedingJoinPoint.getSignature()).getMethod();
    }

    @Override
    public Class getReturnType() {
        return ((MethodSignature)proceedingJoinPoint.getSignature()).getReturnType();
    }

    @Override
    public Object proceed() throws Throwable {
        return proceedingJoinPoint.proceed();
    }

    @Override
    public Object proceed(Object[] changedArguments) throws Throwable {
        return proceedingJoinPoint.proceed( changedArguments );
    }

    @Override
    public Object[] getArguments() {
        return proceedingJoinPoint.getArgs();
    }
}
