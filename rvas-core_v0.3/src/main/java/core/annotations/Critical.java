package core.annotations;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates that when the target throws any unhandled exception it
 * should intentionally result in the shutting down of the server.
 *
 * Set isFatal to false to prevent shutting down the server in these cases.
 * However, doing so is NOT intended for production releases.
 *
 * @author LysergikProductions
 */

@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Critical {
    /**
     * @return isFatal
     */
    boolean isFatal() default true;
}
