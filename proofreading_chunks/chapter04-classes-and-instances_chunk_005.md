<!-- 
校正チャンク情報
================
元ファイル: chapter04-classes-and-instances.md
チャンク: 5/10
行範囲: 854 - 1053
作成日時: 2025-08-03 02:32:41

校正時の注意事項:
- 文章の流れは前後のチャンクを考慮してください
- このヘッダーとフッターは校正対象外です
- 校正が完了したらステータスを「completed」に変更してください
================
-->

これはimport文の使用例です。

import文の種類。

1. 単一型インポート。
<span class="listing-number">**サンプルコード4-24**</span>

```java
import java.util.Scanner;  // Scannerクラスのみインポート
```

これは単一型インポートの例です。

2. オンデマンドインポート。
<span class="listing-number">**サンプルコード4-25**</span>

```java
import java.util.*;  // java.utilパッケージのすべてのクラスをインポート
```

これはオンデマンドインポートの例です。

3. 静的インポート。
<span class="listing-number">**サンプルコード4-14**</span>

```java
import static java.lang.Math.PI;
import static java.lang.Math.sqrt;

double circumference = 2 * PI * radius;  // Math.PI と書く必要がない
double result = sqrt(16);                 // Math.sqrt と書く必要がない
```

これはstatic importの使用例です。

### import文の注意点

異なるパッケージに同名のクラスが存在する場合、明示的な指定が必要です。

<span class="listing-number">**サンプルコード4-15**</span>

```java
import java.util.*;
import java.awt.*;

public class Example {
    // コンパイルエラー: List はどちらのパッケージか不明
    // List myList;  
    
    // 解決策1: 完全限定名を使用
    java.util.List<String> utilList;
    java.awt.List awtList;
    
    // 解決策2: 片方のみimport
    // import java.util.List;
    // List<String> utilList;  // java.util.List
    // java.awt.List awtList;  // 完全限定名
}
```

これは同名クラスの競合解決の例です。

### パッケージ構成のベストプラクティス

効果的なパッケージ構成は、プロジェクトの保守性と拡張性を大きく向上させます。

<span class="listing-number">**サンプルコード4-16**</span>

```java
com.example.myapp/
├── model/          // ドメインモデル（エンティティ）
├── service/        // ビジネスロジック（処理の中核となる業務ルール）
├── repository/     // データアクセス層
├── controller/     // UIコントローラ
├── util/          // ユーティリティクラス
└── exception/     // カスタム例外
```

これはパッケージ構造の例です。

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

#### 使用例と実行結果

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

#### 実行結果

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


<!-- 
================
チャンク 5/10 の終了
校正ステータス: [ ] 未完了 / [ ] 完了
================
-->
