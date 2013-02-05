package com.jayway.dejavu.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * All arguments for these methods will be
 * scanned for <i>Runnable</i> instances. These
 * instances will be assumed to be run on other
 * threads than the current.
 *
 * Examples:
 *
 * private ExecutorService threadPool;
 *
 * @AttachThread
 * public void runInThreadPool( Runnable runnable ) {
 *     threadPool.submit( runnable );
 * }
 *
 * @AttachThread
 * public void runInNewThread( Runnable runnable ) {
 *     new Thread( runnable ).start();
 * }
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AttachThread {
}
