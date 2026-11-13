/**
 * 基本的な例外処理（課題1の解答例）
 */
public class BasicExceptionHandling {

    // 0で割るとArithmeticException。finallyは、例外が起きても起きなくても実行される
    static void divide(int a, int b) {
        IO.println("計算実行: " + a + " / " + b);
        try {
            IO.println("結果: " + (a / b));
        } catch (ArithmeticException e) {
            IO.println("例外発生: " + e);
        } finally {
            IO.println("finally実行: 計算処理を終了します");
        }
    }

    // nullのメソッドを呼ぶとNullPointerException
    static void stringLength(String s) {
        IO.println("文字列の長さを求める: " + s);
        try {
            IO.println("長さ: " + s.length());
        } catch (NullPointerException e) {
            IO.println("例外発生: " + e.getClass().getName());
        }
    }

    // 配列の範囲の外を読むとArrayIndexOutOfBoundsException
    static void arrayAccess(int index) {
        int[] array = new int[5];
        IO.println("配列アクセス: array[" + index + "] (配列サイズ: " + array.length + ")");
        try {
            IO.println("値: " + array[index]);
        } catch (ArrayIndexOutOfBoundsException e) {
            IO.println("例外発生: " + e.getMessage());
        } finally {
            IO.println("finally実行: 配列処理を終了します");
        }
    }

    // 起きうる例外ごとにcatchを分ける。どちらかが起きたら、代わりの値0を返す
    static int parseOrDefault(String text) {
        IO.println("入力値: \"" + text + "\"");
        try {
            int[] values = {10, 20, 30};
            int i = Integer.parseInt(text);
            return values[i];
        } catch (NumberFormatException e) {
            IO.println("NumberFormatException: 数値に変換できません");
        } catch (ArrayIndexOutOfBoundsException e) {
            IO.println("ArrayIndexOutOfBoundsException: 0〜2で指定してください");
        }
        IO.println("代わりの値を使います: 0");
        return 0;
    }

    // 受け取った例外を、ここでは処理しきれないので呼び出し元へ投げ直す
    static void inner() {
        try {
            Integer.parseInt("12x");
        } catch (NumberFormatException e) {
            IO.println("内側のメソッド: 変換に失敗したので、呼び出し元へ投げ直します");
            throw e;
        }
    }

    public static void main(String[] args) {
        IO.println("--- ゼロ除算 ---");
        divide(10, 0);
        IO.println("\n--- null参照 ---");
        stringLength(null);
        IO.println("\n--- 配列の範囲外 ---");
        arrayAccess(10);
        IO.println("\n--- 複数のcatch ---");
        IO.println("結果: " + parseOrDefault("abc"));
        IO.println("結果: " + parseOrDefault("5"));
        IO.println("結果: " + parseOrDefault("1"));
        IO.println("\n--- 例外の投げ直し ---");
        try {
            inner();
        } catch (NumberFormatException e) {
            IO.println("外側のmain: " + e.getMessage());
        }
    }
}
