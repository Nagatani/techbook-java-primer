/**
 * リスト10-3
 * コードスニペット
 * 
 * 元ファイル: chapter10-collections.md (68行目)
 */

// 面倒な配列の拡張処理
String[] newStudents = new String[10];  // 大きめの配列を作成
for (int i = 0; i < students.length; i++) {
    newStudents[i] = students[i];  // 既存データをコピー
}
students = newStudents;  // 参照を付け替え
students[5] = "山田";    // やっと6人目を追加できる