# 6. 形式手法と仕様記述

## この項目で学ぶこと

メソッドが「正しい」とは、何に対して正しいのでしょうか。
この項目では、プログラムが満たすべき性質を条件として書き表し、その条件をもとにプログラムの正しさを考える方法を学びます。

- 仕様と、事前条件・事後条件・不変条件
- Hoare論理（ホーア論理）による正しさの証明の考え方
- ループ不変条件と、ループが止まることの示し方
- 契約による設計（Design by Contract）と、Javaでの実践
- `assert`文の使い方と限界
- JMLなどの仕様記述言語と形式手法の紹介

## なぜ必要か

### テストだけでは正しさを示せない

第21章で学ぶユニットテストは、選んだ入力に対してプログラムが期待どおりに動くことを確かめます。
しかし、`int`を2つ受け取るメソッドの入力は2⁶⁴通りあり、すべてを試すことはできません。
テストに通ったことは「試した入力では正しかった」ことを示すだけです。
「テストは誤りがあることを示せても、誤りがないことは示せない」という趣旨の言葉は、Edsger Dijkstraの言葉として知られています。

そこで、プログラムが満たすべき性質を論理式で書き、プログラムの文の意味から、その性質が「どんな入力でも」成り立つことを推論する方法が研究されてきました。
これが形式手法（formal methods）の出発点です。

### 歴史的な背景

- 1967年
    + Robert Floydが論文「Assigning Meanings to Programs」で、流れ図の各所に論理式を添えてプログラムの正しさを示す方法を発表した
- 1969年
    + C. A. R. Hoareが論文「An Axiomatic Basis for Computer Programming」で、文ごとの推論規則としてこれを整理した。今日Hoare論理と呼ばれる体系である
- 1975〜1976年
    + Dijkstraが最弱事前条件（weakest precondition）を導入し、仕様からプログラムを導く方法を示した（著書『A Discipline of Programming』、1976年）
- 1980年代後半
    + Bertrand Meyerが、事前条件・事後条件・不変条件をクラスの設計に組み込む「契約による設計」を提唱し、Eiffel言語で言語の機能として取り入れた

証明そのものは手間がかかるので、すべてのプログラムを証明する現場はまれです。
それでも、事前条件・事後条件・不変条件という考え方は、ドキュメントの書き方、例外の使い分け、テストの設計など、日々のプログラミングの土台になっています。

## 仕様と3種類の条件

### 仕様とは

仕様（specification）は、プログラムが「何をするか」を、「どうやるか」から切り離して書いたものです。
たとえば「整数の配列の最大値を返す」は仕様で、「先頭から順に比べる」はその実現方法です。
同じ仕様を満たす実装は何通りもありえます。

C言語の標準ライブラリの説明を思い出してください。
`strcpy(dest, src)`は、`dest`に`src`の文字列を写します。
ただし、`dest`が十分な大きさを持つこと、2つの領域が重ならないことは呼び出す側の責任で、守られなかったときの動作は未定義です。
このように、仕様には「呼び出す側が守ること」と「呼ばれた側が約束すること」の両方が含まれます。

### 事前条件・事後条件・不変条件

- 事前条件（precondition）
    + メソッドを呼び出す時点で成り立っていなければならない条件。呼び出す側が守る
    + 例: 二分探索では「配列が昇順に並んでいる」
- 事後条件（postcondition）
    + 事前条件が成り立つ状態で呼び出されたとき、メソッドが終わった時点で成り立つことを約束する条件
    + 例: 二分探索では「戻り値が0以上なら、その位置に目的の値がある。-1なら、配列のどこにも目的の値はない」
- 不変条件（invariant）
    + つねに成り立つ条件。クラスについて言うときは、オブジェクトを外から観察できるすべての時点（コンストラクターの終わりと、各publicメソッドのはじめと終わり）で成り立つ条件を指す
    + 例: 銀行口座では「残高は0以上」

`src/main/java/com/example/theory/algorithms/BinarySearchWithSpecs.java`の`binarySearch`は、Javadocのコメントにこの3つを書いています。

```java
/**
 * ...
 * 事前条件: arr != null && isSorted(arr)
 * 事後条件: 
 *   result >= 0 => arr[result] == target
 *   result == -1 => ∀i. arr[i] != target
 */
public int binarySearch(int[] arr, int target) {
```

「∀i.」は「すべてのiについて」、「=>」は「ならば」を表す論理の記号です。
仕様を論理式で書くと、日本語の説明にありがちなあいまいさ（「見つからなければ-1」の「見つからない」は、存在しないのか、探し損ねたのか）がなくなります。

## Hoare論理

### Hoareトリプル

Hoare論理では、プログラムの断片Sの意味を、次の形の式で表します。

```
{P} S {Q}
```

「事前条件Pが成り立つ状態でSを実行し、Sが終了したなら、その後で事後条件Qが成り立つ」という意味です。
この形をHoareトリプル（Hoare triple、ホーアの3つ組とも訳される）と呼びます。

たとえば、次のHoareトリプルは正しい主張です（xは数学の整数とします）。

```
{x = 5} x = x + 1 {x = 6}
{x > 0} x = x * 2 {x > 1}
```

2つ目は、事後条件が「x > 1」であって「x = 2倍の値」ではありません。
正しいHoareトリプルは1つではなく、必要な性質が言えれば十分です。

Hoareトリプルの定義には「Sが終了したなら」という条件が付いています。
この意味での正しさを部分正当性（partial correctness）と呼び、さらにSが必ず終了することまで含めた正しさを完全正当性（total correctness）と呼びます。
Hoareの1969年の論文が扱ったのは部分正当性で、終了することは別に示す必要があります。

### 推論規則

Hoare論理は、文の種類ごとの推論規則からなります。
表の各行は、「から」の前の式がすべて成り立てば、「から」の後ろの式も成り立つという意味です。

| 規則 | 内容 |
|------|------|
| 代入 | {Q[E/x]} x = E {Q}。Q[E/x]は、QのなかのxをEで置き換えた式 |
| 順次 | {P} S1 {R} と {R} S2 {Q} から、{P} S1; S2 {Q} |
| 条件分岐 | {P ∧ B} S1 {Q} と {P ∧ ¬B} S2 {Q} から、{P} if (B) S1 else S2 {Q} |
| 反復 | {I ∧ B} S {I} から、{I} while (B) S {I ∧ ¬B} |
| 帰結 | P ⇒ P'、{P'} S {Q'}、Q' ⇒ Q から、{P} S {Q} |

代入の規則は、事後条件から逆向きに考えるのがこつです。
「代入の後でQが成り立ってほしいなら、代入の前には、xをEに置き換えたQが成り立っていればよい」という意味です。
たとえば、x = x + 1の後でx = 6にしたいなら、前にはx + 1 = 6（つまりx = 5）が成り立っていればよいと求まります。

### 例：2つの変数の値の交換

C言語でも書いたことのある、一時変数を使った交換を証明してみます。

```
{x = a ∧ y = b}
t = x;
x = y;
y = t;
{x = b ∧ y = a}
```

事後条件から1文ずつ逆にたどります。

1. `y = t`の後で x = b ∧ y = a にしたい
    + 代入の規則で y を t に置き換え、前には x = b ∧ t = a が要る
2. `x = y`の後で x = b ∧ t = a にしたい
    + x を y に置き換え、前には y = b ∧ t = a が要る
3. `t = x`の後で y = b ∧ t = a にしたい
    + t を x に置き換え、前には y = b ∧ x = a が要る

最後に得た条件は、最初の事前条件 x = a ∧ y = b と同じです。
順次の規則で3つをつなぐと、交換のプログラムが正しいことが示せます。

### 最弱事前条件

事後条件Qと文Sから「Sの後でQを成り立たせる、もっとも弱い（ゆるい）事前条件」を求める関数を、Dijkstraは最弱事前条件と呼んでwp(S, Q)と書きました。
上の例では wp(x = x + 1, x = 6) は x = 5 です。

この考え方は、Javaの`int`のような有限の整数では注意が要ります。
数学の整数なら wp(x = x + 1, x > 0) は x ≥ 0 ですが、Javaの`int`では x が`Integer.MAX_VALUE`のとき x + 1 は負の数になります（オーバーフロー）。
正しくは「x ≥ 0 かつ x ≠ Integer.MAX_VALUE」です。
形式手法の道具は、こうした言語ごとの意味を正確に扱うことで、人の見落としを見つけます。

## ループ不変条件

### 反復の規則の読み方

ループの正しさを示すときに中心になるのが、反復の規則に出てくる I、ループ不変条件（loop invariant）です。
ループ不変条件は、ループの条件を調べる直前に毎回成り立つ条件です。
ループ不変条件を使った議論は、次の3段階で行います。

1. 初期化
    + ループに入る前に、I が成り立つ
2. 維持
    + I とループの条件 B が成り立つ状態で本体を1回実行すると、再び I が成り立つ
3. 終了
    + ループが終わったとき（I が成り立ち、B が成り立たないとき）、そこから目的の事後条件が導ける

これは数学的帰納法と同じ形をしています。
初期化が「n = 0 のとき」、維持が「n のとき成り立てば n + 1 でも成り立つ」に当たります。

### 例：配列の最大値

`src/main/java/com/example/theory/algorithms/StructuredProgramming.java`の`findMax`を証明してみます。

```java
int max = array[0];
int i = 1;

// ループ不変条件: max は array[0..i-1] の最大値
while (i < array.length) {
    if (array[i] > max) {
        max = array[i];
    }
    i++;
    // 不変条件は保持される
}

return max; // max は array[0..length-1] の最大値
```

nを`array.length`とし、事前条件を n ≥ 1 とします（空の配列なら例外を投げるので、ループの手前では n ≥ 1 です）。
ループ不変条件 I を「1 ≤ i ≤ n、かつ max は array[0..i-1] の最大値」とします。

1. 初期化
    + max = array[0]、i = 1 なので、array[0..0] の最大値は array[0] で、I が成り立つ
2. 維持
    + 本体の前で max は array[0..i-1] の最大値である。if文の後、max は array[0..i-1] と array[i] の大きいほう、つまり array[0..i] の最大値になる。i++ の後、これは「array[0..i-1] の最大値」となり、I が再び成り立つ。i < n だったので、i++ の後も i ≤ n である
3. 終了
    + ループを抜けたとき i < n が成り立たないので i ≥ n、I から i ≤ n なので i = n である。I に代入すると、max は array[0..n-1] の最大値で、目的の事後条件になる

### ループが止まることを示す

部分正当性に加えて、ループが必ず止まることを示すには、変動関数（variant）を使います。
変動関数は、0以上の整数値をとり、本体を1回実行するたびに必ず減る式です。
0以上の整数が限りなく減り続けることはできないので、ループはいつか止まります。

`findMax`では n - i が変動関数です。
i は毎回1増えるので n - i は毎回1減り、ループの条件 i < n が成り立つ間は n - i > 0 です。

`BinarySearchWithSpecs`の`binarySearch`では、ループ不変条件は「target が配列にあるなら、arr[left..right] の範囲にある」で、変動関数は範囲の大きさ right - left + 1 です。
`left = mid + 1`も`right = mid - 1`も、mid を範囲から外すので、範囲は毎回少なくとも1つ小さくなります。
ここを`left = mid`と書く誤りはよくあり、その場合 left と right が隣り合ったところで範囲が縮まず、ループが止まらなくなります。
変動関数が減ることを確かめる習慣があれば、この誤りに気づけます。

### ループ不変条件をコードで検査する

`StructuredProgramming.java`の`findMaxWithInvariantCheck`は、ループ不変条件を`assert`文で毎回検査します。

```java
while (i < array.length) {
    // ループ不変条件の事前チェック
    assert isMaxOfRange(array, 0, i - 1, max) : "Invariant violated before iteration";

    if (array[i] > max) {
        max = array[i];
    }
    i++;

    // ループ不変条件の事後チェック
    assert isMaxOfRange(array, 0, i - 1, max) : "Invariant violated after iteration";
}
```

`isMaxOfRange`は、「max が範囲の最大値である」を「範囲のどの要素も max 以下」と「max が範囲のなかに実際にある」の2つで調べます。
前者だけを調べると、範囲のどの値よりも大きいだけの値（たとえば`Integer.MAX_VALUE`）でも検査に通ってしまいます。
条件を検査のコードに直すときは、論理式のすべての部分を写したかを確かめてください。

## 契約による設計

### 契約のたとえ

Bertrand Meyerは、メソッドを呼び出す側（クライアント）と呼ばれる側（サプライヤー）の関係を、契約にたとえました。

| | 義務 | 権利（受け取る利益） |
|------|------|------|
| クライアント | 事前条件を満たして呼び出す | 事後条件が成り立つ結果を受け取れる |
| サプライヤー | 事後条件を満たして返す | 事前条件が成り立つことを前提にできる |

この考え方の要点は、責任の所在をはっきりさせることです。
事前条件が破られたらクライアントの誤り、事前条件が守られたのに事後条件が成り立たなければサプライヤーの誤りです。
Meyerは、サプライヤーが事前条件を自分で調べ直すと、同じ検査が呼び出しの両側に散らばって複雑になるとして、検査の責任を一方に決めることを勧めました。

### クラス不変条件

クラス不変条件は、すべてのpublicメソッドの事前条件と事後条件に暗黙に加わる条件です。
コンストラクターが不変条件を成り立たせ、各publicメソッドが不変条件を保てば、外から見たオブジェクトはつねに不変条件を満たします。
メソッドの途中では一時的に崩れてもかまいません。

`src/main/java/com/example/theory/specifications/BankAccountWithInvariants.java`のクラス不変条件は次の2つです。

1. 残高`balance`は0以上である
2. `balance`は、取引の履歴`transactions`の金額の合計に等しい

`deposit`は、事前条件（金額が正）を調べ、呼び出し前の残高を覚えておき、処理の後で事後条件と不変条件を`assert`文で確かめます。

```java
public void deposit(double amount) {
    // 事前条件
    if (amount <= 0) {
        throw new IllegalArgumentException("Amount must be positive");
    }
    
    double oldBalance = balance; // 事後条件で「呼び出し前の値」を参照するために覚えておく
    
    // 操作
    transactions.add(new Transaction("DEPOSIT", amount));
    balance += amount;
    
    // 事後条件: 残高がamountだけ増えている
    assert Math.abs(balance - (oldBalance + amount)) < 0.001 : "Postcondition violated after deposit";
    // 不変条件の維持
    assert checkInvariant() : "Invariant violated after deposit";
}
```

`transactions.add`の直後、`balance`を更新する前は、不変条件2が一時的に崩れています。
メソッドの外からはこの状態が見えないので、問題ありません（ただし、複数のスレッドから同時に呼ぶと見えてしまいます。項目7で扱います）。

事後条件の「呼び出し前の残高」のように、事後条件はしばしば呼び出し前の値を参照します。
Eiffelには`old`、あとで紹介するJMLには`\old`という記法がありますが、Javaの`assert`文では値を自分で変数に覚えておく必要があります。

このクラスは例を短くするために金額を`double`で表していますが、`double`は0.1のような10進の小数を正確に表せません。
そのため、合計が等しいかを誤差0.001以内で比べています。
実務では`BigDecimal`か、最小単位（円やセント）の整数で金額を表してください。

実行すると、正常な操作の結果と、事前条件を破った呼び出しが例外で拒否される様子が表示されます。

```bash
java -ea -cp out com.example.theory.Main specifications
```

```
--- Class Invariants Demo ---
=== Demonstrating Class Invariants ===
Initial balance: $1000.0
After deposit $500: $1500.0
After withdrawal $200: $1300.0

Transaction history:
INITIAL: 1000.00
DEPOSIT: 500.00
WITHDRAW: -200.00

=== Testing precondition violations ===
Caught expected exception: Amount must be positive
Caught expected exception: Insufficient balance
```

### Javaの実践：publicメソッドは引数を検査して例外を投げる

Meyerの考え方では、事前条件を守るのはクライアントの責任で、サプライヤーは調べなくてもかまいません。
一方、Javaの標準ライブラリは、publicメソッドの引数を調べ、守られていなければ例外を投げる方針をとっています。
不特定の利用者が呼ぶメソッドでは、誤った呼び出しをその場で知らせるほうが、壊れた状態のまま処理が進むよりも安全だからです。
これは契約の考え方と矛盾しません。
「この条件が守られなければ`IllegalArgumentException`を投げる」こと自体を、事後条件の一部としてJavadocの`@throws`に書き、契約に含めているからです。

よく使う例外と、検査のためのメソッドは次のとおりです。

- `IllegalArgumentException`
    + 引数の値が事前条件を満たさない
- `NullPointerException`
    + `null`を受け付けない引数に`null`が渡された。`Objects.requireNonNull`で検査できる
- `IndexOutOfBoundsException`
    + 添え字が範囲外である。`Objects.checkIndex`、`Objects.checkFromIndexSize`で検査できる
- `IllegalStateException`
    + 引数ではなく、オブジェクトの状態がその操作を許さない（閉じたファイルへの書き込みなど）

```java
/**
 * 配列のindex番目からcount個の合計を返す
 * 事前条件: values != null、0 <= index、0 <= count、index + count <= values.length
 */
static int sum(int[] values, int index, int count) {
    Objects.requireNonNull(values, "values");
    Objects.checkFromIndexSize(index, count, values.length);
    int total = 0;
    for (int i = index; i < index + count; i++) {
        total += values[i];
    }
    return total;
}
```

`sum(data, 1, 3)`（`data`は`{1, 2, 3, 4, 5}`）は9を返し、`sum(data, 3, 5)`は次のメッセージの`IndexOutOfBoundsException`を投げます。

```
Range [3, 3 + 5) out of bounds for length 5
```

`BankAccountWithInvariants`の`withdraw`が残高不足で投げる`InsufficientFundsException`は、引数の誤りというより、「残高が足りなければこの例外を投げる」という仕様の一部と見るのが自然です。
呼び出す側が前もって残高を調べられないなら、それは事前条件にせず、例外を返す結果の1つとして仕様に書きます。
例外の種類の使い分けは第13章で学びます。

不変条件を守る道具として、Javaには次の方法もあります。

- フィールドを`private`にして、変更をメソッドに限る（第3章・第4章）
- フィールドを`final`にして変更そのものをなくす（第6章）
- レコードのコンパクトコンストラクターで、生成の時点で不変条件を確かめる（第8章）

```java
// 不変条件「low <= high」を、生成の時点で確かめるレコード
public record Range(int low, int high) {
    public Range {
        if (low > high) {
            throw new IllegalArgumentException("low > high: " + low + " > " + high);
        }
    }

    // 事後条件: 戻り値は0以上（不変条件 low <= high から導けるのは、high - low がintに収まる場合だけ）
    public int length() {
        return high - low;
    }
}
```

`new Range(3, 10)`は`Range[low=3, high=10]`になり`length()`は7を返します。
`new Range(10, 3)`は「low > high: 10 > 3」というメッセージの例外で拒否されます。
レコードのフィールドは変更できないので、生成の時点で確かめれば、不変条件はずっと成り立ちます。
ただし、`length()`の事後条件は、`int`のオーバーフローまでは考えていません。
`new Range(Integer.MIN_VALUE, Integer.MAX_VALUE)`の`length()`は-1を返します。
最弱事前条件の節で見たように、有限の整数では、数学の整数で成り立つ推論がそのままでは成り立たないことがあります。

### 継承と契約

サブクラスがメソッドをオーバーライドするとき、契約には次の制約があります（Meyerはこれを契約の再定義の規則として示しました）。

- 事前条件は、同じか、より弱く（ゆるく）してよいが、強くしてはいけない
- 事後条件は、同じか、より強くしてよいが、弱くしてはいけない
- クラス不変条件は、スーパークラスのものを保たなければならない

スーパークラスの型で変数を宣言したクライアントは、スーパークラスの契約しか知りません。
サブクラスが事前条件を強めると、クライアントが契約どおりに呼んだのに拒否されることになります。
これは項目2で扱うリスコフの置換原則（Liskov & Wing、1994年）と同じことを、契約の言葉で述べたものです。

## assert文の使い方と限界

### 書き方と有効化

`assert`文はJava 1.4で加わった文で、次の2つの形があります。

```java
assert 条件;
assert 条件 : 詳細メッセージ;
```

条件が`false`なら`AssertionError`が投げられます。
`assert`文は既定では無効で、`java`コマンドに`-ea`（`-enableassertions`）を付けたときだけ実行されます。
無効のときは条件の式そのものが評価されません。
`-ea:com.example.theory...`のようにパッケージを指定して一部だけ有効にすることもできます。

C言語の`assert`マクロ（`<assert.h>`）は、`NDEBUG`を定義してコンパイルすると消えます。
Javaの`assert`はコンパイル時ではなく実行時の指定で切り替える点が違いますが、「開発中の検査に使い、本番では外すことがある」という性格は同じです。

### Oracleの指針

Oracleのドキュメント「Programming With Assertions」は、`assert`文の使い方について次の指針を示しています。

- 使ってよい場面
    + 内部の不変条件（「ここに来るときは x は正のはず」）
    + 制御の流れの不変条件（`switch`の`default`など、到達しないはずの場所）
    + publicでないメソッドの事前条件、メソッドの事後条件、クラス不変条件
- 使ってはいけない場面
    + publicメソッドの引数の検査。`assert`文は無効にできるので、検査が行われない場合がある。引数の検査は、上で述べたように例外で行う
    + プログラムの動作に必要な処理を`assert`文のなかに書くこと。無効のときはその処理ごと実行されなくなる

`BinarySearchWithSpecs`の`binarySearch`は、`arr`が`null`でないことは例外で、ソート済みであることは`assert`文で調べています。
`null`の検査はO(1)で済みますが、ソート済みの検査はO(n)かかり、O(log n)の探索がO(n)になってしまうためです。
このように、検査に時間がかかる条件を開発中だけ調べたい場面が、`assert`文の向いている場面です。

### 実行して確かめる

`src/main/java/com/example/theory/specifications/AssertionDemo.java`は、`-ea`の有無で何が変わるかを確かめるプログラムです。

- ループ不変条件（`findMaxWithInvariantCheck`）と事後条件（`binarySearchWithPostConditionCheck`）を検査しながら実行する
- 事前条件（ソート済み）を破って、並んでいない配列`{5, 1, 4, 2, 3}`から1を探す
- 悪い例として、`assert tasks.remove("compile");`のように、必要な処理を`assert`文のなかに書く

`-ea`を付けて実行した結果です。

```bash
java -ea -cp out com.example.theory.specifications.AssertionDemo
```

```
Assertions enabled: true
findMaxWithInvariantCheck: 9
Search 7 with postcondition check: 3
Search 8 with postcondition check: -1
AssertionError: Array must be sorted
Tasks after assert with side effect: [test]
```

`-ea`を付けずに実行した結果です。

```bash
java -cp out com.example.theory.specifications.AssertionDemo
```

```
Assertions enabled: false
findMaxWithInvariantCheck: 9
Search 7 with postcondition check: 3
Search 8 with postcondition check: -1
Search 1 in unsorted array: -1
Tasks after assert with side effect: [compile, test]
```

読み取れることは次のとおりです。

- 事前条件を破った呼び出しは、`-ea`のときは`AssertionError`で止まる。`-ea`なしでは検査されず、配列に1があるのに-1という誤った答えが返る
- 必要な処理を`assert`文に書くと、`-ea`の有無でプログラムの動作が変わる（`"compile"`が削除されるかどうか）
- 正しい呼び出しの結果は、`-ea`の有無で変わらない

### assert文の限界

- 実行した入力しか調べられない
    + `assert`文は実行時の検査で、テストと同じく、通ったことは「その実行では条件が成り立った」ことしか示さない。Hoare論理の証明が「すべての入力で」成り立つことを示すのとは違う
- 既定で無効である
    + 本番で`-ea`を付けないなら、本番で起きた違反は見つからない
- 書ける条件に限りがある
    + 「すべてのiについて」のような条件は、ループを書くメソッドに直す必要がある（`isSorted`、`contains`）。呼び出し前の値も自分で覚えておく必要がある
- 検査そのものが重いことがある
    + `BankAccountWithInvariants`の`checkInvariant`は、取引の履歴をすべて合計するので、操作のたびにO(n)かかる。`-ea`で長い間動かすと、目に見えて遅くなる

ユニットテストと`assert`文は役割が違います。
テストは「この入力にはこの結果」という外からの確認で、`assert`文はプログラムの内部の想定を書き残す道具です。
`assert`文を書いておくと、テストを実行したときに内部の想定の誤りにも気づけます。

## JMLとその他の形式手法

### JML

JML（Java Modeling Language）は、Javaのための仕様記述言語で、Gary Leavensらが中心になって開発してきました。
特別な形のコメント（`//@`や`/*@ ... @*/`）のなかに、事前条件（`requires`）、事後条件（`ensures`）、不変条件（`invariant`）を書きます。
コメントなので、普通の`javac`はそのまま無視してコンパイルします。

```java
public class Account {
    private /*@ spec_public @*/ long balance;

    //@ public invariant balance >= 0;

    //@ requires amount > 0;
    //@ ensures balance == \old(balance) + amount;
    public void deposit(long amount) {
        balance += amount;
    }
}
```

`\old(balance)`は呼び出し前の`balance`の値、`\result`は戻り値を表します。
`spec_public`は、privateのフィールドを仕様のなかで参照できるようにする指定です。
OpenJMLなどのツールは、この仕様を実行時に検査するコードを生成したり、仕様を満たすことを自動証明器で確かめたりします。
JMLを使うJavaの検証ツールには、ほかにKeYがあります。

この例の`deposit`は、`balance + amount`が`long`の範囲を超える場合に事後条件と不変条件を破ります。
自動証明器で検査すると、こうしたオーバーフローの可能性が指摘されます（この例は書き方を示すためのもので、本リポジトリではJMLのツールでの検査はしていません）。

### その他の手法

- モデルベースの仕様記述
    + Z記法、VDM、Bメソッドなどは、集合と論理でシステムの状態と操作を記述する
- TLA+
    + Leslie Lamportが考案した、並行システムや分散システムの仕様記述言語。モデル検査器TLCで、ありうる状態をしらみつぶしに調べる。Amazon Web Servicesの技術者が、分散システムの設計にTLA+を使った経験を2015年の論文「How Amazon Web Services Uses Formal Methods」で報告している
- Alloy
    + Daniel Jacksonが開発した軽量の仕様記述言語。小さな範囲の反例を自動で探す
- モデル検査
    + 状態の数が有限になるようにシステムを抽象化し、すべての状態を機械的に調べて性質を確かめる。項目7で扱うデッドロックの検出にも使われる

形式手法は、誤りが重大な損害につながる分野（鉄道の信号、航空機、半導体の設計、暗号の通信手順など）を中心に使われています。
一般の業務システムで証明まで行うことはまれですが、仕様を論理式で書いてみるだけでも、日本語の仕様のあいまいさや考え漏れに気づけます。

## よくある誤解・注意点

- 不変条件は「変わらない値」ではない
    + 残高は変わるが「残高は0以上」という条件は変わらない。不変なのは値ではなく条件である
- 事前条件は「入力の検査」と同じではない
    + 事前条件は契約の一部で、誰が守るかを決めるものである。検査するかどうか、どう知らせるかは別に決める（publicメソッドなら例外、内部なら`assert`文など）
- `assert`文をエラー処理に使う
    + ファイルが開けない、ネットワークが切れたなど、正しいプログラムでも起こりうる事態は`assert`文ではなく例外で扱う。`assert`文は「プログラムが正しければ起きないこと」に使う
- 仕様は書けば正しいわけではない
    + 仕様そのものが要求とずれていることがある。形式手法が示すのは「プログラムが仕様を満たす」ことで、「仕様が要求を満たす」ことではない
- 検査のコードが条件の一部しか写していない
    + `isMaxOfRange`の例のように、論理式の一部を落とした検査は、誤りを見逃す

## 実務での使いどころ

- Javadocに、事前条件を`@param`と`@throws`で、事後条件を`@return`で書く
- publicメソッドの入口で引数を検査し、`Objects.requireNonNull`などで早く失敗させる
- 不変条件のあるクラスは、不変条件を確かめるprivateメソッドを用意し、テストや`assert`文から呼ぶ
- ループを書いたら、ループ不変条件と変動関数を頭のなか（あるいはコメント）で確かめる。二分探索やソートのような境界の誤りが起きやすい処理ではとくに有効である
- ユニットテストのケースを、事前条件の境界（空の配列、要素1つ、範囲の端）から選ぶ

## 本文・付録との関連

- 第4章「カプセル化とクラス設計」
    + 不変条件の考え方と、カプセル化で不変条件を守る設計
- 第6章「不変性とfinalキーワード」
    + 変更できないオブジェクトは、生成の時点で不変条件を確かめればよい
- 第7章「抽象クラスとインターフェイス」
    + 契約としてのインターフェイス
- 第8章「Javaの特殊なクラス形式」
    + レコードのコンパクトコンストラクターによる値の検査
- 第13章「例外処理」
    + 事前条件が破られたときに投げる例外の選び方
- 第21章「ユニットテストと品質保証」
    + テストで確かめられることと、`assert`文との違い
- 付録B.03「ソフトウェア設計原則」
    + リスコフの置換原則と、継承での契約の制約
- 付録B.15「テスト戦略」
    + 事前条件・事後条件を意識したテストの設計

## 参考文献

- C. A. R. Hoare「An Axiomatic Basis for Computer Programming」『Communications of the ACM』12巻10号、1969年
- Robert W. Floyd「Assigning Meanings to Programs」『Proceedings of Symposia in Applied Mathematics』19巻、American Mathematical Society、1967年
- Edsger W. Dijkstra『A Discipline of Programming』Prentice-Hall、1976年
- Bertrand Meyer「Applying 'Design by Contract'」『IEEE Computer』25巻10号、1992年
- Bertrand Meyer『Object-Oriented Software Construction』第2版、Prentice Hall、1997年（邦訳『オブジェクト指向入門』翔泳社）
    + 契約による設計と、継承での契約の規則
- Barbara Liskov・Jeannette Wing「A Behavioral Notion of Subtyping」『ACM Transactions on Programming Languages and Systems』16巻6号、1994年
- Thomas H. Cormen ほか『Introduction to Algorithms』第4版、MIT Press、2022年
    + ループ不変条件の初期化・維持・終了による正しさの示し方
- Chris Newcombe ほか「How Amazon Web Services Uses Formal Methods」『Communications of the ACM』58巻4号、2015年
- Oracle「Programming With Assertions」
    + `https://docs.oracle.com/javase/8/docs/technotes/guides/language/assert.html`
- OpenJML（JMLで書いた仕様を検査するツール）の公式サイト
    + `https://www.openjml.org/`
