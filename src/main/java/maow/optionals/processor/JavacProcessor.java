package maow.optionals.processor;

import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;
import maow.optionals.filter.Filter;
import maow.optionals.util.NoImplementation;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiFunction;

import static maow.optionals.util.CollectionUtil.toSet;

public abstract class JavacProcessor extends AbstractProcessor {
    private final String supportedAnnotation;

    protected Elements elements;
    protected Trees trees;
    protected TreeMaker maker;
    protected Names names;

    protected JavacProcessor(String supportedAnnotation) {
        this.supportedAnnotation = supportedAnnotation;
    }

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);

        this.elements = env.getElementUtils();
        this.trees = Trees.instance(env);
        final Context context = ((JavacProcessingEnvironment) env).getContext();
        this.maker = TreeMaker.instance(context);
        this.names = Names.instance(context);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        if (!env.processingOver()) {
            for (TypeElement annotation : annotations) {
                env.getElementsAnnotatedWith(annotation).forEach(element -> {
                    final ElementKind kind = element.getKind();
                    if (getSupportedElementKinds().contains(kind)) {
                        final Element clazz = getClassElement(element);
                        if (clazz != null && validate(clazz)) {
                            final JCTree tree = (JCTree) trees.getTree(clazz);
                            final Filter filter = getFilter().apply(maker, names);
                            filter.filter(tree);
                            processed(clazz);
                        }
                    }
                });
            }
        }
        return isAnnotationClaimed();
    }

    @NoImplementation
    protected boolean validate(Element element) {
        return true;
    }

    @NoImplementation
    protected void processed(Element element) {}

    protected String getBinaryName(Element element) {
        if (element instanceof TypeElement) {
            final TypeElement type = (TypeElement) element;
            return elements
                    .getBinaryName(type)
                    .toString()
                    .intern();
        }
        return "";
    }

    protected Element getClassElement(Element element) {
        switch (element.getKind()) {
            case CLASS:
                return element;
            case METHOD:
            case FIELD:
                return element.getEnclosingElement();
            case PARAMETER:
                return element.getEnclosingElement().getEnclosingElement();
        }
        return null;
    }

    protected abstract BiFunction<TreeMaker, Names, Filter> getFilter();

    public abstract Set<ElementKind> getSupportedElementKinds();

    public boolean isAnnotationClaimed() {
        return false;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(supportedAnnotation);
    }

    public abstract static class ClassProcessor extends JavacProcessor {
        protected ClassProcessor(String supportedAnnotation) {
            super(supportedAnnotation);
        }

        @Override
        public Set<ElementKind> getSupportedElementKinds() {
            return Collections.singleton(ElementKind.CLASS);
        }
    }

    public abstract static class MethodProcessor extends JavacProcessor {
        protected MethodProcessor(String supportedAnnotation) {
            super(supportedAnnotation);
        }

        @Override
        public Set<ElementKind> getSupportedElementKinds() {
            return toSet(ElementKind.METHOD, ElementKind.CONSTRUCTOR);
        }
    }

    public abstract static class FieldProcessor extends JavacProcessor {
        protected FieldProcessor(String supportedAnnotation) {
            super(supportedAnnotation);
        }

        @Override
        public Set<ElementKind> getSupportedElementKinds() {
            return Collections.singleton(ElementKind.FIELD);
        }
    }

    public abstract static class ParameterProcessor extends JavacProcessor {
        protected ParameterProcessor(String supportedAnnotation) {
            super(supportedAnnotation);
        }

        @Override
        public Set<ElementKind> getSupportedElementKinds() {
            return Collections.singleton(ElementKind.PARAMETER);
        }
    }
}