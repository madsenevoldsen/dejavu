package com.jayway.dejavu.core;

import com.jayway.dejavu.annotation.Autowire;
import com.jayway.dejavu.core.impl.ValueProvider;
import com.jayway.dejavu.value.Value;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

public class DejaVuUseCase<Input extends Value, Output> implements Tracer {

    //private Trace trace;
    private ValueProvider valueProvider;
    private UseCase<Input,Output> useCase;
    private Class<? extends UseCase<Input,Output>> clazz;

    public DejaVuUseCase( Trace trace ) {
        this.clazz = (Class<? extends UseCase<Input,Output>>) trace.getUseCaseClass();
        valueProvider = new ValueProvider( trace );
    }
    public DejaVuUseCase( Class<? extends UseCase<Input,Output>> clazz, List<Value> values ) {
        this.clazz = clazz;
        valueProvider = new ValueProvider( values );
    }

    @Override
    public <I, O extends Value> O provide(Provider<I, O> provider, I input) {
        throw new RuntimeException("Must not be called in a Deja vu use case!");
    }

    @Override
    public <I, O> O step(Class<? extends Step<I, O>> clazz, I input) {
        Step<I, O> step = instance(clazz);
        return step.run( input );
    }

    public Output run() {
        useCase = instance( clazz );
        useCase.setTracer( this );
        try {
            return useCase.run((Input) valueProvider.getNext());
        } catch ( TraceEndedException e ) {
            // the trace has ended so
            // we can do nothing but return null
            return null;
        }
    }

    private <T> T instance(Class<T> clazz ) {
        try {
            T t = clazz.newInstance();
            if ( Step.class.isAssignableFrom( clazz) ) {
                ((Step) t).setUseCase(useCase);
            }
            for (Field field : clazz.getDeclaredFields()) {
                for (Annotation annotation : field.getDeclaredAnnotations()) {
                    if ( Autowire.class.isAssignableFrom(annotation.getClass())) {
                        Class<?> type = field.getType();
                        if ( Provider.class.isAssignableFrom( type ) ) {
                            field.setAccessible(true);
                            field.set(t, new DejaVuProvider( valueProvider ));
                        }
                    }
                }
            }
            return t;
        } catch (InstantiationException e) {
            throw new RuntimeException("Could not instantiate provider: "+clazz.getSimpleName() );
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not access: "+clazz.getSimpleName());
        }
    }
}