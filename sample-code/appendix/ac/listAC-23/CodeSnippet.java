/**
 * リストAC-23
 * コードスニペット
 * 
 * 元ファイル: appendix-c-theoretical-foundations.md (659行目)
 */

// O(1) 空間：in-place ソート
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

// O(n) 空間：配列をコピーするソート
public int[] mergeSortWithCopy(int[] arr) {
    if (arr.length <= 1) return arr.clone();
    
    int mid = arr.length / 2;
    int[] left = mergeSortWithCopy(Arrays.copyOf(arr, mid));
    int[] right = mergeSortWithCopy(Arrays.copyOfRange(arr, mid, arr.length));
    
    return merge(left, right);
    // 空間: O(n) (各レベルでn要素の配列を作成)
}