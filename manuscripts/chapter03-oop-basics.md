# <b>3章</b> <span>オブジェクト指向の考え方</span> <small>現実世界をプログラムで表現する技術</small>

## 本章の学習目標

### 前提知識

本章でオブジェクト指向の考え方を学習するために、いくつかの前提知識があるとスムーズに進められます。

#### 技術的な前提知識

第1章までの内容を理解していることがポイントです。これには、Java開発環境の構築と基本的な操作、コンパイルと実行のプロセス、そして簡単なJavaプログラムの作成と動作確認が含まれます。これらの基礎知識なしに、より複雑なオブジェクト指向の概念を理解することは困難です。

また、Java基本文法での簡単なプログラム作成経験があることを想定しています。変数の宣言と初期化、メソッドの定義と呼び出し、制御構造（if文、ループ文）の使用など、手続き型プログラミングの基本パターンを実際に書いた経験があることで、オブジェクト指向との違いを明確に理解できます。

さらに、メソッドと変数の概念を理解していると、学習がより深まります。メソッドがどのようにデータを処理し、戻り値を返すのか、変数のスコープがどのように決まるのかといった基本概念は、オブジェクト指向でのカプセル化やデータ隠蔽の理解につながります。

#### 概念的前提知識

ソフトウェア開発における実際の問題を実感していると、オブジェクト指向の価値をより深く理解できます。手続き型プログラミングでプログラムが大きくなると、コードの複雑化、メンテナンスの困難さ、機能追加時の影響範囲の拡大といった問題が顕在化します。これらの問題を実際に経験していることで、オブジェクト指向がどのような解決策を提供するのかを深く理解できます。

また、大規模なシステム開発の課題に関心があると、学習の動機づけになります。個人で書く小さなプログラムと、チームで開発する大きなシステムでは、求められる設計思想が大きく異なります。コードの再利用性、保守性、拡張性といった観点への関心があることで、オブジェクト指向の設計原則の価値を理解できます。

### 学習目標

本章では、オブジェクト指向プログラミングの基本的な考え方と実装技術を体系的に学習します。学習目標を以下の4つの側面から設定しています。

#### 知識理解目標

まず、オブジェクト指向パラダイムがどのような背景から生まれ、どのような場面で活用されているのか、その歴史的経緯を理解します。1960年代のSimula言語から始まり、1980年代のSmalltalk、そして現在のJava、C#、Pythonに至るまで、オブジェクト指向の概念がどのように発展してきたかを学びます。これにより、オブジェクト指向が特定の問題を解決するための一つのアプローチであることを理解し、適切な場面で活用できるようになります。

カプセル化、継承、ポリモーフィズムというオブジェクト指向の3つの基本概念を深く理解することも大切な目標です。カプセル化はデータと処理を一体化して外部からの不正な操作を防ぐ仕組み、継承は既存のクラスの機能を再利用して新しいクラスを作る仕組み、ポリモーフィズムは同じインターフェイスで異なる実装を扱える仕組みです。これらの概念を理論だけでなく、実際のコード例を通じて理解します。

クラスとオブジェクトの関係性についても理解を深めていきましょう。クラスは「設計図」であり、オブジェクトはその設計図から作られた「実体」です。この関係を理解することで、なぜクラスベースのオブジェクト指向プログラミングが効果的なのかを深く理解できます。

#### 技能習得目標

実際のプログラミングスキルとして、まず基本的なクラスの設計と実装能力を身につけます。これには、現実世界の概念をクラスとして抽象化する能力、適切なフィールドとメソッドの選択、クラス内部の処理の実装などが含まれます。

コンストラクタの適切な定義と使用方法も大切なスキルです。オブジェクトの初期化はその後の処理の正確性に影響します。適切なコンストラクタの設計により、安全で使いやすいクラスを作ることができます。

さらに、インスタンス変数とメソッドの実装技術を習得し、オブジェクトの状態と振る舞いを適切に表現できるようになります。特に、privateとpublicアクセス修飾子の適切な使用により、カプセル化を実現し、クラスの内部実装を保護する方法を学びます。

#### 設計思考目標

プログラミング技術を超えて、オブジェクト指向的な設計思考を身につけることも目指します。現実世界の複雑な概念をクラスとして抽象化する能力は、大規模なシステム開発で非常に役立ちます。

データと処理をまとめる設計思考を習得することで、責任の明確な、保守しやすいコードを書けるようになります。これは、手続き型プログラミングでは困難だった、大規模システムでの開発効率向上に直結します。

カプセル化によるデータ保護の設計思想も学んでいきます。外部からの不正なアクセスを防ぎ、データの整合性を保護する設計能力は、信頼性の高いソフトウェアを開発する上で大いに役立ちます。

#### 到達レベルの指標

本章を完了した時点で、実世界の概念（商品、人物、車、銀行口座など）をクラスとして適切に設計・実装できるようになります。これには、適切な属性の選択、必要なメソッドの洗い出し、アクセス制御の設定などが含まれます。

複数のクラスを組み合わせた簡単なプログラムが作成できるレベルに到達することも目標です。クラス間の関係を設計し、オブジェクト同士が協調して動作するプログラムを実装できるようになります。

手続き型とオブジェクト指向の違いを具体例で説明できるようになることも目指します。同じ問題を手続き型とオブジェクト指向の両方で実装し、それぞれの利点と課題を理解し、他者に説明できるレベルを目指します。

最終的に、クラス図の基本的な読み書きができるようになります。UMLクラス図は、オブジェクト指向設計のコミュニケーションツールとして広く使われており、この表記法を理解することで、チーム開発での設計共有が可能になります。



## 章の構成

本章では、オブジェクト指向プログラミングの基本的な考え方と実装方法を学習します。プログラミングパラダイムの歴史的な発展を理解した上で、以下の内容を順に学んでいきます：

1. **オブジェクト指向の背景** - ソフトウェア開発の課題と解決アプローチ
2. **オブジェクト指向の基本概念** - 手続き型との違いと3つの基本原則
3. **クラスとオブジェクト** - 設計図と実体の関係性
4. **カプセル化の実践** - データ保護とアクセス制御
5. **静的メンバー** - クラスレベルの共有データと処理
6. **配列の活用** - データの集合を効率的に管理

オブジェクト指向は、大規模なソフトウェア開発において威力を発揮する一つの有効なアプローチです。その考え方と実装技術を実践的に身につけていきます。

## はじめに：C言語からJavaへ - なぜオブジェクト指向が必要なのか

前章では、Javaの基本文法を学習しました。本章では、Javaの核心となる「オブジェクト指向プログラミング」について学習します。

### C言語での開発で直面する実務的な課題

皆さんは1年次にC言語を学習し、構造体と関数を使ったプログラミングを経験してきました。C言語は優れた言語ですが、実際の開発現場では以下のような課題に直面することがあります。

#### 実例：学生管理システムの開発

C言語で学生管理システムを開発する場合を考えてみましょう：

```c
// students.h
struct Student {
    int id;
    char name[50];
    double gpa;
    int year;
};

// students.c
void print_student(struct Student* s) {
    printf("ID: %d, Name: %s, GPA: %.2f\n", s->id, s->name, s->gpa);
}

void update_gpa(struct Student* s, double new_gpa) {
    s->gpa = new_gpa;
}

// main.c
int main() {
    struct Student student1 = {1001, "田中太郎", 3.5, 2};
    
    // 問題1: 誰でも直接データを変更できてしまう
    student1.gpa = -5.0;  // 不正な値でも設定可能
    
    // 問題2: 関連する関数がどこにあるか分かりにくい
    // print_student? display_student? show_student?
    
    return 0;
}
```

### 構造体と関数を分離することの限界

1. データの保護ができない
構造体のメンバーは誰でも直接アクセスできるため、不正な値が設定されるリスクがあります。例えば、GPAに負の値を設定したり、学年に100を設定したりすることを防ぐには、すべてのアクセス箇所でチェックが必要になります。

2. 関連する関数の管理が困難
学生に関する関数（表示、更新、削除など）が増えると、どの関数がどの構造体に対応するのか分かりにくくなります。命名規則で対応しようとしても、大規模プロジェクトでは限界があります。

3. 仕様変更時の影響範囲が大きい
構造体のメンバーを変更すると、その構造体を使用しているすべての関数を修正する必要があります。例えば、`name`を`first_name`と`last_name`に分割する場合、影響は広範囲に及びます。

### より実践的な例：銀行口座システム

```c
// C言語での実装の問題点
struct BankAccount {
    char account_number[20];
    double balance;
    char owner_name[50];
};

// グローバル変数や外部からの直接アクセスによる問題
void transfer_money(struct BankAccount* from, struct BankAccount* to, double amount) {
    // 残高チェックを忘れやすい
    from->balance -= amount;  // 残高がマイナスになる可能性
    to->balance += amount;
}

// 異なる開発者が独自の関数を作成
void withdraw_money(struct BankAccount* acc, double amount) {
    acc->balance -= amount;  // また残高チェックを忘れている！
}
```

このような問題は、プロジェクトが大きくなるほど深刻になります。データと処理が分離していることで、以下の問題が発生します：

- 一貫性の欠如: 同じ処理でも開発者によって異なる実装
- バグの温床: データ検証の漏れが各所で発生
- 保守の困難: どの関数がどのデータを操作するか追跡が困難

### オブジェクト指向による解決

オブジェクト指向プログラミングは、これらの実務的な問題を解決するアプローチの一つです：

1. **データと処理の一体化**: 構造体（データ）と関数（処理）を一つのクラスにまとめる
2. **アクセス制御**: privateやpublicなどのアクセス修飾子でデータを保護
3. **責任の明確化**: 各クラスが自身のデータに対する責任を持つ

この章では、C言語での開発経験を活かしながら、オブジェクト指向がどのようにこれらの課題を解決するかを実践的に学んでいきます。

> **補足**: プログラミングパラダイムの歴史的発展について詳しく知りたい方は、GitHubリポジトリの付録資料（`https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b03-programming-paradigms/`）を参照してください。そこでは、ソフトウェアクライシスからオブジェクト指向の誕生までの歴史的経緯を詳しく解説しています。


## オブジェクト指向とは

オブジェクト指向という言葉は、実は多様な意味を持っています。ソフトウェア開発のさまざまな工程で、オブジェクト指向の考え方が適用されているからです。

まず、システム設計の段階では、**オブジェクト指向設計（OOD: Object-Oriented Design）**が用いられます。これは、システムをオブジェクトの集合として設計し、それらの関係性を定義する手法です。同様に、要求分析の段階では**オブジェクト指向分析（OOA: Object-Oriented Analysis）**が、問題領域をオブジェクトの観点から分析します。これらは総称して**オブジェクト指向分析設計（OOAD: Object-Oriented Analysis and Design）**と呼ばれます。

しかし、本書が焦点を当てるのは**オブジェクト指向プログラミング（OOP: Object-Oriented Programming）**です。これは、設計されたオブジェクトを実際にプログラミング言語で実装する技術です。Javaは、OOPを実践するために設計された言語であり、クラス、継承、ポリモーフィズムなどのOOPの主要概念を言語レベルでサポートしています。

さらに幅広い視点では、開発方法論やプロジェクト管理手法としてのオブジェクト指向、そしてプログラミング言語仕様としてのオブジェクト指向も存在しますが、これらは本書の範囲を超える内容です。

## オブジェクト指向を学ぶ意義

オブジェクト指向は、プログラミングの歴史の中で生まれた**多くのアプローチの一つ**です。本書では、オブジェクト指向を特別視するのではなく、**問題解決のための便利なツールの一つ**として学びます。

皆さんは一年次にC言語を講義で学びました。
C言語でのプログラミングでも、構造的なプログラミングや、関数ライブラリを使った柔軟な開発は可能です。
極論を言ってしまえば、新たな道具としてオブジェクト指向を学ばなくても、たいていのプログラムは作れます。

### より良く継続的な開発を行うために

C言語でも十分なプログラミングは可能ですが、現実のソフトウェア開発では、プログラムの作成は全体の一部にすぎません。作成されたプログラムは、その後長期にわたる「保守・運用」のフェーズに入り、この期間が実は開発期間よりもはるかに長く、コストも大きくなることが一般的です。

このような現実を踏まえると、プログラミング言語には単に動くプログラムを作ること以上の要求が突きつけられます。まず、開発の効率化が重要です。同じ機能を何度も実装するのではなく、既存のコードを再利用し、新たな機能をすばやく追加できる必要があります。

次に、保守性の向上が不可欠です。要求の変化やバグ修正の際に、変更箇所が明確で、変更の影響範囲が限定されていることが理想です。そして、品質の向上も欠かせません。バグが少なく、意図が明確で、拡張性の高いコードを書くことが、長期的な成功につながります。

これらの要求を満たすための強力なアプローチが、オブジェクト指向プログラミングなのです。

### 大規模で長期的に維持可能なプログラムを作る

C言語でも不可能ではありませんが「もっと効率良くしたい」という要望のために、オブジェクト指向という考え方が登場します。
より効率良くプログラミングを行うことを目的として、保守運用を見据えた概念であることをよく理解してオブジェクト指向を学んでください。

実際、現代のプログラミングでは、オブジェクト指向だけでなく、関数型プログラミング、並行プログラミング、リアクティブプログラミングなど、さまざまなパラダイムを組み合わせて使うことが一般的です。
オブジェクト指向は、これらの中の一つのアプローチに過ぎません。









## オブジェクト指向の基本概念を実際のコードで理解する

理論的な説明の前に、実際のJavaコードを見てオブジェクト指向の基本概念を理解しましょう。

### 手続き型とオブジェクト指向の違い

#### 手続き型の例（C言語風）
手続き型プログラミングの典型的な構造：

手続き型プログラミングでは、データと処理が完全に分離された構造となります。以下の例では、この分離がどのような問題を引き起こすかを示しています。

<span class="listing-number">**サンプルコード3-1**</span>

```java
public class ProceduralExample {
    public static void main(String[] args) {
        String studentName = "田中太郎";  // ①
        int studentAge = 20;             // ①
        double studentGpa = 3.5;         // ①
        
        printStudent(studentName, studentAge, studentGpa);  // ②
    }
    
    public static void printStudent(String name, int age, double gpa) {  // ③
        System.out.println("名前: " + name + ", 年齢: " + age + ", GPA: " + gpa);
    }
}
```

構造的問題の分析：

①　データの散在: 学生に関する情報（名前、年齢、GPA）が独立した変数として存在し、これらが一つの概念（学生）を表すという関係性がコード上で明確でない

②　パラメータ渡しの煩雑さ: 関連するデータを処理するたびに、すべてのパラメータを個別に渡す必要があり、データの追加や変更時に多くの関数の引数リストを修正する必要がある

③　責任の分散: 学生データの検証、変更、表示など、学生に関する処理が複数の場所に散らばり、どこで何をしているかが把握しにくい

#### オブジェクト指向の例
オブジェクト指向プログラミングによる改善されたアプローチ：

オブジェクト指向では、関連するデータと処理を1つのクラスに統合し、概念の一貫性と責任の明確化を実現します。

<span class="listing-number">**サンプルコード3-2**</span>

```java
public class Student {
    private String name;  // ①
    private int age;      // ①
    private double gpa;   // ①
    
    public Student(String name, int age, double gpa) {  // ②
        this.name = name;
        this.age = age;
        this.gpa = gpa;
    }
    
    public void printInfo() {  // ③
        System.out.println("名前: " + name + ", 年齢: " + age + ", GPA: " + gpa);
    }
}

public class ObjectOrientedExample {
    public static void main(String[] args) {
        Student student = new Student("田中太郎", 20, 3.5);  // ④
        student.printInfo();  // ⑤
    }
}
```

オブジェクト指向の利点の実現：

①　データのカプセル化: 学生に関するすべての属性がprivateフィールドとして一箇所に集約され、外部からの直接アクセスを制限

②　初期化の保証: コンストラクタにより、オブジェクト生成時に必要なデータがすべて設定されることを保証

③　振る舞いの局所化: 学生データの表示処理がStudentクラス内部に定義され、データとその操作の責任が明確

④　概念の一体性: `Student`オブジェクトとして学生という概念を直接的にプログラムで表現

⑤　インターフェイスの簡潔性: 外部からは単純にメソッドを呼び出すだけで、内部の複雑さを隠蔽

### オブジェクト指向の3つの基本原則

#### カプセル化

データ保護と制御されたアクセスの実現：

カプセル化は、オブジェクトの内部データを外部から直接アクセスできないように保護し、適切なメソッドを通じてのみ操作を許可する仕組みです。

<span class="listing-number">**サンプルコード3-3**</span>

```java
public class BankAccount {
    private double balance;  // ①
    
    public void deposit(double amount) {  // ②
        if (amount > 0) {
            balance += amount;
        }
    }
    
    public double getBalance() {  // ③
        return balance;
    }
}
```

カプセル化の効果：

①　データの隠蔽: `private`修飾子により残高データを直接操作不可能にし、不正な値の設定を防止

②　制御された変更: 入金処理では金額の妥当性を検証してから残高を更新し、負の値の入金を防ぐ

③　安全な読み取り: 残高の確認は可能だが、直接的な変更はできないため、データの整合性を保証

#### 継承

コードの再利用と階層構造の構築：

継承は、既存のクラス（親クラス）の機能を新しいクラス（子クラス）が引き継ぎ、さらに独自の機能を追加する仕組みです。

<span class="listing-number">**サンプルコード3-4**</span>

```java
public class Product {
    protected String productId;  // ①
    protected String name;       // ①
    protected int price;         // ①
    
    public void displayInfo() {  // ②
        System.out.println("商品名: " + name + ", 価格: " + price + "円");
    }
}

public class Book extends Product {  // ③
    private String author;     // ④
    private String isbn;       // ④
    
    public void displayBookInfo() {  // ⑤
        displayInfo();  // ②を呼び出し
        System.out.println("著者: " + author + ", ISBN: " + isbn);
    }
}
```

継承による機能拡張：

①　共通属性の継承: 商品ID、名前、価格など、すべての商品に共通する属性を親クラスで定義

②　共通機能の継承: 基本的な商品情報表示機能を親クラスで実装し、子クラスでも利用可能

③　is-a関係の表現: 「本は商品である」という概念的関係をextends文で明確に表現

④　専用属性の追加: 本特有の情報（著者、ISBN）を子クラスで独自に定義

⑤　機能の組み合わせ: 親クラスの機能を活用しつつ、子クラス独自の機能を追加

#### ポリモーフィズム

同一インターフェイスによる多様な実装の統一的扱い：

ポリモーフィズム（多態性）は、同じインターフェイスを通じて異なる実装を統一的に扱える仕組みです。これにより、新しい実装を追加してもクライアントコードを変更する必要がありません。

<span class="listing-number">**サンプルコード3-5**</span>

```java
public interface PaymentMethod {  // ①
    void processPayment(double amount);
}

public class CreditCardPayment implements PaymentMethod {  // ②
    private String cardNumber;
    
    public void processPayment(double amount) {  // ③
        System.out.println("クレジットカードで" + amount + "円を決済しました");
    }
}

public class BankTransferPayment implements PaymentMethod {  // ②
    private String accountNumber;
    
    public void processPayment(double amount) {  // ④
        System.out.println("銀行振込で" + amount + "円を送金しました");
    }
}
```

ポリモーフィズムの仕組み：

①　統一インターフェイス: すべての決済方法が実装すべき共通のメソッド仕様を定義

②　多様な実装: 同じインターフェイスに対して、具体的な決済手段ごとに異なる実装を提供

③　クレジットカード固有処理: カード決済に特化した具体的な処理を実装

④　銀行振込固有処理: 銀行振込に特化した具体的な処理を実装

この設計により、`PaymentMethod`型の変数で異なる決済方法を統一的に扱えるため、新しい決済方法（PayPal、仮想通貨など）を追加しても既存のコードに影響を与えません。

## クラスの作成

オブジェクト指向プログラミングでは、**クラス**という設計図を作成し、その設計図から**オブジェクト（インスタンス）**を生成します。

### クラスの基本構造

クラスは主に以下の要素で構成されます：
1. **フィールド（属性）** - オブジェクトが持つデータ
2. **メソッド（操作）** - オブジェクトができる処理
3. **コンストラクタ** - オブジェクトを初期化する特別なメソッド

これらの要素について、順に詳しく見ていきましょう。

### フィールド（属性）の基礎

フィールドは、オブジェクトが持つデータを表現します。各オブジェクトは自分専用のフィールドの値を持ちます。

<span class="listing-number">**サンプルコード3-6**</span>

```java
public class Student {
    // フィールドの宣言
    String name;           // 名前
    int age;              // 年齢
    double height;        // 身長（cm）
    boolean isActive;     // 在籍中かどうか
    
    // 初期値を持つフィールド
    String school = "東京大学";    // 学校名（初期値付き）
    int grade = 1;                // 学年（初期値: 1年生）
}
```

フィールドの重要なポイント：
- フィールドは通常、クラスの最初に宣言します
- 各フィールドには型（String、int、doubleなど）が必要です
- 初期値を設定しない場合、デフォルト値が自動的に設定されます
- フィールド名は意味が分かりやすい名前にします

#### フィールドのデフォルト値

<span class="listing-number">**サンプルコード3-7**</span>

```java
public class DefaultValueDemo {
    // 数値型のデフォルト値
    int intValue;         // 0
    double doubleValue;   // 0.0
    
    // その他の型のデフォルト値
    boolean boolValue;    // false
    char charValue;       // '\u0000' (空文字)
    String stringValue;   // null
    
    public void showDefaults() {
        System.out.println("int: " + intValue);        // 0
        System.out.println("double: " + doubleValue);  // 0.0
        System.out.println("boolean: " + boolValue);   // false
        System.out.println("String: " + stringValue);  // null
    }
    
    public static void main(String[] args) {
        DefaultValueDemo demo = new DefaultValueDemo();
        demo.showDefaults();
    }
}
```

### フィールド（インスタンス変数）の詳細

フィールドは、オブジェクトの状態を表現する変数です。クラス内で宣言され、そのクラスから生成される各オブジェクトが独自の値を保持します。

#### フィールドの宣言構文

```java
アクセス修飾子 データ型 フィールド名;
アクセス修飾子 データ型 フィールド名 = 初期値;
```

**実際の使用例：**

<span class="listing-number">**サンプルコード3-8**</span>

```java
public class Car {
    // フィールドの宣言
    private String model;      // 車種
    private String color;      // 色  
    private int year;         // 年式
    private double mileage;   // 走行距離
    
    // コンストラクタでフィールドを初期化
    public Car(String model, String color, int year) {
        this.model = model;    // thisは現在のオブジェクトを指す
        this.color = color;
        this.year = year;
        this.mileage = 0.0;    // 新車なので0km
    }
    
    // フィールドを使った処理
    public void drive(double distance) {
        if (distance > 0) {
            mileage += distance;  // 走行距離を更新
            System.out.println(distance + "km走行しました");
        }
    }
    
    // フィールドの値を表示
    public void showInfo() {
        System.out.println("車種: " + model);
        System.out.println("色: " + color);
        System.out.println("年式: " + year + "年");
        System.out.println("走行距離: " + mileage + "km");
    }
}
```

#### フィールドのデフォルト値

フィールドに初期値を指定しない場合、以下のデフォルト値が自動的に設定されます：

| データ型 | デフォルト値 |
|---------|------------|
| byte, short, int, long | 0 |
| float, double | 0.0 |
| char | '\u0000' |
| boolean | false |
| 参照型（クラス、配列など） | null |

### メソッド（操作）の基礎

メソッドは、オブジェクトが「できること」を定義します。C言語の関数と似ていますが、オブジェクトに属している点が異なります。

#### メソッドの宣言構文

```java
アクセス修飾子 戻り値の型 メソッド名(引数リスト) {
    // メソッドの処理
    return 戻り値;  // 戻り値がある場合
}
```

<span class="listing-number">**サンプルコード3-9**</span>

```java
public class Rectangle {
    // フィールド
    private double width;
    private double height;
    
    // コンストラクタ（特別なメソッド）
    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    // 戻り値があるメソッド
    public double calculateArea() {
        return width * height;
    }
    
    // 戻り値があるメソッド
    public double calculatePerimeter() {
        return 2 * (width + height);
    }
    
    // 戻り値がないメソッド（void）
    public void displayInfo() {
        System.out.println("幅: " + width);
        System.out.println("高さ: " + height);
        System.out.println("面積: " + calculateArea());
        System.out.println("周囲: " + calculatePerimeter());
    }
    
    // 引数を受け取るメソッド
    public void resize(double factor) {
        if (factor > 0) {
            width *= factor;
            height *= factor;
            System.out.println(factor + "倍にリサイズしました");
        }
    }
}

// 使用例
public class RectangleExample {
    public static void main(String[] args) {
        // オブジェクトの作成
        Rectangle rect = new Rectangle(10.0, 5.0);
        
        // メソッドの呼び出し
        rect.displayInfo();
        
        // メソッドの戻り値を使用
        double area = rect.calculateArea();
        System.out.println("面積は" + area + "です");
        
        // 引数付きメソッドの呼び出し
        rect.resize(2.0);
        rect.displayInfo();
    }
}
```

#### メソッドの種類

1. 戻り値がないメソッド（voidメソッド）
```java
public void printMessage() {
    System.out.println("こんにちは！");
}
```

2. 戻り値があるメソッド
```java
public int add(int a, int b) {
    return a + b;  // int型の値を返す
}
```

3. 引数がないメソッド
```java
public void showTime() {
    System.out.println("現在時刻: " + new Date());
}
```

4. 複数の引数を持つメソッド
```java
public double calculateBMI(double weight, double height) {
    return weight / (height * height);
}
```

### getter/setterメソッドの基本

オブジェクト指向プログラミングでは、フィールドをprivateにして直接アクセスを禁止し、publicメソッドを通じてアクセスする方法が推奨されます。これをアクセサメソッド（getter/setter）パターンと呼びます。

<span class="listing-number">**サンプルコード3-10**</span>

```java
public class Person {
    // privateフィールド
    private String name;
    private int age;
    
    // getter：フィールドの値を取得
    public String getName() {
        return name;
    }
    
    // setter：フィールドの値を設定
    public void setName(String name) {
        this.name = name;  // thisは現在のオブジェクトを指す
    }
    
    // 年齢のgetter
    public int getAge() {
        return age;
    }
    
    // 年齢のsetter（簡単な検証付き）
    public void setAge(int age) {
        if (age >= 0 && age <= 150) {
            this.age = age;
        } else {
            System.out.println("無効な年齢です");
        }
    }
}
```

getter/setterを使う利点：
- フィールドへのアクセスを制御できる
- 値の設定時に検証を行える
- 将来的に実装を変更しても、外部インターフェースは変わらない
- デバッグ時にアクセスポイントを特定しやすい

> 注意: すべてのフィールドに機械的にgetter/setterを作成するのは避けましょう。本当に外部からアクセスが必要なフィールドにのみ提供し、可能な限りオブジェクトの内部状態は隠蔽することが良い設計です。

### コンストラクタ（初期化メソッド）

コンストラクタは、オブジェクトを作成するときに自動的に呼ばれる特別なメソッドです。

<span class="listing-number">**サンプルコード3-11**</span>

```java
public class Book {
    private String title;
    private String author;
    private int pages;
    
    // コンストラクタ（クラス名と同じ名前）
    public Book(String title, String author, int pages) {
        this.title = title;
        this.author = author;
        this.pages = pages;
    }
    
    // デフォルトコンストラクタ（引数なし）
    public Book() {
        this.title = "未設定";
        this.author = "不明";
        this.pages = 0;
    }
    
    public void displayInfo() {
        System.out.println("タイトル: " + title);
        System.out.println("著者: " + author);
        System.out.println("ページ数: " + pages);
    }
}
}
```

### メソッドオーバーロード（同じ名前のメソッド）

Javaでは、同じ名前のメソッドを複数定義できます。これを「メソッドオーバーロード」と呼びます。

<span class="listing-number">**サンプルコード3-12**</span>

```java
public class PrintHelper {
    // 文字列を出力
    public void print(String message) {
        System.out.println(message);
    }
    
    // 整数を出力
    public void print(int number) {
        System.out.println("数値: " + number);
    }
    
    // 2つの値を出力
    public void print(String label, int value) {
        System.out.println(label + ": " + value);
    }
}

// 使用例
public class OverloadExample {
    public static void main(String[] args) {
        PrintHelper helper = new PrintHelper();
        
        // 同じprintという名前だが、引数によって異なるメソッドが呼ばれる
        helper.print("こんにちは");          // String版
        helper.print(42);                   // int版
        helper.print("年齢", 20);           // String, int版
    }
}
```

メソッドオーバーロードのポイント：
- 引数の数や型が異なれば、同じ名前のメソッドを作れる
- Javaが自動的に適切なメソッドを選んで実行する
- コードが読みやすくなる

## 実用的なクラス設計例

### 例1：図書管理システム

図書館の蔵書管理システムを想定したBookクラスの例です。このクラスでは、本の基本情報と貸出状況を管理し、貸出・返却処理を安全に行う仕組みを提供します。

<span class="listing-number">**サンプルコード3-13**</span>

```java
public class Book {
    private String isbn;
    private String title;
    private String author;
    private boolean isAvailable;
    
    public Book(String isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.isAvailable = true;
    }
    
    public boolean borrow() {
        if (isAvailable) {
            isAvailable = false;
            return true;
        }
        return false;
    }
    
    public void returnBook() {
        isAvailable = true;
    }
    
    public String getInfo() {
        String status = isAvailable ? "貸出可能" : "貸出中";
        return String.format("%s - %s (%s) [%s]", isbn, title, author, status);
    }
}
```

このBookクラスでは、boolean型のisAvailableフィールドで貸出状況を管理し、メソッドでの状態変更を適切に制御しています。

### 例2：ショッピングカート

ECサイトのショッピングカート機能を実装したクラスです。商品の追加、合計金額の計算（税込）、カート内容の表示など、ショッピングに必要な基本機能を提供します。

<span class="listing-number">**サンプルコード3-14**</span>

```java
import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private List<Item> items;
    private double taxRate;
    
    public ShoppingCart(double taxRate) {
        this.items = new ArrayList<>();
        this.taxRate = taxRate;
    }
    
    public void addItem(String name, double price, int quantity) {
        items.add(new Item(name, price, quantity));
    }
    
    public double calculateTotal() {
        double subtotal = 0;
        for (Item item : items) {
            subtotal += item.getSubtotal();
        }
        return subtotal * (1 + taxRate);
    }
    
    public void displayCart() {
        System.out.println("=== ショッピングカート ===");
        for (Item item : items) {
            System.out.println(item);
        }
        System.out.printf("合計（税込）: %.2f円%n", calculateTotal());
    }
    
    // 内部クラス
    private class Item {
        private String name;
        private double price;
        private int quantity;
        
        public Item(String name, double price, int quantity) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }
        
        public double getSubtotal() {
            return price * quantity;
        }
        
        @Override
        public String toString() {
            return String.format("%s - %.2f円 × %d = %.2f円", 
                name, price, quantity, getSubtotal());
        }
    }
}
```

## まとめ

このパートでは、オブジェクト指向の基本概念を実際のコードを通じて学習しました。重要なポイントは：

1. **データと処理の一体化**：クラスは関連するデータと処理をまとめる
2. **カプセル化**：内部実装を隠蔽し、必要なインターフェイスのみを公開
3. **オブジェクトの生成と操作**：newキーワードでインスタンスを生成し、メソッドを通じて操作
4. **現実世界のモデリング**：実世界の概念をクラスとして表現










## オブジェクト指向の重要な用語と概念

ここからは、オブジェクト指向プログラミングで使用される重要な用語と概念を、実践的な例を交えて説明します。

### オブジェクトとは

オブジェクトは、コンピュータのメモリ上に展開された、プログラム内の「状態」と「振る舞い」を統合した存在です。「状態」とは、オブジェクトが保持するデータを指し、変数やフィールドとして実装されます。一方、「振る舞い」は、オブジェクトが実行できる処理内容であり、メソッド（関数）として実装されます。

重要なのは、これらの状態と振る舞いが別々に存在するのではなく、密接な関連を持って一体化されている点です。たとえば、銀行口座オブジェクトであれば、残高（状態）と入金・出金操作（振る舞い）が1つのオブジェクトとして管理されることで、データの整合性を保ちやすくなります。

### クラスとは

オブジェクトはメモリ上に展開されて使用されます。
そのオブジェクトは、どんな状態を持って、どんな振る舞いをするのかを定義するのがクラスです。

よくある表現として、オブジェクトの設計図をクラスという言い方がされます。

### インスタンス（実体）とは
オブジェクトは、メモリ上に展開されて使用されます。
その状態をインスタンスと呼び、メモリ上に展開して使用できるようにすることをインスタンス化（実体化）と言います。

### クラス、インスタンス、オブジェクトの関係

オブジェクト指向プログラミングでは、「クラス」「インスタンス」「オブジェクト」という用語が頻繁に登場し、初学者にとって混乱の原因となることがあります。これらの関係を明確に理解することが重要です。

**クラス**は、オブジェクトの設計図です。建築にたとえれば、家の設計図にあたります。クラスは、どのようなデータ（フィールド）を持ち、どのような操作（メソッド）ができるかを定義します。

**インスタンス**は、クラスをもとに実際に作成された具体的な実体です。設計図から実際に建てられた家に相当します。1つのクラスから複数のインスタンスを作成でき、それぞれが独立したデータを保持します。

**オブジェクト**という用語は、文脈によって意味が変わることに注意が必要です。多くの場合、オブジェクトはインスタンスを指しますが、より幅広い概念を表す場合や、クラス自体を指す場合もあります。「オブジェクト指向」という場合のオブジェクトは、より抽象的な概念を表しています。

※厳密には、インスタンス化をしなくても内部的に使用できる状態や振る舞いというのも存在します。それらは「**静的（static）な**○○」と呼ばれ、インスタンス化を明示的に行わなくても、プログラム実行時に自動的にメモリへ展開されており使用できるようになっています。対義語として「動的（dynamic）な○○」という言い方もあり、そちらはインスタンス化しないと使用できません。

### クラスはどのように書くか

何らかのデータと、それに対する処理をまとめて書けると良いです。

「クラスは、C言語における構造体（struct）に、関数をつけられるようにしたもの」のようにイメージすると理解しやすいです。

### 役割ごとに分割する利点

役割ごとに分割することで、大規模なプログラムを作る際に管理しやすくなることがあります。ただし、これは**一つのアプローチ**であり、問題によっては他の分割方法（機能別、レイヤー別など）の方が適切な場合もあります。
管理しやすいとは、複雑でなく、バグの発見・修正が容易なことを指します。

### 手続き型 vs オブジェクト指向

ここでは、手続き型プログラミングとオブジェクト指向プログラミングの根本的な違いを、学生情報を管理するプログラムの例を通じて理解します。この違いは、データの管理方法と責任の所在に関する重要な設計思想の違いを表しています。

#### 手続き型プログラミングの特徴と課題

手続き型プログラミングでは、データ構造と処理が分離されており、グローバルな関数がデータを操作します。この方式は単純で理解しやすい反面、大規模なシステムでは以下のような問題が生じます：

手続き型プログラミング（C言語）
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

手続き型プログラミングの根本的課題：

手続き型プログラミングでは、データと処理の分離によって複数の深刻な問題が生じます。まず、データの一貫性管理が極めて困難になります。Studentデータを変更する関数が複数存在する場合、どの関数がどのタイミングでデータを変更するかを追跡することが困難で、データの整合性を保つためには開発者が常に全体の状況を把握している必要があります。これは大規模なシステムになるほど現実的でなくなります。

次に、変更の影響範囲が広範囲に拡大してしまう問題があります。たとえば、Student構造体に新しいフィールド（たとえば所属学部）を追加する場合、そのデータを使用するすべての関数を見つけ出して修正する必要があります。これは、システムの規模が大きくなるほど見落としやバグの原因となり、保守性を著しく低下させます。

さらに、責任の所在があいまいになることも重要な課題です。データの検証、初期化、出力、業務ロジックなど、学生に関する処理がシステム全体に散らばってしまうため、どの部分がどの機能を担当しているのかが不明確になります。これにより、バグの原因特定や機能追加が困難になり、開発効率が大幅に低下します。

#### オブジェクト指向プログラミングによる解決

オブジェクト指向では、関連するデータとそれを操作する処理を1つのクラスにまとめることで、上記の問題を解決します：

オブジェクト指向プログラミング（Java）
<span class="listing-number">**サンプルコード3-15**</span>

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

オブジェクト指向プログラミングによる問題解決：

オブジェクト指向プログラミングは、手続き型プログラミングの課題を体系的に解決します。最も重要な改善点は責任の明確化です。学生に関するすべての操作がStudentクラス内に集約されることで、データの管理責任が明確になり、どこに何の機能があるかが一目瞭然になります。これにより、バグの原因特定や機能追加が劇的に簡単になります。

カプセル化による保護機能も重要な利点です。privateキーワードを使用することで、外部からの不正なデータアクセスを防ぎ、データの整合性を確実に保護できます。これは、手続き型プログラミングでは実現困難だった、データの安全性を保証する仕組みです。

変更の局所化も大きなメリットです。学生に関する処理の変更は、Studentクラス内のみで完結し、システム全体への影響を最小限に抑えることができます。たとえば、成績計算のロジックを変更する場合、Studentクラス内の該当メソッドのみを修正すれば済み、他のクラスに影響を与えません。

さらに、現実世界のモデリングが可能になることで、プログラムの理解しやすさが向上します。「学生」という概念を直接的にプログラムの構造に反映させることで、直感的で理解しやすいコードになり、チーム開発での意思疎通も円滑になります。

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

→ これをカプセル化（隠ぺい）とよいます。
適切なカプセル化を施すことで、オブジェクトの使いやすさ向上につながります。

### 設計時に振る舞いの名前や入出力情報だけ決める
昨今のオブジェクト指向言語には、インターフェイスという概念があります。
インターフェイスにはクラスの振る舞いの名前や渡されるべきデータ型、振る舞いによる結果のデータ型だけを定義しておけます。

- インターフェイスは、それ自体をデータ型として使える便利なもの
- オブジェクト指向は、プログラミングだけでなく設計としての側面も強い

### オブジェクト指向言語でつまずやすい継承

難しく考える必要はありません。
同じ状態、同じ振る舞いを持つクラスをそれぞれコピー&ペーストで作ると、管理が煩雑になってしまいます。

「似たデータ、似た処理をもつオブジェクトを、クラスの時点でまとめよう」として使用するのが継承という考え方になります。
単にまとめるための手段だと知っておきましょう。

（「従業員クラス」「マネージャークラス」がそれぞれ「人事管理可能」インターフェイスを実装することで、どちらも「人事評価機能」を持つといった設計もよくあります。教育的な例としては有効ですが、実際のシステムでは権限管理やロールベースのアクセス制御など、より洗練されたアプローチが必要になることが多いです）

### システム開発に「銀の弾丸などない」

フレッド・ブルックスが1975年に著した「人月の神話」で述べたように、ソフトウェア開発の複雑性を一挙に解決する「銀の弾丸」は存在しません。オブジェクト指向プログラミングも例外ではなく、万能の解決策ではありません。

オブジェクト指向を不適切に適用した場合、返って問題を悪化させることがあります。たとえば、ごく小規模なスクリプトやユーティリティプログラムにオブジェクト指向を無理に取り入れると、シンプルな処理が複雑なクラス構造に埋もれてしまい、コードの記述量が膨大になることがあります。

また、バッチ処理やパイプライン処理のように、単機能の処理が連続して呼び出されるシステムでは、オブジェクト指向のオーバーヘッドが大きくなり、手続き型のアプローチの方が適切な場合があります。さらに、過度な抽象化や継承の乱用は、コードの理解を困難にし、管理しきれない副作用を発生させる可能性があります。

重要なのは、オブジェクト指向が適切な場面を見極め、適度に使用することです。問題の性質、システムの規模、開発チームのスキルセットなどを考慮して、最適なアプローチを選択することが、真のプロフェッショナルに求められるスキルです。

### オブジェクト指向は効率良く開発を行うための考え方

歴史をたどると、システム開発の効率を求めた結果、オブジェクト指向という体系ができたに過ぎません。
これは通過点であり、オブジェクト指向の考え方を学ぶことですべてのことが説明できる気になったりましますが、これですべてを賄えることはありません。
本書では、オブジェクト指向という道具を学びますが、これはただのプログラミングテクニックの1つとして、使いたいときに使えるよう、適切な使い方を身に付けましょう。

## クラスとオブジェクト

### クラスの定義

クラスの定義は、オブジェクト指向プログラミングの核心部分です。ここでは、「本」という現実世界の概念をプログラムで表現する方法を学びます。クラス設計では、そのオブジェクトが「何を知っているか（属性）」と「何ができるか（操作）」を明確に定義することが重要です。

以下のBookクラスは、本という概念の本質的な属性と操作を表現した例です：

<span class="listing-number">**サンプルコード3-16**</span>

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

このクラス定義から学ぶ重要な概念：

1. **カプセル化の実践**：すべてのフィールドをprivateで宣言することで、外部からの直接アクセスを禁止し、データの整合性を保護しています。

2. **コンストラクタによる初期化**：オブジェクト作成時に必要な情報をすべて受け取ることで、不完全なオブジェクトの生成を防いでいます。

3. **責任の明確化**：本に関する表示処理（displayInfo）を本クラス自身が担当することで、「本は自分の情報を表示できる」という自然な責任分担を実現しています。

4. **制御されたアクセス**：setPrice()メソッドで価格の妥当性をチェックすることで、ビジネスルール（価格は0以上）をクラス内で守っています。

5. **情報隠蔽の原則**：読み取り専用の情報（title, author）はgetterのみを提供し、変更可能な情報（price）には検証付きのsetterを提供するという、アクセス制御の使い分けを示しています。

### オブジェクトの作成と使用

クラスを定義しただけでは、まだ実際にデータを保存したり処理を実行したりすることはできません。クラスは「設計図」であり、実際に動作するプログラムを作るには、その設計図からオブジェクト（インスタンス）を作成する必要があります。

以下のプログラムは、Bookクラスから実際のオブジェクトを作成し、それらを操作する方法を示しています：

<span class="listing-number">**サンプルコード3-17**</span>

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

このプログラムから学ぶオブジェクト指向の重要な概念：

1. **オブジェクトの独立性**：book1とbook2は同じBookクラスから作られていますが、それぞれ独立したデータを持っています。一方のオブジェクトの状態を変更しても、他方には影響しません。

2. **new演算子の役割**：`new Book(...)`は、メモリ上にBookオブジェクト用の領域を確保し、コンストラクタを呼び出してオブジェクトを初期化します。これにより、クラスという「型」から具体的な「実体」が作られます。

3. **メッセージパッシング**：`book1.displayInfo()`のような記法は、book1オブジェクトに「自分の情報を表示して」というメッセージを送っていると解釈できます。オブジェクトは自分の責任範囲で処理を実行します。

4. **状態の変更と永続性**：`book1.setPrice(2500.0)`により、book1オブジェクトの内部状態が変更されます。この変更は、そのオブジェクトが存在する限り維持されます。

5. **型安全性**：BookTestクラスでは、Bookオブジェクトに対してBookクラスで定義されたメソッドのみ呼びだすことができ、不正な操作を防いでいます。

実行時の動作理解：
このプログラムを実行すると、メモリ上に2つの独立したBookオブジェクトが作成され、それぞれが異なる本の情報を保持します。price変更後のbook1は新しい価格情報を保持し続け、これによりオブジェクトの「状態を持つ」という特性を実感できます。

## publicとprivate - アクセス修飾子の基本

ここまでの例では、フィールドやメソッドの前に`public`や`private`というキーワードを使ってきました。これらは「アクセス修飾子」と呼ばれ、クラスのメンバー（フィールドやメソッド）へのアクセスを制御する重要な仕組みです。

### アクセス修飾子の基本的な使い分け

3章では、最も基本的な2つのアクセス修飾子について学習します：

1. public修飾子
- どこからでもアクセス可能
- 他のクラスから自由に使用できる
- 主にメソッドで使用（クラスの機能を外部に公開）

2. private修飾子
- 同じクラス内からのみアクセス可能
- 外部から直接アクセスできない
- 主にフィールドで使用（データを保護）

<span class="listing-number">**サンプルコード3-18**</span>

```java
public class Student {
    // privateフィールド - クラス内部でのみアクセス可能
    private String name;
    private int age;
    private double gpa;
    
    // publicコンストラクタ - 外部からオブジェクト作成可能
    public Student(String name, int age, double gpa) {
        this.name = name;
        this.age = age;
        this.gpa = gpa;
    }
    
    // publicメソッド - 外部から呼び出し可能
    public void introduce() {
        System.out.println("私は" + name + "です。" + age + "歳です。");
    }
    
    // privateメソッド - クラス内部でのみ使用
    private boolean isAdult() {
        return age >= 20;
    }
    
    // publicメソッドからprivateメソッドを呼び出す
    public void checkStatus() {
        if (isAdult()) {
            System.out.println("成人です");
        } else {
            System.out.println("未成年です");
        }
    }
}
```

### なぜprivateを使うのか？

フィールドをprivateにする理由を具体例で見てみましょう：

<span class="listing-number">**サンプルコード3-19**</span>

```java
public class Product {
    // publicフィールドの問題点
    public double price;     // 誰でも直接変更できる
    public int stock;        // 在庫数も自由に変更可能
}

// 使用例（問題のある使い方）
public class BadExample {
    public static void main(String[] args) {
        Product product = new Product();
        
        // 問題1: 負の価格を設定できてしまう
        product.price = -100.0;
        
        // 問題2: 在庫数を不正に操作できる
        product.stock = -50;
        
        // 問題3: データの整合性が保てない
        product.stock = 1000000;  // 現実的でない在庫数
    }
}
```

privateを使った改善例：

```java
public class Product {
    // privateフィールド - 直接アクセスできない
    private double price;
    private int stock;
    
    // コンストラクタで初期値を設定
    public Product(double price, int stock) {
        // 初期値の検証も可能
        if (price < 0) {
            this.price = 0;
        } else {
            this.price = price;
        }
        
        if (stock < 0) {
            this.stock = 0;
        } else {
            this.stock = stock;
        }
    }
    
    // publicメソッドを通じて安全にアクセス
    public void updatePrice(double newPrice) {
        if (newPrice >= 0) {
            price = newPrice;
            System.out.println("価格を更新しました: " + price + "円");
        } else {
            System.out.println("エラー: 価格は0円以上である必要があります");
        }
    }
    
    public void addStock(int amount) {
        if (amount > 0) {
            stock += amount;
            System.out.println("在庫を追加しました。現在の在庫: " + stock);
        }
    }
    
    public boolean sell(int amount) {
        if (amount > 0 && stock >= amount) {
            stock -= amount;
            System.out.println("販売しました。残り在庫: " + stock);
            return true;
        }
        System.out.println("在庫が不足しています");
        return false;
    }
}
```


## static修飾子の基本

これまで学習したフィールドやメソッドは、`new`でオブジェクトを作ってから使用していました。しかし、`static`修飾子を付けると、オブジェクトを作らなくても使えるようになります。

### staticメソッドの例

<span class="listing-number">**サンプルコード3-20**</span>

```java
public class MathHelper {
    // staticメソッド - オブジェクトを作らずに使える
    public static int add(int a, int b) {
        return a + b;
    }
    
    public static double calculateCircleArea(double radius) {
        return 3.14159 * radius * radius;
    }
}

// 使用例
public class StaticExample {
    public static void main(String[] args) {
        // newを使わずに直接呼び出し
        int sum = MathHelper.add(5, 3);
        System.out.println("合計: " + sum);
        
        double area = MathHelper.calculateCircleArea(10.0);
        System.out.println("円の面積: " + area);
    }
}
```

staticの特徴：
- クラス名.メソッド名()で呼び出す
- オブジェクトを作る必要がない
- ユーティリティメソッドに便利

### staticフィールド（クラス共有変数）

<span class="listing-number">**サンプルコード3-21**</span>

```java
// StaticMemberExample.java
class Tool {
    // インスタンスメンバー（各Toolインスタンスが個別に持つ）
    String name;

    // クラスメンバー（Toolクラス全体で共有される）
    static int toolCount = 0;

    // コンストラクタ
    public Tool(String name) {
        this.name = name;
        toolCount++; // インスタンスが作られるたびに、共有カウンタを増やす
        System.out.println(this.name + " が作成されました。現在のツール総数: " + toolCount);
    }

    // インスタンスメソッド
    public void showName() {
        System.out.println("このツールの名前は " + this.name + " です。");
    }

    // クラスメソッド
    public static void showToolCount() {
        // System.out.println(this.name); // エラー！ staticメソッド内ではインスタンスメンバー(name)は使えない
        System.out.println("作成されたツールの総数は " + toolCount + " です。");
    }
}

public class StaticMemberExample {
    public static void main(String[] args) {
        System.out.println("--- ツール作成前 ---");
        // インスタンスがなくてもクラスメソッドは呼び出せる
        Tool.showToolCount();

        System.out.println("\n--- ツール作成 ---");
        Tool hammer = new Tool("ハンマー");
        Tool wrench = new Tool("レンチ");

        System.out.println("\n--- 各ツールの情報表示 ---");
        hammer.showName(); // インスタンスメソッドの呼び出し
        wrench.showName();

        System.out.println("\n--- ツール総数の再確認 ---");
        // クラス名経由でのアクセスが推奨
        Tool.showToolCount();
    }
}
```

実行結果:
```
--- ツール作成前 ---
作成されたツールの総数は 0 です。

--- ツール作成 ---
ハンマー が作成されました。現在のツール総数: 1
レンチ が作成されました。現在のツール総数: 2

--- 各ツールの情報表示 ---
このツールの名前は ハンマー です。
このツールの名前は レンチ です。

--- ツール総数の再確認 ---
作成されたツールの総数は 2 です。
```

### staticを使う場面

staticは主に以下の場面で使用します：

1. **ユーティリティメソッド**: 計算や変換などの汎用的な処理
2. **共有カウンタ**: すべてのインスタンスで共有する値
3. **定数の定義**: 変更されない固定値

### staticの注意点

- staticメソッド内では`this`は使えません
- staticメソッドからインスタンスフィールドにアクセスできません
- 使いすぎるとオブジェクト指向の利点が失われます

staticの詳細な使い方や設計パターンについては、第7章以降でさらに詳しく学習します。









## オブジェクトの配列

### オブジェクト配列の基本

第2章で基本的な配列の使い方を学習しました。ここでは、オブジェクト指向プログラミングの文脈で重要な「オブジェクトの配列」について学習します。

プリミティブ型の配列とは異なり、オブジェクトの配列は参照の配列として実装されます。これは、配列の各要素がオブジェクトそのものではなく、オブジェクトへの参照を保持することを意味します。

### オブジェクト配列の宣言と初期化

<span class="listing-number">**サンプルコード3-22**</span>

```java
// オブジェクト配列の宣言
Student[] students;  // Studentクラスの配列

// 配列の初期化（参照の配列を作成）
students = new Student[5];  // 5人分のStudent参照を保持できる配列

// 各要素にオブジェクトを代入
students[0] = new Student("田中太郎", 20, 3.5);
students[1] = new Student("佐藤花子", 21, 3.8);
// students[2]〜students[4]はnullのまま
```

重要なポイント：
- `new Student[5]`はStudentオブジェクト5個を作るのではなく、Studentへの参瑧5個分の配列を作ります
- 初期化直後の各要素は`null`です
- 各要素に対して個別に`new`でオブジェクトを作成して代入する必要があります

### オブジェクト配列の操作

<span class="listing-number">**サンプルコード3-23**</span>

```java
public class StudentArrayExample {
    public static void main(String[] args) {
        // オブジェクト配列の作成と初期化
        Student[] students = new Student[3];
        
        // 各要素にオブジェクトを代入
        students[0] = new Student("田中太郎", 20, 3.5);
        students[1] = new Student("佐藤花子", 21, 3.8);
        students[2] = new Student("鈴木一郎", 19, 3.2);
        
        // オブジェクト配列の反復処理
        System.out.println("学生一覧:");
        for (Student student : students) {
            student.display();  // 各オブジェクトのメソッドを呼び出し
        }
        
        // GPAの平均を計算
        double totalGpa = 0;
        for (Student student : students) {
            totalGpa += student.getGpa();
        }
        double averageGpa = totalGpa / students.length;
        System.out.println("\n平均GPA: " + averageGpa);
    }
}
```

### オブジェクト配列のnullチェック

オブジェクト配列を操作する際の最も重要な注意点は、nullチェックです：

<span class="listing-number">**サンプルコード3-24**</span>

```java
public class NullCheckExample {
    public static void main(String[] args) {
        Student[] students = new Student[5];
        students[0] = new Student("田中", 20, 3.5);
        students[1] = new Student("佐藤", 21, 3.8);
        // students[2]〜[4]はnull
        
        // nullチェックを忘れるとNullPointerExceptionが発生
        for (int i = 0; i < students.length; i++) {
            if (students[i] != null) {  // nullチェックが必須
                students[i].display();
            }
        }
    }
}
```

### オブジェクト配列とプリミティブ配列の違い

<span class="listing-number">**サンプルコード3-25**</span>

```java
// プリミティブ型の配列
int[] numbers = new int[5];     // 各要素は0で初期化される
System.out.println(numbers[0]); // 0が表示される

// オブジェクトの配列
Student[] students = new Student[5]; // 各要素はnullで初期化される
System.out.println(students[0]);     // nullが表示される
```

### まとめ

オブジェクト配列は、複数のオブジェクトを効率的に管理するための重要なデータ構造です。プリミティブ型の配列とは異なり、参照の配列であることを理解し、nullチェックを忘れずに行うことが重要です。

より柔軟なオブジェクト管理が必要な場合は、第10章で学習するコレクションフレームワーク（`ArrayList`など）の使用を検討してください。









## 章末演習

本章で学んだオブジェクト指向の基本概念を実践的な課題で確認しましょう。

### 演習を始める前の確認

第3章では、オブジェクト指向プログラミングの中核概念を学習しました。演習に入る前に、以下の重要概念を理解できているか確認しましょう：

✅ **理解度チェックリスト**
- クラスとオブジェクトの違いを説明できる
- フィールドとメソッドの役割を理解している
- コンストラクタの目的と書き方を理解している
- thisキーワードの使い方を理解している
- メソッドオーバーロードの概念を理解している
- カプセル化（private/public）の重要性を理解している
- staticとインスタンスメンバーの違いを理解している

### 演習課題へのアクセス

本書の演習課題は、以下のGitHubリポジトリで提供されています：

リポジトリ: `https://github.com/Nagatani/techbook-java-primer/tree/main/exercises`

### 第3章の課題構成

```
exercises/chapter03/
├── basic/              # 基礎課題（必須）- オブジェクト指向の基本を確実に
│   ├── README.md       # 詳細な課題説明と段階的な実装ガイド
│   ├── MethodsPractice.java
│   └── StudentScores.java
├── advanced/           # 発展課題（推奨）- より実践的な設計課題
│   ├── Car.java
│   └── FuelExpenseCalculator.java
├── challenge/          # チャレンジ課題（任意）- 応用力を試す
└── solutions/          # 解答例（実装後に参照）
```

### 学習の目標

本章の演習を通じて以下のスキルを習得します：
- クラスの設計と実装
- メソッドの定義と呼び出し
- オブジェクトの状態管理
- カプセル化の実践

### 基礎課題の詳細ガイド

#### MethodsPractice.java - メソッドの基本実装
**目的**: メソッドの定義、呼び出し、オーバーロードを実践

**実装する機能**:
1. **基本的な計算メソッド**
   - add(int a, int b) - 2つの整数の和
   - subtract(int a, int b) - 2つの整数の差
   - multiply(int a, int b) - 2つの整数の積

2. **オーバーロードの実践**
   - add(double a, double b) - 小数の和
   - add(int a, int b, int c) - 3つの整数の和
   - add(String a, String b) - 文字列の連結

3. **実装のポイント**
   - メソッドの戻り値の型に注意
   - 引数の個数や型によるオーバーロードの理解
   - mainメソッドから各メソッドを呼び出して動作確認

**コード構造のヒント**:
<span class="listing-number">**サンプルコード3-26**</span>

```java
public class MethodsPractice {
    // ここに各メソッドを実装
    
    public static void main(String[] args) {
        // メソッドの呼び出しと結果の表示
    }
}
```

#### StudentScores.java - オブジェクトの状態管理
**目的**: クラス設計とオブジェクトの基本操作を実践

**実装する機能**:
1. **フィールドの定義**
   - name (String) - 学生名
   - mathScore (int) - 数学の点数
   - englishScore (int) - 英語の点数
   - scienceScore (int) - 理科の点数

2. **コンストラクタの実装**
   - 引数なしコンストラクタ（デフォルト値設定）
   - 全フィールドを初期化するコンストラクタ

3. **メソッドの実装**
   - getAverage() - 3教科の平均点を計算
   - getTotal() - 3教科の合計点を計算
   - displayInfo() - 学生情報を整形して表示

4. **カプセル化の実践**
   - フィールドはprivateで定義
   - 必要に応じてゲッター・セッターを実装
   - 点数の妥当性チェック（0-100の範囲）

### 発展課題への橋渡し

基礎課題を完了したら、発展課題で以下の概念を学びます：

#### Car.java - 状態変化を伴うオブジェクト
**新しく学ぶ概念**:
- オブジェクトの状態変化（燃料の消費）
- メソッド間の連携（drive → consumeFuel）
- エラーハンドリングの基礎（燃料不足の処理）

**実装のアイデア**:
- 走行距離に応じた燃料消費
- 燃料補給メソッド
- 走行可能距離の計算

#### FuelExpenseCalculator.java - 複数オブジェクトの管理
**新しく学ぶ概念**:
- 複数のオブジェクトを配列で管理
- オブジェクト間の計算と集計
- より複雑なビジネスロジックの実装

### よくある実装ミスと対策

| ミスの種類 | 症状 | 対策 |
|------------|------|------|
| フィールドの初期化忘れ | NullPointerException | コンストラクタで確実に初期化 |
| thisの使い忘れ | 引数とフィールドの混同 | 同名の場合は必ずthisを使用 |
| staticの誤用 | "non-static variable" エラー | インスタンスメソッドではstaticを外す |
| アクセス修飾子の誤り | privateフィールドに直接アクセス | ゲッターメソッドを使用 |

### 段階的な実装アプローチ

1. **スケルトン作成**（5分）
   - クラスの枠組みだけ作成
   - メソッドのシグネチャだけ定義
   - コンパイルが通ることを確認

2. **基本機能実装**（15分）
   - 最も簡単なメソッドから実装
   - 各メソッドごとに動作確認
   - printlnでデバッグ出力

3. **機能追加**（10分）
   - エラーチェック追加
   - 出力の整形
   - コメント追加

4. **テストと改善**（10分）
   - 境界値でのテスト
   - エラーケースの確認
   - コードの整理

### 学習を深める追加実験

基礎課題完了後、以下の実験を試してみましょう：

1. **メモリ効率の実験**
   - 大量のオブジェクトを作成して挙動を観察
   - staticメソッドとインスタンスメソッドの使い分け

2. **設計の改善**
   - より使いやすいAPIを考える
   - メソッドチェーンの実装に挑戦

3. **実用的な拡張**
   - ファイルからデータを読み込む（次章の予習）
   - 簡単な対話型プログラムの作成

### デバッグのヒント

**オブジェクト指向特有のデバッグ方法**:
1. **toString()メソッドの活用**
   - オブジェクトの状態を文字列で確認
   - デバッグ時の状態把握に便利

2. **段階的なコンストラクタ実行**
   - 各フィールドの初期化を確認
   - thisの参照先を意識

3. **メソッド呼び出しの追跡**
   - どのオブジェクトのメソッドが呼ばれているか
   - staticコンテキストとインスタンスコンテキストの区別

次のステップ: 基礎課題が完了したら、第4章「クラスとインスタンス」に進みましょう。第4章では、より高度なクラス設計と、複数クラスの連携について学びます。

## より深い理解のために

本章で学んだプログラミングパラダイムについて、さらに深く理解したい方は、GitHubリポジトリの付録資料を参照してください：

付録リソース: `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b03-programming-paradigms/`

この付録では以下の高度なトピックを扱います：

- オブジェクト指向の歴史的発展: SimulaからJavaへの歴史的経緯
- プログラミングパラダイムの比較: 手続き型、オブジェクト指向、関数型の差異
- オブジェクト指向設計の成功事例: 実世界での適用例とパターン
- 関数型プログラミングの数学的基礎: ラムダ計算、圏論、モナド等

## 次章への橋渡し

本章では、オブジェクト指向プログラミングの基本的な考え方と、クラス・フィールド・メソッドの基本的な書き方を学びました。簡単なgetter/setterメソッドやpublic/privateの使い分けについても触れました。

次章「第4章 クラスとインスタンス」では、より高度なカプセル化技術について学びます。具体的には：

- アクセス修飾子（public、private、protected、パッケージプライベート）の詳細
- 完全なカプセル化の実現方法（BankAccountの段階的改善例）
- データ検証とバリデーション
- 防御的プログラミング
- パッケージシステムとクラスの組織化

第3章で学んだ基礎をもとに、実践的で堅牢なクラス設計の技術を身につけていきましょう。
