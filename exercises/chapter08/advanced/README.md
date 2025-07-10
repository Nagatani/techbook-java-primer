# 第8章 応用課題

## 🎯 学習目標
- ArrayListの高度な活用技術
- 動的データ構造の効率的な実装
- カスタムデータ構造の設計
- アルゴリズムの最適化
- 実用的なアプリケーション開発

## 📝 課題一覧

### 課題1: 動的配列ベースのデータベース
**ファイル名**: `DynamicArrayDatabase.java`

ArrayListを使用した高性能なインメモリデータベースを作成してください。

**要求仕様**:
- 動的なスキーマ管理
- インデックスによる高速検索
- トランザクション機能
- クエリ最適化

**データベース機能**:
- CRUD操作の実装
- JOIN演算の実装
- 集約関数（SUM, AVG, COUNT等）
- 同期・非同期処理

**実行例**:
```
=== 動的配列ベースデータベース ===

💾 FlexiDB Pro v3.0

=== スキーマ管理 ===
🗂️ テーブル作成:

CREATE TABLE users (
    id INTEGER PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(255),
    age INTEGER,
    created_at TIMESTAMP
);

CREATE TABLE orders (
    id INTEGER PRIMARY KEY,
    user_id INTEGER,
    product_name VARCHAR(200),
    amount DECIMAL(10,2),
    order_date TIMESTAMP
);

スキーマ情報:
- テーブル数: 2個
- 総カラム数: 9個
- プライマリキー: 2個
- 外部キー: 1個 (orders.user_id → users.id)

内部データ構造:
users: ArrayList<UserRecord> (capacity: 1000)
orders: ArrayList<OrderRecord> (capacity: 5000)
メモリ使用量: 2.4MB (初期割り当て)

=== インデックス構築 ===
🔍 高速検索用インデックス:

主キーインデックス:
- users.id: HashMap<Integer, Integer> (O(1)検索)
- orders.id: HashMap<Integer, Integer>

セカンダリインデックス:
- users.email: TreeMap<String, Integer> (一意制約)
- users.age: HashMap<Integer, List<Integer>> (範囲検索対応)
- orders.user_id: HashMap<Integer, List<Integer>> (JOIN用)
- orders.order_date: TreeMap<LocalDateTime, List<Integer>> (時系列検索)

インデックス統計:
- インデックス数: 6個
- メモリオーバーヘッド: 15%
- 構築時間: 0.12秒 (10,000レコード)

=== データ挿入 ===
📝 大量データ投入:

INSERT INTO users VALUES
(1, '田中太郎', 'tanaka@example.com', 25, '2024-01-15 10:30:00'),
(2, '佐藤花子', 'sato@example.com', 32, '2024-01-16 14:20:00'),
(3, '山田次郎', 'yamada@example.com', 28, '2024-01-17 09:45:00'),
...
(10000, 'ユーザー10000', 'user10000@example.com', 45, '2024-07-05 16:30:00');

挿入統計:
- 挿入レコード数: 10,000件
- 平均挿入時間: 0.08ms/レコード
- 総挿入時間: 0.8秒
- インデックス更新: 0.3秒

配列自動拡張:
初期容量: 1,000 → 2,000 → 4,000 → 8,000 → 16,000
拡張回数: 4回
コピー操作: 15,000レコード移動
拡張オーバーヘッド: 3.2%

=== 高速検索 ===
⚡ インデックス活用検索:

主キー検索:
SELECT * FROM users WHERE id = 5432;
実行時間: 0.001ms (HashMap直接アクセス)
結果: 1件

範囲検索:
SELECT * FROM users WHERE age BETWEEN 25 AND 35;
実行時間: 0.045ms
検索対象: 3,456件
結果: 3,456件 (100%ヒット)

文字列検索:
SELECT * FROM users WHERE email LIKE '%@example.com';
実行時間: 8.7ms (全件スキャン)
検索対象: 10,000件
結果: 10,000件

複合条件検索:
SELECT * FROM users WHERE age > 30 AND name LIKE '田中%';
実行時間: 2.1ms
検索最適化: age インデックス使用 (4,567件) → name フィルタ
結果: 123件

=== JOIN演算 ===
🔗 テーブル結合処理:

内部結合:
SELECT u.name, o.product_name, o.amount
FROM users u 
INNER JOIN orders o ON u.id = o.user_id
WHERE u.age > 25;

実行計画:
1. users テーブル age > 25 抽出: 6,789件
2. orders テーブル user_id インデックス参照
3. ハッシュ結合実行
4. 結果セット構築: 23,456件

実行統計:
- 処理時間: 15.7ms
- メモリ使用量: 8.2MB (一時テーブル)
- 結合効率: 94.3%

外部結合:
SELECT u.name, COUNT(o.id) as order_count
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
GROUP BY u.id, u.name;

結果:
- 注文あり顧客: 7,845名
- 注文なし顧客: 2,155名
- 平均注文数: 2.7件/人
- 最大注文数: 45件 (ヘビーユーザー)

=== 集約処理 ===
📊 統計計算:

売上分析:
SELECT 
    DATE(order_date) as date,
    COUNT(*) as order_count,
    SUM(amount) as total_sales,
    AVG(amount) as avg_order_value,
    MAX(amount) as max_order,
    MIN(amount) as min_order
FROM orders 
GROUP BY DATE(order_date)
ORDER BY date;

集約結果 (サンプル):
2024-07-01: 1,234件, ¥4,567,890, ¥3,698, ¥89,500, ¥980
2024-07-02: 1,456件, ¥5,234,670, ¥3,594, ¥95,600, ¥1,200
2024-07-03: 1,678件, ¥6,123,450, ¥3,649, ¥87,300, ¥890

年代別分析:
SELECT 
    CASE 
        WHEN age < 20 THEN '10代'
        WHEN age < 30 THEN '20代'
        WHEN age < 40 THEN '30代'
        WHEN age < 50 THEN '40代'
        ELSE '50代以上'
    END as age_group,
    COUNT(*) as user_count,
    AVG(total_spent) as avg_spent
FROM (
    SELECT u.age, COALESCE(SUM(o.amount), 0) as total_spent
    FROM users u
    LEFT JOIN orders o ON u.id = o.user_id
    GROUP BY u.id, u.age
) t
GROUP BY age_group;

分析結果:
10代: 234名, 平均¥12,345
20代: 2,678名, 平均¥23,456
30代: 3,456名, 平均¥45,678
40代: 2,345名, 平均¥67,890
50代以上: 1,287名, 平均¥34,567

=== トランザクション ===
💳 ACID特性保証:

トランザクション例:
BEGIN TRANSACTION;
    UPDATE users SET age = 26 WHERE id = 1001;
    INSERT INTO orders VALUES (50001, 1001, 'ノートPC', 98000, NOW());
    UPDATE users SET last_purchase = NOW() WHERE id = 1001;
COMMIT;

トランザクション管理:
- 分離レベル: READ_COMMITTED
- ロック粒度: 行レベル
- デッドロック検出: 有効
- タイムアウト: 30秒

ロック統計:
共有ロック: 1,234個 (読み取り専用)
排他ロック: 567個 (更新・削除)
待機中: 0個
デッドロック: 0件 (24時間)

ロールバック例:
BEGIN TRANSACTION;
    DELETE FROM orders WHERE user_id = 999;
    -- エラー発生: 外部キー制約違反
ROLLBACK;

ロールバック実行:
- 変更取り消し: 完了
- インデックス復元: 完了
- メモリ解放: 完了
- 実行時間: 0.003秒

=== クエリ最適化 ===
🚀 実行計画最適化:

複雑クエリ:
SELECT u.name, u.age, 
       COUNT(o.id) as order_count,
       SUM(o.amount) as total_spent
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
WHERE u.age BETWEEN 25 AND 45
  AND u.created_at >= '2024-01-01'
GROUP BY u.id, u.name, u.age
HAVING total_spent > 10000
ORDER BY total_spent DESC
LIMIT 100;

最適化前実行計画:
1. users 全件スキャン (10,000件)
2. 条件フィルタ適用
3. orders と結合
4. GROUP BY 実行
5. HAVING フィルタ
6. ORDER BY ソート
推定実行時間: 125ms

最適化後実行計画:
1. users.age インデックス使用 (6,234件)
2. created_at 条件で絞り込み (4,567件)
3. orders.user_id インデックスでJOIN
4. 集約処理を並列実行
5. TOP-K ソートアルゴリズム適用
実際実行時間: 23ms (5.4倍高速化)

=== 並列処理 ===
🔄 マルチスレッド対応:

並列クエリ実行:
SELECT user_id, SUM(amount) as total
FROM orders 
WHERE order_date >= '2024-01-01'
GROUP BY user_id;

並列化戦略:
- データ分割: 10,000件を4分割
- ワーカースレッド: 4個
- 局所集約: 各スレッドで部分集約
- 最終統合: メインスレッドで結合

パフォーマンス:
シングルスレッド: 45ms
マルチスレッド: 12ms (3.75倍高速)
オーバーヘッド: 8% (スレッド管理)
スケーラビリティ: 85%

同期制御:
読み取り: ReadWriteLock (並列読み取り可)
書き込み: 排他ロック (順次実行)
スナップショット: MVCC風実装

=== メモリ管理 ===
💾 効率的メモリ使用:

メモリ使用量統計:
データ本体: 45.2MB
インデックス: 8.7MB
キャッシュ: 12.1MB
メタデータ: 2.3MB
総使用量: 68.3MB

ガベージコレクション最適化:
- 世代別GC: 有効
- 長寿命オブジェクト: データレコード
- 短寿命オブジェクト: クエリ結果
- GC頻度: 5秒間隔
- GC時間: 平均 3ms

メモリプール:
結果セット用: 16MB プール
一時オブジェクト用: 8MB プール
再利用率: 89%
メモリ断片化: 5%以下

キャッシュ戦略:
LRU キャッシュ: クエリ結果 (100件)
ヒット率: 76%
キャッシュサイズ: 12MB
TTL: 300秒

=== パフォーマンス監視 ===
📈 リアルタイム性能監視:

クエリ実行統計 (過去1時間):
総クエリ数: 45,678件
平均実行時間: 2.3ms
95%tile実行時間: 15.7ms
99%tile実行時間: 45.2ms

スループット:
SELECT: 3,456 queries/sec
INSERT: 567 queries/sec
UPDATE: 234 queries/sec
DELETE: 89 queries/sec

リソース使用率:
CPU: 23% (平均)
メモリ: 68.3MB / 1GB (6.8%)
ディスク: 0% (メモリのみ)
ネットワーク: N/A (ローカル)

ボトルネック分析:
CPU集約: 67% (集約処理)
メモリ集約: 23% (大量結合)
I/O集約: 0% (インメモリ)
ロック待機: 10% (並行更新)

最適化提案:
💡 インデックス追加提案: orders.product_name
💡 パーティショニング: order_date による分割
💡 非正規化: 頻繁JOIN対象テーブル
💡 読み取り専用レプリカ: 参照負荷分散
```

**評価ポイント**:
- ArrayListの効果的な活用
- データベース理論の理解
- パフォーマンス最適化技術
- メモリ効率の考慮



### 課題2: リアルタイムログ解析システム
**ファイル名**: `RealTimeLogAnalyzer.java`

大量のログデータをリアルタイムで解析するシステムを作成してください。

**要求仕様**:
- ストリーミングデータの処理
- 時系列データの効率的な管理
- 異常検知アルゴリズム
- リアルタイムアラート機能

**解析機能**:
- ログパターンの自動検出
- 統計的異常検知
- トレンド分析
- 予測分析

**実行例**:
```
=== リアルタイムログ解析システム ===

📊 LogInsight AI v4.0

=== ストリーミング設定 ===
🌊 データ流入設定:

ログソース:
- Webサーバー: 8台 (nginx, apache)
- アプリサーバー: 12台 (Java, Python, Node.js)
- データベース: 4台 (MySQL, PostgreSQL)
- ロードバランサー: 2台 (HAProxy)

データ流入レート:
- 平常時: 12,000 行/秒
- ピーク時: 45,000 行/秒
- データサイズ: 平均 512 bytes/行
- 総データ量: 2.1 TB/日

ログ形式:
[2024-07-05 10:30:15.123] [INFO] [web-01] 192.168.1.100 GET /api/users 200 45ms
[2024-07-05 10:30:15.156] [ERROR] [app-03] NullPointerException at line 245
[2024-07-05 10:30:15.198] [WARN] [db-02] Slow query detected: 1.2s

パーサー設定:
- 日時抽出: 正規表現パターン
- ログレベル: INFO, WARN, ERROR, DEBUG
- ソース識別: ホスト名 + サービス名
- 構造化: 自動JSON変換

=== リアルタイム処理 ===
⚡ ストリーム処理エンジン:

処理パイプライン:
1. 受信バッファ: 1MB × 8個 (並列受信)
2. パース処理: 16並列スレッド
3. 正規化: フィールド統一・型変換
4. エンリッチ: IP→地域変換、ユーザー情報付加
5. 分析処理: 統計・異常検知
6. 出力: アラート・ダッシュボード更新

バッファ管理:
受信バッファ: ArrayDeque<LogLine> (10,000件)
処理キュー: PriorityQueue (時刻順ソート)
ウィンドウバッファ: ArrayList<MetricPoint> (1分間隔)

メモリ使用量:
受信バッファ: 45MB
処理バッファ: 120MB
ウィンドウデータ: 230MB
インデックス: 78MB
総使用量: 473MB

処理レイテンシ:
受信→パース: 0.5ms
パース→正規化: 0.3ms
正規化→分析: 1.2ms
分析→出力: 0.8ms
End-to-End: 2.8ms (目標: < 5ms)

=== 時系列データ管理 ===
📈 効率的な時系列処理:

時系列データ構造:
TimeSeriesBuffer<MetricValue>:
- 内部: ArrayList<Bucket> (1分間隔)
- 各Bucket: 60秒間のデータポイント
- 保持期間: 24時間 (1,440バケット)
- 自動ローテーション: 古いデータ削除

メトリクス種類:
- リクエスト数: /秒
- エラー率: %
- レスポンス時間: ms (P50, P95, P99)
- スループット: MB/s
- 接続数: 同時接続

時系列演算:
移動平均: 5分、15分、1時間ウィンドウ
変化率: 前期間比較 (%, 絶対値)
季節性除去: 曜日・時間帯パターン除去
トレンド抽出: 線形回帰・指数平滑

圧縮アルゴリズム:
同一値圧縮: 連続同値は1個に圧縮
デルタ圧縮: 差分値のみ記録
LZ4圧縮: バッチ単位での圧縮
圧縮率: 73% (元データサイズ比)

=== 異常検知 ===
🚨 AI による異常検知:

統計的異常検知:
Z-Score 異常検知:
- 閾値: |Z| > 3.0 (99.7%信頼区間外)
- 対象: レスポンス時間、エラー率
- 計算: (x - μ) / σ
- ウィンドウ: 過去1時間の統計

IQR (四分位範囲) 異常検知:
- 外れ値定義: Q1 - 1.5×IQR または Q3 + 1.5×IQR
- 対象: リクエスト数、CPU使用率
- 季節調整: 曜日・時間帯による正規化

機械学習異常検知:
Isolation Forest:
- 特徴量: 15次元 (メトリクス組み合わせ)
- 木の数: 100本
- 異常スコア: 0.6以上で異常
- 学習データ: 過去30日間

LSTM 予測モデル:
- 入力: 過去60分のメトリクス
- 出力: 次15分の予測値
- 異常判定: 予測値と実際値の乖離 > 20%
- モデル更新: 6時間毎

検知結果 (過去24時間):
異常アラート: 23件
- 高優先度: 3件 (システム障害レベル)
- 中優先度: 8件 (性能劣化)
- 低優先度: 12件 (軽微な異常)

偽陽性率: 2.1% (目標: <5%)
偽陰性率: 0.8% (目標: <1%)

=== パターン認識 ===
🔍 ログパターン自動検出:

エラーパターン分析:
NullPointerException: 145回 (最頻出)
ConnectionTimeout: 67回
OutOfMemoryError: 12回 (重要度: 高)
FileNotFoundException: 89回

エラーパターン例:
[ERROR] java.sql.SQLException: Connection timeout
発生頻度: 45分間隔 (周期性あり)
影響範囲: app-server-03, db-cluster-01
相関: CPU使用率 > 80% との関連性

アクセスパターン分析:
正常パターン:
/api/login → /api/dashboard → /api/data
異常パターン:
/api/admin (未認証) → 403エラー (15回/分)
→ 潜在的なブルートフォース攻撃

セキュリティパターン:
不審なアクセス:
- 短時間大量アクセス: 192.168.100.* から 1000req/min
- 異常なUser-Agent: "scanner_bot_v2.0"
- SQLインジェクション試行: 'OR 1=1-- パターン

自動分類:
正常: 98.7% (45,123件)
異常: 1.1% (503件)
不明: 0.2% (91件)

=== 予測分析 ===
🔮 トレンド予測:

時系列予測モデル:
ARIMA(2,1,2) モデル:
- 自己回帰項: 2次
- 差分: 1回
- 移動平均: 2次
- 予測精度: MAPE 8.7%

季節性分解:
トレンド成分: 長期増減傾向
季節成分: 週単位・日単位周期
ランダム成分: 予測不可能な変動

予測結果:
次1時間のリクエスト数:
現在: 12,450 req/min
予測: 13,780 req/min (±890)
信頼区間: 95%

次のピーク予測:
予測時刻: 18:30-19:30 (夕方ピーク)
予測負荷: 45,000 req/min
推奨アクション: オートスケール 3台追加

容量プランニング:
30日後の必要リソース:
CPU: 現在の1.23倍
メモリ: 現在の1.15倍
ストレージ: 現在の1.45倍

=== アラート管理 ===
🔔 インテリジェントアラート:

アラートルール:
Critical (即座通知):
- エラー率 > 5% (5分継続)
- レスポンス時間 > 5秒 (P95)
- システムダウン検知

Warning (15分以内通知):
- エラー率 > 2% (10分継続)
- レスポンス時間 > 2秒 (P95)
- CPU使用率 > 80%

Info (1時間以内通知):
- 新しいエラーパターン検出
- 予測しきい値超過
- 定期レポート

アラート抑制:
重複アラート: 同一条件で5分以内は抑制
関連アラート: 根本原因アラートのみ通知
メンテナンス時間: 自動抑制

通知チャネル:
- Slack: 開発チーム向け
- Email: 管理者向け
- PagerDuty: 緊急障害
- WebHook: 外部システム連携

エスカレーション:
Level 1: 当番エンジニア (即座)
Level 2: チームリーダー (15分無応答)
Level 3: 部門長 (30分無応答)

アラート統計 (過去1週間):
発報数: 156件
解決時間: 平均 12分
偽陽性: 8件 (5.1%)
見逃し: 1件 (0.6%)

=== ダッシュボード ===
📊 リアルタイム可視化:

メインダッシュボード:
システム概要:
- 稼働率: 99.97% (30日間)
- 総リクエスト: 2.1億件 (今月)
- 平均レスポンス: 245ms
- エラー率: 0.23%

リアルタイムメトリクス:
現在のRPS: 12,450
レスポンス時間分布:
< 100ms: 67%
100-500ms: 28%
500ms-1s: 4%
> 1s: 1%

地理的分布:
日本: 67% (東京, 大阪)
アメリカ: 18% (カリフォルニア, ニューヨーク)
ヨーロッパ: 12% (ロンドン, フランクフルト)
その他: 3%

トレンドグラフ:
- 過去24時間のリクエスト数推移
- エラー率の変化
- レスポンス時間の P50, P95, P99

ヒートマップ:
時間 × 曜日のアクセス密度
ピーク: 平日 18:00-19:00
最小: 土日 04:00-05:00

=== システム性能 ===
⚡ 高性能処理:

スループット性能:
データ処理: 45,000 行/秒
メモリ使用量: 473MB (安定)
CPU使用率: 34% (8コア)
GC時間: 平均 15ms/分

レイテンシ分布:
P50: 1.2ms
P95: 2.8ms
P99: 7.1ms
最大: 45.3ms

スケーラビリティ:
水平スケール: 8ノードまで検証済み
線形スケール率: 94%
ボトルネック: ネットワーク帯域幅

高可用性:
稼働率: 99.97% (30日)
平均復旧時間: 2.3分
データ損失: 0件
冗長化: 3ノード構成

障害対応:
自動フェイルオーバー: 15秒
バックアップ復元: 5分
災害復旧: 30分 (別サイト)
```

**評価ポイント**:
- ストリーミングデータの効率的処理
- 機械学習アルゴリズムの実装
- リアルタイム性能の実現
- スケーラブルなシステム設計



### 課題3: 動的在庫最適化システム
**ファイル名**: `DynamicInventoryOptimizer.java`

需要予測と在庫最適化を行うスマートシステムを作成してください。

**要求仕様**:
- 需要予測アルゴリズム
- 在庫レベルの動的調整
- 季節性と トレンドの考慮
- 多拠点在庫の最適化

**最適化機能**:
- ABC分析
- 発注点の自動計算
- 安全在庫の動的調整
- コスト最小化

**実行例**:
```
=== 動的在庫最適化システム ===

📦 SmartStock AI v3.5

=== 商品マスタ管理 ===
🏷️ 商品データ管理:

登録商品数: 50,000 SKU
カテゴリ: 1,200種類
サプライヤー: 850社
倉庫: 15拠点

商品分類 (ABC分析):
Aクラス (重要): 10,000 SKU (20%)
- 売上貢献: 80%
- 管理頻度: 毎日
- 安全在庫日数: 3-7日

Bクラス (中程度): 20,000 SKU (40%)
- 売上貢献: 15%
- 管理頻度: 週1回
- 安全在庫日数: 7-14日

Cクラス (一般): 20,000 SKU (40%)
- 売上貢献: 5%
- 管理頻度: 月1回
- 安全在庫日数: 14-30日

データ構造:
商品マスタ: ArrayList<Product>
在庫履歴: ArrayList<StockTransaction>
需要履歴: ArrayList<DemandRecord>
予測結果: ArrayList<ForecastResult>

メモリ使用量:
商品マスタ: 12.5MB
取引履歴: 156.7MB (2年分)
予測データ: 45.2MB
総使用量: 214.4MB

=== 需要予測エンジン ===
🔮 AI需要予測:

予測アルゴリズム:
1. 時系列分解: STL分解
   - トレンド: 長期増減傾向
   - 季節性: 週・月・年周期
   - 残差: ランダム要素

2. 機械学習モデル:
   - Random Forest: 15個決定木
   - 特徴量: 20次元
   - 学習期間: 過去2年
   - 予測期間: 13週先まで

3. 外部要因考慮:
   - 天気データ: API連携
   - イベント情報: カレンダー統合
   - 経済指標: GDP, 消費者物価
   - プロモーション: 過去効果分析

予測精度評価:
MAPE (平均絶対誤差率):
Aクラス: 8.7% (高精度)
Bクラス: 15.2% (標準)
Cクラス: 28.4% (低精度許容)

商品別予測例:
商品A (ノートPC):
過去4週平均: 245台/週
予測 (次4週):
Week 1: 267台 (±23台)
Week 2: 289台 (±31台)
Week 3: 301台 (±35台)
Week 4: 278台 (±29台)

季節要因: 新学期需要 (+15%)
トレンド: 微増 (+2% YoY)
プロモーション効果: 期待なし

=== 在庫最適化 ===
📊 在庫レベル最適化:

最適化目標:
- 在庫コスト最小化
- サービスレベル維持 (95%)
- 欠品コスト最小化
- 廃棄コスト最小化

発注点計算:
発注点 = 平均リードタイム需要 + 安全在庫

安全在庫計算:
安全在庫 = Z値 × √(リードタイム × 需要分散 + 平均需要² × リードタイム分散)

商品Aの最適化結果:
現在在庫: 1,245台
発注点: 856台
安全在庫: 267台
最適発注量: 1,500台 (EOQ)

コスト分析:
保管コスト: ¥450/台/年
発注コスト: ¥15,000/回
欠品コスト: ¥3,200/台
廃棄コスト: ¥8,900/台

総コスト (年間):
保管: ¥560,250
発注: ¥180,000 (12回/年)
欠品: ¥64,000 (20件)
廃棄: ¥89,000 (10台)
合計: ¥893,250

最適化効果:
最適化前: ¥1,245,600
最適化後: ¥893,250
コスト削減: ¥352,350 (28%)

=== 多拠点最適化 ===
🏭 拠点間在庫最適化:

拠点構成:
関東DC (メイン): 30,000 SKU
関西DC: 25,000 SKU
中部DC: 20,000 SKU
九州DC: 15,000 SKU
東北DC: 12,000 SKU

輸送最適化:
目的: 輸送コスト + 在庫コスト最小化
制約: 各拠点容量制限、納期制約

最適配置例 (商品A):
関東DC: 4,500台 (需要地に近い)
関西DC: 2,800台
中部DC: 1,900台
九州DC: 1,200台
東北DC: 800台

拠点間移動:
関東→関西: 500台 (需要増対応)
中部→九州: 200台 (在庫バランス)
移動コスト: ¥145,000
移動効果: 欠品回避 ¥380,000

相互補完ルール:
緊急補充: 隣接拠点から24時間以内
定期補充: 週1回最適化実行
閾値補充: 安全在庫切れ時自動発動

=== 季節性対応 ===
📅 季節変動対応:

季節パターン分析:
春: 新生活需要 (+25%)
- 家電: +40%
- 家具: +30%
- 衣料: +20%

夏: エアコン・冷蔵庫需要
- 冷房機器: +80%
- 冷蔵庫: +45%
- 扇風機: +120%

秋: 衣替え需要
- 秋冬衣料: +60%
- 暖房機器: +35%

冬: 年末商戦・ボーナス需要
- 家電全般: +55%
- ゲーム: +90%
- 玩具: +150%

季節調整在庫:
エアコン (夏季商品):
1-3月: 最小在庫 (100台)
4-5月: 段階増加 (500→1,500台)
6-8月: ピーク在庫 (3,000台)
9-11月: 段階減少 (1,500→300台)
12月: 最小在庫 (100台)

予約注文システム:
季節商品の早期受注:
- 予約開始: 2ヶ月前
- 確定発注: 1ヶ月前
- リスク軽減: 85%

=== 自動発注システム ===
🤖 AI自動発注:

発注ルール:
1. 発注点方式:
   在庫量 ≤ 発注点 → 自動発注

2. 定期発注方式:
   毎週月曜日に発注量計算

3. 予測発注方式:
   予測需要に基づく先行発注

4. 緊急発注方式:
   突発需要対応

発注実行例:
商品B (冷蔵庫):
現在在庫: 145台
発注点: 180台
→ 発注点割れ検知

発注量計算:
目標在庫: 350台
発注量: 350 - 145 + 予測需要(100) = 305台
発注先: サプライヤーX
納期: 7日
発注承認: 自動実行

発注結果通知:
✓ 発注書送信: 完了
✓ 入荷予定登録: 完了
✓ 在庫計画更新: 完了
✓ 関係者通知: 完了

=== リスク管理 ===
⚠️ 在庫リスク管理:

リスク種類:
1. 需要変動リスク
   - 予測誤差による過剰/不足
   - 対策: 安全在庫、柔軟な発注

2. 供給リスク
   - サプライヤー遅延/故障
   - 対策: 複数調達先、バッファ

3. 廃棄リスク
   - 売れ残りによる損失
   - 対策: 早期値引き、返品制度

4. 機会損失リスク
   - 欠品による売上機会逸失
   - 対策: 高精度予測、迅速補充

リスク評価 (商品A):
需要変動: 中リスク (MAPE 8.7%)
供給遅延: 低リスク (実績 2%)
廃棄可能性: 低リスク (0.5%)
機会損失: 高影響 (¥3,200/台)

総合リスクスコア: 6.2/10 (管理対象)

=== パフォーマンス統計 ===
📈 システム性能指標:

処理性能:
予測計算: 50,000 SKU/10分
最適化実行: 15拠点同時処理
データ更新: リアルタイム
レスポンス: 平均 0.8秒

在庫改善効果:
在庫回転率: 6.2回/年 → 8.7回/年 (+40%)
サービスレベル: 92% → 96% (+4%)
欠品率: 3.2% → 1.8% (-44%)
廃棄率: 2.1% → 0.9% (-57%)

コスト削減:
在庫コスト: -¥45M/年 (-25%)
物流コスト: -¥12M/年 (-15%)
機会損失: -¥8M/年 (-35%)
総削減効果: ¥65M/年

精度向上:
予測精度 (MAPE):
導入前: 22.4%
導入後: 12.8%
改善率: 43%

ユーザー満足度:
在庫担当者: 4.6/5.0
調達担当者: 4.4/5.0
経営陣: 4.8/5.0
```

**評価ポイント**:
- 需要予測アルゴリズムの実装
- 最適化アルゴリズムの理解
- ビジネスロジックの適切な実装
- 実用的なシステム設計

## 💡 実装のヒント

### 課題1のヒント
```java
class DynamicArrayDatabase {
    private Map<String, ArrayList<Record>> tables;
    private Map<String, Map<String, Integer>> indexes; // table -> column -> recordIndex
    
    public ResultSet executeQuery(Query query) {
        if (query.hasJoin()) {
            return executeJoinQuery(query);
        } else {
            return executeSimpleQuery(query);
        }
    }
    
    private ResultSet executeJoinQuery(JoinQuery query) {
        // 結合戦略の選択
        if (hasIndex(query.getJoinColumn())) {
            return executeHashJoin(query);
        } else {
            return executeNestedLoopJoin(query);
        }
    }
    
    private ResultSet executeHashJoin(JoinQuery query) {
        // ハッシュ結合の実装
        Map<Object, ArrayList<Record>> hashTable = new HashMap<>();
        
        // 小さいテーブルでハッシュテーブル構築
        ArrayList<Record> smallTable = getSmallTable(query);
        for (Record record : smallTable) {
            Object key = record.getValue(query.getJoinColumn());
            hashTable.computeIfAbsent(key, k -> new ArrayList<>()).add(record);
        }
        
        // 大きいテーブルでプローブ
        ArrayList<Record> result = new ArrayList<>();
        ArrayList<Record> largeTable = getLargeTable(query);
        for (Record record : largeTable) {
            Object key = record.getValue(query.getJoinColumn());
            ArrayList<Record> matches = hashTable.get(key);
            if (matches != null) {
                for (Record match : matches) {
                    result.add(mergeRecords(record, match));
                }
            }
        }
        
        return new ResultSet(result);
    }
}
```

### 課題2のヒント
```java
class RealTimeLogAnalyzer {
    private final ArrayDeque<LogEntry> buffer = new ArrayDeque<>();
    private final ArrayList<TimeWindow> windows = new ArrayList<>();
    private final AnomalyDetector detector = new AnomalyDetector();
    
    public void processLogStream(Stream<String> logStream) {
        logStream.parallel()
                .map(this::parseLogEntry)
                .forEach(this::processLogEntry);
    }
    
    private void processLogEntry(LogEntry entry) {
        // バッファに追加
        synchronized (buffer) {
            buffer.offer(entry);
            if (buffer.size() > MAX_BUFFER_SIZE) {
                buffer.poll(); // 古いエントリを削除
            }
        }
        
        // 時系列ウィンドウの更新
        updateTimeWindows(entry);
        
        // 異常検知
        if (detector.isAnomaly(entry)) {
            sendAlert(entry);
        }
    }
    
    private void updateTimeWindows(LogEntry entry) {
        long timestamp = entry.getTimestamp();
        
        // 適切なウィンドウを見つけるか作成
        TimeWindow window = findOrCreateWindow(timestamp);
        window.addEntry(entry);
        
        // 古いウィンドウを削除
        windows.removeIf(w -> w.isExpired(timestamp));
    }
    
    class AnomalyDetector {
        private final ArrayList<Double> baseline = new ArrayList<>();
        
        public boolean isAnomaly(LogEntry entry) {
            double value = extractMetricValue(entry);
            
            if (baseline.size() < MIN_BASELINE_SIZE) {
                baseline.add(value);
                return false;
            }
            
            // Z-score による異常検知
            double mean = baseline.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            double variance = baseline.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .average().orElse(0);
            double stdDev = Math.sqrt(variance);
            
            double zScore = Math.abs((value - mean) / stdDev);
            
            // ベースラインの更新
            baseline.add(value);
            if (baseline.size() > MAX_BASELINE_SIZE) {
                baseline.remove(0);
            }
            
            return zScore > ANOMALY_THRESHOLD;
        }
    }
}
```

### 課題3のヒント
```java
class DynamicInventoryOptimizer {
    private ArrayList<Product> products;
    private ArrayList<DemandRecord> demandHistory;
    private ForecastEngine forecastEngine;
    
    public OptimizationResult optimize(Product product) {
        // 需要予測
        ForecastResult forecast = forecastEngine.forecast(product, demandHistory);
        
        // 最適在庫レベル計算
        double optimalStock = calculateOptimalStock(product, forecast);
        double reorderPoint = calculateReorderPoint(product, forecast);
        double safetyStock = calculateSafetyStock(product, forecast);
        
        return new OptimizationResult(optimalStock, reorderPoint, safetyStock);
    }
    
    private double calculateOptimalStock(Product product, ForecastResult forecast) {
        // EOQ (Economic Order Quantity) モデル
        double annualDemand = forecast.getAnnualDemand();
        double orderingCost = product.getOrderingCost();
        double holdingCost = product.getHoldingCost();
        
        return Math.sqrt((2 * annualDemand * orderingCost) / holdingCost);
    }
    
    private double calculateSafetyStock(Product product, ForecastResult forecast) {
        // 安全在庫の計算
        double serviceLevel = product.getTargetServiceLevel();
        double zValue = getZValue(serviceLevel); // 正規分布表から取得
        
        double leadTime = product.getLeadTime();
        double demandVariance = forecast.getDemandVariance();
        double leadTimeVariance = product.getLeadTimeVariance();
        double averageDemand = forecast.getAverageDemand();
        
        return zValue * Math.sqrt(leadTime * demandVariance + 
                                 averageDemand * averageDemand * leadTimeVariance);
    }
    
    class ForecastEngine {
        public ForecastResult forecast(Product product, ArrayList<DemandRecord> history) {
            // 時系列分解
            TimeSeriesDecomposition decomp = decompose(history);
            
            // トレンド予測
            double trend = calculateTrend(decomp.getTrend());
            
            // 季節性調整
            double seasonal = getSeasonalFactor(product, getCurrentSeason());
            
            // 基本予測値
            double baseForecast = decomp.getLevel() + trend;
            
            // 季節性適用
            double adjustedForecast = baseForecast * seasonal;
            
            return new ForecastResult(adjustedForecast, calculateVariance(history));
        }
        
        private TimeSeriesDecomposition decompose(ArrayList<DemandRecord> history) {
            // STL分解の簡易実装
            double level = history.stream()
                .mapToDouble(DemandRecord::getDemand)
                .average().orElse(0);
            
            // トレンド計算（線形回帰）
            double trend = calculateLinearTrend(history);
            
            return new TimeSeriesDecomposition(level, trend);
        }
    }
}
```

## 🔍 応用のポイント

1. **ArrayList の高度な活用**: 動的配列の特性を活かしたデータ構造設計
2. **アルゴリズムの実装**: 検索、ソート、最適化アルゴリズムの効率的な実装
3. **リアルタイム処理**: ストリーミングデータの効率的な処理
4. **機械学習の応用**: 予測、分類、異常検知アルゴリズムの実装
5. **システム設計**: スケーラブルで保守性の高いアーキテクチャ

## ✅ 完了チェックリスト

- [ ] 課題1: 動的配列ベースのデータベースが正常に動作する
- [ ] 課題2: リアルタイムログ解析システムが実装できた
- [ ] 課題3: 動的在庫最適化システムが動作する
- [ ] ArrayListが効果的に活用されている
- [ ] アルゴリズムが正しく実装されている
- [ ] パフォーマンスが最適化されている

**次のステップ**: 応用課題が完了したら、challenge/のチャレンジ課題に挑戦してみましょう！