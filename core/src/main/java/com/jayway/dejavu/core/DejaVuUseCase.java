package com.jayway.dejavu.core;

import com.jayway.dejavu.annotation.Autowire;
import com.jayway.dejavu.core.impl.ValueProvider;
import com.jayway.dejavu.value.Value;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class DejaVuUseCase<Input extends Value, Output> implements Tracer {

    private Trace trace;
    private ValueProvider valueProvider;
    private UseCase<Input,Output> useCase;

    public DejaVuUseCase( Trace trace ) {
        this.trace = trace;
        valueProvider = new ValueProvider( trace.getTracedElements() );
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
        Class<? extends UseCase<Input, Output>> aClass = (Class<? extends UseCase<Input, Output>>) trace.getUseCaseClass();
        useCase = instance( aClass );
        useCase.setTracer( this );
        return useCase.run((Input) valueProvider.getNext());
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
                        Autowire wire = (Autowire) annotation;
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