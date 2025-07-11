/**
 * リストAC-24
 * DynamicArrayクラス
 * 
 * 元ファイル: appendix-c-theoretical-foundations.md (699行目)
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
}