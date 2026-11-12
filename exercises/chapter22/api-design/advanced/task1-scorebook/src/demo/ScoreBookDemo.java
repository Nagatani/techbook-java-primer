package demo;

import scorebook.ScoreBook;
import scorebook.ScoreEntry;

/**
 * scorebookライブラリを使う側のプログラム（公開されたメソッドだけを使う）
 */
public class ScoreBookDemo {
    private ScoreBookDemo() {
    }

    /**
     * 成績を登録して集計する。
     *
     * @param args 使わない
     */
    public static void main(String[] args) {
        ScoreBook book = new ScoreBook("プログラミング演習");
        book.add("青木", 82);
        book.add("石井", 67);
        book.add("上田", 45);
        book.add("江藤", 91);

        IO.println("科目: " + book.getSubject() + "（" + book.size() + "人）");
        for (ScoreEntry e : book.entries()) {
            IO.println("  " + e.name() + " " + e.score() + "点");
        }
        IO.println("平均点: " + "%.1f".formatted(book.average()));
        ScoreEntry best = book.max();
        IO.println("最高点: " + best.name() + " " + best.score() + "点");
        IO.println("60点以上: " + book.countAtLeast(60) + "人");

        // ドキュメントに書いた例外が、書いたとおりに発生することを確かめる
        try {
            book.add("石井", 70);
        } catch (IllegalArgumentException e) {
            IO.println("登録できません: " + e.getMessage());
        }
        try {
            book.entries().clear();
        } catch (UnsupportedOperationException e) {
            IO.println("entries()の戻り値は変更できません");
        }
        try {
            new ScoreBook("情報数学").average();
        } catch (IllegalStateException e) {
            IO.println("平均点を求められません: " + e.getMessage());
        }
    }
}
