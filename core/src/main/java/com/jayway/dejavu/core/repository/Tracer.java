package com.jayway.dejavu.core.repository;

import com.jayway.dejavu.core.DejaVuInterception;
import com.jayway.dejavu.core.Trace;

public interface Tracer {

    Object nextValue(String threadId, DejaVuInterception interception) throws Throwable;

    String getNextChildThreadId(String parentThreadId);

    Trace getTrace();
}
