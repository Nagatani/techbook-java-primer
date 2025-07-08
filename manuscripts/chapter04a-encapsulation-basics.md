# 第4章 クラスとインスタンス - Part A: カプセル化の基礎

## 本章の学習目標

### 前提知識
**必須前提**：
- 第3章のオブジェクト指向基礎概念の完全な理解
- 基本的なクラス設計と実装の経験
- カプセル化の実践的な理解

**設計経験前提**：
- 複数のクラスを含むプログラムの作成経験
- オブジェクト間の関係性の実装経験

### 学習目標
**知識理解目標**：
- クラスとインスタンス（オブジェクト）の関係性
- コンストラクタの役割と種類
- インスタンス変数とインスタンスメソッドの概念
- thisキーワードの意味と使用法
- メソッドオーバーロードの概念

**技能習得目標**：
- クラスの基本構造（フィールド、メソッド、コンストラクタ）の実装
- 適切なコンストラクタの定義
- thisキーワードを使った明確なコード記述
- メソッドオーバーロードの効果的な活用
- インスタンス変数の適切な初期化

**設計能力目標**：
- 責任が明確なクラスの設計
- 適切なカプセル化による情報隠蔽
- 使いやすいクラスインターフェイスの設計

**到達レベルの指標**：
- 実世界の概念をクラスとして適切に設計・実装できる
- 複数のコンストラクタを持つクラスが作成できる
- インスタンス変数とメソッドを適切に組み合わせたクラスが実装できる
- 他のクラスから利用しやすいクラスが設計できる

---

## 始めに：クラス設計の実践とカプセル化

前章では、オブジェクト指向プログラミングの基本概念について学習しました。本章では、実践的なクラス設計とカプセル化の技術について詳細に学習します。

クラス設計は、単なるコードの書き方ではありません。データと処理を適切に組み合わせ、保守性と拡張性の高いソフトウェアを構築するための重要な技術です。

## カプセル化とアクセス制御

### カプセル化の基本概念

カプセル化は、関連するデータ（フィールド）と処理（メソッド）を1つのクラスにまとめ、外部から直接アクセスできないよう保護する技術です。これにより、オブジェクトの内部状態を安全に管理できます。

### アクセス修飾子の詳細

Javaのアクセス制御は、カプセル化を実現する上で最も重要な機能の1つです。Javaは4つのアクセス修飾子を提供しており、これらを適切に使い分けることで、クラスの内部実装を隠蔽しながら必要な機能だけを外部に公開できます。各修飾子は、フィールドとメソッドの可視性を段階的に制御し、オブジェクト指向設計の原則である「必要最小限の公開」を実現します。

#### アクセス修飾子の可視性マトリックス

| 修飾子 | 同じクラス | 同じパッケージ | サブクラス（別パッケージ） | それ以外（別パッケージ） | 説明 |
|--------|-----------|---------------|---------------------------|-------------------------|------|
| `private` | ○ | × | × | × | 同じクラス内からのみアクセス可能 |
| (なし)※ | ○ | ○ | × | × | 同じパッケージ内からのみアクセス可能 |
| `protected` | ○ | ○ | ○ | × | 同じパッケージまたはサブクラスからアクセス可能 |
| `public` | ○ | ○ | ○ | ○ | どこからでもアクセス可能 |

※ 修飾子を記述しない場合（デフォルトアクセス、package-privateとも呼ばれる）

#### アクセス修飾子の使い分け原則

1. **`private`を基本とする**: フィールドは原則として`private`にし、必要に応じてgetter/setterを提供
2. **`public`は慎重に**: 外部APIとして公開する必要があるメソッドのみ`public`にする
3. **`protected`の活用**: 継承関係で使用する場合に限定し、設計意図を明確にする
4. **パッケージプライベートの戦略的使用**: 同一パッケージ内での協調動作が必要な場合に使用

#### 各アクセス修飾子の詳細と使用例

**`private`の使用例**：
```java
public class BankAccount {
    private double balance;      // 外部から直接変更不可
    private String accountId;    // 内部管理用ID
    
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;   // 同じクラス内からは可視
            logTransaction();    // プライベートメソッドの呼び出し
        }
    }
    
    private void logTransaction() {  // 内部処理専用メソッド
        // トランザクションログの記録
    }
}
```

**パッケージプライベート（デフォルト）の使用例**：
```java
package com.example.internal;

public class DataProcessor {
    String processId;     // 同じパッケージ内からアクセス可能
    
    void processInternal() {  // パッケージ内協調用メソッド
        // 内部処理
    }
}

// 同じパッケージ内の別クラス
class ProcessorHelper {
    void assist(DataProcessor processor) {
        processor.processId = "PROC-001";     // OK: 同じパッケージ
        processor.processInternal();           // OK: 同じパッケージ
    }
}
```

**`protected`の使用例**：
```java
package com.example.base;

public class Vehicle {
    protected String engineType;     // サブクラスからアクセス可能
    protected int maxSpeed;
    
    protected void startEngine() {   // サブクラスで利用可能
        System.out.println("Engine started: " + engineType);
    }
}

// 別パッケージのサブクラス
package com.example.cars;
import com.example.base.Vehicle;

public class Car extends Vehicle {
    public void initialize() {
        engineType = "V6";          // OK: protected継承
        maxSpeed = 200;             // OK: protected継承
        startEngine();              // OK: protectedメソッド
    }
}
```

**`public`の使用例**：
```java
public class MathUtils {
    public static final double PI = 3.14159;  // 公開定数
    
    public static int add(int a, int b) {     // 公開ユーティリティメソッド
        return a + b;
    }
    
    public boolean isPositive(int number) {   // 公開インスタンスメソッド
        return number > 0;
    }
}
```

### getter/setterメソッドのベストプラクティス

getter/setterメソッド（アクセサメソッドとも呼ばれます）は、カプセル化の実装において中心的な役割を果たします。単にフィールドの値を取得・設定するだけでなく、データの整合性を保証し、将来の変更に対する柔軟性を提供します。以下の例では、プライベートフィールドへの安全なアクセスを提供する標準的なパターンを示します：

```java
public class Product {
    private String name;
    private double price;
    
    // getter：値を取得
    public String getName() {
        return name;
    }
    
    public double getPrice() {
        return price;
    }
    
    // setter：データ検証付きで値を設定
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("商品名は必須です");
        }
        this.name = name;
    }
    
    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("価格は負の値にできません");
        }
        this.price = price;
    }
}
```

### データ検証の重要性

オブジェクト指向プログラミングにおいて、オブジェクトの状態を常に有効に保つことは極めて重要です。setterメソッドは単なる値の代入ではなく、オブジェクトの不変条件（invariant）を守るゲートキーパーとしての役割を担います。適切なデータ検証を実装することで、バグの早期発見と予防が可能になり、システム全体の信頼性が向上します。以下の例では、実務でよく使用される検証パターンを示します：

```java
public class Employee {
    private String name;
    private int age;
    private double salary;
    
    public void setAge(int age) {
        if (age < 18 || age > 100) {
            throw new IllegalArgumentException("年齢は18歳以上100歳以下で入力してください");
        }
        this.age = age;
    }
    
    public void setSalary(double salary) {
        if (salary < 0) {
            throw new IllegalArgumentException("給与は負の値にできません");
        }
        this.salary = salary;
    }
}
```

## 設計原則とソフトウェアアーキテクチャ

オブジェクト指向設計では、SOLID原則と呼ばれる5つの基本原則があります。中でも単一責任原則（クラスは1つの責任のみを持つべき）と開放閉鎖原則（拡張に開かれ、修正に閉じているべき）は特に重要です。

カプセル化は単にデータを隠す技術ではなく、変更の影響を局所化し、再利用性とテスト容易性を高める重要な設計技術です。

**SOLID原則の詳細、情報隠蔽の深い意味、契約による設計については、付録B.05「ソフトウェア設計原則」を参照してください。**

## 実践的なクラス設計例

### 銀行口座クラスの設計

カプセル化とアクセス制御を活用した実践的な例：

```java
public class BankAccount {
    // プライベートフィールド：外部から直接変更不可
    private String accountNumber;
    private String accountHolder;
    private double balance;
    private boolean isActive;
    
    // コンストラクタ：初期化時のデータ検証
    public BankAccount(String accountNumber, String accountHolder, double initialBalance) {
        setAccountNumber(accountNumber);
        setAccountHolder(accountHolder);
        setBalance(initialBalance);
        this.isActive = true;
    }
    
    // パブリックメソッド：外部インターフェイス
    public void deposit(double amount) {
        validateAccount();
        if (amount <= 0) {
            throw new IllegalArgumentException("入金額は正の値である必要があります");
        }
        balance += amount;
    }
    
    public void withdraw(double amount) {
        validateAccount();
        if (amount <= 0) {
            throw new IllegalArgumentException("出金額は正の値である必要があります");
        }
        if (balance < amount) {
            throw new IllegalArgumentException("残高不足です");
        }
        balance -= amount;
    }
    
    // プライベートメソッド：内部ロジック
    private void validateAccount() {
        if (!isActive) {
            throw new IllegalStateException("この口座は無効です");
        }
    }
    
    // getter/setterメソッド
    public String getAccountNumber() {
        return accountNumber;
    }
    
    private void setAccountNumber(String accountNumber) {
        if (accountNumber == null || !accountNumber.matches("\\d{10}")) {
            throw new IllegalArgumentException("口座番号は10桁の数字である必要があります");
        }
        this.accountNumber = accountNumber;
    }
    
    public double getBalance() {
        validateAccount();
        return balance;
    }
    
    private void setBalance(double balance) {
        if (balance < 0) {
            throw new IllegalArgumentException("残高は負の値にできません");
        }
        this.balance = balance;
    }
}
```

### クラス設計のベストプラクティス

優れたクラス設計は、ソフトウェアの品質を決定づける重要な要素です。オブジェクト指向設計の長年の実践から生まれたベストプラクティスは、保守性、拡張性、信頼性の高いソフトウェアを構築するための指針となります。

最も基本的な原則は**単一責任の原則**です。1つのクラスは1つの明確な責任のみを持つべきであり、これにより変更の理由が限定され、クラスの理解と保守が容易になります。次に重要なのは**データの隠蔽**で、クラスの内部状態をprivateで保護し、必要な操作のみをパブリックメソッドとして公開します。これにより、実装の詳細を変更してもクライアントコードに影響を与えません。

**入力検証**も欠かせない要素です。すべての外部入力は、setterメソッドやコンストラクタで厳密に検証し、不正なデータがオブジェクトの状態を破壊することを防ぎます。また、**意味のある名前**を使用することで、コードの可読性と保守性が大幅に向上します。クラス名は名詞、メソッド名は動詞で始まるという慣習に従い、その役割を明確に表現する名前を選びます。

最後に、**不変条件の維持**は、オブジェクトの整合性を保証する上で極めて重要です。オブジェクトの生成から破棄まで、常に満たされるべき条件（不変条件）を明確に定義し、すべての操作でこれを維持するよう設計します。

これらの原則に従うことで、保守性が高く、バグの少ないJavaプログラムを作成できます。

## パッケージシステムとクラスの組織化

### パッケージの概念と必要性

Javaにおけるパッケージは、関連するクラスやインターフェースをグループ化するための仕組みです。大規模なプロジェクトでは何百、何千ものクラスが存在することがあり、適切な組織化なしには管理が困難になります。パッケージは以下の重要な役割を果たします：

1. **名前空間の提供**: 異なるパッケージに同じ名前のクラスを作成可能
2. **アクセス制御**: パッケージレベルでのアクセス制限を実現
3. **論理的な構造化**: 機能や責任に基づいてクラスを整理
4. **クラスの再利用**: パッケージ単位での配布と利用

### パッケージの命名規則

Javaの言語仕様では、パッケージ名の衝突を避けるため、インターネットドメイン名を逆順にした命名規則が推奨されています：

```java
// ドメイン名: example.com
// パッケージ名: com.example.プロジェクト名.モジュール名
package com.example.myapp.service;

// ドメイン名: ac.jp（教育機関）
// パッケージ名: jp.ac.大学名.プロジェクト名
package jp.ac.university.research;
```

**命名規則のポイント**：
- すべて小文字を使用
- ドメイン名を逆順に記述
- 意味のある階層構造を作成
- Javaの予約語は使用しない

### パッケージとディレクトリ構造

パッケージ名はファイルシステムのディレクトリ構造と対応している必要があります：

```
src/
└── com/
    └── example/
        └── myapp/
            ├── model/
            │   ├── User.java
            │   └── Product.java
            ├── service/
            │   ├── UserService.java
            │   └── ProductService.java
            └── util/
                └── DateUtils.java
```

### import文の使い方

パッケージに含まれるクラスを使用する際は、完全限定名かimport文を使用します：

```java
// 完全限定名での使用
java.util.List<String> names = new java.util.ArrayList<>();

// import文を使用した場合
import java.util.List;
import java.util.ArrayList;

List<String> names = new ArrayList<>();
```

**import文の種類**：

1. **単一型インポート**：
```java
import java.util.Scanner;  // Scannerクラスのみインポート
```

2. **オンデマンドインポート**：
```java
import java.util.*;  // java.utilパッケージのすべてのクラスをインポート
```

3. **静的インポート**：
```java
import static java.lang.Math.PI;
import static java.lang.Math.sqrt;

double circumference = 2 * PI * radius;  // Math.PI と書く必要がない
double result = sqrt(16);                 // Math.sqrt と書く必要がない
```

### import文の注意点

異なるパッケージに同名のクラスが存在する場合、明示的な指定が必要です：

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

### パッケージ構成のベストプラクティス

効果的なパッケージ構成は、プロジェクトの保守性と拡張性を大きく向上させます：

```java
com.example.myapp/
├── model/          // ドメインモデル（エンティティ）
├── service/        // ビジネスロジック
├── repository/     // データアクセス層
├── controller/     // UIコントローラ
├── util/          // ユーティリティクラス
└── exception/     // カスタム例外
```

**設計原則**：
- 機能的凝集性: 関連する機能を同じパッケージに
- 循環依存の回避: パッケージ間の依存関係を一方向に
- 適切な粒度: 大きすぎず小さすぎないパッケージサイズ
- 明確な責任: 各パッケージの役割を明確に定義

これらの原則に従うことで、保守性が高く、バグの少ないJavaプログラムを作成できます。次の章では、これらのクラスを組み合わせて、より複雑なオブジェクト指向設計を学習していきます。

本章を通じて、Javaという言語の持つ型システムの力強さと精密さを理解し、現代のソフトウェア開発者として必要な基礎的な素養を身につけていきましょう。

---

次のパート：[Part B - コンストラクタとthisキーワード](chapter04b-constructors-this.md)