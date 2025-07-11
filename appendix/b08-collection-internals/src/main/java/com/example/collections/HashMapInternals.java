package com.example.collections;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * HashMap内部実装の詳細デモンストレーション
 * ハッシュテーブル、衝突処理、負荷係数の影響を学習
 */
public class HashMapInternals {
    
    /**
     * 簡略化されたHashMap実装
     */
    public static class SimpleHashMap<K, V> {
        private static final int DEFAULT_CAPACITY = 16;
        private static final double DEFAULT_LOAD_FACTOR = 0.75;
        
        private Node<K, V>[] table;
        private int size;
        private int threshold;
        private final double loadFactor;
        
        static class Node<K, V> {
            final int hash;
            final K key;
            V value;
            Node<K, V> next;
            
            Node(int hash, K key, V value, Node<K, V> next) {
                this.hash = hash;
                this.key = key;
                this.value = value;
                this.next = next;
            }
            
            @Override
            public String toString() {
                return key + "=" + value + (next != null ? " -> " + next : "");
            }
        }
        
        @SuppressWarnings("unchecked")
        public SimpleHashMap() {
            this.loadFactor = DEFAULT_LOAD_FACTOR;
            this.table = new Node[DEFAULT_CAPACITY];
            this.threshold = (int) (DEFAULT_CAPACITY * DEFAULT_LOAD_FACTOR);
        }
        
        @SuppressWarnings("unchecked")
        public SimpleHashMap(int initialCapacity, double loadFactor) {
            this.loadFactor = loadFactor;
            this.table = new Node[initialCapacity];
            this.threshold = (int) (initialCapacity * loadFactor);
        }
        
        /**
         * ハッシュ値の計算（Java 8のアルゴリズム）
         */
        private int hash(Object key) {
            if (key == null) return 0;
            int h = key.hashCode();
            return h ^ (h >>> 16); // 上位16ビットと下位16ビットのXOR
        }
        
        /**
         * バケットインデックスの計算
         */
        private int indexFor(int hash, int length) {
            return hash & (length - 1); // length が2の累乗の場合の最適化
        }
        
        /**
         * 値の取得
         */
        public V get(Object key) {
            int hash = hash(key);
            int index = indexFor(hash, table.length);
            
            for (Node<K, V> node = table[index]; node != null; node = node.next) {
                if (node.hash == hash && Objects.equals(node.key, key)) {
                    return node.value;
                }
            }
            return null;
        }
        
        /**
         * 値の設定
         */
        public V put(K key, V value) {
            int hash = hash(key);
            int index = indexFor(hash, table.length);
            
            // 既存キーの検索
            for (Node<K, V> node = table[index]; node != null; node = node.next) {
                if (node.hash == hash && Objects.equals(node.key, key)) {
                    V oldValue = node.value;
                    node.value = value;
                    return oldValue;
                }
            }
            
            // 新しいノードの追加
            Node<K, V> newNode = new Node<>(hash, key, value, table[index]);
            table[index] = newNode;
            size++;
            
            // リサイズが必要かチェック
            if (size >= threshold) {
                resize();
            }
            
            return null;
        }
        
        /**
         * テーブルのリサイズ
         */
        @SuppressWarnings("unchecked")
        private void resize() {
            Node<K, V>[] oldTable = table;
            int oldCapacity = oldTable.length;
            int newCapacity = oldCapacity * 2;
            
            table = new Node[newCapacity];
            threshold = (int) (newCapacity * loadFactor);
            size = 0;
            
            // 既存エントリの再配置
            for (Node<K, V> head : oldTable) {
                for (Node<K, V> node = head; node != null; node = node.next) {
                    put(node.key, node.value);
                }
            }
            
            System.out.println("Resized from " + oldCapacity + " to " + newCapacity);
        }
        
        /**
         * サイズの取得
         */
        public int size() {
            return size;
        }
        
        /**
         * 内部状態の表示（デバッグ用）
         */
        public void printInternalState() {
            System.out.println("=== HashMap Internal State ===");
            System.out.println("Size: " + size);
            System.out.println("Capacity: " + table.length);
            System.out.println("Load Factor: " + loadFactor);
            System.out.println("Threshold: " + threshold);
            System.out.println("Current Load: " + String.format("%.2f", (double) size / table.length));
            
            for (int i = 0; i < table.length; i++) {
                if (table[i] != null) {
                    int chainLength = getChainLength(table[i]);
                    System.out.println("Bucket " + i + ": " + table[i] + " (chain length: " + chainLength + ")");
                }
            }
        }
        
        private int getChainLength(Node<K, V> head) {
            int length = 0;
            for (Node<K, V> node = head; node != null; node = node.next) {
                length++;
            }
            return length;
        }
        
        /**
         * 衝突統計の取得
         */
        public CollisionStats getCollisionStats() {
            int usedBuckets = 0;
            int totalChainLength = 0;
            int maxChainLength = 0;
            int emptyBuckets = 0;
            
            for (Node<K, V> head : table) {
                if (head == null) {
                    emptyBuckets++;
                } else {
                    usedBuckets++;
                    int chainLength = getChainLength(head);
                    totalChainLength += chainLength;
                    maxChainLength = Math.max(maxChainLength, chainLength);
                }
            }
            
            return new CollisionStats(
                table.length,
                usedBuckets,
                emptyBuckets,
                totalChainLength,
                maxChainLength,
                usedBuckets > 0 ? (double) totalChainLength / usedBuckets : 0
            );
        }
    }
    
    /**
     * 衝突統計クラス
     */
    public static class CollisionStats {
        public final int totalBuckets;
        public final int usedBuckets;
        public final int emptyBuckets;
        public final int totalChainLength;
        public final int maxChainLength;
        public final double averageChainLength;
        
        public CollisionStats(int totalBuckets, int usedBuckets, int emptyBuckets,
                            int totalChainLength, int maxChainLength, double averageChainLength) {
            this.totalBuckets = totalBuckets;
            this.usedBuckets = usedBuckets;
            this.emptyBuckets = emptyBuckets;
            this.totalChainLength = totalChainLength;
            this.maxChainLength = maxChainLength;
            this.averageChainLength = averageChainLength;
        }
        
        @Override
        public String toString() {
            return String.format(
                "CollisionStats{buckets=%d, used=%d(%.1f%%), empty=%d(%.1f%%), " +
                "maxChain=%d, avgChain=%.2f}",
                totalBuckets, usedBuckets, usedBuckets * 100.0 / totalBuckets,
                emptyBuckets, emptyBuckets * 100.0 / totalBuckets,
                maxChainLength, averageChainLength
            );
        }
    }
    
    /**
     * 悪いhashCode実装の例
     */
    static class BadHashCodeExample {
        private final String name;
        private final int id;
        
        public BadHashCodeExample(String name, int id) {
            this.name = name;
            this.id = id;
        }
        
        @Override
        public int hashCode() {
            return 1; // 全オブジェクトが同じハッシュ値
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof BadHashCodeExample)) return false;
            BadHashCodeExample other = (BadHashCodeExample) obj;
            return Objects.equals(name, other.name) && id == other.id;
        }
        
        @Override
        public String toString() {
            return name + "(" + id + ")";
        }
    }
    
    /**
     * 良いhashCode実装の例
     */
    static class GoodHashCodeExample {
        private final String name;
        private final int id;
        
        public GoodHashCodeExample(String name, int id) {
            this.name = name;
            this.id = id;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(name, id); // 適切なハッシュ値計算
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof GoodHashCodeExample)) return false;
            GoodHashCodeExample other = (GoodHashCodeExample) obj;
            return Objects.equals(name, other.name) && id == other.id;
        }
        
        @Override
        public String toString() {
            return name + "(" + id + ")";
        }
    }
    
    /**
     * 負荷係数の影響をデモンストレーション
     */
    public static void demonstrateLoadFactorImpact() {
        System.out.println("=== Load Factor Impact Demo ===");
        
        double[] loadFactors = {0.5, 0.75, 1.0, 1.5};
        int itemCount = 1000;
        
        for (double loadFactor : loadFactors) {
            System.out.println("\n--- Load Factor: " + loadFactor + " ---");
            
            SimpleHashMap<Integer, String> map = new SimpleHashMap<>(16, loadFactor);
            
            long startTime = System.nanoTime();
            for (int i = 0; i < itemCount; i++) {
                map.put(i, "Value" + i);
            }
            long insertTime = System.nanoTime() - startTime;
            
            startTime = System.nanoTime();
            for (int i = 0; i < itemCount; i++) {
                map.get(i);
            }
            long retrieveTime = System.nanoTime() - startTime;
            
            CollisionStats stats = map.getCollisionStats();
            
            System.out.println("Insert time: " + insertTime / 1_000_000 + "ms");
            System.out.println("Retrieve time: " + retrieveTime / 1_000_000 + "ms");
            System.out.println(stats);
        }
    }
    
    /**
     * hashCode実装の影響をデモンストレーション
     */
    public static void demonstrateHashCodeImpact() {
        System.out.println("\n=== HashCode Implementation Impact Demo ===");
        
        int itemCount = 100;
        
        // 悪いhashCode実装
        System.out.println("\n--- Bad HashCode Implementation ---");
        SimpleHashMap<BadHashCodeExample, String> badMap = new SimpleHashMap<>();
        
        long startTime = System.nanoTime();
        for (int i = 0; i < itemCount; i++) {
            BadHashCodeExample key = new BadHashCodeExample("User" + i, i);
            badMap.put(key, "Value" + i);
        }
        long badInsertTime = System.nanoTime() - startTime;
        
        CollisionStats badStats = badMap.getCollisionStats();
        System.out.println("Insert time: " + badInsertTime / 1_000_000 + "ms");
        System.out.println(badStats);
        if (itemCount <= 20) {
            badMap.printInternalState();
        }
        
        // 良いhashCode実装
        System.out.println("\n--- Good HashCode Implementation ---");
        SimpleHashMap<GoodHashCodeExample, String> goodMap = new SimpleHashMap<>();
        
        startTime = System.nanoTime();
        for (int i = 0; i < itemCount; i++) {
            GoodHashCodeExample key = new GoodHashCodeExample("User" + i, i);
            goodMap.put(key, "Value" + i);
        }
        long goodInsertTime = System.nanoTime() - startTime;
        
        CollisionStats goodStats = goodMap.getCollisionStats();
        System.out.println("Insert time: " + goodInsertTime / 1_000_000 + "ms");
        System.out.println(goodStats);
        if (itemCount <= 20) {
            goodMap.printInternalState();
        }
        
        System.out.println("\nPerformance difference: " + 
                         String.format("%.1fx", (double) badInsertTime / goodInsertTime));
    }
    
    /**
     * リサイズ動作のデモンストレーション
     */
    public static void demonstrateResizing() {
        System.out.println("\n=== Resizing Behavior Demo ===");
        
        SimpleHashMap<Integer, String> map = new SimpleHashMap<>(4, 0.75);
        
        System.out.println("Initial state:");
        map.printInternalState();
        
        // 閾値を超えるまで要素を追加
        for (int i = 0; i < 10; i++) {
            System.out.println("\nAdding item " + i);
            map.put(i, "Value" + i);
            
            if ((i + 1) % 3 == 0 || i >= 6) { // リサイズ前後の状態を表示
                CollisionStats stats = map.getCollisionStats();
                System.out.println("Size: " + map.size() + ", " + stats);
            }
        }
        
        System.out.println("\nFinal state:");
        map.printInternalState();
    }
    
    /**
     * 標準HashMap vs カスタム実装の比較
     */
    public static void compareWithStandardHashMap() {
        System.out.println("\n=== Standard HashMap vs Custom Implementation ===");
        
        int itemCount = 100_000;
        
        // 標準HashMap
        Map<Integer, String> standardMap = new HashMap<>();
        long startTime = System.nanoTime();
        for (int i = 0; i < itemCount; i++) {
            standardMap.put(i, "Value" + i);
        }
        long standardInsertTime = System.nanoTime() - startTime;
        
        startTime = System.nanoTime();
        for (int i = 0; i < itemCount; i++) {
            standardMap.get(i);
        }
        long standardRetrieveTime = System.nanoTime() - startTime;
        
        // カスタム実装
        SimpleHashMap<Integer, String> customMap = new SimpleHashMap<>();
        startTime = System.nanoTime();
        for (int i = 0; i < itemCount; i++) {
            customMap.put(i, "Value" + i);
        }
        long customInsertTime = System.nanoTime() - startTime;
        
        startTime = System.nanoTime();
        for (int i = 0; i < itemCount; i++) {
            customMap.get(i);
        }
        long customRetrieveTime = System.nanoTime() - startTime;
        
        System.out.println("Standard HashMap:");
        System.out.println("  Insert: " + standardInsertTime / 1_000_000 + "ms");
        System.out.println("  Retrieve: " + standardRetrieveTime / 1_000_000 + "ms");
        
        System.out.println("Custom HashMap:");
        System.out.println("  Insert: " + customInsertTime / 1_000_000 + "ms");
        System.out.println("  Retrieve: " + customRetrieveTime / 1_000_000 + "ms");
        
        CollisionStats stats = customMap.getCollisionStats();
        System.out.println("Custom HashMap Stats: " + stats);
        
        System.out.println("Performance ratio (Standard/Custom):");
        System.out.println("  Insert: " + String.format("%.2f", 
                         (double) standardInsertTime / customInsertTime));
        System.out.println("  Retrieve: " + String.format("%.2f", 
                         (double) standardRetrieveTime / customRetrieveTime));
    }
    
    /**
     * ハッシュ分散の可視化
     */
    public static void visualizeHashDistribution() {
        System.out.println("\n=== Hash Distribution Visualization ===");
        
        int bucketCount = 16;
        int itemCount = 64;
        int[] distribution = new int[bucketCount];
        
        // ランダムな文字列キーでの分散
        for (int i = 0; i < itemCount; i++) {
            String key = "Key" + ThreadLocalRandom.current().nextInt(10000);
            int hash = key.hashCode() ^ (key.hashCode() >>> 16);
            int bucket = hash & (bucketCount - 1);
            distribution[bucket]++;
        }
        
        System.out.println("Hash distribution for " + itemCount + " random string keys:");
        for (int i = 0; i < bucketCount; i++) {
            System.out.printf("Bucket %2d: %s (%d items)\n", 
                            i, "█".repeat(distribution[i]), distribution[i]);
        }
        
        // 統計
        double average = (double) itemCount / bucketCount;
        double variance = 0;
        for (int count : distribution) {
            variance += Math.pow(count - average, 2);
        }
        variance /= bucketCount;
        double stdDev = Math.sqrt(variance);
        
        System.out.println("\nDistribution Statistics:");
        System.out.println("Average items per bucket: " + String.format("%.1f", average));
        System.out.println("Standard deviation: " + String.format("%.2f", stdDev));
        System.out.println("Ideal standard deviation: " + String.format("%.2f", Math.sqrt(average)));
    }
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        demonstrateLoadFactorImpact();
        demonstrateHashCodeImpact();
        demonstrateResizing();
        compareWithStandardHashMap();
        visualizeHashDistribution();
        
        System.out.println("\n=== HashMap Internals Summary ===");
        System.out.println("1. Hash function quality directly affects performance");
        System.out.println("2. Load factor balances space vs time efficiency");
        System.out.println("3. Resizing redistributes elements for better performance");
        System.out.println("4. Good hash distribution minimizes collisions");
        System.out.println("5. Java 8+ uses red-black trees for long collision chains");
    }
}