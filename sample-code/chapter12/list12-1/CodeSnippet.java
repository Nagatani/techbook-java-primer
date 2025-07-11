/**
 * リスト12-1
 * コードスニペット
 * 
 * 元ファイル: chapter12-advanced-collections.md (77行目)
 */

import java.util.Comparator;
// ...
// 匿名クラスを使ったComparatorの実装（古い書き方）
Comparator<Student> scoreComparator = new Comparator<Student>() {
    @Override
    public int compare(Student s1, Student s2) {
        return Integer.compare(s2.getScore(), s1.getScore()); // 点数の降順
    }
};