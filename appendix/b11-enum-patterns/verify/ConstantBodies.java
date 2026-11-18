import java.util.EnumSet;

/**
 * 定数ごとの本体（クラス本体）を持つenumを観察する。
 *   javac ConstantBodies.java && javap -p 'ConstantBodies$Op' 'ConstantBodies$Op$1'
 *   javap -v 'ConstantBodies$Op' | grep -A4 PermittedSubclasses
 *   java ConstantBodies
 */
public class ConstantBodies {
    enum Op {
        PLUS("+") {
            @Override
            int apply(int a, int b) { return a + b; }
        },
        MINUS("-") {
            @Override
            int apply(int a, int b) { return a - b; }
        },
        // 本体を持たない定数（applyの既定の実装を使う）
        TIMES("*");

        private final String symbol;

        Op(String symbol) {
            this.symbol = symbol;
        }

        int apply(int a, int b) { return a * b; }

        String symbol() { return symbol; }
    }

    public static void main(String[] args) {
        for (Op op : Op.values()) {
            IO.println("%s: 3 %s 2 = %d".formatted(op, op.symbol(), op.apply(3, 2)));
        }
        for (Op op : Op.values()) {
            IO.println("%s getClass=%s getDeclaringClass=%s getClass().isEnum()=%b".formatted(
                op, op.getClass().getName(), op.getDeclaringClass().getName(), op.getClass().isEnum()));
        }
        // EnumSetやvalueOfは、getDeclaringClass()の型で扱う
        IO.println("EnumSet.allOf: " + EnumSet.allOf(Op.class));
        IO.println("valueOf(\"PLUS\") == PLUS: " + (Op.valueOf("PLUS") == Op.PLUS));
    }
}
