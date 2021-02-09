package maow.optionals.javac.handler;

import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import maow.optionals.javac.util.JavacUtils;

import java.util.stream.Collectors;

import static com.sun.tools.javac.tree.JCTree.*;
import static maow.optionals.javac.util.JavacUtils.isOptional;

public final class OptionalHandler extends TreeTranslator {
    private final JavacUtils utils;
    private final TreeMaker maker;

    public OptionalHandler(JavacUtils utils, TreeMaker maker) {
        this.utils = utils;
        this.maker = maker;
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
        final List<JCVariableDecl> params = method.params;
        final MethodInfo info = getMethodInfo(method);
        handleParameters(info, params);
    }

    private MethodInfo getMethodInfo(JCMethodDecl method) {
        final JCModifiers mods = method.mods;
        final List<JCTypeParameter> typeparams = method.typarams;
        final JCExpression _return = method.restype;
        final Name name = method.name;
        final List<JCExpression> _throws = method.thrown;
        return new MethodInfo(mods, typeparams, _return, name, _throws);
    }

    private void handleParameters(MethodInfo info, List<JCVariableDecl> params) {
        if (params.stream().noneMatch(JavacUtils::isOptional)) {
            return;
        }
        getOverloadMethods(info, params)
                .forEach(method ->
                        clazz.defs = clazz.defs.append(method)
                );
    }

    private List<JCMethodDecl> getOverloadMethods(MethodInfo info, List<JCVariableDecl> params) {
        List<JCMethodDecl> overloads = List.nil();
//        System.out.println("==============");
//        System.out.println("GETTING OVERLOAD METHODS : " + info.name);
//        System.out.println("PARAMS : " + params.stream().map(JCVariableDecl::getName).collect(Collectors.joining(", ")));
        final int optional = (int) params
                .stream()
                .filter(JavacUtils::isOptional)
                .count();
//        System.out.println("TOTAL [all,req,opt] : [" + params.size() + "," + (params.size() - optional) + "," + optional + "]");
        for (int i = 1; i <= optional; i++) {
//            System.out.println("==============");
//            System.out.println("GETTING OVERLOAD METHOD : " + i + " (optionals to skip)");
//            System.out.println("==============");
            final List<JCVariableDecl> overloadParams = getOverloadParameters(params, i);
//            System.out.println("==============");
//            System.out.println("OVERLOAD PARAMS : " + overloadParams);
//            System.out.println("==============");
            final JCBlock overloadBody = getOverloadBody(info.name, params, i);
//            System.out.println("==============");
//            System.out.println("OVERLOAD BODY : " + overloadBody);
//            System.out.println("==============");
            final JCMethodDecl overloadMethod = getOverloadMethod(info, overloadBody, overloadParams);
//            System.out.println("==============");
//            System.out.println("OVERLOAD METHOD : " + overloadMethod);
//            System.out.println("==============");
            overloads = overloads.append(overloadMethod);
        }
        return overloads;
    }

    private List<JCVariableDecl> getOverloadParameters(List<JCVariableDecl> params, int skip) {
        params = params.reverse();
        List<JCVariableDecl> overloadParams = List.nil();
//        System.out.println("== PARAMS ==");
        int totalSkipped = 0;
        for (JCVariableDecl param : params) {
//            System.out.println("---");
//            System.out.println("TOTAL SKIPPED : " + totalSkipped);
            if (isOptional(param) && totalSkipped != skip) {
//                System.out.println("SKIPPED : " + param.name);
                totalSkipped += 1;
                continue;
            }
//            System.out.println("NOT SKIPPED : " + param.name);
            overloadParams = overloadParams.append(param);
        }
        return overloadParams.reverse();
    }

    private JCBlock getOverloadBody(Name name, List<JCVariableDecl> params, int skip) {
        params = params.reverse();
        List<JCExpression> args = List.nil();
//        System.out.println("== BODY ==");
        int totalSkipped = 0;
        for (JCVariableDecl param : params) {
//            System.out.println("---");
//            System.out.println("TOTAL SKIPPED : " + totalSkipped);
            if (isOptional(param) && totalSkipped != skip) {
//                System.out.println("SKIPPED : " + param.name);
                totalSkipped += 1;
                args = args.append(utils._null());
                continue;
            }
            final JCExpression paramExpr = maker.Ident(param);
            args = args.append(paramExpr);
        }
        JCStatement call = utils.call(name, args.reverse());
        return utils.block(call);
    }

    private JCMethodDecl getOverloadMethod(MethodInfo info, JCBlock body, List<JCVariableDecl> params) {
        return maker.MethodDef(
                info.mods,
                info.name,
                info._return,
                info.typeparams,
                params,
                info._throws,
                body,
                null
        );
    }

    private static class MethodInfo {
        private final JCModifiers mods;
        private final List<JCTypeParameter> typeparams;
        private final JCExpression _return;
        private final Name name;
        private final List<JCExpression> _throws;

        private MethodInfo(JCModifiers mods, List<JCTypeParameter> typeparams, JCExpression _return, Name name, List<JCExpression> _throws) {
            this.mods = mods;
            this.typeparams = typeparams;
            this._return = _return;
            this.name = name;
            this._throws = _throws;
        }

        @Override
        public String toString() {
            return "MethodInfo{" +
                    "mods=" + mods +
                    ", typeparams=" + typeparams +
                    ", _return=" + _return +
                    ", name=" + name +
                    ", _throws=" + _throws +
                    '}';
        }
    }
}