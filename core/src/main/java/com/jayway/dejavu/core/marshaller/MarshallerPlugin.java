package com.jayway.dejavu.core.marshaller;

import com.jayway.dejavu.core.TraceElement;

/**
 * Implementation of this interface can be supplied to
 * the Marshaller class upon construction.
 */
public interface MarshallerPlugin {
    Object unmarshal( Class<?> clazz, String marshalValue );
    String marshalObject( Object value );
    String asTraceBuilderArgument( TraceElement element );
}
