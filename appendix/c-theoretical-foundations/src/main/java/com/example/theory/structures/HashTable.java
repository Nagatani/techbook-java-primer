package com.example.theory.structures;

/**
 * ハッシュテーブルの理論的実装
 * キーにnullは使えない
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
        
        int index = indexFor(key, capacity);
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
    
    public V get(K key) {
        Entry<K, V> entry = findEntry(key);
        return entry == null ? null : entry.value;
    }
    
    public boolean containsKey(K key) {
        // get(key) != null では、値がnullのエントリを「ない」と誤る
        return findEntry(key) != null;
    }
    
    private Entry<K, V> findEntry(K key) {
        Entry<K, V> entry = table[indexFor(key, capacity)];
        while (entry != null) {
            if (entry.key.equals(key)) {
                return entry;
            }
            entry = entry.next;
        }
        return null;
    }
    
    private int indexFor(K key, int tableLength) {
        // 良いハッシュ関数は重要
        // Math.abs(hashCode()) % n は、hashCodeがInteger.MIN_VALUEのとき負になる
        // （Math.absの結果が負のまま）。floorModは常に0以上n未満を返す
        return Math.floorMod(key.hashCode(), tableLength);
    }
    
    @SuppressWarnings("unchecked")
    private void resize() {
        int newCapacity = capacity * 2;
        Entry<K, V>[] newTable = new Entry[newCapacity];
        
        // 全エントリの再ハッシュ
        for (Entry<K, V> entry : table) {
            while (entry != null) {
                Entry<K, V> next = entry.next;
                int index = indexFor(entry.key, newCapacity);
                entry.next = newTable[index];
                newTable[index] = entry;
                entry = next;
            }
        }
        
        table = newTable;
        capacity = newCapacity;
    }
    
    public int size() {
        return size;
    }
    
    public double getLoadFactor() {
        return (double) size / capacity;
    }
    
    /**
     * ハッシュテーブルの性能分析
     */
    public void performanceAnalysis() {
        IO.println("Hash Table Performance Analysis:");
        IO.println("Size: " + size);
        IO.println("Capacity: " + capacity);
        IO.println("Load Factor: " + getLoadFactor());
        
        // チェインの長さの分析
        int maxChainLength = 0;
        int nonEmptyBuckets = 0;
        
        for (Entry<K, V> entry : table) {
            if (entry != null) {
                nonEmptyBuckets++;
                int chainLength = 0;
                while (entry != null) {
                    chainLength++;
                    entry = entry.next;
                }
                maxChainLength = Math.max(maxChainLength, chainLength);
            }
        }
        
        IO.println("Non-empty buckets: " + nonEmptyBuckets);
        IO.println("Max chain length: " + maxChainLength);
        IO.println("Average chain length: " + (nonEmptyBuckets > 0 ? (double) size / nonEmptyBuckets : 0));
    }
}