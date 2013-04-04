package com.jayway.dejavu.core;

/**
 * Class used to indicate that a throwable was thrown in a trace.
 * This is to be able to distinguish from returned throwables
 */
public class ThrownThrowable {
    private Throwable throwable;

    public ThrownThrowable () {}
    ThrownThrowable( Throwable throwable ) {
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}
