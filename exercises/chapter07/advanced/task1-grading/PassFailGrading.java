/**
 * 合格・不合格の2段階で評価する。合格点はオブジェクトごとに決める
 */
public class PassFailGrading extends GradingPolicy {
    private final int passLine;

    public PassFailGrading(int passLine) {
        super("合否判定（" + passLine + "点以上で合格）");
        this.passLine = passLine;
    }

    @Override
    public String evaluate(int score) {
        return isPassing(score) ? "合格" : "不合格";
    }

    @Override
    public boolean isPassing(int score) {
        return score >= passLine;
    }
}
