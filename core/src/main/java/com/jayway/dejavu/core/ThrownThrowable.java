package com.jayway.dejavu.core;

/**
 * Class used how throwable that was thrown in a trace
 */
public class ThrownThrowable {
    private Throwable throwable;

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
