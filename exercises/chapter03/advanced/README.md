# 第3章 応用課題

## 学習目標
- 複数のコンストラクタの適切な使い分け
- thisキーワードの効果的な活用
- メソッドオーバーロードの実践
- イミュータブルオブジェクトの設計
- 初期化ブロックの理解

## 課題一覧

### 課題1: 銀行口座管理システム
**ファイル名**: `BankAccountSystem.java`

様々な種類の銀行口座を管理するシステムを作成してください。

**要求仕様**:
- 複数のコンストラクタで異なる初期化方法に対応
- thisキーワードによるコンストラクタチェーン
- メソッドオーバーロードによる多様な操作
- 口座の種類に応じた初期設定

**実装すべきクラス**:

```java
class BankAccount {
    // 複数のコンストラクタ:
    // 1. 基本情報のみ
    // 2. 初期残高付き
    // 3. 口座種別付き
    // 4. 完全指定
    
    // メソッドオーバーロード:
    // deposit(double) / deposit(double, String)
    // withdraw(double) / withdraw(double, String)
}
```

**実行例**:
```
=== 銀行口座管理システム ===

=== 口座開設 ===
✓ 普通預金口座開設: 1001-山田太郎 (初期残高: ¥0)
✓ 定期預金口座開設: 2001-佐藤花子 (初期残高: ¥100,000)
✓ 当座預金口座開設: 3001-田中次郎 (初期残高: ¥50,000, 当座貸越限度額: ¥100,000)

=== 取引処理 ===
山田太郎の普通預金:
入金: ¥30,000 (給与振込)
現在残高: ¥30,000

出金: ¥5,000 (ATM出金)
現在残高: ¥25,000

佐藤花子の定期預金:
満期日設定: 2025年7月5日
金利設定: 0.5%

田中次郎の当座預金:
出金: ¥80,000 (小切手振出)
現在残高: -¥30,000 (当座貸越中)
利用可能額: ¥70,000
```

**評価ポイント**:
- コンストラクタオーバーロードの適切な使用
- thisによるコードの簡潔化
- 口座種別に応じた適切な初期化



### 課題2: 商品在庫管理
**ファイル名**: `ProductInventory.java`

商品の在庫管理を行うシステムを作成してください。

**要求仕様**:
- 複数の商品作成パターンに対応
- 商品情報の段階的設定
- 在庫操作のオーバーロード
- 商品カテゴリごとの特別処理

**実装すべきクラス**:

```java
class Product {
    // 複数のコンストラクタパターン:
    // 1. 基本情報のみ (商品名、価格)
    // 2. カテゴリ付き
    // 3. 初期在庫付き
    // 4. 完全スペック付き
    
    // メソッドオーバーロード:
    // addStock(int) / addStock(int, String) / addStock(int, String, Date)
    // removeStock(int) / removeStock(int, String)
}
```

**実行例**:
```
=== 商品在庫管理システム ===

=== 商品登録 ===
✓ ノートPC (基本登録)
  価格: ¥98,000
  カテゴリ: 未設定
  在庫: 0個

✓ マウス (カテゴリ付き登録)
  価格: ¥2,500
  カテゴリ: 周辺機器
  在庫: 0個

✓ キーボード (初期在庫付き登録)
  価格: ¥8,000
  カテゴリ: 周辺機器
  在庫: 50個

✓ モニター (完全スペック登録)
  価格: ¥35,000
  カテゴリ: ディスプレイ
  在庫: 20個
  保証期間: 3年

=== 在庫操作 ===
ノートPCの在庫追加:
- 基本追加: +10個 → 10個
- 備考付き追加: +5個 (新ロット) → 15個
- 詳細追加: +8個 (仕入先: A社, 2024-07-05) → 23個

マウスの在庫出庫:
- 基本出庫: -3個 → 47個
- 備考付き出庫: -2個 (店舗販売) → 45個

=== 在庫レポート ===
商品別在庫状況:
1. ノートPC: 23個 (¥2,254,000)
2. マウス: 45個 (¥112,500)
3. キーボード: 50個 (¥400,000)
4. モニター: 20個 (¥700,000)

カテゴリ別集計:
- 周辺機器: 95個 (¥512,500)
- ディスプレイ: 20個 (¥700,000)
- 未分類: 23個 (¥2,254,000)

総在庫価値: ¥3,466,500
```

**評価ポイント**:
- 柔軟な商品作成方法の提供
- 在庫操作の多様性への対応
- カテゴリ別処理の実装



### 課題3: 図形描画システム
**ファイル名**: `GeometrySystem.java`

様々な図形を作成・管理するシステムを作成してください。

**要求仕様**:
- 図形の多様な作成方法
- 座標系の異なる指定方法
- 図形変換のオーバーロード
- イミュータブル図形クラス

**実装すべきクラス**:

```java
class Point {
    // イミュータブル座標クラス
    // 複数のコンストラクタ:
    // 1. デフォルト原点
    // 2. x, y指定
    // 3. 他の点からのコピー
    // 4. 極座標から変換
}

class Circle {
    // 複数の作成方法:
    // 1. 中心と半径
    // 2. 直径で指定
    // 3. 3点を通る円
    // 4. 他の円からのコピー
    
    // メソッドオーバーロード:
    // move(double, double) / move(Point)
    // scale(double) / scale(double, double)
}
```

**実行例**:
```
=== 図形描画システム ===

=== 座標点作成 ===
✓ 原点: (0.0, 0.0)
✓ 点A: (3.0, 4.0)
✓ 点B: (6.0, 8.0) [点Aから作成]
✓ 点C: (5.0, 0.0) [極座標 r=5, θ=0° から変換]

=== 円作成 ===
✓ 円1: 中心(0,0), 半径5.0
  面積: 78.54, 周囲長: 31.42

✓ 円2: 中心(2,3), 直径10.0
  面積: 78.54, 周囲長: 31.42

✓ 円3: 3点(1,1), (4,1), (2.5,3.5)を通る円
  中心: (2.5, 2.0), 半径: 1.58
  面積: 7.85, 周囲長: 9.93

=== 図形変換 ===
円1の移動:
- ベクトル移動 (2, 3) → 中心: (2.0, 3.0)
- 点指定移動 → 中心: (5.0, 5.0)

円2の拡大縮小:
- 均等拡大 ×1.5 → 半径: 7.5
- 比率拡大 (x×2, y×1.5) → 楕円化

=== 幾何計算 ===
円同士の関係:
円1と円2: 重複あり
円1と円3: 含有関係
円2と円3: 離れている

点と円の関係:
点A (3,4) は円1内: true
点B (6,8) は円2内: false

=== 図形統計 ===
総図形数: 3個
総面積: 165.0
平均半径: 4.0
最大円: 円2 (半径: 7.5)
```

**評価ポイント**:
- イミュータブルオブジェクトの設計
- 幾何学的計算の実装
- 座標系変換の理解
- 図形間の関係性計算

## 実装のヒント

### 課題1のヒント
```java
class BankAccount {
    private String accountNumber;
    private String accountHolder;
    private double balance;
    private AccountType type;
    private double overdraftLimit;
    
    // コンストラクタチェーン
    public BankAccount(String number, String holder) {
        this(number, holder, 0.0);
    }
    
    public BankAccount(String number, String holder, double initialBalance) {
        this(number, holder, initialBalance, AccountType.SAVINGS);
    }
    
    public BankAccount(String number, String holder, double balance, AccountType type) {
        this.accountNumber = number;
        this.accountHolder = holder;
        this.balance = balance;
        this.type = type;
        this.overdraftLimit = (type == AccountType.CHECKING) ? 100000 : 0;
    }
    
    // メソッドオーバーロード
    public boolean deposit(double amount) {
        return deposit(amount, "入金");
    }
    
    public boolean deposit(double amount, String memo) {
        if (amount > 0) {
            this.balance += amount;
            recordTransaction("入金", amount, memo);
            return true;
        }
        return false;
    }
}
```

### 課題2のヒント
```java
class Product {
    private final String productId;
    private final String name;
    private final double price;
    private String category;
    private int stock;
    private ArrayList<StockTransaction> transactions;
    
    // 段階的初期化用のthis()呼び出し
    public Product(String name, double price) {
        this(generateId(), name, price, "未分類", 0);
    }
    
    public Product(String name, double price, String category) {
        this(generateId(), name, price, category, 0);
    }
    
    public Product(String name, double price, String category, int initialStock) {
        this(generateId(), name, price, category, initialStock);
    }
    
    private Product(String id, String name, double price, String category, int stock) {
        this.productId = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.stock = stock;
        this.transactions = new ArrayList<>();
    }
}
```

### 課題3のヒント
```java
public final class Point {
    private final double x;
    private final double y;
    
    public Point() {
        this(0.0, 0.0);
    }
    
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public Point(Point other) {
        this(other.x, other.y);
    }
    
    // 極座標からの変換コンストラクタ
    public Point(double radius, double angleRadians, boolean isPolar) {
        this(radius * Math.cos(angleRadians), radius * Math.sin(angleRadians));
    }
    
    // イミュータブルなので変更メソッドは新しいインスタンスを返す
    public Point move(double dx, double dy) {
        return new Point(this.x + dx, this.y + dy);
    }
}
```

## 応用のポイント

1. **コンストラクタの設計**: 利用者の使いやすさを考慮した多様な初期化方法
2. **thisキーワード**: コードの重複を避け、保守性を向上
3. **オーバーロード**: 同じ操作の異なるバリエーションを提供
4. **イミュータブル設計**: 安全で予測可能なオブジェクト
5. **初期化の順序**: 適切なフィールド初期化とバリデーション

## ○ 完了チェックリスト

- [ ] 課題1: 銀行口座システムが正常に動作する
- [ ] 課題2: 商品在庫管理が実装できた
- [ ] 課題3: 図形描画システムが動作する
- [ ] 複数のコンストラクタが適切に実装されている
- [ ] thisキーワードが効果的に使用されている
- [ ] メソッドオーバーロードが適切に機能している

**次のステップ**: 応用課題が完了したら、challenge/のチャレンジ課題に挑戦してみましょう！