# 2. オブジェクト指向の理論的基盤：抽象データ型、カプセル化、継承

## この項目で学ぶこと

本文の第3章〜第7章では、クラスの書き方、`private`によるカプセル化、`extends`による継承、インターフェイスを学びます。
この項目では、それらの機能の背後にある3つの理論を説明します。

- 抽象データ型（ADT）
    + 型を「中身」ではなく「操作とその性質」で定義する考え方
- カプセル化と情報隠蔽
    + 型の中身を外から触れなくして、正しい状態を保証する考え方
- 継承と部分型（サブタイプ）
    + ある型を別の型の代わりに使ってよいのはどんなときか、という問い。リスコフの置換原則（LSP）がその答えになる

項目1で見たように、これらはどれも「大きなプログラムを、一度に全部を考えなくても正しく作れるようにする」ために生まれました。

## 抽象データ型（ADT）

### 型とは何か

C言語で`int`型を使うとき、`int`がメモリ上で何ビットか、負の数をどう表しているか（2の補数かどうか）を意識することはほとんどありません。
意識しているのは「足し算や比較ができる」「大きさに上限がある」といった性質だけです。
つまり、私たちは`int`を、操作と性質によって理解しています。

抽象データ型は、この見方を利用者が作る型にも広げたものです。
1974年、リスコフ（Barbara Liskov）とジルズ（Stephen Zilles）は、抽象データ型を「その型の値に対して使える操作の集まりによって特徴づけられる型」として説明しました。
利用者は操作だけを使い、値がどう表現されているかを知りません。
そのため、表現を変えても、操作の意味が同じなら利用者のプログラムは変わらずに動きます。

### スタックを例にした代数的仕様

抽象データ型を「操作とその性質」で書き表す方法の1つに、代数的仕様（algebraic specification）があります。
1970年代にGuttagらが発展させた方法で、操作を関数として書き、操作のあいだに成り立つ等式（公理）で意味を決めます。

スタック（後から入れたものを先に取り出す入れ物）の代数的仕様は、次のようになります。
ここでは、スタックを書き換えられる入れ物ではなく、数のような「値」として扱います。

```
操作
  new     : → Stack              空のスタックを作る
  push    : Stack × T → Stack    要素を1つ積んだスタック
  pop     : Stack → Stack        いちばん上を取り除いたスタック
  top     : Stack → T            いちばん上の要素
  isEmpty : Stack → Boolean      空かどうか

公理（どんなスタック s と要素 x でも成り立つ）
  1. isEmpty(new) = true
  2. isEmpty(push(s, x)) = false
  3. top(push(s, x)) = x
  4. pop(push(s, x)) = s
  top(new) と pop(new) は定義しない（エラー）
```

この4つの公理だけで、どんな操作の列についても結果が決まります。
たとえば `top(pop(push(push(new, 1), 2)))` は、公理4で `top(push(new, 1))` になり、公理3で1になります。
配列を使うかリストを使うかは、どこにも書かれていません。
これが「中身ではなく、操作とその性質で型を定義する」という意味です。

### Javaでの表現

Javaでは、抽象データ型の操作をインターフェイスで、表現をそれを実装するクラスで書きます。
`src/main/java/com/example/theory/structures/Stack.java`はスタックの操作を宣言したインターフェイスで、コメントに上の公理を書いています。
`src/main/java/com/example/theory/structures/ArrayStack.java`は、容量に上限のある配列でそれを実装したクラスです。

Javaのメソッドはスタックを値として返すのではなく、オブジェクトの状態を書き換えます。
そのため、代数的仕様の操作とJavaのメソッドは次のように対応します。

| 代数的仕様 | Javaのメソッド |
|------------|----------------|
| `push(s, x)` | `push(x)`で、状態を`push(s, x)`に変える |
| `top(s)` | `peek()`で、`top(s)`を返す |
| `pop(s)`と`top(s)` | `pop()`で、`top(s)`を返して、状態を`pop(s)`に変える |
| `isEmpty(s)` | `isEmpty()` |
| `top(new)`、`pop(new)`がエラー | 空のスタックに`peek()`・`pop()`すると`EmptyStackException` |

`ArrayStack`には、公理がこの実装で成り立つことを`assert`文で確かめる`checkAxioms`メソッドがあります。
次のコマンドで実行できます（`-ea`を付けないと`assert`文が実行されないので、注意を表示して終わります）。

```bash
java -ea -cp out com.example.theory.structures.ArrayStack
```

```
pop on empty stack: EmptyStackException
Stack axioms hold for ArrayStack
```

1行目は、空のスタックに`pop()`したときにエラー（例外）になることを確かめた結果です。
2行目が表示されれば、すべての`assert`文が成り立ったことになります。
`Main`の`structures`のデモ（`java -ea -cp out com.example.theory.Main structures`）でも、`push`・`peek`・`pop`の動きを確かめられます。

```
--- Stack ADT Implementation ---
Stack peek: Third
Stack pop: Third
Stack peek after pop: Second
```

なお、`ArrayStack`は容量に上限があり、満杯で`push`すると`IllegalStateException`になります。
公理2（`push`は必ず成功し、空でないスタックができる）は、上限のないスタックを想定しています。
上限のある実装は厳密には別の抽象データ型で、「満杯のときは`push`できない」という条件（事前条件）が付きます。
実際のライブラリが容量を自動で広げる実装を選ぶことが多いのは、この違いを利用者に意識させないためでもあります（容量を広げる仕組みと、その計算量は項目5の[05-algorithm-analysis.md](05-algorithm-analysis.md)で`DynamicArray`を例に扱います）。

### C言語での抽象データ型

C言語でも、ヘッダーファイルに構造体の名前だけを書く（不完全型にする）ことで、抽象データ型を作れます。

```c
/* stack.h：利用者に見せるのは型の名前と操作だけ */
typedef struct Stack Stack;   /* 中身を書かない（不完全型） */

Stack *stack_new(int capacity);
int    stack_push(Stack *s, int x);   /* 成功で1、満杯で0 */
int    stack_pop(Stack *s, int *out); /* 成功で1、空で0 */
int    stack_is_empty(const Stack *s);
void   stack_free(Stack *s);
```

```c
/* stack.c：構造体の中身はこのファイルの中だけで見える */
struct Stack {
    int *data;
    int top;       /* 次に積む位置 */
    int capacity;
};
/* 各関数の定義が続く */
```

`stack.h`だけをインクルードしたファイルで`s->top`と書くと、「incomplete definition of type」のようなコンパイルエラーになります（clangで確かめました。メッセージはコンパイラーによって違います）。
ただし、C言語の方法には次の限界があります。

- 要素の型ごとに別のスタックを書く必要がある（`void *`で汎用にすると、型の検査が効かなくなる）
- 構造体を`malloc`で確保するので、`stack_free`の呼び忘れがメモリリークになる
- 1つの型に対して実装を1つしか選べない（配列版と連結リスト版を、同じプログラムのなかで同じ名前の操作で使い分けられない）

Javaでは、ジェネリクス（第10章「ジェネリクス」）で要素の型を引数にでき、ガベージコレクションがメモリを回収し、インターフェイスで1つの操作の集まりに複数の実装を持たせられます。

## カプセル化と情報隠蔽

### 2つの言葉の違い

カプセル化（encapsulation）と情報隠蔽（information hiding）は、同じ意味で使われることも多い言葉ですが、もとの意味は少し違います。

- 情報隠蔽
    + パーナスが1972年に示した設計の原則（項目1）
    + あとで変わりそうな設計上の決定を、1つのモジュールのなかに隠す
- カプセル化
    + データと、そのデータを扱う操作を1つの単位（Javaではクラス）にまとめること
    + 言語の機能（`private`など）で、外からデータに直接触れないようにすることを含めて言うことが多い

カプセル化は、情報隠蔽を実現する主な手段です。
ただし、`private`を付けても、内部の配列をそのまま返すメソッドがあれば、中身は隠れていません。
言語の機能を使うことと、設計として隠せていることは別だと覚えておきましょう。

### 不変条件を守る

カプセル化の最大の効果は、オブジェクトがいつも正しい状態にあることを、そのクラスだけで保証できるようになることです。
「いつも成り立っているべき条件」を不変条件（invariant）と呼びます。
フィールドが公開されていると、どのクラスからでも不変条件を壊せるので、不変条件が成り立っているかを確かめるにはプログラム全体を調べなければなりません。
フィールドが`private`なら、そのクラスのメソッドだけを調べれば済みます。

`src/main/java/com/example/theory/structures/EncapsulationExamples.java`は、この違いを2つのクラスで示しています。

- `BadBankAccount`
    + 取引の配列`transactionHistory`と件数`transactionCount`が`public`
    + 外から件数を-1にでき、その後の`addTransaction`が失敗する
- `GoodBankAccount`
    + 取引のリストと残高が`private`で、入金は`deposit`メソッドだけで行う
    + `deposit`は0以下の金額を受け付けない
    + `getTransactionHistory`は変更できないコピー（`List.copyOf`）を返すので、受け取った側がリストに足しても残高と食い違わない

次のコマンドで実行します。

```bash
java -cp out com.example.theory.structures.EncapsulationExamples
```

```
--- BadBankAccount ---
Broken invariant: Index -1 out of bounds for length 3
--- GoodBankAccount ---
Rejected: Amount must be positive
History is read-only
Balance: 150.0, transactions: 2
```

`BadBankAccount`では、外から件数を書き換えた結果、配列の-1番目に書き込もうとして例外になっています。
誤りが表に出たのは書き換えた箇所ではなく、あとで`addTransaction`を呼んだ箇所です。
大きなプログラムでは、こうした「原因と症状が離れた誤り」の原因を探すのに時間がかかります。
`GoodBankAccount`では、不正な入金も、履歴の書き換えも、その場で拒否されています。

### 防御的コピーと不変オブジェクト

`GoodBankAccount`の`getTransactionHistory`のように、内部のコレクションを返すときにコピーを返す書き方を防御的コピーと呼びます。
`Transaction`のようにフィールドをすべて`final`にして、作ったあとは状態が変わらないクラスを不変（immutable）クラスと呼びます。
不変オブジェクトは、どこへ渡しても状態が変わらないので、不変条件を守るのがさらに簡単になります。
防御的コピーと不変オブジェクトは第6章「不変性とfinalキーワード」で詳しく学びます。

### Javaのアクセス修飾子

Javaには4段階の公開範囲があり、情報隠蔽の度合いを細かく決められます。

| 修飾子 | 見える範囲 |
|--------|------------|
| `private` | そのクラスのなかだけ |
| なし（パッケージプライベート） | 同じパッケージのなか |
| `protected` | 同じパッケージと、サブクラス |
| `public` | どこからでも |

Joshua Blochは『Effective Java』で、クラスとメンバーの公開範囲はできるだけ狭くすることを勧めています。
公開したものは、利用者のプログラムが頼るので、後から変えられなくなるからです。
`protected`は「サブクラスには見せる」という意味で、サブクラスを書く人にとっては公開と同じです。
`protected`のフィールドは、継承の節で見るように、親クラスの変更を難しくします。

## 継承と部分型

### 継承の2つの役割

Javaの`extends`には、性質の違う2つの役割があります。

- 実装の継承（コードの再利用）
    + 親クラスのフィールドとメソッドの実装を、子クラスが引き継ぐ
- 部分型（サブタイプ）の関係
    + 子クラスのインスタンスを、親クラスの型の変数に入れて使える
    + 第5章「継承とポリモーフィズム」で学ぶポリモーフィズムは、この関係にもとづく

インターフェイスの`implements`は、部分型の関係だけを作ります。
2つの役割のうち、コードを再利用したいだけなのに`extends`を使うと、次に説明する2つの問題が起きやすくなります。

### 問題1：継承はカプセル化を破る

1986年、Alan Snyderは論文で、継承がカプセル化と衝突することを指摘しました。
子クラスは親クラスのメソッドをオーバーライドしたり`protected`のフィールドに触れたりするので、親クラスの内部の作りに依存してしまいます。
親クラスの内部を変えると、親クラスの利用者には影響がなくても、子クラスが壊れることがあります。
この問題は「壊れやすい基底クラス（fragile base class）問題」とも呼ばれます。

『Effective Java』に載っている例を、Java 25で確かめてみます。
`HashSet`に追加された要素の数を数えるため、`HashSet`を継承して`add`と`addAll`をオーバーライドします。

```java
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InstrumentedSetDemo {
    // 継承で数える（誤り）
    static class InheritedCountingSet<E> extends HashSet<E> {
        private int addCount = 0;

        @Override
        public boolean add(E e) {
            addCount++;
            return super.add(e);
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            addCount += c.size();
            return super.addAll(c); // HashSetのaddAllは内部でaddを呼ぶ
        }

        int getAddCount() {
            return addCount;
        }
    }

    // コンポジションで数える
    static class ComposedCountingSet<E> {
        private final Set<E> set = new HashSet<>();
        private int addCount = 0;

        public boolean add(E e) {
            addCount++;
            return set.add(e);
        }

        public boolean addAll(Collection<? extends E> c) {
            addCount += c.size();
            return set.addAll(c); // setのaddAllが何を呼んでも、このクラスのaddは呼ばれない
        }

        int getAddCount() {
            return addCount;
        }
    }

    public static void main(String[] args) {
        InheritedCountingSet<String> s1 = new InheritedCountingSet<>();
        s1.addAll(List.of("a", "b", "c"));
        IO.println("inheritance: " + s1.getAddCount());

        ComposedCountingSet<String> s2 = new ComposedCountingSet<>();
        s2.addAll(List.of("a", "b", "c"));
        IO.println("composition: " + s2.getAddCount());
    }
}
```

```
inheritance: 6
composition: 3
```

3つの要素を追加したのに、継承版は6と数えています。
`HashSet`の`addAll`は、親の`AbstractCollection`から受け継いだ実装で、要素ごとに`add`を呼びます。
その`add`は子クラスでオーバーライドされているので、`addAll`で3、`add`で3、合わせて6回数えられたわけです。
`addAll`が`add`を呼ぶかどうかは`HashSet`の内部の作りで、`HashSet`の利用者が頼ってよい性質ではありません。
継承した子クラスは、その内部の作りに気づかないうちに依存していました。

コンポジション版は、`HashSet`を継承せず、フィールドとして持ち、処理を任せています（委譲）。
`set.addAll`が内部で何を呼んでも、それは`set`自身の`add`で、`ComposedCountingSet`の`add`ではありません。
このように、別のオブジェクトを部品として持って使うことをコンポジション（合成）と呼びます。
GoFの『Design Patterns』（1994年）は「クラスの継承よりオブジェクトのコンポジションを優先せよ」と書き、『Effective Java』も同じことを勧めています（項目4のDecoratorパターンは、この考え方の応用です）。

Javaの標準ライブラリにも、継承を誤って使った例があります。
`java.util.Stack`は`Vector`を継承しているため、スタックなのに`get(int)`や`add(int, E)`で途中の要素を読み書きできてしまいます。
`Stack`のAPIドキュメントは、代わりに`Deque`インターフェイス（`ArrayDeque`など）を使うよう勧めています。

### 問題2：部分型として振る舞えない

継承のもう1つの役割である部分型の関係にも、落とし穴があります。
Javaのコンパイラーは、子クラスが親クラスと同じ名前・引数のメソッドを持っているかは確かめますが、そのメソッドが親クラスと同じように振る舞うかは確かめません。

1987年、リスコフはOOPSLAの基調講演「Data Abstraction and Hierarchy」で、部分型であるための条件を次のような趣旨で述べました。

> 型Sのどのオブジェクトについても、型Tのあるオブジェクトがあって、Tを使って書かれたどのプログラムでも、後者を前者に置き換えてプログラムの振る舞いが変わらないなら、SはTの部分型である

これがリスコフの置換原則（Liskov Substitution Principle、LSP）と呼ばれる考え方です。
1994年には、リスコフとウィング（Jeannette Wing）が論文「A Behavioral Notion of Subtyping」で、この条件をメソッドの仕様の言葉で定式化しました。
要点は次のとおりです。

- 事前条件を強めてはならない
    + 親クラスのメソッドが受け付ける引数は、子クラスのメソッドも受け付ける
    + 例: 親が「0以上の数」を受け付けるのに、子が「1以上の数」しか受け付けないのは違反
- 事後条件を弱めてはならない
    + 親クラスのメソッドが保証する結果は、子クラスのメソッドも保証する
    + 例: 親が「並べ替えた結果を返す」と約束しているのに、子が並べ替えないことがあるのは違反
- 不変条件を保たなければならない
    + 親クラスの不変条件は、子クラスでも成り立つ
- 履歴の制約（history constraint）を守らなければならない
    + 親クラスで「一度決めたら変わらない」とされている値を、子クラスが変えられるようにしてはならない

事前条件・事後条件・不変条件はバートランド・メイヤー（Bertrand Meyer）の「契約による設計（Design by Contract）」の言葉で、メイヤーも子クラスのメソッドについて同じ規則（事前条件は弱めてよく、事後条件は強めてよい）を示しています（契約による設計は項目6の[06-formal-specification.md](06-formal-specification.md)で扱います）。

### 長方形と正方形

LSPの違反例として最もよく知られているのが、長方形と正方形の問題です。
数学では正方形は長方形の一種なので、`Square extends Rectangle`と書きたくなります。

`src/main/java/com/example/theory/structures/LiskovSubstitution.java`の`Rectangle`は、幅と高さを別々に変える`setWidth`と`setHeight`を持ちます。
`Square`は正方形であり続けるために、`setWidth`で高さも、`setHeight`で幅も変えます。
`Rectangle`の利用者は「`setWidth`しても高さは変わらない」と考えてプログラムを書きます。
これは`setWidth`の事後条件で、`Square`はそれを弱めています。

`testLSPViolation`は、`Rectangle`型の変数に`Square`を入れ、幅5・高さ4にして面積が20になることを`assert`文で確かめます。
`Main`の`structures`のデモで実行すると、次の行が表示されます（`-ea`が必要です）。

```
--- Liskov Substitution Principle ---
LSP violation detected: LSP violation: Expected 20 but got 16
LSP compliance test passed!
```

`setHeight(4)`が幅も4にしたため、面積は16になりました。
`Square`はコンパイラーから見れば`Rectangle`の部分型ですが、振る舞いのうえでは部分型ではありません。

同じファイルの`ImmutableRectangle`と`ImmutableSquare`は、この問題を避ける設計です。
どちらも`Shape`インターフェイスを実装し、状態を変えるメソッドを持ちません。
大きさを変える`resize`は、新しいオブジェクトを返します。
「幅だけを変える」という操作がなければ、それを破ることもできません。
2行目の「LSP compliance test passed!」は、こちらの設計で`assert`文がすべて成り立ったことを示しています。

この例からわかるのは、「is-a（〜は〜の一種である）」という日常の言葉の関係だけで継承を決めてはいけない、ということです。
決め手は、親クラスの利用者が頼っている振る舞いを、子クラスがすべて守れるかどうかです。

## よくある誤解

- 「フィールドを`private`にしてgetterとsetterを付ければカプセル化できている」
    + すべてのフィールドに無条件のsetterがあれば、`public`のフィールドとほとんど変わらない。カプセル化の目的は不変条件を守ることなので、値を検査しないsetterや、状態をばらばらに変えられるsetterは見直す
- 「抽象データ型はインターフェイスのことである」
    + インターフェイスに書けるのはメソッドの名前と型だけで、公理のような振る舞いの約束は書けない。振る舞いはコメントやAPIドキュメント、テストで補う。`java.util.List`のAPIドキュメントが各メソッドの振る舞いを詳しく書いているのはそのため
- 「LSPは継承のときだけの話である」
    + インターフェイスを実装するクラスにも当てはまる。`List`を実装しているのに`add`が順番を守らないクラスは、`List`を使うプログラムを壊す
- 「継承は使うべきではない」
    + 継承そのものが悪いわけではない。親クラスが継承されることを前提に設計・文書化されていて、子クラスが部分型として振る舞えるなら、継承は自然な道具になる。そうでないクラスは`final`にして継承を禁じるのも1つの方法（第6章「不変性とfinalキーワード」）

## 実務での使いどころ

- 新しいクラスを作るときは、まずその型の利用者が使う操作と、操作が満たす性質を決める。中身（フィールド）はあとで決める
- 「いつも成り立つべき条件」をクラスのコメントに書き、それを壊せるメソッドがないかを確かめる
- コードを再利用したいだけなら、継承ではなくコンポジションと委譲を選ぶ
- 継承を使うときは、「親クラスの型で書かれたテストを、子クラスのインスタンスで実行しても通るか」で確かめる。通らなければLSPに違反している

## 本文・付録との関係

- 第3章「オブジェクト指向の考え方」
    + クラス、`private`、getter/setterの基本
- 第4章「カプセル化とクラス設計」
    + アクセス修飾子の全体、パッケージ、クラスの責任の分け方
- 第5章「継承とポリモーフィズム」
    + 継承、オーバーライド、ポリモーフィズム。リスコフ置換原則と長方形・正方形の問題も扱う
- 第6章「不変性とfinalキーワード」
    + 不変オブジェクト、防御的コピー、`final`
- 第7章「抽象クラスとインターフェイス」
    + 契約としてのインターフェイス
- 第10章「ジェネリクス」
    + 要素の型を引数にした型の定義
- 付録B.03「ソフトウェア設計原則」
    + SOLID原則（LSPを含む）の実装例
- 付録B.04「仮想メソッドテーブル」
    + オーバーライドしたメソッドがどう呼び分けられるか
- 付録B.05「不変性の設計パターン」
    + 不変オブジェクトの設計
- この付録の項目6（[06-formal-specification.md](06-formal-specification.md)）
    + 事前条件・事後条件・不変条件の詳しい扱い

## 参考文献

- B. Liskov, S. Zilles, "Programming with Abstract Data Types", ACM SIGPLAN Notices, 9(4), 1974
- J. V. Guttag, "Abstract Data Types and the Development of Data Structures", Communications of the ACM, 20(6), 1977
- D. L. Parnas, "On the Criteria To Be Used in Decomposing Systems into Modules", Communications of the ACM, 15(12), 1972
- A. Snyder, "Encapsulation and Inheritance in Object-Oriented Programming Languages", Proceedings of OOPSLA '86, 1986
- B. Liskov, "Data Abstraction and Hierarchy", Addendum to the Proceedings of OOPSLA '87, ACM SIGPLAN Notices, 23(5), 1988
- B. Liskov, J. M. Wing, "A Behavioral Notion of Subtyping", ACM Transactions on Programming Languages and Systems, 16(6), 1994
- B. Meyer, "Object-Oriented Software Construction", 2nd ed., Prentice Hall, 1997
- E. Gamma, R. Helm, R. Johnson, J. Vlissides, "Design Patterns: Elements of Reusable Object-Oriented Software", Addison-Wesley, 1994
- J. Bloch, "Effective Java", 3rd ed., Addison-Wesley, 2018（項目15「クラスとメンバーへのアクセス可能性を最小限にする」、項目18「継承よりコンポジションを選ぶ」）
- Java SE 25 APIドキュメント `java.util.Stack`（`https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/Stack.html`）
