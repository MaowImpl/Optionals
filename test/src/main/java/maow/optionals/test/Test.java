package maow.optionals.test;

import maow.optionals.annotations.Optional;

public final class Test {
    public static void main(String[] args) {
        final Test test = new Test();
        test.test("Meme");
    }

    public void test(String s, @Optional String s2) {
        System.out.println(s);
        System.out.println(s2);
    }
}
