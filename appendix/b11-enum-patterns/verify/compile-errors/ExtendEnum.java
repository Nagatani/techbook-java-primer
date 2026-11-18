/** java.lang.Enumを直接継承するクラスは書けない（コンパイルエラーになることを確かめるプローブ） */
public class ExtendEnum {
    @SuppressWarnings("rawtypes")
    abstract static class MyEnum extends Enum {
    }

    public static void main(String[] args) {
        IO.println("ExtendEnum");
    }
}
