package com.example.records;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
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
     * メモリレイアウトについて
     * HotSpot JVMはフィールドを自動で並べ替えて配置するので、
     * レコードのコンポーネントを宣言する順序はオブジェクトの大きさに影響しない。
     * また、既定で有効なcompressed oopsでは、参照（Stringなど）は4バイトで表される
     */
    public static class MemoryOptimization {
        
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
            IO.println("=== Memory Layout ===");
            IO.println("HotSpot reorders fields automatically, so the declaration order of");
            IO.println("record components does not change the object size.");
            IO.println("With compressed oops (the default), a reference takes 4 bytes.");
            
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
                
                CacheKey pooled = KEY_POOL.get(poolKey);
                if (pooled != null) {
                    return pooled;
                }
                if (KEY_POOL.size() >= MAX_POOL_SIZE) {
                    // プールサイズの上限に達した場合は、プールに入れずに新しく作る
                    return new CacheKey(namespace, key, version);
                }
                return KEY_POOL.computeIfAbsent(poolKey, k -> new CacheKey(namespace, key, version));
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
            
            // 作られたオブジェクトの数の違い
            // distinct()はequalsで比べるので、内容の同じレコードを1つと数えてしまう。
            // オブジェクトそのものの数は、同一性（==）で比べるIdentityHashMapで数える
            IO.println("Equal values (both): " + unpooledKeys.stream().distinct().count());
            IO.println("Distinct objects (unpooled): " + countIdentities(unpooledKeys));
            IO.println("Distinct objects (pooled): " + countIdentities(pooledKeys));
        }
        
        private static int countIdentities(List<?> objects) {
            Set<Object> identities = Collections.newSetFromMap(new IdentityHashMap<>());
            identities.addAll(objects);
            return identities.size();
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
            // レコードはコンポーネントの値で直列化され、serialVersionUIDの一致は検査されない
            // （宣言しなければ0Lになる）。そのため互換性のためにserialVersionUIDを宣言する必要はない
            
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
                    // Instantは秒とナノ秒に分けて書く（ミリ秒にすると精度が落ちる）
                    dos.writeLong(data.timestamp().getEpochSecond());
                    dos.writeInt(data.timestamp().getNano());
                    
                    // メタデータのシリアライズ（値の型を1文字で記録して、元の型に戻せるようにする）
                    dos.writeInt(data.metadata().size());
                    for (var entry : data.metadata().entrySet()) {
                        dos.writeUTF(entry.getKey());
                        switch (entry.getValue()) {
                            case String str -> { dos.writeByte('S'); dos.writeUTF(str); }
                            case Integer i -> { dos.writeByte('I'); dos.writeInt(i); }
                            case Boolean b -> { dos.writeByte('B'); dos.writeBoolean(b); }
                            default -> throw new IllegalArgumentException(
                                "Unsupported metadata type: " + entry.getValue().getClass());
                        }
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
                    Instant timestamp = Instant.ofEpochSecond(dis.readLong(), dis.readInt());
                    
                    int metadataSize = dis.readInt();
                    Map<String, Object> metadata = new HashMap<>();
                    for (int i = 0; i < metadataSize; i++) {
                        String key = dis.readUTF();
                        Object val = switch (dis.readByte()) {
                            case 'S' -> dis.readUTF();
                            case 'I' -> dis.readInt();
                            case 'B' -> dis.readBoolean();
                            default -> throw new java.io.IOException("Unknown type tag");
                        };
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
            
            // Java標準シリアライゼーション（書き込みと読み込みの往復）
            long standardTime = measureTime(() -> {
                for (int i = 0; i < iterations; i++) {
                    standardRoundTrip(testData);
                }
            });
            
            // カスタム高速シリアライゼーション
            long fastTime = measureTime(() -> {
                for (int i = 0; i < iterations; i++) {
                    byte[] bytes = FastSerializer.serialize(testData);
                    SerializableData deserialized = FastSerializer.deserialize(bytes);
                }
            });
            
            IO.println("Standard serialization (round trip): %.2f ms".formatted(standardTime / 1_000_000.0));
            IO.println("Fast serialization (round trip): %.2f ms".formatted(fastTime / 1_000_000.0));
            IO.println("Speedup: %.2fx".formatted((double) standardTime / fastTime));
            
            // 元に戻した値が一致するか（レコードのequalsで比べる）
            IO.println("Standard round trip equals original: " + standardRoundTrip(testData).equals(testData));
            IO.println("Fast round trip equals original: "
                + FastSerializer.deserialize(FastSerializer.serialize(testData)).equals(testData));
            
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
        
        private static SerializableData standardRoundTrip(SerializableData data) {
            try {
                var baos = new java.io.ByteArrayOutputStream();
                try (var oos = new java.io.ObjectOutputStream(baos)) {
                    oos.writeObject(data);
                }
                try (var ois = new java.io.ObjectInputStream(
                        new java.io.ByteArrayInputStream(baos.toByteArray()))) {
                    return (SerializableData) ois.readObject();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
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
        
        // クラスごとに、各コンポーネントのアクセサーのMethodHandleを1回だけ作って保存する
        private static final Map<Class<?>, List<Map.Entry<String, MethodHandle>>> HANDLE_CACHE = 
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
        
        // MethodHandlesを使ったアクセス
        // unreflectは重い処理なので、呼び出すたびに行うと通常のリフレクションより遅くなる。
        // MethodHandleをキャッシュしておき、2回目以降は呼び出すだけにする
        public static Map<String, Object> recordToMapFast(Record record) {
            var handles = HANDLE_CACHE.computeIfAbsent(record.getClass(), ReflectionOptimization::createHandles);
            var result = new HashMap<String, Object>();
            
            for (var entry : handles) {
                try {
                    result.put(entry.getKey(), entry.getValue().invoke(record));
                } catch (Throwable e) {
                    throw new RuntimeException("Failed to access component", e);
                }
            }
            
            return result;
        }
        
        private static List<Map.Entry<String, MethodHandle>> createHandles(Class<?> clazz) {
            var lookup = MethodHandles.lookup();
            var handles = new ArrayList<Map.Entry<String, MethodHandle>>();
            for (var component : clazz.getRecordComponents()) {
                try {
                    // 引数と戻り値をObjectにそろえておくと、invokeExactに近い速さで呼び出せる
                    MethodHandle handle = lookup.unreflect(component.getAccessor())
                        .asType(MethodType.methodType(Object.class, Object.class));
                    handles.add(Map.entry(component.getName(), handle));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to create handle", e);
                }
            }
            return handles;
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
            IO.println("Record as map: " + new TreeMap<>(recordToMapFast(sampleRecord))); // キーの順に表示
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
        
        IO.println("\nKey Optimization Insights:");
        IO.println("✓ Field order does not affect object size on HotSpot");
        IO.println("✓ Pooling immutable records reduces the number of objects (not always faster)");
        IO.println("✓ Custom serialization must preserve types and precision");
        IO.println("✓ Cache MethodHandles; creating them on every call is slow");
        IO.println("✓ Record fields are final, which helps sharing data between threads");
        IO.println("Timings depend on the environment; use JMH for accurate measurements.");
        
        IO.println("\nPerformance Tips:");
        IO.println("• Use string interning for frequently used values");
        IO.println("• Cache reflection metadata for repeated access");
        IO.println("• Leverage record immutability in concurrent scenarios");
        IO.println("• Consider custom serialization for hot paths");
    }
}