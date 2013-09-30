package com.jayway.dejavu.core;

import com.jayway.dejavu.core.annotation.Impure;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;


@Aspect
public class DejaVuAspect {

    @Around("execution(@com.jayway.dejavu.core.annotation.Traced * *(..))")
    public Object traced( ProceedingJoinPoint proceed ) throws Throwable {
        return new DejaVuPolicy().aroundTraced( new AspectJInterception( proceed ));
    }

    @Around("execution(@com.jayway.dejavu.core.annotation.Impure * *(..)) && @annotation(impure)")
    public Object integrationPoint( ProceedingJoinPoint proceed, Impure impure) throws Throwable {
        return new DejaVuPolicy().aroundImpure( new AspectJInterception( proceed), impure.integrationPoint());
    }

    @Around("execution(@com.jayway.dejavu.core.annotation.AttachThread * *(..))")
    public void attach( ProceedingJoinPoint proceed ) throws Throwable {
        new DejaVuPolicy().attachThread( new AspectJInterception(proceed));
    }
}
