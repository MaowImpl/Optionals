package maow.optionals.filter;

import com.sun.tools.javac.tree.JCTree.*;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import maow.optionals.transformer.OptionalTransformer;
import maow.optionals.util.Constants;

public class AllOptionalFilter extends Filter {
    public AllOptionalFilter(TreeMaker maker, Names names) {
        super(maker, names);
    }

    @Override
    protected void accept(JCClassDecl clazz) {
        final OptionalTransformer transformer = new OptionalTransformer(this, clazz);
        getMethods(clazz)
                .filter(this::hasAllOptionalAnnotation)
                .forEach(transformer::transform);
    }

    @Override
    public boolean validateParameter(JCVariableDecl parameter) {
        return true;
    }

    protected boolean hasAllOptionalAnnotation(JCMethodDecl method) {
        return method.mods.annotations
                .stream()
                .anyMatch(annotation -> {
                    final Name name = annotation.type.tsym.getQualifiedName();
                    return name.contentEquals(Constants.Annotations.ALL_OPTIONAL);
                });
    }
}
