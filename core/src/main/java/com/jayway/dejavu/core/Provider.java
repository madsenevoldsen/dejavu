package com.jayway.dejavu.core;

import com.jayway.dejavu.value.Value;

public interface Provider<I, O extends Value> {
    O request( I input );
}
