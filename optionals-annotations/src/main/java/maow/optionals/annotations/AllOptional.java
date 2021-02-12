package maow.optionals.annotations;

import java.lang.annotation.*;

/**
 * This annotation states that all of the parameters of a constructor or method will be considered optional.
 *
 * @since 1.0.0
 * @author Maow
 */
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface AllOptional {
}
