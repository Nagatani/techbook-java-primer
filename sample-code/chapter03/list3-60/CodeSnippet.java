/**
 * リスト3-60
 * コードスニペット
 * 
 * 元ファイル: chapter03-oop-basics.md (2556行目)
 */

// 範囲外アクセスの例（エラーになる）
int[] arr = new int[5];
// arr[5] = 10;  // エラー！インデックスは0-4まで

// 参照の共有の例
int[] arr1 = {1, 2, 3};
int[] arr2 = arr1;  // 参照のコピー
arr2[0] = 99;
System.out.println(arr1[0]);  // 99と表示される