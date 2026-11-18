/**
 * バイトコードがオペランドスタックとローカル変数を使って計算する様子を、javapで観察する。
 *
 *   javac StackMachine.java
 *   javap -c -v 'StackMachine$Calc'      （stack=・locals=が、オペランドスタックの深さとローカル変数の数）
 *   javap -c -v 'StackMachine$Counter'
 */
public class StackMachine {
    // コンストラクターを持たないインターフェイスに置き、javapの出力をこの3つのメソッドだけにする
    interface Calc {
        // 引数aとbがローカル変数の0番と1番に入る
        static int add(int a, int b) {
            int c = a + b;
            return c;
        }

        // longはローカル変数を2つずつ使う（aが0番と1番、bが2番、cが3番と4番）
        static long widen(long a, int b) {
            long c = a * b;
            return c;
        }

        // 式が深くなると、オペランドスタックも深くなる
        static int expression(int a, int b, int c, int d) {
            return (a + b) * (c + d);
        }
    }

    static class Counter {
        private int base = 100;

        // インスタンスメソッドでは、ローカル変数の0番がthisになる
        int addBase(int x) {
            return base + x;
        }
    }

    public static void main(String[] args) {
        IO.println("add(2, 3) = " + Calc.add(2, 3));
        IO.println("widen(4, 5) = " + Calc.widen(4L, 5));
        IO.println("expression(1, 2, 3, 4) = " + Calc.expression(1, 2, 3, 4));
        IO.println("addBase(1) = " + new Counter().addBase(1));
    }
}
