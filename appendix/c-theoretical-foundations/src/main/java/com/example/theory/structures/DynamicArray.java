package com.example.theory.structures;

/**
 * 動的配列（ArrayList）の償却解析の例
 * リストAC-24
 */
public class DynamicArray<T> {
    private Object[] array;
    private int size;
    private int capacity;
    
    public DynamicArray() {
        capacity = 1;
        array = new Object[capacity];
        size = 0;
    }
    
    public void add(T element) {
        if (size == capacity) {
            resize(); // O(n) だが頻度は低い
        }
        array[size++] = element; // O(1)
    }
    
    private void resize() {
        capacity *= 2; // 容量を倍増
        Object[] newArray = new Object[capacity];
        System.arraycopy(array, 0, newArray, 0, size);
        array = newArray;
    }
    
    @SuppressWarnings("unchecked")
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return (T) array[index];
    }
    
    public int size() {
        return size;
    }
    
    public int capacity() {
        return capacity;
    }
    
    /**
     * 償却解析のデモンストレーション
     * n回のadd操作の総コストを計算
     */
    public static void amortizedAnalysisDemo(int n) {
        DynamicArray<Integer> array = new DynamicArray<>();
        int totalCost = 0;
        int resizeCount = 0;
        
        for (int i = 0; i < n; i++) {
            int oldCapacity = array.capacity();
            array.add(i);
            
            // リサイズが発生した場合のコストを計算
            if (array.capacity() > oldCapacity) {
                totalCost += oldCapacity; // コピーのコスト
                resizeCount++;
            }
            totalCost++; // 要素追加のコスト
        }
        
        System.out.println("n = " + n);
        System.out.println("Total cost: " + totalCost);
        System.out.println("Resize count: " + resizeCount);
        System.out.println("Amortized cost per operation: " + (double) totalCost / n);
        
        // 理論的な期待値
        // 総コスト: n + (1 + 2 + 4 + ... + n) < n + 2n = 3n
        // 償却コスト: O(3n/n) = O(1)
    }
}