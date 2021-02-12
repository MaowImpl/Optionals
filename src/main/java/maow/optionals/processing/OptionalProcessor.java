package maow.optionals.processing;

import com.sun.tools.javac.tree.TreeMaker;
import maow.optionals.javac.handler.JavacHandler;
import maow.optionals.javac.handler.OptionalHandler;
import maow.optionals.javac.util.JavacUtils;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

import static maow.optionals.util.Annotations.OPTIONAL_ANNOTATION;

/**
 * Annotation processor for the {@link maow.optionals.annotations.Optional} annotation.
 *
 * @since 1.0.0
 * @author Maow
 */
@SupportedAnnotationTypes({ OPTIONAL_ANNOTATION })
public final class OptionalProcessor extends JavacProcessor {
    private final Set<String> processed = new HashSet<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        if (!env.processingOver()) {
            forAnnotation(annotations, env, element ->
                    getClassElement(element).ifPresent(this::checkAndHandle)
            );
        }
        return true;
    }

    private void checkAndHandle(Element clazz) {
        final String name = getBinaryName(clazz).intern();
        if (!processed.contains(name)) {
            handle(clazz);
            processed.add(name);
        }
    }

    private String getBinaryName(Element element) {
        if (element instanceof TypeElement) {
            final TypeElement type = (TypeElement) element;
            return elements.getBinaryName(type).toString();
        }
        return "";
    }

    private Optional<Element> getClassElement(Element element) {
        Element clazz = null;
        if (element.getKind() == ElementKind.PARAMETER) {
            final Element method = element.getEnclosingElement();
            clazz = method.getEnclosingElement();
        }
        return Optional.ofNullable(clazz);
    }

    @Override
    protected ElementKind[] getSupportedElementKinds() {
        return new ElementKind[]{
                ElementKind.PARAMETER
        };
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    protected BiFunction<JavacUtils, TreeMaker, JavacHandler> handler() {
        return OptionalHandler::new;
    }
}
