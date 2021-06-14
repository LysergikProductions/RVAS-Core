package core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates that when the target throws any exception it should
 * intentionally result in the shutting down of the server.
 *
 * @author LysergikProductions
 */

@Retention(RetentionPolicy.SOURCE)
public @interface Critical { }
