# 付録B.09 インターフェイスの進化

第7章で学んだインターフェイスのdefaultメソッド・staticメソッド・privateメソッドが、コンパイラーとJVMのなかでどう扱われているかを、`javac`のメッセージと`javap`、実行結果で観察しながら学ぶ節です。
同じメソッドが複数の場所にあるときにどれが呼ばれるのか、既存のインターフェイスにメソッドを足すと何が起きるのかを、規則の根拠（言語仕様とJVMの仕様）とあわせて確かめます。

## この節の読み方

この節の解説のうち、事実を述べた段落には`<!-- claim: … -->`という印を付けています（GitHubの画面では見えません）。
印の名前は同じディレクトリの`claims.yml`に対応し、そこにその主張の確かめ方（コンパイラーのメッセージ、バイトコードの照合、実行結果、出典）を書いています。
リポジトリのルートで`npm run check:deep-dive -- b09`を実行すると、すべての主張をJDK 25で確かめ直せます。
「観察する」の欄に載せた出力は、この検査が実際に出したものです（定数プールの番号`#12`などは省いています）。

## ファイル構成

### 観察用のプログラム（verify/）

| ファイル | 観察すること |
|---|---|
| `InterfaceMembers.java` | インターフェイスに書けるメンバーと暗黙の修飾子、クラスファイルのフラグ、呼び出し命令 |
| `VersionDefault.java`・`VersionPrivate.java`・`VersionSealed.java` | 各機能が入った版（`javac --release`で確かめる） |
| `ResolutionRules.java` | 同じシグネチャのメソッドが複数あるときに選ばれるメソッド |
| `FunctionalInterfaces.java` | 関数型インターフェイスの抽象メソッドの数え方 |
| `BinaryCompatibility.java` | インターフェイスにメソッドを足したあと、古いクラスファイルを使うと起きること |
| `DefaultMethodsInJdk.java` | 標準ライブラリにdefaultメソッドとして足されたメソッド |
| `DefaultInlining.java` | defaultメソッドのインライン化（JITコンパイラー） |

`verify/`のプログラムはパッケージを持たないので、`verify/`ディレクトリでそのままコンパイル・実行できます。

```bash
cd verify
javac InterfaceMembers.java && java InterfaceMembers && javap -v -p 'InterfaceMembers$Greeter'
javac ResolutionRules.java && java ResolutionRules
javac BinaryCompatibility.java && java BinaryCompatibility
```

`verify/compile-errors/`には、インターフェイスの規則に反するためにコンパイルエラーになる書き方を、1ファイルに1つずつ置いています。
このディレクトリのファイルはコンパイルできないのが正しい状態です。

```bash
cd verify/compile-errors
javac DiamondConflict.java
```

### 体験用のデモ（src/）

第7章の「Java 8以降のインターフェイス進化」と「高度な設計パターン」の内容を、少し大きなプログラムで試すためのデモです。

- `PaymentProcessorEvolution.java`
  - 決済処理のインターフェイスを、defaultメソッドで段階的に拡張する（テンプレートメソッド、非同期処理）
  - 取引IDと時刻は実行のたびに変わる
- `DiamondProblemDemo.java`
  - ダイヤモンド継承、継承の優先順位の規則、`インターフェイス名.super.メソッド名()`による呼び分け
- `MixinPatternDemo.java`
  - 小さなインターフェイス（Timestamped・Identifiable・Versioned・Printable・Cacheable・Validatable）を組み合わせるMixinの設計
  - インターフェイスはインスタンスフィールドを持てないので、キャッシュの入れ物は実装クラスが持つ
- `EnhancedFunctionalInterface.java`
  - `Function`と`Predicate`を拡張し、defaultメソッドで条件付き適用・リトライ・メモ化・関数の合成を加える
  - リトライの例は`Math.random()`で失敗させるので、結果は実行のたびに変わる
- `InterfaceSeparationDemo.java`
  - インターフェイス分離の原則に従い、参照・保存・通知・レポート・認証などの責務ごとにインターフェイスを分ける

```bash
javac -d out src/main/java/com/example/interfaces/*.java
java -cp out com.example.interfaces.DiamondProblemDemo
```

## 1. インターフェイスに書けるもの

### 版ごとの変化

インターフェイスに書けるものは、Javaの版とともに増えてきました。

| 版 | 加わったもの | 出典 |
|---|---|---|
| Java 8 | defaultメソッド、staticメソッド | JEP 126（JSR 335） |
| Java 9 | privateメソッド（インスタンスメソッドとstaticメソッド） | JEP 213 |
| Java 17 | sealedインターフェイス（`permits`で実装できるクラスを限る） | JEP 409 |

JDK 25の`javac`は、`--release`で古い版の言語としてコンパイルできます。
privateメソッドを持つ`verify/VersionPrivate.java`は`--release 8`ではコンパイルできず、sealedインターフェイスの`verify/VersionSealed.java`は`--release 16`ではコンパイルできません。

<!-- output: private-java9 -->
```
verify/VersionPrivate.java:14: error: private interface methods are not supported in -source 8
```
<!-- /output -->

<!-- claim: private-java9 -->

<!-- output: sealed-java17 -->
```
verify/VersionSealed.java:5: error: sealed classes are not supported in -source 16
```
<!-- /output -->

<!-- claim: sealed-java17 -->

JDK 25の`javac`が受け付ける最も古い版は8なので、defaultメソッドが7で書けないことはこの方法では確かめられません。
代わりに、`--release 8`でコンパイルしたクラスファイルの版を見ます。
版52はJava 8のクラスファイルの版で、defaultメソッドとstaticメソッドを含むインターフェイスは`--release 8`でコンパイルできます。

<!-- output: default-static-java8 -->
```
major version: 52
```
<!-- /output -->

<!-- claim: default-static-java8 -->

sealedインターフェイスのクラスファイルには、実装を許すクラスの一覧が`PermittedSubclasses`属性として入ります。
JVMSの5.3.5は、一覧にないクラスがこのインターフェイスを実装していると、そのクラスを読み込むときに`IncompatibleClassChangeError`にすると決めています（コンパイラーだけでなくJVMも制限を守らせます）。
sealedの使い方は付録B.10で扱います。

<!-- output: sealed-permitted -->
```
PermittedSubclasses:
VersionSealed$Circle
VersionSealed$Square
```
<!-- /output -->

<!-- claim: sealed-permitted -->

`permits`に挙げていないクラスから実装しようとすると、コンパイルエラーになります。

<!-- output: not-permitted -->
```
verify/compile-errors/NotPermitted.java:9: error: class is not allowed to extend sealed class: Shape (as it is not listed in its 'permits' clause)
```
<!-- /output -->

<!-- claim: not-permitted -->

### 暗黙の修飾子

インターフェイスのメンバーには、書かなくても付く修飾子があります。
`verify/InterfaceMembers.java`は、次のインターフェイスの各メンバーの修飾子をリフレクションで表示します。

```java
interface Greeter {
    String PREFIX = "Hello, ";          // フィールド
    String name();                      // 抽象メソッド
    default String greet() { ... }      // defaultメソッド
    static Greeter of(String name) { ... }      // staticメソッド
    private String decorate(String text) { ... } // privateメソッド
}
```

<!-- output: implicit-modifiers -->
```
[Hello, Java]
field PREFIX: public static final
method decorate: private
method greet: public (default)
method name: public abstract
method of: public static
```
<!-- /output -->

修飾子を書かないフィールドは`public static final`（定数）に、修飾子を書かないメソッドは`public abstract`になります。
defaultメソッドとstaticメソッドも、`private`を付けない限り`public`です。
<!-- claim: implicit-modifiers -->

インターフェイスが持てないのはインスタンスフィールド（オブジェクトごとの状態）で、フィールドそのものは持てます。
ただし暗黙に`final`なので、代入しようとするとコンパイルエラーになります。

<!-- output: assign-constant -->
```
verify/compile-errors/AssignConstant.java:8: error: cannot assign a value to static final variable LIMIT
```
<!-- /output -->

<!-- claim: assign-constant -->

インターフェイスのメソッドが暗黙に`public`であることは、実装クラスで`public`を付け忘れたときのエラーの原因にもなります。
オーバーライドするメソッドは、元のメソッドより公開の範囲を狭められないためです。

<!-- output: weaker-access -->
```
verify/compile-errors/WeakerAccess.java:8: error: area() in Square cannot implement area() in Shape
attempting to assign weaker access privileges; was public
```
<!-- /output -->

<!-- claim: weaker-access -->

## 2. クラスファイルに記録されたインターフェイス

### メソッドのフラグ

`javap -v -p 'InterfaceMembers$Greeter'`で、クラスファイルに記録されたフラグを見ます。

<!-- output: member-flags -->
```
  public static final java.lang.String PREFIX;
    flags: (0x0019) ACC_PUBLIC, ACC_STATIC, ACC_FINAL
  public abstract java.lang.String name();
    flags: (0x0401) ACC_PUBLIC, ACC_ABSTRACT
  public default java.lang.String greet();
    flags: (0x0001) ACC_PUBLIC
  public static InterfaceMembers$Greeter of(java.lang.String);
    flags: (0x0009) ACC_PUBLIC, ACC_STATIC
  private java.lang.String decorate(java.lang.String);
    flags: (0x0002) ACC_PRIVATE
```
<!-- /output -->

`ACC_ABSTRACT`を持つのは抽象メソッドだけです。
クラスファイルには「default」というフラグはなく、インターフェイスのメソッドのうち`ACC_ABSTRACT`も`ACC_STATIC`も`ACC_PRIVATE`も持たないものが、defaultメソッドです（`javap`の`default`という表示は、そこから読み取ったものです）。
<!-- claim: member-flags -->

Java仮想マシン仕様（JVMS）の4.6は、版が52.0未満のクラスファイルでは、インターフェイスのメソッドがすべて`ACC_PUBLIC`と`ACC_ABSTRACT`を持つことを求めています。
52.0以上では、`ACC_PUBLIC`か`ACC_PRIVATE`のどちらか1つを持てばよくなりました。
JVMの側でも、Java 8で「インターフェイスのメソッドは実装を持たない」という前提が外れたことがわかります。
<!-- claim: jvms-interface-flags -->

### 呼び出し命令

`verify/InterfaceMembers.java`の`main`は、staticメソッド`Greeter.of`とdefaultメソッド`greet`を呼びます。
`javap -c -p InterfaceMembers`で見ると、次の命令になります。

<!-- output: call-instructions -->
```
2: invokestatic // InterfaceMethod InterfaceMembers$Greeter.of:(Ljava/lang/String;)LInterfaceMembers$Greeter;
7: invokeinterface // InterfaceMethod InterfaceMembers$Greeter.greet:()Ljava/lang/String;
```
<!-- /output -->

インターフェイスのstaticメソッドは、クラスのstaticメソッドと同じ`invokestatic`で呼ばれます。
ただし参照の種類は、クラスのメソッドへの参照（`Method`）ではなく、インターフェイスのメソッドへの参照（`InterfaceMethod`）です。
defaultメソッドは抽象メソッドと同じ`invokeinterface`で呼ばれ、呼び出す側からは区別がありません。
<!-- claim: call-instructions -->

defaultメソッドからprivateメソッドを呼ぶ命令は、コンパイルするときの版で変わります。
`verify/VersionPrivate.java`を`--release 10`と`--release 11`でコンパイルして見比べると、次のようになります。

<!-- output: private-call-10 -->
```
invokespecial // InterfaceMethod decorate:(Ljava/lang/String;)Ljava/lang/String;
```
<!-- /output -->

<!-- output: private-call-11 -->
```
invokeinterface // InterfaceMethod decorate:(Ljava/lang/String;)Ljava/lang/String;
```
<!-- /output -->

Java 10までは`invokespecial`、Java 11からは`invokeinterface`です。
Java 11で入った、入れ子のクラスどうしが互いの`private`メンバーを使えるしくみ（ネストメイト、JEP 181）に合わせて変わりました。
クラスの`private`メソッドの呼び出しが`invokevirtual`に変わったのと同じ変更です（付録B.04）。
<!-- claim: private-call-10, private-call-11 -->

## 3. staticメソッドは継承されない

クラスのstaticメソッドはサブクラスに継承され、サブクラスの名前やインスタンスからも呼べます。
インターフェイスのstaticメソッドは違い、実装クラスにもサブインターフェイスにも継承されません。
呼べるのは、`Greeter.hello()`のように、宣言したインターフェイスの名前を付けたときだけです。

<!-- output: static-not-inherited-class -->
```
verify/compile-errors/StaticViaImplClass.java:13: error: cannot find symbol
symbol: method hello()
location: class Impl
```
<!-- /output -->

<!-- claim: static-not-inherited-class -->

<!-- output: static-not-inherited-interface -->
```
verify/compile-errors/StaticViaSubinterface.java:13: error: cannot find symbol
symbol: method hello()
location: interface Polite
```
<!-- /output -->

<!-- claim: static-not-inherited-interface -->

インターフェイスの型の変数を通して呼ぶこともできません。

<!-- output: static-via-instance -->
```
verify/compile-errors/StaticViaInstance.java:10: error: illegal static interface method call
the receiver expression should be replaced with the type qualifier 'Greeter'
```
<!-- /output -->

<!-- claim: static-via-instance -->

クラスは複数のインターフェイスを実装できるので、staticメソッドが継承されると、別々のインターフェイスにある同じ名前のstaticメソッドがぶつかります。
宣言したインターフェイスの名前でしか呼べないようにしておけば、この衝突は起きません。
`List.of`や`Comparator.comparing`のようなファクトリメソッドを、実装クラスと関係なくインターフェイスに置けるのはこのためです。

## 4. どのメソッドが選ばれるか

### 3つの規則

同じシグネチャのメソッドが複数の場所にあるとき、どれが使われるかは次の規則で決まります。

1. クラス（実装クラス自身とスーパークラス）のメソッドが、インターフェイスのdefaultメソッドより優先される
2. インターフェイスどうしでは、より具体的なもの（サブインターフェイス）のdefaultメソッドが優先される
3. それでも決まらないときはコンパイルエラーになり、クラスでオーバーライドして決める

`verify/ResolutionRules.java`は、3つの規則の場合を1つずつ実行します。

<!-- output: resolution-rules -->
```
規則1: Base.who（クラス）
規則2: Child.who
規則3: Resolver.who → Left.who + Right.who
Named型の変数から: Base.who（クラス）
```
<!-- /output -->

規則2の`SpecificWins`は`implements Parent, Child`と`Parent`を直接書き並べていますが、`Child`が`Parent`のメソッドをオーバーライドしているので、`Child`のメソッドになります。
最後の行のように、インターフェイスの型の変数から呼んでも、選ばれるメソッドは同じです。
<!-- claim: resolution-rules -->

Java言語仕様（JLS）では、この規則は「継承」の定義として書かれています。
8.4.8は、スーパークラスから実装のあるメソッドを受け継いでいるとき、インターフェイスの同じシグネチャのメソッドは継承しないと決めています（規則1）。
また、ほかのスーパータイプがオーバーライドしているメソッドも継承しません（規則2）。
規則1は「クラスが勝つ」、規則2は「より具体的なインターフェイスが勝つ」とも言われます。

### 選ぶのは実行時のJVM

defaultメソッドは、実装クラスのクラスファイルにコピーされません。
`javap -p 'ResolutionRules$SpecificWins'`で見ると、クラスにはコンストラクターしかありません。

<!-- output: no-copy -->
```
class ResolutionRules$SpecificWins implements ResolutionRules$Parent,ResolutionRules$Child {
ResolutionRules$SpecificWins();
}
```
<!-- /output -->

<!-- claim: no-copy -->

呼び出すメソッドは、実行時にJVMが選びます。
JVMSの5.4.6（メソッドの選択）は、まずオブジェクトのクラス自身を、次にスーパークラスを順にさかのぼってメソッドを探す、と決めています。
そこで見つからなければ「最も具体的なスーパーインターフェイスのメソッド」を求め、抽象でないものがちょうど1つならそれを選びます。
クラスを先に探すことが規則1に、最も具体的なものを選ぶことが規則2に当たり、コンパイラーとJVMが同じ規則で動いています。
<!-- claim: jvms-selection -->

### 規則1はスーパークラスの抽象メソッドにも効く

規則1の「クラスのメソッド」には、スーパークラスの抽象メソッドも含まれます。
抽象クラスが`public abstract String who();`を宣言していると、インターフェイスに`default String who()`があっても、それは実装として使われません。

<!-- output: abstract-superclass-wins -->
```
verify/compile-errors/AbstractSuperclassWins.java:13: error: Sub is not abstract and does not override abstract method who() in Base
```
<!-- /output -->

<!-- claim: abstract-superclass-wins -->

### 決まらないとき（ダイヤモンド問題）

継承関係のない2つのインターフェイスに同じシグネチャのdefaultメソッドがあり、両方を実装したクラスがオーバーライドしないと、コンパイルエラーになります。

<!-- output: diamond-conflict -->
```
verify/compile-errors/DiamondConflict.java:16: error: types Left and Right are incompatible;
class Both inherits unrelated defaults for who() from types Left and Right
```
<!-- /output -->

<!-- claim: diamond-conflict -->

一方が抽象メソッドで、もう一方がdefaultメソッドのときも同じです。
defaultメソッドが、別のインターフェイスの抽象メソッドの実装になることはありません。

<!-- output: abstract-and-default -->
```
verify/compile-errors/AbstractAndDefault.java:13: error: Both is not abstract and does not override abstract method who() in WithAbstract
```
<!-- /output -->

<!-- claim: abstract-and-default -->

### インターフェイス名.super.メソッド名()

クラスでオーバーライドしたうえで、`Left.super.who()`と書くと、`Left`のdefaultメソッドを呼べます。
`javap -c -p 'ResolutionRules$Resolver'`で見ると、この呼び出しは`invokespecial`です。

<!-- output: super-call-invokespecial -->
```
1: invokespecial // InterfaceMethod ResolutionRules$Left.who:()Ljava/lang/String;
5: invokespecial // InterfaceMethod ResolutionRules$Right.who:()Ljava/lang/String;
```
<!-- /output -->

`invokespecial`は、オブジェクトの実際のクラスによらず、指定したメソッドを呼ぶ命令です（`super.メソッド名()`と同じ）。
`invokeinterface`で呼ぶと、オーバーライドした`Resolver.who`自身が選ばれて、無限に呼び出し続けることになります。
<!-- claim: super-call-invokespecial -->

`インターフェイス名.super`に書けるのは、直接のスーパーインターフェイス（`implements`や`extends`に書いたもの）だけです。
`Child extends Parent`のとき、`implements Child`のクラスから`Parent.super.who()`とは書けません。

<!-- output: super-call-direct-only -->
```
verify/compile-errors/IndirectSuperCall.java:19: error: not an enclosing class: Parent
```
<!-- /output -->

`implements Parent, Child`と直接書き並べても、`Child`が`Parent`のメソッドをオーバーライドしているので書けません。
オーバーライドされたメソッドを横から呼べると、`Child`がオーバーライドした意味がなくなるためです。

<!-- output: super-call-redundant -->
```
verify/compile-errors/RedundantSuperCall.java:19: error: bad type qualifier Parent in default super call
redundant interface Parent is extended by Child
```
<!-- /output -->

<!-- claim: super-call-direct-only, super-call-redundant -->

## 5. defaultメソッドとObjectのメソッド

インターフェイスに`default String toString()`のように、`Object`の`public`メソッドと同じシグネチャのdefaultメソッドを書くと、コンパイルエラーになります。

<!-- output: default-object-method -->
```
verify/compile-errors/DefaultObjectMethod.java:5: error: default method toString in interface DefaultObjectMethod overrides a member of java.lang.Object
```
<!-- /output -->

どのクラスも`Object`から`toString`・`equals`・`hashCode`の実装を受け継いでいるので、規則1により、インターフェイスのdefaultメソッドが使われることはありません。
書いても決して呼ばれないメソッドなので、言語仕様（JLS 9.4.1.2）はこれをエラーにしています。
<!-- claim: default-object-method -->

一方、`Object`のメソッドを抽象メソッドとして宣言し直すことはできます。
次の節で見る`Comparator`は、`boolean equals(Object obj);`を宣言して、APIドキュメントに比較器どうしの等しさの約束を書いています。

## 6. 関数型インターフェイス

ラムダ式（第11章）で実装を書けるのは、関数型インターフェイス（抽象メソッドが1つのインターフェイス）です。
この「1つ」の数え方には、2つの例外があります。

- defaultメソッドとstaticメソッドは数えない（抽象メソッドではない）
- `Object`の`public`メソッドと同じシグネチャの抽象メソッドは数えない（どの実装クラスも`Object`から実装を受け継ぐ）

`verify/FunctionalInterfaces.java`は、標準ライブラリのインターフェイスが宣言している抽象メソッドを数えます。

<!-- output: functional-count -->
```
Comparator: 抽象メソッド [compare, equals]、Objectのメソッドを除くと 1 個、defaultメソッド 7 個
Function: 抽象メソッド [apply]、Objectのメソッドを除くと 1 個、defaultメソッド 2 個
Runnable: 抽象メソッド [run]、Objectのメソッドを除くと 1 個、defaultメソッド 0 個
[a, bb, ccc]
```
<!-- /output -->

`Comparator`は`compare`と`equals`の2つの抽象メソッドを宣言していますが、`equals`は数えないので関数型インターフェイスで、ラムダ式で作れます。
多くのdefaultメソッド（`reversed`・`thenComparing`など）を持つことも妨げになりません。
JLS 9.8は、このほかにsealedインターフェイスは関数型インターフェイスにならないとも決めています。
<!-- claim: functional-count -->

`@FunctionalInterface`を付けると、コンパイラーがこの条件を確かめ、満たさなければエラーにします。
付けなくても条件を満たせばラムダ式で実装できますが、あとから抽象メソッドを足してしまう誤りを防げます。

<!-- output: functional-annotation -->
```
verify/compile-errors/TwoAbstractMethods.java:2: error: Unexpected @FunctionalInterface annotation
TwoAbstractMethods is not a functional interface
multiple non-overriding abstract methods found in interface TwoAbstractMethods
```
<!-- /output -->

<!-- claim: functional-annotation -->

## 7. 既存のインターフェイスにメソッドを足す

JEP 126は、defaultメソッド（当時は仮想拡張メソッドと呼んでいた）の目的を、インターフェイスをソースとバイナリの互換性を保ったまま拡張できるようにすることとしています。
<!-- claim: default-purpose -->

ここでは「ソースの互換性」（実装クラスをコンパイルし直せるか）と「バイナリの互換性」（コンパイル済みのクラスファイルをそのまま使えるか）に分けて確かめます。

### ソースの互換性

インターフェイスに抽象メソッドを足すと、それを実装していない既存のクラスは、コンパイルし直したときにエラーになります。

<!-- output: source-incompatible -->
```
verify/compile-errors/MissingAbstract.java:10: error: Square is not abstract and does not override abstract method perimeter() in Shape
```
<!-- /output -->

<!-- claim: source-incompatible -->

### バイナリの互換性

ライブラリのインターフェイスが新しくなっても、それを実装した利用者のクラスはコンパイルし直されないまま使われることがあります。
`verify/BinaryCompatibility.java`は、この状況を作ります。
実装クラス`Square`は古い版のインターフェイス（`area()`だけ）でコンパイルし、呼び出す側の`Client`は新しい版のインターフェイスでコンパイルして、両方を1つのクラスローダーで読み込みます。
コンパイルには、プログラムから`javac`を使える`javax.tools.JavaCompiler`を使っています。

<!-- output: binary-compat -->
```
抽象メソッドを足した: java.lang.AbstractMethodError: Receiver class Square does not define or inherit an implementation of the resolved method 'abstract double perimeter()' of interface Shape.
defaultメソッドを足した: perimeter = -1.0
2つのインターフェイスに同じdefaultメソッドを足した: java.lang.IncompatibleClassChangeError: Conflicting default methods: Left.who Right.who
```
<!-- /output -->

1行目のように、抽象メソッドを足しても古い`Square`は問題なく読み込まれ、足したメソッドを呼んだときに初めて`AbstractMethodError`になります。
2行目のように、defaultメソッドとして足せば、古い`Square`からもそのdefaultメソッドが呼ばれます。
JLS 13.5.7は、defaultメソッドを足すことを、既存のクラスファイルとの互換性を壊さない変更としています。
ただし3行目のように、無関係な2つのインターフェイスに同じシグネチャのdefaultメソッドを足すと、両方を実装した古いクラスで呼んだときに`IncompatibleClassChangeError`になります。
4で見たコンパイルエラー（ダイヤモンド問題）が、実行時に表れたものです。
<!-- claim: binary-compat -->

### 標準ライブラリの例

Java 8では、ラムダ式とStream APIを既存のコレクションから使えるように、コレクションのインターフェイスに多くのdefaultメソッドが足されました。
`verify/DefaultMethodsInJdk.java`は、メソッドがdefaultメソッドかstaticメソッドかを、リフレクションの`Method.isDefault()`で調べます。

<!-- output: jdk-defaults -->
```
Iterable.forEach: default
Collection.stream: default
Collection.removeIf: default
Collection.toArray: default
List.sort: default
List.reversed: default
Map.getOrDefault: default
List.of: static
Comparator.comparing: static
Collection.size: abstract
```
<!-- /output -->

<!-- claim: jdk-defaults -->

APIドキュメントの「Since」で、それぞれのメソッドが足された版がわかります。

| メソッド | 種類 | Since |
|---|---|---|
| `Iterable.forEach`、`Collection.stream`・`removeIf`、`List.sort`、`Map.getOrDefault` | default | 1.8 |
| `Comparator.comparing` | static | 1.8 |
| `List.of` | static | 9 |
| `Collection.toArray(IntFunction)` | default | 11 |
| `List.reversed` | default | 21 |

<!-- claim: jdk-since -->

`List.reversed`は、Java 21で順序のあるコレクションをまとめる`SequencedCollection`が入ったときに足されたメソッドです。
Java 8のあとも、インターフェイスの拡張には同じ方法が使われ続けています。

## 8. defaultメソッドとJITコンパイラー

defaultメソッドも、ふつうのメソッドと同じようにJITコンパイラーの最適化の対象になります。
`verify/DefaultInlining.java`は、defaultメソッド`area()`を繰り返し呼びます。
`-XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining`を付けて実行すると、JITコンパイラー（C2）が`area()`を呼び出し箇所にインライン化したことがわかります。

<!-- output: default-inlining -->
```
@ 13 DefaultInlining$Shape::area (14 bytes) inline
@ 13 DefaultInlining$Shape::area (14 bytes) inline (hot)
```
<!-- /output -->

`inline (hot)`は、よく呼ばれる呼び出し箇所としてC2がインライン化したことを表します。
`hot`の付かない行は、その前の段階のC1コンパイラーの判断です。
呼び出し箇所に現れるクラスが`Square`だけなので、ふつうのメソッドと同じように呼び出し先を1つに決めて展開できます。
インライン化の条件の詳しいことは付録B.04で扱います。
<!-- claim: default-inlining -->

## よくある誤解

- インターフェイスはフィールドを持てない
  - 持てないのはインスタンスフィールドで、`public static final`の定数は持てる（1）
  <!-- claim: implicit-modifiers -->
- インターフェイスのstaticメソッドは、クラスのstaticメソッドと同じように実装クラスから呼べる
  - 実装クラスにもサブインターフェイスにも継承されず、宣言したインターフェイスの名前でしか呼べない（3）
  <!-- claim: static-not-inherited-class, static-not-inherited-interface -->
- defaultメソッドは、実装クラスにコピーされる
  - クラスファイルにはコピーされず、呼び出すたびにJVMがメソッドの選択の規則で選ぶ（4）
  <!-- claim: no-copy -->
- インターフェイスのdefaultメソッドで、スーパークラスの抽象メソッドを実装できる
  - スーパークラスのメソッドは抽象メソッドでも優先されるので、実装クラスでオーバーライドする必要がある（4）
  <!-- claim: abstract-superclass-wins -->
- `インターフェイス名.super.メソッド名()`で、どのスーパーインターフェイスのメソッドでも呼べる
  - 直接のスーパーインターフェイスで、ほかのスーパーインターフェイスにオーバーライドされていないものだけ（4）
  <!-- claim: super-call-direct-only -->
- インターフェイスに`toString`のdefaultメソッドを書けば、実装クラスの文字列表現を決められる
  - `Object`の`public`メソッドと同じシグネチャのdefaultメソッドはコンパイルエラーになる（5）
  <!-- claim: default-object-method -->
- 関数型インターフェイスは、メソッドを1つしか宣言できない
  - defaultメソッドとstaticメソッドはいくつあってもよく、`Object`のメソッドと同じシグネチャの抽象メソッドも数えない（6）
  <!-- claim: functional-count -->
- defaultメソッドを足せば、既存のインターフェイスはいつでも安全に拡張できる
  - 別のインターフェイスに同じシグネチャのdefaultメソッドがあると、両方を実装したクラスはコンパイルできなくなり、古いクラスファイルでは呼んだときに`IncompatibleClassChangeError`になる（7）
  <!-- claim: diamond-conflict, binary-compat -->

## 参考資料

- Java言語仕様（Java SE 25）
  - 8.1.5「Superinterfaces」（sealedインターフェイスの実装）
  - 8.4.8「Inheritance, Overriding, and Hiding」（クラスが継承するメソッド、staticメソッドを継承しないこと）
  - 8.4.8.4「Inheriting Methods with Override-Equivalent Signatures」
  - 9.2「Interface Members」
  - 9.3「Field (Constant) Declarations」
  - 9.4「Method Declarations」（暗黙の修飾子）
  - 9.4.1.2「Requirements in Overriding」（`Object`のメソッドとdefaultメソッド）
  - 9.4.1.3「Inheriting Methods with Override-Equivalent Signatures」
  - 9.8「Functional Interfaces」
  - 13.5.7「Interface Method Declarations」（バイナリの互換性）
  - 15.12.1「Compile-Time Step 1」（`インターフェイス名.super`）
  - 15.12.3「Compile-Time Step 3」（staticなインターフェイスのメソッドの呼び出し）
  - `https://docs.oracle.com/javase/specs/jls/se25/html/`
- Java仮想マシン仕様（Java SE 25）
  - 4.6「Methods」（インターフェイスのメソッドのフラグ）
  - 4.7.31「The PermittedSubclasses Attribute」
  - 5.3.5「Deriving a Class from a class File Representation」
  - 5.4.6「Method Selection」
  - 6.5「Instructions」（`invokeinterface`・`invokespecial`・`invokestatic`）
  - `https://docs.oracle.com/javase/specs/jvms/se25/html/`
- APIドキュメント（Java SE 25）
  - `java.lang.FunctionalInterface`、`java.lang.Iterable`、`java.util.Collection`、`java.util.List`、`java.util.Map`、`java.util.Comparator`、`javax.tools.ToolProvider`
  - `https://docs.oracle.com/en/java/javase/25/docs/api/`
- JEP
  - JEP 126「Lambda Expressions & Virtual Extension Methods」（`https://openjdk.org/jeps/126`）
  - JEP 181「Nest-Based Access Control」（`https://openjdk.org/jeps/181`）
  - JEP 213「Milling Project Coin」（`https://openjdk.org/jeps/213`）
  - JEP 409「Sealed Classes」（`https://openjdk.org/jeps/409`）
