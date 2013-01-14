package com.jayway.dejavu.core;

import com.jayway.dejavu.core.annotation.Autowire;
import com.jayway.dejavu.core.impl.TracedProvider;
import com.jayway.dejavu.core.impl.Tracer;
import com.jayway.dejavu.core.impl.ValueProvider;
import com.jayway.dejavu.core.value.Value;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

public class DejaVuTrace extends Tracer {

    private ValueProvider valueProvider;
    private Class<? extends UseCase> clazz;

    public DejaVuTrace(Trace trace) {
        this.clazz = trace.getUseCaseClass();
        valueProvider = new ValueProvider( trace );
    }
    public DejaVuTrace(Class<? extends UseCase> clazz, List<Value> values) {
        this.clazz = clazz;
        valueProvider = new ValueProvider( values );
    }

    @Override
    protected <I, O extends Value> O provide(Provider<I, O> provider, I input) {
        throw new RuntimeException("Must not be called in a Deja vu use case!");
    }

    @Override
    protected void provided(Value value) {
        throw new RuntimeException("Must not be called in a Deja vu use case!");
    }

    @Override
    protected Provider addDecoration(Provider provider) {
        return provider;
    }

    @Override
    protected Provider getFieldProvider(Field field) throws IllegalAccessException, InstantiationException {
        for (Annotation annotation : field.getDeclaredAnnotations()) {
            if ( Autowire.class.isAssignableFrom(annotation.getClass())) {
                Class<?> type = field.getType();
                if ( Provider.class.isAssignableFrom( type ) ) {
                    return new DejaVuProvider( valueProvider );
                }
            }
        }
        return null;
    }

    public <T> T run() {
        UseCase useCase = instance( clazz );
        wireDependencies( useCase );
        useCase.setTracer( this );
        try {
            return (T) useCase.run(valueProvider.getNext());
        } catch ( TraceEndedException e ) {
            // the trace has ended so
            // we can do nothing but return null
            return null;
        }
    }
}