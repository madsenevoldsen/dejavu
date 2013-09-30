package com.jayway.dejavu.core;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.neo4j.helpers.collection.PagingIterator;

import java.util.Iterator;

import static org.easymock.EasyMock.createMock;

@Aspect
public class Neo4jAutoImpure {

    static {
        // install this as a type helper
        DejaVuPolicy.addTypeHelper( new Neo4jTypeInference() );
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
        DejaVuPolicy policy = new DejaVuPolicy();
        return policy.aroundImpure(new AspectJInterception( proceed), "neo4j");
    }

    private <T> T impureConstruction( ProceedingJoinPoint proceed, Class<T> clazz ) throws Throwable {
        // if already inside an @impure just proceed
        DejaVuPolicy policy = new DejaVuPolicy();
        if ( policy.justProceed()) {
            return (T) proceed.proceed();
        }
        if ( policy.isRecording() ) {

            policy.setIgnore(true);
            T t  = (T) proceed.proceed();
            policy.setIgnore(false);
            return t;
        } else {
            // if test mode we have to mock the object, because constructing
            // the object might not be possible
            return createMock(clazz);
        }
    }

}
