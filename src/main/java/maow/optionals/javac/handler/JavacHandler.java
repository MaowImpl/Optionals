package maow.optionals.javac.handler;

import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import maow.optionals.javac.util.JavacUtils;

public abstract class JavacHandler extends TreeTranslator {
    protected final JavacUtils utils;
    protected final TreeMaker maker;

    protected JavacHandler(JavacUtils utils, TreeMaker maker) {
        this.utils = utils;
        this.maker = maker;
    }
}