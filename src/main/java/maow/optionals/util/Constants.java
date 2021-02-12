package maow.optionals.util;

/**
 * Contains constants used in this library.
 */
public interface Constants {
    /**
     * Contains the qualified names of annotations.
     */
    interface Annotations extends Constants {
        String OPTIONAL = getConstant("Optional");
        String ALL_OPTIONAL = getConstant("AllOptional");

        static String getConstant(String name) {
            return "maow.optionals.annotations." + name;
        }
    }
}
