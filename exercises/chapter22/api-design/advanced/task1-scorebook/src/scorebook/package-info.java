/**
 * 科目の成績を記録し、平均点や最高点などを求めるライブラリです。
 *
 * <p>中心となるクラスは {@link scorebook.ScoreBook} です。
 * 学生の名前と点数を登録し、集計のメソッドで結果を受け取ります。</p>
 *
 * <pre>{@code
 * ScoreBook book = new ScoreBook("プログラミング演習");
 * book.add("青木", 82);
 * book.add("石井", 67);
 * double average = book.average();   // 74.5
 * }</pre>
 *
 * @since 1.0
 */
package scorebook;
