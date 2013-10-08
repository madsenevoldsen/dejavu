package com.jayway.dejavu.core;

import com.jayway.dejavu.neo4j.Neo4jTraceValueHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.neo4j.helpers.collection.PagingIterator;

import java.util.Iterator;

@Aspect
public class Neo4jAutoImpure {

    public static void initialize() {
        RunningTrace.addTraceHandler(new Neo4jTraceValueHandler());
    }

    @Around("call(* org.neo4j.graphdb.GraphDatabaseService.*(..))")
    public Object dbService(ProceedingJoinPoint proceed ) throws Throwable {
        return impureMethod(proceed);
    }

    @Around("call(* org.neo4j.graphdb.Node.*(..))")
    public Object node(ProceedingJoinPoint proceed ) throws Throwable {
        if ( proceed.getSignature().getName().equals("getPropertyKeys") ) {
            Iterator iterator = ((Iterable) proceed.proceed()).iterator();
            return new Neo4jIterator( iterator );
        }
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

    @Around("call(org.neo4j.helpers.collection.PagingIterator.new(..))")
    public PagingIterator paging(ProceedingJoinPoint proceed ) throws Throwable {
        return impureConstruction(proceed, PagingIterator.class);
    }

    @Around("call(* org.neo4j.helpers.collection.PagingIterator.*(..))")
    public Object pageIterator(ProceedingJoinPoint proceed ) throws Throwable {
        if ( proceed.getSignature().getName().equals("nextPage") ) {
            Iterator iterator = (Iterator) proceed.proceed();
            return new Neo4jIterator(iterator);
        }
        return impureMethod(proceed);
    }

    private Object impureMethod(ProceedingJoinPoint proceed) throws Throwable {
        return DejaVuPolicy.impure(new AspectJInterception( proceed), "neo4j");
    }

    private <T> T impureConstruction( ProceedingJoinPoint proceed, Class<T> clazz ) throws Throwable {
        // if already inside an @impure just proceed
        return (T) DejaVuPolicy.impure(new AspectJInterception(proceed), "neo4j");
    }

}
