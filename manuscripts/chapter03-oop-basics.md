# <b>3章</b> <span>オブジェクト指向の考え方</span> <small>よりよいプログラミング技法を求めて</small>

## 本章の学習目標

### この章で学ぶこと

1. オブジェクト指向の基本概念
    - カプセル化
    -    + データと処理の一体化
    - 継承
    -    + 既存クラスの機能の再利用
    - ポリモーフィズム
    -    + 同じインターフェイスで異なる実装
2. クラスとオブジェクト
    - クラス（設計図）とオブジェクト（実体）の関係
    - コンストラクタによるオブジェクトの初期化
    - インスタンス変数とメソッドの定義
3. カプセル化の実践
    - privateとpublicによるアクセス制御
    - getterとsetterメソッドの活用
    - データの整合性保護
4. 静的メンバーとオブジェクトの配列
    - staticフィールドとstaticメソッド
    - オブジェクトの配列による複数データの管理

### この章を始める前に

第2章でJavaの基本文法を理解していれば準備完了です。
第2章まででは、C言語などの手続き型プログラミングでの開発とそれほど違いがありませんでした。
この章からは、新しい視点としてのオブジェクト指向を学びましょう。



## 章の構成

本章では、オブジェクト指向プログラミングの基本的な考え方と実装方法を学習します。
プログラミングパラダイムの歴史的な発展を理解したうえで、以下の内容を順に学んでいきます。

1. オブジェクト指向の背景
    - ソフトウェア開発の課題と解決アプローチ
2. オブジェクト指向の基本概念
    - 手続き型との違いと3つの基本原則
3. クラスとオブジェクト
    - 設計図と実体の関係性
4. カプセル化の実践
    - データ保護とアクセス制御
5. 静的メンバー
    - クラスレベルの共有データと処理
6. 配列の活用
    - データの集合を一元的に管理

オブジェクト指向は、大規模なソフトウェア開発において威力を発揮する1つの有効なアプローチです。
その考え方の基礎と実装技術を身につけていきます。

## なぜオブジェクト指向が必要なのか
### C言語での開発で直面する実務的な課題

C言語はC言語で優れた言語ですが、実際の開発現場では以下のような課題に直面することがあります。

#### 実例：学生管理システムの開発

C言語で学生管理システムを開発する場合を考えてみましょう。

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

#### 1. データの保護ができない

構造体のメンバーは誰でも直接アクセスできるため、不正な値が設定されるリスクもあります。
たとえば、GPAに負の値を設定したり、学年に100を設定したりすることを防ぐには、すべてのアクセス箇所でチェックが必要になります。

#### 2. 関連する関数の管理が困難
学生に関する関数（表示、更新、削除など）が増えると、どの関数がどの構造体に対応するのかわかりにくくなります。命名規則で対応しようとしても、大規模プロジェクトでは限界があります。

#### 3. 仕様変更時の影響範囲が大きい
構造体のメンバーを変更すると、その構造体を使用しているすべての関数を修正してください。たとえば、`name`を`first_name`と`last_name`に分割する場合、影響は広範囲に渡ります。


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


このような問題は、プロジェクトが大きくなるほど深刻になります。
データと処理が分離していることで、開発対象のコードの大規模化に伴い以下の問題が発生します。

- 一貫性の欠如
    + 同じ処理でも開発者によって異なる実装
- バグの温床
    + データ検証の漏れが各所で発生
- 保守の困難
    + どの関数がどのデータを操作するか追跡が困難

### オブジェクト指向による解決

オブジェクト指向プログラミングは、これらの実務的な問題を解決するアプローチの1つです。

1. データと処理の一体化
    + 構造体（データ）と関数（処理）を1つのクラスにまとめる
2. アクセス制御
    + `private`や`public`などのアクセス修飾子でデータを保護
3. 責任の明確化
    + 各クラスが自身のデータに対する責任を持つことで関心を分離させる

この章では、C言語での開発経験を活かしながら、オブジェクト指向がどのようにこれらの課題を解決するかを実践的に学んでいきます。

> 補足: プログラミングパラダイムの歴史的発展について詳しく知りたいほうは、GitHubリポジトリの付録資料（`https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b02-programming-paradigms/`）を参照してください。そこでは、ソフトウェアクライシスからオブジェクト指向の誕生までの歴史的経緯を詳しく解説しています。


## オブジェクト指向とは

オブジェクト指向という言葉は、実は多様な意味を持っています。
ソフトウェア開発のさまざまな工程で、オブジェクト指向の考え方が適用されているからです。

まず、システム設計の段階では、オブジェクト指向設計（OOD: Object-Oriented Design）が用いられます。
これは、システムをオブジェクトの集合として設計し、それらの関係性を定義する手法です。

同様に、要求分析の段階ではオブジェクト指向分析（OOA: Object-Oriented Analysis）が、問題領域をオブジェクトの観点から分析します。
これらは総称してオブジェクト指向分析設計（OOAD: Object-Oriented Analysis and Design）と呼ばれます。

しかし、本書が焦点を当てるのはオブジェクト指向プログラミング（OOP: Object-Oriented Programming）です。
これは、設計されたオブジェクトを実際にプログラミング言語で実装する技術です。

Javaは、OOPを実践するために設計された言語であり、クラス、継承、ポリモーフィズムなどのOOPの主要概念を言語レベルでサポートしています。

さらに幅広い視点では、開発方法論やプロジェクト管理手法としてのオブジェクト指向、そしてプログラミング言語仕様としてのオブジェクト指向も存在しますが、これらは本書の範囲を超える内容です。

## オブジェクト指向を学ぶ意義

オブジェクト指向は、プログラミングの歴史のなかで生まれた多くのアプローチの1つです。
本書では、オブジェクト指向を特別視するのではなく、問題解決のための便利なツールの1つとして学びます。

C言語でのプログラミングでも、構造的なプログラミングや、関数ライブラリを使った柔軟な開発は可能です。
極論を言ってしまえば、新たな道具としてオブジェクト指向を学ばなくても、たいていのプログラムは作れます。

### よりよく継続的な開発を行うために

C言語でも十分なプログラミングは可能ですが、現実のソフトウェア開発では、プログラムの作成は全体の一部にすぎません。
作成されたプログラムは、その後長期にわたる「保守・運用」のフェーズに入り、この期間が実は開発期間よりもはるかに長く、コストも大きくなることが一般的です。

このような現実を踏まえると、プログラミング言語には単に動くプログラムを作ること以上の要求が突きつけられます。
まず、開発の効率化が重要です。同じ機能を何度も実装するのではなく、既存のコードを再利用し、新たな機能をすばやく追加できることが求められます。

次に、保守性の向上が不可欠です。
要求の変化やバグ修正の際に、変更箇所が明確で、変更の影響範囲が限定されていることが理想です。
そして、品質の向上も欠かせません。バグが少なく、意図が明確で、拡張性の高いコードを書くことが、長期的な成功につながります。

これらの要求を満たすための強力なアプローチが、オブジェクト指向プログラミングなのです。

### 大規模で長期的に維持可能なプログラムを作る

「もっと効率よくしたい」という要望のために、オブジェクト指向という考え方が登場します。
より効率よくプログラミングを行うことを目的として、保守運用を見据えた概念であることをよく理解してオブジェクト指向を学んでください。

実際、現代のプログラミングでは、オブジェクト指向だけでなく、さまざまなパラダイムを組み合わせて使うことが一般的です。
関数型プログラミング、並行プログラミング、リアクティブプログラミングなども併用されます。
オブジェクト指向は、これらのなかの1つのアプローチに過ぎません。



## オブジェクト指向の基本概念を実際のコードで理解する

理論的な説明の前に、実際のJavaコードを見てオブジェクト指向の基本概念を理解しましょう。

### 手続き型とオブジェクト指向の違い

#### 手続き型の例（C言語風）

手続き型プログラミングでは、データと処理が分離された構造となります。以下の例では、この分離がどのような問題を引き起こすかを示しています。

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

#### 構造的問題の分析

- ①　データの散在
    + 学生に関する情報（名前、年齢、GPA）が独立した変数として存在し、これらが1つの概念（学生）を表すという関係性がコード上で明確でない
- ②　パラメータ渡しの煩雑さ
    + 関連するデータを処理するたびに、すべてのパラメータを個別に渡す必要があり、データの追加や変更時に多くの関数の引数リストを修正する必要がある
- ③　責任の分散
    + 学生データの検証、変更、表示など、学生に関する処理が複数の場所に散らばり、どこで何をしているかが把握しにくい

#### オブジェクト指向の例

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

#### オブジェクト指向の利点の実現

- ①　データのカプセル化
    + 学生に関するすべての属性がprivateフィールドとして一箇所に集約され、外部からの直接アクセスを制限する
- ②　初期化の保証
    + コンストラクタにより、オブジェクト生成時に必要なデータがすべて設定されることを保証する
- ③　振る舞いの局所化
    + 学生データの表示処理がStudentクラス内部に定義され、データとその操作の責任が明確になる
- ④　概念の一体性
    + `Student`オブジェクトとして学生という概念を直接的にプログラムで表現される
- ⑤　インターフェイスの簡潔性
    + 外部からは単純にメソッドを呼び出すだけで、内部の複雑さを隠蔽

### オブジェクト指向の3つの基本原則

#### カプセル化

##### データ保護と制御されたアクセスの実現

カプセル化は、オブジェクトの内部データを外部から直接アクセスできないように保護し、入力値の検証やビジネスルールの適用を含むメソッドを通じてのみ操作を許可する仕組みです。

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

##### カプセル化の効果

- ①　データの隠蔽
    + `private`修飾子により残高データを直接操作できないようにし、不正な値の設定を防止
- ②　制御された変更
    + 入金処理では金額の妥当性を検証してから残高を更新し、負の値の入金を防ぐ
- ③　安全な読み取り
    + 残高の確認は可能だが、直接的な変更はできないため、データの整合性を保証

#### 継承

##### コードの再利用と階層構造の構築

継承は、既存のクラス（親クラス）の機能を新しいクラス（子クラス）が引き継ぎ、さらに独自の機能を追加する仕組みです。

<span class="listing-number">**サンプルコード3-4**</span>

```java
public class Product {
    protected String productId;  // ① protectedにより同一パッケージまたはサブクラスからアクセス可能
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

##### 継承による機能拡張

- ①　共通属性の継承
    + 商品ID、名前、価格など、すべての商品に共通する属性を親クラスで定義する
- ②　共通機能の継承
    + 基本的な商品情報の表示機能を親クラスで実装し、子クラスでも利用可能となる
- ③　is-a関係の表現
    + 「本は商品である」という概念的関係をextends文で明確に表現する
- ④　専用属性の追加
    + 本特有の情報（著者、ISBN）を子クラスで独自に定義
- ⑤　機能の組み合わせ
    + 親クラスの機能を活用しつつ、子クラス独自の機能を追加する

#### ポリモーフィズム

##### 同一インターフェイスによる多様な実装の統一的扱い

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

##### ポリモーフィズムの仕組み

- ①　統一インターフェイス
    + すべての決済方法が実装すべき共通のメソッド仕様を定義
- ②　多様な実装
    + 同じインターフェイスに対して、具体的な決済手段ごとに異なる実装を提供する
- ③　クレジットカード固有処理
    + カード決済に特化した具体的な処理を実装
- ④　銀行振込の固有処理
    + 銀行振込に特化した具体的な処理を実装

この設計により、`PaymentMethod`型の変数で異なる決済方法を統一的に扱えるため、新しい決済方法（新しいカード会社の決済、決済サービス、仮想通貨決済など）を追加しても既存のコードに影響を与えません。




## クラスの作成

オブジェクト指向プログラミングでは、クラスという設計図を作成し、その設計図からオブジェクト（インスタンス）を生成します。

### クラスの基本構造

クラスは主に以下の要素で構成されます。
1. フィールド（属性）- オブジェクトが持つデータ
2. メソッド（操作）- オブジェクトができる処理
3. コンストラクタ - オブジェクトを初期化する特別なメソッド

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

#### フィールドの重要なポイント
- フィールドは通常、クラスの最初に宣言する
- 各フィールドには型（String、int、doubleなど）が必要である
- 初期値を設定しない場合、デフォルト値が自動的に設定される
- フィールド名は意味がわかりやすい名前にする

#### フィールドのデフォルト値

フィールドにプリミティブ型を指定する場合は、それぞれの型に合わせた初期値が代入されています。
それに対して、`String`をはじめとしたクラス型のフィールドの場合、初期値として`null`が代入されます。

これらは、メソッド内部で通常の変数として定義した際の「未初期化」の状態とは異なります。

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

<span class="listing-number">**サンプルコード3-27**</span>

```java
アクセス修飾子 データ型 フィールド名;
アクセス修飾子 データ型 フィールド名 = 初期値;
```

#### 実際の使用例

<span class="listing-number">**サンプルコード3-8**</span>

```java
public class Product {
    // フィールドの宣言
    private String productId;  // 商品ID
    private String name;       // 商品名  
    private double price;      // 価格
    private int stock;         // 在庫数
    
    // コンストラクタでフィールドを初期化
    public Product(String productId, String name, double price) {
        this.productId = productId;  // thisは現在のオブジェクトを指す
        this.name = name;
        this.price = price;
        this.stock = 0;              // 初期在庫は0
    }
    
    // フィールドを使った処理
    public void addStock(int quantity) {
        if (quantity > 0) {
            stock += quantity;  // 在庫数を更新
            System.out.println(quantity + "個入荷しました");
        }
    }
    
    // フィールドの値を表示
    public void showInfo() {
        System.out.println("商品ID: " + productId);
        System.out.println("商品名: " + name);
        System.out.println("価格: ¥" + price);
        System.out.println("在庫数: " + stock + "個");
    }
}
```

#### フィールドのデフォルト値

フィールドに初期値を指定しない場合、以下のデフォルト値が自動的に設定されます。

| データ型 | デフォルト値 |
|---------|------------|
| byte, short, int, long | 0 |
| float, double | 0.0 |
| char | '\u0000' |
| boolean | false |
| 参照型（クラス、配列など） | null |

### メソッド（操作）の基礎

メソッドは、オブジェクトが「できること」を定義します。
C言語の関数と似ていますが、関数とデータ（フィールド）がそれぞれオブジェクトに属している点が異なります。

これにより、データとそれに対してできることをまとめて管理することができます。

#### メソッドの宣言構文

<span class="listing-number">**サンプルコード3-28**</span>

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

##### 1. 戻り値がないメソッド（voidメソッド）

<span class="listing-number">**サンプルコード3-29**</span>

```java
public void printMessage() {
    System.out.println("こんにちは！");
}
```

##### 2. 戻り値があるメソッド

<span class="listing-number">**サンプルコード3-30**</span>

```java
public int add(int a, int b) {
    return a + b;  // int型の値を返す
}
```

##### 3. 引数がないメソッド

<span class="listing-number">**サンプルコード3-31**</span>

```java
public void showTime() {
    System.out.println("現在時刻: " + new Date());
}
```

##### 4. 複数の引数を持つメソッド

<span class="listing-number">**サンプルコード3-32**</span>

```java
public double calculateBMI(double weight, double height) {
    return weight / (height * height);
}
```

### getter/setterメソッドの基本

オブジェクト指向プログラミングでは、フィールドを`private`にして直接アクセスを禁止し、`public`メソッドを通じてアクセスする方法が推奨されます。
これをアクセサメソッド（getter/setter）パターンと呼びます。

値の取得(getter)と設定(setter)をそれぞれ分割して作成することは、「取得はできるけど外部からは設定できないデータ」のような条件のデータを作成できることになります。

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

#### getter/setterを使う利点
- フィールドへのアクセスを制御できる
- 値の設定時に検証を行える
- 将来的に実装を変更しても、外部インターフェイスは変わらない
- デバッグ時にアクセスポイントを特定しやすい

> 注意: すべてのフィールドに機械的にgetter/setterを作成するのは避けましょう。本当に外部からアクセスが必要なフィールドにのみ提供し、可能な限りオブジェクトの内部状態は隠蔽することがよい設計です。

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

#### メソッドオーバーロードの詳細

メソッドオーバーロードは、同じ名前で異なるパラメータを持つメソッドを複数定義できる強力な機能です。
これにより、似た機能を持つメソッドに統一的な名前を付けることができ、コードの可読性と使いやすさが大幅に向上します。

##### C言語との決定的な違い

C言語では関数名は一意でなければならず、パラメータが違っても別の名前を付ける必要がありました。
以下の例でこの制約を確認できます。
```c
// C言語では別々の名前が必要
int add_int(int a, int b);
double add_double(double a, double b);
int add_three_ints(int a, int b, int c);
```

一方、Javaではすべて同じ名前で定義できます：
<span class="listing-number">**サンプルコード3-33**</span>

```java
public int add(int a, int b) { return a + b; }
public double add(double a, double b) { return a + b; }
public int add(int a, int b, int c) { return a + b + c; }
```

##### オーバーロードのメリット

1. 直感的なAPI設計
   - 利用者は1つのメソッド名を覚えるだけで、さまざまな状況で使える
   - `Math.max(int, int)`、`Math.max(double, double)`など、統一的な名前で提供
2. 型安全性の向上
   - コンパイル時に引数の型に基づいてもっとも適合するメソッドが選択される
   - 型エラーが実行前に検出される
3. 後方互換性の維持
   - 既存のメソッドを変更せずに、新しいパラメータパターンを追加できる
   - 機能拡張が容易

##### オーバーロードの解決規則

Javaコンパイラはメソッド選択の際に特定のルールに従います。
以下の優先順位で適切なメソッドを選択します。

1. 完全一致（型変換なし）
2. プリミティブ型の拡大変換（`int` → `long`、`float` → `double`など）
3. オートボクシング（`int` → `Integer`など）
4. 可変長引数

<span class="listing-number">**サンプルコード3-34**</span>

```java
public class OverloadResolution {
    public static void test(int x) {
        System.out.println("int: " + x);
    }
    
    public static void test(long x) {
        System.out.println("long: " + x);
    }
    
    public static void test(Integer x) {
        System.out.println("Integer: " + x);
    }
    
    public static void test(int... x) {
        System.out.println("可変長引数: " + Arrays.toString(x));
    }
    
    public static void main(String[] args) {
        byte b = 10;
        test(b);      // int版が呼ばれる（自動拡大変換）
        test(10);     // int版が呼ばれる（完全一致）
        test(10L);    // long版が呼ばれる（完全一致）
        test(Integer.valueOf(10)); // Integer版が呼ばれる
        test(1, 2, 3); // 可変長引数版が呼ばれる
    }
}
```

> **重要**: 戻り値の型だけが異なるメソッドはオーバーロードできません。次のコードはコンパイルエラーになります。
> ```java
> public int calculate() { return 1; }
> public double calculate() { return 1.0; }  // エラー：戻り値の型だけでは区別できない
> ```

##### コンストラクタのオーバーロード

コンストラクタもメソッドと同様にオーバーロードできます。これにより、オブジェクトの作成時にさまざまな初期化方法を提供できます：

<span class="listing-number">**サンプルコード3-35**</span>

```java
public class Person {
    private String name;
    private int age;
    
    // デフォルトコンストラクタ
    public Person() {
        this("名無し", 0);
    }
    
    // 名前のみを指定するコンストラクタ
    public Person(String name) {
        this(name, 0);
    }
    
    // すべての属性を指定するコンストラクタ
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```

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

このBookクラスでは、boolean型のisAvailableフィールドで貸出状況を管理しています。
borrowメソッドでは二重貸出を防ぎ、returnBookメソッドでは確実に返却処理を行うことで、データの整合性を保っています。

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

このパートでは、オブジェクト指向の基本概念を実際のコードを通じて学習しました。重要なポイントは以下のとおりです。

1. データと処理の一体化
    + クラスは関連するデータと処理をまとめる
2. カプセル化
    + 内部実装を隠蔽し、必要なインターフェイスのみを公開
3. オブジェクトの生成と操作
    + `new`キーワードでインスタンスを生成し、メソッドを通じて操作
4. 現実世界のモデリング
    + 実世界の概念をクラスとして表現










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

クラスは、オブジェクトの設計図です。建築にたとえれば、家の設計図にあたります。クラスは、どのようなデータ（フィールド）を持ち、どのような操作（メソッド）ができるかを定義します。

インスタンスは、クラスをもとに実際に作成された具体的な実体です。設計図から実際に建てられた家に相当します。1つのクラスから複数のインスタンスを作成でき、それぞれが独立したデータを保持します。

オブジェクトという用語は、文脈によって意味が変わることに注意が必要です。多くの場合、オブジェクトはインスタンスを指しますが、より幅広い概念を表す場合や、クラス自体を指す場合もあります。「オブジェクト指向」という場合のオブジェクトは、より抽象的な概念を表しています。

※厳密には、インスタンス化をしなくても内部的に使用できる状態や振る舞いというのも存在します。それらは「静的（static）な○○」と呼ばれ、インスタンス化を明示的に行わなくても、プログラム実行時にJVMによってクラスがロードされ、メモリへ展開されて使用できるようになっています。対義語として「動的（dynamic）な○○」という言い方もあり、そちらはインスタンス化しないと使用できません。

> 補足: JVMのクラスローディング機構により、staticメンバーはクラスがはじめて参照されたときに初期化されます。また、JITコンパイラによって実行時に最適化され、頻繁に使用されるstaticメソッドは高速に実行されます。

### クラスはどのように書くか

何らかのデータと、それに対する処理をまとめて書けるとよいです。

「クラスは、C言語における構造体（struct）に、関数をつけられるようにしたもの」のようにイメージすると理解しやすいです。

### 役割ごとに分割する利点

役割ごとに分割することで、大規模なプログラムを作る際に管理しやすくなることがあります。
ただし、これは1つのアプローチに過ぎません。
WebアプリケーションではMVCパターン（Model-View-Controller）、マイクロサービスではドメイン分割など、システムの特性に応じた分割方法が用いられます。
管理しやすいとは、複雑でなく、バグの発見・修正が容易なことを指します。

### 手続き型 vs オブジェクト指向

ここでは、手続き型プログラミングとオブジェクト指向プログラミングの根本的な違いを、学生情報を管理するプログラムの例を通じて理解します。この違いは、データの管理方法と責任の所在に関する重要な設計思想の違いを表しています。

#### 手続き型プログラミングの特徴と課題

手続き型プログラミングでは、データ構造と処理が分離されており、グローバルな関数がデータを操作します。この方式は単純で理解しやすい反面、大規模なシステムでは以下のような問題が生じます。

##### 手続き型プログラミング（C言語）

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

#### 手続き型プログラミングの根本的課題

手続き型プログラミングでは、データと処理の分離によって複数の深刻な問題が生じます。まず、データの一貫性管理がきわめて困難になります。Studentデータを変更する関数が複数存在する場合、どの関数がどのタイミングでデータを変更するかを追跡することが困難で、データの整合性を保つためには開発者が常に全体の状況を把握してください。これは大規模なシステムになるほど現実的でなくなります。

次に、変更の影響範囲が広範囲に拡大してしまう問題があります。たとえば、Student構造体に新しいフィールド（たとえば所属学部）を追加する場合、そのデータを使用するすべての関数を見つけ出して修正してください。これは、システムの規模が大きくなるほど見落としやバグの原因となり、保守性を著しく低下させます。

さらに、責任の所在があいまいになることも重要な課題です。データの検証、初期化、出力、業務ロジックなど、学生に関する処理がシステム全体に散らばってしまうため、どの部分がどの機能を担当しているのかが不明確になります。これにより、バグの原因特定や機能追加が困難になり、開発効率が低下します。

#### オブジェクト指向プログラミングによる解決

オブジェクト指向では、関連するデータとそれを操作する処理を1つのクラスにまとめることで、上記の問題を解決します。

##### オブジェクト指向プログラミング（Java）

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

#### オブジェクト指向プログラミングによる問題解決

オブジェクト指向プログラミングは、手続き型プログラミングの課題を体系的に解決します。もっとも重要な改善点は責任の明確化です。学生に関するすべての操作がStudentクラス内に集約されることで、データの管理責任が明確になり、どこに何の機能があるかが一目瞭然になります。これにより、バグの原因特定や機能追加が劇的に簡単になります。

カプセル化による保護機能も重要な利点です。privateキーワードを使用することで、外部からの不正なデータアクセスを防ぎ、データの整合性を確実に保護できます。これは、手続き型プログラミングでは実現困難だった、データの安全性を保証する仕組みです。

変更の局所化も大きなメリットです。学生に関する処理の変更は、Studentクラス内のみで完結し、システム全体への影響を最小限に抑えることができます。たとえば、成績計算のロジックを変更する場合、Studentクラス内の該当メソッドのみを修正すれば済み、他のクラスに影響を与えません。

さらに、現実世界のモデリングが可能になることで、プログラムの理解しやすさが向上します。「学生」という概念を直接的にプログラムの構造に反映させることで、直感的で理解しやすいコードになり、チーム開発での意思疎通も円滑になります。

### main関数に処理を突っ込むのではなく

自分の作っているプログラムをよく分析して、どんな登場人物（データ）がいるか、それぞれどんな役割があるかで考え、クラスに分割しましょう。

ここでは、小さい子ども（あなたはその子を世話する親とする）の着替えを例に考えてみましょう。
小さい子どもに対して着替えの指示を出す場合、細かくすべてを指示する必要があるでしょうか。
たとえば、対象の子どもが上着を着ているか確認し、近づいてボタンを外し、右腕から袖を通して脱がせるなど、毎回すべての手順を指示するのは非常に手間がかかります。

毎回、状態を確認しつつすべての行動を指示出しするのは面倒でありませんか？
子どもには、「あなたは、着替えてください（服を）」というような命令で指示を出せて、結果だけ受け取ることができたらよいですね。


### 全部自分のプログラムでやろうとしない

自分が作ったクラスから、役割を持つほかのオブジェクトに対して指示を出すことで、関心の分離が可能となります。
大事なポイントとして、「処理をほかのオブジェクトにお任せする」ということが挙げられます。

### 処理をお任せすることと、お任せされる側の配慮

オブジェクトに処理をお任せする以上、そのオブジェクトの状態や振る舞いに外から干渉したくない（されたくもない）。

- オブジェクトが持つ状態や、状態に対する変更処理を外部から直接触られると、予期せぬ不具合や、副作用が発生する
    + 意図しない操作をされたくない部分を、外部から操作できないようにする

これを**カプセル化（隠ぺい）**といいます。

カプセル化を施すことで、内部実装の変更による影響を局所化し、バグの混入を防ぎ、APIの安定性を保つことができます。

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

このような設計では、「従業員クラス」「マネージャークラス」がそれぞれ「人事管理可能」インターフェイスを実装する場合もあります。
これにより、どちらも「人事評価機能」を持つことができます。

ただし、実際のシステムでは権限管理やロールベースのアクセス制御など、より洗練されたアプローチが必要です。

### オブジェクト指向は効率よく開発を行うための考え方

大事な観点として、歴史をたどり、システム開発の効率を求めた結果、オブジェクト指向という体系ができたに過ぎません。

これは通過点であり、オブジェクト指向の考え方を学ぶことですべてのことが説明できる気になったりします<span class="footnote">これはプログラマの三大美徳の1つである「傲慢」ですね。</span>が、これですべてを賄えることはありません。
本書では、オブジェクト指向という道具を学びますが、これはただのプログラミングテクニックの1つとして、システムの要求や規模に応じて使い分けられるよう、判断基準と実装方法を身に付けましょう。


> **コラム：システム開発に「銀の弾丸などない」**{.column-section}
> 
> ### 「人月の神話」
> 
> フレッド・ブルックスが1975年に著した「人月の神話」で述べられているように、ソフトウェア開発の複雑性を一挙に解決する「銀の弾丸」は存在しません。
> ここで言う「銀の弾丸」とは、システム開発におけるあらゆる問題を一挙に解決できるような、魔法のような特効薬や万能な解決策を指す比喩表現です。
> 過去の文献ではオブジェクト指向プログラミングも万能の解決策かのように言われることもありますが、例外ではなく、万能の解決策ではありません。
> 
> オブジェクト指向を不適切に適用した場合、返って問題を悪化させることがあります。
> たとえば、100行未満のスクリプトや単機能のコマンドラインツールにオブジェクト指向を無理に取り入れた場合です。
> シンプルな処理が複雑なクラス構造に埋もれてしまい、コードの記述量が数倍に膨れ上がることがあります。
> また、バッチ処理やパイプライン処理のように、単機能の処理が連続して呼び出されるシステムでは問題が生じます。
> オブジェクト指向のオーバーヘッドが大きくなり、手続き型のアプローチのほうがメモリ使用量を抑えられる場合があります。
> さらに、過度な抽象化や継承の乱用は、コードの理解を困難にし、管理しきれない副作用を発生させる可能性があります。
> 
> 重要なのは、オブジェクト指向が有効な場面を見極め、適度に使用することです。
> 一般的に、以下の場合にオブジェクト指向が有効です。
> 
> 1. 複数の開発者が長期間保守するシステム
> 2. ドメインモデルが複雑なビジネスアプリケーション
> 3. 再利用可能なコンポーネントを作る場合
> 
> #### なぜ「銀の弾丸」は存在しないのか？
>
> 少し内容は逸れますが、システム開発の難しさは、本質的に複雑な問題を扱っていることに起因します。
> 「人月の神話」の著者ブルックスは、この困難さを以下の2つに分類しました。
>
> 1. 本質的な困難さ (Essence)
>     - ソフトウェアが解決しようとする問題そのものの複雑さ、仕様の曖昧さ、満たすべき要求の多さなど、避けることのできない根源的な難しさ
> 2. 偶有的な困難さ (Accident)
>     - プログラミング言語の扱いにくさ、開発ツールの未熟さ、非効率な開発プロセスなど、技術や手法によって改善・解決できる副次的な難しさ
> 
> 過去数十年にわたり、プログラミング言語の進化、開発ツールの改善、新しい開発手法（アジャイルなど）の登場により、「偶有的な困難さ」は大幅に改善されてきました。しかし、「本質的な困難さ」は依然として残っています。
> どのような画期的な技術や手法が登場したとしても、開発プロジェクトが直面する以下のような本質的な問題を完全に消し去ることはできません。
> - 要件の複雑さと変化
>     + 顧客の要求は複雑であり、開発の途中で変化することも少なくない
> - コミュニケーションの問題
>     + 関係者間の認識の齟齬や情報伝達の漏れ
> - 設計の難しさ
>     + 将来の変更を見越した、柔軟で堅牢なシステムを設計することの困難さ。
> - 人間の思考の限界
>     + 複雑なシステム全体を一度に正確に把握することの難しさ。
> 
> したがって、特定の1つの技術や方法論を導入すれば、すべての開発プロジェクトが必ず成功するという「銀の弾丸」は存在しないのです。
> 成功のためには、地道なコミュニケーション、慎重な設計、継続的な改善といった日々の努力が不可欠となります。

## クラスとオブジェクト

### クラスの定義

クラスの定義は、オブジェクト指向プログラミングの核心部分です。ここでは、「本」という現実世界の概念をプログラムで表現する方法を学びます。クラス設計では、そのオブジェクトが「何を知っているか（属性）」と「何ができるか（操作）」を明確に定義することが重要です。

以下のBookクラスは、本という概念の本質的な属性と操作を表現した例です。

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

#### このクラス定義から学ぶ重要な概念

1. カプセル化の実践
    + すべてのフィールドをprivateで宣言することで、外部からの直接アクセスを禁止し、データの整合性を保護している
2. コンストラクタによる初期化
    + オブジェクト作成時に必要な情報をすべて受け取ることで、不完全なオブジェクトの生成を防いでいる
3. 責任の明確化
    + 本に関する表示処理（displayInfo）を本クラス自身が担当することで、「本は自分の情報を表示できる」という自然な責任分担を実現している
4. 制御されたアクセス
    + setPrice()メソッドで価格の妥当性をチェックすることで、ビジネスルール（価格は0以上）をクラス内で守っている
5. 情報隠蔽の原則
    + 読み取り専用の情報（title, author）はgetterのみを提供し、変更可能な情報（price）には検証付きのsetterを提供するという、アクセス制御の使い分けを示している

### オブジェクトの作成と使用

クラスを定義しただけでは、まだ実際にデータを保存したり処理を実行したりすることはできません。クラスは「設計図」であり、実際に動作するプログラムを作るには、その設計図からオブジェクト（インスタンス）を作成します。

以下のプログラムは、Bookクラスから実際のオブジェクトを作成し、それらを操作する方法を示しています。

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

#### このプログラムから学ぶオブジェクト指向の重要な概念

1. オブジェクトの独立性
    + book1とbook2は同じBookクラスから作られているが、それぞれ独立したデータを持っている。一方のオブジェクトの状態を変更しても、他方には影響しません
2. new演算子の役割
    + `new Book(...)`は、メモリ上にBookオブジェクト用の領域を確保し、コンストラクタを呼び出してオブジェクトを初期化する。これにより、クラスという「型」から具体的な「実体」が作られる
3. メッセージパッシング
    + `book1.displayInfo()`のような記法は、book1オブジェクトに「自分の情報を表示して」というメッセージを送っていると解釈できる。オブジェクトは自分の責任範囲で処理を実行する
4. 状態の変更と永続性
    + `book1.setPrice(2500.0)`により、book1オブジェクトの内部状態が変更される。この変更は、そのオブジェクトが存在する限り維持される
5. 型安全性
    + BookTestクラスでは、Bookオブジェクトに対してBookクラスで定義されたメソッドのみ呼びだすことができ、不正な操作を防いでいる

#### 実行時の動作理解
このプログラムを実行すると、メモリ上に2つの独立したBookオブジェクトが作成され、それぞれが異なる本の情報を保持します。
price変更後のbook1は新しい価格情報を保持し続け、これによりオブジェクトの「状態を持つ」という特性を実感できます。

## publicとprivate - アクセス修飾子の基本

ここまでの例では、フィールドやメソッドの前に`public`や`private`というキーワードを使ってきました。これらは「アクセス修飾子」と呼ばれ、クラスのメンバー（フィールドやメソッド）へのアクセスを制御する重要な仕組みです。

### アクセス修飾子の基本的な使い分け

3章では、もっとも基本的な2つのアクセス修飾子について学習します。

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

フィールドをprivateにする理由を具体例で見てみましょう。

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

#### privateを使った改善例

<span class="listing-number">**サンプルコード3-36**</span>

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
            System.out.println("エラー: 価格は0円以上である必要です");
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


## static修飾子の詳細な理解

第2章では、mainメソッドから直接呼び出せるstaticメソッドの基本を学習しました。ここでは、オブジェクト指向プログラミングの文脈におけるstatic修飾子の意味と使い方を、より体系的に理解します。

### staticとは何か

static修飾子は、フィールドやメソッドが「クラスに属する」ことを示します。通常のフィールドやメソッド（インスタンスメンバー）が各オブジェクトごとに存在するのに対し、staticメンバーはクラス全体で1つだけ存在します。

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

#### staticの特徴

- `クラス名.メソッド名()`で呼び出す
- オブジェクトを作る必要がない（インスタンス化して変数に入れる必要がない）
- ユーティリティメソッドに便利

> **ユーティリティメソッドとは**  
> ユーティリティメソッドは、特定のオブジェクトの状態に依存せず、引数のみに基づいて処理を行うメソッドです。数学計算、文字列処理、データ変換など、汎用的な機能を提供します。Javaの標準ライブラリでは、`Math.max()`、`Math.sqrt()`、`Integer.parseInt()`などがユーティリティメソッドの典型例です。これらのメソッドは、オブジェクトを作成せずに直接呼び出せるため、staticメソッドとして実装されています。

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

#### 実行結果

```
--- ツール作成前 ---
作成されたツールの総数は 0 です

--- ツール作成 ---
ハンマー が作成されました。現在のツール総数: 1
レンチ が作成されました。現在のツール総数: 2

--- 各ツールの情報表示 ---
このツールの名前は ハンマー です。
このツールの名前は レンチ です。

--- ツール総数の再確認 ---
作成されたツールの総数は 2 です。
```

### staticとインスタンスメンバーの違い

#### インスタンスメンバー
- 各オブジェクトが独自に持つ
- `new`でオブジェクトを作成してから使用
- `this`参照が使える
- オブジェクトの状態を表現

#### staticメンバー
- クラス全体で1つだけ存在
- クラス名経由で直接アクセス
- `this`参照は使えない
- クラス全体の共通情報を表現

### staticを使う適切な場面

#### ユーティリティメソッド

<span class="listing-number">**サンプルコード3-37**</span>

   ```java
   public class MathUtils {
       public static double calculateDistance(double x1, double y1, double x2, double y2) {
           return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
       }
   }
   ```

#### ファクトリーメソッド

<span class="listing-number">**サンプルコード3-38**</span>

   ```java
   public class Student {
       private String name;
       private int age;
       
       private Student(String name, int age) {
           this.name = name;
           this.age = age;
       }
       
       public static Student createStudent(String name, int age) {
           if (age < 0 || age > 150) {
               throw new IllegalArgumentException("Invalid age");
           }
           return new Student(name, age);
       }
   }
   ```

#### 定数の定義

<span class="listing-number">**サンプルコード3-39**</span>

   ```java
   public class Constants {
       public static final double PI = 3.14159265359;
       public static final int MAX_RETRY_COUNT = 3;
   }
   ```

### staticの設計上の注意点

1. 過度な使用を避ける
   - staticメソッドはテストが困難になる場合がある
   - オブジェクト指向の柔軟性が失われる
2. 状態を持たせない
   - staticフィールドは慎重に使用する
   - 可変なstaticフィールドは避ける
3. 適切な責任分担
   - オブジェクトの振る舞いはインスタンスメソッドを使う
   - ユーティリティ的な処理はstaticメソッドを使う

### まとめ

static修飾子は強力な機能ですが、オブジェクト指向プログラミングの基本原則と相反する場合があります。
適切な場面で適切に使用することで、よりよい設計のプログラムを作成できます。
第7章以降では、インターフェイスや抽象クラスと組み合わせた、より高度なstatic活用パターンを学習します。









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

#### 重要なポイント

- `new Student[5]`はStudentオブジェクト5個を作るのではなく、Studentへの参照5個分の配列を作る
- 初期化直後の各要素は`null`である
- 各要素に対して個別に`new`でオブジェクトを作成して代入する必要がある

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

オブジェクト配列を操作する際のもっとも重要な注意点は、nullチェックです。

配列を`new`で初期化を行うと、配列の要素それぞれの初期値は`null`です。
すべての要素に実体化されたオブジェクトが格納されているとは限らないので、配列の要素の中身を走査する場合は必ずnullチェックを行いましょう。


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

### クラス型配列とプリミティブ型配列の違い

クラス型の配列の初期値は`null`でしたが、プリミティブ型の配列の場合は、それぞれの型の初期値が代入されています。
プリミティブ型を指定した配列には、`null`は入りません。

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

オブジェクト配列は、複数のオブジェクトを一括管理するための重要なデータ構造です。プリミティブ型の配列とは異なり、参照の配列であることを理解し、nullチェックを忘れずに行うことが重要です。

より柔軟なオブジェクト管理が必要な場合は、第10章で学習するコレクションフレームワーク（`ArrayList`など）の使用を検討してください。









## 章末演習

### 演習課題へのアクセス
本章の演習課題は、GitHubリポジトリで提供されています。<br>
`https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapter03/`

### 課題構成
- 本章の基本概念の理解確認
- 応用的な実装練習
- 実践的な総合問題

詳細な課題内容と実装のヒントは、各課題フォルダ内のREADME.mdを参照してください。

#### 実装する機能
1. 基本的な計算メソッド
   - add(int a, int b) - 2つの整数の和
   - subtract(int a, int b) - 2つの整数の差
   - multiply(int a, int b) - 2つの整数の積

2. オーバーロードの実践
   - add(double a, double b) - 小数の和
   - add(int a, int b, int c) - 3つの整数の和
   - add(String a, String b) - 文字列の連結

3. 実装のポイント
   - メソッドの戻り値の型に注意
   - 引数の個数や型によるオーバーロードの理解
   - mainメソッドから各メソッドを呼び出して動作確認

#### コード構造のヒント
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
目的： クラス設計とオブジェクトの基本操作を実践

##### 実装する機能
1. フィールドの定義
   - `name(String)` - 学生名
   - `mathScore(int)` - 数学の点数
   - `englishScore(int)` - 英語の点数
   - `scienceScore(int)` - 理科の点数

2. コンストラクタの実装
   - 引数なしコンストラクタ（デフォルト値設定）
   - 全フィールドを初期化するコンストラクタ

3. メソッドの実装
   - `getAverage()` - 3教科の平均点を計算
   - `getTotal()` - 3教科の合計点を計算
   - `displayInfo()` - 学生情報を整形して表示

4. カプセル化の実践
   - フィールドはprivateで定義
   - 読み取り専用のゲッター、検証付きセッターを実装
   - 点数の妥当性チェック（0-100の範囲）

### 発展課題への橋渡し

基礎課題を完了したら、発展課題で以下の概念を学びます。

#### Product.java - 状態変化を伴うオブジェクト
##### 新しく学ぶ概念
- オブジェクトの状態変化（在庫の増減）
- メソッド間の連携（sell → reduceStock）
- エラーハンドリングの基礎（在庫不足の処理）

##### 実装のアイデア
- 在庫数に応じた販売可否判定
- 在庫補充メソッド
- 販売可能数の計算

#### FuelExpenseCalculator.java - 複数オブジェクトの管理
##### 新しく学ぶ概念
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

1. スケルトン作成（5分）
   - クラスの枠組みだけ作成
   - メソッドのシグネチャだけ定義
   - コンパイルが通ることを確認

2. 基本機能実装（15分）
   - もっとも簡単なメソッドから実装
   - 各メソッドごとに動作確認
   - printlnでデバッグ出力

3. 機能追加（10分）
   - エラーチェック追加
   - 出力の整形
   - コメント追加

4. テストと改善（10分）
   - 境界値でのテスト
   - エラーケースの確認
   - コードの整理

### 学習を深める追加実験

基礎課題の完了後、以下の実験を試してみましょう。

1. メモリ効率の実験
   - 大量のオブジェクトを作成して挙動を観察
   - staticメソッドとインスタンスメソッドの使い分け

2. 設計の改善
   - より使いやすいAPIを考える
   - メソッドチェーンの実装に挑戦

3. 実用的な拡張
   - ファイルからデータを読み込む（次章の予習）
   - 簡単な対話型プログラムの作成

### デバッグのヒント

#### オブジェクト指向特有のデバッグ方法
1. `toString()`メソッドの活用
   - オブジェクトの状態を文字列で確認
   - デバッグ時の状態把握に便利
2. 段階的なコンストラクタ実行
   - 各フィールドの初期化を確認
   - `this`の参照先を意識
3. メソッド呼び出しの追跡
   - どのオブジェクトのメソッドが呼ばれているか
   - staticコンテキストとインスタンスコンテキストの区別

次のステップ： 基礎課題が完了したら、第4章「クラスとインスタンス」に進みましょう。第4章では、より高度なクラス設計と、複数クラスの連携について学びます。

> ※ 本章の高度な内容については、付録B.03「ソフトウェア設計原則」（`https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b03-software-design-principles/`）を参照してください。

## 次章への橋渡し

本章では、オブジェクト指向プログラミングの基本的な考え方と、クラス・フィールド・メソッドの基本的な書き方を学びました。簡単なgetter/setterメソッドやpublic/privateの使い分けについても触れました。

次章「第4章クラスとインスタンス」では、企業システム開発で必須のカプセル化技術について学びます。具体的には以下の内容を扱います。

- アクセス修飾子（`public`、`private`、`protected`、パッケージプライベート）の詳細
- 完全なカプセル化の実現方法（BankAccountの段階的改善例）
- データ検証とバリデーション
- 防御的プログラミング
- パッケージシステムとクラスの組織化

第3章で学んだ基礎をもとに、実践的で堅牢なクラス設計の技術を身につけていきましょう。







## よくあるエラーと対処法

本章では、オブジェクト指向プログラミングの基礎を学ぶ際にとくによく遭遇するエラーを扱います。

### 本章特有のエラー

#### 1. クラスとインスタンスの概念の混乱

問題: クラス名で直接インスタンスメソッドを呼び出そうとしてエラーになる

<span class="listing-number">**サンプルコード3-40**</span>

```java
// エラー例
Student.setName("太郎");  // クラス名で呼び出している
```

解決策:

<span class="listing-number">**サンプルコード3-41**</span>

```java
// 正しい例
Student student = new Student();  // インスタンスを作成
student.setName("太郎");          // インスタンスから呼び出し
```

重要なポイント:

- クラスは設計図、インスタンスは実体
- インスタンスメソッドはnewで作成したオブジェクトから呼び出す
- IDEでのプログラミングの場合は自動補完機能を活用する

#### 2. static vs non-staticの混乱
問題: staticメソッドからインスタンスメンバーにアクセスしようとする

<span class="listing-number">**サンプルコード3-42**</span>

```java
// エラー例
public static void main(String[] args) {
    int result = add(5, 3);  // インスタンスメソッドを直接呼び出し
}
```

解決策:
<span class="listing-number">**サンプルコード3-43**</span>

```java
// 正しい例
public static void main(String[] args) {
    Calculator calc = new Calculator();
    int result = calc.add(5, 3);  // インスタンス経由で呼び出し
}
```

##### 重要なポイント
- staticメソッドはクラスに属し、インスタンスなしで呼び出せる
- インスタンスメソッドはオブジェクトに属し、インスタンスが必要

#### 3. 基本的な構文エラー
問題: Javaの基本構文を間違えて記述する

<span class="listing-number">**サンプルコード3-44**</span>

```java
// よくあるエラー例
System.out.println("Hello")  // セミコロン忘れ
Public class MyClass { }      // 大文字のP
```

解決策:
<span class="listing-number">**サンプルコード3-45**</span>

```java
// 正しい例
System.out.println("Hello");  // セミコロンを追加
public class MyClass { }      // 小文字のp
```

##### 重要なポイント
- すべての文の末尾にはセミコロンが必要
- キーワードは小文字（public、class、void等）
- クラス名は大文字で始める（慣例）

### 関連する共通エラー

以下のエラーも本章の内容に関連します。

- 　`NullPointerException`（→ 付録A.1.1）
    - とくに初期化されていないフィールドへのアクセスで発生しやすい
- コンストラクタエラー（→ 第4章）
    - 本章では基本概念のみ、詳細は第4章で学習
- アクセス修飾子エラー（→ 付録A.2.3）
    - `private`フィールドへの不正アクセス

### デバッグのヒント

#### エラーに遭遇した際の基本的な対処法

1. エラーメッセージを読む
    + 行番号とエラーの種類を確認
2. クラスとインスタンスの区別
    + staticかどうかを確認
3. 初期化の確認
    + すべてのフィールドが初期化されているか確認

### さらに学ぶには

- 付録H: Java共通エラーガイド（基本的なエラーパターン）
- 第4章: より詳細なコンストラクタとフィールドの扱い
- 第14章: 例外処理の体系的な学習

## オブジェクトの等価性とhashCode

オブジェクト指向プログラミングでは、オブジェクト同士が「等しい」かどうかを判定します。Javaでは、`==`演算子と`equals`メソッドの違いを理解することが重要です。

### ==演算子とequalsメソッドの違い

<span class="listing-number">**サンプルコード3-46**</span>

```java
public class StringComparison {
    public static void main(String[] args) {
        String str1 = new String("Hello");
        String str2 = new String("Hello");
        String str3 = str1;
        
        // ==演算子：参照の比較
        System.out.println(str1 == str2);  // false（異なるオブジェクト）
        System.out.println(str1 == str3);  // true（同じオブジェクトを参照）
        
        // equalsメソッド：値の比較
        System.out.println(str1.equals(str2));  // true（同じ値）
        System.out.println(str1.equals(str3));  // true（同じ値）
    }
}
```

### カスタムクラスでのequalsメソッドの実装

独自のクラスで`equals`メソッドを適切に実装する方法を示します。

<span class="listing-number">**サンプルコード3-47**</span>

```java
public class Student {
    private String id;
    private String name;
    
    public Student(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    @Override
    public boolean equals(Object obj) {
        // 同一オブジェクトの場合
        if (this == obj) return true;
        
        // nullまたは異なるクラスの場合
        if (obj == null || getClass() != obj.getClass()) return false;
        
        // 型変換して比較
        Student student = (Student) obj;
        return id != null && id.equals(student.id);
    }
    
    @Override
    public int hashCode() {
        // equalsで使用するフィールドと同じフィールドを使用
        return id != null ? id.hashCode() : 0;
    }
}
```

### equals/hashCodeの契約

`equals`メソッドをオーバーライドする場合、必ず`hashCode`メソッドもオーバーライドしてください。これは以下の契約を満たすために必要です。

1. `equals`で等しいと判定される2つのオブジェクトは、同じ`hashCode`を返す
2. `hashCode`が同じでも、`equals`で等しいとは限らない（ハッシュ衝突）
3. プログラムの実行中、同じオブジェクトの`hashCode`は一貫している

## モダンJavaにおけるオブジェクト指向

Java 21 LTSでは、オブジェクト指向プログラミングをより簡潔に記述できる新機能が追加されています。

### Recordクラス（Java 14以降）

不変のデータクラスを簡潔に定義できる`record`は、多くのボイラープレートコードを削減します。

<span class="listing-number">**サンプルコード3-48**</span>

```java
// 従来の方法
import java.util.Objects;

public class Point {
    private final int x;
    private final int y;
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return x == point.x && y == point.y;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
    
    @Override
    public String toString() {
        return "Point{x=" + x + ", y=" + y + '}';
    }
}

// Record を使った方法（Java 14以降）
public record Point(int x, int y) {
    // コンストラクタ、getter、equals、hashCode、toStringが自動生成される
}
```

### Pattern Matching（Java 16以降）

`instanceof`演算子と組み合わせたパターンマッチングにより、型チェックとキャストを簡潔に記述できます。

<span class="listing-number">**サンプルコード3-49**</span>

```java
// 従来の方法
public void processShape(Shape shape) {
    if (shape instanceof Circle) {
        Circle circle = (Circle) shape;
        System.out.println("円の半径: " + circle.getRadius());
    } else if (shape instanceof Rectangle) {
        Rectangle rectangle = (Rectangle) shape;
        System.out.println("長方形の面積: " + rectangle.getArea());
    }
}

// Pattern Matching を使った方法（Java 16以降）
public void processShape(Shape shape) {
    if (shape instanceof Circle circle) {
        System.out.println("円の半径: " + circle.getRadius());
    } else if (shape instanceof Rectangle rectangle) {
        System.out.println("長方形の面積: " + rectangle.getArea());
    }
}
```

### Sealed Classes（Java 17以降）

クラス階層を制限し、より安全な継承を実現できます。

<span class="listing-number">**サンプルコード3-50**</span>

```java
public sealed class Shape 
    permits Circle, Rectangle, Triangle {
    // Shapeクラスを継承できるのは、Circle、Rectangle、Triangleのみ
}

public final class Circle extends Shape {
    private double radius;
    // 実装
}

public final class Rectangle extends Shape {
    private double width, height;
    // 実装
}

public final class Triangle extends Shape {
    private double base, height;
    // 実装
}
```

これらの新機能により、より安全で簡潔なオブジェクト指向プログラミングが可能になっています。
ただし、基本的な概念の理解が前提となるため、本章で学んだ基礎をしっかりと理解してから活用しましょう。
