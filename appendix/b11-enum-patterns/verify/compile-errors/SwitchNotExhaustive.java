/** switch式で定数をすべて書かず、defaultもないとコンパイルエラーになることを確かめるプローブ */
public class SwitchNotExhaustive {
    enum Color { RED, GREEN, BLUE }

    static String japanese(Color c) {
        return switch (c) {
            case RED -> "赤";
            case GREEN -> "緑";
        };
    }

    public static void main(String[] args) {
        IO.println(japanese(Color.RED));
    }
}
