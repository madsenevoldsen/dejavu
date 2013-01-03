package com.jayway.dejavu.repository;

import com.jayway.dejavu.core.Trace;

public interface TraceRepository {

    void storeTrace( Trace trace );
}
