package com.example.collections;

import java.util.*;

/**
 * HashMap内部実装の詳細デモンストレーション
 * ハッシュテーブル、衝突処理、負荷係数の影響を学習
 *
 * 本物のjava.util.HashMap（Java 8以降）は、1つのバケットのチェーンが
 * 8（TREEIFY_THRESHOLD）を超えると連結リストを赤黒木に変える。
 * ただし表の大きさが64（MIN_TREEIFY_CAPACITY）未満のときは、木にせず表を広げる。
 * 削除やリサイズで6（UNTREEIFY_THRESHOLD）以下になると連結リストに戻す。
 * ここのSimpleHashMapは仕組みを見るための簡略版なので、木への変換はしない。
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
        private boolean verbose;

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

        public SimpleHashMap() {
            this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
        }

        @SuppressWarnings("unchecked")
        public SimpleHashMap(int initialCapacity, double loadFactor) {
            int capacity = tableSizeFor(initialCapacity);
            this.loadFactor = loadFactor;
            this.table = (Node<K, V>[]) new Node[capacity];
            this.threshold = (int) (capacity * loadFactor);
        }

        /**
         * 指定された値以上で最小の2のべき乗を返す（HashMapと同じ考え方）
         * indexForのビット演算は、表の長さが2のべき乗であることを前提にしている
         */
        private static int tableSizeFor(int capacity) {
            int n = 1;
            while (n < capacity) {
                n <<= 1;
            }
            return n;
        }

        /**
         * リサイズのたびにメッセージを出すかどうか
         */
        public void setVerbose(boolean verbose) {
            this.verbose = verbose;
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

            // HashMapと同じく、要素数が閾値を超えたらリサイズする
            if (size > threshold) {
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

            table = (Node<K, V>[]) new Node[newCapacity];
            threshold = (int) (newCapacity * loadFactor);

            // 既存エントリの再配置（ハッシュ値は保存してあるので計算し直さない）
            for (Node<K, V> head : oldTable) {
                Node<K, V> node = head;
                while (node != null) {
                    Node<K, V> next = node.next;
                    int index = indexFor(node.hash, newCapacity);
                    node.next = table[index];
                    table[index] = node;
                    node = next;
                }
            }

            if (verbose) {
                IO.println("Resized from " + oldCapacity + " to " + newCapacity);
            }
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
            IO.println("=== HashMap Internal State ===");
            IO.println("Size: " + size);
            IO.println("Capacity: " + table.length);
            IO.println("Load Factor: " + loadFactor);
            IO.println("Threshold: " + threshold);
            IO.println("Current Load: " + String.format("%.2f", (double) size / table.length));

            for (int i = 0; i < table.length; i++) {
                if (table[i] != null) {
                    int chainLength = getChainLength(table[i]);
                    IO.println("Bucket " + i + ": " + table[i] + " (chain length: " + chainLength + ")");
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
            if (!(obj instanceof BadHashCodeExample other)) return false;
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
            if (!(obj instanceof GoodHashCodeExample other)) return false;
            return Objects.equals(name, other.name) && id == other.id;
        }

        @Override
        public String toString() {
            return name + "(" + id + ")";
        }
    }

    /**
     * 処理を数回空回ししてから（ウォームアップ）、最後の1回の時間をナノ秒で返す
     * JITコンパイルの影響を減らすための簡易的な方法で、正確な測定にはJMHを使う
     */
    private static long measureNanos(Runnable task) {
        for (int i = 0; i < 5; i++) {
            task.run();
        }
        long start = System.nanoTime();
        task.run();
        return System.nanoTime() - start;
    }

    private static String millis(long nanos) {
        return String.format("%.2fms", nanos / 1_000_000.0);
    }

    /**
     * 負荷係数の影響をデモンストレーション
     * 負荷係数が大きいほど表が小さく保たれ、チェーンが長くなる
     */
    public static void demonstrateLoadFactorImpact() {
        IO.println("=== Load Factor Impact Demo ===");

        double[] loadFactors = {0.5, 0.75, 1.0, 1.5};
        int itemCount = 10_000;

        String[] keys = new String[itemCount];
        for (int i = 0; i < itemCount; i++) {
            keys[i] = "key" + i;
        }

        for (double loadFactor : loadFactors) {
            IO.println("\n--- Load Factor: " + loadFactor + " ---");

            SimpleHashMap<String, Integer> map = new SimpleHashMap<>(16, loadFactor);
            for (int i = 0; i < itemCount; i++) {
                map.put(keys[i], i);
            }

            long retrieveTime = measureNanos(() -> {
                for (String key : keys) {
                    map.get(key);
                }
            });

            IO.println("Retrieve time: " + millis(retrieveTime));
            IO.println(map.getCollisionStats());
        }
    }

    /**
     * hashCode実装の影響をデモンストレーション
     */
    public static void demonstrateHashCodeImpact() {
        IO.println("\n=== HashCode Implementation Impact Demo ===");

        int itemCount = 5_000;

        // 悪いhashCode実装：すべてのキーが1つのバケットに集まり、putのたびにチェーンをたどる
        IO.println("\n--- Bad HashCode Implementation ---");
        SimpleHashMap<BadHashCodeExample, String> badMap = new SimpleHashMap<>();

        long startTime = System.nanoTime();
        for (int i = 0; i < itemCount; i++) {
            BadHashCodeExample key = new BadHashCodeExample("User" + i, i);
            badMap.put(key, "Value" + i);
        }
        long badInsertTime = System.nanoTime() - startTime;

        IO.println("Insert time: " + millis(badInsertTime));
        IO.println(badMap.getCollisionStats());

        // 良いhashCode実装
        IO.println("\n--- Good HashCode Implementation ---");
        SimpleHashMap<GoodHashCodeExample, String> goodMap = new SimpleHashMap<>();

        startTime = System.nanoTime();
        for (int i = 0; i < itemCount; i++) {
            GoodHashCodeExample key = new GoodHashCodeExample("User" + i, i);
            goodMap.put(key, "Value" + i);
        }
        long goodInsertTime = System.nanoTime() - startTime;

        IO.println("Insert time: " + millis(goodInsertTime));
        IO.println(goodMap.getCollisionStats());

        IO.println("\nPerformance difference: " +
                         String.format("%.1fx", (double) badInsertTime / goodInsertTime));
        IO.println("Note: java.util.HashMap turns long chains into red-black trees,");
        IO.println("      but that keeps lookups at O(log n) only when the keys are Comparable.");
    }

    /**
     * リサイズ動作のデモンストレーション
     */
    public static void demonstrateResizing() {
        IO.println("\n=== Resizing Behavior Demo ===");

        SimpleHashMap<Integer, String> map = new SimpleHashMap<>(4, 0.75);
        map.setVerbose(true);

        IO.println("Initial state:");
        map.printInternalState();

        // 閾値（容量×負荷係数）を超えるとリサイズされる
        for (int i = 0; i < 10; i++) {
            IO.println("\nAdding item " + i);
            map.put(i, "Value" + i);
            IO.println("Size: " + map.size() + ", " + map.getCollisionStats());
        }

        IO.println("\nFinal state:");
        map.printInternalState();
    }

    /**
     * 標準HashMap vs カスタム実装の比較
     */
    public static void compareWithStandardHashMap() {
        IO.println("\n=== Standard HashMap vs Custom Implementation ===");

        int itemCount = 100_000;

        long standardInsertTime = measureNanos(() -> {
            Map<Integer, String> standardMap = new HashMap<>();
            for (int i = 0; i < itemCount; i++) {
                standardMap.put(i, "Value" + i);
            }
        });

        long customInsertTime = measureNanos(() -> {
            SimpleHashMap<Integer, String> customMap = new SimpleHashMap<>();
            for (int i = 0; i < itemCount; i++) {
                customMap.put(i, "Value" + i);
            }
        });

        Map<Integer, String> standardMap = new HashMap<>();
        SimpleHashMap<Integer, String> customMap = new SimpleHashMap<>();
        for (int i = 0; i < itemCount; i++) {
            standardMap.put(i, "Value" + i);
            customMap.put(i, "Value" + i);
        }

        long standardRetrieveTime = measureNanos(() -> {
            for (int i = 0; i < itemCount; i++) {
                standardMap.get(i);
            }
        });

        long customRetrieveTime = measureNanos(() -> {
            for (int i = 0; i < itemCount; i++) {
                customMap.get(i);
            }
        });

        IO.println("Standard HashMap:");
        IO.println("  Insert: " + millis(standardInsertTime));
        IO.println("  Retrieve: " + millis(standardRetrieveTime));

        IO.println("Custom HashMap:");
        IO.println("  Insert: " + millis(customInsertTime));
        IO.println("  Retrieve: " + millis(customRetrieveTime));

        IO.println("Custom HashMap Stats: " + customMap.getCollisionStats());
    }

    /**
     * ハッシュ分散の可視化
     */
    public static void visualizeHashDistribution() {
        IO.println("\n=== Hash Distribution Visualization ===");

        int bucketCount = 16;
        int itemCount = 64;
        int[] distribution = new int[bucketCount];
        Random random = new Random(42); // 毎回同じ結果になるよう種を固定する

        // ランダムな文字列キーでの分散
        for (int i = 0; i < itemCount; i++) {
            String key = "Key" + random.nextInt(10000);
            int hash = key.hashCode() ^ (key.hashCode() >>> 16);
            int bucket = hash & (bucketCount - 1);
            distribution[bucket]++;
        }

        IO.println("Hash distribution for " + itemCount + " random string keys:");
        for (int i = 0; i < bucketCount; i++) {
            IO.println("Bucket %2d: %s (%d items)".formatted(
                            i, "█".repeat(distribution[i]), distribution[i]));
        }

        // 統計
        double average = (double) itemCount / bucketCount;
        double variance = 0;
        for (int count : distribution) {
            variance += Math.pow(count - average, 2);
        }
        variance /= bucketCount;
        double stdDev = Math.sqrt(variance);

        IO.println("\nDistribution Statistics:");
        IO.println("Average items per bucket: " + String.format("%.1f", average));
        IO.println("Standard deviation: " + String.format("%.2f", stdDev));
        IO.println("Ideal standard deviation: " + String.format("%.2f", Math.sqrt(average)));
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

        IO.println("\n=== HashMap Internals Summary ===");
        IO.println("1. Hash function quality directly affects performance");
        IO.println("2. Load factor balances space vs time efficiency");
        IO.println("3. Resizing redistributes elements for better performance");
        IO.println("4. Good hash distribution minimizes collisions");
        IO.println("5. Java 8+ turns a chain longer than 8 into a red-black tree (table size 64 or more)");
        IO.println("Timings depend on the environment; use JMH for accurate measurements.");
    }
}
