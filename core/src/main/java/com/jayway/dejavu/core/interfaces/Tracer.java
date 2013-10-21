package com.jayway.dejavu.core.interfaces;

import com.jayway.dejavu.core.interfaces.DejaVuInterception;
import com.jayway.dejavu.core.interfaces.Trace;

public interface Tracer {

    Object nextValue(String threadId, DejaVuInterception interception) throws Throwable;

    String getNextChildThreadId(String parentThreadId);

    Trace getTrace();
}
