package maow.optionals.javac.handler;

import com.sun.tools.javac.tree.TreeMaker;
import maow.optionals.javac.transformer.OptionalTransformer;
import maow.optionals.javac.util.JavacUtils;

import java.util.List;

import static com.sun.tools.javac.tree.JCTree.*;

/**
 * Javac handler for {@link maow.optionals.processing.OptionalProcessor}, generates method overloads when it finds parameters annotated with {@link maow.optionals.annotations.Optional}.
 *
 * @since 1.0.0
 * @author Maow
 */
public final class OptionalHandler extends JavacHandler {
    public OptionalHandler(JavacUtils utils, TreeMaker maker) {
        super(utils, maker);
    }

    @Override
    public void visitClassDef(JCClassDecl clazz) {
        super.visitClassDef(clazz);
        final OptionalTransformer transformer = new OptionalTransformer(clazz, utils, maker);

        clazz.defs.stream()
                .filter(JavacUtils::isMethod)
                .map(JavacUtils::getMethod)
                .filter(this::hasOptionalParameters)
                .forEach(transformer::transform);

        result = clazz;
    }

    private boolean hasOptionalParameters(JCMethodDecl method) {
        final List<JCVariableDecl> parameters = method.params;
        return parameters
                .stream()
                .anyMatch(JavacUtils::isOptional);
    }
}