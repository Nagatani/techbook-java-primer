# 付録B.02 プログラミングパラダイムの進化と関数型プログラミングの数学的基礎<small>ラムダ計算からモナドまで</small>

第3章で学んだオブジェクト指向と、第11章で学んだラムダ式・関数型インターフェイスを、プログラミングの考え方（パラダイム）の流れと、関数型プログラミングの土台にある数学（ラムダ計算・圏論・ファンクター・モナド）から見直す節です。
数学の概念は、Javaのコードで確かめられる範囲に絞って説明します。
法則が成り立つ場合だけでなく、Javaでは成り立たない場合も実際に動かして確かめます。

## この節の読み方

この節の解説のうち、事実を述べた段落には`<!-- claim: … -->`という印を付けています（GitHubの画面では見えません）。
印の名前は同じディレクトリの`claims.yml`に対応し、そこにその主張の確かめ方（プログラムの実行、`javap`・`javac`の出力の照合、出典）を書いています。
リポジトリのルートで`npm run check:deep-dive -- b02`を実行すると、すべての主張をJDK 25で確かめ直せます。
「観察する」の欄に載せた出力は、この検査が実際に出したものです。

歴史の記述は、題名・著者・年を確かめられる資料（論文、言語の仕様、大学の公式のページ）があるものに限っています。

## ファイル構成

この節には体験用のデモ（`src/`）はなく、コードはすべて観察用のプログラム（`verify/`）です。

| ファイル | 観察すること |
|---|---|
| `Paradigms.java` | 同じ計算を命令型・構造化・オブジェクト指向・関数型で書く |
| `Composition.java` | `andThen`と`compose`の順序、圏の法則を値ごとに確かめる |
| `LambdaCalculus.java` | ラムダ計算の式、カリー化、チャーチ数 |
| `LambdaBytecode.java` | ラムダ式・メソッド参照・匿名クラスのコンパイル結果と実行時のクラス（`javap`で見る） |
| `FunctorLaws.java` | `Optional`・`Stream`の`map`とファンクターの法則 |
| `MonadLaws.java` | `Optional`・`Stream`の`flatMap`とモナドの法則 |
| `ResultMonad.java` | sealedインターフェイスとレコードで作る`Result`型 |
| `Sequence.java` | `List<Optional<T>>`を`Optional<List<T>>`に裏返す |
| `TailCall.java` | 末尾再帰の呼び出しが最適化されないこと |
| `Laziness.java` | 引数を先に評価すること、`Supplier`と`Stream`による遅延評価 |
| `Memoize.java` | 高階関数によるメモ化と、`computeIfAbsent`を再帰で使う落とし穴 |
| `ParserCombinator.java` | パーサーを関数として組み合わせる |
| `ExpressionAdt.java` | 代数的データ型（式の木）とパターンマッチング |
| `IOMonad.java` | 副作用を「実行する手順の値」として組み立てる |
| `patterns/` | 設計パターンを関数で書く（イベントのハンドラー、ミドルウェア、テンプレートメソッド、イベントソーシング、ファクトリ、ビルダー） |
| `SinceRecord.java`ほか`Since`で始まる4つ | 言語機能が使えるようになった版（`javac --release`で見る） |
| `compile-errors/` | コンパイルエラーになる書き方（実質的にfinalでない変数の参照など） |

`verify/`のプログラムはパッケージを持たないので、`verify/`ディレクトリでそのままコンパイル・実行できます。

```bash
cd verify
javac FunctorLaws.java && java FunctorLaws
javac LambdaBytecode.java && javap -c -p LambdaBytecode
javac --release 15 SinceRecord.java
```

## 1. パラダイムの流れ

プログラミングパラダイムは、プログラムをどのような単位で組み立て、計算をどう表すかという考え方の枠組みです。
`verify/Paradigms.java`は、1から10までの和を4つの書き方で求めます。
どれも結果は同じ55で、違うのは「何を単位に考えるか」です。

<!-- output: paradigms-sum -->
```
命令型: 55
構造化: 55
オブジェクト指向: 55
関数型: 55
```
<!-- /output -->
<!-- claim: paradigms-sum -->

- 命令型
  - 変数の状態を、命令を並べて1つずつ書き換える。コンピューターの動き（メモリの読み書き）に近い
- 構造化
  - 制御の流れを、順次・分岐・繰り返しと、入口と出口が1つずつの手続きに限る
- オブジェクト指向
  - 状態（フィールド）と、状態を変える操作（メソッド）をクラスにまとめ、外から状態を直接触らせない
- 関数型
  - 値を受け取って値を返す関数を組み合わせ、変数の書き換えを避ける

### ソフトウェア危機と構造化プログラミング

1968年10月7日〜11日に、NATOの科学委員会の後援で、ドイツのガルミッシュでソフトウェア工学の会議が開かれました。
翌年1月にまとめられた報告書（編者はPeter NaurとBrian Randell）には、参加者の一部が「software crisis（ソフトウェア危機）」と呼んだ問題をめぐる議論が収められています。
大きなシステムの開発で、目標と実際にできたものとの差が広がっているという問題です。
「crisis（危機）」は感情的な言葉で好まないという参加者の発言も載っています。
<!-- claim: nato-1968 -->

同じ1968年、Edsger W. DijkstraはCommunications of the ACM（11巻、1968年3月、147〜148ページ）に、`go to`文をなくすべきだと論じる投稿を発表しました。
掲載時の題名は「Go To Statement Considered Harmful」で、Dijkstraの原稿の題名は「A Case against the GO TO Statement」（EWD215）です。
投稿は、`go to`文が論理的には不要であることを示したBöhmとJacopiniの論文（1966年）にも触れています。
<!-- claim: dijkstra-goto -->

Javaには`goto`文がありません。
`goto`は予約語ですが、いまは使われておらず、変数名にもできません（JLSの3.9）。
`break`・`continue`（ラベル付きを含む）、`return`、例外が、構造化された範囲での脱出を受け持ちます。
<!-- claim: java-goto-reserved -->

### オブジェクト指向

クラス、サブクラス（継承）、オブジェクトを動的に作るといったオブジェクト指向の考え方は、ノルウェー計算センターのOle-Johan DahlとKristen NygaardがSimula I（1961〜1965年）とSimula 67（1965〜1968年）で形にしました。
Dahlが教授を務めたオスロ大学の公式のページに、この経緯がまとめられています。
<!-- claim: simula -->

Java言語仕様（JLS）の第1章は、Javaを「クラスに基づくオブジェクト指向の言語」と呼び、CとC++に関係するが組み立て方はかなり違うと述べています。
参考文献には、Smalltalk-80、C++、Common Lisp Object Systemなど、ほかのオブジェクト指向の言語の文書が挙がっています。
<!-- claim: jls-oo-lineage -->

Javaの値のうち、オブジェクトはクラスのインスタンスと配列だけです。
`int`や`double`などのプリミティブ型の値はオブジェクトではありません。
そのためJavaは、すべての値がオブジェクトである言語（Smalltalkなど）とは違います。
<!-- claim: jls-primitive-not-object -->

### 関数型

LISPは、John McCarthyが1960年4月のCommunications of the ACMに発表した論文で示した言語です。
この論文は、関数を表す記法として、Alonzo Churchのλ記法（Churchの著書『The Calculi of Lambda-Conversion』、1941年）をほぼそのまま使っています。
<!-- claim: lisp-church -->

Javaは、Java 8（JEP 126）でラムダ式とメソッド参照を、インターフェイスのdefaultメソッドとともに取り入れました。
その後、レコード（Java 16、JEP 395）、sealedクラス（Java 17、JEP 409）、switchのパターンマッチングとレコードパターン（Java 21、JEP 441・JEP 440）が加わり、関数型の言語でよく使う「代数的データ型をパターンで分解する」書き方ができるようになりました。
<!-- claim: jep126-java8 -->

JDK 25の`javac`は、`--release 8`より古い版を受け付けないので、ラムダ式の版は`javac`では確かめられません。
後の3つは、`--release`で1つ前の版を指定すると、コンパイルエラーになることで確かめられます。
`--release 20`では、switchのパターンとレコードパターン（エラーの文ではdeconstruction patterns）の両方がエラーになります。

<!-- output: since-record -->
```
error: records are not supported in -source 15
```
<!-- /output -->
<!-- claim: since-record -->

<!-- output: since-sealed -->
```
error: sealed classes are not supported in -source 16
```
<!-- /output -->
<!-- claim: since-sealed -->

<!-- output: since-record-pattern -->
```
error: patterns in switch statements are not supported in -source 20
error: deconstruction patterns are not supported in -source 20
```
<!-- /output -->
<!-- claim: since-record-pattern -->

この節のコードで使う名前のない変数`_`は、Java 22（JEP 456）からです。
<!-- claim: since-unnamed -->

いまのJavaは、オブジェクト指向を中心に、関数型の書き方を取り入れたマルチパラダイムの言語です。
どの書き方がよいかではなく、問題に合わせて組み合わせることが大切です。

## 2. 関数型プログラミングの考え方

関数型プログラミングでは、次のような性質を大切にします。

- 参照透過性
  - 式を、その式の値に置き換えてもプログラムの意味が変わらない。同じ引数の関数呼び出しは、いつも同じ値になる
- 不変性
  - 一度作ったデータを書き換えず、変えたいときは新しいデータを作る（第6章）
- 副作用を限る
  - 関数の外の状態を変える処理（出力、変数の書き換え）を、決まった場所に集める
- 高階関数
  - 関数を値として扱い、引数に渡したり、戻り値として返したりする

Javaのラムダ式は、外のローカル変数に代入し直せません（第11章）。
ラムダ式から参照するローカル変数は、`final`か実質的にfinalでなければならないと、JLSの15.27.2が決めています。
JLSは、値が変わり続けるローカル変数を取り込むと並行処理の問題を招きやすいことを、この制限の理由に挙げています。
`verify/compile-errors/CaptureMutation.java`をコンパイルすると、次のエラーになります。

<!-- output: capture-effectively-final -->
```
error: local variables referenced from a lambda expression must be final or effectively final
```
<!-- /output -->
<!-- claim: capture-effectively-final -->

同じ規則は匿名クラスなどの内部クラスにもあります（`verify/compile-errors/AnonymousCapture.java`）。

<!-- output: anonymous-effectively-final -->
```
error: local variables referenced from an inner class must be final or effectively final
```
<!-- /output -->
<!-- claim: anonymous-effectively-final -->

この制限は変数への代入を禁じるだけで、変数が指すオブジェクトの中身（リストの要素など）を変えることは禁じていません。
参照透過性を保つかどうかは、書く人に任されています。

### 関数の合成

数学では、関数`g`を当てはめてから`f`を当てはめる関数を`f ∘ g`と書きます（`(f ∘ g)(x) = f(g(x))`）。
`java.util.function.Function`には、合成のメソッドが2つあります。

- `f.andThen(g)`
  - `f`を先に、`g`を後に当てはめる（`g ∘ f`）
- `f.compose(g)`
  - `g`を先に、`f`を後に当てはめる（`f ∘ g`。数学の書き方と同じ順）

<!-- output: compose-order -->
```
addOne.andThen(multiplyByTwo).apply(3) = 8
addOne.compose(multiplyByTwo).apply(3) = 7
```
<!-- /output -->

`addOne.andThen(multiplyByTwo)`は`(3 + 1) * 2 = 8`、`addOne.compose(multiplyByTwo)`は`(3 * 2) + 1 = 7`です。
<!-- claim: compose-order -->

## 3. ラムダ計算

ラムダ計算は、関数を作ることと関数を当てはめることだけで計算を表す形式体系です。
式は次の3種類だけからできています。

1. 変数
    + `x`、`y`、`z`など
2. 抽象（関数を作る）
    + `λx.M`（`x`を受け取って`M`を返す関数）
3. 適用（関数を当てはめる）
    + `M N`（関数`M`に`N`を当てはめる）

計算は、適用の式`(λx.M) N`を、`M`のなかの`x`を`N`に置き換えた式`M[x := N]`に書き換えること（ベータ簡約）で進みます。
たとえば`(λf.λx.f (f x)) (λy.y + 1) 5`は、次のように簡約されます（`+`と数はラムダ計算に足した記号です）。

```
(λf.λx.f (f x)) (λy.y + 1) 5
→ (λx.(λy.y + 1) ((λy.y + 1) x)) 5
→ (λy.y + 1) ((λy.y + 1) 5)
→ (λy.y + 1) 6
→ 7
```

### Javaで書いてみる

`verify/LambdaCalculus.java`は、ラムダ計算の式をJavaのラムダ式で書きます。
ラムダ計算の関数は1つの引数しか取らないので、複数の引数は「関数を返す関数」で表します（カリー化）。
カリー化した関数に一部の引数だけを渡して、残りの引数を待つ関数を作ることを部分適用といいます。

<!-- output: lambda-calculus-run -->
```
(λx.x) 5 = 5
(λx.λy.x) 1 2 = 1
twice(increment)(5) = 7
add5(3) = 8
triple(100) = 300
curry(volume)(2)(3)(4) = 24
toInt(zero) = 0
toInt(succ(two)) = 3
toInt(plus(two, three)) = 5
toInt(times(two, three)) = 6
three(s -> s + "*")("") = ***
```
<!-- /output -->
<!-- claim: lambda-calculus-run -->

標準ライブラリには引数が3つの関数型インターフェイスがないので、`TriFunction`を自分で定義しています（標準にあるのは`Function`と`BiFunction`まで）。

### チャーチ数

ラムダ計算には数がありませんが、自然数nを「関数`f`をn回当てはめる関数」で表せます（チャーチ数）。
`0 = λf.λx.x`、`1 = λf.λx.f x`、`2 = λf.λx.f (f x)`です。
1を足す関数は`succ = λn.λf.λx.f (n f x)`、足し算は`plus = λm.λn.λf.λx.m f (n f x)`、掛け算は`times = λm.λn.λf.m (n f)`と書けます。
上の出力の`toInt`は、チャーチ数に「1を足す関数」と0を渡して、`int`の値に直しています。
同じチャーチ数`three`に、文字列に`*`を足す関数を渡すと`***`になります。

チャーチ数は、どんな型`T`の関数`T → T`にも使えなければなりません。
そこで`ChurchNumeral`は、型パラメーターを持つメソッド`<T> Function<T, T> apply(Function<T, T> f)`のインターフェイスにしています。
このような抽象メソッドは、ラムダ式では実装できません。
JLSの15.27.3は、ラムダ式が当てはまる関数の型に型パラメーターがないことを条件にしています。
`verify/compile-errors/GenericLambda.java`のように`ChurchNumeral ZERO = f -> x -> x;`と書くと、次のエラーになります。

<!-- output: church-generic-lambda -->
```
error: incompatible types: invalid functional descriptor for lambda expression
method (Function<T,T>)Function<T,T> in interface ChurchNumeral is generic
```
<!-- /output -->
<!-- claim: church-generic-lambda -->

そのため`LambdaCalculus.java`では、チャーチ数を匿名クラスで書いています。

## 4. Javaのラムダ式の実体

Javaのラムダ式は、ラムダ計算の`λ`をそのまま実行するしくみではありません。
ラムダ式は、関数型インターフェイス（抽象メソッドが1つだけのインターフェイス）を実装したオブジェクトになります。
`verify/LambdaBytecode.java`を`javap -c -p`で見ると、その作り方がわかります。

<!-- output: lambda-indy -->
```
static java.util.function.IntUnaryOperator noCapture();
0: invokedynamic // InvokeDynamic #0:applyAsInt:()Ljava/util/function/IntUnaryOperator;
static java.util.function.IntUnaryOperator capture(int);
1: invokedynamic // InvokeDynamic #1:applyAsInt:(I)Ljava/util/function/IntUnaryOperator;
static java.util.function.BinaryOperator<java.lang.Integer> methodRef();
0: invokedynamic // InvokeDynamic #2:apply:()Ljava/util/function/BinaryOperator;
static java.util.function.Supplier<java.lang.String> anonymous();
0: new // class LambdaBytecode$1
private static int lambda$capture$0(int, int);
private static int lambda$noCapture$0(int);
```
<!-- /output -->

- ラムダ式`x -> x * 2`の本体は、同じクラスの`private static`メソッド`lambda$noCapture$0`になる
- 外の変数`factor`を取り込むラムダ式`x -> x * factor`の本体は、取り込んだ値を最初の引数として受け取るメソッド`lambda$capture$0(int, int)`になる
- ラムダ式とメソッド参照を評価する箇所は`invokedynamic`命令になる。取り込む値は、その命令の引数として渡す
- 匿名クラスは、`LambdaBytecode$1`という別のクラスファイルになり、`new`で作る
<!-- claim: lambda-indy -->

`invokedynamic`命令が最初に実行されるとき、JVMはブートストラップメソッドを呼んで、呼び出し先を決めます。
`javap -v -p`で見ると、ブートストラップメソッドは`LambdaMetafactory.metafactory`で、その引数に本体のメソッドを指す`MethodHandle`が渡されています。
メソッド参照`Integer::sum`には本体のメソッドを作らず、`Integer.sum`を直接指します。
本体のメソッドには、ソースコードにないことを表す`ACC_SYNTHETIC`の印が付いています。

<!-- output: lambda-metafactory -->
```
REF_invokeStatic LambdaBytecode.lambda$capture$0:(II)I
REF_invokeStatic LambdaBytecode.lambda$noCapture$0:(I)I
REF_invokeStatic java/lang/Integer.sum:(II)I
REF_invokeStatic java/lang/invoke/LambdaMetafactory.metafactory
flags: (0x100a) ACC_PRIVATE, ACC_STATIC, ACC_SYNTHETIC
```
<!-- /output -->
<!-- claim: lambda-metafactory -->

実行すると、ラムダ式とメソッド参照のオブジェクトのクラスは、実行時に作られた隠しクラス（hidden class）で、名前に`$$Lambda`を含みます。
匿名クラスは、ふつうのクラスです。

<!-- output: lambda-hidden-class -->
```
ラムダ式: hidden=true synthetic=true anonymous=false name=LambdaBytecode$$Lambda/0x…
メソッド参照: hidden=true synthetic=true anonymous=false name=LambdaBytecode$$Lambda/0x…
匿名クラス: hidden=false synthetic=false anonymous=true name=LambdaBytecode$1
```
<!-- /output -->
<!-- claim: lambda-hidden-class -->

ここまでのクラスの作り方は、JDKの実装（`LambdaMetafactory`）が決めていることです。
JLSの15.27.4は、ラムダ式を評価すると、新しいインスタンスを作るか既存のインスタンスを使うかのどちらかになると述べるだけで、クラスの作り方やインスタンスを使い回すかどうかは実装に任せています。
第11章のサンプルコード11-23で、取り込みのないラムダ式が同じインスタンスになるのは、OpenJDKの実装の動作です。
<!-- claim: jls-lambda-evaluation -->

## 5. 圏論の基礎

圏論は、数学のさまざまな構造を、対象と、対象の間の矢印（射）で捉える理論です。
Samuel EilenbergとSaunders Mac Laneが1945年の論文で、関手（ファンクター）と自然変換を定義する準備として圏を導入しました。
<!-- claim: category-def -->

### 圏の定義

圏は、次のものからなります。

1. 対象の集まり
2. 対象`A`から対象`B`への射`f: A → B`の集まり
3. 射の合成
    + `f: A → B`と`g: B → C`から、射`g ∘ f: A → C`を作る
4. 恒等射
    + 対象`A`ごとに、射`id_A: A → A`がある

そのうえで、次の2つの法則が成り立つ必要があります。

- 結合法則
  - `h ∘ (g ∘ f) = (h ∘ g) ∘ f`
- 単位法則
  - `f: A → B`について、`id_B ∘ f = f`かつ`f ∘ id_A = f`
<!-- claim: category-def -->

### Javaの型と関数は、圏に近いが圏ではない

Javaの型を対象、`Function<A, B>`を射、`andThen`・`compose`を合成、`Function.identity()`を恒等射と見ると、圏に似た構造になります。
`verify/Composition.java`は、2つの法則を、いくつかの入力で確かめます。

<!-- output: category-laws-sampled -->
```
結合法則（入力 [-3, 0, 3, 100]）: true
単位法則（入力 [-3, 0, 3, 100]）: true
((3 + 1) * 2)^2 = 64
f.equals(f.andThen(id)) = false
f.equals(f) = true
```
<!-- /output -->
<!-- claim: category-laws-sampled -->

ただし、この対応は厳密ではありません。

- 2つの関数が等しいとは「すべての入力で同じ値を返す」ことだが、Javaでそれを確かめる方法はない。上の観察は、選んだ入力で値が一致することを示すだけである
- `Function`のオブジェクトの`equals`は、同じオブジェクトかどうかしか比べない。`f`と`f.andThen(id)`は、どの入力でも同じ値を返すのに`equals`では等しくない
- Javaの関数には、例外を投げるもの・終わらないもの・`null`を返すもの・外の状態を変えるものがある。数学の関数（どの入力にも値を1つ決める対応）とは違う
- 副作用を持つ関数では、返す値が同じでも、出力などの振る舞いまで同じとは限らない。値だけを比べる観察では、この違いを見逃す

このため、以下で「ファンクター」「モナド」と呼ぶものは近似です。
副作用がなく、`null`を返さず、例外を投げない関数だけを使うときに、法則が成り立ちます。
どこで法則が崩れるかを知っておくと、`Optional`や`Stream`の振る舞いを正しく予想できます。

## 6. ファンクター

数学のファンクター（関手）`F`は、圏から圏への対応です。
対象`A`を対象`F(A)`に、射`f: A → B`を射`F(f): F(A) → F(B)`に移し、恒等射と合成を保ちます。
<!-- claim: category-def -->

プログラミングでは、型を包む型（`Optional<T>`、`List<T>`など）と、中の値に関数を当てはめる操作`map`の組をファンクターと呼びます。
`map`は次の2つの法則を満たす必要があります（Haskellの言語仕様の書き方では`fmap id = id`、`fmap (f . g) = fmap f . fmap g`）。

- 恒等則
  - `m.map(x -> x)`は`m`と等しい
- 合成則
  - `m.map(g).map(f)`は`m.map(g.andThen(f))`と等しい
<!-- claim: haskell-laws -->

### 観察する

`verify/FunctorLaws.java`は、`Optional`と`Stream`の`map`で法則を確かめます。

<!-- output: optional-functor-laws -->
```
Optional[3]: 恒等則 true、合成則 true
Optional.empty: 恒等則 true、合成則 true
List: 恒等則 true、合成則 true
```
<!-- /output -->
<!-- claim: optional-functor-laws -->

`null`を返さない関数なら、`Optional.map`は両方の法則を満たします。
ところが、`null`を返す関数`g`（`x -> null`）と、`null`を受け取って文字列を返す関数`f`を使うと、合成則が崩れます。

<!-- output: optional-null-composition -->
```
Optional.map: m.map(g).map(f) = Optional.empty
Optional.map: m.map(g.andThen(f)) = Optional[nullでした]
Optional.map: 合成則 false
```
<!-- /output -->

`Optional.map`は、関数が`null`を返すと空の`Optional`を返します（APIドキュメントに書かれた仕様）。
そのため`m.map(g)`の時点で空になり、`f`は呼ばれません。
一方、`g.andThen(f)`は`f`に`null`を渡すので、`"nullでした"`が返ります。
<!-- claim: optional-null-composition -->

`Stream`は要素に`null`を持てるので、同じ関数でも合成則が成り立ちます。

<!-- output: stream-null-composition -->
```
Stream.map: [nullでした] / [nullでした] 合成則 true
```
<!-- /output -->
<!-- claim: stream-null-composition -->

`Stream`のオブジェクトどうしは比べられないので、上の観察は`toList()`で集めたリストを比べています。
また、1つの`Stream`には中間操作か終端操作を一度しか行えません。
`s.map(...)`を呼んだあとで、同じ`s`にもう一度操作すると例外になります。

<!-- output: stream-single-use -->
```
Stream.equals: false
2回目の操作: IllegalStateException: stream has already been operated upon or closed
```
<!-- /output -->
<!-- claim: stream-single-use -->

### Javaでは「ファンクター」という型を書けない

Haskellでは、「`map`を持つ型を包む型」をまとめて`Functor`という1つの型クラスで表せます。
Javaでこれを書こうとすると、型パラメーター`F`に型引数を付けて`F<A>`と書く必要があります。
しかしJavaの型パラメーターは型引数を取れないので（高カインド型がない）、`verify/compile-errors/HigherKinded.java`はコンパイルエラーになります。

<!-- output: no-hkt -->
```
error: unexpected type
required: class
found: type parameter F
```
<!-- /output -->
<!-- claim: no-hkt -->

そのため`Optional`・`Stream`・`CompletableFuture`は、それぞれが同じ形の`map`（`CompletableFuture`では`thenApply`）を別々に持っています。
「`map`を持つ型ならどれでも使える」メソッドは、Javaでは書けません。

### List<Optional<T>>を裏返す

包む型が2重になったとき、内と外を入れ替えたいことがあります。
`verify/Sequence.java`の`sequence`は、`List<Optional<T>>`を`Optional<List<T>>`に変えます。
1つでも空があれば全体が空になるので、「入力をすべて数に直せたときだけ、数のリストを得る」処理に使えます。

<!-- output: sequence-run -->
```
Optional[[1, 2, 3]]
Optional.empty
Optional[[10, 20, 30]]
Optional.empty
```
<!-- /output -->
<!-- claim: sequence-run -->

関数型の言語ではこの操作を`sequence`（関数を当てはめながら行うものを`traverse`）と呼びます。
Javaの標準ライブラリにはないので、自分で書きます。

## 7. モナド

モナドは、ファンクターに次の2つの操作を加えたものです。

1. unit（`return`、`pure`とも呼ぶ）
    + 値`a`を包む。`Optional`では`Optional.of`、`Stream`では`Stream.of`
2. bind（`>>=`）
    + 包まれた値に「値を受け取って包まれた値を返す関数」を当てはめ、結果を平らにする。Javaでは`flatMap`

unitとbindは、次の3つの法則を満たす必要があります（Haskellの言語仕様にある法則を、Javaの書き方にしたもの）。

1. 左単位元則
    + `unit(a).flatMap(f)`は`f(a)`と等しい
2. 右単位元則
    + `m.flatMap(x -> unit(x))`は`m`と等しい
3. 結合則
    + `m.flatMap(f).flatMap(g)`は`m.flatMap(x -> f(x).flatMap(g))`と等しい
<!-- claim: haskell-laws -->

関数型プログラミングでモナドが使われるようになったのは、Eugenio Moggiがプログラミング言語の意味論の構成にモナドを使い、Philip Wadlerがその方法を関数型プログラムの構成に応用してからです。
Wadlerの講義録「Monads for functional programming」（1995年、Lecture Notes in Computer Science 925）は、状態・例外・出力などの効果をモナドで表す例と、モナドで作るパーサーを示しています。
<!-- claim: wadler-moggi -->

### Optionalで確かめる

`verify/MonadLaws.java`は、`Optional`で3つの法則を確かめます。
結合則は、途中で空になる関数（偶数なら半分にし、奇数なら空を返す`half`）でも確かめています。

<!-- output: optional-monad-laws -->
```
左単位元則: true
右単位元則 Optional[20]: true
右単位元則 Optional.empty: true
結合則 a=5: Optional[3] / Optional[3] true
結合則 a=6: Optional.empty / Optional.empty true
```
<!-- /output -->
<!-- claim: optional-monad-laws -->

これらは`null`を使わない場合の結果です。
`null`が絡むと、次のようになります。

<!-- output: optional-null-monad -->
```
ofNullable(null).flatMap(describe) = Optional.empty
describe.apply(null) = Optional[nullを受け取った]
Optional.of(null): NullPointerException
Optional.flatMap(x -> null): NullPointerException
```
<!-- /output -->

- unitを`Optional.ofNullable`にすると、`a`が`null`のとき左単位元則が崩れる。`ofNullable(null)`は空になり、`describe`は呼ばれない
- unitを`Optional.of`にすると、`null`はそもそも包めない（`NullPointerException`）
- `flatMap`に渡した関数が`null`を返すと、`NullPointerException`になる（APIドキュメントに書かれた仕様）
<!-- claim: optional-null-monad -->

つまり`Optional`は、「値は`null`でなく、関数も`null`を返さない」という前提のもとでモナドとして振る舞います。

### Streamで確かめる

`Stream`では、`Stream.of`がunit、`flatMap`がbindです。

<!-- output: stream-monad-laws -->
```
Stream 左単位元則: true
Stream 右単位元則: true
Stream 結合則: [2, 4] / [2, 4] true
Stream.flatMap(x -> null): []
```
<!-- /output -->

`Stream.flatMap`は、関数が`null`を返すと、`Optional.flatMap`のように例外を投げず、空のストリームとして扱います（APIドキュメントに書かれた仕様）。
<!-- claim: stream-monad-laws -->

`flatMap`・`filter`・`map`をつなぐと、次のようになります。
1〜5の各要素とその2乗を並べた列（`1, 1, 2, 4, 3, 9, 4, 16, 5, 25`）から偶数を選び、10倍します。
2の2乗の4と、元の要素の4の両方が残ります。
<!-- claim: stream-chain -->

<!-- output: stream-chain -->
```
連鎖: [20, 40, 40, 160]
```
<!-- /output -->
<!-- claim: stream-chain -->

### 自分で作るResult型

`verify/ResultMonad.java`は、成功（`Success`）か失敗（`Failure`）かを表す`Result`型を、sealedインターフェイスとレコードで作ります。
sealedで実装を2つに限っているので、`switch`で両方を網羅しているかをコンパイラーが確かめます（第8章）。
`flatMap`は、成功なら関数を当てはめ、失敗ならそのまま失敗を伝えます。

<!-- output: result-run -->
```
20 / 4 → sqrt → *2: Success: 4.47213595499958
20 / 0 → sqrt → *2: Failure: Division by zero
-20 / 4 → sqrt → *2: Failure: Cannot take square root of negative number
```
<!-- /output -->
<!-- claim: result-run -->

`20 / 4 = 5`、`√5 × 2 = 4.47213595499958`です。
途中で失敗すると、残りの関数は呼ばれず、最初の失敗の理由が最後まで伝わります。

この`flatMap`は、関数が投げた実行時例外も失敗に変えています。
便利ですが、そのせいで左単位元則が崩れます。

<!-- output: result-left-identity-exception -->
```
of("12").flatMap(parse) = Success: 12
parse.apply("12") = Success: 12
of("x").flatMap(parse) = Failure: NumberFormatException
parse.apply("x") は例外を投げる: NumberFormatException
```
<!-- /output -->

`of("x").flatMap(parse)`は失敗の値を返しますが、`parse.apply("x")`は例外を投げるので、両者は等しくありません。
法則を守るなら、例外を失敗に変える処理は`flatMap`の外（`parse`のなか）に置きます。
<!-- claim: result-left-identity-exception -->

## 8. 末尾呼び出しと再帰

関数型の言語では、繰り返しを再帰で書くことがよくあります。
再帰呼び出しの結果をそのまま返す（呼び出しのあとに計算が残らない）形を末尾再帰といいます。
言語によっては、末尾再帰をループと同じように、スタックを積まずに実行します（末尾呼び出しの最適化）。

Javaでは、末尾再帰で書いてもスタックを使い切ります。
`verify/TailCall.java`の`sumTo`は末尾再帰で1からnまでの和を求めます。

<!-- output: tail-call-overflow -->
```
末尾再帰 n=1000: 500500
末尾再帰 n=10000000: StackOverflowError
ループ n=10000000: 50000005000000
Stream n=10000000: 50000005000000
```
<!-- /output -->
<!-- claim: tail-call-overflow -->

`javac`は末尾再帰をループに書き換えません。
`javap -c -p`で見ると、`sumTo`の最後は自分自身の呼び出し（`invokestatic`）と、その結果を返す`lreturn`のままです。

<!-- output: tail-call-bytecode -->
```
14: invokestatic // Method sumTo:(JJ)J
17: lreturn
```
<!-- /output -->
<!-- claim: tail-call-bytecode -->

Javaで深い繰り返しを書くときは、ループか`Stream`を使います。
スタックの深さは`-Xss`（スレッドのスタックの大きさ）や実行環境で変わるので、再帰がどの深さで失敗するかに頼る書き方は避けます。

## 9. 評価の順序と遅延評価

Javaは、メソッドを呼ぶ前に引数をすべて評価します（JLSの15.12.4.2）。
そのため、`Optional.orElse(式)`の式は、値があって使われないときでも評価されます。
評価を必要になるまで遅らせるには、値の代わりに「値を作る関数」（`Supplier`）を渡します。
`orElseGet`は、値がないときだけ`Supplier`を呼びます。
`verify/Laziness.java`で確かめます。

<!-- output: eager-orelse -->
```
orElse（引数を先に評価する）:
代わりの値A を計算した
orElseGet（必要になったときだけSupplierを呼ぶ）:
```
<!-- /output -->
<!-- claim: eager-orelse -->

### 1回だけ計算する遅延値

`Lazy`クラスは、`Supplier`を持っておき、最初に`get`したときだけ計算して結果を覚えます。
`map`で作った`Lazy`も、`get`するまで元の計算を始めません。

<!-- output: lazy-value -->
```
Lazy:
Lazyを作った
重い値 を計算した
length = 3
lazy = 重い値（2回目は計算しない）
```
<!-- /output -->
<!-- claim: lazy-value -->

この`Lazy`は1つのスレッドから使う前提です。
複数のスレッドから使うなら、`get`を`synchronized`にするなどの対策が要ります（第15章）。

### Streamの中間操作は遅延する

`Stream`の中間操作（`filter`・`map`・`peek`など）は、終端操作（`toList`など）を呼ぶまで何もしません。
終端操作を呼ぶと、要素を1つずつパイプラインに通し、`limit`などで結果が決まった時点で残りの要素を処理しません。

<!-- output: stream-lazy -->
```
Stream（終端操作がないと何もしない）:
パイプラインを作った
peek 1
peek 2
結果 [2]
```
<!-- /output -->

パイプラインを作った時点では`peek`は何も表示せず、最初の偶数2が見つかった時点で3と4は処理されていません。
<!-- claim: stream-lazy -->

遅延するので、無限に続く列も、必要な分だけ取り出せます。
`LazyList`は、先頭の要素と「残りを作る方法」（`Lazy`）の組で無限のリストを表し、フィボナッチ数列を作ります。

<!-- output: infinite-stream -->
```
無限のストリーム:
[1, 2, 4, 8, 16, 32, 64, 128, 256, 512]
[0, 1, 1, 2, 3, 5, 8, 13, 21, 34]
```
<!-- /output -->
<!-- claim: infinite-stream -->

## 10. 高階関数とメモ化

関数を返す関数や、関数を受け取る関数を高階関数といいます。
`verify/Memoize.java`の`memoize`は、関数を受け取り、結果を覚える関数を返します。
同じ引数で2回呼ぶと、2回目は元の関数を呼びません。

<!-- output: memoize-run -->
```
multiplier(3).apply(5) = 15
Computing for 5
125
125
[2, 4]
```
<!-- /output -->
<!-- claim: memoize-run -->

メモ化は、副作用がなく同じ引数に同じ値を返す関数でなければ、意味が変わります。
上の`cube`は「Computing for 5」と表示する副作用を持つので、メモ化すると表示が1回に減ります。

### computeIfAbsentを再帰で使わない

メモ化した関数のなかから、同じ表に対して再帰的に`computeIfAbsent`を呼ぶ書き方（フィボナッチ数など）は動きません。
`ConcurrentHashMap`は`IllegalStateException`（Recursive update）を、`HashMap`は`ConcurrentModificationException`を投げます。

<!-- output: compute-recursive -->
```
ConcurrentHashMap: java.lang.IllegalStateException: Recursive update
HashMap: java.util.ConcurrentModificationException
表を埋める: fib(30) = 832040
```
<!-- /output -->

どちらのクラスのAPIドキュメントも、`computeIfAbsent`に渡す関数のなかでそのマップを変更してはいけないと書いています。
再帰の計算をメモ化するときは、小さいほうから順に表を埋めるか、`computeIfAbsent`を使わずに`get`と`put`で書きます。
<!-- claim: compute-recursive -->

## 11. 関数型の技法の応用

### パーサーコンビネーター

`verify/ParserCombinator.java`は、パーサーを「文字列を受け取り、成功なら（解析した値, 残りの文字列）を、失敗なら空を返す関数」として表します。
小さなパーサー（1文字、決まった文字列、1桁の数字）を、`or`・`and`・`many`で組み合わせて大きなパーサーを作ります。
`Parser`の`map`と`flatMap`は、パーサーをファンクター・モナドとして扱うためのもので、Wadlerの講義録の第5節と同じ考え方です。

<!-- output: parser-run -->
```
Optional[(a, bc)]
Optional[(b, cd)]
Optional.empty
Optional[(123, abc)]
Optional[((x=, 42), )]
Optional[(42,  rest)]
Optional.empty
```
<!-- /output -->
<!-- claim: parser-run -->

最後の2行は、`flatMap`で「数、`+`、数」の順に解析して足し算するパーサーの結果です。
`12+30 rest`は`42`と残り` rest`に、`+`のない`12-30`は失敗になります。

### 代数的データ型とパターンマッチング

`verify/ExpressionAdt.java`は、式の木を「定数・変数・足し算・掛け算のどれか」という型（代数的データ型）として、sealedインターフェイスとレコードで表します。
評価・簡約・表示は、レコードパターンを使った`switch`で書きます。

<!-- output: adt-run -->
```
Expression: ((x + 2.0) * (y + 3.0))
Evaluation: 70.0
Before: (0.0 + (x * 1.0))
After: x
equals: true
x * 0.0 (x = Infinity): NaN
```
<!-- /output -->
<!-- claim: adt-run -->

`x = 5`、`y = 7`のとき`(5 + 2) * (7 + 3) = 70`です。
レコードの`equals`は成分を比べるので、簡約した結果を`new Variable("x")`と比べられます。

簡約では「0を足す」「1を掛ける」を取り除いています。
「0を掛けると0」という簡約は入れていません。
`double`では、`x`が無限大のとき`x * 0.0`は`NaN`になるので、この簡約は式の意味を変えてしまいます。

sealedインターフェイスの実装をすべて網羅しない`switch`式は、コンパイルエラーになります（`verify/compile-errors/NonExhaustive.java`）。
型を1つ足したときに、処理を書き忘れた`switch`をコンパイラーが教えてくれます。

<!-- output: non-exhaustive -->
```
error: the switch expression does not cover all possible input values
```
<!-- /output -->
<!-- claim: non-exhaustive -->

### 副作用を値として扱う（IOモナド）

`verify/IOMonad.java`の`IO<T>`は、「実行すると`T`の値を返す手順」を表す値です。
`flatMap`で手順をつないでも、何も実行しません。
`unsafeRun()`を呼んだときに、つないだ順に実行します。

<!-- output: io-monad-run -->
```
--- 手順を組み立てた（まだ何も表示していない）
Welcome!
Enter a number:
Enter a number:
Sum: 7
--- getNumberは2回実行された（残りの入力 []）
IOが指すクラス: IOMonad$IO
```
<!-- /output -->
<!-- claim: io-monad-run -->

1つの`getNumber`を2回つなぐと、その手順は2回実行され、入力も2行読まれます。
`getNumber`は「読んだ値」ではなく「読む手順」だからです。
観察の結果が毎回同じになるように、入力の代わりに決まった行（3と4）を返す`readLine`を使っています。

入れ子のクラス`IO`は、`IOMonad`のなかでは`java.lang.IO`より優先されます（最後の行のとおり、`IO`は`IOMonad$IO`を指します）。
そのため、標準出力には`java.lang.IO.println`と完全修飾名で書いています。

Haskellでは、入出力をこのような値として組み立て、`main`だけがそれを実行します。
Javaの`IO<T>`は、その考え方をまねた簡易版です。
Javaでは、どのメソッドのなかでも副作用を起こせるので、言語が副作用の場所を限ってくれるわけではありません。

## 12. 設計パターンを関数で書く

オブジェクト指向の設計パターン（デザインパターン）の一部は、振る舞いをラムダ式やメソッド参照で渡すことで、振る舞いごとのクラスを作らずに書けます。
ここでは、第11章で学んだ関数型インターフェイス（`Consumer`・`Predicate`・`Function`・`Supplier`）を使って、よく知られた設計を書いた例を見ます。
どれも`verify/patterns/`にあり、補助のクラスを別のファイルに置いているものがあるので、まとめてコンパイルしてから実行します。

```bash
cd verify/patterns
javac -d out *.java
java -cp out EventProcessor
```

### イベントの型ごとにハンドラーを登録する

`verify/patterns/EventProcessor.java`は、イベントの型（`Class`）をキーにして、そのイベントを処理する`Consumer`の一覧を`Map`に持ちます。
`on`はイベントの型ごとにハンドラーを登録し、`publish`はそのイベントの型に登録されたハンドラーを順に呼び出します。
`onFiltered`は、条件を表す`Predicate`とハンドラーを組み合わせた新しいラムダ式を作り、`on`で登録します。

<!-- output: pattern-event -->
```
注文を受け付けました: A001
注文を受け付けました: A002
高額の注文です: A002
```
<!-- /output -->
<!-- claim: pattern-event -->

金額が10000円を超えるA002のときだけ、条件付きの2つ目のハンドラーが処理をします。
`OrderCreated.class`は、クラスを表すオブジェクトです。
`computeIfAbsent`は、キーに対応する一覧がまだなければ新しく作って入れ、その一覧を返します。

### ミドルウェアを関数の合成でつなぐ

Webのサーバーは、届いたリクエストを本来の処理に渡す前に、いくつかの検査を順に行います。
たとえば、記録を取る処理や、短い時間に届きすぎていないかを確かめる処理、利用者を認証する処理です。
このような、本来の処理の前に挟む処理をミドルウェアと呼びます。
`verify/patterns/MiddlewareChain.java`は、検査をラムダ式で書き、`then`でつなぎます（`Request`などの補助の型は`Support.java`にあります）。

<!-- output: pattern-middleware -->
```
[INFO] Request: GET /orders
200 注文一覧（ユーザー: alice）
[INFO] Request: GET /orders
401 Unauthorized
[INFO] Request: GET /orders
200 注文一覧（ユーザー: alice）
[INFO] Request: GET /orders
429 Too Many Requests
```
<!-- /output -->
<!-- claim: pattern-middleware -->

`then`でつないだ処理は、前の検査が成功したときだけ次の検査に進み、失敗したらその時点の応答を返します。
2回目は認証に失敗した応答（401）、4回目は回数の上限を超えた応答（429）で止まっています。
`Middleware`は`Function`を継承しているので、`andThen`という名前のメソッドをすでに持っています。
同じ名前で合成のメソッドを定義すると、ラムダ式を渡したときにどちらを呼ぶのかが決まらずコンパイルエラーになるので、`then`という名前にしています。

### テンプレートメソッドを関数で渡す

テンプレートメソッドパターンは、処理の骨格を親クラスで決め、各段階の処理をサブクラスに任せる設計です（第5章・第7章）。
`verify/patterns/DataProcessingTemplate.java`の`processData`は、読み込み・変換・後処理の順序だけを決め、各段階で何をするかは呼び出す側がラムダ式やメソッド参照で渡します。
`processCsvData("  Alice,30;Bob,-1;Carol,25  ")`を呼び出すと、年齢が負のBobを除いたリストが返ります。

<!-- output: pattern-template -->
```
[Customer[name=Alice, age=30], Customer[name=Carol, age=25]]
```
<!-- /output -->
<!-- claim: pattern-template -->

継承を使う書き方と違い、処理の組み合わせごとにサブクラスを作らずに済みます。
後処理のラムダ式は`removeIf`でリストを書き換えていますが、このリストは`parseCustomers`が新しく作ったものなので、ほかの処理に影響しません。

### イベントソーシング

イベントソーシングは、口座の残高のような現在の状態をそのまま記録するのではなく、「口座を作った」「入金した」「出金した」といった出来事（イベント）の列を記録する設計です。
現在の状態が必要になったら、記録したイベントを順に適用して求めます。
`verify/patterns/EventSourcedAccount.java`は、イベントをsealedインターフェイスとレコードで表し、`project`で空の状態から古い順に適用します（第8章のレコードパターンを使ったswitch）。

<!-- output: pattern-event-sourcing -->
```
2件目まで: AccountState[id=AC001, owner=田中, balance=Money[amount=10000]]
すべて: AccountState[id=AC001, owner=田中, balance=Money[amount=7500]]
```
<!-- /output -->
<!-- claim: pattern-event-sourcing -->

途中までのイベントだけを適用すれば、その時点の状態も求められます。
適用の繰り返しは、第12章で学ぶStreamの`reduce`を使うと1つの式で書けます。

### コンストラクター参照で作るファクトリ

ファクトリは、オブジェクトを生成する処理を1か所にまとめ、呼び出す側が具体的なクラスを知らなくても済むようにする設計です。
`verify/patterns/ProcessorFactory.java`は、形式の名前と、生成する処理（`Supplier`に入れたコンストラクター参照）を`Map`に対応づけます。

<!-- output: pattern-factory -->
```
CSVとして読み込み: a,b,c
JSONとして読み込み: {"a": 1}
エラー: 未対応の形式: yaml
```
<!-- /output -->
<!-- claim: pattern-factory -->

`Map`に入れた時点ではオブジェクトは作られず、`supplier.get()`を呼び出したときにはじめてコンストラクターが実行されます。
新しい形式に対応するときは、`Map`に1行を足すだけで済みます。

### 操作をためておくビルダー

`verify/patterns/FunctionalBuilder.java`の`with`は、設定の操作（`Consumer<Person>`）をリストにためておくだけで、`build`を呼んだときに、作った`Person`へ操作を加えた順に適用します。

<!-- output: pattern-builder -->
```
Person{name='Alice', age=30, adult=true}
```
<!-- /output -->
<!-- claim: pattern-builder -->

3つ目の操作は、先に設定した年齢を見て`adult`を決めています。
第6章のビルダーは不変オブジェクトを組み立てるためのものでした。
この関数型のビルダーは、setterを持つ可変のオブジェクトに操作を順に加える点が異なります。

`verify/patterns/ConfigurationBuilder.java`の`whenCondition`は、条件が成り立つときだけ設定の操作を加えます。
実行する環境の名前によって、データベースの設定を変えます。

<!-- output: pattern-config -->
```
development: localhost:5432/app_db (SSL: false)
test: localhost:5432/test_db (SSL: false)
production: db.example.com:5432/app_db (SSL: true)
```
<!-- /output -->
<!-- claim: pattern-config -->

productionでは、既定の`localhost`のあとに`db.example.com`を設定する操作が加わるので、あとから加えた操作が優先されます。

## よくある誤解

- `Optional`は、どんな関数でもファンクター・モナドの法則を満たす
  - `null`を返す関数を`map`に渡すと合成則が崩れ、`flatMap`に渡すと例外になる（6・7）
  <!-- claim: optional-null-composition, optional-null-monad -->
- 法則を確かめるには、`Function`や`Stream`を`equals`で比べればよい
  - `Function`の`equals`は同じオブジェクトかしか比べず、`Stream`は比べられないうえ一度しか使えない。関数の値やリストに直して比べる（5・6）
  <!-- claim: category-laws-sampled, stream-single-use -->
- ラムダ式は匿名クラスの短い書き方である
  - 匿名クラスは別のクラスファイルになるが、ラムダ式は同じクラスの`private`メソッドと`invokedynamic`になり、実行時に隠しクラスが作られる（4）
  <!-- claim: lambda-indy, lambda-hidden-class -->
- ラムダ式から外の変数を変えられないのは、Javaが関数型だからである
  - 実質的にfinalの規則は匿名クラスにもあり、JLSは並行処理の問題を避けることを理由に挙げている（2）
  <!-- claim: capture-effectively-final, anonymous-effectively-final -->
- 末尾再帰で書けば、深い再帰でもスタックがあふれない
  - `javac`もJVMも末尾呼び出しを最適化しない。深い繰り返しはループか`Stream`で書く（8）
  <!-- claim: tail-call-overflow, tail-call-bytecode -->
- `orElse`に渡した式は、値がないときだけ評価される
  - 引数は呼び出しの前に評価される。遅らせたいときは`orElseGet`に`Supplier`を渡す（9）
  <!-- claim: eager-orelse -->
- メモ化は`computeIfAbsent`のなかで再帰すれば書ける
  - `ConcurrentHashMap`も`HashMap`も例外を投げる（10）
  <!-- claim: compute-recursive -->

## 参考資料

- Java言語仕様（Java SE 25）
  - Chapter 1 Introduction（1.7 References）
  - 4.3.1「Objects」
  - 14.11.1.1「Exhaustive Switch Blocks」
  - 15.12.4.2「Evaluate Arguments」
  - 15.27.2「Lambda Body」、15.27.3「Type of a Lambda Expression」、15.27.4「Run-Time Evaluation of Lambda Expressions」
  - `https://docs.oracle.com/javase/specs/jls/se25/html/`
- Java SE 25のAPI
  - `Function`、`Optional`、`Stream`（パッケージ`java.util.stream`の説明を含む）、`ConcurrentHashMap.computeIfAbsent`、`HashMap.computeIfAbsent`、`LambdaMetafactory`、`Class.isHidden`
  - `https://docs.oracle.com/en/java/javase/25/docs/api/java.base/module-summary.html`
- JEP 126: Lambda Expressions & Virtual Extension Methods（Java 8）
  - `https://openjdk.org/jeps/126`
- JEP 395: Records（Java 16）、JEP 409: Sealed Classes（Java 17）、JEP 440: Record Patterns・JEP 441: Pattern Matching for switch（Java 21）、JEP 456: Unnamed Variables & Patterns（Java 22）
  - `https://openjdk.org/jeps/395`ほか
- Haskell 2010 Language Report、6.3「Standard Haskell Classes」（ファンクターとモナドの法則）
  - `https://www.haskell.org/onlinereport/haskell2010/haskellch6.html`
- Peter Naur, Brian Randell（編）「Software Engineering: Report on a conference sponsored by the NATO Science Committee」（1969年）
  - `http://homepages.cs.ncl.ac.uk/brian.randell/NATO/nato1968.PDF`
- Edsger W. Dijkstra「Go To Statement Considered Harmful」、Communications of the ACM 11(3)、1968年（原稿EWD215の翻刻）
  - `https://www.cs.utexas.edu/~EWD/transcriptions/EWD02xx/EWD215.html`
- John McCarthy「Recursive Functions of Symbolic Expressions and Their Computation by Machine, Part I」、Communications of the ACM、1960年
  - `http://www-formal.stanford.edu/jmc/recursive.html`
- Philip Wadler「Monads for functional programming」、Advanced Functional Programming（LNCS 925）、1995年
  - `https://homepages.inf.ed.ac.uk/wadler/papers/marktoberdorf/baastad.pdf`
- オスロ大学「Ole-Johan Dahl」（Simulaの開発の経緯）
  - `https://www.mn.uio.no/ifi/english/about/ole-johan-dahl/`
- Stanford Encyclopedia of Philosophy「Category Theory」（圏・関手の定義と歴史）
  - `https://plato.stanford.edu/entries/category-theory/`
