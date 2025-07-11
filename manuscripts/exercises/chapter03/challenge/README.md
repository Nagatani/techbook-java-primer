# 第3章 チャレンジ課題

## 概要
オブジェクト指向設計の真髄に迫る高度な課題です。実世界の複雑な問題をオブジェクト指向の手法で解決し、保守性・拡張性・再利用性の高いシステムを設計・実装します。

## 学習目標
- 大規模システムのアーキテクチャを設計できる
- デザインパターンの基礎を理解し適用できる
- 複雑なビジネスロジックを適切にモデル化できる
- 将来の変更に強い設計ができる

## 課題一覧

### チャレンジ課題1: 統合物流管理システム
**ファイル名**: `Product.java`、`Warehouse.java`、`Truck.java`、`Driver.java`、`Route.java`、`Order.java`、`LogisticsSystem.java`

#### 要求仕様
1. 在庫管理機能：
   - 複数倉庫での在庫管理
   - 商品の入出庫記録
   - 在庫の最適配置提案
   - 期限管理（賞味期限など）
2. 配送管理機能：
   - 配送ルートの計画
   - トラックの積載量管理
   - ドライバーのスケジュール
   - リアルタイム配送追跡
3. 注文処理機能：
   - 注文の受付と割り当て
   - 在庫引当と出荷指示
   - 配送状況の更新
   - 納期管理
4. 最適化機能：
   - 配送ルートの最適化
   - 積載効率の最大化
   - コスト計算と分析
   - KPI（重要業績評価指標）ダッシュボード

#### 実装のヒント
- グラフ理論を使った経路探索
- 貪欲法による積載最適化
- イベント駆動の状態管理

#### 実装例の骨組み
```java
// Product.java
public class Product {
    private String productId;
    private String name;
    private double weight;
    private double volume;
    private int shelfLife; // 日数
    private boolean isFragile;
    
    public boolean isExpiringSoon(int currentDate) {
        // 期限切れチェック
    }
}

// Warehouse.java
public class Warehouse {
    private String warehouseId;
    private String location;
    private Inventory inventory;
    private int capacity;
    
    public boolean canFulfillOrder(Order order) {
        // 在庫充足チェック
    }
    
    public void optimizeLayout() {
        // 在庫配置の最適化
    }
}

// Route.java
public class Route {
    private String[] waypoints;
    private double totalDistance;
    private double estimatedTime;
    
    public void optimize() {
        // 巡回セールスマン問題の簡易解法
    }
}

// LogisticsSystem.java
public class LogisticsSystem {
    private Warehouse[] warehouses;
    private Truck[] fleet;
    private Order[] pendingOrders;
    
    public DeliveryPlan planDeliveries(int date) {
        // 配送計画の生成
    }
    
    public void generateKPIReport() {
        // KPIレポートの生成
    }
}
```

#### 評価ポイント
- システム全体の一貫性
- アルゴリズムの効率性
- 実務での実用性
- 拡張可能な設計

#### 発展学習
- 機械学習による需要予測
- IoTデバイスとの連携
- ブロックチェーンでの履歴管理

---

### チャレンジ課題2: ソーシャルネットワークシミュレーター
**ファイル名**: `User.java`、`Post.java`、`Comment.java`、`Like.java`、`Friendship.java`、`Timeline.java`、`SocialNetwork.java`

#### 要求仕様
1. ユーザー管理：
   - プロフィール管理
   - フレンド関係の構築
   - フォロー/フォロワー機能
   - プライバシー設定
2. コンテンツ管理：
   - 投稿の作成・編集・削除
   - コメント機能
   - いいね機能
   - シェア機能
3. タイムライン機能：
   - 時系列表示
   - フレンドの投稿表示
   - アルゴリズムによる表示優先度
   - トレンド分析
4. 分析機能：
   - エンゲージメント率計算
   - 影響力スコア算出
   - ネットワーク分析
   - コミュニティ検出

#### 実装のヒント
- グラフ構造でソーシャルネットワークを表現
- 時系列データの効率的な管理
- ページランクアルゴリズムの簡易版

#### 実装例の骨組み
```java
// User.java
public class User {
    private String userId;
    private String username;
    private Profile profile;
    private User[] friends;
    private User[] followers;
    private PrivacySettings privacy;
    
    public double calculateInfluenceScore() {
        // 影響力スコアの計算
    }
}

// Post.java
public class Post {
    private String postId;
    private User author;
    private String content;
    private long timestamp;
    private Like[] likes;
    private Comment[] comments;
    private Post[] shares;
    
    public double getEngagementRate() {
        // エンゲージメント率の計算
    }
}

// Timeline.java
public class Timeline {
    private User owner;
    private Post[] posts;
    
    public Post[] generateFeed() {
        // パーソナライズされたフィードの生成
    }
    
    public void applyAlgorithm(FeedAlgorithm algorithm) {
        // フィードアルゴリズムの適用
    }
}

// SocialNetwork.java
public class SocialNetwork {
    private User[] users;
    private Map<String, User> userIndex;
    
    public User[] findShortestPath(User from, User to) {
        // 最短経路探索（6次の隔たり）
    }
    
    public Community[] detectCommunities() {
        // コミュニティ検出アルゴリズム
    }
}
```

#### 評価ポイント
- グラフアルゴリズムの実装
- スケーラビリティの考慮
- プライバシーとセキュリティ
- リアルな振る舞いの再現

#### 発展学習
- 推薦システムの実装
- 感情分析機能
- リアルタイム通知システム

## 提出方法
1. 選択した課題の完全な実装
2. アーキテクチャ設計書（クラス図、シーケンス図を含む）
3. アルゴリズムの説明文書
4. パフォーマンステストの結果
5. 今後の拡張提案書

## 評価の観点
- オブジェクト指向設計の優秀性
- アルゴリズムの革新性と効率性
- コードの品質と保守性
- ドキュメントの充実度
- 実世界での適用可能性

## 注意事項
- 大規模データでの動作を意識
- メモリ効率を考慮した実装
- 将来の機能拡張を前提とした設計
- セキュリティとプライバシーへの配慮