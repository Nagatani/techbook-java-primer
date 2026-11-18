/**
 * クラス階層解析（CHA）と脱最適化を観察する。
 *
 *   java -XX:+PrintCompilation ClassHierarchy
 */
public class ClassHierarchy {
    static class Calculator {            // finalではない
        int add(int a, int b) { return a + b; }
    }

    static int sum(Calculator calc, int n) {
        int s = 0;
        for (int i = 0; i < n; i++) {
            s = calc.add(s, i);
        }
        return s;
    }

    public static void main(String[] args) throws Exception {
        Calculator calc = new Calculator();
        int r = 0;
        for (int i = 0; i < 20_000; i++) {
            r += sum(calc, 100);
        }
        IO.println("--- サブクラスを読み込む ---");
        // ここで初めて、addをオーバーライドするサブクラスが読み込まれる
        Class<?> sub = Class.forName("ClassHierarchy$LoggingCalculator");
        Calculator other = (Calculator) sub.getDeclaredConstructor().newInstance();
        r += sum(other, 1);
        IO.println("result=" + r);
    }

    static class LoggingCalculator extends Calculator {
        @Override
        int add(int a, int b) { return a + b; }
    }
}
