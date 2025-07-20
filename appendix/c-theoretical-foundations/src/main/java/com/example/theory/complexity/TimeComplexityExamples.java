package com.example.theory.complexity;

import java.util.Arrays;
import java.util.Stack;

/**
 * 時間複雑度と空間複雑度の分析例
 * リストAC-22, AC-23, AC-24を含む
 */
public class TimeComplexityExamples {
    
    /**
     * O(n²) の例：バブルソート
     * リストAC-22
     */
    public void bubbleSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {        // n-1 回
            for (int j = 0; j < n - i - 1; j++) { // (n-1) + (n-2) + ... + 1 回
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
        // 総比較回数: Σ(i=1 to n-1) i = n(n-1)/2 = O(n²)
    }
    
    /**
     * O(n log n) の例：マージソート
     * リストAC-22
     */
    public void mergeSort(int[] arr, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            
            mergeSort(arr, left, mid);      // T(n/2)
            mergeSort(arr, mid + 1, right); // T(n/2)
            merge(arr, left, mid, right);   // O(n)
        }
        // 漸化式: T(n) = 2T(n/2) + O(n)
        // 解: T(n) = O(n log n) （マスター定理より）
    }
    
    /**
     * O(1) 空間：in-place ソート
     * リストAC-23
     */
    public void quickSortIterative(int[] arr) {
        Stack<Integer> stack = new Stack<>();
        stack.push(0);
        stack.push(arr.length - 1);
        
        while (!stack.isEmpty()) {
            int high = stack.pop();
            int low = stack.pop();
            
            if (low < high) {
                int pivot = partition(arr, low, high);
                stack.push(low);
                stack.push(pivot - 1);
                stack.push(pivot + 1);
                stack.push(high);
            }
        }
        // 空間: O(log n) (スタックの最大深度)
    }
    
    /**
     * O(n) 空間：配列をコピーするソート
     * リストAC-23
     */
    public int[] mergeSortWithCopy(int[] arr) {
        if (arr.length <= 1) return arr.clone();
        
        int mid = arr.length / 2;
        int[] left = mergeSortWithCopy(Arrays.copyOf(arr, mid));
        int[] right = mergeSortWithCopy(Arrays.copyOfRange(arr, mid, arr.length));
        
        return merge(left, right);
        // 空間: O(n) (各レベルでn要素の配列を作成)
    }
    
    // ヘルパーメソッド
    private void merge(int[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;
        
        int[] L = new int[n1];
        int[] R = new int[n2];
        
        System.arraycopy(arr, left, L, 0, n1);
        System.arraycopy(arr, mid + 1, R, 0, n2);
        
        int i = 0, j = 0;
        int k = left;
        
        while (i < n1 && j < n2) {
            if (L[i] <= R[j]) {
                arr[k] = L[i];
                i++;
            } else {
                arr[k] = R[j];
                j++;
            }
            k++;
        }
        
        while (i < n1) {
            arr[k] = L[i];
            i++;
            k++;
        }
        
        while (j < n2) {
            arr[k] = R[j];
            j++;
            k++;
        }
    }
    
    private int[] merge(int[] left, int[] right) {
        int[] result = new int[left.length + right.length];
        int i = 0, j = 0, k = 0;
        
        while (i < left.length && j < right.length) {
            if (left[i] <= right[j]) {
                result[k++] = left[i++];
            } else {
                result[k++] = right[j++];
            }
        }
        
        while (i < left.length) {
            result[k++] = left[i++];
        }
        
        while (j < right.length) {
            result[k++] = right[j++];
        }
        
        return result;
    }
    
    private int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = (low - 1);
        
        for (int j = low; j < high; j++) {
            if (arr[j] < pivot) {
                i++;
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        
        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        
        return i + 1;
    }
}