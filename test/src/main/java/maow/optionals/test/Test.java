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

    public void oneTest(@Optional String s) {
        System.out.println(s);
    }

    public void defaultTest(
            @Optional(classValue = String.class) Class<String> c,
            @Optional(stringValue = "Meme.") String s,
            @Optional(byteValue = (byte) 1) byte by,
            @Optional(shortValue = (short) 1) short sh,
            @Optional(intValue = 1) int i,
            @Optional(longValue = 1) long l,
            @Optional(floatValue = 1) float f,
            @Optional(doubleValue = 1) double d,
            @Optional(booleanValue = true) boolean b
    ) {}
}