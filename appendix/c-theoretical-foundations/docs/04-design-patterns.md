# 4. デザインパターンの理論的分析

## この項目で学ぶこと

デザインパターン（design pattern）は、ソフトウェアの設計で繰り返し現れる問題と、その解決の型に名前を付けたものです。
本文でも第8章「Javaの特殊なクラス形式」のSingleton、第7章「抽象クラスとインターフェイス」のFactory、第19章「高度なイベント処理パターン」のObserverなど、いくつかのパターンを使っています。

この項目では、個々のパターンの書き方を覚えるのではなく、パターンを「分析する」見方を身につけることを目標にします。

- パターンとは何か、どこから来た考え方か
- パターンが共通して使っている設計原則（インターフェイスに対してプログラムする、継承よりコンポジション）
- 「何を変えられるようにするパターンか」「その代わりに何を失うか」という分析の仕方
- Singleton、Decorator、Observerの3つを、その見方で分析する

## パターンの由来

### 建築のパターン・ランゲージ

パターンという考え方は、建築から来ています。
建築家のクリストファー・アレグザンダー（Christopher Alexander）らは、1977年の書籍『A Pattern Language』で、町や建物の設計で繰り返し現れる問題と、その解決の核心を253のパターンとしてまとめました。
アレグザンダーは、パターンを「ある状況で繰り返し起きる問題と、その解決の核心を述べたもので、その解決は何度使っても同じ形にはならない」という趣旨で説明しています。
パターンは部品ではなく、状況に合わせて形を変えて使う「解決の考え方」だということです。

### ソフトウェアへの導入とGoF

1987年、ケント・ベック（Kent Beck）とウォード・カニンガム（Ward Cunningham）は、OOPSLAのワークショップで、アレグザンダーの考え方をオブジェクト指向プログラミングに使う試みを発表しました。

1994年、ガンマ（Erich Gamma）、ヘルム（Richard Helm）、ジョンソン（Ralph Johnson）、ブリシディース（John Vlissides）の4人が書籍『Design Patterns: Elements of Reusable Object-Oriented Software』を出版しました。
4人は「Gang of Four（4人組）」と呼ばれ、この本は略してGoF本と呼ばれます。
GoF本は23のパターンを、目的によって3つに分類しています。

| 分類 | 目的 | パターン |
|------|------|----------|
| 生成（Creational） | オブジェクトの作り方を、使う側から切り離す | Abstract Factory、Builder、Factory Method、Prototype、Singleton |
| 構造（Structural） | クラスやオブジェクトを組み合わせて、より大きな構造を作る | Adapter、Bridge、Composite、Decorator、Facade、Flyweight、Proxy |
| 振る舞い（Behavioral） | オブジェクトのあいだの役割分担と、やりとりの仕方を決める | Chain of Responsibility、Command、Interpreter、Iterator、Mediator、Memento、Observer、State、Strategy、Template Method、Visitor |

GoF本では、1つのパターンを次の4つの要素で記述しています。

- パターン名
    + 設計の会話で使う共通の語彙になる。「ここはObserverにしよう」の一言で、構造と意図が伝わる
- 問題
    + いつそのパターンを使うのか。どんな状況・制約のもとで起きる問題か
- 解決
    + 設計を構成する要素（クラス、オブジェクト）と、その関係・責任・協調の仕方
- 結果（consequences）
    + パターンを使うことで得られるものと、払う代償

4つめの「結果」が、パターンを分析するときの中心です。
パターンは無料の改善ではなく、何かと引き換えに柔軟さを得る取引だからです。

## パターンを支える2つの原則

GoF本の第1章は、多くのパターンに共通する設計原則として、次の2つを挙げています。

### インターフェイスに対してプログラムする

1つめは「実装に対してではなく、インターフェイスに対してプログラムせよ（Program to an interface, not an implementation）」です。
変数や引数の型を具体的なクラスではなくインターフェイス（または抽象クラス）にすれば、使う側は具体的なクラスを知らずに済みます。
`ArrayList<String> list = new ArrayList<>();`ではなく`List<String> list = new ArrayList<>();`と書くのは、この原則の身近な例です。
項目3の言葉で言えば、使う側と具体的なクラスのあいだの結合度を下げる原則です。

### 継承よりコンポジション

2つめは「クラスの継承よりもオブジェクトのコンポジションを優先せよ（Favor object composition over class inheritance）」です。
継承で機能を足すと、どの組み合わせになるかはコンパイルの時点で決まり、子クラスは親クラスの内部に依存します（項目2の「継承はカプセル化を破る」）。
コンポジションなら、部品のオブジェクトを実行中に差し替えられ、部品の内部にも依存しません。
Decorator、Strategy、Observer、Compositeなど、多くのパターンはコンポジションを使って作られています。

## パターンを分析する見方

### 何を変えられるようにするか

GoF本には、各パターンが「設計のどの側面を、変更できるようにするか」をまとめた表があります。
パターンを分析するときは、まずこの問いを立てます。

| パターン | 変えられるようにするもの |
|----------|--------------------------|
| Strategy | アルゴリズム（並べ替えの基準、計算の方法など） |
| Decorator | オブジェクトに付け加える責任（機能）。サブクラスを作らずに足せる |
| Observer | ある対象に依存するオブジェクトの数と、それらを最新の状態に保つ方法 |
| Factory Method | 作るオブジェクトのクラス |
| Template Method | アルゴリズムの一部の手順 |
| Iterator | 集まりの要素をたどる方法（集まりの内部構造を見せずに） |

「変えられるようにするもの」は、項目1で見たパーナスの情報隠蔽の「あとで変わりそうな決定」に当たります。
パターンは、変わりそうな部分をインターフェイスの向こうに閉じ込める方法を、状況ごとに型にしたものと言えます。

### 何と引き換えにするか

柔軟さには代償があります。
多くのパターンに共通する代償は次のとおりです。

- クラスとオブジェクトの数が増える
- 処理の流れが間接的になり、コードを読んで追いにくくなる
- 実行中に組み立てるので、どの組み合わせで動いているかが実行するまでわからない

変わる見込みのない部分にパターンを使うと、代償だけを払うことになります。
パターンを使うかどうかは、「その部分が実際に変わるか、変わったときにどれだけ困るか」で決めます。

## Singleton：生成パターンの分析

### 意図と構造

Singletonの意図は、あるクラスのインスタンスが1つしかないことを保証し、それにアクセスする方法を1つ用意することです。
コンストラクターを`private`にして外から`new`できないようにし、唯一のインスタンスを返す静的メソッドを用意します。

`src/main/java/com/example/theory/patterns/SingletonPattern.java`には、4つの実装があります。

| 実装 | インスタンスを作る時点 | スレッドセーフか |
|------|------------------------|------------------|
| `UnsafeSingleton` | 最初の`getInstance()` | いいえ |
| `DatabaseConnection`（ダブルチェックロッキング） | 最初の`getInstance()` | はい（`volatile`が必要） |
| `BillPughSingleton`（ホルダークラス） | 最初の`getInstance()` | はい |
| `DatabaseConnectionEnum`（enum） | enumのクラスが初期化されるとき | はい |

`UnsafeSingleton`では、2つのスレッドが同時に`instance == null`を確かめると、両方がインスタンスを作ってしまうことがあります。
これは第15章「マルチスレッドプログラミング」で学ぶ競合状態の一種です（並行プログラミングの理論は項目7で扱います）。

### ダブルチェックロッキングと`volatile`

`DatabaseConnection`は、`synchronized`のブロックの外と中で2回`null`を確かめます。
インスタンスができた後は、ロックを取らずに返せるので速い、というのがこの書き方の狙いです。

この書き方は、`instance`フィールドに`volatile`を付けないと正しく動きません。
`new DatabaseConnection()`は「メモリを確保する」「コンストラクターを実行する」「参照をフィールドに書く」という処理からなり、`volatile`がないと、ほかのスレッドから見て、コンストラクターが終わる前に参照が書かれたように見えることがあります。
するとほかのスレッドは、初期化の終わっていないオブジェクトを受け取ってしまいます。
Java 5（JSR 133）で改訂されたJavaメモリモデルでは、`volatile`のフィールドへの書き込みと読み込みのあいだに順序の保証（happens-before関係）があるため、`volatile`を付ければ正しく動きます。
それより前のメモリモデルでは、この書き方は`volatile`を付けても正しくないことが、Bill Pughらによって指摘されていました。

### ホルダークラスとenum

`BillPughSingleton`は、インスタンスを静的な入れ子クラス`SingletonHelper`の`static final`フィールドに持たせます。
Javaの言語仕様では、クラスはそのクラスの静的フィールドが初めて使われるときなどに初期化され、初期化はJVMによって1回だけ、スレッドセーフに行われます。
そのため、`getInstance()`を初めて呼んだときに`SingletonHelper`が初期化され、インスタンスが1つだけ作られます。
`synchronized`も`volatile`も書かずに、遅延初期化とスレッドセーフを両立できます。

`DatabaseConnectionEnum`は、要素が1つのenumです。
enumの定数はJVMが1つだけ作ることを保証し、リフレクションで新しいインスタンスを作ることもできません。
『Effective Java』は、多くの場合これがSingletonを実装する最善の方法だとしています。

次のコマンドで、各実装がいつインスタンスを作るかを確かめられます。

```bash
java -cp out com.example.theory.patterns.SingletonPattern
```

```
--- Double-checked locking ---
DatabaseConnection initialized
Same instance? true
--- Bill Pugh (holder class) ---
Before first getInstance()
BillPughSingleton initialized
Same instance? true
--- Enum ---
Before first use of INSTANCE
DatabaseConnectionEnum initialized
Executing query via enum: SELECT 1
Same instance? true
```

「Before first getInstance()」の後に「BillPughSingleton initialized」が表示されています。
`BillPughSingleton`クラスが読み込まれても、`getInstance()`を呼ぶまでインスタンスは作られないことがわかります。
どの実装でも、2回取得したインスタンスが同じ（`Same instance? true`）です。
`Main`の`patterns`のデモ（`java -ea -cp out com.example.theory.Main patterns`）でも、同じ3つの実装を確かめられます。

### 分析：Singletonの代償

Singletonの代償は、項目3の言葉で説明できます。
Singletonはどこからでも`getInstance()`で取り出せるので、状態を持つSingletonは大域変数と同じで、共通結合を生みます。

- `getInstance()`を呼ぶクラスは、Singletonへの依存がメソッドの引数に現れない
- テストで本物の代わりに偽物を使いたくても、差し替えにくい
- 状態がテストのあいだで持ち越され、テストの実行順によって結果が変わることがある

項目3の`CouplingExamples`の`BadOrderProcessor`がまさにこの形で、`GoodOrderProcessor`のように依存をコンストラクターで受け取る形にすると、テスト用の偽物を渡せるようになりました。
「インスタンスが1つ」であることと、「どこからでも取り出せる」ことは別の問題です。
インスタンスを1つだけ作り、それを必要なクラスに引数で渡す設計にすれば、前者だけを得て後者の代償を避けられます。

## Decorator：構造パターンの分析

### 意図と構造

Decoratorの意図は、オブジェクトに責任（機能）を動的に付け加えることです。
サブクラスを作る代わりに、同じインターフェイスを持つ「包むオブジェクト」で元のオブジェクトを包みます。

`src/main/java/com/example/theory/patterns/DecoratorPattern.java`では、コーヒーの値段と説明を例にしています。

```
        «interface» Coffee
        getCost(), getDescription()
           ▲                ▲
           |                |
     SimpleCoffee     CoffeeDecorator（Coffeeを1つ持つ）
                       ▲        ▲        ▲
                       |        |        |
                     Milk     Sugar    Whip
```

`CoffeeDecorator`は`Coffee`インターフェイスを実装し、同時に`Coffee`を1つフィールドに持ちます。
`MilkDecorator`の`getCost()`は、包んでいるコーヒーの値段に0.5を足して返します。

### 分析：組み合わせの数

トッピングが n 種類あり、それぞれ「入れる・入れない」を選べるとします。
継承で表すと、組み合わせごとにサブクラス（`CoffeeWithMilkAndSugar`など）が必要になり、その数は 2^n になります。
3種類なら8、10種類なら1024です。
Decoratorなら、トッピング1種類につき1つのクラス、つまり n 個のクラスで、すべての組み合わせを実行中に作れます。
ミルクを2回入れる（同じDecoratorで2回包む）こともできます。
これが「継承よりコンポジション」の具体的な効果です。

### 分析：関数の合成として見る

Decoratorは関数の合成として見ることもできます。
値段について、ミルクは f(x) = x + 0.5、砂糖は g(x) = x + 0.3、ホイップは h(x) = x + 0.7 という関数で、包むことは関数を合成することに当たります。
`new WhipDecorator(new MilkDecorator(new SugarDecorator(new SimpleCoffee())))`の値段は h(f(g(1.0))) です。

`Main`の`patterns`のデモでは、次の行が表示されます。

```
--- Decorator Pattern ---
Simple Coffee : $1.0
Simple Coffee, Milk : $1.5
Simple Coffee, Milk, Sugar : $1.8

Complex coffee (function composition):
Simple Coffee, Sugar, Milk, Whip : $2.5
```

この見方から、次のことがわかります。

- 値段の関数はどれも「足し算」なので、包む順番を変えても値段は変わらない（足し算は順番を入れ替えても結果が同じ）
- 説明の文字列は「後ろに付け足す」関数なので、包む順番で結果が変わる。最後の行が「Sugar, Milk, Whip」の順になっているのは、内側から砂糖、ミルク、ホイップの順に包んだから
- 「2割引き」のような掛け算のDecoratorを足すと、値段も順番に依存するようになる

Decoratorを使う設計では、包む順番で結果が変わるかどうかを意識しておく必要があります。
なお、この例では金額を`double`で扱っていますが、`double`は0.1のような10進数の小数を正確に表せないため、実際の金額の計算には`BigDecimal`を使います。

### 標準ライブラリのDecorator

Javaの`java.io`パッケージは、Decoratorパターンの代表的な例です。
`BufferedReader`や`LineNumberReader`は、`Reader`を受け取って包み、自分も`Reader`として振る舞います。

```java
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;

public class IoDecoratorDemo {
    public static void main(String[] args) throws IOException {
        Reader source = new StringReader("first\nsecond\nthird\n");
        // LineNumberReaderはBufferedReaderのサブクラスで、Readerを包んで行番号を数える機能を足す
        try (LineNumberReader reader = new LineNumberReader(source)) {
            String line;
            while ((line = reader.readLine()) != null) {
                IO.println(reader.getLineNumber() + ": " + line);
            }
        }
    }
}
```

```
1: first
2: second
3: third
```

`StringReader`を`FileReader`に替えればファイルから、ソケットの`InputStream`を`InputStreamReader`で包んだものに替えればネットワークから読めます。
どこから読むか（包まれる側）と、どう読むか（包む側）を独立に選べるのが、Decoratorの利点です。
`Collections.unmodifiableList`も、`List`を包んで「変更できない」という性質を足すDecoratorの一種と見られます。

### 分析：Decoratorの代償

- 小さなオブジェクトがたくさんでき、デバッガーで中身を追うと何重にも包まれている
- 包まれたオブジェクトと包んだオブジェクトは別のオブジェクトなので、`==`での比較や、具体的なクラスを前提にした処理（`instanceof SimpleCoffee`など）は期待どおりに動かない
- インターフェイスのメソッドが多いと、Decoratorはすべてのメソッドを書いて、包んだオブジェクトに処理を任せる必要がある

## Observer：振る舞いパターンの分析

### 意図と構造

Observerの意図は、あるオブジェクトの状態が変わったときに、それに依存するすべてのオブジェクトへ自動的に知らせることです。
状態を持つ側をSubject（観察される側）、知らせを受け取る側をObserver（観察する側）と呼びます。

`src/main/java/com/example/theory/patterns/ObserverPattern.java`では、株価（`Stock`）がSubjectで、株価の表示（`StockDisplay`）、しきい値を超えたときの警告（`StockAlert`）、記録（`StockLogger`）がObserverです。
`Stock`は`Observer`インターフェイスの一覧だけを持ち、具体的なクラスを知りません。

`Main`の`patterns`のデモの出力の一部を見てみます。

```
=== Price Update 2 ===
Display 1 - Stock price updated: AAPL = $156.0
Display 2 - Stock price updated: AAPL = $156.0
Price Alert - ALERT: AAPL exceeded threshold of $155.0 (current: $156.0)
Log: AAPL = $156.0

=== After removing Display 1 ===
Display 2 - Stock price updated: AAPL = $154.0
Log: AAPL = $154.0
```

価格を156.0にすると、登録された4つのObserverに順に知らせが届き、しきい値155.0を超えたので警告も表示されます。
`Display 1`を登録から外した後は、`Display 1`には知らせが届きません。
1回目の価格の変更（152.0）では、しきい値を超えないので警告は表示されません。

### 分析：何が分離されるか

Observerパターンで、SubjectはObserverについて「`update`メソッドを持つ」ことしか知りません。

- Observerの種類を増やしても、Subject（`Stock`）のコードは変わらない
- Subjectは、誰がいくつ登録されているかを知らずに知らせを送れる
- 第19章「高度なイベント処理パターン」で見るように、Subjectをモデル、Observerを画面の表示にすれば、モデルが画面のクラスに依存しない設計になる

知らせ方には2つの型があります。

- プッシュ型
    + Subjectが、変わった値そのものを`update`の引数で渡す
- プル型
    + Subjectは「変わった」ことと自分自身だけを渡し、Observerが必要な値を取り出す

`ObserverPattern`の`update(Stock stock)`は、Subject自身を渡して、Observerが`getPrice()`で値を取り出すプル型です。
プッシュ型はObserverがSubjectを知らなくて済み、プル型はObserverごとに必要な値だけを取り出せます。

### 分析：Observerの代償と落とし穴

Observerには、処理の流れがコードの字面から見えにくくなるという代償があります。
`setPrice`の1行から、どのクラスのどのメソッドが動くかは、実行中に何が登録されているかで決まります。
そのほかにも、次の落とし穴があります。

- 知らせる順番
    + `ObserverPattern`は登録した順に知らせるが、Observerの側が順番に頼ると、登録の順番を変えたときに壊れる
- 知らせの連鎖
    + Observerが知らせを受けて別のSubjectの状態を変え、それがまた知らせを生むと、処理の流れを追いにくくなり、最悪の場合は無限に続く
- 登録の解除忘れ
    + Subjectが一覧でObserverへの参照を持ち続けるので、不要になったObserverがガベージコレクションで回収されない
- 知らせの途中での登録・解除
    + Observerが`update`のなかで自分を登録から外すと、一覧を拡張for文でたどっている途中で一覧が変わる

最後の落とし穴を確かめてみます。
次のプログラムは、知らせを1回受けると自分を登録から外すリスナーを3つ登録し、一覧に`ArrayList`を使った場合と`CopyOnWriteArrayList`を使った場合を比べます。

```java
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ObserverRemovalDemo {
    interface Listener {
        void onEvent(String event);
    }

    static class Subject {
        private final List<Listener> listeners;

        Subject(List<Listener> listeners) {
            this.listeners = listeners;
        }

        void add(Listener l) {
            listeners.add(l);
        }

        void remove(Listener l) {
            listeners.remove(l);
        }

        void fire(String event) {
            for (Listener l : listeners) {
                l.onEvent(event);
            }
        }
    }

    // 1回だけ通知を受けて、自分を登録から外すリスナー
    static class OneShot implements Listener {
        private final Subject subject;
        private final String name;

        OneShot(Subject subject, String name) {
            this.subject = subject;
            this.name = name;
        }

        @Override
        public void onEvent(String event) {
            IO.println(name + " got " + event);
            subject.remove(this);
        }
    }

    static void run(String label, List<Listener> list) {
        IO.println("--- " + label + " ---");
        Subject subject = new Subject(list);
        subject.add(new OneShot(subject, "A"));
        subject.add(new OneShot(subject, "B"));
        subject.add(new OneShot(subject, "C"));
        try {
            subject.fire("e1");
            subject.fire("e2");
        } catch (ConcurrentModificationException e) {
            IO.println("ConcurrentModificationException");
        }
    }

    public static void main(String[] args) {
        run("ArrayList", new ArrayList<>());
        run("CopyOnWriteArrayList", new CopyOnWriteArrayList<>());
    }
}
```

```
--- ArrayList ---
A got e1
ConcurrentModificationException
--- CopyOnWriteArrayList ---
A got e1
B got e1
C got e1
```

`ArrayList`では、Aが自分を外した直後に、拡張for文が一覧の変更を検出して`ConcurrentModificationException`を投げ、BとCには知らせが届きません。
外したObserverが一覧の最後から2番目だった場合は、例外にならずに最後のObserverへの知らせが黙って飛ばされることもあります（拡張for文が、要素が残っていないと判断して終わるため）。
`CopyOnWriteArrayList`は、変更のたびに中の配列をコピーし、たどっている途中の拡張for文は変更前の配列を見続けるので、3つすべてに知らせが届きます。
変更のたびにコピーするので、登録・解除が多い場合には向きませんが、知らせの回数に比べて登録・解除が少ないObserverの一覧には向いています。
`ObserverPattern`の`Stock`は一覧に`ArrayList`を使っているので、`update`のなかで`removeObserver`を呼ぶと同じ問題が起きます。

Javaの標準ライブラリには`java.util.Observer`と`java.util.Observable`がありましたが、Java 9で非推奨になりました。
APIドキュメントは、より豊かなイベントの仕組みには`java.beans`パッケージ（`PropertyChangeListener`など）を、リアクティブストリームの形の処理には`java.util.concurrent.Flow`を使うよう勧めています。
Swingのリスナーの仕組み（`addActionListener`など）も、Observerパターンの一種です（第18章「基本的なイベント処理」）。

## ラムダ式とパターン

GoF本の例はC++とSmalltalkで書かれていて、出版された1994年にはJavaはまだ公開されていませんでした。
言語の機能が増えると、パターンの書き方は簡単になります。

Strategyパターンは、アルゴリズムをインターフェイスの向こうに置き、実行中に差し替えられるようにするパターンです。
Javaの`Comparator`はその典型で、Java 8以降はラムダ式やメソッド参照で、Strategyのクラスを書かずに渡せます。

```java
list.sort(Comparator.comparing(String::length));
```

ピーター・ノーヴィグ（Peter Norvig）は1996年の講演で、関数を値として扱える動的な言語では、GoFのパターンの多くが言語の機能で簡単になるか、パターンとして意識する必要がなくなると指摘しました。
パターンは言語の機能の不足を補う面も持っているということです。
ただし、パターンの名前と、「何を変えられるようにするか」という分析は、書き方が変わっても設計の会話の語彙として役に立ちます。

## よくある誤解

- 「パターンをたくさん使うほど、よい設計になる」
    + パターンは代償と引き換えに柔軟さを得るもの。変わる見込みのない部分にパターンを使うと、クラスが増えて読みにくくなるだけになる
- 「パターンはコードの部品として再利用するものである」
    + パターンは設計の考え方で、状況に合わせて毎回形を変えて書く。ライブラリのように丸ごと持ってきて使うものではない
- 「静的なファクトリメソッド（`List.of`など）はFactory Methodパターンである」
    + GoFのFactory Methodは、作るオブジェクトのクラスをサブクラスに決めさせるパターン。`List.of`のような静的メソッドは「静的ファクトリメソッド」と呼ばれる別のもので、『Effective Java』もこの2つは違うものだと注意している
- 「Singletonはよいパターンである」または「Singletonは使ってはいけない」
    + インスタンスが1つであることが本当に必要か、どこからでも取り出せる必要があるかを分けて考える。状態を持たないもの、または状態が変わらないものなら、代償は小さい

## 実務での使いどころ

- 既存のコードやライブラリを読むときに、パターンの名前がわかると構造を早くつかめる（`java.io`はDecorator、Swingのリスナーは Observer、`Comparator`はStrategy、拡張for文の裏側はIterator）
- 設計に迷ったら、「この部分で、後から変えたくなるのは何か」を考え、それを変えられるようにするパターンを探す
- パターンを使う前に、使わない場合のコードを書いてみる。変更が実際に起きて困ったときに、パターンへ書き直す（リファクタリング）のでも遅くない

## 本文・付録との関係

- 第7章「抽象クラスとインターフェイス」
    + Factoryパターン（Simple Factory、Factory Method）
- 第8章「Javaの特殊なクラス形式」
    + enumによるSingleton、Strategy
- 第11章「ラムダ式と関数型インターフェイス」
    + ラムダ式で書くStrategy
- 第14章「ファイルI/O」
    + `BufferedReader`などのストリームの組み合わせ（Decorator）
- 第15章「マルチスレッドプログラミング」
    + スレッドセーフなSingleton、`volatile`
- 第18章「基本的なイベント処理」、第19章「高度なイベント処理パターン」
    + リスナーとObserverパターン
- 付録B.03「ソフトウェア設計原則」
    + GoFパターンの分類とSOLID原則の実装例
- 付録B.11「Enumの高度な活用」
    + enumを使ったパターン
- 付録B.14「Javaメモリモデル」
    + ダブルチェックロッキングに`volatile`が必要な理由（happens-before関係）
- この付録の項目2・項目3・項目7
    + 継承よりコンポジション（項目2）、結合度（項目3）、並行プログラミングの理論（項目7）

## 参考文献

- C. Alexander, S. Ishikawa, M. Silverstein, "A Pattern Language: Towns, Buildings, Construction", Oxford University Press, 1977
- K. Beck, W. Cunningham, "Using Pattern Languages for Object-Oriented Programs", OOPSLA '87 Workshop, 1987
- E. Gamma, R. Helm, R. Johnson, J. Vlissides, "Design Patterns: Elements of Reusable Object-Oriented Software", Addison-Wesley, 1994
- J. Bloch, "Effective Java", 3rd ed., Addison-Wesley, 2018（項目1「コンストラクターの代わりにstaticファクトリメソッドを検討する」、項目3「privateのコンストラクターかenum型でシングルトン特性を強制する」）
- B. Goetz et al., "Java Concurrency in Practice", Addison-Wesley, 2006
- J. Gosling et al., "The Java Language Specification, Java SE 25 Edition", 12.4「Initialization of Classes and Interfaces」（`https://docs.oracle.com/javase/specs/`）
- JSR 133: Java Memory Model and Thread Specification Revision（`https://jcp.org/en/jsr/detail?id=133`）
- Java SE 25 APIドキュメント `java.util.Observable`（`https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/Observable.html`）
