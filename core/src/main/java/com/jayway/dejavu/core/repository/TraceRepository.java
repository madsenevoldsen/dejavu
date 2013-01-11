package com.jayway.dejavu.core.repository;

import com.jayway.dejavu.core.Trace;

public interface TraceRepository {

    void storeTrace( RuntimeException cause, Trace trace );
}
