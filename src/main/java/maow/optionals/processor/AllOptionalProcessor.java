package maow.optionals.processor;

import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Names;
import maow.optionals.filter.AllOptionalFilter;
import maow.optionals.filter.Filter;
import maow.optionals.util.Constants;

import java.util.function.BiFunction;

public final class AllOptionalProcessor extends JavacProcessor.MethodProcessor {
    public AllOptionalProcessor() {
        super(Constants.Annotations.ALL_OPTIONAL);
    }

    @Override
    protected BiFunction<TreeMaker, Names, Filter> getFilter() {
        return AllOptionalFilter::new;
    }
}
