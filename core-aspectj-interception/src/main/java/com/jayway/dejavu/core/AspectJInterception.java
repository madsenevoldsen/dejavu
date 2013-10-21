package com.jayway.dejavu.core;

import com.jayway.dejavu.core.interfaces.DejaVuInterception;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class AspectJInterception implements DejaVuInterception {

    private ProceedingJoinPoint proceedingJoinPoint;

    public AspectJInterception(ProceedingJoinPoint proceedingJoinPoint ) {
        this.proceedingJoinPoint = proceedingJoinPoint;
    }

    @Override
    public Method getMethod() {
        /*Signature signature = proceedingJoinPoint.getSignature();
        if ( signature instanceof MethodSignature ) {
            return ((MethodSignature) signature).getMethod();
        } else if ( signature instanceof ConstructorSignature ) {
            Constructor constructor = ((ConstructorSignature) signature).getConstructor();

        }

        return signature.getDeclaringType();

        //*/return ((MethodSignature)proceedingJoinPoint.getSignature()).getMethod();
    }

    @Override
    public Class getReturnType() {
        Signature signature = proceedingJoinPoint.getSignature();
        if ( signature instanceof MethodSignature ) {
            return ((MethodSignature)proceedingJoinPoint.getSignature()).getReturnType();
        }
        return signature.getDeclaringType();
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
