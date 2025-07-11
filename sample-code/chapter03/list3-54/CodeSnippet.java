/**
 * リスト3-54
 * コードスニペット
 * 
 * 元ファイル: chapter03-oop-basics.md (2425行目)
 */

int[] scores = {74, 88, 98, 53, 25};

// 要素の最後にアクセス
System.out.println(scores[scores.length - 1]); //25と表示される

// 各要素を順に表示
for (int i = 0; i < scores.length; i++) {
  System.out.print(scores[i] + ", "); // 74, 88, 98, 53, 25と表示される。
}