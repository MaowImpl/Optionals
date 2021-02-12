package maow.optionals.test.example;

public class ExampleMain {
    public static void main(String[] args) {
        final Example example = new Example("my.name");
        System.out.println(example.getName());
        System.out.println(example.getName("prefix."));
    }
}
