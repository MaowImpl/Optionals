package maow.optionals.filter;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.*;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import maow.optionals.transformer.OptionalTransformer;
import maow.optionals.util.Constants;

public class OptionalFilter extends Filter {
    public OptionalFilter(TreeMaker maker, Names names) {
        super(maker, names);
    }

    @Override
    public void visitClassDef(JCClassDecl clazz) {
        super.visitClassDef(clazz);

        final OptionalTransformer transformer = new OptionalTransformer(this, clazz);
        getMethods(clazz)
                .filter(this::hasOptionalParameter)
                .forEach(transformer::transform);

        result = clazz;
    }

    @Override
    public boolean validate(JCTree tree) {
        return true;
    }

    @Override
    public boolean validateParameter(JCVariableDecl parameter) {
        return isOptional(parameter);
    }

    protected boolean isOptional(JCVariableDecl parameter) {
        return parameter.mods.annotations
                .stream()
                .anyMatch(annotation -> {
                    final Name name = annotation.type.tsym.getQualifiedName();
                    return name.contentEquals(Constants.Annotations.OPTIONAL);
                });
    }

    protected boolean hasOptionalParameter(JCMethodDecl method) {
        return method.params
                .stream()
                .anyMatch(this::isOptional);
    }
}
