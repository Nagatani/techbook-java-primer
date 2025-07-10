## 第3章 オブジェクト指向の考え方 - Part C: クラスとオブジェクトの詳細

### 2.4 オブジェクト指向の重要な用語と概念

ここからは、オブジェクト指向プログラミングで使用される重要な用語と概念を、実践的な例を交えて説明します。

#### オブジェクトとは

オブジェクトは、コンピュータのメモリ上に展開された、プログラム内の「状態」と「振る舞い」を統合した存在です。「状態」とは、オブジェクトが保持するデータを指し、変数やフィールドとして実装されます。一方、「振る舞い」は、オブジェクトが実行できる処理内容であり、メソッド（関数）として実装されます。

重要なのは、これらの状態と振る舞いが別々に存在するのではなく、密接な関連を持って一体化されている点です。たとえば、銀行口座オブジェクトであれば、残高（状態）と入金・出金操作（振る舞い）が1つのオブジェクトとして管理されることで、データの整合性を保ちやすくなります。

#### クラスとは

オブジェクトはメモリ上に展開されて使用されます。
そのオブジェクトは、どんな状態を持って、どんな振る舞いをするのかを定義するのがクラスです。

よくある表現として、オブジェクトの設計図をクラスという言い方がされます。

#### インスタンス（実体）とは
オブジェクトは、メモリ上に展開されて使用されます。
その状態をインスタンスと呼び、メモリ上に展開して使用できるようにすることをインスタンス化（実体化）と言います。

#### クラス、インスタンス、オブジェクトの関係

オブジェクト指向プログラミングでは、「クラス」「インスタンス」「オブジェクト」という用語が頻繁に登場し、初学者にとって混乱の原因となることがあります。これらの関係を明確に理解することが重要です。

**クラス**は、オブジェクトの設計図です。建築にたとえれば、家の設計図にあたります。クラスは、どのようなデータ（フィールド）を持ち、どのような操作（メソッド）ができるかを定義します。

**インスタンス**は、クラスをもとに実際に作成された具体的な実体です。設計図から実際に建てられた家に相当します。1つのクラスから複数のインスタンスを作成でき、それぞれが独立したデータを保持します。

**オブジェクト**という用語は、文脈によって意味が変わることに注意が必要です。多くの場合、オブジェクトはインスタンスを指しますが、より幅広い概念を表す場合や、クラス自体を指す場合もあります。「オブジェクト指向」という場合のオブジェクトは、より抽象的な概念を表しています。

※厳密には、インスタンス化をしなくても内部的に使用できる状態や振る舞いというのも存在します。それらは「**静的（static）な**○○」と呼ばれ、インスタンス化を明示的に行わなくても、プログラム実行時に自動的にメモリへ展開されており使用できるようになっています。対義語として「動的（dynamic）な○○」という言い方もあり、そちらはインスタンス化しないと使用できません。

#### クラスはどのように書くか

何らかのデータと、それに対する処理をまとめて書けると良いです。

「クラスは、C言語における構造体（struct）に、関数をつけられるようにしたもの」のようにイメージすると理解しやすいです。

#### 役割ごとに分割する必要性

その方が大規模なプログラムを作る際には管理しやすくなります。
管理しやすいとは、複雑でなく、バグの発見・修正が容易なことを指します。

#### 手続き型 vs オブジェクト指向

ここでは、手続き型プログラミングとオブジェクト指向プログラミングの根本的な違いを、学生情報を管理するプログラムの例を通じて理解します。この違いは、データの管理方法と責任の所在に関する重要な設計思想の違いを表しています。

##### 手続き型プログラミングの特徴と課題

手続き型プログラミングでは、データ構造と処理が分離されており、グローバルな関数がデータを操作します。この方式は単純で理解しやすい反面、大規模なシステムでは以下のような問題が生じます：

**手続き型プログラミング（C言語）**
```c
// データと処理が分離している
struct Student {
    char name[50];
    int age;
    double gpa;
};

void printStudent(struct Student s) {
    printf("名前: %s, 年齢: %d, GPA: %.2f\n", s.name, s.age, s.gpa);
}
```

**手続き型の課題：**
- **データの一貫性管理の困難さ**：Studentデータを変更する関数が複数存在する場合、どの関数がどのタイミングでデータを変更するかを追跡することが困難になります。
- **変更の影響範囲の拡大**：Student構造体にフィールドを追加した場合、そのデータを使用するすべての関数を見つけ出して修正する必要があります。
- **責任の所在のあいまいさ**：データの検証、初期化、出力など、学生に関する処理がシステム全体に散らばってしまいます。

##### オブジェクト指向プログラミングによる解決

オブジェクト指向では、関連するデータとそれを操作する処理を1つのクラスにまとめることで、上記の問題を解決します：

**オブジェクト指向プログラミング（Java）**
```java
// データと処理が一体化している
public class Student {
    private String name;
    private int age;
    private double gpa;
    
    // データを操作するメソッドも同じクラス内に定義
    public void printInfo() {
        System.out.println("名前: " + name + ", 年齢: " + age + ", GPA: " + gpa);
    }
}
```

**オブジェクト指向の利点：**
- **責任の明確化**：学生に関するすべての操作がStudentクラス内に集約され、データの管理責任が明確になります。
- **カプセル化による保護**：privateキーワードにより、外部からの不正なデータアクセスを防ぎ、データの整合性を保護します。
- **変更の局所化**：学生に関する処理の変更は、Studentクラス内のみで完結し、システム全体への影響を最小限に抑えられます。
- **現実世界のモデリング**：「学生」という概念を直接的にプログラムの構造に反映させることで、直感的で理解しやすいコードになります。

#### main関数に処理を突っ込むのではなく

自分の作っているプログラムをよく分析して、どんな登場人物（データ）がいるか、それぞれどんな役割があるかで考え、クラスに分割しましょう。

- イメージとして、子どもに対して着替えの指示を出すことを考えましょう。
    + （上着を着ているか確認）「上着のボタンを外して」「右腕から袖を通して次に左腕通して脱いで」（シャツを着ているか確認）「シャツも右腕から……」（靴下を履いているか確認）「屈んで」「靴下脱いで」（ズボンを履いているか確認）「ズボン脱いで」（タンスの場所を子どもが知っているか）「新しい服をタンスから出して」（新しい服はそろっているか）「シャツを首に通して……」……………

毎回、全部指示出すのは面倒でありませんか？
子どもには、**「子どもは、着替えてください（服を）」** というような命令で指示を出せて、結果だけ受け取ることができたらよいですよね？

#### 全部自分のプログラムでやろうとしない

自分が作ったクラスから、役割を持つほかのオブジェクトに対して指示を出すことで、関心の分離が可能となります。
大事なポイントとして、「処理をほかのオブジェクトに**お任せ**する」ということが挙げられます。

#### 処理をお任せすることと、お任せされる側の配慮

オブジェクトに処理をお任せする以上、そのオブジェクトの状態や振る舞いに外から干渉したくない（されたくもない）

- オブジェクトが持つ状態や、状態に対する変更処理を外部から直接触られると、予期せぬ不具合や、副作用が発生する。
    + 意図しない操作をされたくない部分を、外部から操作できないようにする

→ これを**カプセル化（隠ぺい）** とよいます。
適切なカプセル化を施すことで、オブジェクトの使いやすさ向上につながります。

#### 設計時に振る舞いの名前や入出力情報だけ決める
昨今のオブジェクト指向言語には、**インターフェイス**という概念があります。
インターフェイスにはクラスの振る舞いの名前や渡されるべきデータ型、振る舞いによる結果のデータ型だけを定義しておけます。

- インターフェイスは、それ自体をデータ型として使える便利なもの
- オブジェクト指向は、プログラミングだけでなく設計としての側面も強い

#### オブジェクト指向言語でつまずやすい継承

難しく考える必要はありません。
同じ状態、同じ振る舞いを持つクラスをそれぞれコピー&ペーストで作ると、管理が煩雑になってしまいます。

「似たデータ、似た処理をもつオブジェクトを、クラスの時点でまとめよう」として使用するのが継承という考え方になります。
単にまとめるための手段だと知っておきましょう。

（「従業員クラス」「マネージャークラス」がそれぞれ「人事管理可能」インターフェイスを実装することで、どちらも「人事評価機能」を持つといった設計もよくあります。教育的な例としては有効ですが、実際のシステムでは権限管理やロールベースのアクセス制御など、より洗練されたアプローチが必要になることが多いです）

#### システム開発に「銀の弾丸などない」

フレッド・ブルックスが1975年に著した「人月の神話」で述べたように、ソフトウェア開発の複雑性を一挙に解決する「銀の弾丸」は存在しません。オブジェクト指向プログラミングも例外ではなく、万能の解決策ではありません。

オブジェクト指向を不適切に適用した場合、返って問題を悪化させることがあります。たとえば、ごく小規模なスクリプトやユーティリティプログラムにオブジェクト指向を無理に取り入れると、シンプルな処理が複雑なクラス構造に埋もれてしまい、コードの記述量が膨大になることがあります。

また、バッチ処理やパイプライン処理のように、単機能の処理が連続して呼び出されるシステムでは、オブジェクト指向のオーバーヘッドが大きくなり、手続き型のアプローチの方が適切な場合があります。さらに、過度な抽象化や継承の乱用は、コードの理解を困難にし、管理しきれない副作用を発生させる可能性があります。

重要なのは、オブジェクト指向が適切な場面を見極め、適度に使用することです。問題の性質、システムの規模、開発チームのスキルセットなどを考慮して、最適なアプローチを選択することが、真のプロフェッショナルに求められるスキルです。

#### オブジェクト指向は効率良く開発を行うための考え方

歴史をたどると、システム開発の効率を求めた結果、オブジェクト指向という体系ができたに過ぎません。
これは通過点であり、オブジェクト指向の考え方を学ぶことですべてのことが説明できる気になったりましますが、これですべてを賄えることはありません。
本書では、オブジェクト指向という道具を学びますが、これはただのプログラミングテクニックの1つとして、使いたいときに使えるよう、適切な使い方を身に付けましょう。

### 2.5 クラスとオブジェクト

#### クラスの定義

クラスの定義は、オブジェクト指向プログラミングの核心部分です。ここでは、「本」という現実世界の概念をプログラムで表現する方法を学びます。クラス設計では、そのオブジェクトが「何を知っているか（属性）」と「何ができるか（操作）」を明確に定義することが重要です。

以下のBookクラスは、本という概念の本質的な属性と操作を表現した例です：

```java
public class Book {
    // フィールド（属性）
    private String title;
    private String author;
    private int pages;
    private double price;
    
    // コンストラクタ
    public Book(String title, String author, int pages, double price) {
        this.title = title;
        this.author = author;
        this.pages = pages;
        this.price = price;
    }
    
    // メソッド（操作）
    public void displayInfo() {
        System.out.println("タイトル: " + title);
        System.out.println("著者: " + author);
        System.out.println("ページ数: " + pages);
        System.out.println("価格: " + price + "円");
    }
    
    // ゲッターメソッド
    public String getTitle() {
        return title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    // セッターメソッド
    public void setPrice(double price) {
        if (price >= 0) {
            this.price = price;
        }
    }
}
```

**このクラス定義から学ぶ重要な概念：**

1. **カプセル化の実践**：すべてのフィールドをprivateで宣言することで、外部からの直接アクセスを禁止し、データの整合性を保護しています。

2. **コンストラクタによる初期化**：オブジェクト作成時に必要な情報をすべて受け取ることで、不完全なオブジェクトの生成を防いでいます。

3. **責任の明確化**：本に関する表示処理（displayInfo）を本クラス自身が担当することで、「本は自分の情報を表示できる」という自然な責任分担を実現しています。

4. **制御されたアクセス**：setPrice()メソッドで価格の妥当性をチェックすることで、ビジネスルール（価格は0以上）をクラス内で守っています。

5. **情報隠蔽の原則**：読み取り専用の情報（title, author）はgetterのみを提供し、変更可能な情報（price）には検証付きのsetterを提供するという、アクセス制御の使い分けを示しています。

#### オブジェクトの作成と使用

クラスを定義しただけでは、まだ実際にデータを保存したり処理を実行したりすることはできません。クラスは「設計図」であり、実際に動作するプログラムを作るには、その設計図からオブジェクト（インスタンス）を作成する必要があります。

以下のプログラムは、Bookクラスから実際のオブジェクトを作成し、それらを操作する方法を示しています：

```java
public class BookTest {
    public static void main(String[] args) {
        // オブジェクトの作成（インスタンス化）
        Book book1 = new Book("Java入門", "田中太郎", 300, 2800.0);
        Book book2 = new Book("データ構造", "佐藤花子", 250, 3200.0);
        
        // メソッドの呼び出し
        book1.displayInfo();
        System.out.println("---");
        book2.displayInfo();
        
        // 価格の変更
        book1.setPrice(2500.0);
        System.out.println("変更後の価格: " + book1.getTitle() + " - " + book1.getPrice() + "円");
    }
}
```

**このプログラムから学ぶオブジェクト指向の重要な概念：**

1. **オブジェクトの独立性**：book1とbook2は同じBookクラスから作られていますが、それぞれ独立したデータを持っています。一方のオブジェクトの状態を変更しても、他方には影響しません。

2. **new演算子の役割**：`new Book(...)`は、メモリ上にBookオブジェクト用の領域を確保し、コンストラクタを呼び出してオブジェクトを初期化します。これにより、クラスという「型」から具体的な「実体」が作られます。

3. **メッセージパッシング**：`book1.displayInfo()`のような記法は、book1オブジェクトに「自分の情報を表示して」というメッセージを送っていると解釈できます。オブジェクトは自分の責任範囲で処理を実行します。

4. **状態の変更と永続性**：`book1.setPrice(2500.0)`により、book1オブジェクトの内部状態が変更されます。この変更は、そのオブジェクトが存在する限り維持されます。

5. **型安全性**：BookTestクラスでは、Bookオブジェクトに対してBookクラスで定義されたメソッドのみ呼びだすことができ、不正な操作を防いでいます。

**実行時の動作理解：**
このプログラムを実行すると、メモリ上に2つの独立したBookオブジェクトが作成され、それぞれが異なる本の情報を保持します。price変更後のbook1は新しい価格情報を保持し続け、これによりオブジェクトの「状態を持つ」という特性を実感できます。

### 2.3 カプセル化

カプセル化は、オブジェクト指向プログラミングの最も重要な原則の1つです。この概念は、関連するデータと処理を1つの単位にまとめ、外部からの不適切なアクセスを制限することで、システムの安全性と保守性を大幅に向上させます。

カプセル化の本質は「情報隠蔽」と「責任の局所化」であり、これにより以下の利益を得られます：
- データの整合性保護（不正な状態への変更を防ぐ）
- 変更の影響範囲の局所化（内部実装を変更しても外部への影響を最小限に抑える）
- 再利用性の向上（明確なインターフェイスを通じた利用）

#### アクセス修飾子とカプセル化の段階的理解

カプセル化の重要性を理解するために、段階的に設計を改善していく例を見てみましょう。

##### 段階1: カプセル化なし（問題のある設計）

まず、カプセル化されていない設計から始めます：

```java
// 悪い例：カプセル化されていない銀行口座
public class BankAccountV1 {
    public String accountNumber;  // public: 誰でも変更可能
    public double balance;        // public: 残高を直接操作可能
    
    public BankAccountV1(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }
}

// 使用例（問題のある使い方）
public class ProblemExample {
    public static void main(String[] args) {
        BankAccountV1 account = new BankAccountV1("12345", 1000.0);
        
        // 問題：残高を直接操作できてしまう
        account.balance = -500.0;  // 負の残高！
        account.accountNumber = "";  // 口座番号を空に！
        
        // 問題：取引履歴が残らない
        account.balance += 1000.0;  // 誰がいつ入金したか不明
    }
}
```

**この設計の問題点：**
- データの整合性が保証されない（負の残高、空の口座番号）
- ビジネスルールを強制できない
- 変更履歴が追跡できない
- 誤った使用方法を防げない

##### 段階2: 基本的なカプセル化

privateキーワードとメソッドを使って基本的なカプセル化を実装：

```java
// 改善例：基本的なカプセル化
public class BankAccountV2 {
    private String accountNumber;
    private double balance;
    
    public BankAccountV2(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }
    
    // 入金メソッド
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }
    
    // 出金メソッド
    public boolean withdraw(double amount) {
        if (amount > 0 && balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }
    
    // 残高照会
    public double getBalance() {
        return balance;
    }
}
```

**改善された点：**
- フィールドがprivateで保護されている
- メソッドを通じてのみ操作可能
- 基本的な検証ロジックを実装

##### 段階3: 完全なカプセル化（エンタープライズレベル）

実際の業務システムで求められるレベルのカプセル化：

```java
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BankAccountV3 {
    private final String accountNumber;  // finalで不変性を保証
    private double balance;
    private final List<Transaction> transactions;
    private boolean isActive;
    
    public BankAccountV3(String accountNumber, double initialBalance) {
        // コンストラクタでも検証
        if (!isValidAccountNumber(accountNumber)) {
            throw new IllegalArgumentException("無効な口座番号です");
        }
        if (initialBalance < 0) {
            throw new IllegalArgumentException("初期残高は0以上である必要があります");
        }
        
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.transactions = new ArrayList<>();
        this.isActive = true;
        
        // 初期取引を記録
        transactions.add(new Transaction(
            TransactionType.INITIAL_DEPOSIT, 
            initialBalance, 
            LocalDateTime.now()
        ));
    }
    
    public synchronized void deposit(double amount) {
        validateAccountActive();
        if (amount <= 0) {
            throw new IllegalArgumentException("入金額は正の値である必要があります");
        }
        
        balance += amount;
        transactions.add(new Transaction(
            TransactionType.DEPOSIT, 
            amount, 
            LocalDateTime.now()
        ));
    }
    
    public synchronized boolean withdraw(double amount) {
        validateAccountActive();
        if (amount <= 0) {
            throw new IllegalArgumentException("出金額は正の値である必要があります");
        }
        
        if (balance < amount) {
            return false;  // 残高不足
        }
        
        balance -= amount;
        transactions.add(new Transaction(
            TransactionType.WITHDRAWAL, 
            amount, 
            LocalDateTime.now()
        ));
        return true;
    }
    
    // イミュータブルな取引履歴を返す
    public List<Transaction> getTransactionHistory() {
        return new ArrayList<>(transactions);  // 防御的コピー
    }
    
    // アカウントの凍結
    public void freeze() {
        this.isActive = false;
    }
    
    private void validateAccountActive() {
        if (!isActive) {
            throw new IllegalStateException("この口座は凍結されています");
        }
    }
    
    private static boolean isValidAccountNumber(String accountNumber) {
        return accountNumber != null && accountNumber.matches("\\d{10}");
    }
    
    // 内部クラスで取引を表現
    public static class Transaction {
        private final TransactionType type;
        private final double amount;
        private final LocalDateTime timestamp;
        
        private Transaction(TransactionType type, double amount, LocalDateTime timestamp) {
            this.type = type;
            this.amount = amount;
            this.timestamp = timestamp;
        }
        
        // getterのみ提供（イミュータブル）
        public TransactionType getType() { return type; }
        public double getAmount() { return amount; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
    
    public enum TransactionType {
        INITIAL_DEPOSIT, DEPOSIT, WITHDRAWAL
    }
}
```

**エンタープライズレベルのカプセル化の特徴：**
1. **完全な検証**：すべての入力を検証
2. **イミュータビリティ**：変更不可能なフィールドはfinalで宣言
3. **防御的コピー**：内部状態を返す際はコピーを返す
4. **スレッドセーフティ**：synchronizedで同時アクセスを制御
5. **監査証跡**：すべての操作を記録
6. **状態管理**：アカウントの有効/無効状態を管理

このように、カプセル化は単にprivateを使うだけでなく、システムの要求に応じて適切なレベルで実装する必要があります。



次のパート：[Part D - Java言語の基礎](chapter03d-java-basics.md)
