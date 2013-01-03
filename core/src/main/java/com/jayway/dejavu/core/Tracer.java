package com.jayway.dejavu.core;

import com.jayway.dejavu.value.Value;

public interface Tracer {
    public <I,O extends Value> O provide(Provider<I, O> provider, I input);
    public <I,O> O step(Class<? extends Step<I, O>> clazz, I input);
}
