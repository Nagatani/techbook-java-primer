# 第6章 応用課題

## 🎯 学習目標
- finalキーワードの戦略的活用
- 不変オブジェクトの設計と実装
- スレッドセーフなクラス設計
- 定数クラスとユーティリティクラスの作成
- パフォーマンスとメモリ効率の最適化

## 📝 課題一覧

### 課題1: 不変設定管理クラス
**ファイル名**: `ImmutableConfiguration.java`

アプリケーションの設定を安全に管理する不変クラスを作成してください。

**要求仕様**:
- 設定値の変更不可能性
- ビルダーパターンによる柔軟な構築
- デフォルト値の適切な管理
- 設定の検証とバリデーション

**不変性要件**:
- 一度作成された設定は変更不可
- 内部コレクションの外部変更防止
- スレッドセーフな設計
- メモリ効率の最適化

**実行例**:
```
=== 不変設定管理システム ===

🔧 AppConfig Builder v1.0

=== 設定構築 ===
設定ビルダー開始...

✓ データベース設定:
  URL: jdbc:mysql://localhost:3306/app
  ユーザー: admin
  接続プール: 10

✓ サーバー設定:
  ポート: 8080
  ホスト: localhost  
  SSL有効: true

✓ キャッシュ設定:
  タイプ: Redis
  TTL: 3600秒
  最大サイズ: 1000MB

=== 不変オブジェクト生成 ===
✅ 設定オブジェクト作成完了
ハッシュコード: 1547832946

=== 設定値参照 ===
📋 現在の設定:
データベースURL: jdbc:mysql://localhost:3306/app
サーバーポート: 8080
キャッシュTTL: 3600秒
SSL設定: 有効

=== 不変性テスト ===
❌ 設定変更試行: 
「ポート番号を9090に変更」
→ エラー: 設定は変更できません（不変オブジェクト）

❌ 内部リスト変更試行:
「許可IPリストに新しいIPを追加」  
→ エラー: 防御的コピーにより変更不可

✅ 新しい設定作成:
「既存設定をベースに新しい設定を構築」
→ 成功: 新しい不変オブジェクト生成

=== スレッドセーフ検証 ===
🧵 マルチスレッドテスト実行中...

スレッド1: 設定読み取り - 成功
スレッド2: 設定読み取り - 成功  
スレッド3: 設定読み取り - 成功
スレッド4: 設定読み取り - 成功

📊 結果: データ競合なし、完全スレッドセーフ

=== メモリ効率検証 ===
💾 メモリ使用量:
設定オブジェクト: 2.1KB
文字列プール最適化: 有効
重複データ削減: 85%
```

**評価ポイント**:
- 完全な不変性の実現
- ビルダーパターンの実装
- スレッドセーフティの確保
- メモリ効率の最適化



### 課題2: 金融取引記録システム
**ファイル名**: `FinancialTransaction.java`

金融取引の完全性を保証する不変取引記録システムを作成してください。

**要求仕様**:
- 取引データの改ざん防止
- 取引履歴の不変性
- 監査証跡の完全性
- 暗号学的ハッシュによる検証

**セキュリティ要件**:
- 取引の後方変更不可
- チェーン構造による整合性
- デジタル署名の実装
- 不正検出機能

**実行例**:
```
=== 金融取引記録システム ===

🏦 SecureTransaction Ledger v3.0

=== 取引開始 ===
💳 取引ID生成: TXN-20240705-001
🔐 ハッシュ値: a7f3c891b2d4e560f9c8...

✓ 送金取引記録:
  送金者: ACC-001 (田中太郎)
  受取者: ACC-002 (佐藤花子)
  金額: ¥50,000
  手数料: ¥200
  タイムスタンプ: 2024-07-05T10:30:15Z

=== 不変性確認 ===
🔒 取引確定後の変更試行:

❌ 金額変更試行: ¥50,000 → ¥45,000
→ エラー: final フィールドは変更不可

❌ 受取者変更試行: 佐藤花子 → 山田次郎  
→ エラー: 不変オブジェクトは変更不可

❌ タイムスタンプ変更試行:
→ エラー: 時刻の改ざんは検出されました

✅ 取引の完全性: 保証されています

=== ブロックチェーン風検証 ===
📦 ブロック1 (Genesis):
  前ブロックハッシュ: 0000000000000000
  取引数: 0
  ハッシュ: b4c7e9d1a8f2...

📦 ブロック2:
  前ブロックハッシュ: b4c7e9d1a8f2...
  取引数: 1 (TXN-20240705-001)
  ハッシュ: f8a3c2e7b9d4...

📦 ブロック3:
  前ブロックハッシュ: f8a3c2e7b9d4...
  取引数: 2 (TXN-20240705-002, TXN-20240705-003)
  ハッシュ: d7e9f4c8a1b5...

=== 監査ログ ===
🔍 監査証跡検証:
✅ ハッシュチェーン: 完全
✅ 時系列整合性: 正常
✅ デジタル署名: 有効
✅ 改ざん検出: 異常なし

📊 統計情報:
- 総取引数: 3件
- 検証済み取引: 3件  
- 無効取引: 0件
- システム信頼性: 100%

=== 不正検出テスト ===
⚠️ 故意の改ざんテスト:

🚨 ブロック2のハッシュを手動変更...
→ チェーン検証: 失敗
→ 異常検出: ブロック2-3間で不整合

🔧 自動修復試行...
→ エラー: 不変データのため修復不可
→ 対処: システム管理者に通知済み
```

**評価ポイント**:
- 金融レベルのセキュリティ
- 暗号学的検証の実装
- 改ざん検出機能
- 監査証跡の完全性



### 課題3: マルチスレッド対応キャッシュシステム
**ファイル名**: `ThreadSafeCache.java`

高性能でスレッドセーフなキャッシュシステムを作成してください。

**要求仕様**:
- 不変キャッシュエントリ
- 並行アクセスの安全性
- LRU（Least Recently Used）アルゴリズム
- 統計情報の収集

**パフォーマンス要件**:
- 高速読み取り性能
- 効率的なメモリ使用
- ガベージコレクション負荷軽減
- スケーラブルな設計

**実行例**:
```
=== マルチスレッドキャッシュシステム ===

⚡ HyperCache Pro v2.5

=== キャッシュ初期化 ===
✓ 最大容量: 1000エントリ
✓ TTL（生存時間）: 300秒
✓ LRU削除ポリシー: 有効
✓ スレッドセーフティ: 有効

=== データ格納テスト ===
📝 データ格納中...

✓ "user:1001" → UserData(田中太郎, age=25)
✓ "product:2001" → ProductData(ノートPC, ¥98000)  
✓ "session:abc123" → SessionData(10.0.0.1, active)

=== 並行アクセステスト ===
🧵 100スレッドで同時アクセス開始...

スレッド-01: GET user:1001 → HIT (0.02ms)
スレッド-02: PUT user:1002 → SUCCESS (0.05ms)
スレッド-03: GET product:2001 → HIT (0.01ms)
スレッド-04: GET user:9999 → MISS (0.03ms)
...
スレッド-100: GET session:abc123 → HIT (0.02ms)

📊 同時アクセス結果:
- 総アクセス: 10,000回
- データ競合: 0件
- 平均応答時間: 0.025ms
- エラー率: 0%

=== LRU削除テスト ===
💾 メモリ上限テスト...

現在のエントリ数: 1000 (上限到達)
新規データ追加: "temp:001" → TempData(一時的)

🔄 LRU削除実行:
- 削除対象: "old:999" (最終アクセス: 285秒前)
- 新規追加: "temp:001" 
- エントリ数: 1000 (維持)

=== パフォーマンス統計 ===
📈 キャッシュ統計 (過去1時間):

ヒット率: 94.7%
- ヒット数: 47,350回
- ミス数: 2,650回
- 総アクセス: 50,000回

応答時間分布:
- 平均: 0.024ms
- 50%tile: 0.020ms  
- 95%tile: 0.045ms
- 99%tile: 0.087ms

メモリ使用量:
- 現在使用量: 45.2MB
- 最大使用量: 50.0MB
- 効率性: 90.4%

=== 障害耐性テスト ===
⚠️ 異常シナリオテスト:

テスト1: メモリ不足シミュレーション
→ 結果: 自動削除により正常動作継続

テスト2: 大量同時アクセス (1000スレッド)
→ 結果: デッドロックなし、正常処理

テスト3: 不正データ投入
→ 結果: バリデーションにより拒否

✅ すべてのテストに合格

=== GC負荷分析 ===
🗑️ ガベージコレクション影響:

GC前後のメモリ変化:
- GC実行前: 128MB
- GC実行後: 84MB  
- 削減量: 44MB (34%)

オブジェクト生成数:
- 短命オブジェクト: 最小化済み
- 長命オブジェクト: キャッシュエントリのみ
- GC負荷: 5% (目標: <10%)
```

**評価ポイント**:
- 高性能な並行処理
- メモリ効率の最適化
- 実用的なキャッシュ機能
- 包括的な統計収集

## 💡 実装のヒント

### 課題1のヒント
```java
public final class ImmutableConfiguration {
    private final String databaseUrl;
    private final int serverPort;
    private final boolean sslEnabled;
    private final List<String> allowedIps; // 防御的コピー必須
    
    // プライベートコンストラクタ
    private ImmutableConfiguration(Builder builder) {
        this.databaseUrl = builder.databaseUrl;
        this.serverPort = builder.serverPort;
        this.sslEnabled = builder.sslEnabled;
        // 防御的コピー
        this.allowedIps = List.copyOf(builder.allowedIps);
    }
    
    // 防御的コピーでの取得
    public List<String> getAllowedIps() {
        return List.copyOf(allowedIps);
    }
    
    // ビルダーパターン
    public static class Builder {
        private String databaseUrl;
        private int serverPort = 8080; // デフォルト値
        private boolean sslEnabled = false;
        private List<String> allowedIps = new ArrayList<>();
        
        public Builder databaseUrl(String url) {
            this.databaseUrl = url;
            return this;
        }
        
        public ImmutableConfiguration build() {
            validate();
            return new ImmutableConfiguration(this);
        }
        
        private void validate() {
            if (databaseUrl == null) {
                throw new IllegalStateException("Database URL is required");
            }
        }
    }
}
```

### 課題2のヒント
```java
public final class Transaction {
    private final String transactionId;
    private final String fromAccount;
    private final String toAccount;
    private final BigDecimal amount;
    private final long timestamp;
    private final String previousHash;
    private final String signature;
    
    // パッケージプライベートコンストラクタ
    Transaction(String id, String from, String to, BigDecimal amount, String prevHash) {
        this.transactionId = id;
        this.fromAccount = from;
        this.toAccount = to;
        this.amount = amount;
        this.timestamp = System.currentTimeMillis();
        this.previousHash = prevHash;
        this.signature = calculateSignature();
    }
    
    private String calculateSignature() {
        String data = transactionId + fromAccount + toAccount + 
                     amount.toString() + timestamp + previousHash;
        return Integer.toHexString(data.hashCode()); // 簡易実装
    }
    
    // ハッシュチェーン検証
    public boolean isValid(String expectedPreviousHash) {
        return this.previousHash.equals(expectedPreviousHash) &&
               this.signature.equals(calculateSignature());
    }
}
```

### 課題3のヒント
```java
public final class ThreadSafeCache<K, V> {
    private final int maxSize;
    private final long ttlMillis;
    private final ConcurrentHashMap<K, CacheEntry<V>> cache;
    private final AtomicLong hitCount = new AtomicLong(0);
    private final AtomicLong missCount = new AtomicLong(0);
    
    public ThreadSafeCache(int maxSize, long ttlMillis) {
        this.maxSize = maxSize;
        this.ttlMillis = ttlMillis;
        this.cache = new ConcurrentHashMap<>(maxSize);
    }
    
    public Optional<V> get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry != null && !entry.isExpired()) {
            hitCount.incrementAndGet();
            entry.updateAccessTime(); // LRU更新
            return Optional.of(entry.getValue());
        } else {
            missCount.incrementAndGet();
            if (entry != null) {
                cache.remove(key); // 期限切れエントリ削除
            }
            return Optional.empty();
        }
    }
    
    public void put(K key, V value) {
        if (cache.size() >= maxSize) {
            evictLRU(); // LRU削除
        }
        cache.put(key, new CacheEntry<>(value, ttlMillis));
    }
    
    private static final class CacheEntry<V> {
        private final V value;
        private final long creationTime;
        private final long ttl;
        private volatile long lastAccessTime;
        
        CacheEntry(V value, long ttl) {
            this.value = value;
            this.creationTime = System.currentTimeMillis();
            this.ttl = ttl;
            this.lastAccessTime = creationTime;
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() - creationTime > ttl;
        }
        
        void updateAccessTime() {
            this.lastAccessTime = System.currentTimeMillis();
        }
        
        V getValue() { return value; }
        long getLastAccessTime() { return lastAccessTime; }
    }
}
```

## 🔍 応用のポイント

1. **不変性の設計**: オブジェクトの状態を変更不可能にする設計パターン
2. **スレッドセーフティ**: 並行アクセスでも安全なデータ構造
3. **メモリ効率**: オブジェクト生成コストとGC負荷の最適化
4. **セキュリティ**: データの完全性と改ざん防止機能
5. **パフォーマンス**: 高速アクセスと効率的なデータ管理

## ✅ 完了チェックリスト

- [ ] 課題1: 不変設定管理クラスが正常に動作する
- [ ] 課題2: 金融取引記録システムが実装できた
- [ ] 課題3: マルチスレッド対応キャッシュシステムが動作する
- [ ] finalキーワードが適切に使用されている
- [ ] 不変オブジェクトが正しく実装されている
- [ ] スレッドセーフティが確保されている

**次のステップ**: 応用課題が完了したら、challenge/のチャレンジ課題に挑戦してみましょう！