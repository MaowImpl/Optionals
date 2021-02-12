package maow.optionals.util;

public interface Constants {
    interface Annotations extends Constants {
        String OPTIONAL = getConstant("Optional");
        String ALL_OPTIONAL = getConstant("AllOptional");

        static String getConstant(String name) {
            return "maow.optionals.annotations." + name;
        }
    }
}
