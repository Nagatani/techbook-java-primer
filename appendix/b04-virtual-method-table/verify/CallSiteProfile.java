/**
 * 呼び出し箇所に現れる型の数で、JITのインライン化がどう変わるかを観察する。
 *
 *   java -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining CallSiteProfile mono
 *   （mono・bi・megaのどれかを指定する）
 */
public class CallSiteProfile {
    interface Shape { double area(); }
    static final class Circle implements Shape { public double area() { return 3.0; } }
    static final class Square implements Shape { public double area() { return 4.0; } }
    static final class Triangle implements Shape { public double area() { return 5.0; } }

    // 呼び出し箇所 shape.area() を1つだけ持つメソッド
    static double total(Shape[] shapes) {
        double sum = 0;
        for (Shape shape : shapes) {
            sum += shape.area();
        }
        return sum;
    }

    public static void main(String[] args) {
        int kinds = switch (args.length == 0 ? "mono" : args[0]) {
            case "mono" -> 1;
            case "bi" -> 2;
            default -> 3;
        };
        Shape[] shapes = new Shape[1200];
        for (int i = 0; i < shapes.length; i++) {
            shapes[i] = switch (i % kinds) {
                case 0 -> new Circle();
                case 1 -> new Square();
                default -> new Triangle();
            };
        }
        double result = 0;
        for (int i = 0; i < 20_000; i++) {
            result += total(shapes);
        }
        IO.println("kinds=" + kinds + " result=" + result);
    }
}
