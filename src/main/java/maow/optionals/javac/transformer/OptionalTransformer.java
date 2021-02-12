package maow.optionals.javac.transformer;

import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import maow.optionals.javac.util.JavacUtils;

import javax.lang.model.type.TypeKind;

import static com.sun.tools.javac.tree.JCTree.*;
import static maow.optionals.javac.util.JavacUtils.isOptional;
import static maow.optionals.util.Annotations.OPTIONAL_ANNOTATION;

public final class OptionalTransformer implements JavacTransformer<JCMethodDecl> {
    private final JCClassDecl clazz;
    private final JavacUtils utils;
    private final TreeMaker maker;

    public OptionalTransformer(JCClassDecl clazz, JavacUtils utils, TreeMaker maker) {
        this.clazz = clazz;
        this.utils = utils;
        this.maker = maker;
    }

    @Override
    public void transform(JCMethodDecl method) {
        transform(method, -1);
    }

    public void transform(JCMethodDecl method, int override) {
        final String name = method.name.toString();
        final boolean isCtor = name.equals("<init>");
        final List<JCVariableDecl> params = method.params;
        handleParameters(method, params, isCtor, override);
    }

    private void handleParameters(JCMethodDecl method, List<JCVariableDecl> params, boolean isCtor, int override) {
        getOverloadMethods(method, params, isCtor, override)
                .forEach(overload ->
                        clazz.defs = clazz.defs.append(overload)
                );
    }

    private List<JCMethodDecl> getOverloadMethods(JCMethodDecl method, List<JCVariableDecl> params, boolean isCtor, int override) {
        List<JCMethodDecl> overloads = List.nil();
        final boolean overridden = override > 0;
        int optional = (!overridden)
                ? (int) params
                    .stream()
                    .filter(JavacUtils::isOptional)
                    .count()
                : override;
        for (int i = 1; i <= optional; i++) {
            final List<JCVariableDecl> overloadParams = getOverloadParameters(params, i, overridden);
            final JCBlock overloadBody = getOverloadBody(method, params, i, isCtor, overridden);
            final JCMethodDecl overloadMethod = getOverloadMethod(method, overloadBody, overloadParams);
            overloads = overloads.append(overloadMethod);
        }
        return overloads;
    }

    private List<JCVariableDecl> getOverloadParameters(List<JCVariableDecl> params, int skip, boolean override) {
        params = params.reverse();
        List<JCVariableDecl> overloadParams = List.nil();
        int totalSkipped = 0;
        for (JCVariableDecl param : params) {
            if ((isOptional(param) | override) && totalSkipped != skip) {
                totalSkipped += 1;
                continue;
            }
            overloadParams = overloadParams.append(param);
        }
        return overloadParams.reverse();
    }

    private JCBlock getOverloadBody(JCMethodDecl method, List<JCVariableDecl> params, int skip, boolean isCtor, boolean override) {
        params = params.reverse();
        List<JCExpression> args = List.nil();
        int totalSkipped = 0;
        for (JCVariableDecl param : params) {
            if ((isOptional(param) || override) && totalSkipped != skip) {
                totalSkipped += 1;
                args = args.appendList(getDefaultValue(param));
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

    private List<JCExpression> getDefaultValue(JCVariableDecl param) {
        for (JCAnnotation annotation : param.mods.annotations) {
            if (annotation.type.toString().equals(OPTIONAL_ANNOTATION)) {
                final List<JCExpression> args = annotation.args;
                if (args.size() > 0) {
                    final JCExpression arg = args.get(0);
                    if (arg instanceof JCAssign) {
                        final JCAssign assign = (JCAssign) arg;
                        final JCExpression rhs = assign.rhs;
                        if (!(rhs instanceof JCNewArray)) {
                            return List.of(rhs);
                        }
                    }
                }
            }
        }
        final Type type = param.vartype.type;
        return List.of(getDefaultValue(type));
    }

    private JCExpression getDefaultValue(Type type) {
        final TypeKind kind = type.getKind();
        switch (kind) {
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
                return maker.Literal(0);
            case BOOLEAN: return maker.Literal(false);
            case BYTE: return maker.Literal((byte) 0x0);
            case SHORT: return maker.Literal((short) 0);
            case CHAR: return maker.Literal(TypeTag.CHAR, 0);
            default:
                return utils._null();
        }
    }
}
