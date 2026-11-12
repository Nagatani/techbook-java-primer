import java.util.List;

/**
 * 4択の問題。answerIndexは正解の選択肢の番号（0〜3）。
 */
public record Question(String text, List<String> choices, int answerIndex) {
    public Question {
        if (choices.size() != 4) {
            throw new IllegalArgumentException("選択肢は4つにしてください");
        }
        if (answerIndex < 0 || answerIndex >= choices.size()) {
            throw new IllegalArgumentException("正解の番号が範囲外です");
        }
        choices = List.copyOf(choices);
    }

    public String answerText() {
        return choices.get(answerIndex);
    }
}
