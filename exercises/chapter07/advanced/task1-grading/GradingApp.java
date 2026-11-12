/**
 * 第7章 発展課題1: 成績の付け方の切り替え（解答例）
 *
 * 同じ点数の一覧を、付け方の違う3つのGradingPolicyで成績表にします。
 */
public class GradingApp {
    public static void main(String[] args) {
        String[] names = {"青木", "伊藤", "上田", "江口", "小野"};
        int[] scores = {92, 78, 64, 55, 97};

        GradingPolicy[] policies = {
            new LetterGrading(),
            new PassFailGrading(70),
            new BonusLetterGrading(8)
        };

        // 抽象クラスの型の配列で、付け方の違いを意識せずに同じ手順を呼ぶ
        for (GradingPolicy policy : policies) {
            policy.report(names, scores);
            IO.println();
        }

        // new GradingPolicy("独自") はコンパイルエラーになる（抽象クラスはnewできない）
        GradingPolicy policy = policies[0];
        IO.println(policy.getName() + "で85点は" + policy.evaluate(85));
    }
}
