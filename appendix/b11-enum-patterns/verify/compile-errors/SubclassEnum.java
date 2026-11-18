/** enumを継承するクラスは書けない（コンパイルエラーになることを確かめるプローブ） */
public class SubclassEnum {
    enum Color { RED, GREEN, BLUE }

    static class MyColor extends Color {
    }

    public static void main(String[] args) {
        IO.println(Color.RED);
    }
}
