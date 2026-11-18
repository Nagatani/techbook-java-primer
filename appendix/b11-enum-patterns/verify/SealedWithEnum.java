/**
 * sealedインターフェイスを実装するenumを、switchのパターンと修飾した定数名で網羅する（Java 21〜）。
 *   javac SealedWithEnum.java && java SealedWithEnum
 */
public class SealedWithEnum {
    sealed interface Shape permits Basic, Polygon {
    }

    enum Basic implements Shape { CIRCLE, SQUARE }

    record Polygon(int sides) implements Shape {
    }

    static String describe(Shape s) {
        // Basicの2つの定数とPolygonを書けば網羅したことになり、defaultは要らない
        return switch (s) {
            case Basic.CIRCLE -> "円";
            case Basic.SQUARE -> "正方形";
            case Polygon p -> p.sides() + "角形";
        };
    }

    public static void main(String[] args) {
        IO.println(describe(Basic.CIRCLE));
        IO.println(describe(Basic.SQUARE));
        IO.println(describe(new Polygon(5)));
    }
}
