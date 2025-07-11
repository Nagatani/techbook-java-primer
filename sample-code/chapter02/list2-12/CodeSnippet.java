/**
 * リスト2-12
 * コードスニペット
 * 
 * 元ファイル: chapter02-getting-started.md (547行目)
 */

// 配列の要素を検索する
public static int findElement(int[] array, int target) {
    for (int i = 0; i < array.length; i++) {
        if (array[i] == target) {
            return i;  // 見つかった位置を返す
        }
    }
    return -1;  // 見つからなかった
}

// 配列の要素を合計する
public static int sumArray(int[] array) {
    int sum = 0;
    for (int value : array) {
        sum += value;
    }
    return sum;
}

// 配列をコピーする
int[] original = {1, 2, 3, 4, 5};
int[] copy = new int[original.length];
System.arraycopy(original, 0, copy, 0, original.length);

// またはArraysクラスを使用
import java.util.Arrays;
int[] copy2 = Arrays.copyOf(original, original.length);