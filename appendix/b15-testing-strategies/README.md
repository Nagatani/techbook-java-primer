# 付録B.15 テスト戦略<small>テストピラミッドと統合テスト</small>

第21章で学んだJUnitによる単体テストを、内側から深める節です。
JUnitがテストの成功と失敗を何で決めているのか、テストどうしを独立させるしくみ、テストそのものの強さを測る方法（ミューテーションテスト）、入力を自動で作って調べる方法（Property-based Testing）、データベースを使う統合テストを、実際に動かして観察します。
最後に、これらをどの粒度で組み合わせるか（テストピラミッド）を考えます。
第26章で選んだ「JUnitだけで書き、SQLは本物のデータベースで確かめる」という方針の背景にもなります。

## この節の読み方

この節の解説のうち、事実を述べた段落には`<!-- claim: … -->`という印を付けています（GitHubの画面では見えません）。
印の名前は同じディレクトリの`claims.yml`に対応し、そこにその主張の確かめ方（観察用のプログラムの実行結果、出典）を書いています。
リポジトリのルートで`npm run check:deep-dive -- b15`を実行すると、すべての主張をJDK 25で確かめ直せます。
「観察する」の欄に載せた出力は、この検査が実際に出したものです。

JUnitのふるまいは、JUnit 5.13.4（`junit-platform-console-standalone` 1.13.4）で観察しています。
JUnitの最新の系列はJUnit 6（6.0.0は2025年9月30日公開）で、この節のテストの書き方（JUnit Jupiter）は6でも同じです。
<!-- claim: junit6 -->

## ファイル構成

### 観察用のプログラム（verify/）

| ファイル | 観察すること |
|---|---|
| `ProbeRunner.java` | 補助クラス。JUnit PlatformのLauncherでテストクラスを実行し、テストごとの結果を表示する |
| `OutcomeProbe.java` | テストの成功・失敗・中断・スキップが何で決まるか |
| `OrderProbe.java` | テストメソッドの実行順、テストクラスのインスタンスの作られ方、テストどうしの依存 |
| `AssertionProbe.java` | `assertAll`と`assertThrows`のふるまい |
| `ParameterizedProbe.java` | パラメーター化テストの1回ずつの実行と表示名 |
| `NestedProbe.java` | `@Nested`での`@BeforeEach`・`@AfterEach`の順序 |
| `TimeoutProbe.java` | `@Timeout`を超えたテストの止まり方 |
| `MutationProbe.java` | 変異を入れたコードに同じテストを流し、テストが変異を見逃すか |
| `PropertyProbe.java` | 乱数で入力を作って性質を確かめ、反例を縮める |
| `H2Lifecycle.java` | インメモリデータベースの寿命と、ロールバックによるテストの独立 |

`verify/`のプログラムはパッケージを持たないので、`verify/`ディレクトリでそのままコンパイル・実行できます。
JUnitとH2のJARをMaven Centralから入手し、クラスパスに加えます。

```bash
cd verify
curl -O https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.13.4/junit-platform-console-standalone-1.13.4.jar
curl -O https://repo1.maven.org/maven2/com/h2database/h2/2.5.252/h2-2.5.252.jar
javac -cp junit-platform-console-standalone-1.13.4.jar:h2-2.5.252.jar *.java
java -cp .:junit-platform-console-standalone-1.13.4.jar:h2-2.5.252.jar OutcomeProbe
```

Windowsでは、クラスパスの区切りを`;`にします。
テストの対象のクラスは、観察用のプログラムのなかに`Cases`などの名前の入れ子のクラスとして書いてあります。
同じテストは、JUnitのコマンドラインの実行器（Console Launcher）で、クラスを指定して実行することもできます。

```bash
java -jar junit-platform-console-standalone-1.13.4.jar execute \
  --class-path . --select-class 'OutcomeProbe$Cases'
```

Console Launcherは、クラスパスからテストを探すとき、既定では名前が`Test`で始まるか、`Test`・`Tests`で終わるクラスだけを対象にします。
`OutcomeProbe$Cases`のような名前のクラスは、探しても見つかりません（クラスを指定すれば実行されます）。
観察用のテストクラスはわざと失敗するテストを含むので、この規則を利用して、リポジトリの検査（`check:public-code`）がふつうのテストとして実行しないようにしています。
<!-- claim: console-scan-pattern -->

### 体験用のデモ（src/）

JUnitを使わず、テストの考え方を小さな自作のしくみで体験するデモです。

- `TestPyramidDemo.java`
  - ECサイトの注文処理を題材に、単体テスト（依存するサービスを手書きのモックに置き換える）、サービス層の連携のテスト、利用者のシナリオ全体のテストを順に実行する
- `PropertyBasedTestDemo.java`
  - ソート・文字列・データ構造・算術・銀行口座の性質を、乱数で作った多数の入力で確かめる。乱数のシードは既定で42で、`-Dseed=数値`で変えられる
- `MutationTestDemo.java`
  - 変異を入れたコードをサブクラスとして用意し、弱いテストと強いテストでミューテーションスコアを比べる。等価ミュータントの例も含む
- `TestContainersDemo.java`
  - CRUD操作、一意制約、集計のクエリ、トランザクションをテストする。Dockerなしで動かせるよう、Testcontainersの代わりにインメモリのH2データベースを使う。最後に表示する処理時間は、実行する環境で変わる

```bash
javac -d out src/main/java/com/example/testing/*.java
java -cp out com.example.testing.TestPyramidDemo
java -cp out com.example.testing.PropertyBasedTestDemo
java -cp out com.example.testing.MutationTestDemo
java -cp out:verify/h2-2.5.252.jar com.example.testing.TestContainersDemo
```

`TestContainersDemo`は、H2がクラスパスにないと「Could not open the test database」と表示して終了します。

## 1. JUnitはテストをどう実行しているか

### しくみ

第21章では、テストメソッドに`@Test`を付け、IDEの実行ボタンで動かしました。
JUnit 5は、テストを見つけて実行する土台（JUnit Platform）と、`@Test`などの書き方を解釈するテストエンジン（JUnit Jupiter、JUnit 4のテストを動かすJUnit Vintage）に分かれています。
IDEやMaven・Gradleは、Platformが用意する入り口（Launcher）にテストクラスを渡して実行し、結果を受け取って表示しています。
`verify/ProbeRunner.java`は、このLauncherの公開APIだけを使ってテストを実行し、結果を1行ずつ表示します。
<!-- claim: junit-platform-launcher -->

### 観察する

`verify/OutcomeProbe.java`は、成功・失敗・例外・前提の不成立・`@Disabled`を1つずつ書いたテストクラスを実行します。

<!-- output: outcome-by-exception -->
```
ABORTED    assumptionNotMet()  <- org.opentest4j.TestAbortedException: Assumption failed: 前提が成り立たない
SUCCESSFUL passes()
FAILED     throwsUnexpectedException()  <- java.lang.NullPointerException: Cannot invoke "String.length()" ...
SKIPPED    disabled()  <- 一時的に止めている
SUCCESSFUL checksNothing()
FAILED     assertionFails()  <- org.opentest4j.AssertionFailedError: expected: <5> but was: <4>
summary: found=6 succeeded=2 failed=2 aborted=1 skipped=1
```
<!-- /output -->

### 説明

JUnitがテストの結果を決める規則は単純です。
テストメソッドが例外を投げずに終われば成功、例外を投げれば失敗です。
`assertEquals`は、値が違うときに`org.opentest4j.AssertionFailedError`という例外を投げるメソッドにすぎません。
テストのなかで予期しない`NullPointerException`が起きても、同じく失敗になります。
<!-- claim: outcome-by-exception -->

逆に言えば、結果を何も確かめないテスト（`checksNothing`）は、対象のコードが例外を投げない限り成功します。
テストが「通った」ことは、そのテストが何かを確かめたことを意味しません。
この弱点を測る方法が、4で扱うミューテーションテストです。

例外のうち、`assumeTrue`などの前提が成り立たないときに投げられる`TestAbortedException`だけは、失敗ではなく中断（ABORTED）として扱われます。
特定のOSでしか意味のないテストなどに使います。
`@Disabled`を付けたテストは実行されず、スキップ（SKIPPED）になり、付けた理由が表示されます。
<!-- claim: outcome-aborted-skipped -->

## 2. テストどうしを独立させるしくみ

第21章のFIRST原則の「Independent（独立）」は、各テストがほかのテストに依存しないことでした。
JUnitには、これを支えるしくみと、依存に気づかせるしくみがあります。

### 観察する

`verify/OrderProbe.java`は、メソッドを`first`〜`fifth`の順に書いたテストクラスを2回実行し、実行された順を表示します。
`@Order`で順序を指定したクラスも実行します。

<!-- output: order-default -->
```
default order (1st run): fourth second fifth first third
default order (2nd run): fourth second fifth first third
@Order(3,1,2): step2 step3 step1
```
<!-- /output -->

続いて、テストメソッドが呼ばれるたびにフィールド`count`を1増やして表示します。

<!-- output: lifecycle-per-method -->
```
PER_METHOD: count=1 count=1 count=1
PER_CLASS: count=1 count=2 count=3
```
<!-- /output -->

最後に、1つ目のテストが静的フィールドのリストに要素を足し、2つ目のテストが要素の数を確かめるクラスを、2回続けて実行します。

<!-- output: static-state-dependency -->
```
dependent tests (1st run):
SUCCESSFUL addItem()
SUCCESSFUL hasOneItem()
dependent tests (2nd run, static field kept):
SUCCESSFUL addItem()
FAILED     hasOneItem()  <- org.opentest4j.AssertionFailedError: expected: <1> but was: <2>
```
<!-- /output -->

### 説明

実行順を指定しないとき、テストメソッドは書いた順には実行されません。
JUnitのユーザーガイドは、既定の順を「決定的だが、わざとわかりにくくした」順と説明しています。
何度実行しても同じ順になるので、ビルドの結果は再現できます。
一方で、書いた順に実行されることを前提にしたテストは、いつ壊れてもおかしくありません。
<!-- claim: order-default -->

`@TestMethodOrder(MethodOrderer.OrderAnnotation.class)`をクラスに付けると、`@Order`の値の小さい順に実行されます。
画面の操作の手順をたどる統合テストのように、順序に意味があるときだけ使います。
<!-- claim: order-annotation -->

JUnitは既定で、テストメソッドを1つ実行するたびに、テストクラスの新しいインスタンスを作ります（PER_METHOD）。
そのため、インスタンスのフィールドは次のテストに引き継がれず、`count`はどのテストでも1です。
クラスに`@TestInstance(TestInstance.Lifecycle.PER_CLASS)`を付けると、すべてのテストメソッドが1つのインスタンスを共有します。
<!-- claim: lifecycle-per-method -->

インスタンスを作り直しても、静的フィールドは残ります。
1回目は、たまたま`addItem`が先に実行されたので成功しました。
同じクラスを2回目に実行すると、1回目に足した要素が残っているため失敗します。
IDEで1つのテストだけを実行したときや、ほかのテストクラスと一緒に実行したときにだけ失敗するテストは、このような共有された状態を疑います。
状態は`@BeforeEach`で毎回作り直し、静的フィールドに置かないようにします。
<!-- claim: static-state-dependency -->

## 3. アサーションとテストの組み立て

### 観察する

`verify/AssertionProbe.java`は、3つの値がすべて違う`Person`を、`assertEquals`を3つ並べる書き方と`assertAll`でまとめる書き方で確かめます。

<!-- output: assertall-collects -->
```
FAILED     sequential()  <- org.opentest4j.AssertionFailedError: expected: <Taro> but was: <Hanako>
FAILED     grouped()  <- org.opentest4j.MultipleFailuresError: person (3 failures)
failures in MultipleFailuresError: 3
  expected: <Taro> but was: <Hanako>
  expected: <Suzuki> but was: <Sato>
  expected: <21> but was: <20>
```
<!-- /output -->

同じプログラムで、`Integer.parseInt("abc")`が投げる`NumberFormatException`（`IllegalArgumentException`のサブクラス）を、`assertThrows`のいろいろな書き方で確かめます。

<!-- output: assertthrows-behavior -->
```
FAILED     otherType()  <- Unexpected exception type thrown, expected: <java.lang.ArithmeticException> but was: <java.lang.NumberFormatException>
FAILED     nothingThrown()  <- Expected java.lang.NumberFormatException to be thrown, but nothing was thrown.
SUCCESSFUL exactType()
SUCCESSFUL superType()
FAILED     superTypeExactly()  <- Unexpected exception type thrown, expected: <java.lang.IllegalArgumentException> but was: <java.lang.NumberFormatException>
SUCCESSFUL checkMessage()
```
<!-- /output -->

### 説明

1で見たとおり、アサーションの失敗は例外です。
そのため、`assertEquals`を並べると、最初の失敗で例外が投げられ、残りのアサーションは実行されません。
直して実行するたびに次の誤りが1つずつ見つかることになります。
`assertAll`は、渡したアサーションをすべて実行し、失敗をまとめて`MultipleFailuresError`として報告します。
1つのオブジェクトの複数の項目を確かめるときに向いています。
<!-- claim: assertall-collects -->

`assertThrows`は、指定した型か、そのサブクラスの例外が投げられれば成功します。
例外が投げられないときと、関係のない型の例外が投げられたときは失敗します。
サブクラスを認めたくないときは`assertThrowsExactly`を使います。
`assertThrows`は投げられた例外を返すので、`checkMessage`のように例外のメッセージも確かめられます。
<!-- claim: assertthrows-behavior -->

### パラメーター化テスト

`verify/ParameterizedProbe.java`は、4で割り切れる年をすべてうるう年とする誤ったメソッドを、`@ValueSource`で複数の年について確かめます。
`@CsvSource`で、`int`どうしの足し算も確かめます。

<!-- output: parameterized-invocations -->
```
SUCCESSFUL [1] 2023
FAILED     [2] 1900  <- org.opentest4j.AssertionFailedError: expected: <false> but was: <true>
FAILED     [3] 2100  <- org.opentest4j.AssertionFailedError: expected: <false> but was: <true>
SUCCESSFUL 1 + 2 = 3
SUCCESSFUL -1 + 1 = 0
FAILED     2147483647 + 1 = 2147483648  <- org.opentest4j.AssertionFailedError: expected: <2147483648> but was: <-2147483648>
SUCCESSFUL [1] 2024
SUCCESSFUL [2] 2000
summary: found=8 succeeded=5 failed=3 aborted=0 skipped=0
```
<!-- /output -->

上の3行が平年を確かめる`commonYears`、次の3行が足し算の`addition`、最後の2行がうるう年を確かめる`leapYears`の結果です。
`@ParameterizedTest`のメソッドは、引数の組ごとに別のテストとして実行されます。
1900年の組が失敗しても、2100年の組は実行されます。
拡張for文で複数の値を確かめる1つのテストでは、最初の失敗で止まるので、こうはなりません。
表示名は既定で「[番号] 引数」になり、`name`属性で「{0} + {1} = {2}」のように変えられます。
<!-- claim: parameterized-invocations -->

### 入れ子のテスト

第21章のサンプルコード21-16では、`@Nested`で正常系と異常系を分けました。
`verify/NestedProbe.java`は、外側と内側の両方に`@BeforeEach`と`@AfterEach`を書き、呼ばれる順を表示します。

<!-- output: nested-callbacks -->
```
  outer @BeforeEach
  test: isEmpty
  outer @AfterEach
  outer @BeforeEach
  inner @BeforeEach
  test: popEmpties
  inner @AfterEach
  outer @AfterEach
SUCCESSFUL 作った直後は空
SUCCESSFUL 取り出すと空になる
```
<!-- /output -->

内側のクラスのテストでは、外側の`@BeforeEach`で作ったスタックに、内側の`@BeforeEach`が要素を1つ積んでからテストが始まります。
「空のスタック」から「1つ積んだスタック」へと、準備を段階的に重ねて書けるのが`@Nested`の利点です。
`@Nested`にできるのは`static`でない内部クラスだけです。
内部クラスなので、外側のインスタンスのフィールド（ここでは`stack`）をそのまま使えます。
<!-- claim: nested-callbacks -->

### 時間の上限

`verify/TimeoutProbe.java`は、10秒待つテストと、割り込みを確かめずに1秒間回り続ける計算のループに、200ミリ秒の`@Timeout`を付けて実行します。

<!-- output: timeout-interrupts -->
```
SUCCESSFUL fastEnough()
FAILED     tooSlow()  <- java.util.concurrent.TimeoutException: tooSlow() timed out after 200 milliseconds
finished in less than 5 seconds (interrupted)
FAILED     busyLoop()  <- java.util.concurrent.TimeoutException: busyLoop() timed out after 200 milliseconds
busy loop ran to the end (at least 1 second)
```
<!-- /output -->

`@Timeout`の時間を超えたテストは失敗になります。
既定の動き方（SAME_THREAD）では、JUnitは別のスレッドから、テストを実行しているスレッドに割り込み（interrupt）をかけます。
`Thread.sleep`は割り込まれると待つのをやめるので、テストは10秒を待たずに終わります。
割り込みを確かめない計算のループ（`busyLoop`）は止まらず、1秒間動き終わってから失敗になります。
割り込みはスレッドを強制的に止めるしくみではなく、止まるかどうかは割り込まれた側のコードしだいだからです。
<!-- claim: timeout-interrupts -->

## 4. ミューテーションテスト：テストの強さを測る

### しくみ

1で見たとおり、テストが成功していても、そのテストが誤りを見つけられるとは限りません。
カバレッジ（第21章）は、テストがどの行を実行したかを測りますが、実行した結果を確かめたかどうかは測りません。
ミューテーションテストは、元のコードに小さな変更（変異）を1つ入れた版（ミュータント）を作り、同じテストを実行します。
テストが1つでも失敗すれば、そのミュータントは「殺された」、すべて成功すれば「生き残った」といいます。
生き残ったミュータントは、テストが確かめていない動きを指しています。

### 観察する

`verify/MutationProbe.java`は、「会員で1000円以上なら1割引きにする」次のメソッドを対象にします。

```java
member && price >= 1000 ? price - price / 10 : price
```

ミュータントを6つ用意し、2つのテストクラスをJUnitで実行します。
弱いテストは「会員の2000円」と「会員でない2000円」の2つで、割引きする場合としない場合の両方の分岐を通ります。
強いテストは、境界の1000円と999円も確かめます。

<!-- output: mutation-weak-strong -->
```
--- WeakSuite
original: all tests pass
  SURVIVED >= を > に
  KILLED   && を || に
  KILLED   - を + に
  KILLED   10 を 9 に
  KILLED   割引きの式を消す
  SURVIVED >= 1000 を > 999 に
mutation score: 4/6
--- StrongSuite
original: all tests pass
  KILLED   >= を > に
  KILLED   && を || に
  KILLED   - を + に
  KILLED   10 を 9 に
  KILLED   割引きの式を消す
  SURVIVED >= 1000 を > 999 に
mutation score: 5/6
```
<!-- /output -->

### 説明

弱いテストは両方の分岐を通っているので、テストで実行されない行や分岐はありません。
それでも、`>=`を`>`に変えたミュータントは生き残りました。
2000円では、どちらの比較でも結果が同じだからです。
境界の1000円を確かめる強いテストは、このミュータントを殺します。
ミューテーションテストは、このように「境界を確かめていない」というテストの穴を、具体的な変更として示します。
<!-- claim: mutation-weak-strong -->

`price >= 1000`を`price > 999`に変えたミュータントは、強いテストでも生き残りました。
`price`は`int`なので、2つの式はどんな値でも同じ結果になります。
このように元のコードと同じ動きをするミュータントを、等価ミュータントといいます。
等価ミュータントはどんなテストでも殺せないので、スコアが100%にならないことは、必ずしもテストの不足を意味しません。
生き残ったミュータントは1つずつ見て、テストの穴か等価ミュータントかを判断します。
<!-- claim: mutation-equivalent -->

### 道具：PIT

Javaのミューテーションテストの道具には、PIT（Pitest）があります。
PITは、ソースファイルではなく、コンパイラーが生成したバイトコードに変異を入れ、テストを実行します。
変異の種類は名前で選べ、たとえば既定で有効な`CONDITIONALS_BOUNDARY`は、`<`と`<=`、`>`と`>=`を入れ替えます（上の観察の「`>=`を`>`に」と同じ変異です）。
<!-- claim: pit-basics -->

Mavenでは、プラグイン`org.pitest:pitest-maven`（2026年11月19日時点の最新版は1.30.2）を使い、`mvn test-compile org.pitest:pitest-maven:mutationCoverage`で実行します。
結果はHTMLのレポートとして`target/pit-reports`の下に出力されます。
ミュータントの数だけテストを実行し直すので、時間がかかります。
変更したクラスに絞る、夜間のビルドで実行するなどの工夫をして使います。
<!-- claim: pit-maven -->

## 5. Property-based Testing：入力を自動で作る

### しくみ

ふつうのテストは、作った人が選んだ入力と、その期待値を書きます（例示によるテスト）。
Property-based Testingでは、具体的な期待値の代わりに「どんな入力でも成り立つはずの性質」を書き、入力は乱数で多数作ります。
性質が成り立たない入力（反例）が見つかったら、失敗したまま、できるだけ小さな入力へ縮めて（shrink）報告します。
大きな乱数のままの反例より、小さな反例のほうが原因を考えやすいからです。

### 観察する

`verify/PropertyProbe.java`は、2つの`int`の中間の値を`(a + b) / 2`で求めるメソッドを調べます。
性質は「中間の値は、小さいほうの値以上、大きいほうの値以下である」です。
まず、手で選んだ例を確かめます。

<!-- output: pbt-examples-pass -->
```
  mid(1, 3) = 2  property: true
  mid(-4, 4) = 0  property: true
  mid(10, 10) = 10  property: true
  mid(0, 100) = 50  property: true
```
<!-- /output -->

次に、シード42の`java.util.Random`で入力を1000件まで作って性質を確かめ、反例が見つかったら縮めます。

<!-- output: pbt-counterexample -->
```
--- random inputs, seed 42 (buggy version)
  counterexample found at try 7: (1583910553, 1639144584) -> -535956079
  shrink steps: 18
  shrunk counterexample: (736429957, 1411053691) -> -1073741824
  a + b = 2147483648 (Integer.MAX_VALUE + 1 = 2147483648)
```
<!-- /output -->

`(int) (((long) a + b) / 2)`のように`long`で計算する版は、同じ1000件の入力で性質を満たします。
<!-- claim: pbt-fixed -->

<!-- output: pbt-fixed -->
```
--- random inputs, seed 42 (fixed version)
  property held for 1000 inputs
```
<!-- /output -->

### 説明

手で選んだ小さな例は、どれも性質を満たしました。
乱数の入力では7件目で反例が見つかり、中間の値が負の数になりました。
`a + b`が`int`の範囲を超えると、結果は数学的な和の下位32ビットになり、負の値に変わるからです。
<!-- claim: pbt-examples-pass, pbt-counterexample -->

縮めた反例は、和がちょうど`Integer.MAX_VALUE + 1`になる組でした。
正の値どうしなら、和が`Integer.MAX_VALUE`以下の組ではあふれは起きません。
縮めた結果は「2つの値の和が`int`の最大値を超えるとき」という失敗の条件を、そのまま示しています。
この観察の縮め方は、それぞれの値を0に近づける候補を順に試すだけの素朴なものです。
<!-- claim: pbt-counterexample -->

`java.util.Random`は、乱数を作る算法がAPI仕様で決められていて、同じシードで作れば、どのJVMでも同じ値の並びを返します。
同じプログラムで、もう一度シード42で探すと、同じ反例が同じ7件目で見つかります。
Property-based Testingでは、失敗したときのシードを記録しておけば、同じ反例を再現して原因を調べられます。
<!-- claim: pbt-seed-reproducible -->

性質の見つけ方には、いくつかの型があります。

- 逆の操作で元に戻る
  + 文字列を2回反転すると元に戻る、保存して読み込むと同じ値になる
- 結果が満たす条件を確かめる
  + ソートした結果は昇順に並び、要素の数が変わらない
- 2回適用しても変わらない
  + ソートした結果をもう一度ソートしても同じ
- 別の実装と比べる
  + 複雑で速い実装の結果を、単純な実装の結果と比べる

`src/`の`PropertyBasedTestDemo`は、これらの性質をいくつかの題材で確かめます。

### 道具：jqwik

JavaのProperty-based Testingのライブラリには、jqwikがあります。
jqwikは、JUnit Platformのうえで動く、Jupiterとは別のテストエンジンです。
2026年11月19日時点の最新版は1.10.2で、JUnit Platform 1.14.4以降を必要とします。
入力の型ごとの生成器、反例の縮小、シードの指定などを備えています。
<!-- claim: jqwik-version -->

## 6. データベースを使う統合テスト

### しくみ

第21章では、データベースに接続するリポジトリをスタブやモックに置き換えて、サービスの単体テストを書きました。
しかし、スタブはSQLを実行しないので、SQLの誤り、制約の違反、トランザクションの動きは確かめられません。
第26章の事例のように、リポジトリそのものは本物のデータベースに接続してテストします。
テストでは、プロセスのなかで動き、テストごとに空の状態から始められるインメモリのデータベースがよく使われます。

### 観察する

`verify/H2Lifecycle.java`は、第24章で使ったH2のインメモリデータベースがいつ消えるかを、URLの書き方を変えて確かめます。

<!-- output: h2-mem-lifecycle -->
```
--- named in-memory database (jdbc:h2:mem:shop)
second connection while the first is open: rows=1
new connection after all connections closed: Table "ITEMS" not found (this database is empty)
--- DB_CLOSE_DELAY=-1 (kept until the JVM exits)
new connection after all connections closed: rows=1
--- unnamed in-memory database (jdbc:h2:mem:)
second connection while the first is open: Table "ITEMS" not found (this database is empty)
```
<!-- /output -->

同じプログラムの最後で、自動コミットを止め、テストに見立てた処理のたびにロールバックします。

<!-- output: h2-rollback -->
```
test 1: rows during the test=2
test 1: rows after rollback=1
test 2: rows during the test=2
test 2: rows after rollback=1
```
<!-- /output -->

### 説明

名前を付けたインメモリデータベース（`jdbc:h2:mem:shop`）は、同じJVMのなかなら別の接続からも同じものが見えます。
しかし、最後の接続を閉じると、中身ごと消えます。
`@BeforeAll`で表を作る接続をすぐに閉じてしまい、テストで「表がない」というエラーになるのは、この性質のためです。
<!-- claim: h2-mem-lifecycle -->

URLに`;DB_CLOSE_DELAY=-1`を付けると、接続をすべて閉じても、JVMが終わるまで中身が残ります。
<!-- claim: h2-close-delay -->

名前のない`jdbc:h2:mem:`は、接続ごとに別のデータベースになります。
<!-- claim: h2-unnamed -->

テストどうしを独立させるには、テストごとに別の名前のデータベースを作るか、テストの終わりにロールバックします。
ロールバックの方法では、テストで加えた行が残らないので、次のテストは同じ状態から始まります。
ただし、テストの対象のコードが自分でコミットするときは、この方法は使えません。
<!-- claim: h2-rollback -->

### 本番と同じデータベースで確かめる

H2は標準SQLとほかのデータベースとの互換を目指していますが、ふるまいの違いが残っています。
たとえばH2のドキュメントは、MySQLの文字列の列は既定で大文字と小文字を区別しないが、H2は区別する、という違いを挙げています。
H2で通ったテストが、本番のデータベースで同じように通るとは限りません。
<!-- claim: h2-compatibility -->

本番と同じ種類・同じ版のデータベースで確かめたいときに使われるのが、Testcontainersです。
Testcontainersは、データベースなどをDockerのコンテナーとして起動し、テストのあいだだけ使えるようにするJavaのライブラリです。
実行にはDockerが動く環境が必要です。
2026年11月19日時点の最新版は2.0.5で、2.0からモジュールの名前が`testcontainers-postgresql`のように`testcontainers-`で始まる形になりました（1.x系では`postgresql`）。
古い解説の依存関係の書き方をそのまま使うと、1.x系の版を指定することになります。
この節の検査はDockerを使わないので、Testcontainersは動かしていません。
<!-- claim: testcontainers -->

| 方法 | 速さ | 本番との近さ | 必要なもの |
|---|---|---|---|
| スタブ・モック | 速い | SQLを実行しない | なし |
| インメモリのデータベース（H2など） | 速い | 方言や機能に違いがある | JDBCドライバー |
| Testcontainers | コンテナーの起動を待つ | 同じ種類・同じ版のデータベース | Docker |

## 7. テストピラミッド：どの粒度で何をテストするか

### 考え方

テストピラミッドは、自動テストを粒度で分け、細かいテストを多く、粗いテストを少なく持つという考え方です。
Martin Fowlerの記事「TestPyramid」（2012年）によると、Mike Cohnが2009年の著書『Succeeding with Agile』で「Test Automation Pyramid」として述べたことで広く知られました。
<!-- claim: pyramid-origin -->

```
          /\
         /  \      画面や利用者のシナリオ全体のテスト（少ない）
        /----\
       /      \    複数の部品やデータベースを組み合わせるテスト
      /--------\
     /          \  単体テスト（多い）
    /------------\
```

下の層ほど多く持つ理由は、次のように説明できます。

- 単体テストは速く、失敗したときに原因の場所がすぐわかる
  + 依存をスタブやモックに置き換えるので、外部の状態に左右されない
- 組み合わせのテストは、単体テストでは確かめられないことを確かめる
  + SQLの誤り、制約、トランザクションは、本物のデータベースでなければわからない（6）
  + 共有される状態が増えるので、テストどうしの独立に気を配る（2・6）
- 画面やシナリオ全体のテストは、利用者から見た動きを確かめる
  + 遅く、失敗の原因が多くの部品のどこにあるかわかりにくいので、重要な流れに絞る

層ごとの数の割合に決まった値はありません。
ピラミッドは「粗いテストで確かめられることを、細かいテストで確かめられないか」を考えるための形です。

`src/`の`TestPyramidDemo`は、注文処理を題材に、3つの層のテストを順に実行します。
単体テストの層では、決済・在庫・メールのサービスを手書きのモックに置き換えています。

### 層ごとに確かめること

- 単体テスト
  + 計算や判定の規則、境界の値、例外の条件
  + Property-based Testingで性質を確かめる（5）
  + ミューテーションテストでテストの穴を探す（4）
- 組み合わせのテスト
  + リポジトリのSQL、制約、トランザクション（6）
  + サービスと依存先の受け渡し
- 画面やシナリオ全体のテスト
  + 注文から支払いまでのような、利用者にとって重要な流れ

## よくある誤解

- テストが成功していれば、コードは正しく確かめられている
  - 結果を確かめないテストも成功する（1）。弱いテストは、両方の分岐を通っていても境界の誤りを見逃す（4）
  <!-- claim: outcome-by-exception, mutation-weak-strong -->
- テストメソッドは書いた順に実行される
  - 既定の順は書いた順ではない。ただし実行のたびに同じ順になる（2）
  <!-- claim: order-default -->
- テストクラスのフィールドは、テストメソッドのあいだで共有される
  - 既定ではテストメソッドごとにインスタンスが作られる。共有されるのは静的フィールドである（2）
  <!-- claim: lifecycle-per-method, static-state-dependency -->
- ミューテーションスコアは100%を目指すべきである
  - 等価ミュータントはどんなテストでも殺せない（4）
  <!-- claim: mutation-equivalent -->
- Property-based Testingは乱数を使うので、失敗を再現できない
  - シードを固定すれば同じ入力が作られる（5）
  <!-- claim: pbt-seed-reproducible -->
- インメモリのデータベースは、テストのあいだずっと残っている
  - 最後の接続を閉じると消える（6）
  <!-- claim: h2-mem-lifecycle -->

## 参考資料

- JUnit 5 User Guide（5.13.4）
  - 1.1「What is JUnit 5?」、2.5「Assertions」、2.6「Assumptions」
  - 2.11「Test Execution Order」、2.12「Test Instance Lifecycle」、2.13「Nested Tests」
  - 2.21「Timeouts」、4.3「Console Launcher」、6.4「JUnit Platform Launcher API」
  - `https://docs.junit.org/5.13.4/user-guide/index.html`
- JUnit 6.0.0 Release Notes
  - `https://docs.junit.org/6.0.0/release-notes.html`
- Java言語仕様（Java SE 25）15.18.2「Additive Operators (+ and -) for Numeric Types」
  - `https://docs.oracle.com/javase/specs/jls/se25/html/jls-15.html`
- java.util.Random（Java SE 25 API）
  - `https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/Random.html`
- PIT
  - `https://pitest.org/`
  - `https://pitest.org/quickstart/mutators/`
  - `https://pitest.org/quickstart/maven/`
- jqwik User Guide
  - `https://jqwik.net/docs/current/user-guide.html`
- H2 Database Engine「Features」（In-Memory Databases、Compatibility）
  - `https://www.h2database.com/html/features.html`
- Testcontainers for Java
  - `https://java.testcontainers.org/`
- Martin Fowler「TestPyramid」（2012年）
  - `https://martinfowler.com/bliki/TestPyramid.html`
