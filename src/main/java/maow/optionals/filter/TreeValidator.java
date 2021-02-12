package maow.optionals.filter;

import com.sun.tools.javac.tree.JCTree.*;
import maow.optionals.util.NoImplementation;

/**
 * Custom logic for filters to implement, transformers can call these methods to validate certain Javac elements.
 *
 * @since 1.0.0
 * @author Maow
 * @see Filter
 */
public interface TreeValidator {
    /**
     * Logic for validating a {@link JCClassDecl}.
     *
     * @param clazz The class to be validated
     * @return Whether or not this class is valid
     */
    @NoImplementation
    default boolean validateClass(JCClassDecl clazz) {
        return true;
    }

    /**
     * Logic for validating a {@link JCMethodDecl}.
     *
     * @param method The method to be validated
     * @return Whether or not this method is valid
     */
    @NoImplementation
    default boolean validateMethod(JCMethodDecl method) {
        return true;
    }

    /**
     * Logic for validating a field.<br>
     * This method is the same as {@link TreeValidator#validateParameter(JCVariableDecl)}, however, it is in a different context.
     *
     * @param field The field to be validated
     * @return Whether or not this field is valid
     */
    @NoImplementation
    default boolean validateField(JCVariableDecl field) {
        return true;
    }

    /**
     * Logic for validating a parameter.<br>
     * This method is the same as {@link TreeValidator#validateField(JCVariableDecl)}, however, it is in a different context.
     *
     * @param parameter The parameter to be validated
     * @return Whether or not this parameter is valid
     */
    @NoImplementation
    default boolean validateParameter(JCVariableDecl parameter) {
        return true;
    }
}