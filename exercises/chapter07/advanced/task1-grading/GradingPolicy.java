/**
 * 成績の付け方を表す抽象クラス
 *
 * 成績表を作る手順（report）はこのクラスに1回だけ書く。
 * 点数から評価を決める方法（evaluate）と合格の基準（isPassing）は、
 * 付け方ごとに違うので抽象メソッドにして、子クラスに任せる。
 */
public abstract class GradingPolicy {
    private final String name;

    protected GradingPolicy(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // 点数から評価の文字列を決める。子クラスで必ず実装する
    public abstract String evaluate(int score);

    // 合格かどうか。子クラスで必ず実装する
    public abstract boolean isPassing(int score);

    // 評価の前に点数を調整するフックメソッド。標準では何もしない
    protected int adjust(int score) {
        return score;
    }

    // 成績表を表示する手順（テンプレートメソッド）。子クラスで変えられないようfinalにする
    public final void report(String[] names, int[] scores) {
        IO.println("--- " + name + " ---");
        int passed = 0;
        for (int i = 0; i < names.length; i++) {
            int adjusted = adjust(scores[i]);
            String grade = evaluate(adjusted);
            boolean pass = isPassing(adjusted);
            if (pass) {
                passed++;
            }
            String shown = (adjusted == scores[i]) ? String.valueOf(scores[i]) : scores[i] + "→" + adjusted;
            IO.println("  " + (pass ? "○ " : "× ") + names[i] + " " + shown + "点 " + grade);
        }
        IO.println("  合格者: " + passed + "人 / " + names.length + "人");
    }
}
