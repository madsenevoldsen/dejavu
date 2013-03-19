package com.jayway.dejavu.core;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.Callable;

import static org.easymock.EasyMock.createMock;

@Aspect
public class AutoImpure {

    /*@Around("call(* java.util.concurrent.ExecutorService.invoke*(..))")
    public Object threadPoolInvoke( ProceedingJoinPoint join ) throws Throwable {
        Collection<Callable> callables = (Collection<Callable>) join.getArgs()[0];
        for (Callable callable : callables) {
            DejaVuAspect.patch( callable );
        }
        return join.proceed( callables.toArray() );
    } */

    @Around("call(* java.util.concurrent.ExecutorService.submit(..))")
    public Object threadPoolSubmit( ProceedingJoinPoint join ) throws Throwable {
        Object[] args = join.getArgs();
        DejaVuAspect.patch(args);
        return join.proceed( args );
    }

    @Around("call(java.util.Random.new(..))")
    public Random random(ProceedingJoinPoint proceed ) throws Throwable {
        return impureConstruction( proceed, Random.class );
    }

    @Around("call(* java.util.Random.*(..))")
    public Object randomMethods(ProceedingJoinPoint proceed ) throws Throwable {
        return impureMethod(proceed);
    }

    @Around("call(java.io.FileReader.new(..))")
    public FileReader fileReader(ProceedingJoinPoint proceed ) throws Throwable {
        return impureConstruction(proceed, FileReader.class);
    }

    @Around("call(* java.io.FileReader.*(..))")
    public Object fileReaderMethods( ProceedingJoinPoint proceed ) throws Throwable {
        return impureMethod(proceed);
    }

    @Around("call(java.io.BufferedReader.new(..))")
    public BufferedReader buffered(ProceedingJoinPoint proceed ) throws Throwable {
        return impureConstruction(proceed, BufferedReader.class);
    }

    @Around("call(* java.io.BufferedReader.*(..))")
    public Object bufferedReaderMethods( ProceedingJoinPoint proceed ) throws Throwable {
        return impureMethod(proceed);
    }

    @Around("call(* java.util.UUID.toString(..))")
    public Object uuid( ProceedingJoinPoint proceed ) throws Throwable {
        return impureMethod(proceed);
    }

    private Object impureMethod(ProceedingJoinPoint proceed) throws Throwable {
        // if already inside an @impure just proceed
        if ( DejaVuAspect.fallThrough() ) {
            return proceed.proceed();
        }
        return DejaVuAspect.handle(proceed, "");
    }

    private <T> T impureConstruction( ProceedingJoinPoint proceed, Class<T> clazz ) throws Throwable {
        // if already inside an @impure just proceed
        if ( DejaVuAspect.fallThrough() ) {
            return (T) proceed.proceed();
        }
        if ( DejaVuAspect.isTraceMode() ) {
            DejaVuAspect.setIgnore(true);
            T t  = (T) proceed.proceed();
            DejaVuAspect.setIgnore(false);
            return t;
        } else {
            // if test mode we have to mock the object, because constructing
            // the object might now be possible
            return createMock(clazz);
        }
    }
}
