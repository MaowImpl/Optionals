package maow.optionals.annotations;

import java.lang.annotation.*;

/**
 * An annotation that declares that a method's parameter is optional and may be omitted in a method call.<br>
 * Multiple parameters may be annotated with this annotation, and in any order.<br><br>
 *
 * During processing, this annotation is read and the method is overloaded with the optional parameter omitted.
 *
 * @since 1.0.0
 * @author Maow
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface Optional {
}
