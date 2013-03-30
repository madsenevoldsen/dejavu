package com.jayway.dejavu.core.typeinference;

import org.aspectj.lang.reflect.MethodSignature;

/**
 * Interface to be used when a TraceElement is created.
 * This requires a detection of type which sometimes
 * requires specific knowledge because of erasure
 */
public interface TypeInference {
    Class<?> inferType( Object instance, MethodSignature signature );
}
