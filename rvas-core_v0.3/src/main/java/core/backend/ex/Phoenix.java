package core.backend.ex;

import java.lang.annotation.*;
import com.google.common.annotations.Beta;

/**
 * Indicates that when the target method throws any unhandled exception
 * it should intentionally result in the restarting of the server.
 *
 * Set doRestart to false to prevent restarting the server in these cases.
 * However, doing so is NOT intended for production releases.
 *
 * @author LysergikProductions
 */

@Beta
@Inherited
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Phoenix {
    /**
     * @return doRestart
     */
    boolean doRestart() default true;
}
