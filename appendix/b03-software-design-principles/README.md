# 付録B.03 ソフトウェア設計原則とデザインパターン

第3〜7章で学んだクラス・継承・インターフェイスの設計の考え方を、SOLID原則とデザインパターンとして整理し、Javaの言語と標準ライブラリのなかに実例を探して確かめる節です。
原則は「守れば必ずよくなる規則」ではありません。
どんな変更に強くなり、代わりに何を払うのかを、実際のコードの振る舞いで見ていきます。
統合プロジェクトの設計に原則を当てはめる判断は、本文の第25章「統合プロジェクトの設計」で扱います。

## この節の読み方

この節の解説のうち、事実を述べた段落には`<!-- claim: … -->`という印を付けています（GitHubの画面では見えません）。
印の名前は同じディレクトリの`claims.yml`に対応し、そこにその主張の確かめ方（プログラムの実行、バイトコードの照合、コンパイルエラーの照合、出典）を書いています。
リポジトリのルートで`npm run check:deep-dive -- b03`を実行すると、すべての主張をJDK 25で確かめ直せます。
「観察する」の欄に載せた出力は、この検査が実際に出したものです。

設計原則の多くは、機械では確かめられない考え方です。
この節では、原則を「Javaの言語や標準ライブラリがどうなっているか」で裏づけられる箇所だけを観察にし、原則の言葉や出どころは、確かめられた出典がある範囲だけを書きます。

## ファイル構成

### 観察用のプログラム（verify/）

| ファイル | 観察すること |
|---|---|
| `DateResponsibilities.java` | `java.util.Date`の非推奨のメソッドに残る、責任の分割の跡 |
| `OcpDependencies.java` | 計算するクラスがどの型に依存しているか（`javap`で見る） |
| `LspCollections.java` | `List`型の変数に足せるとは限らないこと（optional operation） |
| `LspTimestamp.java` | `Timestamp`と`Date`の`equals`が対称にならないこと |
| `StackInheritance.java` | `Stack`が`Vector`から引き継いだ操作 |
| `IspFunctional.java` | `java.util.function`のインターフェイスの大きさ |
| `IspLanguage.java` | 拡張for文とtry-with-resources文が求めるインターフェイス |
| `DipServiceLoader.java` | `ServiceLoader`で実装を後から差し込むしくみ |
| `CreationalInJdk.java` | 標準ライブラリのなかの生成のパターン |
| `StructuralInJdk.java` | 標準ライブラリのなかの構造のパターン |
| `BehavioralInJdk.java` | 標準ライブラリのなかの振る舞いのパターン |
| `SingletonVariants.java` | シングルトンの3つの書き方の違い |
| `compile-errors/` | コンパイラーが拒む書き方（コンパイルが失敗するのが正しいファイル） |

`verify/`のプログラムはパッケージを持たないので、`verify/`ディレクトリでそのままコンパイル・実行できます。

```bash
cd verify
javac LspTimestamp.java && java LspTimestamp
javac OcpDependencies.java && javap -c -p 'OcpDependencies$ShapeCalculator'
javac compile-errors/SealedSwitchMissing.java   # エラーになるのが正しい
```

### 体験用のデモ（src/）

- `SolidPrinciplesDemo.java`
  - 5つの原則それぞれについて、原則に反する書き方と、原則に沿った書き方を並べて動かす
- `DesignPatternsDemo.java`
  - シングルトン・ファクトリ・抽象ファクトリ・アダプター・デコレーター・オブザーバー・ストラテジー・コマンドを、小さな例で動かす
  - ファクトリの例は、種類を表す文字列を受け取って生成する形で、Simple Factoryと呼ばれることがある。GoFの本のFactory Methodは、生成するメソッドをサブクラスでオーバーライドする形で、これとは別のものである

```bash
javac -d out src/main/java/com/example/design/*.java
java -cp out com.example.design.SolidPrinciplesDemo
java -cp out com.example.design.DesignPatternsDemo
```

## 1. 原則とパターンの出どころ

SOLIDは、単一責任（Single Responsibility）、開放閉鎖（Open-Closed）、リスコフの置換（Liskov Substitution）、インターフェイス分離（Interface Segregation）、依存関係逆転（Dependency Inversion）の5つの原則の頭文字です（第4章の脚注）。
ロバート・C・マーチンは2020年の記事で、5つの原則を次のように短く言い直しています。

- 単一責任の原則（SRP）
  - 同じ理由で変わるものを集め、違う理由で変わるものを分ける
- 開放閉鎖の原則（OCP）
  - モジュールは拡張に対して開き、変更に対して閉じる
- リスコフの置換原則（LSP）
  - インターフェイスを使うプログラムは、そのインターフェイスのどの実装を渡されても戸惑ってはならない
- インターフェイス分離の原則（ISP）
  - 使わないものに依存しなくて済むよう、インターフェイスを小さく保つ
- 依存関係逆転の原則（DIP）
  - 抽象の方向に依存する。上位のモジュールは下位の詳細に依存しない

同じ記事でマーチンは、LSPを継承の話だと考えたのは誤りで、インターフェイスの実装を含むサブタイプの話だと書いています。
<!-- claim: solid-martin-wording -->

原則の多くは、マーチンより前の考えを下敷きにしています。
単一責任の原則を説明した2014年の記事は、デイビッド・パルナスが1972年に発表したモジュール分割の論文から説き起こしています。
<!-- claim: srp-reason-to-change -->
開放閉鎖の原則は、バートランド・メイヤーが1988年の著書『Object-Oriented Software Construction』で示したものです。
<!-- claim: ocp-meyer -->

デザインパターンの「GoF」（Gang of Four）は、エリック・ガンマ、リチャード・ヘルム、ラルフ・ジョンソン、ジョン・ブリシディースの4人を指します。
4人の本『Design Patterns: Elements of Reusable Object-Oriented Software』（Addison-Wesley、1994年）は、23のパターンを生成・構造・振る舞いの3つの章に分けて載せています。
<!-- claim: gof-book -->

## 2. 単一責任の原則（変更の理由で分ける）

### しくみ

「1つのクラスは1つのことだけをする」と覚えると、「メソッドを1つにする」「機能を細かく分けるほどよい」と誤解しがちです。
マーチンの言い直しでは、責任とは「変更の理由」のことで、変更の理由は変更を求める人だとしています。
たとえば、画面の見た目を決める人と計算の規則を決める人は、別々の理由でコードの変更を求めます。
違う人の都合で変わるコードが1つのクラスに同居していると、片方の変更がもう片方を壊すおそれが生まれます。
<!-- claim: srp-reason-to-change -->

### 観察する

JDKのなかにも、責任を分けた跡が残っています。
`java.util.Date`は、もともと時刻の値を持つだけでなく、年・月・日などの欄の計算と、文字列との変換も引き受けていました。
`verify/DateResponsibilities.java`は、`Date`のpublicメソッドのうち非推奨のものを、リフレクションで数えて分類します。

<!-- output: date-split -->
```
Dateが宣言するpublicメソッド: 29
そのうち非推奨: 18
欄の計算（Calendarへ移った）: UTC getDate getDay getHours getMinutes getMonth getSeconds getTimezoneOffset getYear setDate setHours setMinutes setMonth setSeconds setYear
文字列との変換（DateFormatへ移った）: parse toGMTString toLocaleString
```
<!-- /output -->

JDK 1.1で、欄の計算は`Calendar`へ、文字列との変換は`DateFormat`へ移され、`Date`の該当するメソッドは非推奨になりました。
APIドキュメントの`Date`の説明にも、この経緯が書かれています。
<!-- claim: date-split -->

### 説明

欄の計算は暦とタイムゾーンの規則が変わると変わり、文字列の形式は表示する国や画面の都合で変わります。
変わる理由が違うものを別のクラスに分けたのが、JDK 1.1の変更です。
第6章で学んだ`java.time`は、これをさらに進め、時点（`Instant`）、日付（`LocalDate`）、書式（`DateTimeFormatter`）を別々のクラスにしています。

## 3. 開放閉鎖の原則（依存をバイトコードで見る）

### しくみ

「拡張に開き、変更に閉じる」とは、新しい種類を足すときにすでにあるクラスを書き換えずに済むということです。
そのためには、すでにあるクラスが具体的な種類の名前を知らず、抽象（インターフェイス）だけを知っている必要があります。
クラスが何を知っているかは、コンパイルしたバイトコードに現れます。

### 観察する

`verify/OcpDependencies.java`の`TypeCheckCalculator`は、`instanceof`で種類を調べて面積を計算します。
`javap -c -p`で見ると、具体的なクラスへの`instanceof`が並びます。

<!-- output: ocp-typecheck-bytecode -->
```
1: instanceof // class OcpDependencies$Rectangle
23: instanceof // class OcpDependencies$Circle
```
<!-- /output -->
<!-- claim: ocp-typecheck-bytecode -->

`ShapeCalculator`は、`Shape`インターフェイスの`area()`だけを呼びます。
バイトコードに現れるのは`Shape`だけで、`Rectangle`・`Circle`・`Triangle`の名前はどこにもありません。

<!-- output: ocp-interface-bytecode -->
```
27: checkcast // class OcpDependencies$Shape
35: invokeinterface // InterfaceMethod OcpDependencies$Shape.area:()D
```
<!-- /output -->
<!-- claim: ocp-interface-bytecode -->

あとから足した`Triangle`を渡すと、`ShapeCalculator`は書き換えずに計算でき、`TypeCheckCalculator`は例外を投げます。

<!-- output: ocp-run -->
```
ShapeCalculator: 42.1416
TypeCheckCalculator: 知らない図形: Triangle[base=6.0, height=8.0]
```
<!-- /output -->
<!-- claim: ocp-run -->

### 説明：閉じる方向を選ぶ

開放閉鎖の原則は、あらゆる変更に閉じることを求めているのではありません。
`Shape`インターフェイスで書くと、種類を足す変更には閉じますが、「すべての図形に周囲の長さを求める操作を足す」変更には閉じません（インターフェイスと全部の実装を書き換えます）。

第8章で学んだsealedインターフェイスとswitch式で書くと、逆になります。
操作を足すのはswitch式を1つ書くだけですが、種類を足すと、その種類を扱わないswitch式がすべてコンパイルエラーになります。

<!-- output: sealed-switch-missing -->
```
SealedSwitchMissing.java:9: error: the switch expression does not cover all possible input values
```
<!-- /output -->
<!-- claim: sealed-switch-missing -->

これは欠点ではなく、場合分けし忘れた箇所をコンパイラーが見つけてくれるということです。
種類が増えやすく操作が固定されているならインターフェイスとポリモーフィズム、種類が固定されていて操作が増えやすいならsealedとswitch、と使い分けます。
どちらの変更に閉じるかを選ぶのが設計です。

## 4. リスコフの置換原則（型の約束と振る舞いの約束）

### しくみ

親の型で扱っている箇所に子の型のオブジェクトを渡しても、プログラムが正しく動き続けること、というのがこの原則です（第5章）。
「正しく動く」の中身は、型（メソッドの名前・引数・戻り値・例外）の約束と、振る舞い（呼んだ結果どうなるか）の約束の2つに分けられます。

### 観察する（コンパイラーが検査する約束）

型の約束の一部は、コンパイラーがオーバーライドのときに検査します。
公開範囲を狭めることと、親が宣言していない検査例外を投げることは、コンパイルエラーになります（検査例外の規則は第13章）。

<!-- output: override-weaker-access -->
```
WeakerAccess.java:9: error: run() in Child cannot override run() in Base
attempting to assign weaker access privileges; was public
```
<!-- /output -->
<!-- claim: override-weaker-access -->

<!-- output: override-broader-throws -->
```
BroaderThrows.java:11: error: load() in Child cannot override load() in Base
overridden method does not throw Exception
```
<!-- /output -->
<!-- claim: override-broader-throws -->

### 観察する（コンパイラーが検査しない約束）

振る舞いの約束は、コンパイラーには検査できません。
第5章の正方形と長方形の例を`src/`のデモで動かすと、コンパイルは通るのに、長方形として幅5・高さ3を設定した結果の面積が15になりません。

<!-- output: square-rectangle -->
```
Expected area: 15, Actual area: 9.0
```
<!-- /output -->
<!-- claim: square-rectangle -->

標準ライブラリにも、振る舞いの約束が実装ごとに違う例があります。
`verify/LspCollections.java`は、`List`型の引数に要素を1つ足すメソッドに、いろいろな`List`を渡します。

<!-- output: optional-operations -->
```
ArrayList.add: できた [a, b, default]
List.of(...).add: UnsupportedOperationException
Collections.unmodifiableList(...).add: UnsupportedOperationException
Arrays.asList(...).add: UnsupportedOperationException
Arrays.asList(...).set: できた [z, b]
```
<!-- /output -->

`List`型として受け取っても、`add`ができるとは限りません。
`Collection`のAPIドキュメントは、変更メソッドを「optional operation」とし、対応しない実装は`UnsupportedOperationException`を投げてよいと決めています。
例外を投げることも約束のうちなので、仕様のうえでは置換原則の違反ではありません。
ただし、呼ぶ側が「足せる」と思い込んでいると、実行時に初めて失敗します。
<!-- claim: optional-operations -->

変更できるリストとできないリストを型で分けなかった理由は、コレクションのフレームワークの設計FAQに書かれています。
設計者はこれをAPI全体で最も議論になった判断だとし、型で区別しようとすると、変更できる・大きさを変えられる・追記だけできる、と区別ごとにインターフェイスが増えるうえ、実行時の例外もなくせないと説明しています。
<!-- claim: collections-faq -->

### 観察する（継承が置換を壊した例）

`java.sql.Timestamp`は`java.util.Date`のサブクラスで、ナノ秒の値を足したものです。
`verify/LspTimestamp.java`で、同じ時刻の`Date`と`Timestamp`を比べます。

<!-- output: timestamp-asymmetric -->
```
date.equals(ts) = true
ts.equals(date) = false
hashCodeは同じ = true
{ts}.contains(date) = true
{date}.contains(ts) = false
```
<!-- /output -->

`date.equals(ts)`はtrueなのに、`ts.equals(date)`はfalseです。
`Object.equals`のAPIドキュメントが求める対称性（`x.equals(y)`と`y.equals(x)`が同じ結果になること）が崩れているため、集合に入れる側と探す側を入れ替えるだけで`contains`の結果が変わります。
`Timestamp`のAPIドキュメントは、この継承は実装の継承であって型の継承ではないので、`Timestamp`を`Date`として扱わないよう勧めています。
<!-- claim: timestamp-asymmetric -->

`java.util.Stack`は`Vector`を継承しています。
`verify/StackInheritance.java`で、`Stack`が持つメソッドと、引き継いだ操作で中身を変えた結果を見ます。

<!-- output: stack-vector -->
```
Stackのスーパークラス: java.util.Vector
Stackが宣言するpublicメソッド: [empty, peek, pop, push, search]
Stackで呼べるpublicメソッドの数: 71
Stackの中身: [割り込み, 1, 3]
ArrayDequeの中身（先頭が最後に入れた要素）: [3, 2, 1]
Dequeに位置を指定する操作があるか: false
```
<!-- /output -->

`Stack`が自分で宣言するのは5つのメソッドだけですが、`Vector`から引き継いだメソッドを合わせると71個のpublicメソッドを呼べます。
位置を指定する`add`や`remove`で途中の要素を操作でき、「最後に入れたものから取り出す」というスタックの約束を利用者が破れてしまいます。
APIドキュメントは、スタックには`Deque`インターフェイス（`ArrayDeque`など）を使うよう勧めています。
<!-- claim: stack-vector -->

### 説明

`Timestamp`と`Stack`は、どちらも「コードを再利用したい」ために継承を使った例です。
継承すると、親の型のすべての約束を引き受けることになります。
約束を守れない（`equals`の対称性）か、守りたくない（途中への挿入）なら、継承ではなく、親にあたるオブジェクトをフィールドに持つ合成を選びます。
継承か合成かを決める問いは、第25章の事例2で扱います。

## 5. インターフェイス分離の原則（小さなインターフェイスと、その代償）

### しくみ

使わないメソッドを含む大きなインターフェイスに依存すると、そのメソッドの変更の影響を受け、実装する側は使わないメソッドまで書かされます（第7章）。
`src/`のデモの`Robot`は、大きな`BadWorker`インターフェイスを実装させられたため、食事のメソッドで例外を投げるしかありません。

<!-- output: isp-demo-robot -->
```
UnsupportedOperationException: Robots don't eat
```
<!-- /output -->
<!-- claim: isp-demo-robot -->

これは前の節の「optional operation」と同じ形です。
インターフェイスが大きいと、実装の一部が「対応しない」と例外を投げることになり、置換原則も危うくなります。

### 観察する

`java.util.function`のインターフェイスを、`verify/IspFunctional.java`で数えます。
JDKのクラスの一覧は、実行中のJDKのモジュールを読む`jrt:/`ファイルシステムから取り出しています。

<!-- output: function-package -->
```
java.util.functionのインターフェイス: 43
抽象メソッドが1つだけのもの: 43
@FunctionalInterfaceが付いたもの: 43
比べると、Listの抽象メソッド: 25
```
<!-- /output -->

43個のインターフェイスは、どれも抽象メソッドが1つだけです。
抽象メソッドが1つなので、ラムダ式やメソッド参照（第11章）で実装を渡せます。
<!-- claim: function-package -->

言語の構文も、小さなインターフェイスだけを求めます。
`verify/IspLanguage.java`の`Countdown`は`Iterable`だけを、`Session`は`AutoCloseable`だけを実装したクラスです。
`Collection`や`List`を実装しなくても、拡張for文とtry-with-resources文で使えます。

<!-- output: language-small-interfaces -->
```
3 2 1
開く
使う
閉じる
```
<!-- /output -->
<!-- claim: language-small-interfaces -->

拡張for文は、`iterator()`で得た`Iterator`の`hasNext()`と`next()`を呼ぶコードにコンパイルされます。

<!-- output: foreach-bytecode -->
```
1: invokevirtual // Method IspLanguage$Countdown.iterator:()Ljava/util/Iterator;
6: invokeinterface // InterfaceMethod java/util/Iterator.hasNext:()Z
15: invokeinterface // InterfaceMethod java/util/Iterator.next:()Ljava/lang/Object;
```
<!-- /output -->
<!-- claim: foreach-bytecode -->

### 説明

インターフェイスを小さくすれば、使う側は必要なものだけに依存でき、実装する側は書くものが減ります。
一方で、前の節で見たとおり、`List`は変更できるかどうかでインターフェイスを分けませんでした。
分けるほど型の数が増え、組み合わせを表す型が必要になるからです。
インターフェイスを分けるのは、使う側の必要が実際に違うとき（読むだけの利用者と書き込む利用者がいる、など）に限るのが現実的です。

## 6. 依存関係逆転の原則（実装を後から差し込む）

### しくみ

ふつうに書くと、上位の処理（注文を受け付ける）は下位の詳細（データベースに保存する）のクラスを`new`して使い、上位が下位に依存します。
依存関係逆転の原則では、上位の側がインターフェイスを決め、下位の側がそれを実装します。
すると、ソースコードの依存の向きが「下位 → 上位のインターフェイス」に逆転します。

実装をどこで作って渡すかには、いくつかの方法があります。
`src/`のデモの`GoodOrderService`は、コンストラクターで実装を受け取ります（依存性の注入）。
Javaの標準ライブラリには、設定ファイルに書いた実装クラスを実行時に探す`ServiceLoader`もあります。

### 観察する

`verify/DipServiceLoader.java`は、`Greeter`インターフェイスと2つの実装を持ちます。
実装クラスの名前は、`META-INF/services/`の下の、インターフェイスの名前を付けたファイルに書きます。
ふつうはJARファイルに入れますが、このプログラムは一時ディレクトリに書き、そこを読むクラスローダーで探します。

<!-- output: serviceloader-run -->
```
設定ファイル: META-INF/services/DipServiceLoader$Greeter
こんにちは、Java
Hello, Java
```
<!-- /output -->
<!-- claim: serviceloader-run -->

実装を使う`greetAll`メソッドのバイトコードには、`ServiceLoader`と`Greeter`だけが現れ、実装クラスの名前は現れません。

<!-- output: serviceloader-javap -->
```
0: ldc // class DipServiceLoader$Greeter
3: invokestatic // Method java/util/ServiceLoader.load:(Ljava/lang/Class;Ljava/lang/ClassLoader;)Ljava/util/ServiceLoader;
27: checkcast // class DipServiceLoader$Greeter
34: invokeinterface // InterfaceMethod DipServiceLoader$Greeter.greet:(Ljava/lang/String;)Ljava/lang/String;
```
<!-- /output -->
<!-- claim: serviceloader-javap -->

JDK自身も同じしくみを使っています。
`Path`や`Files`が扱うファイルシステムは、プロバイダーとして実装を差し込めるようになっていて、JDK 25には次の3つが入っています。

<!-- output: fs-providers -->
```
インストールされたファイルシステムの実装: [file, jar, jrt]
```
<!-- /output -->
<!-- claim: fs-providers -->

### 説明

依存の向きが逆転すると、上位の処理を下位の実装なしにコンパイル・テストでき、実装を差し替えても上位を書き換えずに済みます。
代わりに、インターフェイスという部品が1つ増え、どの実装が使われるかはコードを読むだけではわからなくなります。
実装が1つしかなく、差し替える予定もないところにまでインターフェイスを置く必要はありません。
テストで差し替えたい、保存先を選べるようにしたい、といった理由があるときに使います（第21章のテストダブル、第25章の事例4）。

## 7. 標準ライブラリのなかのデザインパターン

GoFのパターンは、新しく作るコードだけでなく、毎日使っている標準ライブラリのなかにも見つかります。
パターンの名前を知っていると、APIの設計の意図が読み取りやすくなります。

### 生成のパターン

`verify/CreationalInJdk.java`の出力です。

<!-- output: integer-cache -->
```
Integer.valueOf(127) == Integer.valueOf(127): true
Integer.valueOf(128) == Integer.valueOf(128): false
```
<!-- /output -->

`Integer.valueOf`は、コンストラクターの代わりにオブジェクトを返す静的ファクトリメソッドです。
-128〜127の値では、作り置いたオブジェクトを返すことを仕様が保証しています。
それ以外の値で同じオブジェクトが返るかは実装しだいで、この実行では別のオブジェクトでした。
ラッパークラスを`==`で比べてはいけない理由です（第9章）。
<!-- claim: integer-cache -->

<!-- output: list-of-classes -->
```
List.of(1, 2)のクラス: java.util.ImmutableCollections$List12
List.of(1, 2, 3)のクラス: java.util.ImmutableCollections$ListN
```
<!-- /output -->

`List.of`は、要素の数によって別々の非公開のクラスのオブジェクトを返します。
ファクトリメソッドは返すクラスを隠せるので、JDKは利用者のコードを壊さずに実装を替えられます。
クラスの名前は実装の詳細で、将来の版では変わることがあります。
<!-- claim: list-of-classes -->

<!-- output: runtime-singleton -->
```
Runtimeのpublicなコンストラクター: 0
Runtime.getRuntime() == Runtime.getRuntime(): true
```
<!-- /output -->

`Runtime`はシングルトンです。
publicなコンストラクターがなく、`getRuntime()`がいつも同じオブジェクトを返します。
<!-- claim: runtime-singleton -->

<!-- output: builders -->
```
Locale.Builderで作ったLocale: ja_JP
sb.append("a") == sb: true
```
<!-- /output -->

`Locale.Builder`はビルダーです。
値を1つずつ設定してから`build()`で`Locale`を作ります。
`StringBuilder`の`append`は自分自身を返すので、メソッドをつなげて書けます（この書き方はビルダーでよく使われます）。
<!-- claim: builders -->

### 構造のパターン

`verify/StructuralInJdk.java`の出力です。

<!-- output: unmodifiable-view -->
```
unmodifiableListのクラス: java.util.Collections$UnmodifiableRandomAccessList
元に足したあとのビュー: [a, b]
元に足したあとのコピー: [a]
```
<!-- /output -->

`Collections.unmodifiableList`は、元のリストを包み、変更の操作だけを断るオブジェクト（ビュー）を返します。
元のリストに足した要素はビューからも見えるので、コピーではありません。
包んだオブジェクトへのアクセスを制御するのはプロキシ、機能を足すのはデコレーターと呼ばれ、どちらも同じインターフェイスで包む形をしています。
第6章の防御的コピーで中身を守りたいときは、ビューではなく`List.copyOf`のコピーを使います。
<!-- claim: unmodifiable-view -->

<!-- output: arrays-aslist-adapter -->
```
Arrays.asListのsetのあとの配列: [z, y]
```
<!-- /output -->

`Arrays.asList`はアダプターです。
配列を`List`インターフェイスで扱えるようにし、`set`による書き込みは元の配列に届きます。
<!-- claim: arrays-aslist-adapter -->

<!-- output: filter-stream-decorator -->
```
FilterInputStreamのスーパークラス: java.io.InputStream
FilterInputStreamのコンストラクターの引数: [class java.io.InputStream]
BufferedInputStreamのスーパークラス: java.io.FilterInputStream
DataInputStreamのスーパークラス: java.io.FilterInputStream
3段に包んだストリームからreadInt: 256
```
<!-- /output -->

`java.io`の入力ストリームは、デコレーターの代表例です。
`FilterInputStream`は`InputStream`を継承し（`InputStream`である）、コンストラクターで別の`InputStream`を受け取って包みます（`InputStream`を持つ）。
バッファリングを足す`BufferedInputStream`と、基本型を読む`DataInputStream`はそのサブクラスで、好きな順に重ねて機能を組み合わせられます。
<!-- claim: filter-stream-decorator -->

### 振る舞いのパターン

`verify/BehavioralInJdk.java`の出力です。

<!-- output: comparator-strategy -->
```
辞書順: [banana, fig, kiwi, pear]
長さ順（同じ長さは逆の辞書順）: [fig, pear, kiwi, banana]
```
<!-- /output -->

`List.sort`に渡す`Comparator`は、ストラテジー（並べ方という手順を差し替える部品）です。
`sort`のコードを変えずに、並べ方だけを替えられます（第11章）。
<!-- claim: comparator-strategy -->

<!-- output: iterator-class -->
```
ArrayListのイテレーターのクラス: java.util.ArrayList$Itr
```
<!-- /output -->

`iterator()`はイテレーターを返します。
返るのは`ArrayList`の内部クラスのオブジェクトで、使う側は`Iterator`インターフェイスだけを知っていれば、中身の並び方を知らずに順にたどれます。
<!-- claim: iterator-class -->

<!-- output: abstractlist-template -->
```
AbstractListから受け継いだtoString: [0, 1, 4, 9, 16]
AbstractListから受け継いだindexOf(9): 3
AbstractListから受け継いだcontains(16): true
```
<!-- /output -->

`AbstractList`はテンプレートメソッドの例です。
`get`と`size`だけを書けば、`toString`・`indexOf`・`contains`などは、`AbstractList`が用意したメソッドが`get`と`size`を呼んで動きます（第7章の抽象クラス）。
<!-- claim: abstractlist-template -->

<!-- output: observable-deprecated -->
```
Observableの@Deprecated: since=9 forRemoval=false
```
<!-- /output -->

オブザーバーのための`java.util.Observable`は、Java 9で非推奨になりました。
APIドキュメントは、通知の順序が決まっていないなど、このクラスのイベントのモデルが限られていることを理由に挙げています。
代わりに、`java.beans`パッケージ、`java.util.concurrent`のデータ構造、Flow API（`java.util.concurrent.Flow`）を案内しています。
Swingのイベント処理（第18章）も、リスナーを登録して通知を受ける、オブザーバーの形をしています。
<!-- claim: observable-deprecated -->

パターンの名前は、コードの形よりも意図を表します。
同じ「包む」形でも、インターフェイスを合わせるならアダプター、機能を足すならデコレーター、アクセスを制御するならプロキシと呼び分けます。

## 8. シングルトンの書き方を比べる

`src/`のデモの`DatabaseConnection`は、`volatile`と`synchronized`を使った二重チェックでシングルトンを作っています。
`volatile`がないと、ほかのスレッドが初期化途中のオブジェクトを見るおそれがあります（理由は付録B.14「Javaメモリモデル」）。
同じことを、より短く確実に書く方法が2つあります。
`verify/SingletonVariants.java`で比べます。

<!-- output: singleton-lazy-holder -->
```
1. 遅延初期化
  Lazy.hello()（まだ作らない）
  getInstanceを呼ぶ
  Lazyを作る
  同じオブジェクト: true
```
<!-- /output -->

1つ目は、入れ子のクラス（`Holder`）にインスタンスを持たせる方法です。
クラスの初期化は、そのクラスを初めて使うときに1回だけ、JVMが排他制御をして行います。
そのため、`Lazy`のほかのメソッドを呼んでもインスタンスは作られず、`getInstance()`で`Holder`を初めて使うときに作られます。
<!-- claim: singleton-lazy-holder -->

<!-- output: singleton-serialization -->
```
2. 直列化して読み戻す
  クラス: 同じオブジェクト = false
  enum: 同じオブジェクト = true
```
<!-- /output -->
<!-- claim: singleton-serialization -->

<!-- output: singleton-reflection -->
```
3. リフレクションでコンストラクターを呼ぶ
  クラス: 2つ目ができた = true
  enum: IllegalArgumentException: Cannot reflectively create enum objects
```
<!-- /output -->

2つ目はenumです。
クラスで書いたシングルトンは、直列化して読み戻すと別のオブジェクトになり、リフレクションで`private`なコンストラクターを呼べば2つ目を作れます。
enumの定数は直列化しても同じオブジェクトに戻り、リフレクションで作ろうとすると`IllegalArgumentException`になります。
JLSは、enumには定数として定義したもの以外のインスタンスがないと決めています。
<!-- claim: singleton-reflection -->

どの書き方でも、シングルトンを`getInstance()`で直接取り出すコードは、そのクラスに依存します。
テストで差し替えたい部品は、シングルトンにするよりも、1つだけ作ったオブジェクトをコンストラクターで渡す（6節の依存性の注入）ほうが扱いやすくなります。

## 9. アーキテクチャパターンと原則

クラスの設計原則を、アプリケーション全体の構造に広げたものがアーキテクチャパターンです。

- レイヤードアーキテクチャ
  - 画面・業務の処理・データの保存を層に分け、上の層から下の層へだけ依存させる
  - 層ごとに変わる理由が違う（単一責任の原則を層の単位に当てはめたもの）
- ヘキサゴナルアーキテクチャ（ポートとアダプター）
  - アプリケーションの中心が外部とのやり取りの口（ポート）をインターフェイスとして決め、画面やデータベースはそれに合わせるアダプターとして外側に置く
  - 依存関係逆転の原則で、中心が外側に依存しないようにする
- クリーンアーキテクチャ
  - 同心円の内側に方針（業務の規則）、外側にしくみ（画面・データベース・フレームワーク）を置き、ソースコードの依存を内側へだけ向ける

ヘキサゴナルアーキテクチャは、アリスター・コーバーンが2005年の文書で示したもので、UIやデータベースがなくてもアプリケーションを動かし、自動テストできるようにすることを目標に掲げています。
<!-- claim: hexagonal -->
クリーンアーキテクチャは、マーチンが2012年の記事で、ヘキサゴナルアーキテクチャなどの考えを1つの図にまとめたものです。
記事は「ソースコードの依存は内側にだけ向く」という規則を中心に置いています。
<!-- claim: clean-architecture -->

依存の向きを決めても、守られなければ意味がありません。
Javaのモジュール（`module-info.java`）は、`exports`しないパッケージをほかのモジュールから使えなくします。
JDK自身も、内部のパッケージをexportせず、利用者のコードから使えないようにしています。
`verify/compile-errors/InternalPackage.java`は、`java.base`がexportしていない`jdk.internal.misc`のクラスを使おうとします。
検査は`--release 25`を付けてコンパイルするので、exportされたAPIだけが見え、パッケージが存在しないと報告されます。

<!-- output: module-boundary -->
```
InternalPackage.java:2: error: package jdk.internal.misc does not exist
```
<!-- /output -->
<!-- claim: module-boundary -->

`--release`を付けずに`javac`を実行すると、「package jdk.internal.misc is not visible」というエラーになり、`java.base`がこのパッケージを無名モジュールへexportしていないことが理由として示されます。

層やモジュールの境界をコンパイラーに検査させると、設計の決まりを人の注意だけに頼らずに済みます。
統合プロジェクトで層に分けて依存の向きを決める判断は、第25章の事例4で扱います。

## よくある誤解

- 単一責任の原則は「クラスのメソッドを1つにする」こと
  - 責任とは変更の理由のことで、同じ理由で変わるメソッドは1つのクラスにまとめてよい（2）
  <!-- claim: srp-reason-to-change -->
- リスコフの置換原則は継承だけの話
  - インターフェイスの実装を含むサブタイプ全般の話である（1・4）
  <!-- claim: solid-martin-wording -->
- コンパイルが通れば置換原則を満たしている
  - コンパイラーが検査するのはシグネチャ・公開範囲・検査例外までで、振る舞いの約束は検査しない（4）
  <!-- claim: square-rectangle -->
- インターフェイスは細かく分けるほどよい
  - 分けるほど型が増える。標準ライブラリの`List`も、変更できるかどうかでは分けなかった（4・5）
  <!-- claim: collections-faq -->
- 開放閉鎖の原則に従えば、どんな変更でも既存のコードを触らずに済む
  - 閉じられるのは選んだ方向の変更だけで、種類を足す変更と操作を足す変更は両立しにくい（3）
- デザインパターンは使えば使うほどよい
  - パターンは部品と間接参照を増やす。ストラテジーはラムダ式、シングルトンはenumのように、Javaの機能で短く書けるものもある（7・8）

## 参考資料

- Java言語仕様（Java SE 25）
  - 8.4.8.3「Requirements in Overriding and Hiding」
  - 8.9「Enum Classes」
  - 12.4.1「When Initialization Occurs」
  - 14.14.2「The enhanced for statement」、14.20.3「try-with-resources」
  - 15.28.1「The Switch Block of a switch Expression」
  - `https://docs.oracle.com/javase/specs/jls/se25/html/`
- APIドキュメント（Java SE 25）
  - `java.util.Collection`、`java.util.List`、`java.util.Stack`、`java.sql.Timestamp`、`java.util.ServiceLoader`、`java.io.FilterInputStream`、`java.util.AbstractList`
  - `https://docs.oracle.com/en/java/javase/25/docs/api/`
- Java Collections API Design FAQ
  - `https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/doc-files/coll-designfaq.html`
- Java Object Serialization Specification 1.12「Serialization of Enum Constants」
  - `https://docs.oracle.com/en/java/javase/25/docs/specs/serialization/serial-arch.html`
- JEP 409: Sealed Classes、JEP 403: Strongly Encapsulate JDK Internals
  - `https://openjdk.org/jeps/409`
  - `https://openjdk.org/jeps/403`
- Erich Gamma・Richard Helm・Ralph Johnson・John Vlissides『Design Patterns: Elements of Reusable Object-Oriented Software』（Addison-Wesley、1994年）
- Robert C. Martin, The Clean Code Blog
  - The Clean Architecture（2012）、The Single Responsibility Principle（2014）、The Open Closed Principle（2014）、Solid Relevance（2020）
  - `https://blog.cleancoder.com/`
- Alistair Cockburn, The Hexagonal (Ports & Adapters) Architecture, 2005
  - `https://alistair.cockburn.us/hexagonal-architecture/`
