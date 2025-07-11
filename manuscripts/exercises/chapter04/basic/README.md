# 第4章 基礎課題：カプセル化の実践

## 概要

本課題では、カプセル化の基本概念を実践的に学習します。適切なアクセス修飾子の使用、getter/setterパターンの実装、データ検証の実装を通じて、安全で保守性の高いクラス設計を身につけます。

## 学習目標

- privateフィールドとpublicメソッドによるカプセル化の実装
- getter/setterメソッドでのデータ検証の実装
- 適切なアクセス修飾子の選択と使用
- コンストラクタでの初期化とバリデーション

## 課題1：書籍管理クラス（Book）

### 要求仕様

以下の要件を満たす`Book`クラスを実装してください：

1. **フィールド（すべてprivate）**：
   - `title` (String): 書籍のタイトル
   - `author` (String): 著者名
   - `isbn` (String): ISBN番号（13桁）
   - `price` (int): 価格（円）
   - `publishYear` (int): 出版年

2. **コンストラクタ**：
   - すべてのフィールドを初期化するコンストラクタ
   - 各フィールドの妥当性を検証

3. **getter/setterメソッド**：
   - すべてのフィールドに対してgetterメソッドを実装
   - 価格と出版年に対してのみsetterメソッドを実装（他は不変）
   - setterメソッドでは適切なバリデーションを実施

4. **バリデーションルール**：
   - タイトル：null不可、空文字不可
   - 著者名：null不可、空文字不可
   - ISBN：null不可、13桁の数字のみ
   - 価格：0円以上
   - 出版年：1900年以降、現在の年以下

### 実装のヒント

```java
public class Book {
    private String title;
    // その他のフィールド...
    
    public Book(String title, String author, String isbn, int price, int publishYear) {
        // setterメソッドを使って初期化することで、バリデーションを共通化
        setTitle(title);
        // ...
    }
    
    private void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("タイトルは必須です");
        }
        this.title = title.trim();
    }
    
    // ISBNの検証例
    private void validateIsbn(String isbn) {
        if (isbn == null || !isbn.matches("\\d{13}")) {
            throw new IllegalArgumentException("ISBNは13桁の数字である必要があります");
        }
    }
}
```

### テストケース

以下のテストケースが正しく動作することを確認してください：

```java
public class BookTest {
    public static void main(String[] args) {
        // 正常なケース
        Book book1 = new Book("Java入門", "山田太郎", "9784123456789", 3000, 2024);
        System.out.println("書籍1: " + book1.getTitle() + " by " + book1.getAuthor());
        
        // 価格の変更
        book1.setPrice(2800);
        System.out.println("新価格: " + book1.getPrice() + "円");
        
        // 異常なケース（例外が発生すべき）
        try {
            Book book2 = new Book("", "著者", "9784123456789", 3000, 2024);
        } catch (IllegalArgumentException e) {
            System.out.println("期待通りの例外: " + e.getMessage());
        }
        
        try {
            book1.setPrice(-100);
        } catch (IllegalArgumentException e) {
            System.out.println("期待通りの例外: " + e.getMessage());
        }
    }
}
```

## 課題2：商品管理クラス（Product）

### 要求仕様

ECサイトの商品を管理する`Product`クラスを実装してください：

1. **フィールド（すべてprivate）**：
   - `productId` (String): 商品ID（変更不可）
   - `name` (String): 商品名
   - `description` (String): 商品説明
   - `price` (int): 価格（円）
   - `stock` (int): 在庫数
   - `category` (String): カテゴリ

2. **メソッド**：
   - 標準的なgetter/setter
   - `purchase(int quantity)`: 購入処理（在庫を減らす）
   - `restock(int quantity)`: 入荷処理（在庫を増やす）
   - `applyDiscount(double percentage)`: 割引適用
   - `isAvailable()`: 在庫があるかどうか

3. **ビジネスルール**：
   - 商品IDは一度設定したら変更不可
   - 在庫数は0以上
   - 購入時は在庫数を超えて購入できない
   - 割引率は0〜90%の範囲

### 実装のヒント

```java
public class Product {
    private final String productId; // finalで不変性を保証
    private String name;
    private int stock;
    // その他のフィールド...
    
    public boolean purchase(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("購入数は1以上である必要があります");
        }
        if (quantity > stock) {
            return false; // 在庫不足
        }
        stock -= quantity;
        return true;
    }
    
    // 割引適用の例
    public void applyDiscount(double percentage) {
        if (percentage < 0 || percentage > 90) {
            throw new IllegalArgumentException("割引率は0-90%の範囲で指定してください");
        }
        price = (int)(price * (1 - percentage / 100));
    }
}
```

## 課題3：銀行口座クラス（BankAccount）の改良

### 要求仕様

第3章で学習したBankAccountクラスをさらに改良してください：

1. **追加機能**：
   - 月間取引回数の制限（ATM利用は月10回まで無料、以降は手数料）
   - 最小残高の設定（普通預金は0円、当座預金は10万円など）
   - 取引履歴の詳細記録（日時、取引種別、金額、残高）

2. **アクセス制御の改善**：
   - パッケージ構造を導入（`com.example.bank`）
   - 内部処理用のprivateメソッドの追加
   - protectedメソッドによる継承時の拡張ポイント

### 実装例の構造

```java
package com.example.bank;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BankAccount {
    private final String accountNumber;
    private double balance;
    private final double minimumBalance;
    private int monthlyTransactionCount;
    private final List<Transaction> transactionHistory;
    
    // 内部クラスで取引記録を管理
    private static class Transaction {
        final LocalDateTime timestamp;
        final String type;
        final double amount;
        final double balanceAfter;
        
        Transaction(String type, double amount, double balanceAfter) {
            this.timestamp = LocalDateTime.now();
            this.type = type;
            this.amount = amount;
            this.balanceAfter = balanceAfter;
        }
    }
    
    // protectedメソッドで継承時の拡張を許可
    protected boolean canWithdraw(double amount) {
        return balance - amount >= minimumBalance;
    }
    
    // privateメソッドで内部処理を隠蔽
    private void recordTransaction(String type, double amount) {
        transactionHistory.add(new Transaction(type, amount, balance));
    }
}
```

## 評価ポイント

1. **カプセル化の適切な実装**（40点）
   - すべてのフィールドがprivateで宣言されている
   - 必要なgetter/setterが適切に実装されている
   - 不変であるべきフィールドにsetterが提供されていない

2. **データ検証の実装**（30点）
   - すべての入力値が適切に検証されている
   - エラーメッセージが分かりやすい
   - 境界値（0、負の値、最大値など）が正しく処理される

3. **ビジネスロジックの実装**（20点）
   - 要求された機能が正しく動作する
   - メソッドの責任が明確で単一責任の原則に従っている

4. **コードの品質**（10点）
   - 命名が適切で読みやすい
   - コメントが適切に記述されている
   - コードの重複がない

## 発展学習の提案

1. **デザインパターンの適用**：
   - Builderパターンを使った複雑なオブジェクトの生成
   - Factoryパターンを使った商品タイプ別のインスタンス生成

2. **例外処理の改善**：
   - カスタム例外クラスの作成
   - 例外の階層化による詳細なエラー処理

3. **パフォーマンスの考慮**：
   - 頻繁にアクセスされる計算結果のキャッシング
   - 遅延初期化の実装

4. **テストの作成**：
   - JUnitを使った単体テストの作成
   - 境界値テストやエラーケースのテスト

これらの基礎課題を通じて、カプセル化の重要性と実装方法を実践的に学習してください。