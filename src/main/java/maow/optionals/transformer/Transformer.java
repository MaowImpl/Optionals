package maow.optionals.transformer;

import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.*;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import maow.optionals.filter.Filter;

/**
 * Handles modification of the source AST, is instantiated by a {@link Filter}.
 * @param <T> The type this transformer can transform
 *
 * @since 1.0.0
 * @author Maow
 */
public abstract class Transformer<T extends JCTree> {
    protected final Filter filter;
    protected final TreeMaker maker;
    protected final Names names;

    protected Transformer(Filter filter) {
        this.filter = filter;
        this.maker = filter.getMaker();
        this.names = filter.getNames();
    }

    public abstract void transform(T t);

    protected JCBlock block(JCStatement... statements) {
        final List<JCStatement> body = List.from(statements);
        return maker.Block(0L, body);
    }

    protected Name name(String name) {
        return names.fromString(name);
    }

    protected JCExpression id(String name) {
        final String[] sections = name.split("\\.");
        return (sections.length == 0)
                ? maker.Ident(name(name))
                : chainedId(sections);
    }

    private JCExpression chainedId(String... sections) {
        JCExpression id = null;
        for (String section : sections) {
            if (id == null) id = maker.Ident(name(section));
            else id = maker.Select(id, name(section));
        }
        return id;
    }

    protected JCStatement call(String name, List<JCExpression> parameters) {
        JCExpression call = id(name);
        if (!parameters.isEmpty())
            call = maker.Apply(List.nil(), call, parameters);
        return maker.Exec(call);
    }

    protected JCStatement constructorCall(JCClassDecl clazz, List<JCExpression> parameters) {
        JCExpression call = maker.This(clazz.sym.type);
        call = maker.Apply(List.nil(), call, parameters);
        return maker.Exec(call);
    }

    protected JCLiteral lit(Object value) {
        return maker.Literal(value);
    }

    protected JCLiteral lit(TypeTag type, Object value) {
        return maker.Literal(type, value);
    }

    protected JCLiteral nullLit() {
        return lit(TypeTag.BOT, null);
    }
}
