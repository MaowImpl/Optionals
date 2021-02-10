package maow.optionals.annotations;

import java.lang.annotation.*;

/**
 * An annotation that declares that a method/constructor's parameter is optional and may be omitted in a call.<br>
 * Multiple parameters may be annotated with this annotation, and in any order. Works the same way with static methods.<br><br>
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
    Class<?> classValue() default Void.class;
    String stringValue() default "";
    byte byteValue() default 0;
    short shortValue() default 0;
    int intValue() default 0;
    long longValue() default 0;
    float floatValue() default 0;
    double doubleValue() default 0;
    boolean booleanValue() default false;
}
