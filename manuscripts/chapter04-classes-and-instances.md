# 第4章 クラスとインスタンス

## 本章の学習目標

### 前提知識

本章を学習する前に、第3章で学んだオブジェクト指向の基礎概念を理解していることがポイントです。特に、クラスが「設計図」であり、オブジェクトがその「実体」であるという関係性、そしてカプセル化によるデータ保護の意義を把握していると、学習がスムーズに進みます。

また、基本的なクラスの設計と実装の経験があるとよいでしょう。これには、複数のクラスを含むプログラムの作成経験や、オブジェクト間の関係性を実装した経験が含まれます。これらの経験があることで、本章で扱うより高度な設計技法をスムーズに理解できます。

### 学習目標

本章では、Javaにおける「クラスとインスタンス」の詳細なメカニズムを学習します。まず、知識理解の面では、クラスとインスタンス（オブジェクト）の関係性を深く理解し、コンストラクタがオブジェクト生成時に果たす役割とその種類を学びます。また、インスタンス変数とインスタンスメソッドがどのように連携してオブジェクトの状態と振る舞いを実現するかを理解します。`this`キーワードの意味と使用法、そしてメソッドオーバーロードの概念も重要な学習ポイントです。

技能習得の面では、クラスの基本構造であるフィールド、メソッド、コンストラクタを実際に実装できるようになることを目指します。特に、適切なコンストラクタの定義方法、`this`キーワードを使った明確なコード記述、メソッドオーバーロードの効果的な活用方法を身につけます。インスタンス変数の適切な初期化方法も、安全なプログラムを書く上で大切なスキルです。

さらに、設計能力の観点からは、責任が明確なクラスの設計方法を学びます。適切なカプセル化による情報隠蔽を実現し、ほかの開発者が使いやすいクラスインターフェイスを設計する能力を養います。

最終的には、実世界の概念をクラスとして適切にモデル化し、複数のコンストラクタを持つ柔軟なクラスを作成できます。インスタンス変数とメソッドを適切に組み合わせ、ほかのクラスから利用しやすい、保守性の高いクラスを設計・実装できるようになることが、本章の最終的な到達目標です。



## 章の構成

本章では、以下の内容を学習します：

1. **カプセル化とアクセス制御**
   - カプセル化の基本概念
   - アクセス修飾子の詳細（public、private、protected、パッケージプライベート）
   - データ保護の重要性

2. **実践的なクラス設計**
   - 銀行口座クラスの段階的改善（V0→V1→V2→V3）
   - getter/setterメソッドのベストプラクティス
   - データ検証とバリデーション

3. **コンストラクタとオブジェクト生成**
   - コンストラクタの役割と種類
   - thisキーワードの活用
   - オーバーロードによる柔軟な初期化

4. **パッケージシステム**
   - パッケージの概念と命名規則
   - import文の使い方
   - クラスの組織化

## はじめに：なぜカプセル化が重要なのか

### 銀行口座クラスの復習と発展

第3章では基本的なクラスの作り方を学習しました。本章では、実践的なカプセル化の技術を、銀行口座クラスの段階的な改善を通じて学んでいきます。

まずは、カプセル化を適用していない問題のあるクラスから始めて、段階的に改善していく過程を見ていきましょう：

<span class="listing-number">**サンプルコード4-1**</span>

```java
// 初期バージョン：カプセル化なし（問題のある設計）
public class BankAccountV0 {
    public String accountNumber;
    public double balance;
    
    public BankAccountV0(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }
}

// 使用例
public class ProblemDemo {
    public static void main(String[] args) {
        BankAccountV0 account = new BankAccountV0("123456", 10000);
        
        // 問題1: 直接残高を変更できてしまう
        account.balance = -1000;  // 不正な値の設定が可能
        
        // 問題2: 口座番号を後から変更できてしまう
        account.accountNumber = "999999";  // 口座番号の改ざん
        
        // 問題3: 取引履歴が残らない
        account.balance += 5000;  // 入金の記録がない
    }
}
```

**このコードの問題点**：
- **カプセル化の欠如**: すべてのフィールドがpublicで、外部から自由にアクセス・変更可能
- **データの整合性**: 不正な値（負の残高など）を防ぐ仕組みがない
- **変更の追跡**: 誰がいつ値を変更したか記録できない
- **ビジネスルールの実装場所**: 入出金のルールをクラス外部で実装する必要がある

これらの問題を解決するために、カプセル化という技術を使います。本章では、このBankAccountクラスを段階的に改善しながら、実践的なカプセル化の技術を身につけていきます。

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
<span class="listing-number">**サンプルコード4-2**</span>

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
<span class="listing-number">**サンプルコード4-3**</span>

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
<span class="listing-number">**サンプルコード4-4**</span>

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
<span class="listing-number">**サンプルコード4-5**</span>

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

<span class="listing-number">**サンプルコード4-6**</span>

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

プログラミングにおいて、データの状態を常に有効に保つことは極めて重要です。オブジェクト指向では、これをカプセル化とsetterメソッドによる検証で実現します。setterメソッドは単なる値の代入ではなく、オブジェクトの不変条件（invariant）を守るゲートキーパーとしての役割を担います。適切なデータ検証を実装することで、バグの早期発見と予防が可能になり、システム全体の信頼性が向上します。以下の例では、実務でよく使用される検証パターンを示します：

<span class="listing-number">**サンプルコード4-7**</span>

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

ソフトウェア設計では、SOLID原則と呼ばれる5つの基本原則があります。これらはオブジェクト指向設計の文脈で生まれましたが、単一責任原則（モジュールは1つの責任のみを持つべき）や開放閉鎖原則（拡張に開かれ、修正に閉じているべき）などは、他のプログラミングパラダイムでも応用可能な普遍的な原則です。

カプセル化は単にデータを隠す技術ではなく、変更の影響を局所化し、再利用性とテスト容易性を高める重要な設計技術です。

## 実践的なクラス設計例

### 銀行口座クラスの段階的な改善

先ほどの問題のあるBankAccountV0を、カプセル化の原則に従って段階的に改善していきましょう。

#### ステップ1: 基本的なカプセル化（BankAccountV1）

まず、フィールドをprivateにして、publicメソッドを通じてのみアクセスできるようにします：

<span class="listing-number">**サンプルコード4-8**</span>

```java
public class BankAccountV1 {
    private String accountNumber;
    private double balance;
    
    public BankAccountV1(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }
    
    public void deposit(double amount) {
        balance += amount;
    }
    
    public void withdraw(double amount) {
        balance -= amount;
    }
    
    public double getBalance() {
        return balance;
    }
}
```

**改善点**：
- フィールドがprivateになり、直接アクセスできない
- メソッドを通じてのみ操作可能

**残る問題**：
- 負の金額でも入金・出金できてしまう
- 残高不足でも出金できてしまう
- 初期残高が負の値でも設定できる

#### ステップ2: 基本的な検証を追加（BankAccountV2）

<span class="listing-number">**サンプルコード4-9**</span>

```java
public class BankAccountV2 {
    private String accountNumber;
    private double balance;
    
    public BankAccountV2(String accountNumber, double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("初期残高は0以上である必要があります");
        }
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }
    
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("入金額は正の値である必要があります");
        }
        balance += amount;
    }
    
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            return false;
        }
        if (balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }
    
    public double getBalance() {
        return balance;
    }
}
```

**改善点**：
- コンストラクタで初期値を検証
- 入金時に金額を検証
- 出金時に残高と金額を確認

**残る問題**：
- 口座番号が後から変更可能（getterがない）
- 取引履歴が残らない
- 口座番号の検証がない

#### ステップ3: 完全なカプセル化（BankAccountV3）

<span class="listing-number">**サンプルコード4-10**</span>

```java
import java.util.*;

public class BankAccountV3 {
    private final String accountNumber;  // finalで不変にする
    private double balance;
    private List<String> transactionHistory;
    
    public BankAccountV3(String accountNumber, double initialBalance) {
        // 口座番号の検証
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("口座番号は必須です");
        }
        if (initialBalance < 0) {
            throw new IllegalArgumentException("初期残高は0以上である必要があります");
        }
        
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
        this.transactionHistory.add("口座開設: 初期残高 " + initialBalance + "円");
    }
    
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("入金額は正の値である必要があります");
        }
        balance += amount;
        transactionHistory.add("入金: " + amount + "円");
    }
    
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            return false;
        }
        if (balance >= amount) {
            balance -= amount;
            transactionHistory.add("出金: " + amount + "円");
            return true;
        }
        transactionHistory.add("出金失敗（残高不足）: " + amount + "円");
        return false;
    }
    
    public double getBalance() {
        return balance;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    // 防御的コピーで履歴を返す
    public List<String> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }
}
```

**完成した設計の特徴**：
- **不変性**: 口座番号はfinalで変更不可
- **完全な検証**: すべての入力を検証
- **履歴管理**: すべての取引を記録
- **防御的コピー**: 内部リストの参照を外部に漏らさない

### 銀行口座クラスのさらなる発展

BankAccountV3の設計を基に、本章ではさらに高度な設計パターンを適用した例を見てみましょう。これは、実際のエンタープライズアプリケーションで使用されるレベルの設計です：

<span class="listing-number">**サンプルコード4-11**</span>

```java
// BankAccountV3をさらに発展させた設計例
public class EnhancedBankAccount extends BankAccountV3 {
    // 追加のフィールド：口座の状態管理
    private AccountStatus status;
    private LocalDateTime lastActivityDate;
    private int failedTransactionCount;
    
    // 列挙型で口座状態を管理
    public enum AccountStatus {
        ACTIVE,      // アクティブ
        SUSPENDED,   // 一時停止
        CLOSED       // 閉鎖
    }
    
    // 拡張されたコンストラクタ
    public EnhancedBankAccount(String accountNumber, String accountHolder, double initialBalance) {
        super(accountNumber, initialBalance);  // 親クラスのコンストラクタを呼び出し
        this.status = AccountStatus.ACTIVE;
        this.lastActivityDate = LocalDateTime.now();
        this.failedTransactionCount = 0;
    }
    
    // オーバーライドされた入金メソッド：状態チェックを追加
    @Override
    public void deposit(double amount) {
        validateAccountStatus();
        super.deposit(amount);
        updateLastActivity();
    }
    
    // オーバーライドされた出金メソッド：失敗回数の追跡
    @Override
    public boolean withdraw(double amount) {
        validateAccountStatus();
        boolean success = super.withdraw(amount);
        if (!success) {
            failedTransactionCount++;
            if (failedTransactionCount >= 3) {
                status = AccountStatus.SUSPENDED;
                System.out.println("連続した取引失敗により、口座が一時停止されました。");
            }
        } else {
            failedTransactionCount = 0;  // 成功したらリセット
        }
        updateLastActivity();
        return success;
    }
    
    // プライベートメソッド：内部ロジック
    private void validateAccountStatus() {
        if (status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("口座がアクティブではありません: " + status);
        }
    }
    
    private void updateLastActivity() {
        this.lastActivityDate = LocalDateTime.now();
    }
    
    // 口座の再アクティブ化
    public void reactivateAccount() {
        if (status == AccountStatus.SUSPENDED) {
            status = AccountStatus.ACTIVE;
            failedTransactionCount = 0;
            System.out.println("口座が再アクティブ化されました。");
        }
    }
}
```

**この設計の要点**：
- **継承の活用**: 第3章のBankAccountV3を基に機能を拡張
- **列挙型**: 口座状態を型安全に管理
- **状態管理**: 失敗回数の追跡と自動停止機能
- **テンプレートメソッドパターン**: 基本動作を保持しつつ拡張

### クラス設計のベストプラクティス

適切なクラス設計は、ソフトウェアの品質向上に寄与する要素の一つです。オブジェクト指向設計の実践から生まれたベストプラクティスは、保守性、拡張性、信頼性の高いソフトウェアを構築するための有用な指針を提供します。ただし、これらは唯一の解決策ではなく、問題の性質によっては関数型プログラミングのイミュータブルなデータ構造や、手続き型プログラミングのシンプルな関数分解が適している場合もあります。

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

<span class="listing-number">**サンプルコード4-12**</span>

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

<span class="listing-number">**サンプルコード4-13**</span>

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
<span class="listing-number">**サンプルコード4-14**</span>

```java
import static java.lang.Math.PI;
import static java.lang.Math.sqrt;

double circumference = 2 * PI * radius;  // Math.PI と書く必要がない
double result = sqrt(16);                 // Math.sqrt と書く必要がない
```

### import文の注意点

異なるパッケージに同名のクラスが存在する場合、明示的な指定が必要です：

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

### パッケージ構成のベストプラクティス

効果的なパッケージ構成は、プロジェクトの保守性と拡張性を大きく向上させます：

<span class="listing-number">**サンプルコード4-16**</span>

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

これらの原則に従うことで、保守性が高く、バグの少ないJavaプログラムを作成できます。

## コンストラクタとthisキーワード

### コンストラクタの詳細

コンストラクタは、オブジェクトが生成される際に自動的に呼び出される特殊なメソッドです。クラス名と同じ名前を持ち、戻り値の型を指定しません。

#### デフォルトコンストラクタ

コンストラクタを一つも定義しない場合、Javaコンパイラは自動的に引数なしのデフォルトコンストラクタを生成します：

<span class="listing-number">**サンプルコード4-17**</span>

```java
public class Person {
    private String name;
    private int age;
    
    // コンストラクタを定義しない場合、以下が自動生成される
    // public Person() {
    //     super();  // Objectクラスのコンストラクタを呼び出す
    // }
}

// 使用例
Person person = new Person();  // デフォルトコンストラクタが呼ばれる
```

#### 複数のコンストラクタ（オーバーロード）

クラスには複数のコンストラクタを定義できます。これをコンストラクタオーバーロードといいます：

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

### thisキーワードの3つの用法

`this`キーワードには主に3つの用法があります：

#### 1. インスタンス変数の参照

パラメータ名とフィールド名が同じ場合の区別：

<span class="listing-number">**サンプルコード4-19**</span>

```java
public class Student {
    private String name;
    private int grade;
    
    public Student(String name, int grade) {
        this.name = name;    // this.name はフィールド、name はパラメータ
        this.grade = grade;  // this.grade はフィールド、grade はパラメータ
    }
    
    // thisを省略できる場合（名前が重複しない）
    public void updateGrade(int newGrade) {
        grade = newGrade;  // this.grade と同じ
    }
}
```

#### 2. 他のコンストラクタの呼び出し

同じクラス内の別のコンストラクタを呼び出す：

<span class="listing-number">**サンプルコード4-20**</span>

```java
public class Rectangle {
    private double width;
    private double height;
    
    // メインコンストラクタ
    public Rectangle(double width, double height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("幅と高さは正の値である必要があります");
        }
        this.width = width;
        this.height = height;
    }
    
    // 正方形用のコンストラクタ
    public Rectangle(double side) {
        this(side, side);  // 最初の行でなければならない
    }
    
    // デフォルトコンストラクタ
    public Rectangle() {
        this(1.0, 1.0);  // 1x1の長方形
    }
}
```

#### 3. 現在のオブジェクトの参照を返す

メソッドチェーンを可能にする流暢なインターフェイス：

<span class="listing-number">**サンプルコード4-21**</span>

```java
public class MessageBuilder {
    private String buffer = "";
    
    public MessageBuilder append(String str) {
        buffer += str;
        return this;  // 自分自身を返す
    }
    
    public MessageBuilder appendLine(String str) {
        buffer += str + "\n";
        return this;
    }
    
    public String toString() {
        return buffer;
    }
}

// 使用例：メソッドチェーン
String result = new MessageBuilder()
    .append("Hello")
    .append(" ")
    .append("World")
    .appendLine("!")
    .toString();
```

### メソッドオーバーロード

メソッドオーバーロードは、同じ名前で異なるパラメータリストを持つ複数のメソッドを定義する機能です：

<span class="listing-number">**サンプルコード4-22**</span>

```java
public class Calculator {
    // int型の加算
    public int add(int a, int b) {
        return a + b;
    }
    
    // double型の加算
    public double add(double a, double b) {
        return a + b;
    }
    
    // 3つの数の加算
    public int add(int a, int b, int c) {
        return a + b + c;
    }
    
    // 配列の要素の合計
    public int add(int[] numbers) {
        int sum = 0;
        for (int num : numbers) {
            sum += num;
        }
        return sum;
    }
}
```

**オーバーロードの規則**：
- メソッド名は同じでなければならない
- パラメータリスト（個数、型、順序）が異なる必要がある
- 戻り値の型だけが異なる場合はオーバーロードできない
- アクセス修飾子は異なってもよい

## まとめ

本章では、オブジェクト指向プログラミングの中核となるカプセル化とクラス設計について学習しました。

### 学習した重要概念

1. **カプセル化**
   - データと処理を1つのクラスにまとめる
   - privateフィールドとpublicメソッドによるアクセス制御
   - データの整合性と安全性の確保

2. **アクセス修飾子**
   - `private`: 同じクラス内のみ
   - パッケージプライベート（デフォルト）: 同じパッケージ内
   - `protected`: 同じパッケージまたはサブクラス
   - `public`: どこからでもアクセス可能

3. **実践的なクラス設計**
   - BankAccountの段階的改善（V0→V1→V2→V3）
   - getter/setterによる安全なデータアクセス
   - バリデーションによるデータ検証
   - 防御的コピーによる内部状態の保護

4. **コンストラクタとthisキーワード**
   - デフォルトコンストラクタとカスタムコンストラクタ
   - コンストラクタオーバーロード
   - thisキーワードの3つの用法
   - メソッドオーバーロードによる柔軟なAPI設計

5. **パッケージシステム**
   - 論理的なクラスの組織化
   - 名前空間の管理
   - import文による効率的なクラス利用

### 次章への展望

第5章「継承とポリモーフィズム」では、本章で学んだクラス設計の技術をさらに発展させ、クラス間の関係性を表現する方法を学びます。継承により既存のクラスを拡張し、ポリモーフィズムにより柔軟で拡張性の高いプログラムを作成する技術を習得します。

## 章末演習

本章で学んだカプセル化とクラス設計の概念を実践的な課題で確認しましょう。

### 演習課題へのアクセス

本書の演習課題は、以下のGitHubリポジトリで提供されています：

**リポジトリ**: `https://github.com/[your-repo]/java-primer-exercises`

### 第4章の課題構成

```
exercises/chapter04/
├── basic/              # 基礎課題（必須）
│   ├── README.md       # 詳細な課題説明
│   ├── Book.java
│   └── Product.java
├── advanced/           # 発展課題（推奨）
│   ├── Employee.java
│   └── Department.java
├── challenge/          # チャレンジ課題（任意）
└── solutions/          # 解答例（実装後に参照）
```

### 学習の目標

本章の演習を通じて以下のスキルを習得します：
- カプセル化の適切な実装
- アクセス修飾子の使い分け
- getter/setterパターンの実践
- 入力検証とデータ整合性の確保
- 複数クラス間の連携設計

### 課題の概要

1. **基礎課題**: 書籍管理クラス、製品管理クラス
2. **発展課題**: 従業員管理システム（複数クラスの連携）
3. **チャレンジ課題**: 実務レベルのシステム設計

詳細な課題内容と実装のヒントは、GitHubリポジトリの各課題フォルダ内のREADME.mdを参照してください。

**次のステップ**: 基礎課題が完了したら、第5章「継承とポリモーフィズム」に進みましょう。

## より深い理解のために

本章で学んだクラス設計とカプセル化について、さらに深く理解したい方は、GitHubリポジトリの付録資料を参照してください：

**付録リソース**: `/appendix/b04-software-design-principles/`

この付録では以下の高度なトピックを扱います：

- **SOLID原則**: 単一責任、開放閉鎖、リスコフ置換、インターフェイス分離、依存性逆転の各原則
- **情報隠蔽の深い意味**: デメテルの法則、Tell Don't Askの原則
- **契約による設計**: 事前条件、事後条件、不変条件による堅牢な設計
- **デザインパターン**: GoFパターンの実践的な適用例
