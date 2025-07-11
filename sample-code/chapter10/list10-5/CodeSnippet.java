/**
 * リスト10-5
 * コードスニペット
 * 
 * 元ファイル: chapter10-collections.md (102行目)
 */

// 特定の学生が在籍しているか確認
boolean found = false;
for (String student : students) {
    if (student != null && student.equals("田中")) {
        found = true;
        break;
    }
}

// 学生数のカウント（nullを除く）
int count = 0;
for (String student : students) {
    if (student != null) {
        count++;
    }
}