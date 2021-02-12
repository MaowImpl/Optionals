package maow.optionals.filter;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.*;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Names;
import maow.optionals.util.NoImplementation;

import javax.lang.model.element.Element;
import java.util.stream.Stream;

/**
 * A class that handles the filtering of {@linkplain JCTree}s and creation of {@linkplain maow.optionals.transformer.Transformer}s.<br>
 * Transformers have an instance of the filter that instantiated them, and so any type of tree validation should be sent to this class.
 *
 * @since 1.0.0
 * @author Maow
 */
public abstract class Filter extends TreeTranslator implements TreeValidator {
    protected final TreeMaker maker;
    protected final Names names;

    protected Filter(TreeMaker maker, Names names) {
        this.maker = maker;
        this.names = names;
    }

    @Override
    public void visitClassDef(JCClassDecl clazz) {
        super.visitClassDef(clazz);
        accept(clazz);
        result = clazz;
    }

    /**
     * Custom validation logic related to a {@link JCTree}.<br>
     * This is different from a processor's validate method as it doesn't validate a {@link Element}.
     *
     * @param tree The tree to be validated
     * @return Whether or not this tree is valid
     */
    @NoImplementation
    public boolean validate(JCTree tree){
        return true;
    }

    public void filter(JCTree tree) {
        final boolean valid = validate(tree);
        if (valid) tree.accept(this);
    }

    /**
     * Logic that happens when the tree passes the filter, this is where transformers can be created <i>safely.</i>
     *
     * @param clazz The target class
     */
    protected abstract void accept(JCClassDecl clazz);

    @SuppressWarnings("unchecked")
    private <T extends JCTree> Stream<T> getMembers(JCClassDecl clazz, Class<T> type) {
        return clazz.defs
                .stream()
                .filter(type::isInstance)
                .map(tree -> (T) tree);
    }

    protected Stream<JCMethodDecl> getMethods(JCClassDecl clazz) {
        return getMembers(clazz, JCMethodDecl.class);
    }

    protected Stream<JCVariableDecl> getFields(JCClassDecl clazz) {
        return getMembers(clazz, JCVariableDecl.class);
    }

    public TreeMaker getMaker() {
        return maker;
    }

    public Names getNames() {
        return names;
    }

}