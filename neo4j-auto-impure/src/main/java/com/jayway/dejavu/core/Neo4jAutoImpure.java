package com.jayway.dejavu.core;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class Neo4jAutoImpure {

    @Around("call(* org.neo4j.graphdb.GraphDatabaseService.*(..))")
    public Object dbService(ProceedingJoinPoint proceed ) throws Throwable {
        return impureMethod(proceed);
    }

    @Around("call(* org.neo4j.graphdb.Node.*(..))")
    public Object node(ProceedingJoinPoint proceed ) throws Throwable {
        return impureMethod(proceed);
    }

    @Around("call(* org.neo4j.graphdb.Transaction.*(..))")
    public Object transaction(ProceedingJoinPoint proceed ) throws Throwable {
        return impureMethod(proceed);
    }

    @Around("call(* org.neo4j.graphdb.index.IndexManager.*(..))")
    public Object indexManager(ProceedingJoinPoint proceed ) throws Throwable {
        return impureMethod(proceed);
    }

    @Around("call(* org.neo4j.graphdb.index.Index.*(..))")
    public Object index(ProceedingJoinPoint proceed ) throws Throwable {
        return impureMethod(proceed);
    }

    @Around("call(* org.neo4j.graphdb.index.IndexHits.*(..))")
    public Object indexHits(ProceedingJoinPoint proceed ) throws Throwable {
        return impureMethod(proceed);
    }

    private Object impureMethod(ProceedingJoinPoint proceed) throws Throwable {
        // if already inside an @impure just proceed
        if ( DejaVuAspect.fallThrough() ) {
            return proceed.proceed();
        }
        return DejaVuAspect.handle(proceed, "neo4j");
    }

}
