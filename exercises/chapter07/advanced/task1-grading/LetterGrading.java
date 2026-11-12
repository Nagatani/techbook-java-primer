/**
 * S・A・B・C・Dの5段階で評価する（60点以上が合格）
 */
public class LetterGrading extends GradingPolicy {
    public LetterGrading() {
        super("5段階評価");
    }

    // 名前を変えて継承するクラスのためのコンストラクター
    protected LetterGrading(String name) {
        super(name);
    }

    @Override
    public String evaluate(int score) {
        if (score >= 90) {
            return "S";
        } else if (score >= 80) {
            return "A";
        } else if (score >= 70) {
            return "B";
        } else if (score >= 60) {
            return "C";
        }
        return "D";
    }

    @Override
    public boolean isPassing(int score) {
        return score >= 60;
    }
}
