package maow.optionals.javac.util;

import com.sun.tools.javac.code.Symbol.TypeSymbol;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import maow.optionals.processing.JavacProcessor;

import static com.sun.tools.javac.tree.JCTree.*;
import static maow.optionals.util.Annotations.OPTIONAL_ANNOTATION;

/**
 * Utility class for handlers to use, can only be instantiated with an instance of a {@link JavacProcessor}.
 *
 * @since 1.0.0
 * @author Maow
 */
public final class JavacUtils {
    private final TreeMaker maker;
    private final Names names;

    private JavacUtils(TreeMaker maker, Names names) {
        this.maker = maker;
        this.names = names;
    }

    public static JavacUtils newUtils(JavacProcessor processor) {
        final TreeMaker maker = processor.getMaker();
        final Names names = processor.getNames();
        return new JavacUtils(maker, names);
    }

    public static boolean isMethod(JCTree tree) {
        return (tree instanceof JCMethodDecl);
    }

    public static JCMethodDecl getMethod(JCTree tree) {
        return (JCMethodDecl) tree;
    }

    public static boolean symbolEquals(TypeSymbol tysm, String fqName) {
        final Name tysmName = tysm.getQualifiedName();
        return tysmName.contentEquals(fqName);
    }

    public static boolean isAnnotated(JCVariableDecl var, String annotationFqName) {
        return var.mods.annotations
                .stream()
                .anyMatch(annotation -> symbolEquals(
                        annotation.type.tsym,
                        annotationFqName)
                );
    }

    public static boolean isOptional(JCVariableDecl var) {
        return isAnnotated(var, OPTIONAL_ANNOTATION);
    }

    public Name name(String name) {
        return names.fromString(name);
    }

    public JCStatement call(String call, List<JCExpression> args) {
        JCExpression expr = id(call);
        if (!args.isEmpty()) {
            expr = maker.Apply(List.nil(), expr, args);
        }
        return maker.Exec(expr);
    }

    public JCStatement call(Name name, List<JCExpression> args) {
        return call(name.toString(), args);
    }

    public JCStatement _this(JCClassDecl clazz, List<JCExpression> args) {
        JCExpression expr = maker.This(clazz.sym.type);
        expr = maker.Apply(List.nil(), expr, args);
        return maker.Exec(expr);
    }

    public JCExpression id(String name) {
        final String[] sections = name.split("\\.");
        return (sections.length == 0)
                ? maker.Ident(name(name))
                : chainedId(sections);
    }

    private JCExpression chainedId(String... sections) {
        JCExpression identifier = null;
        for (String section : sections) {
            if (identifier == null) identifier = maker.Ident(name(section));
            else identifier = maker.Select(identifier, name(section));
        }
        return identifier;
    }

    public JCBlock block(JCStatement... statements) {
        final List<JCStatement> statementTree = List.from(statements);
        return maker.Block(0L, statementTree);
    }

    public JCLiteral _null() {
        return maker.Literal(TypeTag.BOT, null);
    }
}
