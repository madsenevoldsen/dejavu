package com.jayway.dejavu.core;

import com.jayway.dejavu.core.annotation.Autowire;
import com.jayway.dejavu.core.impl.DelegatingProvider;
import com.jayway.dejavu.core.value.ExceptionValue;
import com.jayway.dejavu.core.value.Value;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TracedUseCase<Input extends Value, Output> extends UseCase<Input, Output> implements Tracer {

    private UseCase<Input, Output> useCase;
    private Class<? extends UseCase<Input,Output>> clazz;
    private List<TracedElement> trace;
    private UseCaseRunner runner;

    public TracedUseCase(Class<? extends UseCase<Input, Output>> clazz, UseCaseRunner runner ) {
        this.runner = runner;
        this.clazz = clazz;
        trace = new ArrayList<TracedElement>();
    }

    protected UseCase<Input, Output> getUseCase() {
        return useCase;
    }

    public Output run( Input input ) {
        useCase = instance(clazz);
        useCase.setTracer( this );
        for (Method method : clazz.getDeclaredMethods()) {
            if ( method.getName().equals("run")) {
                // it's not the interface
                if ( method.getParameterTypes()[0] != Value.class  ) {
                    trace.add( new TracedElement(method.getParameterTypes()[0], input));
                }
            }
        }
        if ( trace.size() == 0 ) throw new RuntimeException("Error in framework, input type for trace not found");
        return useCase.run(input);
    }
    public <I,O> O step( Class<? extends Step<I,O>> clazz, I input ) {
        return instance(clazz).run(input);
    }

    public <I,O extends Value> O provide(Provider<I,O> provider, I input) {
        try {
            O output = provider.request(input);
            if ( output != null ) {
                Class<?> clazz = output.getClass();
                trace.add( new TracedElement(clazz, output));
            } else {
                trace.add(new TracedElement(Object.class, null));
            }
            return output;
        } catch (RuntimeException e ) {
            // provider threw runtime exception
            trace.add( new TracedElement(ExceptionValue.class, new ExceptionValue( e.getClass().getCanonicalName(), e.getMessage() )));
            throw e;
        }
    }

    protected List<TracedElement> getTrace() {
        return trace;
    }

    @Override
    public void provided(Value value) {
        trace.add( new TracedElement(value.getClass(), value ));
    }

    protected Provider getFieldDecoration( Field field ) throws IllegalAccessException, InstantiationException {
        Provider provider = null;
        for (Annotation annotation : field.getDeclaredAnnotations()) {
            if ( Autowire.class.isAssignableFrom(annotation.getClass())) {
                Autowire wire = (Autowire) annotation;
                Class<?> type = field.getType();
                if ( Provider.class.isAssignableFrom( type )) {
                    Class<? extends Provider> pClazz = runner.provider(wire.value());
                    provider = pClazz.newInstance();
                }
            }
        }
        return provider;
    }

    private <T> T instance(Class<T> clazz ) {
        try {
            T t = clazz.newInstance();
            if ( Step.class.isAssignableFrom( clazz) ) {
                ((Step) t).setUseCase(useCase);
            }
            for (Field field : clazz.getDeclaredFields()) {
                Provider dProvider = getFieldDecoration( field );
                if ( dProvider != null ) {
                    field.setAccessible(true);
                    field.set(t, new DelegatingProvider( this, dProvider ));
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
