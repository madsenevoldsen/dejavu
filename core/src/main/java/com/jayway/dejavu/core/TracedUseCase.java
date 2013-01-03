package com.jayway.dejavu.core;

import com.jayway.dejavu.annotation.Autowire;
import com.jayway.dejavu.annotation.CircuitBreaker;
import com.jayway.dejavu.core.impl.CircuitBreakerProvider;
import com.jayway.dejavu.core.impl.DelegatingProvider;
import com.jayway.dejavu.repository.CircuitBreakerRepository;
import com.jayway.dejavu.value.Value;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TracedUseCase<Input extends Value, Output> extends UseCase<Input, Output> implements Tracer {

    private UseCase<Input, Output> useCase;
    private Class<? extends UseCase<Input,Output>> clazz;
    private List<TracedElement> trace;
    private UseCaseRunner runner;

    protected UseCase<Input, Output> getUseCase() {
        return useCase;
    }

    public TracedUseCase(Class<? extends UseCase<Input, Output>> clazz, UseCaseRunner runner ) {
        this.runner = runner;
        useCase = instance(clazz);
        useCase.setTracer( this );
        this.clazz = clazz;
        trace = new ArrayList<TracedElement>();
    }

    public Output run( Input input ) {
        trace.add( new TracedElement(clazz, input));
        return useCase.run(input);
    }
    public <I,O> O step( Class<? extends Step<I,O>> clazz, I input ) {
        return instance(clazz).run(input);
    }

    public <I,O extends Value> O provide(Provider<I,O> provider, I input) {
        O output = provider.request(input);
        if ( output != null ) {
            Class<?> clazz = output.getClass();
            trace.add( new TracedElement(clazz, output));
        } else {
            trace.add(new TracedElement(Object.class, null));
        }
        return output;
    }

    protected List<TracedElement> getTrace() {
        return trace;
    }

    private <T> T instance(Class<T> clazz ) {
        try {
            T t = clazz.newInstance();
            if ( Step.class.isAssignableFrom( clazz) ) {
                ((Step) t).setUseCase(useCase);
            }
            for (Field field : clazz.getDeclaredFields()) {
                Provider provider = null;
                CircuitBreakerRepository repository = null;
                for (Annotation annotation : field.getDeclaredAnnotations()) {
                    if ( Autowire.class.isAssignableFrom(annotation.getClass())) {
                        Autowire wire = (Autowire) annotation;
                        Class<?> type = field.getType();
                        if ( Provider.class.isAssignableFrom( type )) {
                            Class<? extends Provider> pClazz = runner.provider(wire.value());
                            provider = pClazz.newInstance();
                        }
                    } else if ( CircuitBreaker.class.isAssignableFrom(annotation.getClass())) {
                        CircuitBreaker breaker = (CircuitBreaker) annotation;
                        Class<?> type = field.getType();
                        if ( Provider.class.isAssignableFrom( type ) ) {
                            Class<? extends CircuitBreakerRepository> aClass = runner.circuitBreaker(breaker.value());
                            repository = aClass.newInstance();
                        }
                    }
                }
                if ( provider != null && repository != null ) {
                    CircuitBreakerProvider cbProvider = new CircuitBreakerProvider(provider, repository);
                    field.setAccessible(true);
                    field.set(t, new DelegatingProvider(this, cbProvider ));
                } else if ( provider != null ) {
                    field.setAccessible(true);
                    field.set(t, new DelegatingProvider(this, provider) );
                }
            }
            return t;
        } catch (InstantiationException e) {
            throw new RuntimeException("Could not instantiate class: "+clazz.getSimpleName() );
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not access: "+clazz.getSimpleName());
        }
    }
}
