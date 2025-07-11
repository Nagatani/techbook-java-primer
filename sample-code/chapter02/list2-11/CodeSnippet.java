/**
 * リスト2-11
 * コードスニペット
 * 
 * 元ファイル: chapter02-getting-started.md (522行目)
 */

// 配列の全要素を処理する安全な方法
int[] data = {10, 20, 30, 40, 50};

// インデックスを使用した従来の方法
for (int i = 0; i < data.length; i++) {
    System.out.println("data[" + i + "] = " + data[i]);
}

// 拡張for文を使用した推奨方法
for (int value : data) {
    System.out.println("値: " + value);
}