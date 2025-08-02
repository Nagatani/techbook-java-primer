<!-- 
校正チャンク情報
================
元ファイル: chapter04-classes-and-instances.md
チャンク: 6/11
行範囲: 934 - 1107
作成日時: 2025-08-02 23:30:11

校正時の注意事項:
- 文章の流れは前後のチャンクを考慮してください
- このヘッダーとフッターは校正対象外です
- 校正が完了したらステータスを「completed」に変更してください
================
-->

#### 設計原則
- 機能的凝集性
-    + 関連する機能を同じパッケージに
- 循環依存の回避
-    + AがBを使い、BがAを使うような相互依存を避ける
- 推奨される粒度
-    + 1パッケージに5～20クラス程度を目安とする
- 明確な責任
-    + 各パッケージがデータアクセス、ビジネスロジック、UIなどの役割を明確に持つ

これらの原則に従うことで、保守性が高く、バグの少ないJavaプログラムを作成できます。

## コンストラクタとthisキーワード

### 高度なコンストラクタ設計

第3章ではコンストラクタの基本的な書き方を学びました。本章では、実践的なアプリケーション開発で必要となる、より高度なコンストラクタ設計パターンを学習します。

#### コンストラクタでのバリデーション

実務では、コンストラクタでの入力検証が重要です。不正な状態のオブジェクトが作られることを防ぎます。

<span class="listing-number">**サンプルコード4-17**</span>

```java
public class User {
    private final String email;
    private final String username;
    private final int age;
    
    public User(String email, String username, int age) {
        // メールアドレスの検証
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("有効なメールアドレスが必要です");
        }
        
        // ユーザー名の検証
        if (username == null || username.trim().length() < 3) {
            throw new IllegalArgumentException("ユーザー名は3文字以上である必要があります");
        }
        
        // 年齢の検証
        if (age < 0 || age > 150) {
            throw new IllegalArgumentException("年齢は0歳以上150歳以下である必要があります");
        }
        
        this.email = email;
        this.username = username.trim();
        this.age = age;
    }
}
```

使用例と実行結果：
```java
// UserTest.java
public class UserTest {
    public static void main(String[] args) {
        try {
            // 正常なユーザー作成
            User user1 = new User("user@example.com", "john_doe", 25);
            System.out.println("ユーザー作成成功");
            
            // 不正なメールアドレス
            User user2 = new User("invalid-email", "jane_doe", 30);
        } catch (IllegalArgumentException e) {
            System.out.println("エラー: " + e.getMessage());
        }
        
        try {
            // 短すぎるユーザー名
            User user3 = new User("test@example.com", "ab", 20);
        } catch (IllegalArgumentException e) {
            System.out.println("エラー: " + e.getMessage());
        }
    }
}
```

実行結果：
```
ユーザー作成成功
エラー: 有効なメールアドレスが必要です
エラー: ユーザー名は3文字以上である必要があります
```

#### 複数のコンストラクタ（オーバーロード）

クラスには複数のコンストラクタを定義できます。これをコンストラクタオーバーロードといいます。

<span class="listing-number">**サンプルコード4-18**</span>

```java
public class Book {
    private String title;
    private String author;
    private int pages;
    private double price;
    
    // コンストラクタ1：すべてのフィールドを初期化
    public Book(String title, String author, int pages, double price) {
        this.title = title;
        this.author = author;
        this.pages = pages;
        this.price = price;
    }
    
    // コンストラクタ2：必須フィールドのみ
    public Book(String title, String author) {
        this(title, author, 0, 0.0);  // 他のコンストラクタを呼び出す
    }
    
    // コンストラクタ3：タイトルのみ
    public Book(String title) {
        this(title, "Unknown", 0, 0.0);
    }
}
```

これはコンストラクタオーバーロードの例です。

### thisキーワードの高度な活用

第3章では`this`の基本的な使い方を学びました。ここでは、より実践的で高度な`this`の活用パターンを学習します。

#### ビルダーパターンでの活用

複雑なオブジェクト構築で、`this`を返すことで流暢なインターフェイスを実現できます。

<span class="listing-number">**サンプルコード4-19**</span>

```java
public class EmailBuilder {
    private String to;
    private String subject;
    private String body;
    private boolean isHtml = false;
    
    public EmailBuilder to(String to) {
        this.to = to;
        return this;  // メソッドチェーンを可能にする
    }
    
    public EmailBuilder subject(String subject) {
        this.subject = subject;
        return this;
    }
    
    public EmailBuilder body(String body) {
        this.body = body;
        return this;
    }
    
    public EmailBuilder asHtml() {
        this.isHtml = true;
        return this;
    }
    
    public Email build() {
        return new Email(to, subject, body, isHtml);
    }
}

// 使用例：流暢なインターフェイス
Email email = new EmailBuilder()
    .to("user@example.com")
    .subject("重要なお知らせ")
    .body("<h1>こんにちは</h1>")
    .asHtml()
    .build();
```

これはビルダーパターンを使った流暢なインターフェイスの例です。



<!-- 
================
チャンク 6/11 の終了
校正ステータス: [ ] 未完了 / [ ] 完了
================
-->
