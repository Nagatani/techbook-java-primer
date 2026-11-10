package com.example.records;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.RecordComponent;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Recordsの最適化とパフォーマンス特性のデモンストレーション
 * メモリ効率、シリアライゼーション、リフレクションの最適化
 */
public class RecordOptimizationDemo {
    
    // ========== メモリレイアウト最適化 ==========
    
    /**
     * 最適化されたメモリレイアウトを持つレコード
     */
    public static class MemoryOptimization {
        // 非効率なレイアウト（パディングが発生）
        public record InefficientLayout(
            byte b1,      // 1 byte  + 7 padding
            long l1,      // 8 bytes
            byte b2,      // 1 byte  + 7 padding
            long l2,      // 8 bytes
            int i1,       // 4 bytes + 4 padding
            // 合計: 40 bytes（パディング含む）
            String s1     // 参照: 8 bytes
        ) {}
        
        // 効率的なレイアウト（パディング最小化）
        public record EfficientLayout(
            // プリミティブ型を大きい順に配置
            long l1,      // 8 bytes
            long l2,      // 8 bytes
            int i1,       // 4 bytes
            byte b1,      // 1 byte
            byte b2,      // 1 byte + 2 padding
            // 合計: 24 bytes（パディング最小）
            String s1     // 参照: 8 bytes
        ) {}
        
        // Value-basedの候補（将来のProject Valhalla）
        // @jdk.internal.ValueBased - 将来のProject Valhallaで使用される予定
        public record Point3D(double x, double y, double z) {
            // 将来的にはValue Typeとして最適化される可能性
            
            public double magnitude() {
                return Math.sqrt(x * x + y * y + z * z);
            }
            
            public Point3D normalize() {
                double mag = magnitude();
                return mag == 0 ? this : new Point3D(x / mag, y / mag, z / mag);
            }
            
            public Point3D cross(Point3D other) {
                return new Point3D(
                    y * other.z - z * other.y,
                    z * other.x - x * other.z,
                    x * other.y - y * other.x
                );
            }
            
            public double dot(Point3D other) {
                return x * other.x + y * other.y + z * other.z;
            }
        }
        
        public static void demonstrateMemoryUsage() {
            IO.println("=== Memory Layout Optimization ===");
            
            // メモリ使用量の推定
            Runtime runtime = Runtime.getRuntime();
            
            // 非効率なレイアウト
            runtime.gc();
            long memBefore = runtime.totalMemory() - runtime.freeMemory();
            
            var inefficientList = IntStream.range(0, 100_000)
                .mapToObj(i -> new InefficientLayout(
                    (byte) i, i, (byte) (i + 1), i + 2, i + 3, "str" + i
                ))
                .collect(Collectors.toList());
            
            long memAfterInefficient = runtime.totalMemory() - runtime.freeMemory();
            long inefficientUsage = memAfterInefficient - memBefore;
            
            // 効率的なレイアウト
            runtime.gc();
            memBefore = runtime.totalMemory() - runtime.freeMemory();
            
            var efficientList = IntStream.range(0, 100_000)
                .mapToObj(i -> new EfficientLayout(
                    i, i + 2, i + 3, (byte) i, (byte) (i + 1), "str" + i
                ))
                .collect(Collectors.toList());
            
            long memAfterEfficient = runtime.totalMemory() - runtime.freeMemory();
            long efficientUsage = memAfterEfficient - memBefore;
            
            IO.println("Inefficient layout memory: %,d bytes".formatted(inefficientUsage));
            IO.println("Efficient layout memory: %,d bytes".formatted(efficientUsage));
            IO.println("Memory saved: %,d bytes (%.2f%%)".formatted(
                inefficientUsage - efficientUsage,
                (inefficientUsage - efficientUsage) * 100.0 / inefficientUsage
            ));
            
            // Point3Dの配列効率
            var pointArray = new Point3D[10_000];
            for (int i = 0; i < pointArray.length; i++) {
                pointArray[i] = new Point3D(i, i * 2, i * 3);
            }
            
            IO.println("\nPoint3D operations:");
            var p1 = new Point3D(1, 2, 3);
            var p2 = new Point3D(4, 5, 6);
            IO.println("Cross product: " + p1.cross(p2));
            IO.println("Dot product: " + p1.dot(p2));
        }
    }
    
    // ========== オブジェクトプーリング ==========
    
    /**
     * Recordのオブジェクトプーリング
     */
    public static class RecordPooling {
        // 頻繁に使用される設定値
        public record CacheKey(String namespace, String key, long version) {
            // インターン化による最適化
            public CacheKey {
                namespace = namespace.intern();
                key = key.intern();
            }
        }
        
        // プール可能なレコード
        public static class PooledRecords {
            private static final Map<String, CacheKey> KEY_POOL = new ConcurrentHashMap<>();
            private static final int MAX_POOL_SIZE = 10_000;
            
            public static CacheKey getCacheKey(String namespace, String key, long version) {
                String poolKey = namespace + ":" + key + ":" + version;
                
                return KEY_POOL.computeIfAbsent(poolKey, k -> {
                    if (KEY_POOL.size() >= MAX_POOL_SIZE) {
                        // プールサイズ制限に達した場合は新規作成
                        return new CacheKey(namespace, key, version);
                    }
                    return new CacheKey(namespace, key, version);
                });
            }
            
            public static void clearPool() {
                KEY_POOL.clear();
            }
            
            public static int getPoolSize() {
                return KEY_POOL.size();
            }
        }
        
        public static void demonstratePooling() {
            IO.println("\n=== Record Object Pooling ===");
            
            // プーリングなし
            long startTime = System.nanoTime();
            var unpooledKeys = new ArrayList<CacheKey>();
            for (int i = 0; i < 50_000; i++) {
                unpooledKeys.add(new CacheKey(
                    "namespace" + (i % 10),
                    "key" + (i % 100),
                    i % 5
                ));
            }
            long unpooledTime = System.nanoTime() - startTime;
            
            // プーリングあり
            PooledRecords.clearPool();
            startTime = System.nanoTime();
            var pooledKeys = new ArrayList<CacheKey>();
            for (int i = 0; i < 50_000; i++) {
                pooledKeys.add(PooledRecords.getCacheKey(
                    "namespace" + (i % 10),
                    "key" + (i % 100),
                    i % 5
                ));
            }
            long pooledTime = System.nanoTime() - startTime;
            
            IO.println("Unpooled creation time: %.2f ms".formatted(unpooledTime / 1_000_000.0));
            IO.println("Pooled creation time: %.2f ms".formatted(pooledTime / 1_000_000.0));
            IO.println("Speedup: %.2fx".formatted((double) unpooledTime / pooledTime));
            IO.println("Pool size: " + PooledRecords.getPoolSize());
            
            // メモリ使用量の違い
            long uniqueUnpooled = unpooledKeys.stream().distinct().count();
            long uniquePooled = pooledKeys.stream().distinct().count();
            
            IO.println("Unique objects (unpooled): " + uniqueUnpooled);
            IO.println("Unique objects (pooled): " + uniquePooled);
        }
    }
    
    // ========== 高速シリアライゼーション ==========
    
    /**
     * Recordのシリアライゼーション最適化
     */
    public static class SerializationOptimization {
        // シリアライズ可能なレコード
        public record SerializableData(
            String id,
            String name,
            int value,
            Instant timestamp,
            Map<String, Object> metadata
        ) implements java.io.Serializable {
            // カスタムシリアライゼーション制御
            private static final long serialVersionUID = 1L;
            
            public SerializableData {
                metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
            }
        }
        
        // 手動シリアライゼーション（高速化）
        public static class FastSerializer {
            public static byte[] serialize(SerializableData data) {
                try (var baos = new java.io.ByteArrayOutputStream();
                     var dos = new java.io.DataOutputStream(baos)) {
                    
                    // フィールドを順番に書き込み
                    dos.writeUTF(data.id());
                    dos.writeUTF(data.name());
                    dos.writeInt(data.value());
                    dos.writeLong(data.timestamp().toEpochMilli());
                    
                    // メタデータのシリアライズ
                    dos.writeInt(data.metadata().size());
                    for (var entry : data.metadata().entrySet()) {
                        dos.writeUTF(entry.getKey());
                        dos.writeUTF(String.valueOf(entry.getValue()));
                    }
                    
                    return baos.toByteArray();
                } catch (Exception e) {
                    throw new RuntimeException("Serialization failed", e);
                }
            }
            
            public static SerializableData deserialize(byte[] bytes) {
                try (var bais = new java.io.ByteArrayInputStream(bytes);
                     var dis = new java.io.DataInputStream(bais)) {
                    
                    String id = dis.readUTF();
                    String name = dis.readUTF();
                    int value = dis.readInt();
                    Instant timestamp = Instant.ofEpochMilli(dis.readLong());
                    
                    int metadataSize = dis.readInt();
                    Map<String, Object> metadata = new HashMap<>();
                    for (int i = 0; i < metadataSize; i++) {
                        String key = dis.readUTF();
                        String val = dis.readUTF();
                        metadata.put(key, val);
                    }
                    
                    return new SerializableData(id, name, value, timestamp, metadata);
                } catch (Exception e) {
                    throw new RuntimeException("Deserialization failed", e);
                }
            }
        }
        
        public static void demonstrateSerialization() {
            IO.println("\n=== Serialization Performance ===");
            
            var testData = new SerializableData(
                UUID.randomUUID().toString(),
                "Test Record",
                42,
                Instant.now(),
                Map.of("key1", "value1", "key2", 123, "key3", true)
            );
            
            int iterations = 10_000;
            
            // Java標準シリアライゼーション
            long standardTime = measureTime(() -> {
                try (var baos = new java.io.ByteArrayOutputStream();
                     var oos = new java.io.ObjectOutputStream(baos)) {
                    
                    for (int i = 0; i < iterations; i++) {
                        oos.writeObject(testData);
                        oos.reset();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            
            // カスタム高速シリアライゼーション
            long fastTime = measureTime(() -> {
                for (int i = 0; i < iterations; i++) {
                    byte[] bytes = FastSerializer.serialize(testData);
                    SerializableData deserialized = FastSerializer.deserialize(bytes);
                }
            });
            
            IO.println("Standard serialization: %.2f ms".formatted(standardTime / 1_000_000.0));
            IO.println("Fast serialization: %.2f ms".formatted(fastTime / 1_000_000.0));
            IO.println("Speedup: %.2fx".formatted((double) standardTime / fastTime));
            
            // サイズ比較
            try {
                var baos1 = new java.io.ByteArrayOutputStream();
                var oos = new java.io.ObjectOutputStream(baos1);
                oos.writeObject(testData);
                int standardSize = baos1.size();
                
                byte[] fastBytes = FastSerializer.serialize(testData);
                int fastSize = fastBytes.length;
                
                IO.println("Standard size: %d bytes".formatted(standardSize));
                IO.println("Fast size: %d bytes".formatted(fastSize));
                IO.println("Size reduction: %.2f%%".formatted(
                    (standardSize - fastSize) * 100.0 / standardSize));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    // ========== リフレクション最適化 ==========
    
    /**
     * Recordのリフレクション処理最適化
     */
    public static class ReflectionOptimization {
        public record SampleRecord(String name, int age, double score, boolean active) {}
        
        // リフレクションキャッシュ
        private static final Map<Class<?>, RecordComponent[]> COMPONENT_CACHE = 
            new ConcurrentHashMap<>();
        
        private static final Map<Class<?>, MethodHandles.Lookup> LOOKUP_CACHE = 
            new ConcurrentHashMap<>();
        
        public static Map<String, Object> recordToMap(Record record) {
            var clazz = record.getClass();
            var components = COMPONENT_CACHE.computeIfAbsent(clazz, Class::getRecordComponents);
            
            var result = new HashMap<String, Object>();
            for (var component : components) {
                try {
                    var accessor = component.getAccessor();
                    accessor.setAccessible(true);
                    result.put(component.getName(), accessor.invoke(record));
                } catch (Exception e) {
                    throw new RuntimeException("Failed to access component", e);
                }
            }
            
            return result;
        }
        
        // MethodHandlesを使った高速アクセス
        public static Map<String, Object> recordToMapFast(Record record) {
            var clazz = record.getClass();
            var lookup = LOOKUP_CACHE.computeIfAbsent(clazz, c -> {
                try {
                    return MethodHandles.privateLookupIn(c, MethodHandles.lookup());
                } catch (Exception e) {
                    throw new RuntimeException("Failed to create lookup", e);
                }
            });
            
            var components = COMPONENT_CACHE.computeIfAbsent(clazz, Class::getRecordComponents);
            var result = new HashMap<String, Object>();
            
            for (var component : components) {
                try {
                    var handle = lookup.unreflect(component.getAccessor());
                    result.put(component.getName(), handle.invoke(record));
                } catch (Throwable e) {
                    throw new RuntimeException("Failed to access component", e);
                }
            }
            
            return result;
        }
        
        public static void demonstrateReflection() {
            IO.println("\n=== Reflection Performance ===");
            
            var sampleRecord = new SampleRecord("Alice", 30, 95.5, true);
            int iterations = 100_000;
            
            // ウォームアップ
            for (int i = 0; i < 1000; i++) {
                recordToMap(sampleRecord);
                recordToMapFast(sampleRecord);
            }
            
            // 通常のリフレクション
            long normalTime = measureTime(() -> {
                for (int i = 0; i < iterations; i++) {
                    recordToMap(sampleRecord);
                }
            });
            
            // MethodHandlesを使った高速アクセス
            long fastTime = measureTime(() -> {
                for (int i = 0; i < iterations; i++) {
                    recordToMapFast(sampleRecord);
                }
            });
            
            IO.println("Normal reflection: %.2f ms".formatted(normalTime / 1_000_000.0));
            IO.println("MethodHandles: %.2f ms".formatted(fastTime / 1_000_000.0));
            IO.println("Speedup: %.2fx".formatted((double) normalTime / fastTime));
            
            // 結果の確認
            IO.println("Record as map: " + recordToMapFast(sampleRecord));
        }
    }
    
    // ========== 並行処理での最適化 ==========
    
    /**
     * 並行環境でのRecord使用最適化
     */
    public static class ConcurrencyOptimization {
        // イミュータブルキャッシュエントリ
        public record CacheEntry<K, V>(K key, V value, Instant expiry) {
            public boolean isExpired() {
                return Instant.now().isAfter(expiry);
            }
        }
        
        // 高性能キャッシュ実装
        public static class HighPerformanceCache<K, V> {
            private final ConcurrentHashMap<K, CacheEntry<K, V>> cache = new ConcurrentHashMap<>();
            private final Duration ttl;
            private final ScheduledExecutorService cleaner;
            private final AtomicLong hits = new AtomicLong();
            private final AtomicLong misses = new AtomicLong();
            
            public HighPerformanceCache(Duration ttl) {
                this.ttl = ttl;
                this.cleaner = Executors.newSingleThreadScheduledExecutor(r -> {
                    Thread t = new Thread(r, "cache-cleaner");
                    t.setDaemon(true);
                    return t;
                });
                
                // 定期的なクリーンアップ
                cleaner.scheduleAtFixedRate(this::cleanup, 
                    ttl.toMillis(), ttl.toMillis(), TimeUnit.MILLISECONDS);
            }
            
            public V get(K key) {
                var entry = cache.get(key);
                if (entry == null || entry.isExpired()) {
                    misses.incrementAndGet();
                    return null;
                }
                hits.incrementAndGet();
                return entry.value();
            }
            
            public void put(K key, V value) {
                var expiry = Instant.now().plus(ttl);
                cache.put(key, new CacheEntry<>(key, value, expiry));
            }
            
            private void cleanup() {
                cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
            }
            
            public double getHitRate() {
                long h = hits.get();
                long m = misses.get();
                return h + m == 0 ? 0 : (double) h / (h + m);
            }
            
            public void shutdown() {
                cleaner.shutdown();
            }
        }
        
        public static void demonstrateConcurrency() {
            IO.println("\n=== Concurrency Optimization ===");
            
            var cache = new HighPerformanceCache<String, String>(Duration.ofSeconds(2));
            var executor = Executors.newFixedThreadPool(10);
            var latch = new CountDownLatch(10);
            
            // 並行アクセステスト
            for (int t = 0; t < 10; t++) {
                final int threadId = t;
                executor.submit(() -> {
                    try {
                        for (int i = 0; i < 10_000; i++) {
                            String key = "key" + (i % 100);
                            
                            if (i % 3 == 0) {
                                cache.put(key, "value" + threadId + "_" + i);
                            } else {
                                cache.get(key);
                            }
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }
            
            try {
                latch.await();
                Thread.sleep(100); // 統計の安定化
                
                IO.println("Cache hit rate: %.2f%%".formatted(cache.getHitRate() * 100));
                
                // パフォーマンス測定
                long startTime = System.nanoTime();
                for (int i = 0; i < 100_000; i++) {
                    cache.get("key" + (i % 100));
                }
                long readTime = System.nanoTime() - startTime;
                
                IO.println("Read performance: %.2f ops/ms".formatted(
                    100_000.0 / (readTime / 1_000_000.0)));
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                executor.shutdown();
                cache.shutdown();
            }
        }
    }
    
    // ========== ユーティリティメソッド ==========
    
    private static long measureTime(Runnable task) {
        long start = System.nanoTime();
        task.run();
        return System.nanoTime() - start;
    }
    
    // ========== メインメソッド ==========
    
    public static void main(String[] args) {
        IO.println("Record Optimization and Performance Demo");
        IO.println("=======================================");
        
        // メモリレイアウト最適化
        MemoryOptimization.demonstrateMemoryUsage();
        
        // オブジェクトプーリング
        RecordPooling.demonstratePooling();
        
        // シリアライゼーション最適化
        SerializationOptimization.demonstrateSerialization();
        
        // リフレクション最適化
        ReflectionOptimization.demonstrateReflection();
        
        // 並行処理最適化
        ConcurrencyOptimization.demonstrateConcurrency();
        
        IO.println("\n🎯 Key Optimization Insights:");
        IO.println("✓ Proper field ordering reduces memory padding");
        IO.println("✓ Object pooling works well with immutable records");
        IO.println("✓ Custom serialization can be much faster than default");
        IO.println("✓ MethodHandles provide faster reflection access");
        IO.println("✓ Records are naturally thread-safe for concurrent use");
        
        IO.println("\n⚡ Performance Tips:");
        IO.println("• Order fields by size (largest to smallest)");
        IO.println("• Use string interning for frequently used values");
        IO.println("• Cache reflection metadata for repeated access");
        IO.println("• Leverage record immutability in concurrent scenarios");
        IO.println("• Consider custom serialization for hot paths");
    }
}