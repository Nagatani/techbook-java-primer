/**
 * 課題の提出で加点する5段階評価。評価の決め方はLetterGradingのまま、
 * フックメソッドadjustだけをオーバーライドする
 */
public class BonusLetterGrading extends LetterGrading {
    private final int bonus;

    public BonusLetterGrading(int bonus) {
        super("5段階評価（課題点" + bonus + "点を加算、上限100点）");
        this.bonus = bonus;
    }

    @Override
    protected int adjust(int score) {
        return Math.min(score + bonus, 100);
    }
}
