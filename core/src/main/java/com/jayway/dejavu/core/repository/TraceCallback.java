package com.jayway.dejavu.core.repository;

import com.jayway.dejavu.core.Trace;

public interface TraceCallback {
    void traced( Trace trace, Throwable cause );
}
