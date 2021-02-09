package maow.optionals.javac.util;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol.TypeSymbol;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import static com.sun.tools.javac.tree.JCTree.*;
import static maow.optionals.javac.util.Annotations.OPTIONAL_ANNOTATION;

public final class JavacUtils {
    private final TreeMaker maker;
    private final Names names;

    public JavacUtils(TreeMaker maker, Names names) {
        this.maker = maker;
        this.names = names;
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

    public JCStatement call(String call) {
        return call(call, List.nil());
    }

    public JCStatement call(Name name, List<JCExpression> args) {
        return call(name.toString(), args);
    }

    public JCStatement call(Name name) {
        return call(name.toString());
    }

    public JCExpression id(String name) {
        final String[] sections = name.split("\\.");
        return (sections.length == 0)
                ? maker.Ident(name(name))
                : chainedId(sections);
    }

    public JCExpression id(Name name) {
        return id(name.toString());
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
