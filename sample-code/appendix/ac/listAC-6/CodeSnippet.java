/**
 * リストAC-6
 * コードスニペット
 * 
 * 元ファイル: appendix-c-theoretical-foundations.md (120行目)
 */

// 配列の最大値を求めるアルゴリズム
public int findMax(int[] array) {
    if (array.length == 0) throw new IllegalArgumentException();
    
    int max = array[0];
    int i = 1;
    
    // ループ不変条件: max は array[0..i-1] の最大値
    while (i < array.length) {
        if (array[i] > max) {
            max = array[i];
        }
        i++;
        // 不変条件は保持される
    }
    
    return max; // max は array[0..length-1] の最大値
}