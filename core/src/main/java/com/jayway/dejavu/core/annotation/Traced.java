package com.jayway.dejavu.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Add this annotation to methods that should be traced.
 * Whenever a on such a method completes a callback
 * with a <i>Trace</i> will be made on the <i>TraceCallback</i>
 * instance configured.
 *
 * The input parameters must be treated as immutable values
 * otherwise the trace will not be correct.
 *
 * The trace created can be re-run using the <i>DejaVuPolicy.replay</i>.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Traced {
}
