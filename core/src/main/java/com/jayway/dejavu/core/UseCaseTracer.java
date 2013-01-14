package com.jayway.dejavu.core;

import com.jayway.dejavu.core.annotation.Autowire;
import com.jayway.dejavu.core.impl.TracedProvider;
import com.jayway.dejavu.core.impl.Tracer;
import com.jayway.dejavu.core.value.Value;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class UseCaseTracer extends Tracer {

    private UseCase useCase;
    protected UseCaseRunner runner;

    public UseCaseTracer(Class<? extends UseCase> clazz, UseCaseRunner runner, Value input ) {
        this.runner = runner;
        useCase = instance(clazz);
        wireDependencies( useCase );
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
    }

    protected List<TracedElement> getTrace() {
        return trace;
    }

    protected UseCase getUseCase() {
        return useCase;
    }

    @Override
    protected Provider addDecoration(Provider provider) {
        return new TracedProvider( this, provider );
    }

    @Override
    protected Provider getFieldProvider( Field field ) throws IllegalAccessException, InstantiationException {
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
}
