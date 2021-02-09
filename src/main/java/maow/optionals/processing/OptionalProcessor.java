package maow.optionals.processing;

import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;
import maow.optionals.javac.handler.OptionalHandler;
import maow.optionals.javac.util.JavacUtils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static maow.optionals.javac.util.Annotations.OPTIONAL_ANNOTATION;

@SupportedAnnotationTypes({ OPTIONAL_ANNOTATION })
public final class OptionalProcessor extends AbstractProcessor {
    private static final Set<String> PROCESSED_CLASSES = new HashSet<>();

    private Elements elements;
    private Trees trees;
    private TreeMaker maker;
    private Names names;

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
                final Set<? extends Element> elements = env.getElementsAnnotatedWith(annotation);
                for (Element element : elements) {
                    if (element.getKind() == ElementKind.PARAMETER) {
                        final Element classElement = element.getEnclosingElement().getEnclosingElement();
                        final String classElementName = getQualifiedName(classElement);
                        if (!PROCESSED_CLASSES.contains(classElementName)) {
                            final JCTree tree = (JCTree) trees.getTree(classElement);
                            final JavacUtils utils = new JavacUtils(maker, names);
                            final TreeTranslator translator = new OptionalHandler(utils, maker);
                            tree.accept(translator);
                            PROCESSED_CLASSES.add(classElementName);
                        }
                    }
                }
            }
        }
        return false;
    }

    private String getPackageName(Element element) {
        return elements
                .getPackageOf(element)
                .getQualifiedName()
                .toString();
    }

    private String getQualifiedName(Element element) {
        return getPackageName(element) + "." + element.getSimpleName().toString();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }
}
