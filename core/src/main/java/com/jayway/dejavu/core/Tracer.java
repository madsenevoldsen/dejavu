package com.jayway.dejavu.core;

import com.jayway.dejavu.core.value.Value;

public interface Tracer {
    <I,O extends Value> O provide(Provider<I, O> provider, I input);
    void provided(Value value);
    <I,O> O step(Class<? extends Step<I, O>> clazz, I input);
}
