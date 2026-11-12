package scorebook;

/**
 * 1人の学生の成績です。
 *
 * <p>このレコードは不変です。点数を変えるときは、新しいオブジェクトを作ります。</p>
 *
 * @param name  学生の名前（空でない文字列）
 * @param score 点数（0以上100以下）
 * @since 1.0
 */
public record ScoreEntry(String name, int score) {

    /**
     * 引数を検査して成績を作ります。
     *
     * @param name  学生の名前（空でない文字列）
     * @param score 点数（0以上100以下）
     * @throws IllegalArgumentException 名前が{@code null}か空白だけのとき、
     *                                  または点数が0〜100の範囲外のとき
     */
    public ScoreEntry {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("名前が空です");
        }
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("点数は0〜100です: " + score);
        }
    }
}
