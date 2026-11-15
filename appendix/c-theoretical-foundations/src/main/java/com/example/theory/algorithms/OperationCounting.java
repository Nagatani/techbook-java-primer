package com.example.theory.algorithms;

/**
 * 基本操作（要素どうしの比較）の回数を数えて、計算量の増え方を確かめる
 * 実行時間ではなく回数を数えるので、結果はコンピューターによらず同じになる
 *
 * 実行: java -cp out com.example.theory.algorithms.OperationCounting
 */
public class OperationCounting {
    private static long comparisons = 0;

    // 比較を1回数えてから、a < b の結果を返す
    private static boolean less(int a, int b) {
        comparisons++;
        return a < b;
    }

    // 比較を1回数えてから、a == b の結果を返す
    private static boolean same(int a, int b) {
        comparisons++;
        return a == b;
    }

    /** 線形探索: 最悪で n 回比較する → O(n) */
    static int linearSearch(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) {
            if (same(arr[i], target)) {
                return i;
            }
        }
        return -1;
    }

    /** 二分探索: 1回ごとに範囲が半分になる → O(log n) */
    static int binarySearch(int[] arr, int target) {
        int left = 0;
        int right = arr.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (same(arr[mid], target)) {
                return mid;
            } else if (less(arr[mid], target)) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return -1;
    }

    /** バブルソート: n(n-1)/2 回比較する → Θ(n²) */
    static void bubbleSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (less(arr[j + 1], arr[j])) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }

    /** マージソート: T(n) = 2T(n/2) + O(n) → O(n log n) */
    static void mergeSort(int[] arr, int[] work, int left, int right) {
        if (right - left < 1) {
            return;
        }
        int mid = left + (right - left) / 2;
        mergeSort(arr, work, left, mid);
        mergeSort(arr, work, mid + 1, right);
        int i = left;
        int j = mid + 1;
        int k = left;
        while (i <= mid && j <= right) {
            if (less(arr[j], arr[i])) {
                work[k++] = arr[j++];
            } else {
                work[k++] = arr[i++];
            }
        }
        while (i <= mid) {
            work[k++] = arr[i++];
        }
        while (j <= right) {
            work[k++] = arr[j++];
        }
        System.arraycopy(work, left, arr, left, right - left + 1);
    }

    // 0, 1, ..., n-1 の昇順の配列
    private static int[] ascending(int n) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = i;
        }
        return arr;
    }

    // n-1, ..., 1, 0 の降順の配列
    private static int[] descending(int n) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = n - 1 - i;
        }
        return arr;
    }

    public static void main(String[] args) {
        IO.println("Number of comparisons (worst case for search: target not found)");
        IO.println("%8s %10s %8s %12s %10s".formatted("n", "linear", "binary", "bubble", "merge"));
        int[] sizes = {10, 100, 1_000, 10_000};
        for (int n : sizes) {
            comparisons = 0;
            linearSearch(ascending(n), -1);
            long linear = comparisons;

            comparisons = 0;
            binarySearch(ascending(n), -1);
            long binary = comparisons;

            comparisons = 0;
            bubbleSort(descending(n));
            long bubble = comparisons;

            comparisons = 0;
            int[] arr = descending(n);
            mergeSort(arr, new int[n], 0, n - 1);
            long merge = comparisons;

            IO.println("%8d %10d %8d %12d %10d".formatted(n, linear, binary, bubble, merge));
        }
    }
}
