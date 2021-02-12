package maow.optionals.javac.handler;

import com.sun.tools.javac.tree.TreeMaker;
import maow.optionals.javac.transformer.OptionalTransformer;
import maow.optionals.javac.util.JavacUtils;

import static com.sun.tools.javac.tree.JCTree.*;

public final class AllOptionalHandler extends JavacHandler {
    public AllOptionalHandler(JavacUtils utils, TreeMaker maker) {
        super(utils, maker);
    }

    @Override
    public void visitClassDef(JCClassDecl clazz) {
        super.visitClassDef(clazz);
        final OptionalTransformer transformer = new OptionalTransformer(clazz, utils, maker);

        clazz.defs.stream()
                .filter(JavacUtils::isMethod)
                .map(JavacUtils::getMethod)
                .filter(JavacUtils::isAllOptional)
                .forEach(method -> {
                    System.out.println(method.params.size());
                    transformer.transform(
                        method,
                        method.params.size()
                    );
                });

        result = clazz;
    }
}
