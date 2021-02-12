package maow.optionals.exception;

/**
 * A runtime exception that is thrown when an {@link maow.optionals.annotations.Optional} annotation has more than one attribute.
 *
 * @since 1.0.0
 * @author Maow
 */
public class TooManyAttributesException extends RuntimeException {
    public TooManyAttributesException(String msg) {
        super(msg);
    }

    public TooManyAttributesException() {
        this("@Optional annotation should not have more than one attribute.");
    }
}
