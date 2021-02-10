package maow.optionals.processing;

import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;
import maow.optionals.javac.handler.JavacHandler;
import maow.optionals.javac.util.JavacUtils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public abstract class JavacProcessor extends AbstractProcessor {
    protected Elements elements;
    protected Trees trees;
    protected TreeMaker maker;
    protected Names names;

    protected abstract ElementKind[] getSupportedElementKinds();

    protected abstract BiFunction<JavacUtils, TreeMaker, JavacHandler> handler();

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);

        this.elements = env.getElementUtils();
        this.trees = Trees.instance(env);
        final Context context = ((JavacProcessingEnvironment) env).getContext();
        this.maker = TreeMaker.instance(context);
        this.names = Names.instance(context);
    }

    protected void forAnnotation(Set<? extends TypeElement> annotations, RoundEnvironment env, Consumer<Element> consumer) {
        annotations.stream()
                .map(env::getElementsAnnotatedWith)
                .flatMap(Collection::stream)
                .filter(this::validElementKind)
                .forEach(consumer);
    }

    private boolean validElementKind(Element element) {
        return Arrays.asList(
                getSupportedElementKinds()
        ).contains(element.getKind());
    }

    protected void handle(Element clazz) {
        final JCTree tree = (JCTree) trees.getTree(clazz);
        final JavacUtils utils = JavacUtils.newUtils(this);
        final TreeTranslator translator = handler().apply(utils, maker);
        tree.accept(translator);
    }

    public TreeMaker getMaker() {
        return maker;
    }

    public Names getNames() {
        return names;
    }
}
