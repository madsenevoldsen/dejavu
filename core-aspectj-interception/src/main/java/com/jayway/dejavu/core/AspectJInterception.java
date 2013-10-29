package com.jayway.dejavu.core;

import com.jayway.dejavu.core.interfaces.Interception;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

public class AspectJInterception implements Interception {

    private ProceedingJoinPoint proceedingJoinPoint;
    private String integrationPoint;
    private Object[] arguments;
    private String threadId;

    public AspectJInterception(ProceedingJoinPoint proceedingJoinPoint ) {
        this(proceedingJoinPoint, "");
    }

    public AspectJInterception(ProceedingJoinPoint proceedingJoinPoint, String integrationPoint ) {
        this.proceedingJoinPoint = proceedingJoinPoint;
        this.integrationPoint = integrationPoint;
    }

    @Override
    public Method getMethod() {
        return ((MethodSignature)proceedingJoinPoint.getSignature()).getMethod();
    }

    @Override
    public Object proceed() throws Throwable {
        if ( arguments == null ) {
            return proceedingJoinPoint.proceed();
        }
        return proceedingJoinPoint.proceed(arguments);
    }

    @Override
    public Object[] getArguments() {
        return proceedingJoinPoint.getArgs();
    }

    @Override
    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    @Override
    public String integrationPoint() {
        return integrationPoint;
    }

    @Override
    public String threadId() {
        return threadId;
    }

    @Override
    public void threadId(String threadId) {
        this.threadId = threadId;
    }
}
