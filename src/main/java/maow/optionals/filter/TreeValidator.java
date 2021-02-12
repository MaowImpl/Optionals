package maow.optionals.filter;

import com.sun.tools.javac.tree.JCTree.*;
import maow.optionals.util.NoImplementation;

public interface TreeValidator {
    @NoImplementation
    default boolean validateClass(JCClassDecl clazz) {
        return true;
    }

    @NoImplementation
    default boolean validateMethod(JCMethodDecl method) {
        return true;
    }

    @NoImplementation
    default boolean validateField(JCVariableDecl field) {
        return true;
    }

    @NoImplementation
    default boolean validateParameter(JCVariableDecl parameter) {
        return true;
    }
}
