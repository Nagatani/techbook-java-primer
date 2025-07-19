# <b>4章</b> <span>クラスとインスタンス</span> <small>設計図からオブジェクトを生み出す技術</small>

## 本章の学習目標

### 前提知識

必須
- 第3章のオブジェクト指向の基礎概念
- クラスが「設計図」、オブジェクトが「実体」という関係性
- カプセル化によるデータ保護の意義

推奨
- 基本的なクラスの設計と実装の経験
- 複数のクラスを含むプログラムの作成経験

### 学習目標

本章では、Javaにおける「クラスとインスタンス」の詳細なメカニズムを学習します。

知識理解の面では、クラスとインスタンス（オブジェクト）の関係性を深く理解し、コンストラクタがオブジェクト生成時に果たす役割とその種類を学びます。インスタンス変数とインスタンスメソッドがどのように連携してオブジェクトの状態と振る舞いを実現するかを理解します。`this`キーワードの意味と使用法、そしてメソッドオーバーロードの概念も重要な学習ポイントです。

技能習得の面では、クラスの基本構造であるフィールド、メソッド、コンストラクタを実際に実装できるようになることを目指します。特に、必須フィールドの初期化を保証するコンストラクタの定義、`this`キーワードを使ったフィールドと引数の明確な区別、メソッドオーバーロードによる柔軟なAPI設計を身につけます。インスタンス変数の初期化ではnullポインタ例外を防ぐ方法も、安全なプログラムを書く上で大切なスキルです。

さらに、設計能力の観点からは、責任が明確なクラスの設計方法を学びます。privateフィールドとpublicメソッドによるカプセル化を実現し、メソッド名が目的を明確に表し、引数と戻り値が予測可能なクラスインターフェイスを設計する能力を養います。

最終的には、実世界の概念をクラスとして単一責任の原則に基づいてモデル化し、複数のコンストラクタを持つ柔軟なクラスを作成できます。インスタンス変数とメソッドを凝集度を高く保ちながら組み合わせ、ほかのクラスから利用しやすい、保守性の高いクラスを設計・実装できるようになることが、本章の最終的な到達目標です。



## 章の構成

本章では、以下の内容を学習します。

1. カプセル化とアクセス制御
   - カプセル化の基本概念
   - アクセス修飾子の詳細（public、private、protected、パッケージプライベート）
   - データ保護の重要性

2. 実践的なクラス設計
   - 銀行口座クラスの段階的改善（V0→V1→V2→V3）
   - getter/setterメソッドのベストプラクティス
   - データ検証とバリデーション

3. コンストラクタとオブジェクト生成
   - コンストラクタの役割と種類
   - thisキーワードの活用
   - オーバーロードによる柔軟な初期化

4. パッケージシステム
   - パッケージの概念と命名規則
   - import文の使い方
   - クラスの組織化

## はじめに：なぜカプセル化が重要なのか

### 銀行口座クラスの復習と発展

第3章では基本的なクラスの作り方を学習しました。本章では、実践的なカプセル化の技術を、銀行口座クラスの段階的な改善を通じて学んでいきます。

まずは、カプセル化を適用していない問題のあるクラスから始めて、段階的に改善していく過程を見ていきましょう。

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

#### このコードの問題点
- カプセル化の欠如すべてのフィールドがpublicで、外部から自由にアクセス・変更可能
- データの整合性不正な値（負の残高など）を防ぐ仕組みがない
- 変更の追跡誰がいつ値を変更したか記録できない
- ビジネスルールの実装場所：入出金のルールをクラス外部で実装する必要がある

これらの問題を解決するために、カプセル化という技術を使います。本章では、このBankAccountクラスを段階的に改善しながら、実践的なカプセル化の技術を身につけていきます。

## カプセル化とアクセス制御

### カプセル化の基本概念

カプセル化は、関連するデータ（フィールド）と処理（メソッド）を1つのクラスにまとめ、外部から直接アクセスできないよう保護する技術です。これにより、オブジェクトの内部状態を安全に管理できます。

### アクセス修飾子の詳細

Javaのアクセス制御は、カプセル化を実現する上で最も重要な機能の1つです。Javaは4つのアクセス修飾子を提供しており、これらをパッケージ構造や継承関係に応じて使い分けることで、クラスの内部実装を隠蔽しながら必要な機能だけを外部に公開できます。各修飾子は、フィールドとメソッドの可視性を段階的に制御し、オブジェクト指向設計の原則である「必要最小限の公開」を実現します。

#### アクセス修飾子の可視性マトリックス

| 修飾子 | 同じクラス | 同じパッケージ | サブクラス（別パッケージ） | それ以外（別パッケージ） | 説明 |
|--------|-----------|---------------|---------------------------|-------------------------|------|
| `private` | ○ | × | × | × | 同じクラス内からのみアクセス可能 |
| (なし)※ | ○ | ○ | × | × | 同じパッケージ内からのみアクセス可能 |
| `protected` | ○ | ○ | ○ | × | 同じパッケージまたはサブクラスからアクセス可能 |
| `public` | ○ | ○ | ○ | ○ | どこからでもアクセス可能 |

※ 修飾子を記述しない場合（デフォルトアクセス、package-privateとも呼ばれる）

#### アクセス修飾子の使い分け原則

1. `private`を基本とする： フィールドは原則として`private`にし、外部からの読み取りが必要な場合はgetter、変更が必要な場合はsetterを提供
2. `public`は慎重に： 外部APIとして公開する必要があるメソッドのみ`public`にする
3. `protected`の活用： サブクラスからのアクセスが必要なメソッドやフィールドに使用し、継承を前提とした設計であることを明示
4. パッケージプライベートの戦略的使用： 関連する複数のクラスが密に連携する必要がある場合（例：データアクセス層の内部処理）に使用

#### 各アクセス修飾子の詳細と使用例

`private`の使用例。
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

パッケージプライベート（デフォルト）の使用例。
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

`protected`の使用例。
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

`public`の使用例。
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

getter/setterメソッド（アクセサメソッドとも呼ばれます）は、カプセル化の実装において中心的な役割を果たします。単にフィールドの値を取得・設定するだけでなく、データの整合性を保証し、将来の変更に対する柔軟性を提供します。以下の例では、プライベートフィールドへの安全なアクセスを提供する標準的なパターンを示します。

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

プログラミングにおいて、データの状態を常に有効に保つことは極めて重要です。オブジェクト指向では、これをカプセル化とsetterメソッドによる検証で実現します。setterメソッドは単なる値の代入ではなく、オブジェクトの不変条件（invariant）を守るゲートキーパーとしての役割を担います。範囲チェック、nullチェック、ビジネスルールの検証を実装することで、バグの早期発見と予防が可能になり、システム全体の信頼性が向上します。以下の例では、実務でよく使用される検証パターンを示します。

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

まず、フィールドをprivateにして、publicメソッドを通じてのみアクセスできるようにします。

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

#### 改善点
- フィールドがprivateになり、直接アクセスできない
- メソッドを通じてのみ操作可能

#### 残る問題
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
            throw new IllegalArgumentException("初期残高は0以上である必要です");
        }
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }
    
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("入金額は正の値である必要です");
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

#### 改善点
- コンストラクタで初期値を検証
- 入金時に金額を検証
- 出金時に残高と金額を確認

#### 残る問題
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
            throw new IllegalArgumentException("初期残高は0以上である必要です");
        }
        
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
        this.transactionHistory.add("口座開設: 初期残高 " + initialBalance + "円");
    }
    
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("入金額は正の値である必要です");
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

#### 完成した設計の特徴
- 不変性：口座番号はfinalで変更不可
- 完全な検証すべての入力を検証
- 履歴管理すべての取引を記録
- 防御的コピー: 内部リストの参照を外部に漏らさない

### 銀行口座クラスのさらなる発展

BankAccountV3の設計を基に、本章ではさらに高度な設計パターンを適用した例を見てみましょう。これは、実際のエンタープライズアプリケーションで使用されるレベルの設計です。

<span class="listing-number">**サンプルコード4-11**</span>

```java
// BankAccountV3をさらに発展させた設計例
public class EnhancedBankAccount extends BankAccountV3 {
    // 追加のフィールド：口座の状態管理
    private String status;
    private LocalDateTime lastActivityDate;
    private int failedTransactionCount;
    
    // 口座状態を表す定数
    public static final String STATUS_ACTIVE = "ACTIVE";      // アクティブ
    public static final String STATUS_SUSPENDED = "SUSPENDED"; // 一時停止
    public static final String STATUS_CLOSED = "CLOSED";       // 閉鎖
    
    // 拡張されたコンストラクタ
    public EnhancedBankAccount(String accountNumber, String accountHolder, double initialBalance) {
        super(accountNumber, initialBalance);  // 親クラスのコンストラクタを呼び出し
        this.status = STATUS_ACTIVE;
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
                status = STATUS_SUSPENDED;
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
        if (!STATUS_ACTIVE.equals(status)) {
            throw new IllegalStateException("口座がアクティブではありません: " + status);
        }
    }
    
    private void updateLastActivity() {
        this.lastActivityDate = LocalDateTime.now();
    }
    
    // 口座の再アクティブ化
    public void reactivateAccount() {
        if (STATUS_SUSPENDED.equals(status)) {
            status = STATUS_ACTIVE;
            failedTransactionCount = 0;
            System.out.println("口座が再アクティブ化されました。");
        }
    }
}
```

この設計の要点。
- 継承の活用第3章のBankAccountV3を基に機能を拡張
- 定数による状態管理：口座状態を文字列定数で管理
- 状態管理：失敗回数の追跡と自動停止機能
- テンプレートメソッドパターン基本動作を保持しつつ拡張

### クラス設計のベストプラクティス

クラス設計は、ソフトウェアの品質向上に寄与する要素の1つです。オブジェクト指向設計の実践から生まれたベストプラクティスは、保守性、拡張性、信頼性の高いソフトウェアを構築するための指針を提供します。ただし、これらは唯一の解決策ではなく、バッチ処理やデータ変換処理では関数型プログラミングのイミュータブルなデータ構造、シンプルなスクリプトでは手続き型プログラミングの直接的な関数呼び出しが適している場合もあります。

もっとも基本的な原則は単一責任の原則です。1つのクラスは1つの明確な責任のみを持つべきであり、これにより変更の理由が限定され、クラスの理解と保守が容易になります。次に重要なのはデータの隠蔽で、クラスの内部状態をprivateで保護し、必要な操作のみをパブリックメソッドとして公開します。これにより、実装の詳細を変更してもクライアントコードに影響を与えません。

入力検証も欠かせない要素です。すべての外部入力は、setterメソッドやコンストラクタで厳密に検証し、不正なデータがオブジェクトの状態を破壊することを防ぎます。また、意味のある名前を使用することで、コードの可読性と保守性が向上します。クラス名は名詞、メソッド名は動詞で始まるという慣習に従い、その役割を明確に表現する名前を選びます。

最後に、不変条件の維持は、オブジェクトの整合性を保証する上で極めて重要です。オブジェクトの生成から破棄まで、常に満たされるべき条件（不変条件）を明確に定義し、すべての操作でこれを維持するよう設計します。

これらの原則に従うことで、保守性が高く、バグの少ないJavaプログラムを作成できます。

## パッケージシステムとクラスの組織化

### パッケージの概念と必要性

Javaにおけるパッケージは、関連するクラスやインターフェイスをグループ化するための仕組みです。大規模なプロジェクトでは数百から数千のクラスが存在することがあり、フラットな構造ではクラス名の衝突や依存関係の把握が困難になります。パッケージは以下の重要な役割を果たします。

1. 名前空間の提供： 異なるパッケージに同じ名前のクラスを作成可能
2. アクセス制御： パッケージレベルでのアクセス制限を実現
3. 論理的な構造化： 機能や責任に基づいてクラスを整理
4. クラスの再利用： パッケージ単位での配布と利用

### パッケージの命名規則

Javaの言語仕様では、パッケージ名の衝突を避けるため、インターネットドメイン名を逆順にした命名規則が推奨されています。

<span class="listing-number">**サンプルコード4-12**</span>

```java
// ドメイン名: example.com
// パッケージ名: com.example.プロジェクト名.モジュール名
package com.example.myapp.service;

// ドメイン名: ac.jp（教育機関）
// パッケージ名: jp.ac.大学名.プロジェクト名
package jp.ac.university.research;
```

#### 命名規則のポイント
- すべて小文字を使用
- ドメイン名を逆順に記述
- 意味のある階層構造を作成
- Javaの予約語は使用しない

### パッケージとディレクトリ構造

パッケージ名はファイルシステムのディレクトリ構造と対応している必要があります。

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

パッケージに含まれるクラスを使用する際は、完全限定名かimport文を使用します。

<span class="listing-number">**サンプルコード4-13**</span>

```java
// 完全限定名での使用
java.util.List<String> names = new java.util.ArrayList<>();

// import文を使用した場合
import java.util.List;
import java.util.ArrayList;

List<String> names = new ArrayList<>();
```

import文の種類。

1. 単一型インポート。
```java
import java.util.Scanner;  // Scannerクラスのみインポート
```

2. オンデマンドインポート。
```java
import java.util.*;  // java.utilパッケージのすべてのクラスをインポート
```

3. 静的インポート。
<span class="listing-number">**サンプルコード4-14**</span>

```java
import static java.lang.Math.PI;
import static java.lang.Math.sqrt;

double circumference = 2 * PI * radius;  // Math.PI と書く必要がない
double result = sqrt(16);                 // Math.sqrt と書く必要がない
```

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

#### 設計原則
- 機能的凝集性：関連する機能を同じパッケージに
- 循環依存の回避：AがBを使い、BがAを使うような相互依存を避ける
- 推奨される粒度：1パッケージに5～20クラス程度を目安とする
- 明確な責任：各パッケージがデータアクセス、ビジネスロジック、UIなどの役割を明確に持つ

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

### thisキーワードの高度な活用

第3章では`this`の基本的な使い方を学びました。ここでは、より実践的で高度な`this`の活用パターンを学習します。

#### ビルダーパターンでの活用

複雑なオブジェクト構築において、`this`を返すことで流暢なインターフェイスを実現できます。

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

#### 高度なコンストラクタチェーン

実践的なクラス設計では、バリデーションロジックを1つのコンストラクタに集約し、他のコンストラクタはそれを呼び出します。

<span class="listing-number">**サンプルコード4-20**</span>

```java
public class DatabaseConfig {
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final int maxConnections;
    private final int timeoutSeconds;
    
    // マスターコンストラクタ（すべての検証をここに集約）
    public DatabaseConfig(String host, int port, String database, 
                         String username, String password, 
                         int maxConnections, int timeoutSeconds) {
        // 詳細な検証ロジック
        if (host == null || host.trim().isEmpty()) {
            throw new IllegalArgumentException("ホスト名は必須です");
        }
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("ポート番号は1-65535の範囲で指定してください");
        }
        if (maxConnections < 1) {
            throw new IllegalArgumentException("最大接続数は1以上である必要があります");
        }
        
        this.host = host.trim();
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.maxConnections = maxConnections;
        this.timeoutSeconds = timeoutSeconds;
    }
    
    // 開発環境用のコンストラクタ
    public DatabaseConfig(String host, String database) {
        this(host, 5432, database, "dev_user", "dev_password", 10, 30);
    }
    
    // 本番環境用のコンストラクタ
    public DatabaseConfig(String host, int port, String database, 
                         String username, String password) {
        this(host, port, database, username, password, 100, 60);
    }
}
```

#### コールバックでのthis渡し

イベント処理やコールバック関数において、現在のオブジェクトを別のオブジェクトへ渡す場合に使用します。

<span class="listing-number">**サンプルコード4-21**</span>

```java
public class EventProcessor {
    private String name;
    
    public EventProcessor(String name) {
        this.name = name;
    }
    
    public void startProcessing() {
        // 自分自身をイベントハンドラーに登録
        EventManager.register(this);
        System.out.println(name + " が処理を開始しました");
    }
    
    public void handleEvent(String event) {
        System.out.println(name + " がイベントを処理: " + event);
    }
}

class EventManager {
    private static List<EventProcessor> processors = new ArrayList<>();
    
    public static void register(EventProcessor processor) {
        processors.add(processor);
    }
    
    public static void fireEvent(String event) {
        for (EventProcessor processor : processors) {
            processor.handleEvent(event);
        }
    }
}
```

### 高度なメソッドオーバーロード設計

第3章でメソッドオーバーロードの基本を学びました。ここでは、実用的なAPIデザインにおけるオーバーロードの活用パターンを学習します。

#### デフォルト値を提供するオーバーロード

オーバーロードを使って、使いやすいAPIを設計できます。

<span class="listing-number">**サンプルコード4-22**</span>

```java
public class HttpClient {
    // フルスペックのメソッド
    public String get(String url, Map<String, String> headers, int timeoutMs) {
        // HTTP GET実装
        return "Response from " + url;
    }
    
    // ヘッダーのデフォルト値を提供
    public String get(String url, int timeoutMs) {
        return get(url, new HashMap<>(), timeoutMs);
    }
    
    // タイムアウトのデフォルト値も提供
    public String get(String url) {
        return get(url, 5000);  // 5秒のデフォルトタイムアウト
    }
}
```

#### 型安全性を高めるオーバーロード

異なるデータ型に対応しながら、型安全性を維持します。

<span class="listing-number">**サンプルコード4-23**</span>

```java
public class Logger {
    // 文字列メッセージ
    public void log(String message) {
        System.out.println("[INFO] " + message);
    }
    
    // 例外情報
    public void log(String message, Exception e) {
        System.out.println("[ERROR] " + message + ": " + e.getMessage());
    }
    
    // レベル付きログ
    public void log(String level, String message) {
        System.out.println("[" + level + "] " + message);
    }
    
    // フォーマット付きメッセージ
    public void log(String format, Object... args) {
        System.out.println("[INFO] " + String.format(format, args));
    }
    
    // ログレベルを表す定数
    public static final String LOG_DEBUG = "DEBUG";
    public static final String LOG_INFO = "INFO";
    public static final String LOG_WARN = "WARN";
    public static final String LOG_ERROR = "ERROR"
}
```

実践的なオーバーロード設計原則。
- もっとも多機能なメソッドを1つ定義し、他はそれを呼び出す
- デフォルト値は意味のある値を選ぶ
- 引数の順序を統一する（URL → オプション → タイムアウトなど）
- null安全性を考慮する

## まとめ

本章では、オブジェクト指向プログラミングの中核となるカプセル化とクラス設計について学習しました。

### 学習した重要概念

1. カプセル化
   - データと処理を1つのクラスにまとめる
   - privateフィールドとpublicメソッドによるアクセス制御
   - データの整合性と安全性の確保

2. アクセス修飾子
   - `private`: 同じクラス内のみ
   - パッケージプライベート（デフォルト）: 同じパッケージ内
   - `protected`: 同じパッケージまたはサブクラス
   - `public`: どこからでもアクセス可能

3. 実践的なクラス設計
   - BankAccountの段階的改善（V0→V1→V2→V3）
   - getter/setterによる安全なデータアクセス
   - バリデーションによるデータ検証
   - 防御的コピーによる内部状態の保護

4. コンストラクタとthisキーワード
   - デフォルトコンストラクタとカスタムコンストラクタ
   - コンストラクタオーバーロード
   - thisキーワードの3つの用法
   - メソッドオーバーロードによる柔軟なAPI設計

5. パッケージシステム
   - 機能別・層別によるクラスの組織化
   - ドメイン名を逆にした一意な名前空間
   - import文による明示的な依存関係の表現

### 次章への展望

第5章「継承とポリモーフィズム」では、本章で学んだクラス設計の技術をさらに発展させ、クラス間の関係性を表現する方法を学びます。継承により既存のクラスを拡張し、ポリモーフィズムにより柔軟で拡張性の高いプログラムを作成する技術を習得します。

## 章末演習

### 演習課題へのアクセス
本章の演習課題は、GitHubリポジトリで提供されています。
`https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapter04/`

### 課題構成
- 本章の基本概念の理解確認
- 応用的な実装練習
- 実践的な総合問題

詳細な課題内容と実装のヒントは、各課題フォルダ内のREADME.mdを参照してください。

## よくあるエラーと対処法

クラスとインスタンスの学習において、開発者が遭遇する典型的なエラーと、その解決方法について詳しく説明します。

### 1. クラス設計に関する問題

#### エラー1: 神（God）クラスの作成

##### 問題のあるコード
```java
// 一つのクラスに責任を詰め込みすぎた例
public class UserManager {
    private String username;
    private String password;
    private String email;
    
    // データベース操作
    public void saveToDatabase() { /* ... */ }
    public void deleteFromDatabase() { /* ... */ }
    
    // メール送信
    public void sendWelcomeEmail() { /* ... */ }
    public void sendPasswordResetEmail() { /* ... */ }
    
    // ログイン処理
    public boolean authenticate(String password) { /* ... */ }
    public void generateSession() { /* ... */ }
    
    // レポート生成
    public void generateUserReport() { /* ... */ }
    public void exportToCSV() { /* ... */ }
}
```

##### エラーメッセージ
```
設計上の問題: 単一責任の原則に違反している
```

##### 修正方法
```java
// 責任を分離したクラス設計
public class User {
    private String username;
    private String password;
    private String email;
    
    // コンストラクタ、getter、setter
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
    
    // 基本的なユーザー操作のみ
    public boolean validatePassword(String password) {
        return this.password.equals(password);
    }
}

public class UserRepository {
    public void save(User user) { /* データベース操作 */ }
    public void delete(String username) { /* データベース操作 */ }
}

public class EmailService {
    public void sendWelcomeEmail(User user) { /* メール送信 */ }
    public void sendPasswordResetEmail(User user) { /* メール送信 */ }
}

public class AuthenticationService {
    public boolean authenticate(User user, String password) { /* 認証処理 */ }
    public String generateSession(User user) { /* セッション生成 */ }
}
```

#### エラー2: 過度なgetterとsetterの使用

##### 問題のあるコード
```java
public class BankAccount {
    private double balance;
    
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
}

// 使用例での問題
BankAccount account = new BankAccount();
account.setBalance(1000.0);
double currentBalance = account.getBalance();
account.setBalance(currentBalance - 100.0);  // 直接残高操作
```

##### エラーメッセージ
```
設計上の問題: カプセル化が不十分で、ビジネスロジックが外部に漏れている
```

##### 修正方法
```java
public class BankAccount {
    private double balance;
    
    public BankAccount(double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("初期残高は0以上である必要です");
        }
        this.balance = initialBalance;
    }
    
    // 残高の取得は許可
    public double getBalance() { return balance; }
    
    // 直接設定は不可、代わりにビジネスロジックメソッドを提供
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("入金額は0より大きい必要があります");
        }
        this.balance += amount;
    }
    
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("出金額は0より大きい必要があります");
        }
        if (this.balance < amount) {
            return false;  // 残高不足
        }
        this.balance -= amount;
        return true;
    }
}
```

### 2. コンストラクタ関連のエラー

#### エラー3: デフォルトコンストラクタが見つからない

##### エラーメッセージ
```
error: The constructor User() is undefined
```

##### 問題のあるコード
```java
public class User {
    private String name;
    private int age;
    
    // カスタムコンストラクタを定義
    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }
}

// 使用時のエラー
User user = new User();  // コンパイルエラー
```

##### 修正方法
```java
public class User {
    private String name;
    private int age;
    
    // デフォルトコンストラクタを明示的に定義
    public User() {
        this("Unknown", 0);
    }
    
    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```

#### エラー4: コンストラクタでのnull値未検証

##### 問題のあるコード
```java
public class Product {
    private String name;
    private double price;
    
    public Product(String name, double price) {
        this.name = name;  // null チェックなし
        this.price = price;
    }
}

// 実行時エラーが発生する可能性
Product product = new Product(null, 100.0);
System.out.println(product.name.length());  // NullPointerException
```

##### エラーメッセージ
```
Exception in thread "main" java.lang.NullPointerException
```

##### 修正方法
```java
public class Product {
    private String name;
    private double price;
    
    public Product(String name, double price) {
        if (name == null) {
            throw new IllegalArgumentException("商品名はnullにできません");
        }
        if (price < 0) {
            throw new IllegalArgumentException("価格は0以上である必要があります");
        }
        this.name = name;
        this.price = price;
    }
}
```

### 3. メソッドオーバーロードの問題

#### エラー5: 引数の型による曖昧性

##### 問題のあるコード
```java
public class Calculator {
    public int calculate(int a, double b) {
        return (int)(a + b);
    }
    
    public double calculate(double a, int b) {
        return a + b;
    }
}

// 使用時のエラー
Calculator calc = new Calculator();
calc.calculate(10, 20);  // どちらのメソッドが呼ばれるか不明
```

##### エラーメッセージ
```
error: The method calculate(int, double) is ambiguous for the type Calculator
```

##### 修正方法
```java
public class Calculator {
    public int calculate(int a, int b) {
        return a + b;
    }
    
    public double calculate(double a, double b) {
        return a + b;
    }
    
    public double calculate(int a, double b) {
        return a + b;
    }
    
    public double calculate(double a, int b) {
        return a + b;
    }
}
```

#### エラー6: 戻り値の型のみが異なるオーバーロード

##### 問題のあるコード
```java
public class DataProcessor {
    public String process(String data) {
        return data.toUpperCase();
    }
    
    public int process(String data) {  // コンパイルエラー
        return data.length();
    }
}
```

##### エラーメッセージ
```
error: Duplicate method process(String) in type DataProcessor
```

##### 修正方法
```java
public class DataProcessor {
    public String processToUpperCase(String data) {
        return data.toUpperCase();
    }
    
    public int processToLength(String data) {
        return data.length();
    }
    
    // または、より明確な名前を使用
    public String formatText(String data) {
        return data.toUpperCase();
    }
    
    public int calculateLength(String data) {
        return data.length();
    }
}
```

### 4. オブジェクト参照の理解不足

#### エラー7: 参照の共有による意図しない変更

##### 問題のあるコード
```java
public class Team {
    private List<String> members;
    
    public Team(List<String> members) {
        this.members = members;  // 参照をそのまま保存
    }
    
    public List<String> getMembers() {
        return members;  // 内部リストを直接返す
    }
}

// 使用時の問題
List<String> originalList = new ArrayList<>();
originalList.add("Alice");
originalList.add("Bob");

Team team = new Team(originalList);
originalList.add("Charlie");  // 意図せずTeamの内部状態を変更

List<String> teamMembers = team.getMembers();
teamMembers.add("David");  // 意図せずTeamの内部状態を変更
```

##### 修正方法
```java
public class Team {
    private List<String> members;
    
    public Team(List<String> members) {
        // 防御的コピー
        this.members = new ArrayList<>(members);
    }
    
    public List<String> getMembers() {
        // 防御的コピーを返す
        return new ArrayList<>(members);
    }
    
    // 正しい方法でメンバーを追加
    public void addMember(String member) {
        if (member != null && !member.trim().isEmpty()) {
            members.add(member);
        }
    }
}
```

### 5. メモリリークの基礎

#### エラー8: リスナーの未解除

##### 問題のあるコード
```java
public class EventSource {
    private List<EventListener> listeners = new ArrayList<>();
    
    public void addListener(EventListener listener) {
        listeners.add(listener);
    }
    
    // リスナーを削除するメソッドがない
    public void fireEvent(String event) {
        for (EventListener listener : listeners) {
            listener.onEvent(event);
        }
    }
}

public class EventHandler implements EventListener {
    private EventSource source;
    
    public EventHandler(EventSource source) {
        this.source = source;
        source.addListener(this);
    }
    
    @Override
    public void onEvent(String event) {
        System.out.println("Received: " + event);
    }
}
```

##### 修正方法
```java
public class EventSource {
    private List<EventListener> listeners = new ArrayList<>();
    
    public void addListener(EventListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(EventListener listener) {
        listeners.remove(listener);
    }
    
    public void fireEvent(String event) {
        for (EventListener listener : listeners) {
            listener.onEvent(event);
        }
    }
}

public class EventHandler implements EventListener {
    private EventSource source;
    
    public EventHandler(EventSource source) {
        this.source = source;
        source.addListener(this);
    }
    
    // リソースの解放メソッド
    public void cleanup() {
        if (source != null) {
            source.removeListener(this);
            source = null;
        }
    }
    
    @Override
    public void onEvent(String event) {
        System.out.println("Received: " + event);
    }
}
```

### 6. equals()とhashCode()の実装ミス

#### エラー9: equals()のみの実装

##### 問題のあるコード
```java
public class Person {
    private String name;
    private int age;
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return age == person.age && Objects.equals(name, person.name);
    }
    
    // hashCode() の実装が不足
}

// 使用時の問題
Set<Person> people = new HashSet<>();
people.add(new Person("Alice", 30));
people.add(new Person("Alice", 30));  // 重複として検出されない
System.out.println(people.size());  // 期待値: 1、実際: 2
```

##### 修正方法
```java
public class Person {
    private String name;
    private int age;
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return age == person.age && Objects.equals(name, person.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }
}
```

### 7. toString()メソッドの問題

#### エラー10: 無限再帰による StackOverflowError

##### 問題のあるコード
```java
public class Department {
    private String name;
    private List<Employee> employees;
    
    public Department(String name) {
        this.name = name;
        this.employees = new ArrayList<>();
    }
    
    @Override
    public String toString() {
        return "Department{name='" + name + "', employees=" + employees + "}";
    }
}

public class Employee {
    private String name;
    private Department department;
    
    public Employee(String name, Department department) {
        this.name = name;
        this.department = department;
    }
    
    @Override
    public String toString() {
        return "Employee{name='" + name + "', department=" + department + "}";
    }
}
```

##### エラーメッセージ
```
Exception in thread "main" java.lang.StackOverflowError
```

##### 修正方法
```java
public class Department {
    private String name;
    private List<Employee> employees;
    
    public Department(String name) {
        this.name = name;
        this.employees = new ArrayList<>();
    }
    
    @Override
    public String toString() {
        return "Department{name='" + name + "', employeeCount=" + employees.size() + "}";
    }
}

public class Employee {
    private String name;
    private Department department;
    
    public Employee(String name, Department department) {
        this.name = name;
        this.department = department;
    }
    
    @Override
    public String toString() {
        String deptName = department != null ? department.getName() : "None";
        return "Employee{name='" + name + "', department='" + deptName + "'}";
    }
}
```

### デバッグのヒント

1. IDEの活用： 
   - ブレークポイントを設定してオブジェクトの状態を確認
   - Watch機能で変数の値を監視

2. ログ出力：
   - System.out.println()でデバッグ情報を出力
   - オブジェクトの状態変化を追跡

3. 単体テスト：
   - 小さな単位でクラスの動作を検証
   - 境界値やエラーケースもテスト

4. コードレビュー:
   - 第三者の目で設計の問題を発見
   - ベストプラクティスの共有

これらの典型的なエラーパターンを理解し、各パターンに対応した対処法を身につけることで、バグが少なく、変更に強く、チーム開発で扱いやすいJavaプログラムを作成できるようになります。
