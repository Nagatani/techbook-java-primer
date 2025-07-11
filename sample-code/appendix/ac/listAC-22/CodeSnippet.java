/**
 * リストAC-22
 * コードスニペット
 * 
 * 元ファイル: appendix-c-theoretical-foundations.md (626行目)
 */

// O(n²) の例：バブルソート
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

// O(n log n) の例：マージソート
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