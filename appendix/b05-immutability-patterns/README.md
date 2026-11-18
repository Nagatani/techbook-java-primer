# 付録B.05 不変性の設計パターンと実装テクニック

第6章で学んだ不変オブジェクト・`final`・防御的コピーを、JDKの内部で実際にどう扱われているかを観察しながら掘り下げる節です。
`String`の内部の表現、`final`フィールドの意味、変更できないコレクションのビューとコピー、オブジェクトの共有（`Integer`のキャッシュ）、Copy-on-Write、永続的データ構造（構造共有）を扱います。

## この節の読み方

この節の解説のうち、事実を述べた段落には`<!-- claim: … -->`という印を付けています（GitHubの画面では見えません）。
印の名前は同じディレクトリの`claims.yml`に対応し、そこにその主張の確かめ方（プログラムの実行、`javap`の照合、フラグの既定値、出典）を書いています。
リポジトリのルートで`npm run check:deep-dive -- b05`を実行すると、すべての主張をJDK 25で確かめ直せます。
「観察する」の欄に載せた出力は、この検査が実際に出したものです。

## ファイル構成

### 観察用のプログラム（verify/）

| ファイル | 観察すること |
|---|---|
| `StringAnatomy.java` | `String`のフィールド、コンパクト文字列、ハッシュ値の記憶 |
| `StringIdentity.java` | 文字列リテラルの共有と、実行時に作る文字列 |
| `StringConcat.java` | 文字列の結合（`+`）のバイトコード（`javap`で見る） |
| `ConstantInlining.java` | 定数変数が値に置き換わること（`javap`で見る） |
| `ThisEscape.java` | コンストラクターから`this`が漏れたときに見える値、柔軟なコンストラクター本体 |
| `FinalReflection.java` | リフレクションで`final`フィールドを書き換えられるか |
| `CollectionCopies.java` | 変更できないリストのビューとコピー、`null`の扱い |
| `SetOrder.java` | `Set.of`の順序が起動のたびに変わること |
| `DefensiveCopy.java` | `Date`と配列の防御的コピー、`clone()`の浅さ |
| `BoxCache.java` | `Integer`のキャッシュとその範囲を広げるオプション |
| `LostUpdate.java` | 不変オブジェクトを指す参照の差し替えで更新が失われること |
| `CowIteration.java` | `CopyOnWriteArrayList`のイテレーターのスナップショット |

`verify/`のプログラムはパッケージを持たないので、`verify/`ディレクトリでそのままコンパイル・実行できます。

```bash
cd verify
javac StringAnatomy.java
java --add-opens java.base/java.lang=ALL-UNNAMED StringAnatomy
javac StringConcat.java && javap -c -p StringConcat
```

### 体験用のデモ（src/）

- `ImmutabilityPatternsDemo.java`
  - 防御的コピーとwithメソッドを持つ不変クラス、ビルダー、オブジェクトのプール、遅延初期化、`AtomicReference`による参照の差し替え
- `PersistentDataStructuresDemo.java`
  - Copy-on-Writeのマップ、永続的リスト（構造共有）、ハッシュ値のビットで枝分かれする永続的ハッシュマップ、変更できない配列リスト、可変のデータ構造との時間の比較
- `FunctionalPatternsDemo.java`
  - レンズ（入れ子の不変オブジェクトの更新）、sealedインターフェイスとレコードによるRedux風の状態管理、関数合成とメモ化
- `PersistentStructuresCheck.java`
  - 自作の永続的データ構造が正しく動くかを、標準のコレクションと突き合わせて確かめる（11）

```bash
javac -d out src/main/java/com/example/immutability/*.java
java -cp out com.example.immutability.ImmutabilityPatternsDemo
java -cp out com.example.immutability.PersistentDataStructuresDemo
java -cp out com.example.immutability.FunctionalPatternsDemo
java -cp out com.example.immutability.PersistentStructuresCheck
```

## 1. 仕様が決めている「不変」と、決めていないこと

Javaには「このクラスは不変である」と宣言するキーワードはありません。
言語が決めているのは、`final`のフィールドには1回しか代入できないこと、`final`のクラスは継承できないこと、`final`フィールドの初期化の安全性（4）などの部品です。
不変クラスは、第6章で学んだ5つのルールに従って、これらの部品を組み合わせて作ります。

一方、標準ライブラリのAPIの仕様は、クラスごとに不変であることを約束しています。
たとえば`String`のAPIの冒頭には、文字列は定数で、作ったあとで値を変えられないと書かれています。
<!-- claim: string-constant -->

この節では、仕様が約束していることと、JDK 25の実装の工夫（バイト配列の持ち方、キャッシュの範囲、クラスの名前など）を分けて説明します。
実装の工夫は、版が変われば変わりうるものです。

## 2. Stringの不変性と内部の表現

### しくみ

`String`は`final`クラスで、文字の並びを`private final`の配列に持ちます。
配列を外へ渡すメソッドはなく、`toCharArray()`などは毎回コピーを返すので、作ったあとで中身が変わることはありません。

### 観察する

`verify/StringAnatomy.java`は、`String`のインスタンスのフィールドをリフレクションで一覧にします。

<!-- output: string-fields -->
```
Stringはfinalクラス: true
フィールド: private final byte[] value
フィールド: private final byte coder
フィールド: private int hash
フィールド: private boolean hashIsZero
```
<!-- /output -->

<!-- claim: string-fields -->

文字は`char[]`ではなく`byte[] value`に入っていて、`coder`がその読み方を表します。
`--add-opens java.base/java.lang=ALL-UNNAMED`を付けて実行すると、配列の長さと`coder`の値まで読めます。

<!-- output: compact-strings -->
```
"hello" 文字数=5 配列の長さ=5 coder=0
"héllo" 文字数=5 配列の長さ=5 coder=0
"こんにちは" 文字数=5 配列の長さ=10 coder=1
```
<!-- /output -->

<!-- claim: compact-strings -->

`-XX:-CompactStrings`を付けて実行すると、次のようになります。

<!-- output: compact-strings-off -->
```
"hello" 文字数=5 配列の長さ=10 coder=1
"héllo" 文字数=5 配列の長さ=10 coder=1
"こんにちは" 文字数=5 配列の長さ=10 coder=1
```
<!-- /output -->

<!-- claim: compact-strings-off -->

### 説明

- Latin-1（ISO-8859-1）で表せる文字だけの文字列は1文字1バイト（`coder=0`）で持つ。`é`もLatin-1に含まれる
  <!-- claim: compact-strings -->
- それ以外の文字（日本語など）を1つでも含む文字列は、全体をUTF-16の1文字2バイト（`coder=1`）で持つ
  <!-- claim: compact-strings -->
- このしくみ（コンパクト文字列）はJava 9で入った（JEP 254）。それまでの`String`は`char[]`を持ち、英数字だけの文字列でも1文字2バイトを使っていた
  <!-- claim: compact-strings -->
- コンパクト文字列は既定で有効で、`-XX:-CompactStrings`で止めるとすべての文字列が1文字2バイトになる
  <!-- claim: compact-strings-default -->
- `length()`が返すのはどちらの場合も文字（UTF-16の単位）の数で、内部の持ち方はAPIからは見えない

内部の配列の形を自由に変えられたのは、`String`が不変で、配列を外へ一度も渡さない設計だったからです。
配列を外へ渡していたら、外のコードが`char[]`を前提にしてしまい、表現を変えられなかったはずです。

### ハッシュ値の記憶

フィールドのうち`hash`と`hashIsZero`は`final`ではありません。
`hashCode()`を最初に呼んだときに計算し、結果を覚えておくためのものです。

<!-- output: string-hash-cache -->
```
hashCode()の前のhash=0
hashCode()の後のhash=1596987778（hashCode()=1596987778）
```
<!-- /output -->

<!-- claim: string-hash-cache -->

複数のスレッドが同時に最初の`hashCode()`を呼ぶと、それぞれが計算して書き込むことがあります。
それでも問題にならないのは、文字列が不変なので、誰が計算しても同じ値になるからです。
不変であることが、同期なしのキャッシュを安全にしている例です。

## 3. 文字列リテラルの共有と文字列の結合

### 観察する：同じインスタンスになる文字列

`verify/StringIdentity.java`は、作り方の違う`"Java"`を`==`で比べます。

<!-- output: string-literal-identity -->
```
リテラルどうし: true
定数式の結合: true
実行時の結合: false
new String: false
intern()の結果: true
equals: true
toUpperCase()の後の元の文字列: Java、戻り値: JAVA
```
<!-- /output -->

### 説明

- 同じ内容の文字列リテラルは、いつも同じインスタンスを指す（JLS 3.10.5）
- `static final`の`HEAD`と`"va"`の結合は定数式なので、コンパイル時に`"Java"`になり、リテラルと同じインスタンスになる（JLS 15.29）
- 実行時に結合した文字列と`new String`は、新しいインスタンスになる（JLS 15.18.1）。`intern()`を呼ぶと、リテラルと同じインスタンスが返る
- `toUpperCase()`は元の文字列を変えず、新しい文字列を返す
<!-- claim: string-literal-identity -->

同じインスタンスを共有できるのは、文字列が不変だからです。
もし文字列を変更できたら、あるクラスのリテラル`"Java"`を書き換えると、無関係なクラスの`"Java"`まで変わってしまいます。
`==`の結果は作り方で変わるので、文字列の比較にはいつも`equals`を使います。

### 観察する：文字列の結合のバイトコード

`verify/StringConcat.java`の`"Hello, " + name + "! #" + count`を、`javap -c -p -v`で見ます。

<!-- output: string-concat-indy -->
```
2: invokedynamic // InvokeDynamic #0:makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;
REF_invokeStatic java/lang/invoke/StringConcatFactory.makeConcatWithConstants:(...)
引数: Hello, \u0001! #\u0001
```
<!-- /output -->

<!-- claim: string-concat-indy -->

同じソースを`javac --release 8`でコンパイルすると、次のようになります。

<!-- output: string-concat-java8 -->
```
0: new // class java/lang/StringBuilder
3: dup
4: invokespecial // Method java/lang/StringBuilder."<init>":()V
7: ldc // String Hello,
9: invokevirtual // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
12: aload_0
13: invokevirtual // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
16: ldc // String ! #
18: invokevirtual // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
21: iload_1
22: invokevirtual // Method java/lang/StringBuilder.append:(I)Ljava/lang/StringBuilder;
25: invokevirtual // Method java/lang/StringBuilder.toString:()Ljava/lang/String;
28: areturn
```
<!-- /output -->

<!-- claim: string-concat-java8 -->

### 説明

Java 8までのjavacは、結合を`StringBuilder`の`append`の連鎖に置き換えていました。
Java 9からは、結合全体を1つの`invokedynamic`命令にし、どう結合するかを実行時に`StringConcatFactory`に任せます（JEP 280）。
定数の部分は「`Hello, \u0001! #\u0001`」という型紙（`\u0001`が引数の入る位置）としてクラスファイルに残ります。
言語仕様は、中間の`String`を作らずに結合してよいと述べるだけで、どの方法で結合するかは決めていません。
<!-- claim: string-concat-java8, string-concat-indy -->
そのため、javacを変えずに、JDKの側で結合の方法を改良できるようになりました。

ループで`s = s + x`を繰り返すと、そのたびに新しい文字列が作られ、それまでの内容がコピーされます。
不変であることの代償なので、たくさん結合するときは`StringBuilder`で組み立ててから最後に`toString()`します。

## 4. finalフィールドの意味

### 定数変数は値に置き換わる

`final`のフィールドのうち、プリミティブ型か`String`で、定数式で初期化したものを「定数変数」と呼びます。
定数変数を使う箇所は、コンパイル時にその値に置き換わります。
`verify/ConstantInlining.java`の`readAll`メソッドを`javap`で見ると、5つのフィールドのうち3つだけを`getstatic`で読んでいます。

<!-- output: constant-inlining -->
```
getstatic // Field BOXED:Ljava/lang/Integer;
getstatic // Field COMPUTED:I
getstatic // Field NAMES:Ljava/util/List;
```
<!-- /output -->

<!-- claim: constant-inlining -->

`MAX`（`int`）と`NAME`（`String`）は、結合の型紙に`limit100`として埋め込まれ、フィールドを読みません。
`BOXED`は値が定数でも`Integer`型なので、`COMPUTED`は初期化が定数式（コンパイル時に計算できる式）でないので、定数変数ではありません。

置き換えは、別のクラスで定数を使う場合にも起きます。
そのため、ライブラリの`public static final int`の値を変えても、それを使うクラスを再コンパイルしないかぎり古い値のまま動きます。
<!-- claim: constant-recompile -->

公開する定数の値が将来変わりうるなら、メソッドで返すか、定数変数にならない書き方にします。

### 初期化の安全性

不変オブジェクトを同期なしで複数のスレッドに渡してよい根拠は、JLS 17.5の`final`フィールドの規則です。
オブジェクトは、コンストラクターが終わったときに初期化を終えたとみなされます。
その後にだけ参照を見たスレッドは、`final`フィールドの正しく初期化された値を見ることが保証されます。
<!-- claim: final-field-semantics -->

`final`でないフィールドには、この保証がありません。
データ競合のある渡し方（`volatile`でないフィールドに入れて別のスレッドが読むなど）をすると、別のスレッドは初期化前の値を見ることがありえます。
この現象は実行のたびに起きたり起きなかったりするので、この節では観察せず、仕様の記述で確かめています。

### コンストラクターからthisを漏らさない

保証には「コンストラクターが終わった後に参照を見る」という条件があります。
コンストラクターの途中で`this`を外に渡すと、この条件が崩れます。
同じスレッドでも、オーバーライドできるメソッドをコンストラクターから呼ぶと、同じことが起きます。

`verify/ThisEscape.java`の`Product`は、`final`フィールドに代入する前に`describe()`を呼びます。
サブクラス`LoggedProduct`は`describe()`をオーバーライドして、フィールドを表示します。

<!-- output: this-escape-run -->
```
describe(): name=null stock=0
完成後: name=pen stock=3
--- super(...)の前に代入した場合 ---
describe(): name=null stock=5
完成後: name=ink stock=5
```
<!-- /output -->

<!-- claim: this-escape-run -->

`final`の`name`と`stock`が、代入される前の既定値（`null`と`0`）で見えています。
`final`は「1回しか代入できない」ことを保証しますが、「代入される前に読まれない」ことは保証しません。

javacに`-Xlint:this-escape`を付けると、こうした箇所を警告してくれます。

<!-- output: this-escape-lint -->
```
verify/ThisEscape.java:13: warning: [this-escape] possible 'this' escape before subclass is fully initialized
```
<!-- /output -->

<!-- claim: this-escape-lint -->

Java 25では、コンストラクターの`super(...)`より前に文を書けるようになりました（柔軟なコンストラクター本体、JEP 513）。
`super(...)`の前では作っているオブジェクトを使えませんが、自分のフィールドへの代入はできます。
`EarlyProduct`は`stock`を`super(name)`の前に代入するので、スーパークラスのコンストラクターから呼ばれた`describe()`も正しい値を見ます。

<!-- output: flexible-constructor -->
```
describe(): name=null stock=5
```
<!-- /output -->

<!-- claim: flexible-constructor -->

スーパークラス自身の`name`はまだ`null`のままなので、根本的な対策は、コンストラクターからオーバーライドできるメソッドを呼ばないことです。

### リフレクションとfinal

`final`のフィールドも、リフレクションで書き換えられる場合があります。
`verify/FinalReflection.java`は、`setAccessible(true)`にした`Field`で3種類のフィールドを書き換えようとします。

<!-- output: final-reflection -->
```
ふつうのクラスのfinalフィールド: 999
レコード: IllegalAccessException
static final: IllegalAccessException
```
<!-- /output -->

<!-- claim: final-reflection -->

`Field.set`のAPIの仕様は、`final`フィールドに書き込める場合を限っています。
`setAccessible(true)`が成功していて、`static`でないフィールドで、そのクラスが隠しクラスでもレコードでもない場合だけです。
ふつうのクラスの不変性は、リフレクションを使うコードからは守られないということです。
JDK 26では、このような書き換えに警告を出すようになりました（JEP 500）。
将来は既定で禁止し、`final`が本当に変わらないことを保証する方向です。
<!-- claim: final-mean-final -->

## 5. 変更できないコレクション：ビューとコピー

### しくみ

第6章のViewVsCopyで見たとおり、`Collections.unmodifiableList`は元のリストへの「ビュー」で、`List.copyOf`は「コピー」です。
ここでは、そのほかの違いも含めて比べます。

### 観察する

`verify/CollectionCopies.java`の出力です。

<!-- output: view-vs-copy -->
```
元のリストにcを足した後
unmodifiableList: [a, b, c]
List.copyOf: [a, b]
```
<!-- /output -->

<!-- claim: view-vs-copy -->

<!-- output: copyof-no-copy -->
```
List.copyOf(List.of(...))は同じオブジェクト: true
List.copyOf(unmodifiableList)は同じオブジェクト: false
unmodifiableList(unmodifiableList)は同じオブジェクト: true
```
<!-- /output -->

<!-- claim: copyof-no-copy -->

<!-- output: list-nulls -->
```
unmodifiableListはnullを持てる: [p, null]
List.copyOfにnullを含むリスト: NullPointerException
List.ofにnull: NullPointerException
```
<!-- /output -->

<!-- claim: list-nulls -->

<!-- output: aslist-stream-tolist -->
```
Arrays.asListはsetできる: [S, t]
Arrays.asListにadd: UnsupportedOperationException
Stream.toList()の結果にadd: UnsupportedOperationException
Collectors.toList()の結果にadd: [m, n, o]
```
<!-- /output -->

<!-- claim: aslist-stream-tolist -->

### 説明

| 作り方 | 元のリストの変更 | `null`の要素 | 変更の操作 |
|---|---|---|---|
| `Collections.unmodifiableList(x)` | 見える（ビュー） | 持てる | 例外 |
| `List.copyOf(x)` | 見えない（コピー） | `NullPointerException` | 例外 |
| `List.of(...)` | 元のリストがない | `NullPointerException` | 例外 |
| `Arrays.asList(配列)` | 配列の変更が見える | 持てる | `set`はできる、`add`は例外 |
| `Stream.toList()` | 元のリストがない | 持てる | 例外 |

- 不変クラスのフィールドに入れるなら`List.copyOf`を使う。ビューは、元のリストを持っている人が変えられるので、不変にならない
- `List.copyOf`は、渡されたのが`List.of`などで作った変更できないリストなら、コピーせずに同じオブジェクトを返す（APIは「ふつうはコピーを作らない」と述べる）。不変なものはコピーしても意味がないからである
- `unmodifiableList`のビューを`List.copyOf`に渡すと、コピーを作る。ビューの元のリストは変わりうるからである
- `Collectors.toList()`の結果はJDK 25では`ArrayList`で要素を足せるが、APIは型も変更できるかも保証しない。変更できないリストが欲しいなら`Stream.toList()`を、変更できるリストが欲しいなら`Collectors.toCollection(ArrayList::new)`を使う

`List.of`が返すオブジェクトのクラスは、要素の数で変わります（実装の詳細です）。

<!-- output: immutable-collection-classes -->
```
List.of(2要素)のクラス: java.util.ImmutableCollections$List12
List.of(3要素)のクラス: java.util.ImmutableCollections$ListN
List12のフィールド: Object e0 Object e1
ListNのフィールド: Object[] elements boolean allowNulls
```
<!-- /output -->

<!-- claim: immutable-collection-classes -->

要素が2つまでのリストは配列を使わずフィールド（`e0`・`e1`）に直接持ち、3つ以上は配列に持ちます。
不変なので、作ったあとで要素が増えて形が合わなくなる心配がなく、要素の数に合わせた形を選べます。
`ListN`の`allowNulls`は、`null`を持てる`Stream.toList()`の結果と、持てない`List.of`の結果で、同じクラスを使い分けるためのものです。

`Set.of`や`Map.of`で作ったセットとマップの要素の順序は決まっておらず、変わりうるとAPIに書かれています。
JDK 25の実装では、順序がJVMを起動するたびに変わります。
`verify/SetOrder.java`は、自分自身を別のJVMで5回起動し、`Set.of("a", …, "h")`の順序を集めます。

<!-- output: set-order-unspecified -->
```
同じ要素: abcdefgh
5回の起動で現れた順序の種類: 2以上
```
<!-- /output -->

<!-- claim: set-order-unspecified -->

順序に頼るコードを、テストを通ったまま見過ごさないようにするための工夫です。

## 6. 防御的コピーが要る場所

### 観察する

`verify/DefensiveCopy.java`は、`java.util.Date`を持つクラスを、コピーしない`LeakyPeriod`と、受け取るときと返すときにコピーする`SafePeriod`で比べます。

<!-- output: defensive-copy -->
```
LeakyPeriodのstart（ミリ秒）: 172800000
SafePeriodのstart（ミリ秒）: 0
LocalDateのplusDays後: 元=2026-04-01 戻り値=2026-04-02 レコード=2026-04-01
```
<!-- /output -->

<!-- claim: defensive-copy -->

`LeakyPeriod`は、呼び出し側が元の`Date`を変えたり、ゲッターの戻り値を変えたりすると、中身が変わってしまいます。
`Date`は可変なので、`final`のフィールドに入れても中身は守られません。
`java.time`の`LocalDate`は不変で、`plusDays`は新しいオブジェクトを返すので、コピーせずにそのまま持てます。
新しく書くコードで`Date`の代わりに`java.time`を使う理由の1つです。

<!-- output: array-clone-shallow -->
```
final配列の要素を変更: [99, 20]
clone()後に内側を変更: grid[0][0]=100 外側は別の配列: true 内側は同じ配列: true
```
<!-- /output -->

<!-- claim: array-clone-shallow -->

- `final`の配列は、別の配列を指すようには変えられないが、要素は書き換えられる
- 配列の`clone()`は浅いコピーで、2次元配列では外側の配列だけを新しく作り、内側の配列は共有する
- 配列を持つ不変クラスは、受け取るときも返すときもコピーする。2次元以上なら内側の配列もコピーするか、`List`の`List`（`List.copyOf`）にする

### コピーする順序

コンストラクターでは、引数を検査する前にコピーし、コピーしたほうを検査します。
検査してからコピーすると、検査とコピーの間に別のスレッドが元のオブジェクトを変えたとき、検査を通らない値が入ってしまいます。
また、`Date`は`final`クラスではなく、渡されたのが`clone()`をオーバーライドしたサブクラスということもありうるので、信頼できない引数のコピーには`new Date(d.getTime())`のように自分で作るほうが安全です。

## 7. 不変オブジェクトの共有：Integerのキャッシュ

不変オブジェクトは、同じ値なら1つのインスタンスを共有できます。
標準ライブラリの例が、オートボクシング（`Integer.valueOf`）のキャッシュです。

### 観察する

<!-- output: integer-cache -->
```
Integer -129: 同じオブジェクト=false equals=true
Integer -128: 同じオブジェクト=true equals=true
Integer 127: 同じオブジェクト=true equals=true
Integer 128: 同じオブジェクト=false equals=true
Integer 1000: 同じオブジェクト=false equals=true
Long 128: 同じオブジェクト=false
Long 127: 同じオブジェクト=true
Boolean: 同じオブジェクト=true
```
<!-- /output -->

<!-- claim: integer-cache -->

`-XX:AutoBoxCacheMax=1000`を付けて実行すると、次のようになります。

<!-- output: autobox-cache-max -->
```
Integer 127: 同じオブジェクト=true equals=true
Integer 128: 同じオブジェクト=true equals=true
Integer 1000: 同じオブジェクト=true equals=true
Long 128: 同じオブジェクト=false
```
<!-- /output -->

<!-- claim: autobox-cache-max -->

### 説明

- `Integer.valueOf`は-128〜127の値をいつもキャッシュすると、APIに書かれている。範囲外の値もキャッシュしてよいとされている
- 言語仕様（JLS 5.1.7）が同じオブジェクトになることを求めるのは、-128〜127の整数などを定数式からボクシングした場合である。上のプログラムのように変数の値をボクシングするときの共有は、`Integer.valueOf`のAPIが保証している
  <!-- claim: boxing-constant-rule -->
- HotSpotでは、`-XX:AutoBoxCacheMax`（既定値128）で`Integer`のキャッシュの上限を広げられる。127より小さい値を指定しても、-128〜127の共有はなくならない
  <!-- claim: autobox-cache-default, autobox-cache-min -->
- システムプロパティ`-Djava.lang.Integer.IntegerCache.high=1000`でも同じように広げられる
  <!-- claim: integer-cache-property -->
- `Long`のキャッシュは-128〜127で固定で、`AutoBoxCacheMax`の影響を受けない

`Integer`が不変だから、`valueOf(100)`を呼んだすべての箇所に同じオブジェクトを渡せます。
その結果、`==`の結果は値の範囲や起動オプションで変わります。
ラッパークラスの比較には、いつも`equals`を使います（第9章）。

src/の`ImmutablePoint.of`は、同じ考え方で、よく使う座標のインスタンスをプールに入れて共有する例です。

## 8. 不変オブジェクトとスレッド

不変オブジェクトそのものは、同期なしで複数のスレッドから読めます（4の初期化の安全性）。
しかし、「今の値」を表す変数が不変オブジェクトを指していて、それを新しいオブジェクトに差し替える処理は、別の問題です。

`verify/LostUpdate.java`は、不変な`Counter`レコードを指す参照を、4つのスレッドが10万回ずつ差し替えます。

<!-- output: lost-update -->
```
期待する値: 400000
volatileで差し替え: 更新が失われた（…）
AtomicReference.updateAndGet: 400000
```
<!-- /output -->

<!-- claim: lost-update -->

`shared = shared.next()`は、読み取り・新しいオブジェクトの作成・書き込みの3段階です。
`volatile`は書き込みがほかのスレッドから見えることを保証しますが、3段階をまとめて行うことは保証しないので、2つのスレッドが同じ値を読んで同じ値を書くと、片方の更新が失われます。
失われる数は実行のたびに変わります。
<!-- claim: lost-update -->
`AtomicReference.updateAndGet`は、読んだ値が書き込みの時点でも変わっていないときだけ書き込み、変わっていたら計算をやり直すので、更新が失われません。
やり直しがありうるので、`updateAndGet`に渡す関数には副作用を持たせません。

src/の`ImmutabilityPatternsDemo`の「Concurrency Safety Demo」も、4つのスレッドから注文に品目を足して、`AtomicReference`で101個になることを確かめています。

## 9. Copy-on-Write

### しくみ

Copy-on-Writeは、変更のたびに内部の配列（やマップ）全体をコピーし、コピーを変更してから参照を差し替える方法です。
読む側は、差し替える前の古い配列か、差し替えた後の新しい配列のどちらかを見ます。
どちらの配列も作ったあとは変わらないので、読む側はロックなしで読めます。

### 観察する

`verify/CowIteration.java`は、たどっている途中で要素を足したときの動きを、`ArrayList`と`CopyOnWriteArrayList`で比べます。

<!-- output: cow-snapshot -->
```
ArrayList: ConcurrentModificationException
CopyOnWriteArrayList: たどった要素=abc 終了後のリスト=[a, b, c, d]
イテレーターのremove: UnsupportedOperationException
```
<!-- /output -->

<!-- claim: cow-snapshot -->

### 説明

- `CopyOnWriteArrayList`のイテレーターは、作った時点の配列（スナップショット）をたどる。途中で要素を足しても例外にならず、足した要素はそのイテレーターには見えない
- スナップショットは変わらないので、イテレーターの`remove`・`set`・`add`は使えない（`UnsupportedOperationException`）
- `ArrayList`では、たどる途中の変更が`ConcurrentModificationException`になる
- 変更のたびに全体をコピーするので、変更が多いと遅い。APIの説明も、たどる操作が変更よりずっと多い場面に向くと述べている（イベントのリスナーの一覧など）
  <!-- claim: cow-cost -->

標準ライブラリには`CopyOnWriteArrayList`と`CopyOnWriteArraySet`はありますが、マップはありません。
src/の`CopyOnWriteMap`は、同じ考え方をマップに当てはめた例です（書き込みは`synchronized`で1つずつにし、`volatile`のフィールドの参照を差し替えます）。

## 10. 永続的データ構造と構造共有

### しくみ

不変なデータ構造で「変更」するには、新しい版を作ります。
毎回全体をコピーすると遅いので、変わらない部分を古い版と新しい版で共有します（構造共有）。
古い版も壊れずに残るので、このようなデータ構造を「永続的データ構造」と呼びます。

連結リストの先頭に要素を足す操作では、新しいノードを1つ作り、その次を古いリストの先頭にします。

```
元のリスト:     A → B → C
Xを先頭に足す:  X ↗            （XからAへつなぐ。A・B・Cは共有）
Yを先頭に足す:  Y ↗            （A・B・Cは3つの版で共有）
```

src/の`PersistentHashMap`は、キーのハッシュ値を5ビットずつ区切り、32方向に枝分かれする木です。
キーを足すと、根からそのキーの葉までの道にあるノードだけを作り直し、ほかの枝は古い版と共有します。
ハッシュ値が32ビットすべて同じ別のキーは、1つのノードにまとめて持ちます。

### 観察する

構造共有のコードは、共有を間違えると古い版が壊れるので、誤りに気づきにくいものです。
`PersistentStructuresCheck`は、ノードが同じオブジェクトかを確かめ、乱数の種を固定した2万回の操作を`HashMap`と同時に行って、途中の20の版をすべて突き合わせます。
ハッシュ値の衝突を起こすため、キーの`hashCode`を64通りに絞っています。

<!-- output: persistent-check -->
```
合格: prependした2つのリストが元のノードを共有する
合格: 元のリストは変わらない
合格: tailは2番目以降のノードをそのまま使う
合格: appendは共有しない（全体を作り直す）
合格: filterは最後に除いた要素より後ろを共有する
合格: 10万要素のmap
合格: 10万要素のfilter
合格: size
合格: PersistentHashMapの全20版がHashMapの写しと一致する（衝突するキーを含む）
合格: ImmutableArrayListは元のリストの変更を受けない
合格: appendは新しいリストを返し、元は変わらない
すべての確認に合格
```
<!-- /output -->

<!-- claim: persistent-check -->

### 説明

| データ構造 | 読み取り | 更新 | 古い版 |
|---|---|---|---|
| `ArrayList` | 位置でO(1) | 末尾への追加はO(1)（ならし） | 残らない |
| 自作の`ImmutableArrayList` | 位置でO(1) | O(n)（配列全体をコピー） | 残る |
| 自作の`CopyOnWriteMap` | O(1) | O(n)（マップ全体をコピー） | 残らない（スナップショットは取れる） |
| 自作の`PersistentList` | 位置でO(n) | 先頭への追加はO(1)、末尾への追加はO(n) | 残る（共有） |
| 自作の`PersistentHashMap` | 木の深さ（最大7段） | 木の深さ分のノードを作り直す | 残る（共有） |

`PersistentDataStructuresDemo`は、同じ1万回の追加を可変のデータ構造と比べます。
筆者の環境（Apple M3、JDK 25.0.2）では、全体をコピーする`ImmutableArrayList`と`CopyOnWriteMap`は可変の版より数十倍から百倍以上遅く、先頭に足すだけの`PersistentList`は`LinkedList`と同じ程度でした。
測定値は環境と実行のたびに変わるので、向きだけを読み取ってください（正確に測るにはJMHを使います）。

永続的リストの`map`と`filter`は、再帰で書くと要素の数だけ呼び出しが深くなり、長いリストで`StackOverflowError`になります。
src/の実装は、値をいったんリストに集めてから後ろから組み立てます。
`filter`は、最後に取り除いた要素より後ろのノードを作り直さずに共有します。

## 11. レコードと浅い不変性

レコードのフィールドは`final`ですが、フィールドが指すオブジェクトが可変なら、その中身は変えられます（浅い不変性）。
レコードでの防御的コピー（コンパクトコンストラクターで`List.copyOf`する）と、可変なリストを持つレコードを`HashSet`に入れたときの問題は、付録B.10の「5. 浅い不変性」で観察しています。

## 12. 体験用のデモの補足

- レンズ（`FunctionalPatternsDemo`の`Lens`）
  - 「取り出す関数」と「新しい値で作り直す関数」の組で、`person.withAddress(person.address().withCity(...))`のような入れ子の更新を1つの操作にまとめる。Javaの標準ライブラリにはない考え方で、ここでは自作している
- Redux風の状態管理
  - 状態をレコード、操作（アクション）をsealedインターフェイスとレコードで表し、「今の状態とアクションから新しい状態を作る」関数（リデューサー）で状態を進める。switchがすべてのアクションを網羅しているかをコンパイラーが確かめる（第8章）
- 遅延初期化
  - `LazyImmutable`は、重い計算の結果を最初に使うときに作って覚える。ダブルチェックロッキングでは、フィールドを`volatile`にしないと、初期化の途中のオブジェクトを別のスレッドが見るおそれがある
- オブジェクトのプール
  - 不変なので共有できるが、プールを引くたびに文字列のキーを作ってマップを検索するコストがかかる。共有で得をするかは、測って確かめる

## よくある誤解

- `final`を付けたフィールドは、中身も変わらない
  - 変わらないのは参照だけで、指す先の配列や`ArrayList`の中身は変えられる（6）
  <!-- claim: array-clone-shallow -->
- `final`フィールドは、代入される前に読まれることはない
  - コンストラクターからオーバーライドできるメソッドを呼ぶと、既定値（`null`や`0`）が見える（4）
  <!-- claim: this-escape-run -->
- `Collections.unmodifiableList`で包めば不変になる
  - ビューなので、元のリストを変えると中身が変わる。不変にするなら`List.copyOf`を使う（5）
  <!-- claim: view-vs-copy -->
- 不変オブジェクトを使えば、スレッドの問題はすべて解決する
  - オブジェクトは壊れないが、「今の値」を指す参照の差し替えには`AtomicReference`などが要る（8）
  <!-- claim: lost-update -->
- `Integer`は`==`で比べられる
  - 共有されるのは-128〜127（既定）だけで、範囲はオプションでも変わる。`equals`で比べる（7）
  <!-- claim: integer-cache -->
- 文字列の`+`は、いつも`StringBuilder`に変換される
  - Java 9からは`invokedynamic`で、結合の方法は実行時に`StringConcatFactory`が決める（3）
  <!-- claim: string-concat-indy -->
- `final`は性能のために付ける
  - 不変性や継承の禁止という設計の意図を表すために付ける。メソッドの呼び出しの速さは`final`の有無で変わらない（付録B.04）

## 参考資料

- Java言語仕様（Java SE 25）
  - 3.10.5「String Literals」
  - 4.12.4「final Variables」
  - 5.1.7「Boxing Conversion」
  - 10.7「Array Members」
  - 13.1「The Form of a Binary」、13.4.9「final Fields and static Constant Variables」
  - 15.18.1「String Concatenation Operator +」、15.29「Constant Expressions」
  - 17.5「final Field Semantics」
  - `https://docs.oracle.com/javase/specs/jls/se25/html/`
- Java SE 25のAPI
  - `String`、`List`（Unmodifiable Lists）、`Set`、`Collections.unmodifiableList`、`Integer.valueOf`、`CopyOnWriteArrayList`、`AtomicReference`、`StringConcatFactory`、`Field.set`、`Stream.toList`、`Collectors.toList`
  - `https://docs.oracle.com/en/java/javase/25/docs/api/java.base/module-summary.html`
- javacコマンドのリファレンス（Java SE 25）の`-Xlint`
  - `https://docs.oracle.com/en/java/javase/25/docs/specs/man/javac.html`
- JEP 254: Compact Strings
  - `https://openjdk.org/jeps/254`
- JEP 280: Indify String Concatenation
  - `https://openjdk.org/jeps/280`
- JEP 513: Flexible Constructor Bodies
  - `https://openjdk.org/jeps/513`
- JEP 500: Prepare to Make Final Mean Final
  - `https://openjdk.org/jeps/500`
