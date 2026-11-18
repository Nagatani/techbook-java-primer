/**
 * よく呼ばれるdefaultメソッドも、ふつうのメソッドと同じようにJITコンパイラーがインライン化することを観察する。
 * java -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining DefaultInlining で実行する。
 */
public class DefaultInlining {

    interface Shape {
        double side();

        default double area() {
            return side() * side();
        }
    }

    static final class Square implements Shape {
        private final double side;

        Square(double side) {
            this.side = side;
        }

        @Override
        public double side() {
            return side;
        }
    }

    static double total(Shape shape, int n) {
        double sum = 0;
        for (int i = 0; i < n; i++) {
            sum += shape.area();
        }
        return sum;
    }

    public static void main(String[] args) {
        Shape shape = new Square(2);
        double sum = 0;
        for (int i = 0; i < 200; i++) {
            sum += total(shape, 10_000);
        }
        IO.println("sum = " + sum);
    }
}
