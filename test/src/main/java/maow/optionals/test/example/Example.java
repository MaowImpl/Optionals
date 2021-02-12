package maow.optionals.test.example;

import maow.optionals.annotations.Optional;

public final class Example {
    private final String name;

    public Example(String name) {
        this.name = name;
    }

    public String getName(@Optional String prefix) {
        return prefix + name;
    }
}
