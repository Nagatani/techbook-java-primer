public class CompactConstructor {
    record Student(String name, int score) {
        // コンパクトコンストラクターには値の確認だけを書く。
        // フィールドへの代入は、この本体のあとでコンパイラーが行う
        Student {
            if (score < 0) {
                throw new IllegalArgumentException("点数は0以上にしてください");
            }
            // 引数の値を整えたいときは、引数に代入し直す
            name = name.strip();
        }
    }

    public static void main(String[] args) {
        IO.println(new Student("  田中 ", 80));
        try {
            new Student("佐藤", -1);
        } catch (IllegalArgumentException e) {
            IO.println("エラー: " + e.getMessage());
        }
    }
}
