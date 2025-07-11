/**
 * リスト3-57
 * コードスニペット
 * 
 * 元ファイル: chapter03-oop-basics.md (2508行目)
 */

int[] scores = {74, 88, 98, 53, 25};

// 従来のfor文
for (int i = 0; i < scores.length; i++) {
    System.out.println("点数: " + scores[i]);
}

// 拡張for文（推奨）
for (int score : scores) {
    System.out.println("点数: " + score);
}