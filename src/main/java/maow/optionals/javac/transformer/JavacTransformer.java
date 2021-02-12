package maow.optionals.javac.transformer;

public interface JavacTransformer<T> {
    void transform(T t);
}
