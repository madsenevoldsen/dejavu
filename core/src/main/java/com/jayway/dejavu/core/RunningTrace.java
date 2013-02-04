package com.jayway.dejavu.core;

import java.util.HashSet;
import java.util.Set;

public class RunningTrace {

    private Trace trace;
    private Throwable throwable;
    private Set<String> attachedThreads;
    private Set<String> completedThreads;

    public RunningTrace( Trace trace ) {
        this.trace = trace;
        attachedThreads = new HashSet<String>();
        completedThreads = new HashSet<String>();
    }

    public synchronized void threadAttached( String id ) {
        if ( attachedThreads == null ) {
            attachedThreads = new HashSet<String>();
            completedThreads = new HashSet<String>();
        }
        attachedThreads.add( id );
    }

    public synchronized void threadCompleted( String id ) {
        completedThreads.add( id );
    }

    public synchronized boolean completed() {
        return attachedThreads == null || attachedThreads.size() == completedThreads.size();
    }

    public Trace getTrace() {
        return trace;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}
