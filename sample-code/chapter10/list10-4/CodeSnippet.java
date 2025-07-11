/**
 * リスト10-4
 * コードスニペット
 * 
 * 元ファイル: chapter10-collections.md (83行目)
 */

// 鈴木さん（インデックス2）が転校
students[2] = null;  // nullを代入しても...

// 配列に「穴」ができてしまう
// [田中, 佐藤, null, 高橋, 伊藤]

// 穴を埋めるには手動で詰める必要がある
for (int i = 2; i < students.length - 1; i++) {
    students[i] = students[i + 1];
}
students[students.length - 1] = null;