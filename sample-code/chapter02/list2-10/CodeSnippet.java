/**
 * リスト2-10
 * コードスニペット
 * 
 * 元ファイル: chapter02-getting-started.md (491行目)
 */

// 配列の宣言と初期化
int[] numbers = {1, 2, 3, 4, 5};

// または
int[] scores = new int[10];  // 10要素の配列
scores[0] = 100;
scores[1] = 95;

// 配列の長さ
System.out.println("配列の長さ: " + numbers.length);

// 多次元配列
int[][] matrix = {
    {1, 2, 3},
    {4, 5, 6},
    {7, 8, 9}
};