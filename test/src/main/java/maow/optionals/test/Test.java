package maow.optionals.test;

import maow.optionals.annotations.Optional;

@SuppressWarnings("FieldCanBeLocal")
public final class Test {
    private final String s;
    private final String s2;
    private final String s3;

    public Test(String s, @Optional String s2, @Optional String s3) {
        this.s = s;
        this.s2 = s2;
        this.s3 = s3;
    }

    public void methodTest(String s, @Optional String s2) {
        System.out.println(s);
        System.out.println(s2);
    }

    public static void staticTest(String s, @Optional String s2) {
        System.out.println(s);
        System.out.println(s2);
    }
}
