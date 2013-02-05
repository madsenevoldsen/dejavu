package com.jayway.dejavu.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Add to methods that has behaviour that is
 * non deterministic, randomized, or requires
 * external services (such as connection to a
 * database).
 *
 * All outputs of <i>Impure</i> methods must be
 * treated as immutable otherwise the re-run will
 * not work.
 *
 * If <i>integrationPoint</i> is specified the
 * call to the method is guarded by a circuit
 * breaker identified by name. If no such
 * circuit breaker can be found with the given
 * name a default one will be created and used
 * subsequently.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Impure {
    String integrationPoint() default "";
}
