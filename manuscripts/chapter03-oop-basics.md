# 第3章 オブジェクト指向の考え方

## 📝 章末演習

本章で学んだコンストラクタ、thisキーワード、メソッドオーバーロードを活用して、実践的な練習課題に取り組みましょう。

### 🎯 演習の目標
- コンストラクタの概念と実装
- thisキーワードの理解と使用
- メソッドオーバーロードの実践
- 適切なクラス設計の基本

### 📁 課題の場所
演習課題は `exercises/chapter03/` ディレクトリに用意されています：

```
exercises/chapter03/
├── basic/          # 基本課題（必須）
│   ├── README.md   # 課題の詳細説明
│   ├── Student.java # 課題1: コンストラクタ実装
│   ├── StudentTest.java
│   ├── Rectangle.java # 課題2: オーバーロード実装
│   ├── RectangleTest.java
│   ├── BankAccount.java # 課題3: thisキーワード活用
│   ├── BankAccountTest.java
│   ├── Time.java    # 課題4: 総合実装
│   └── TimeTest.java
├── advanced/       # 発展課題（推奨）
└── challenge/      # 挑戦課題（上級者向け）
```

### 🚀 推奨する学習の進め方

1. **基本課題**から順番に取り組む
2. 各課題のREADME.mdで詳細を確認
3. ToDoコメントを参考に実装
4. 実行例と同じ出力になることを確認
5. コンストラクタチェインとthisキーワードの使い方を理解する

基本課題が完了したら、`advanced/`の発展課題でより高度なクラス設計に挑戦してみましょう！

## 📋 本章の学習目標

### 前提知識
**必須前提**：
- 第1章までの内容の完全な習得
- Java基本文法での簡単なプログラム作成経験
- メソッドと変数の概念の理解

**概念的前提**：
- ソフトウェア開発における問題（コードの複雑化、保守の困難さ）の実感
- 大規模なシステム開発の課題への関心

### 学習目標
**知識理解目標**：
- オブジェクト指向パラダイムの歴史的必然性
- カプセル化、継承、ポリモーフィズムの基本概念
- クラスとオブジェクトの関係性
- アクセス修飾子の役割と重要性

**技能習得目標**：
- 基本的なクラスの設計と実装
- コンストラクタの適切な定義と使用
- インスタンス変数とメソッドの実装
- private/publicアクセス修飾子の適切な使用
- オブジェクトの生成と操作

**設計思考目標**：
- 現実世界の概念をクラスとして抽象化する能力
- データと処理をまとめる設計思考
- カプセル化によるデータ保護の設計

**到達レベルの指標**：
- 実世界の概念（商品、人物、車など）をクラスとして設計・��装できる
- 複数のクラスを組み合わせた簡単なプログラムが作成できる
- 手続き型とオブジェクト指向の違いを具体例で説明できる
- クラス図の基本的な読み書きができる

---

## 始めに：なぜオブジェクト指向が必要なのか

前章では、Javaの歴史的背景と特徴について学習しました。本章では、Javaの核心となる「オブジェクト指向プログラミング」について学習します。

### ソフトウェア開発の課題

1960年代後半、ソフトウェアの規模が急速に拡大し、「ソフトウェアクライシス」と呼ばれる問題が発生しました。プロジェクトの遅延、品質の低下、保守の困難さなど、従来の開発手法では対処できない課題が山積していました。

構造化プログラミングがこれらの問題への最初の回答でしたが、データと処理が分離されているため、大規模システムでは変更の影響範囲の把握が困難で、コードの再利用性も低いという限界がありました。

### オブジェクト指向という解決策

オブジェクト指向は、これらの問題を解決するために生まれました。その基本的な考え方は：

1. **現実世界のモデリング**：プログラムを「オブジェクト」の集合として構成し、各オブジェクトが状態（データ）と振る舞い（メソッド）を持つ
2. **カプセル化**：オブジェクトの内部実装を隠蔽し、明確なインターフェイスのみを公開
3. **継承**：共通の特性を持つオブジェクトを階層的に組織化し、コードを再利用
4. **ポリモーフィズム**：同じインターフェイスで異なる実装を扱える柔軟性

これらの概念により、大規模で保守性の高いソフトウェアの開発が可能になりました。

> **Deep Dive**: オブジェクト指向の詳細な歴史（Simula、Smalltalk、C++の発展など）に興味がある方は、巻末の「オブジェクト指向プログラミングの歴史」を参照してください。

## 2.1 オブジェクト指向とは

オブジェクト指向には、主に以下の目的があります。

- 設計手法としてのオブジェクト指向（OOD）
- 分析手法としてのオブジェクト指向（OOA）
- 設計と分析を総称してオブジェクト指向分析設計（OODA）
- **プログラミングとしてのオブジェクト指向（OOP）**
    + 本書ではここに焦点を当てます。
- 開発方法論としてのオブジェクト指向
- プログラミング言語仕様としてのオブジェクト指向

## 2.2 オブジェクト指向を学ぶ必要性

現代において「専門的にオブジェクト指向について学ぶこと」自体は、実はそれほど重要でありません。では本書で取り扱うものは何なのか、ということになります。

皆さんは一年次にC言語を講義で学びました。
C言語でのプログラミングでも、構造的なプログラミングや、関数ライブラリを使った柔軟な開発は可能です。
極論を言ってしまえば、新たな道具としてオブジェクト指向を学ばなくても、たいていのプログラムは作れます。

### より良く継続的な開発を行うために

C言語でよい、とは言うものの、作成されたプログラムには「保守・運用」のフェーズが存在します。
そこで、言語として以下の要点が求められます。

1. 開発の効率化
2. 保守性の向上
3. 品質の向上

単純に作るだけでなく、これらを意識する必要性があります。

### 大規模で長期的に維持可能なプログラムを作る

C言語でも不可能ではありませんが「もっと効率良くしたい」という要望のために、オブジェクト指向という考え方が登場します。
より効率良くプログラミングを行うことを目的として、保守運用を見据えた概念であることをよく理解してオブジェクト指向を学んでください。

ただ、現代におけるプログラミングに求められるプログラミングの考え方には、オブジェクト指向以外も必要としています。
オブジェクト指向のその先もある、ということです。

## 2.3 オブジェクト指向の基本概念を実際のコードで理解する

理論的な説明の前に、実際のJavaコードを見てオブジェクト指向の基本概念を理解しましょう。

### 手続き型とオブジェクト指向の違い

#### 手続き型の例（C言語風）
```java
// データと処理が分離している
public class ProceduralExample {
    public static void main(String[] args) {
        // データは単なる変数
        String studentName = "田中太郎";
        int studentAge = 20;
        double studentGpa = 3.5;
        
        // 処理は独立した関数
        printStudent(studentName, studentAge, studentGpa);
    }
    
    public static void printStudent(String name, int age, double gpa) {
        System.out.println("名前: " + name + ", 年齢: " + age + ", GPA: " + gpa);
    }
}
```

#### オブジェクト指向の例
```java
// データと処理が一体化している
public class Student {
    // データ（状態）
    private String name;
    private int age;
    private double gpa;
    
    // コンストラクタ
    public Student(String name, int age, double gpa) {
        this.name = name;
        this.age = age;
        this.gpa = gpa;
    }
    
    // 処理（振る舞い）
    public void printInfo() {
        System.out.println("名前: " + name + ", 年齢: " + age + ", GPA: " + gpa);
    }
}

// 使用例
public class ObjectOrientedExample {
    public static void main(String[] args) {
        Student student = new Student("田中太郎", 20, 3.5);
        student.printInfo();  // オブジェクトに処理を依頼
    }
}
```

### オブジェクト指向の3つの基本原則

#### 1. カプセル化
```java
public class BankAccount {
    private double balance;  // privateで隠蔽
    
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;  // 適切な検証後に変更
        }
    }
    
    public double getBalance() {
        return balance;  // 読み取り専用のアクセス
    }
}
```

#### 2. 継承
```java
public class Animal {
    protected String name;
    
    public void eat() {
        System.out.println(name + "が食事をしています");
    }
}

public class Dog extends Animal {
    public void bark() {
        System.out.println(name + "が吠えています");
    }
}
```

#### 3. ポリモーフィズム
```java
public interface Shape {
    double calculateArea();
}

public class Circle implements Shape {
    private double radius;
    
    public double calculateArea() {
        return Math.PI * radius * radius;
    }
}

public class Rectangle implements Shape {
    private double width, height;
    
    public double calculateArea() {
        return width * height;
    }
}
```

これらの基本概念により、大規模で保守性の高いソフトウェアの開発が可能になります。

## 2.4 オブジェクト指向の重要な用語と概念

ここからは、オブジェクト指向プログラミングで使用される重要な用語と概念を、実践的な例を交えて説明します。

### オブジェクトとは
コンピュータのメモリ上に展開された、プログラム内の状態と、振る舞いをまとめたものと言えます。

- 状態（データ）
    + 変数やフィールド
- 振る舞い（処理内容）
    + メソッド（関数）

状態と振る舞いは、関連を持たせて管理をすることが望ましい

### クラスとは

オブジェクトはメモリ上に展開されて使用されます。
そのオブジェクトは、どんな状態を持って、どんな振る舞いをするのかを定義するのがクラスです。

よくある表現として、オブジェクトの設計図をクラスという言い方がされます。

### インスタンス（実体）とは
オブジェクトは、メモリ上に展開されて使用されます。
その状態をインスタンスと呼び、メモリ上に展開して使用できるようにすることをインスタンス化（実体化）と言います。

### クラス、インスタンス、オブジェクトの関係

- クラス： 設計図
- インスタンス： クラスを元に作られたオブジェクト
- オブジェクト： 文脈によってインスタンスを指すこともあれば、クラス自体や、より広い概念を指すこともある

※厳密には、インスタンス化をしなくても内部的に使用できる状態や振る舞いというのも存在します。それらは「**静的（static）な**○○」と呼ばれ、インスタンス化を明示的に行わなくても、プログラム実行時に自動的にメモリへ展開されており使用できるようになっています。対義語として「動的（dynamic）な○○」という言い方もあり、そちらはインスタンス化しないと使用できません。

### クラスはどのように書くか

何らかのデータと、それに対する処理をまとめて書けると良いです。

「クラスは、C言語における構造体（struct）に、関数をつけられるようにしたもの」のようにイメージすると理解しやすいです。

### 役割ごとに分割する必要性

その方が大規模なプログラムを作る際には管理しやすくなります。
管理しやすいとは、複雑でなく、バグの発見・修正が容易なことを指します。

### 手続き型 vs オブジェクト指向

ここでは、手続き型プログラミングとオブジェクト指向プログラミングの根本的な違いを、学生情報を管理するプログラムの例を通じて理解します。この違いは、データの管理方法と責任の所在に関する重要な設計思想の違いを表しています。

#### 手続き型プログラミングの特徴と課題

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

#### オブジェクト指向プログラミングによる解決

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

### main関数に処理を突っ込むのではなく

自分の作っているプログラムをよく分析して、どんな登場人物（データ）がいるか、それぞれどんな役割があるかで考え、クラスに分割しましょう。

- イメージとして、子どもに対して着替えの指示を出すことを考えましょう。
    + （上着を着ているか確認）「上着のボタンを外して」「右腕から袖を通して次に左腕通して脱いで」（シャツを着ているか確認）「シャツも右腕から……」（靴下を履いているか確認）「屈んで」「靴下脱いで」（ズボンを履いているか確認）「ズボン脱いで」（タンスの場所を子どもが知っているか）「新しい服をタンスから出して」（新しい服はそろっているか）「シャツを首に通して……」……………

毎回、全部指示出すのは面倒でありませんか？
子どもには、**「子どもは、着替えてください（服を）」** というような命令で指示を出せて、結果だけ受け取ることができたらよいですよね？

### 全部自分のプログラムでやろうとしない

自分が作ったクラスから、役割を持つほかのオブジェクトに対して指示を出すことで、関心の分離が可能となります。
大事なポイントとして、「処理をほかのオブジェクトに**お任せ**する」ということが挙げられます。

### 処理をお任せすることと、お任せされる側の配慮

オブジェクトに処理をお任せする以上、そのオブジェクトの状態や振る舞いに外から干渉したくない（されたくもない）

- オブジェクトが持つ状態や、状態に対する変更処理を外部から直接触られると、予期せぬ不具合や、副作用が発生する。
    + 意図しない操作をされたくない部分を、外部から操作できないようにする

→ これを**カプセル化（隠ぺい）** とよいます。
適切なカプセル化を施すことで、オブジェクトの使いやすさ向上につながります。

### 設計時に振る舞いの名前や入出力情報だけ決める
昨今のオブジェクト指向言語には、**インターフェイス**という概念があります。
インターフェイスにはクラスの振る舞いの名前や渡されるべきデータ型、振る舞いによる結果のデータ型だけを定義しておけます。

- インターフェイスは、それ自体をデータ型として使える便利なもの
- オブジェクト指向は、プログラミングだけでなく設計としての側面も強い

### オブジェクト指向言語でつまずやすい継承

難しく考える必要はありません。
同じ状態、同じ振る舞いを持つクラスをそれぞれコピー&ペーストで作ると、管理が煩雑になってしまいます。

「似たデータ、似た処理をもつオブジェクトを、クラスの時点でまとめよう」として使用するのが継承という考え方になります。
単にまとめるための手段だと知っておきましょう。

（「犬クラスは鳴く動物」「猫クラスも鳴く動物」それぞれは「鳴く動物クラス」を継承させることでどちらも「動物として鳴く機能」があるといったたとえ話もよくあります。それが必要な場面もありますが多くの場合は分かりづらさの象徴でもあります）

### システム開発に「銀の弾丸などない」

古くから言われていることですが、システム開発に「銀の弾丸などない」のです。オブジェクト指向を取り入れたことによる弊害ももちろんあり得ます。
以下、一部の例を挙げます。
- ごく小規模の案件で、無理矢理取り入れることによりコードの記述量が膨大になってしまった。
- 単機能の呼び出しが連続するようなシステムなど、オブジェクト指向的な考え方が不向きなシステムだった。
- 管理しきれない副作用の発生……など

### オブジェクト指向は効率良く開発を行うための考え方

歴史をたどると、システム開発の効率を求めた結果、オブジェクト指向という体系ができたに過ぎません。
これは通過点であり、オブジェクト指向の考え方を学ぶことですべてのことが説明できる気になったりましますが、これですべてを賄えることはありません。
本書では、オブジェクト指向という道具を学びますが、これはただのプログラミングテクニックの1つとして、使いたいときに使えるよう、適切な使い方を身に付けましょう。

## 2.5 クラスとオブジェクト

### クラスの定義

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

### オブジェクトの作成と使用

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

## 2.3 カプセル化

カプセル化は、オブジェクト指向プログラミングの最も重要な原則の1つです。この概念は、関連するデータと処理を1つの単位にまとめ、外部からの不適切なアクセスを制限することで、システムの安全性と保守性を大幅に向上させます。

カプセル化の本質は「情報隠蔽」と「責任の局所化」であり、これにより以下の利益を得られます：
- データの整合性保護（不正な状態への変更を防ぐ）
- 変更の影響範囲の局所化（内部実装を変更しても外部への影響を最小限に抑える）
- 再利用性の向上（明確なインターフェイスを通じた利用）

### アクセス修飾子

アクセス修飾子は、カプセル化を実現するための重要な機能です。以下のBankAccountクラスは、現実世界の銀行口座が持つセキュリティ要件をプログラムで表現した例です。銀行口座では、残高を直接操作することはできず、必ず適切な手続き（入金、出金）を通じてのみ操作できるという現実をモデル化しています。

```java
public class BankAccount {
    private String accountNumber;  // private: クラス内からのみアクセス可能
    private double balance;        // private: 残高を直接変更されないよう保護
    
    public BankAccount(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }
    
    // public: 外部から利用可能なメソッド
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println(amount + "円を入金しました。");
        }
    }
    
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            System.out.println(amount + "円を出金しました。");
            return true;
        }
        System.out.println("出金できません。");
        return false;
    }
    
    public double getBalance() {
        return balance;
    }
    
    // 口座番号は読み取り専用
    public String getAccountNumber() {
        return accountNumber;
    }
}
```

**このカプセル化の実装から学ぶ重要なセキュリティ原則：**

1. **データの保護**：`balance`フィールドをprivateで宣言することで、外部からの直接アクセスを禁止し、不正な残高操作（例：負の値や異常な大きな値の設定）を防いでいます。

2. **ビジネスルールの実装**：`withdraw()`メソッドでは、出金前に残高チェックを行うことで、現実世界の「残高不足では出金できない」というルールをプログラムレベルで守っています。

3. **インターフェイスの明確化**：外部から利用可能なメソッド（deposit, withdraw, getBalance）をpublicで宣言し、口座に対して実行可能な操作を明確に定義しています。

4. **不変性の保護**：口座番号は作成後は変更できないようにgetterメソッドのみを提供し、setterメソッドは提供していません。

5. **エラーハンドリング**：不正な操作を検知した場合は、適切なメッセージと戻り値（boolean）で呼び出し元に結果を通知し、エラーの伝播を防いでいます。

**カプセル化の効果：**
この設計により、BankAccountクラスを使用するプログラマは、銀行口座の内部実装を理解しなくても、安全に口座を操作できます。また、将来的に内部実装（例：残高の管理方法やログ記録機能）を変更しても、外部インターフェイスが変わらない限り、既存のコードに影響を与えません。

## 2.4 コンストラクタ

コンストラクタは、オブジェクトの初期化を担当する特別なメソッドです。コンストラクタの主な目的は、オブジェクトが作成された瞬間から一貫性のある有効な状態を持つことを保証することです。これにより、「不完全なオブジェクト」がシステム内に存在することを防ぎ、プログラムの安全性と信頼性を大幅に向上させます。

### 基本的なコンストラクタ

以下のRectangleクラスは、同じクラスで異なる初期化パターンを提供する「コンストラクタオーバーロード」の例です。この技法により、利用者は状況に応じて最適な初期化方法を選択でき、コードの可読性と使いやすさが大幅に向上します：

```java
public class Rectangle {
    private double width;
    private double height;
    
    // デフォルトコンストラクタ
    public Rectangle() {
        this.width = 1.0;
        this.height = 1.0;
    }
    
    // パラメータ付きコンストラクタ
    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    // 正方形用コンストラクタ
    public Rectangle(double side) {
        this.width = side;
        this.height = side;
    }
    
    public double getArea() {
        return width * height;
    }
    
    public double getPerimeter() {
        return 2 * (width + height);
    }
}
```

**コンストラクタオーバーロードがもたらす利益：**

1. **柔軟な初期化オプション**：デフォルトコンストラクタ`Rectangle()`は、詳細なサイズ指定が不要な場合に省力化を実現し、学習用やプロトタイプ作成時に有用です。

2. **意図の明確化**：正方形用コンストラクタ`Rectangle(double side)`は、「正方形を作りたい」という意図をコード上で明確に表現し、読み手にとって理解しやすいコードになっています。

3. **エラーの防止**：正方形を作る際に誤って異なる幅と高さを指定してしまうヒューマンエラーを防ぐことができます。

4. **コードの再利用性**：適切なデフォルト値（1.0 x 1.0）を設定することで、テストコードやプロトタイプ作成時に簡単にオブジェクトを作成できます。

**コンストラクタ設計のベストプラクティス：**

- **必要最小限の情報で有効なオブジェクトを作成**：どのコンストラクタを使用しても、作成されるオブジェクトは即座に使用可能な状態であるべきです。
- **不変条件の維持**：コンストラクタ実行後は、オブジェクトの不変条件（例：幅と高さは正の値）が満たされているべきです。
- **明確なインターフェイス**：各コンストラクタの目的と使用場面がパラメータから明確に理解できるように設計するべきです。

### コンストラクタの使用例

以下のプログラムは、同じRectangleクラスから異なる初期化パターンで作成された3つのオブジェクトが、それぞれ異なる特性を持つことを実証しています。この例では、コンストラクタオーバーロードがどのようにコードの可読性と使いやすさを向上させるかを確認できます：

```java
public class RectangleTest {
    public static void main(String[] args) {
        Rectangle rect1 = new Rectangle();           // デフォルト
        Rectangle rect2 = new Rectangle(5.0, 3.0);  // 長方形
        Rectangle rect3 = new Rectangle(4.0);       // 正方形
        
        System.out.println("rect1の面積: " + rect1.getArea());
        System.out.println("rect2の面積: " + rect2.getArea());
        System.out.println("rect3の面積: " + rect3.getArea());
    }
}
```

**このプログラム実行から学ぶ重要な洞察：**

1. **同一コード、異なる動作**：`getArea()`メソッドはすべてのオブジェクトで同じコードですが、各オブジェクトの初期化時に設定された値に応じて異なる結果を返します（rect1: 1.0, rect2: 15.0, rect3: 16.0）。

2. **コンストラクタ選択の明確性**：コードを読むだけで、各オブジェクトがどのような目的で作成されたかが一目でわかります（デフォルトサイズ、カスタムサイズ、正方形）。

3. **タイプセーフティの実現**：コンパイル時にコンストラクタの引数チェックが行われるため、不正な引数でのオブジェクト作成をコンパイル時に防ぐことができます。

4. **メモリ効率と性能**：各オブジェクトは必要最小限のメモリ（widthとheightの2つのdouble値）のみを使用し、不要なオーバーヘッドなしに効率的に動作します。

5. **ポリモーフィズムの基礎**：異なるコンストラクタで作成されたオブジェクトでも、同じメソッド名で操作できることで、将来的なポリモーフィズムの学習に向けた基礎を築いています。

**ビジネスアプリケーションでの応用：**
このパターンは、実際のソフトウェア開発でも頻繁に使用されます。たとえば、データベース接続クラスではデフォルト設定での接続、カスタム設定での接続、コネクションプール付きの接続など、さまざまな初期化パターンを提供できます。

## 2.5 thisキーワード

`this`キーワードは、オブジェクト指向プログラミングにおける重要な概念である「自己参照」を実現するための機能です。このキーワードは、現在実行中のメソッドが所属するオブジェクト自身への参照を表し、パラメータとフィールドの名前衝突解決、オブジェクトの自己操作、メソッドチェインなど、さまざまな用途で活用されます。

以下のPersonクラスは、`this`キーワードの代表的な使用パターンを示しています：

```java
public class Person {
    private String name;
    private int age;
    
    public Person(String name, int age) {
        this.name = name;  // thisでフィールドを明示
        this.age = age;
    }
    
    public void setName(String name) {
        this.name = name;  // パラメータ名とフィールド名が同じ場合に必要
    }
    
    public Person createCopy() {
        return new Person(this.name, this.age);  // 現在のオブジェクトのコピーを作成
    }
}
```

**`this`キーワードの重要な役割と効果：**

1. **名前衝突の解決**：コンストラクタやsetterメソッドでは、パラメータ名とフィールド名が同じになることがよくあります。`this.name = name`のように明示することで、左辺がオブジェクトのフィールド、右辺がパラメータであることを明確に区別できます。

2. **コードの可読性向上**：`this`を使用することで、「この変数はオブジェクトのフィールドである」ということがコード上で明確になり、読み手にとって理解しやすいコードになります。

3. **自己コピーの実現**：`createCopy()`メソッドでは、現在のオブジェクトが持つ値を使用して新しいオブジェクトを作成しています。これは「オブジェクトが自分自身のコピーを作る」という、オブジェクト指向の重要な概念である「自己責任」を実現しています。

4. **メモリ効率と安全性**：`this`は現在のオブジェクトへの参照であり、新たなメモリ領域を消費することなく、安全に自己参照を実現できます。

**実際の開発での応用例：**

- **Builderパターン**：メソッドチェインを実現するために`return this;`を使用
- **Fluent Interface**：連続したメソッド呼び出しで可読性の高いAPIを提供
- **コピーコンストラクタ**：既存オブジェクトから新しいオブジェクトを作成する際の自己参照

**注意すべきポイント：**

`this`キーワードの使用は、単なる文法的な道具ではなく、オブジェクトのアイデンティティと責任を明確化する重要な概念です。これにより、オブジェクトが「自分自身を理解し、操作できる」という、オブジェクト指向の根本原則を実現しています。

## 2.6 静的メンバー（static）

静的メンバー（staticメンバー）は、オブジェクト指向プログラミングにおける重要な概念で、「クラスレベル」で共有されるデータや機能を実現します。通常のインスタンスメンバーが「各オブジェクト固有」のデータや操作であるのに対し、staticメンバーは「クラス全体で共通」のデータや機能を表現し、ユーティリティ関数、定数、シングルトンパターンなどで活用されます。

以下のMathUtilクラスは、staticメンバーのさまざまな使用パターンと、インスタンスメンバーとの違いを示しています：

```java
public class MathUtil {
    // 静的定数
    public static final double PI = 3.14159265359;
    
    // 静的変数（クラス変数）
    private static int instanceCount = 0;
    
    // インスタンス変数
    private int value;
    
    public MathUtil(int value) {
        this.value = value;
        instanceCount++;  // インスタンス作成時にカウントアップ
    }
    
    // 静的メソッド
    public static double circleArea(double radius) {
        return PI * radius * radius;
    }
    
    public static int getInstanceCount() {
        return instanceCount;
    }
    
    // インスタンスメソッド
    public int getValue() {
        return value;
    }
}
```

**staticメンバーの重要な特徴と効果：**

1. **インスタンス作成不要**：`circleArea()`メソッドは、MathUtilオブジェクトを作成することなく直接呼びだすことができます。これは、数学関数のような「状態を持たない機能」に適しています。

2. **メモリ効率の向上**：静的定数`PI`は、プログラム実行中に1つだけメモリに保存され、すべてのインスタンスで共有されます。これにより、各インスタンスが個別にPI値を持つ必要がなくなります。

3. **グローバル状態の管理**：`instanceCount`変数は、作成されたMathUtilオブジェクトの総数を追跡します。このような「クラス全体で共有する情報」を管理するためにstatic変数が使用されます。

4. **アクセスの簡素化**：数学的な計算やユーティリティ関数のように、「状態を持たない純粋な機能」を提供する場合に、呼び出し側のコードが簡潔になります。

5. **名前空間の提供**：関連する関数や定数をクラス内にまとめることで、コードの整理と名前空間の汚染防止を実現しています。

**staticとインスタンスメンバーの違い：**

- **ライフサイクル**：staticメンバーはプログラム開始時に初期化され、終了まで存在し続けます。インスタンスメンバーはオブジェクトの作成から破棄までの間のみ存在します。
- **メモリ使用量**：staticメンバーはプログラム全体で1つだけですが、インスタンスメンバーは各オブジェクトごとに存在します。
- **アクセス方法**：staticメンバーはクラス名で直接アクセスできますが、インスタンスメンバーはオブジェクト参照を通じてのみアクセス可能です。

**実際の開発での応用例：**

- **ユーティリティクラス**：Math.abs(), String.valueOf()などの汎用的な関数
- **ファクトリメソッド**：オブジェクト作成のためのstaticメソッド
- **シングルトンパターン**：アプリケーション全体で1つだけのインスタンスを保証するパターン
- **定数コレクション**：アプリケーション全体で共有する設定値や定数

### 静的メンバーの使用

以下のプログラムは、staticメンバーとインスタンスメンバーの違いを実際のコードで確認し、staticメンバーがどのようにプログラムの効率性と便利性を向上させるかを示しています：

```java
public class MathUtilTest {
    public static void main(String[] args) {
        // 静的メソッドの呼び出し（インスタンス作成不要）
        double area = MathUtil.circleArea(5.0);
        System.out.println("円の面積: " + area);
        
        // インスタンス作成
        MathUtil util1 = new MathUtil(10);
        MathUtil util2 = new MathUtil(20);
        
        // 静的変数の確認
        System.out.println("作成されたインスタンス数: " + MathUtil.getInstanceCount());
    }
}
```

**このプログラムから学ぶstaticメンバーの重要な特性：**

1. **インスタンスとの独立性**：`MathUtil.circleArea(5.0)`の呼び出しでは、MathUtilオブジェクトを一切作成することなく、直接計算結果を得られます。これは、数学関数のような「純粋な機能」に適した使用パターンです。

2. **グローバル状態の管理**：`instanceCount`変数は、util1とutil2の作成に伴って自動的に更新され、プログラム全体でのインスタンス作成数を追跡します。これにより、リソースの使用状況やシステムの状態を監視できます。

3. **メモリ効率の优位性**：`PI`定数は、util1とutil2が作成されても、メモリ上では1つの値しか存在しません。これは、大量のオブジェクトを作成する場合に特に重要なメモリ節約効果をもたらします。

4. **APIの一負性**：静的メソッドはクラス名で直接呼びだすことで、Java標準ライブラリ（Math.sin(), String.valueOf()など）と同様のAPIパターンを提供し、開発者にとって直感的で使いやすいインターフェイスを実現しています。

5. **性能とスレッドセーフティ**：静的メソッドはオブジェクト作成のオーバーヘッドがなく、しかもインスタンス変数にアクセスしないため、本質的にスレッドセーフで高速な操作を実現しています。

**実行結果の理解：**
このプログラムを実行すると、円の面積が正確に計算され（約78.54）、インスタンス数として2が表示されます。これにより、staticメンバーがどのようにインスタンスとは独立して動作し、同時にクラス全体の状態を管理できるかを実感できます。

## 🏋️ 実践演習課題

### 基礎演習 2-1：基本的なクラス設計
**目標**：クラス、オブジェクト、カプセル化の理解

**課題内容**：
`Book`クラスの設計と実装を行ってください。

**必要な属性**：
- タイトル（String）
- 著者（String）
- 価格（int）
- ページ数（int）

**必要なメソッド**：
- コンストラクタ（すべての属性を初期化）
- 情報表示メソッド
- 税込価格計算メソッド（消費税10%）
- ページ単価計算メソッド（税込価格÷ページ数）

**実装要件**：
- 適切なアクセス修飾子の使用（privateフィールド、publicメソッド）
- バリデーション（負の値のチェック等）
- 読みやすい出力フォーマット

**評価ポイント**：

| 評価項目 |
| :--- |
| カプセル化が適切に実装されている |
| コンストラクタで適切な初期化が行われている |
| getter/setterメソッドが必要に応じて実装されている |
| バリデーション処理が含まれている |

### 応用演習 2-2：複数クラスの連携
**目標**：オブジェクト間の関係性の実装

**課題内容**：
図書館システムの基礎を実装してください。

**必要なクラス**：
1. `Book`クラス（前演習の拡張）
2. `Library`クラス（書籍の管理）

**Library クラスの機能**：
- 書籍の追加
- 書籍の削除（タイトルまたはIDで指定）
- 書籍の検索（タイトル、著者で検索）
- 全書籍の一覧表示
- 蔵書数の表示

**実装要件**：
- 配列を使った書籍データの管理
- 適切なメソッドの責任分担
- データの整合性の維持

**評価ポイント**：

| 評価項目 |
| :--- |
| クラス間の適切な関係設計 |
| メソッドの責任分担が明確 |
| データの整合性が保たれている |
| エラーハンドリングが適切 |

### 実践演習 2-3：ビジネスロジックの実装
**目標**：現実的な問題のオブジェクト指向的解決

**課題内容**：
銀行口座管理システムを実装してください。

**BankAccount クラスの仕様**：
- **属性**：口座番号、残高、口座名義、取引履歴
- **メソッド**：
  - 入金処理（金額の妥当性チェック）
  - 出金処理（残高不足チェック）
  - 残高照会
  - 取引履歴表示
  - 口座情報表示

**Bank クラスの仕様**：
- 複数口座の管理
- 新規口座開設
- 口座検索（口座番号、名義で検索）
- 全口座の一覧表示

**実装要件**：
- static変数を使った口座番号の自動採番
- 取引履歴の記録（日付、種別、金額、残高）
- 適切な例外処理（残高不足、無効な金額等）

**評価ポイント**：

| 評価項目 |
| :--- |
| ビジネスルールが適切に実装されている |
| データの一貫性が保たれている |
| ユーザビリティが考慮されている |
| 拡張性を考慮した設計になっている |

**拡張課題**：
- 異なる口座タイプ（普通預金、定期預金）の区別
- 利息計算機能
- 取引制限（日次限度額等）の実装

### 発展演習 2-4：総合的なオブジェクト指向設計
**目標**：複雑なシステムのオブジェクト指向設計

**課題内容**：
簡易的な学生管理システムを実装してください。

**必要なクラス構成**：
1. `Student`クラス - 学生情報の管理
2. `Subject`クラス - 科目情報の管理
3. `Grade`クラス - 成績情報の管理
4. `School`クラス - 学校全体の管理

**機能要件**：
- 学生の登録・削除・検索
- 科目の管理
- 成績の登録・更新
- 学生別成績表示
- 科目別成績統計
- 全体的な成績分析

**技術要件**：
- 適切なオブジェクト関係の設計
- staticメンバーを活用した共通処理
- カプセル化の徹底
- 保守性を考慮したメソッド設計

**評価ポイント**：

| 評価項目 |
| :--- |
| 複雑な要件を適切にクラス分割できている |
| オブジェクト間の関係が適切に設計されている |
| コードの再利用性が高い |
| 将来の機能拡張を考慮した設計になっている |

### セルフチェック課題

各演習完了後、以下の項目で自己評価を行ってください：

**設計能力チェック**

| チェック項目 |
| :--- |
| 現実世界の概念をクラスとして適切にモデル化できる |
| クラス間の関係性を論理的に設計できる |
| カプセル化の原則を理解し実践できる |
| 適切な責任分担でメソッドを設計できる |

**実装技能チェック**

| チェック項目 |
| :--- |
| コンストラクタを適切に実装できる |
| アクセス修飾子を正しく使い分けられる |
| オブジェクトの生成と操作が正確にできる |
| 複数のオブジェクトを連携させられる |

**問題解決能力チェック**

| チェック項目 |
| :--- |
| 要件を分析してクラス設計に落とし込める |
| 実用的な機能を実装できる |
| エラーケースを想定した実装ができる |
| コードの保守性と拡張性を考慮できる |

## 3.7 実践的な技術解説

ここまで学んだ概念を実際のコードで実装する方法を詳しく見ていきましょう。章末演習で必要となる技術的な知識をまとめて解説します。

### コンストラクタの実装パターン

コンストラクタは、オブジェクトの初期化を担当する特別なメソッドです。実際のプログラミングでは、さまざまなパターンでコンストラクタを実装します。

#### 基本的なコンストラクタ

```java
public class Student {
    private String name;
    private int id;
    private int age;
    
    // 基本的なコンストラクタ
    public Student(String name, int id, int age) {
        this.name = name;
        this.id = id;
        this.age = age;
    }
}
```

#### デフォルトコンストラクタ

引数なしのコンストラクタをデフォルトコンストラクタと呼びます：

```java
public class Student {
    private String name;
    private int id;
    private int age;
    
    // デフォルトコンストラクタ
    public Student() {
        this.name = "未設定";
        this.id = 0;
        this.age = 0;
    }
}
```

#### コンストラクタオーバーロード

同じクラスに複数のコンストラクタを定義できます：

```java
public class Student {
    private String name;
    private int id;
    private int age;
    
    // デフォルトコンストラクタ
    public Student() {
        this("未設定", 0, 0);  // 他のコンストラクタを呼び出す
    }
    
    // 名前のみを受け取るコンストラクタ
    public Student(String name) {
        this(name, 0, 0);  // 他のコンストラクタを呼び出す
    }
    
    // すべての値を受け取るコンストラクタ
    public Student(String name, int id, int age) {
        this.name = name;
        this.id = id;
        this.age = age;
    }
}
```

### thisキーワードの実践的な使い方

`this`キーワードは、現在のオブジェクト自身を参照するために使用します。主な用途を見ていきましょう。

#### パラメータとフィールドの区別

フィールド名とパラメータ名が同じ場合、`this`を使って区別します：

```java
public class Rectangle {
    private double width;
    private double height;
    
    public void setDimensions(double width, double height) {
        this.width = width;    // this.widthはフィールド、widthはパラメータ
        this.height = height;  // this.heightはフィールド、heightはパラメータ
    }
}
```

#### コンストラクタチェイン

`this()`を使って、同じクラスの別のコンストラクタを呼び出せます：

```java
public class BankAccount {
    private String accountNumber;
    private String accountHolder;
    private double balance;
    
    // 基本コンストラクタ
    public BankAccount(String accountNumber, String accountHolder, double balance) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = balance;
    }
    
    // 残高0で口座を開設
    public BankAccount(String accountNumber, String accountHolder) {
        this(accountNumber, accountHolder, 0.0);  // 上記のコンストラクタを呼び出す
    }
    
    // 口座番号のみで開設（名義人は後で設定）
    public BankAccount(String accountNumber) {
        this(accountNumber, "未設定", 0.0);  // 最初のコンストラクタを呼び出す
    }
}
```

#### メソッドチェインの実装

`this`を返すことで、メソッドチェインを実現できます：

```java
public class StringBuilder {
    private String content = "";
    
    public StringBuilder append(String str) {
        this.content += str;
        return this;  // 自分自身を返す
    }
    
    public StringBuilder appendLine(String str) {
        this.content += str + "\n";
        return this;  // 自分自身を返す
    }
    
    public String toString() {
        return this.content;
    }
}

// 使用例
StringBuilder sb = new StringBuilder();
String result = sb.append("Hello")
                  .append(" ")
                  .append("World")
                  .appendLine("!")
                  .toString();  // "Hello World!\n"
```

### メソッドオーバーロードの実践

メソッドオーバーロードは、同じ名前で異なるパラメータを持つメソッドを定義する技法です。

#### 基本的なオーバーロード

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
    
    // 配列の要素をすべて加算
    public int add(int[] numbers) {
        int sum = 0;
        for (int num : numbers) {
            sum += num;
        }
        return sum;
    }
}
```

#### 実用的なオーバーロードの例

```java
public class Time {
    private int hours;
    private int minutes;
    private int seconds;
    
    // 時・分・秒を指定
    public Time(int hours, int minutes, int seconds) {
        setTime(hours, minutes, seconds);
    }
    
    // 時・分のみ指定（秒は0）
    public Time(int hours, int minutes) {
        this(hours, minutes, 0);
    }
    
    // 総秒数から作成
    public Time(int totalSeconds) {
        this(totalSeconds / 3600, 
             (totalSeconds % 3600) / 60, 
             totalSeconds % 60);
    }
    
    // 時刻を設定するメソッドのオーバーロード
    public void setTime(int hours, int minutes, int seconds) {
        if (isValidTime(hours, minutes, seconds)) {
            this.hours = hours;
            this.minutes = minutes;
            this.seconds = seconds;
        }
    }
    
    public void setTime(int hours, int minutes) {
        setTime(hours, minutes, 0);
    }
    
    public void setTime(int totalSeconds) {
        setTime(totalSeconds / 3600, 
                (totalSeconds % 3600) / 60, 
                totalSeconds % 60);
    }
    
    private boolean isValidTime(int hours, int minutes, int seconds) {
        return hours >= 0 && hours < 24 &&
               minutes >= 0 && minutes < 60 &&
               seconds >= 0 && seconds < 60;
    }
}
```

### 実践的な設計パターン

#### ビルダパターンの基礎

複雑なオブジェクトを段階的に構築する際に使用します：

```java
public class Student {
    private String name;
    private int id;
    private int age;
    private String major;
    private double gpa;
    
    // プライベートコンストラクタ
    private Student(String name, int id, int age, String major, double gpa) {
        this.name = name;
        this.id = id;
        this.age = age;
        this.major = major;
        this.gpa = gpa;
    }
    
    // Builderクラス
    public static class Builder {
        private String name;
        private int id;
        private int age;
        private String major = "未定";
        private double gpa = 0.0;
        
        public Builder(String name, int id) {
            this.name = name;
            this.id = id;
        }
        
        public Builder age(int age) {
            this.age = age;
            return this;
        }
        
        public Builder major(String major) {
            this.major = major;
            return this;
        }
        
        public Builder gpa(double gpa) {
            this.gpa = gpa;
            return this;
        }
        
        public Student build() {
            return new Student(name, id, age, major, gpa);
        }
    }
}

// 使用例
Student student = new Student.Builder("田中太郎", 12345)
                            .age(20)
                            .major("情報工学")
                            .gpa(3.5)
                            .build();
```

#### ファクトリメソッドパターン

オブジェクトの生成をカプセル化する手法です：

```java
public class BankAccount {
    private String accountNumber;
    private String type;
    private double balance;
    
    private BankAccount(String accountNumber, String type, double balance) {
        this.accountNumber = accountNumber;
        this.type = type;
        this.balance = balance;
    }
    
    // 普通預金口座を作成するファクトリメソッド
    public static BankAccount createSavingsAccount(String accountNumber) {
        return new BankAccount(accountNumber, "普通預金", 0.0);
    }
    
    // 当座預金口座を作成するファクトリメソッド
    public static BankAccount createCheckingAccount(String accountNumber, double initialBalance) {
        return new BankAccount(accountNumber, "当座預金", initialBalance);
    }
    
    // 定期預金口座を作成するファクトリメソッド
    public static BankAccount createFixedDepositAccount(String accountNumber, double amount) {
        if (amount < 10000) {
            throw new IllegalArgumentException("定期預金は1万円以上必要です");
        }
        return new BankAccount(accountNumber, "定期預金", amount);
    }
}
```

### 演習課題で役立つテクニック

#### 配列を使った管理クラスの実装

```java
public class Library {
    private Book[] books;
    private int bookCount;
    private static final int MAX_BOOKS = 1000;
    
    public Library() {
        this.books = new Book[MAX_BOOKS];
        this.bookCount = 0;
    }
    
    public boolean addBook(Book book) {
        if (bookCount < MAX_BOOKS) {
            books[bookCount] = book;
            bookCount++;
            return true;
        }
        return false;
    }
    
    public Book findBookByTitle(String title) {
        for (int i = 0; i < bookCount; i++) {
            if (books[i].getTitle().equals(title)) {
                return books[i];
            }
        }
        return null;
    }
    
    public boolean removeBook(String title) {
        for (int i = 0; i < bookCount; i++) {
            if (books[i].getTitle().equals(title)) {
                // 配列を詰める
                for (int j = i; j < bookCount - 1; j++) {
                    books[j] = books[j + 1];
                }
                books[bookCount - 1] = null;
                bookCount--;
                return true;
            }
        }
        return false;
    }
}
```

#### バリデーション処理の実装

```java
public class Student {
    private String name;
    private int age;
    private double gpa;
    
    public Student(String name, int age, double gpa) {
        setName(name);
        setAge(age);
        setGpa(gpa);
    }
    
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("名前は必須です");
        }
        this.name = name.trim();
    }
    
    public void setAge(int age) {
        if (age < 0 || age > 150) {
            throw new IllegalArgumentException("年齢は0〜150の範囲で入力してください");
        }
        this.age = age;
    }
    
    public void setGpa(double gpa) {
        if (gpa < 0.0 || gpa > 4.0) {
            throw new IllegalArgumentException("GPAは0.0〜4.0の範囲で入力してください");
        }
        this.gpa = gpa;
    }
}
```

これらの技術的な知識を活用して、章末演習に取り組んでみてください。実際にコードを書きながら、オブジェクト指向の概念を体得していきましょう。

## まとめ

本章では、オブジェクト指向プログラミングの基本概念を学習しました。クラス、オブジェクト、カプセル化、コンストラクタなど、Javaプログラミングの核となる概念を理解できました。次章では、さらに詳しくクラスとオブジェクトについて学習します。