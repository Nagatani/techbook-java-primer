# 第7章 基礎課題：抽象クラスとインターフェイス

## 課題概要

本章で学んだ抽象クラスとインターフェイスの概念を実践的に理解するための基礎課題です。図形システムの実装を通じて、抽象化の重要性と使い分けを学びます。

## 課題1：図形システムの実装

### 目的
- 抽象クラスを使った共通機能の実装
- インターフェイスを使った機能の付与
- 継承とポリモーフィズムの活用

### 要求仕様

1. **抽象クラス`Shape`の作成**
   - フィールド：`name`（図形の名前）
   - 抽象メソッド：`getArea()`（面積を返す）、`getPerimeter()`（周長を返す）
   - 具象メソッド：`display()`（図形情報を表示）

2. **インターフェイスの作成**
   - `Drawable`：`draw()`メソッドを持つ
   - `Resizable`：`resize(double factor)`メソッドを持つ

3. **具象クラスの実装**
   - `Circle`：円（半径を持つ）
   - `Rectangle`：長方形（幅と高さを持つ）
   - `Square`：正方形（一辺の長さを持つ）

### 実装のヒント
```java
public abstract class Shape {
    protected String name;
    
    public Shape(String name) {
        this.name = name;
    }
    
    public abstract double getArea();
    public abstract double getPerimeter();
    
    public void display() {
        System.out.println("図形: " + name);
        System.out.println("面積: " + getArea());
        System.out.println("周長: " + getPerimeter());
    }
}
```

### 評価ポイント
- 抽象クラスの適切な設計
- インターフェイスの実装
- 継承関係の正しい理解
- メソッドのオーバーライド

## 課題2：決済システムの実装

### 目的
- インターフェイスを使った戦略パターンの実装
- 複数インターフェイスの実装
- defaultメソッドの活用

### 要求仕様

1. **インターフェイス`PaymentMethod`の作成**
   - `processPayment(double amount)`：支払い処理
   - `default boolean isAvailable()`：利用可能かどうか（デフォルトtrue）

2. **追加インターフェイス**
   - `Refundable`：`refund(double amount)`メソッドを持つ
   - `Chargeable`：`getTransactionFee(double amount)`メソッドを持つ

3. **実装クラス**
   - `CreditCard`：クレジットカード決済（RefundableとChargeable実装）
   - `BankTransfer`：銀行振込（Chargeableのみ実装）
   - `CashPayment`：現金払い（基本機能のみ）

### 実装のヒント
```java
public interface PaymentMethod {
    void processPayment(double amount);
    
    default boolean isAvailable() {
        return true;
    }
    
    default void printReceipt(double amount) {
        System.out.println("領収書: " + amount + "円");
    }
}
```

### 評価ポイント
- インターフェイスの適切な設計
- 複数インターフェイスの実装
- defaultメソッドの理解
- 戦略パターンの実装

## 課題3：動物行動システム

### 目的
- 抽象クラスとインターフェイスの組み合わせ
- is-a関係とcan-do関係の理解
- テンプレートメソッドパターンの実装

### 要求仕様

1. **抽象クラス`Animal`**
   - フィールド：`name`、`age`
   - 抽象メソッド：`makeSound()`
   - テンプレートメソッド：`dailyRoutine()`（起床→食事→活動→就寝）

2. **インターフェイス**
   - `Flyable`：`fly()`メソッド
   - `Swimmable`：`swim()`メソッド
   - `Trainable`：`learn(String trick)`メソッド

3. **実装クラス**
   - `Dog`：Trainable、Swimmable
   - `Bird`：Flyable
   - `Fish`：Swimmable
   - `Dolphin`：Swimmable、Trainable

### 実装のヒント
```java
public abstract class Animal {
    protected String name;
    protected int age;
    
    // テンプレートメソッド
    public final void dailyRoutine() {
        wakeUp();
        eat();
        performActivity();
        sleep();
    }
    
    private void wakeUp() {
        System.out.println(name + "が起きました");
    }
    
    protected abstract void eat();
    protected abstract void performActivity();
    
    private void sleep() {
        System.out.println(name + "が眠りました");
    }
}
```

### 評価ポイント
- テンプレートメソッドパターンの理解
- 抽象クラスとインターフェイスの使い分け
- 適切な責務の分離
- ポリモーフィズムの活用

## 課題4：プラグインシステムの実装

### 目的
- インターフェイスを使った拡張可能な設計
- Java 8以降の機能（defaultメソッド、staticメソッド）の活用
- プラグインアーキテクチャの理解

### 要求仕様

1. **インターフェイス`Plugin`**
   - `getName()`：プラグイン名を返す
   - `execute()`：プラグインの実行
   - `default getVersion()`：バージョン情報（デフォルト"1.0.0"）
   - `static Plugin loadPlugin(String name)`：プラグインのファクトリメソッド

2. **管理インターフェイス**
   - `Configurable`：`configure(Map<String, String> config)`
   - `Lifecycle`：`initialize()`、`shutdown()`

3. **実装例**
   - `LoggerPlugin`：ログ出力プラグイン
   - `DatabasePlugin`：データベース接続プラグイン（Configurable、Lifecycle実装）
   - `EmailPlugin`：メール送信プラグイン（Configurable実装）

### 評価ポイント
- 拡張可能な設計
- defaultメソッドの効果的な使用
- インターフェイスの分離原則
- ファクトリパターンの理解

## 提出方法

1. 各課題のソースコードを作成
2. 動作確認用のMainクラスを作成
3. 実行結果をコメントで記載
4. 設計の意図や工夫点を説明

## 発展学習の提案

- **デザインパターン**：抽象ファクトリパターン、ブリッジパターンなどの学習
- **SOLID原則**：特にインターフェイス分離原則と依存性逆転原則の深い理解
- **Java 9以降の機能**：インターフェイスのprivateメソッドの活用
- **実践的な応用**：フレームワーク（Spring等）でのインターフェイス活用事例の研究

## 参考リソース

- Oracle公式ドキュメント：Abstract Classes and Interfaces
- Effective Java（Joshua Bloch）：項目20-24
- デザインパターン入門（結城浩）