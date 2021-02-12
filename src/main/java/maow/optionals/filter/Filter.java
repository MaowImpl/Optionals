package maow.optionals.filter;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.*;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Names;

import java.util.stream.Stream;

public abstract class Filter extends TreeTranslator implements TreeValidator {
    protected final TreeMaker maker;
    protected final Names names;

    protected Filter(TreeMaker maker, Names names) {
        this.maker = maker;
        this.names = names;
    }

    public void filter(JCTree tree) {
        final boolean valid = validate(tree);
        if (valid) tree.accept(this);
    }

    public abstract boolean validate(JCTree tree);

    public TreeMaker getMaker() {
        return maker;
    }

    public Names getNames() {
        return names;
    }

    protected Stream<JCMethodDecl> getMethods(JCClassDecl clazz) {
        return getMembers(clazz, JCMethodDecl.class);
    }

    protected Stream<JCVariableDecl> getFields(JCClassDecl clazz) {
        return getMembers(clazz, JCVariableDecl.class);
    }

    @SuppressWarnings("unchecked")
    private <T extends JCTree> Stream<T> getMembers(JCClassDecl clazz, Class<T> type) {
        return clazz.defs
                .stream()
                .filter(type::isInstance)
                .map(tree -> (T) tree);
    }
}