package maow.optionals.javac.handler;

import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import maow.optionals.javac.transformer.JavacTransformer;
import maow.optionals.javac.util.JavacUtils;

import java.util.function.BiFunction;

/**
 * A base class for all Javac handlers, provides an instance of {@link JavacUtils} and {@link TreeMaker} for modifying the AST.<br>
 * This class should not be instantiated, but instead inherited.
 *
 * @since 1.0.0
 * @author Maow
 */
public abstract class JavacHandler extends TreeTranslator {
    protected final JavacUtils utils;
    protected final TreeMaker maker;

    protected JavacHandler(JavacUtils utils, TreeMaker maker) {
        this.utils = utils;
        this.maker = maker;
    }
}