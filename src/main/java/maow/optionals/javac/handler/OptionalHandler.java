package maow.optionals.javac.handler;

import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import maow.optionals.javac.util.JavacUtils;

import javax.lang.model.type.TypeKind;

import static com.sun.tools.javac.tree.JCTree.*;
import static maow.optionals.javac.util.JavacUtils.isOptional;
import static maow.optionals.util.Annotations.OPTIONAL_ANNOTATION;

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

    private JCClassDecl clazz;

    @Override
    public void visitClassDef(JCClassDecl clazz) {
        super.visitClassDef(clazz);
        this.clazz = clazz;

        clazz.defs.stream()
                .filter(JavacUtils::isMethod)
                .map(JavacUtils::getMethod)
                .forEach(this::handleMethod);

        result = clazz;
    }

    private void handleMethod(JCMethodDecl method) {
        final String name = method.name.toString();
        final boolean isCtor = name.equals("<init>");
        final List<JCVariableDecl> params = method.params;
        handleParameters(method, params, isCtor);
    }

    private void handleParameters(JCMethodDecl method, List<JCVariableDecl> params, boolean isCtor) {
        if (params.stream().noneMatch(JavacUtils::isOptional)) {
            return;
        }
        getOverloadMethods(method, params, isCtor)
                .forEach(overload ->
                        clazz.defs = clazz.defs.append(overload)
                );
    }

    private List<JCMethodDecl> getOverloadMethods(JCMethodDecl method, List<JCVariableDecl> params, boolean isCtor) {
        List<JCMethodDecl> overloads = List.nil();
        final int optional = (int) params
                .stream()
                .filter(JavacUtils::isOptional)
                .count();
        for (int i = 1; i <= optional; i++) {
            final List<JCVariableDecl> overloadParams = getOverloadParameters(params, i);
            final JCBlock overloadBody = getOverloadBody(method, params, i, isCtor);
            final JCMethodDecl overloadMethod = getOverloadMethod(method, overloadBody, overloadParams);
            overloads = overloads.append(overloadMethod);
        }
        return overloads;
    }

    private List<JCVariableDecl> getOverloadParameters(List<JCVariableDecl> params, int skip) {
        params = params.reverse();
        List<JCVariableDecl> overloadParams = List.nil();
        int totalSkipped = 0;
        for (JCVariableDecl param : params) {
            if (isOptional(param) && totalSkipped != skip) {
                totalSkipped += 1;
                continue;
            }
            overloadParams = overloadParams.append(param);
        }
        return overloadParams.reverse();
    }

    private JCBlock getOverloadBody(JCMethodDecl method, List<JCVariableDecl> params, int skip, boolean isCtor) {
        params = params.reverse();
        List<JCExpression> args = List.nil();
        int totalSkipped = 0;
        for (JCVariableDecl param : params) {
            if (isOptional(param) && totalSkipped != skip) {
                totalSkipped += 1;
                args = args.append(getDefaultValue(param));
                continue;
            }
            final JCExpression paramExpr = maker.Ident(param);
            args = args.append(paramExpr);
        }
        args = args.reverse();
        final JCStatement call = (!isCtor)
                ? utils.call(method.name, args)
                : utils._this(clazz, args);
        return utils.block(call);
    }

    private JCMethodDecl getOverloadMethod(JCMethodDecl method, JCBlock body, List<JCVariableDecl> params) {
        return maker.MethodDef(
                method.mods,
                method.name,
                method.restype,
                method.typarams,
                params,
                method.thrown,
                body,
                null
        );
    }

    private JCExpression getDefaultValue(JCVariableDecl param) {
        for (JCAnnotation annotation : param.mods.annotations) {
            if (annotation.type.toString().equals(OPTIONAL_ANNOTATION)) {
                final List<JCExpression> args = annotation.args;
                if (args.size() > 0) {
                    final JCExpression arg = args.get(0);
                    if (arg instanceof JCAssign) {
                        final JCAssign assign = (JCAssign) arg;
                        final JCExpression rhs = assign.rhs;
                        if (!(rhs instanceof JCNewArray)) {
                            return rhs;
                        }
                    }
                }
            }
        }
        return getDefaultValue(param.vartype.type);
    }

    private JCExpression getDefaultValue(Type type) {
        final TypeKind kind = type.getKind();
        if (kind == TypeKind.CHAR) {
            throw new UnsupportedOperationException("@Optional cannot be used on char.");
        }
        switch (kind) {
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
                return maker.Literal(0);
            case BOOLEAN: return maker.Literal(false);
            case BYTE: return maker.Literal((byte) 0x0);
            case SHORT: return maker.Literal((short) 0);
        }
        return utils._null();
    }
}