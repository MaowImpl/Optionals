package maow.optionals.util;

import java.lang.annotation.*;

/**
 * An API annotation that states that in the declaration of a new non-abstract method or constructor it has no meaningful implementation.
 *
 * @since 1.0.0
 * @author Maow
 */
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.SOURCE)
public @interface NoImplementation {
}
