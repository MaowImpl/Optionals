package maow.optionals.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class CollectionUtil {
    private CollectionUtil() {
        throw new UnsupportedOperationException("Cannot instantiate utility class.");
    }

    @SafeVarargs
    public static <T> Set<T> toSet(T... ts) {
        return new HashSet<>(Arrays.asList(ts));
    }
}
