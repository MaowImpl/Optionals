package maow.optionals.processing;

import com.sun.tools.javac.tree.TreeMaker;
import maow.optionals.javac.handler.AllOptionalHandler;
import maow.optionals.javac.handler.JavacHandler;
import maow.optionals.javac.util.JavacUtils;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

import static maow.optionals.util.Annotations.ALL_OPTIONAL_ANNOTATION;

@SupportedAnnotationTypes(ALL_OPTIONAL_ANNOTATION)
public final class AllOptionalProcessor extends JavacProcessor {
    @Override
    protected ElementKind[] getSupportedElementKinds() {
        return new ElementKind[] {
                ElementKind.METHOD,
                ElementKind.CONSTRUCTOR
        };
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    protected BiFunction<JavacUtils, TreeMaker, JavacHandler> handler() {
        return AllOptionalHandler::new;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        if (!env.processingOver()) {
            forAnnotation(annotations, env, element ->
                    getClassElement(element).ifPresent(this::handle)
            );
        }
        return true;
    }

    private Optional<Element> getClassElement(Element element) {
        Element clazz = null;
        if (
                element.getKind() == ElementKind.METHOD ||
                element.getKind() == ElementKind.CONSTRUCTOR
        ) {
            clazz = element.getEnclosingElement();
        }
        return Optional.ofNullable(clazz);
    }
}
