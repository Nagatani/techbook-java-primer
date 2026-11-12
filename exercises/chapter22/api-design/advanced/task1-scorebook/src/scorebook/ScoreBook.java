package scorebook;

import java.util.ArrayList;
import java.util.List;

/**
 * 1つの科目の成績表です。
 *
 * <p>学生ごとの点数を登録した順に保持し、平均点・最高点・合格者数などを求めます。
 * 同じ名前の学生は1回だけ登録できます。</p>
 *
 * <h2>使用例</h2>
 * <pre>{@code
 * ScoreBook book = new ScoreBook("プログラミング演習");
 * book.add("青木", 82);
 * book.add("石井", 67);
 * book.add("上田", 45);
 *
 * book.average();          // 64.666...
 * book.max();              // 青木の成績
 * book.countAtLeast(60);   // 2
 * }</pre>
 *
 * <p>このクラスはスレッドセーフではありません。
 * 複数のスレッドから同時に変更しないでください。</p>
 *
 * @see ScoreEntry
 * @since 1.0
 */
public class ScoreBook {
    private final String subject;
    private final List<ScoreEntry> entries = new ArrayList<>();

    /**
     * 空の成績表を作ります。
     *
     * @param subject 科目名（空でない文字列）
     * @throws IllegalArgumentException 科目名が{@code null}か空白だけのとき
     */
    public ScoreBook(String subject) {
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("科目名が空です");
        }
        this.subject = subject;
    }

    /**
     * 科目名を返します。
     *
     * @return 科目名
     */
    public String getSubject() {
        return subject;
    }

    /**
     * 学生の点数を登録します。
     *
     * @param name  学生の名前（空でない文字列）
     * @param score 点数（0以上100以下）
     * @return 登録した成績
     * @throws IllegalArgumentException 名前や点数が不正なとき（{@link ScoreEntry}を参照）、
     *                                  または同じ名前の学生がすでに登録されているとき
     */
    public ScoreEntry add(String name, int score) {
        ScoreEntry entry = new ScoreEntry(name, score);
        for (ScoreEntry e : entries) {
            if (e.name().equals(name)) {
                throw new IllegalArgumentException(name + "はすでに登録されています");
            }
        }
        entries.add(entry);
        return entry;
    }

    /**
     * 登録されている人数を返します。
     *
     * @return 人数（登録がなければ0）
     */
    public int size() {
        return entries.size();
    }

    /**
     * 登録した順に並んだ成績の一覧を返します。
     *
     * <p>戻り値は変更できないリストです。
     * 要素を追加・削除しようとすると{@link UnsupportedOperationException}が発生します。
     * 成績表に登録するときは{@link #add(String, int)}を使ってください。</p>
     *
     * @return 成績の一覧（変更できないリスト。登録がなければ空のリスト）
     */
    public List<ScoreEntry> entries() {
        return List.copyOf(entries);
    }

    /**
     * 平均点を返します。
     *
     * @return 平均点
     * @throws IllegalStateException 1人も登録されていないとき
     */
    public double average() {
        requireNotEmpty();
        int sum = 0;
        for (ScoreEntry e : entries) {
            sum += e.score();
        }
        return (double) sum / entries.size();
    }

    /**
     * 最高点の成績を返します。
     *
     * <p>最高点の学生が複数いるときは、先に登録した学生の成績を返します。</p>
     *
     * @return 最高点の成績
     * @throws IllegalStateException 1人も登録されていないとき
     */
    public ScoreEntry max() {
        requireNotEmpty();
        ScoreEntry best = entries.get(0);
        for (ScoreEntry e : entries) {
            if (e.score() > best.score()) {
                best = e;
            }
        }
        return best;
    }

    /**
     * 指定した点数以上の人数を返します。
     *
     * <p>たとえば{@code countAtLeast(60)}は、60点以上（合格）の人数を返します。</p>
     *
     * @param threshold 基準の点数
     * @return {@code threshold}点以上の人数（登録がなければ0）
     */
    public int countAtLeast(int threshold) {
        int count = 0;
        for (ScoreEntry e : entries) {
            if (e.score() >= threshold) {
                count++;
            }
        }
        return count;
    }

    private void requireNotEmpty() {
        if (entries.isEmpty()) {
            throw new IllegalStateException("成績が登録されていません");
        }
    }
}
