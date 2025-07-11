/**
 * リストAC-26
 * コードスニペット
 * 
 * 元ファイル: appendix-c-theoretical-foundations.md (810行目)
 */

/**
 * 二分探索アルゴリズム
 * @param arr ソート済み配列
 * @param target 探索する値
 * @return 見つかった場合はインデックス、見つからない場合は-1
 * 
 * 事前条件: arr != null && isSorted(arr)
 * 事後条件: 
 *   result >= 0 => arr[result] == target
 *   result == -1 => ∀i. arr[i] != target
 */
public int binarySearch(int[] arr, int target) {
    int left = 0;
    int right = arr.length - 1;
    
    // ループ不変条件:
    // target が存在するなら arr[left..right] の範囲内にある
    while (left <= right) {
        int mid = left + (right - left) / 2;
        
        if (arr[mid] == target) {
            return mid; // 事後条件を満たす
        } else if (arr[mid] < target) {
            left = mid + 1;
        } else {
            right = mid - 1;
        }
    }
    
    return -1; // 事後条件を満たす
}