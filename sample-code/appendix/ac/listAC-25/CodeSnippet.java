/**
 * リストAC-25
 * HashTableクラス
 * 
 * 元ファイル: appendix-c-theoretical-foundations.md (736行目)
 */

public class HashTable<K, V> {
    private static class Entry<K, V> {
        K key;
        V value;
        Entry<K, V> next; // チェイン法
        
        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
    
    private Entry<K, V>[] table;
    private int size;
    private int capacity;
    private static final double LOAD_FACTOR = 0.75;
    
    @SuppressWarnings("unchecked")
    public HashTable(int initialCapacity) {
        this.capacity = initialCapacity;
        this.table = new Entry[capacity];
    }
    
    public void put(K key, V value) {
        if (size >= capacity * LOAD_FACTOR) {
            resize(); // 負荷率を一定以下に保つ
        }
        
        int index = hash(key) % capacity;
        Entry<K, V> entry = table[index];
        
        // 既存キーの更新
        while (entry != null) {
            if (entry.key.equals(key)) {
                entry.value = value;
                return;
            }
            entry = entry.next;
        }
        
        // 新しいエントリの追加
        Entry<K, V> newEntry = new Entry<>(key, value);
        newEntry.next = table[index];
        table[index] = newEntry;
        size++;
    }
    
    private int hash(K key) {
        // 良いハッシュ関数は重要
        return Math.abs(key.hashCode());
    }
}