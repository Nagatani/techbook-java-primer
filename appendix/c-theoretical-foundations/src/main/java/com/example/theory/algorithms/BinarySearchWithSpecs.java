package com.example.theory.algorithms;

/**
 * 二分探索アルゴリズムと形式的仕様
 * リストAC-26を含む
 */
public class BinarySearchWithSpecs {
    
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
     * 
     * リストAC-26
     */
    public int binarySearch(int[] arr, int target) {
        // 事前条件のチェック
        if (arr == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        assert isSorted(arr) : "Array must be sorted";
        
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
    
    /**
     * 配列がソート済みかどうかを検証
     */
    private boolean isSorted(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] > arr[i + 1]) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 事後条件を検証するバージョン
     */
    public int binarySearchWithPostConditionCheck(int[] arr, int target) {
        int result = binarySearch(arr, target);
        
        // 事後条件の検証
        if (result >= 0) {
            assert arr[result] == target : "Post-condition violated: found index doesn't contain target";
        } else {
            assert !contains(arr, target) : "Post-condition violated: target exists but not found";
        }
        
        return result;
    }
    
    /**
     * 配列に指定の値が含まれているかチェック
     */
    private boolean contains(int[] arr, int target) {
        for (int value : arr) {
            if (value == target) {
                return true;
            }
        }
        return false;
    }
}