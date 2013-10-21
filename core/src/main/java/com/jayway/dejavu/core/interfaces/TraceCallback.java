package com.jayway.dejavu.core.interfaces;

import com.jayway.dejavu.core.ThreadThrowable;
import com.jayway.dejavu.core.interfaces.Trace;

import java.util.List;

public interface TraceCallback {
    void traced( Trace trace, Throwable cause, List<ThreadThrowable> threadCauses );
}
