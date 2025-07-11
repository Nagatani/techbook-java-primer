# 第21章 チャレンジ課題: 実践的なテスト戦略

## 課題: マルチスレッド対応キャッシュシステムのテスト

### 背景
実際の開発現場で遭遇する複雑なテストシナリオに挑戦します。マルチスレッド環境で動作するキャッシュシステムを実装し、包括的なテストスイートを作成します。

### 要件

#### 実装すべきキャッシュシステム
```java
public interface Cache<K, V> {
    void put(K key, V value);
    Optional<V> get(K key);
    void remove(K key);
    void clear();
    int size();
    void evictExpired();
}

public class TimedCache<K, V> implements Cache<K, V> {
    // 以下の機能を実装してください：
    // 1. TTL（Time To Live）機能: エントリーは指定時間後に自動的に期限切れ
    // 2. 最大サイズ制限: LRU（Least Recently Used）で古いエントリーを削除
    // 3. スレッドセーフ: 複数スレッドから同時アクセス可能
    // 4. 統計情報: ヒット率、ミス率などの統計を記録
}
```

### テスト要件

#### 1. 基本機能テスト
- CRUD操作の正確性
- TTL機能の動作確認
- LRU evictionの動作確認

#### 2. 並行性テスト
- 複数スレッドからの同時読み書き
- デッドロックが発生しないことの確認
- データの整合性保証

#### 3. パフォーマンステスト
- 大量データでの動作確認
- レスポンスタイムの測定
- メモリ使用量の確認

#### 4. 障害シナリオテスト
- OutOfMemoryErrorへの対処
- 同時大量アクセス時の挙動
- システムリソース不足時の graceful degradation

### 実装のヒント

```java
// 並行性テストの例
@Test
void testConcurrentAccess() throws InterruptedException {
    TimedCache<String, String> cache = new TimedCache<>(1000, 60_000); // size: 1000, TTL: 60秒
    int threadCount = 10;
    int operationsPerThread = 1000;
    CountDownLatch latch = new CountDownLatch(threadCount);
    AtomicInteger errors = new AtomicInteger(0);
    
    for (int i = 0; i < threadCount; i++) {
        int threadId = i;
        new Thread(() -> {
            try {
                for (int j = 0; j < operationsPerThread; j++) {
                    // ランダムな操作を実行
                    // put, get, remove をランダムに選択
                }
            } catch (Exception e) {
                errors.incrementAndGet();
            } finally {
                latch.countDown();
            }
        }).start();
    }
    
    latch.await();
    assertEquals(0, errors.get());
    // 追加の検証...
}
```

### 高度な要件

#### 1. カスタムJUnit拡張の作成
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface StressTest {
    int threads() default 10;
    int iterations() default 1000;
    long duration() default 5000; // ミリ秒
}

// カスタム拡張を作成して、ストレステストを簡単に記述できるようにする
```

#### 2. テストレポートの生成
- キャッシュの統計情報をHTMLレポートとして出力
- パフォーマンスメトリクスのグラフ化
- カバレッジレポートの統合

#### 3. プロパティベーステスト
```java
@Property
void cacheInvariantsHold(@ForAll String key, @ForAll String value) {
    // キャッシュの不変条件が常に成立することを検証
    // 例: size() は常に 0 以上、maxSize 以下
}
```

### 提出物

1. **実装コード**
   - `TimedCache`クラスの完全な実装
   - 必要な補助クラス（統計情報、エビクション戦略など）

2. **テストスイート**
   - 単体テスト（最低20個）
   - 統合テスト（最低5個）
   - パフォーマンステスト（最低3個）

3. **ドキュメント**
   - 設計判断の説明（500字）
   - パフォーマンス測定結果
   - 発見した問題と解決方法

4. **CI/CD設定**（オプション）
   - GitHub Actionsまたは類似のCIツールの設定
   - 自動テスト実行とレポート生成

### 評価基準

- **正確性** (30%): すべての機能が仕様通りに動作
- **テストの品質** (30%): 網羅的で保守性の高いテスト
- **パフォーマンス** (20%): 効率的な実装とスケーラビリティ
- **コード品質** (20%): 可読性、保守性、ドキュメント

### ボーナスポイント

- ミューテーションテスト（PITest）の導入と100%キル達成
- JMHを使用したマイクロベンチマーク
- 継続的なパフォーマンス監視の仕組み

## 参考資料
- Java Concurrency in Practice
- JUnit 5 User Guide
- Mockito Documentation
- JMH (Java Microbenchmark Harness)