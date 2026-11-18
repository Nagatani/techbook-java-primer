/**
 * NullPointerExceptionの詳しいメッセージ（JEP 358）を見る。
 *
 *   javac HelpfulNpe.java && java HelpfulNpe            （ローカル変数名は<local1>などになる）
 *   javac -g HelpfulNpe.java && java HelpfulNpe         （ローカル変数名が出る）
 *   java -XX:-ShowCodeDetailsInExceptionMessages HelpfulNpe  （メッセージはnull）
 */
public class HelpfulNpe {

    static String name;
    int[] values;

    static void localVariable() {
        String s = name;
        IO.println(s.length());
    }

    static void field() {
        IO.println(name.length());
    }

    static void array(HelpfulNpe h) {
        IO.println(h.values[0]);
    }

    static void chain(HelpfulNpe h) {
        IO.println(h.values.length);
    }

    static void run(String label, Runnable r) {
        try {
            r.run();
        } catch (NullPointerException e) {
            IO.println(label + ": " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        run("ローカル変数", HelpfulNpe::localVariable);
        run("静的フィールド", HelpfulNpe::field);
        run("配列", () -> array(new HelpfulNpe()));
        run("引数", () -> chain(null));
        run("明示的なthrow", () -> {
            throw new NullPointerException();
        });
    }
}
