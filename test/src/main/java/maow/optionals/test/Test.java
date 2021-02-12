package maow.optionals.test;

import maow.optionals.annotations.AllOptional;
import maow.optionals.annotations.Optional;

public final class Test {
    public static void main(String[] args) {
        final Test test = new Test();

        test.methodTest("loohcS emeM");
        staticTest("Static School");

        test.oneTest();
    }

    public void methodTest(String s, @Optional String s2) {
        System.out.println(s);
        System.out.println(s2);
    }

    public static void staticTest(String s, @Optional String s2) {
        System.out.println(s);
        System.out.println(s2);
    }

    public void oneTest(@Optional String s) {
        System.out.println(s);
    }

    public void defaultTest(
            @Optional(classValue = String.class) Class<String> c,
            @Optional(charValue = 'e') char ch,
            @Optional(stringValue = "Meme.") String s,
            @Optional(byteValue = (byte) 1) byte by,
            @Optional(shortValue = (short) 1) short sh,
            @Optional(intValue = 1) int i,
            @Optional(longValue = 1) long l,
            @Optional(floatValue = 1) float f,
            @Optional(doubleValue = 1) double d,
            @Optional(booleanValue = true) boolean b
    ) {}

    public void charTest(@Optional char c) {
        System.out.println(c);
    }

    @AllOptional
    public void allOptionalTest(String s, String s2) {}
}