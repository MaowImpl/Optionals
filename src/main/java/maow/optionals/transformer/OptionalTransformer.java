package maow.optionals.transformer;

import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree.*;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import maow.optionals.exception.TooManyAttributesException;
import maow.optionals.filter.Filter;

import javax.lang.model.type.TypeKind;

import static maow.optionals.util.Constants.Annotations.OPTIONAL;

public class OptionalTransformer extends Transformer<JCMethodDecl> {
    private final JCClassDecl clazz;
    private boolean ctor;

    public OptionalTransformer(Filter filter, JCClassDecl clazz) {
        super(filter);
        this.clazz = clazz;
    }

    @Override
    public void transform(JCMethodDecl base) {
        final String name = base.name.toString();
        ctor = name.equals("<init>");
        getMethods(base).forEach(method ->
                clazz.defs = clazz.defs.append(method)
        );
    }

    private List<JCMethodDecl> getMethods(JCMethodDecl base) {
        List<JCMethodDecl> methods = List.nil();
        final int optional = getTotalOptionalParameters(base);
        for (int i = 1; i <= optional; i++) {
            final List<JCVariableDecl> originals = base.params.reverse();
            final List<JCVariableDecl> parameters = getParameters(originals, i);
            final JCBlock body = getBody(base, originals, i);
            final JCMethodDecl method = getMethod(base, parameters, body);
            methods = methods.append(method);
        }
        return methods;
    }

    private List<JCVariableDecl> getParameters(List<JCVariableDecl> originals, int skip) {
        int skipped = 0;
        List<JCVariableDecl> parameters = List.nil();
        for (JCVariableDecl original : originals) {
            final boolean valid = filter.validateParameter(original);
            if (valid && skipped != skip) {
                skipped += 1;
                continue;
            }
            parameters = parameters.append(original);
        }
        return parameters.reverse();
    }

    private JCBlock getBody(JCMethodDecl method, List<JCVariableDecl> originals, int skip) {
        int skipped = 0;
        List<JCExpression> parameters = List.nil();
        for (JCVariableDecl original : originals) {
            final boolean valid = filter.validateParameter(original);
            if (valid && skipped != skip) {
                skipped += 1;
                final JCExpression value = getDefaultValue(original);
                parameters = parameters.append(value);
                continue;
            }
            final JCExpression id = maker.Ident(original);
            parameters = parameters.append(id);
        }
        parameters = parameters.reverse();
        final boolean hasReturn = !method.restype.type.toString().equals("void");
        JCStatement call = (!ctor)
                ? call(method.name.toString(), parameters, hasReturn)
                : constructorCall(clazz, parameters);
        return block(call);
    }

    private JCMethodDecl getMethod(JCMethodDecl base, List<JCVariableDecl> parameters, JCBlock body) {
        return maker.MethodDef(
                base.mods,
                base.name,
                base.restype,
                base.typarams,
                parameters,
                base.thrown,
                body,
                null
        );
    }

    private int getTotalOptionalParameters(JCMethodDecl base) {
        return (int) base.params
                .stream()
                .filter(filter::validateParameter)
                .count();
    }

    private JCExpression getDefaultValue(JCVariableDecl parameter) {
        for (JCAnnotation annotation : parameter.mods.annotations) {
            final String annotationType = annotation.type.toString();
            if (annotationType.equals(OPTIONAL)) {
                final List<JCExpression> attributes = annotation.args;
                if (attributes.size() == 1) {
                    final JCExpression attribute = attributes.get(0);
                    final JCAssign assign = (JCAssign) attribute;
                    final JCExpression rhs = assign.rhs;
                    if (!(rhs instanceof JCNewArray)) {
                        return rhs;
                    }
                } else if (attributes.size() > 1) {
                    throw new TooManyAttributesException();
                }
            }
        }
        return getDefaultValue(parameter.vartype);
    }

    private JCExpression getDefaultValue(JCExpression varType) {
        final TypeKind kind = varType.type.getKind();
        switch (kind) {
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
                return lit(0);
            case BOOLEAN: return lit(false);
            case BYTE: return lit((byte) 0);
            case SHORT: return lit((short) 0);
            case CHAR: return lit(TypeTag.CHAR, 0);
            case DECLARED: {
                if (varType.toString().equals("String")) {
                    return lit("");
                }
            }
            default:
                return nullLit();
        }
    }
}