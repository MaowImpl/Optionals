package maow.optionals.processor;

import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Names;
import maow.optionals.filter.Filter;
import maow.optionals.filter.OptionalFilter;
import maow.optionals.util.Constants;

import javax.lang.model.element.Element;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;

public final class OptionalProcessor extends JavacProcessor.ParameterProcessor {
    private final Set<String> processed = new HashSet<>();

    public OptionalProcessor() {
        super(Constants.Annotations.OPTIONAL);
    }

    @Override
    protected BiFunction<TreeMaker, Names, Filter> getFilter() {
        return OptionalFilter::new;
    }

    @Override
    protected boolean validate(Element element) {
        final String name = getBinaryName(element);
        return !processed.contains(name);
    }

    @Override
    protected void processed(Element element) {
        final String name = getBinaryName(element);
        processed.add(name);
    }
}