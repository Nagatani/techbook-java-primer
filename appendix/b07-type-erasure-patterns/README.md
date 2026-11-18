# 付録B.07 型消去とジェネリクス

第10章で学んだジェネリクスが、コンパイルのあとでどのような姿になるか（型消去）を、`javap`とリフレクションで実際に観察しながら学ぶ節です。
型消去を知ると、ジェネリクスに「できないこと」がなぜあるのか、未検査の警告が何を知らせているのかがわかります。

## この節の読み方

この節の解説のうち、事実を述べた段落には`<!-- claim: … -->`という印を付けています（GitHubの画面では見えません）。
印の名前は同じディレクトリの`claims.yml`に対応し、そこにその主張の確かめ方（バイトコードの照合、コンパイラーのメッセージ、実行結果、出典）を書いています。
リポジトリのルートで`npm run check:deep-dive -- b07`を実行すると、すべての主張をJDK 25で確かめ直せます。
「観察する」の欄に載せた出力は、この検査が実際に出したものです（定数プールの番号`#12`などは省いています）。

## ファイル構成

### 観察用のプログラム（verify/）

| ファイル | 観察すること |
|---|---|
| `ErasedSignatures.java` | 消去後のシグネチャ、呼び出し側に入るキャスト、Signature属性（`javap`で見る） |
| `GenericInfo.java` | 実行時にわかる型引数とわからない型引数（リフレクション） |
| `Bridges.java` | ブリッジメソッドのフラグ・中身・リフレクションでの見え方 |
| `ArraysAndGenerics.java` | 配列とジェネリクスの実行時の違い |
| `InstanceofRefine.java` | パラメーター化型を書ける`instanceof` |
| `HeapPollution.java` | raw型と可変長引数によるヒープ汚染（わざと警告を出す） |
| `SafeVarargsUse.java`・`VarargsWarning.java` | `@SafeVarargs`の有無による警告の違い |
| `TypeTokens.java` | 型トークンとスーパータイプトークン |
| `JacksonTypeReference.java` | ライブラリ（Jackson）にある同じ形のクラス |
| `Wildcards.java` | ワイルドカード（`? extends`・`? super`） |

`verify/`のプログラムはパッケージを持たないので、`verify/`ディレクトリでそのままコンパイル・実行できます。

```bash
cd verify
javac ErasedSignatures.java && javap -s -p 'ErasedSignatures$Box'
javac Bridges.java && javap -p -v 'Bridges$Version' && java Bridges
javac -Xlint:unchecked HeapPollution.java && java HeapPollution
```

`verify/compile-errors/`には、型消去のためにコンパイルエラーになる書き方を、1ファイルに1つずつ置いています（6で使います）。
このディレクトリのファイルはコンパイルできないのが正しい状態です。

```bash
cd verify/compile-errors
javac NameClash.java
```

### 体験用のデモ（src/）

- `TypeErasureDemo.java`
  - 下の各クラスのデモを順に実行する
- `GenericBox.java`・`GenericService.java`
  - 型引数の違うオブジェクトが実行時には同じクラスであること、境界付きの型パラメーター
- `BridgeMethodExample.java`
  - `Comparable`の実装と、戻り値の型を型引数で決めたときのブリッジメソッドを、リフレクションで一覧にする
- `TypeToken.java`・`TypeReference.java`
  - スーパータイプトークン（10）を、`equals`や`getRawType`を備えた形で実装したもの
- `ReflectionTypeInfo.java`
  - フィールドとメソッドに書いた型引数を、入れ子やワイルドカード・型変数まで分解して表示する
- `GenericArrayWorkarounds.java`
  - 型パラメーターの配列を作れない問題の回避策（`Object[]`のキャストの危険、`Class<T>`、`List`）

```bash
javac -d out src/main/java/com/example/typeerasure/*.java
java -cp out com.example.typeerasure.TypeErasureDemo
```

## 1. 型消去とは

コンパイラーは、型引数を使って型の誤りを検査したあと、クラスファイルには型引数を取り除いた型（消去後の型）を書きます。
これを型消去（type erasure）と呼びます。
Java言語仕様（JLS）の4.6が決めている規則は、主に次のとおりです。

- パラメーター化型`G<T1,...,Tn>`は`G`（raw型）になる。`List<String>`は`List`
- 型パラメーターは、いちばん左の境界の消去になる。境界のない`T`は`Object`
- 配列`T[]`は、要素の型の消去の配列になる
- メソッドのシグネチャも、引数・戻り値の型を消去したものになる

`verify/ErasedSignatures.java`の`Box<T>`を`javap -s -p`で見ると、ソースの宣言の下に、消去後の型を表すディスクリプター（descriptor）が並びます。

<!-- output: erasure-rules -->
```
void set(T);
descriptor: (Ljava/lang/Object;)V
T get();
descriptor: ()Ljava/lang/Object;
T[] toArray(T[]);
descriptor: ([Ljava/lang/Object;)[Ljava/lang/Object;
void names(java.util.List<java.lang.String>);
descriptor: (Ljava/util/List;)V
```
<!-- /output -->

`Ljava/lang/Object;`は`Object`、`[Ljava/lang/Object;`は`Object[]`、`Ljava/util/List;`は型引数のない`List`を表します。
`T`は`Object`に、`List<String>`は`List`になっています。
<!-- claim: erasure-rules -->

境界のある型パラメーターは、その境界に消去されます。
`NumberBox<T extends Number>`の`get()`は`Number`を返すメソッドになり、境界が複数ある`Multi<T extends CharSequence & Comparable<T> & Serializable>`では、いちばん左の`CharSequence`になります。
<!-- claim: erasure-bounds, erasure-bounds-number -->

<!-- output: erasure-bounds -->
```
class ErasedSignatures$Multi<T extends java.lang.CharSequence & java.lang.Comparable<T> & java.io.Serializable> {
T first(T, T);
descriptor: (Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/CharSequence;
```
<!-- /output -->

## 2. 呼び出し側に入るキャスト

`Box<String>`の`get()`は、クラスファイルでは`Object`を返すメソッドです。
それでも`String s = box.get();`と書けるのは、コンパイラーが呼び出し側にキャストを入れるからです。
`ErasedSignatures`の`useBox`メソッドを`javap -c -p`で見ます。

<!-- output: checkcast-caller -->
```
3: invokevirtual // Method ErasedSignatures$Box.set:(Ljava/lang/Object;)V
7: invokevirtual // Method ErasedSignatures$Box.get:()Ljava/lang/Object;
10: astore_1
12: invokevirtual // Method ErasedSignatures$Box.get:()Ljava/lang/Object;
15: checkcast // class java/lang/String
18: astore_2
```
<!-- /output -->

`set("hello")`の引数にはキャストが入りません。
`Object o = box.get();`の後ろにもキャストはなく、結果をそのまま変数に入れて（`astore_1`）います。
`String s = box.get();`の後ろにだけ`checkcast`（`String`への検査付きキャスト）が入ります。
JLSの15.5も、実行時の型の誤りが起こりうる場所の1つとして「具象化できない型の操作を正しく保つために自動で入れたキャスト」を挙げています。
<!-- claim: checkcast-caller -->

ジェネリックなコードのなかにキャストが入ることもあります。
`Multi`の`first`は`a.compareTo(b)`を呼びますが、`T`は`CharSequence`に消去されるので、`Comparable`のメソッドを呼ぶ前に`Comparable`へのキャストが必要です。

<!-- output: checkcast-bound -->
```
1: checkcast // class java/lang/Comparable
5: invokeinterface // InterfaceMethod java/lang/Comparable.compareTo:(Ljava/lang/Object;)I
```
<!-- /output -->

<!-- claim: checkcast-bound -->

型引数で検査を済ませたコードでは、これらのキャストが失敗することはありません。
キャストが失敗するのは、8で見る「未検査の警告」を無視したときです。

## 3. なぜ型を消すのか

JLSの4.7は、すべてのジェネリック型を実行時に残す（具象化する）ことをしなかった判断を、型システムの設計でもっとも重要で議論を呼んだ決定の1つと述べています。
いちばんの動機は既存のコードとの互換性です。
ジェネリクスを使うように書き直したライブラリと、まだ書き直していないコードを、どちらの順で移行しても混ぜて使えること（移行の互換性、migration compatibility）を目指しました。
その代償として、ジェネリック型のすべてを実行時に残すこと（完全な具象化）はできない、とJLSは書いています。
<!-- claim: erasure-motivation -->

型引数を付けない`List`のような型（raw型）も、この互換性のために残っています。
JLSの4.8は、raw型を使えるのは古いコードとの互換性のための譲歩であり、新しいコードで使うことは強く勧めないとしています。
<!-- claim: raw-type-concession -->

実行時に型の情報がすべて残る型を、具象化可能な型（reifiable type）と呼びます。
JLSの4.7によると、主に次のものです。

- ジェネリックでないクラスやインターフェイス（`String`など）
- すべての型引数が`?`のパラメーター化型（`List<?>`）
- raw型（`List`）
- プリミティブ型
- 要素の型が具象化可能な配列（`String[]`）
<!-- claim: reifiable-types -->

`List<String>`や`T`は具象化可能な型ではありません。
6で見る制限の多くは「その操作には、具象化可能な型が必要」という規則から来ています。

## 4. 宣言に残る型引数（Signature属性）

型引数がクラスファイルからすべて消えるわけではありません。
クラス・フィールド・メソッドの宣言に書いた型は、消去前の形のまま、Signature属性という欄に残ります。
`javap -v`で`Box<T>`を見ると、消去後のディスクリプターとSignature属性が並びます。

<!-- output: signature-attribute -->
```
descriptor: Ljava/lang/Object;
Signature: // TT;
descriptor: (Ljava/lang/Object;)V
Signature: // (TT;)V
descriptor: ()Ljava/lang/Object;
Signature: // ()TT;
descriptor: ([Ljava/lang/Object;)[Ljava/lang/Object;
Signature: // ([TT;)[TT;
descriptor: (Ljava/util/List;)V
Signature: // (Ljava/util/List<Ljava/lang/String;>;)V
Signature: // <T:Ljava/lang/Object;>Ljava/lang/Object;
```
<!-- /output -->

最初の2行はフィールド`value`、続く3組は`set`・`get`・`toArray`、その次が`names`です。
`names`のディスクリプターは`(Ljava/util/List;)V`ですが、Signature属性には`List<Ljava/lang/String;>`と型引数が残っています。
`TT;`は型変数`T`、最後の行の`<T:Ljava/lang/Object;>`はクラスの型パラメーター`T`（境界は`Object`）を表します。
<!-- claim: signature-attribute -->

JVMはメソッドを呼ぶときにSignature属性を使いません。
Java仮想マシン仕様（JVMS）の4.7.9によると、OracleのJVMはクラスの読み込みやリンクのときにSignature属性を検査せず、消去前の型を返すクラスライブラリのメソッド（`Class.getGenericSuperclass`など）が読むときに検査します。
<!-- claim: signature-for-libraries -->

そのため、宣言に書いた型引数はリフレクションで読めます。
`verify/GenericInfo.java`の出力です。

<!-- output: reflection-declarations -->
```
フィールドnamesの型: java.util.List<java.lang.String>
フィールドscoresの型: java.util.Map<java.lang.String, java.util.List<java.lang.Integer>>
2つ目の型引数: java.util.List<java.lang.Integer>
getNamesの戻り値の型: java.util.List<java.lang.String>
getNamesの戻り値のクラス: java.util.List
StringListのスーパークラス: java.util.ArrayList<java.lang.String>
```
<!-- /output -->

`getGenericReturnType()`は`List<String>`を返し、`getReturnType()`は消去後の`List`を返します。
`ArrayList<String>`を継承したクラスでは、スーパークラスの型引数`String`も読めます。
<!-- claim: reflection-declarations -->

一方、オブジェクトそのものは型引数を持っていません。

<!-- output: instance-no-type-argument -->
```
同じクラスか: true
オブジェクトのクラス: java.util.ArrayList
クラスの型パラメーター: [E]
```
<!-- /output -->

`new ArrayList<String>()`と`new ArrayList<Integer>()`は同じ`ArrayList`クラスのオブジェクトです。
クラスからわかるのは、型パラメーターの名前`E`だけです。
<!-- claim: instance-no-type-argument -->

宣言に書いた型はわかるが、オブジェクトの型引数はわからない、という区別が、10の型トークンの土台になります。

## 5. ブリッジメソッド

### 観察する

`verify/Bridges.java`の`Version`は`Comparable<Version>`を実装し、`compareTo(Version)`だけを書いています。
`javap -p -v`で見ると、書いていない`compareTo(Object)`があります。

<!-- output: bridge-flags -->
```
public int compareTo(Bridges$Version);
flags: (0x0001) ACC_PUBLIC
public int compareTo(java.lang.Object);
flags: (0x1041) ACC_PUBLIC, ACC_BRIDGE, ACC_SYNTHETIC
```
<!-- /output -->

`compareTo(Object)`には`ACC_BRIDGE`（ブリッジメソッド）と`ACC_SYNTHETIC`（コンパイラーが作った）のフラグが付いています。
<!-- claim: bridge-flags -->

中身は、引数を`Version`にキャストして、ソースに書いた`compareTo(Version)`を呼ぶだけです。

<!-- output: bridge-body -->
```
public int compareTo(java.lang.Object);
2: checkcast // class Bridges$Version
5: invokevirtual // Method compareTo:(LBridges$Version;)I
8: ireturn
```
<!-- /output -->

<!-- claim: bridge-body -->

### なぜ必要か

`Comparable<T>`の`compareTo(T)`は、消去すると`compareTo(Object)`です。
`Comparable`を扱うジェネリックなコードは、消去後の`compareTo(Object)`を呼びます（2で見た`Multi`の`first`の`invokeinterface`がその例です）。
<!-- claim: checkcast-bound -->
ところが`Version`に書いた`compareTo(Version)`は、消去後もディスクリプターが`(LBridges$Version;)I`のままで、JVMから見ると別のメソッドです。
そこでコンパイラーは、`compareTo(Object)`として呼ばれたときに`compareTo(Version)`へ橋渡しするメソッドを足します。

JLSの15.12.4.5が決めているのは、「消去後のシグネチャで呼ばれても、オーバーライドしたメソッドが実行される」という結果です。
ブリッジメソッドは、その結果を実現する実装の方法として書かれています。
<!-- claim: bridge-implementation -->

ブリッジメソッドは、型引数で戻り値の型を決めたとき（`Processor<String>`の`String process(String)`）にも作られます。
ジェネリクスと関係のない、戻り値の型を狭めたオーバーライド（共変戻り値型、`Animal self()`を`Dog self()`でオーバーライド）でも同じです。
リフレクションでは`Method.isBridge()`で見分けられます。

<!-- output: bridge-reflection -->
```
Versionのメソッド:
int compareTo[Bridges$Version] bridge=false synthetic=false
int compareTo[java.lang.Object] bridge=true synthetic=true
UpperCaseのメソッド:
Object process[java.lang.String] bridge=true synthetic=true
String process[java.lang.String] bridge=false synthetic=false
Dogのメソッド:
Animal self[] bridge=true synthetic=true
Dog self[] bridge=false synthetic=false
```
<!-- /output -->

<!-- claim: bridge-reflection -->

### ブリッジメソッドのキャストが失敗するとき

raw型の`Comparable`を通すと、`Version`でない値を`compareTo`に渡すコードがコンパイルできてしまいます（未検査の警告は出ます）。
呼ばれるのはブリッジメソッドで、そのなかの`checkcast`が失敗します。
ソースに書いた`compareTo(Version)`の本体は実行されません。

<!-- output: bridge-cce -->
```
ClassCastException: Bridges$Version.compareTo
```
<!-- /output -->

<!-- claim: bridge-cce -->

ブリッジメソッドと同じシグネチャのメソッドを自分で書くことはできません。
`Comparable<ManualBridge>`を実装したクラスに`compareTo(Object)`を書くと、名前の衝突（name clash）になります。

<!-- output: manual-bridge-clash -->
```
ManualBridge.java:3: error: name clash: class ManualBridge has two methods with the same erasure, yet neither overrides the other
ManualBridge.java:4: error: name clash: compareTo(Object) in ManualBridge and compareTo(ManualBridge) in Comparable have the same erasure, yet neither overrides the other
```
<!-- /output -->

<!-- claim: manual-bridge-clash -->

## 6. 消去による制限

実行時に型引数がわからないため、次の書き方はコンパイルエラーになります。
`verify/compile-errors/`のファイルをコンパイルすると、表のメッセージが出ます。

| 書き方 | ファイル | javacのメッセージ | 理由 |
|---|---|---|---|
| `new T()` | `NewTypeVariable.java` | `unexpected type` | `T`が何のクラスか実行時にわからない |
| `new T[n]` | `GenericArrayCreation.java` | `generic array creation` | 配列の要素の型は具象化可能でなければならない |
| `new List<String>[n]` | `ParameterizedArray.java` | `generic array creation` | 同上 |
| `T.class` | `ClassLiteral.java` | `cannot select from a type variable` | `T`のクラスが実行時にわからない |
| `obj instanceof List<String>`（`obj`は`Object`） | `InstanceofUnsafe.java` | `Object cannot be safely cast to List<String>` | 実行時には`List`かどうかしか調べられない |
| `static T shared;` | `StaticTypeVariable.java` | `non-static type variable T cannot be referenced from a static context` | クラスは1つなので、型引数ごとの`static`フィールドは持てない |
| `class E<T> extends Exception` | `GenericThrowable.java` | `a generic class may not extend java.lang.Throwable` | JVMの`catch`は、ジェネリックでないクラスでしか働かない |
| `List<int>` | `PrimitiveTypeArgument.java` | `unexpected type` | 型引数には参照型しか書けない |
<!-- claim: no-new-type-variable, no-generic-array, no-parameterized-array, no-class-literal, instanceof-unsafe, no-static-type-variable, no-generic-throwable, no-primitive-type-argument -->

### instanceofとパラメーター化型

`instanceof`の右に`List<String>`を書けないのは、左の式の型が`Object`のように、何のリストかわからないときだけです。
左の式の型が`Collection<String>`なら、`List`かどうかを調べるだけで`List<String>`であることまで保証できるので、`c instanceof List<String>`と書けます（`verify/InstanceofRefine.java`）。
JLSの15.20.2によると、Java SE 15までは`instanceof`の右に具象化可能な型しか書けず、Java SE 16でこの制限がゆるめられました。
`javac --release 15 InstanceofRefine.java`とすると、`reifiable types in instanceof are not supported in -source 15`というエラーになります。
<!-- claim: instanceof-refine -->

### オーバーロードの衝突

型引数だけが違うオーバーロードは、消去後に同じシグネチャになるので宣言できません。

<!-- output: name-clash -->
```
NameClash.java:6: error: name clash: process(List<Integer>) and process(List<String>) have the same erasure
```
<!-- /output -->

<!-- claim: name-clash -->

型パラメーターだけが違う`put(T)`と`put(U)`も、どちらも`put(Object)`に消去されるので、同じエラーになります（`OverloadAfterErasure.java`）。
<!-- claim: name-clash-type-parameters -->

名前を変える（`processStrings`と`processIntegerList`）か、引数の型そのものを変えて区別します。
`src/`の`GenericService.java`は、名前で区別する例です。

## 7. 配列とジェネリクスの違い

型パラメーターの配列が作れない理由は、配列とジェネリクスの性質の違いにあります。
`verify/ArraysAndGenerics.java`の出力です。

<!-- output: arrays-reified -->
```
ArrayStoreException: java.lang.Integer
配列のクラス: [Ljava.lang.String; / [Ljava.lang.Integer;
リストのクラス: java.util.ArrayList / java.util.ArrayList
```
<!-- /output -->

配列は共変です（`String[]`を`Object[]`の変数に代入できる）。
そのかわり、配列は実行時に要素の型（`[Ljava.lang.String;`）を持ち、要素を代入するたびに検査します。
`String[]`に`Integer`を入れようとすると、その場で`ArrayStoreException`になります。
一方、`ArrayList<String>`と`ArrayList<Integer>`は実行時には同じクラスで、検査する手がかりがありません。
<!-- claim: arrays-reified -->

そのためジェネリクスは共変にせず、型の検査をコンパイル時に済ませます。
`List<String>`は`List<Object>`のサブタイプではなく、代入はコンパイルエラーになります（`verify/compile-errors/Invariance.java`）。
<!-- claim: generics-invariant -->

もし`new List<String>[10]`が作れると、`Object[]`の変数を通して`List<Integer>`を入れても、配列の検査は「`List`かどうか」しか調べられず、すり抜けてしまいます。
配列の安全のしくみとジェネリクスの安全のしくみが両立しないので、具象化できない型の配列は作れないのです。

## 8. raw型とヒープ汚染

パラメーター化型の変数が、その型でないオブジェクトを指している状態を、ヒープ汚染（heap pollution）と呼びます。
JLSの4.12.2によると、ヒープ汚染が起こるのは、未検査の警告が出る操作をしたときか、具象化できない要素型の配列を別の型の配列変数で扱ったときだけです。
<!-- claim: heap-pollution-definition -->

未検査の操作があると、javacは既定では箇所を示さず、次の2行だけを表示します。

<!-- output: unchecked-note -->
```
Note: HeapPollution.java uses unchecked or unsafe operations.
Note: Recompile with -Xlint:unchecked for details.
```
<!-- /output -->

<!-- claim: unchecked-note -->

`-Xlint:unchecked`を付けると、箇所ごとの警告になります。

<!-- output: unchecked-warnings -->
```
HeapPollution.java:17: warning: [unchecked] unchecked call to add(E) as a member of the raw type List
HeapPollution.java:28: warning: [unchecked] Possible heap pollution from parameterized vararg type T
HeapPollution.java:33: warning: [unchecked] unchecked generic array creation for varargs parameter of type T[]
```
<!-- /output -->

<!-- claim: unchecked-warnings -->

`verify/HeapPollution.java`を実行すると、例外がどこで起きるかがわかります。

<!-- output: heap-pollution-run -->
```
追加できた。要素数: 2
1. 取り出すときにClassCastException
2. class [Ljava.lang.Object; cannot be cast to class [Ljava.lang.String;
```
<!-- /output -->

1つ目は、raw型の`List`を通して`List<String>`に`42`を入れる例です。
入れるときには何も起きず（要素数は2になる）、取り出して`String`で受けるとき、つまり2で見た`checkcast`で初めて`ClassCastException`になります。
例外が起きる場所と、誤りを作った場所が離れてしまうのが、ヒープ汚染の厄介なところです。

2つ目は、型パラメーターの可変長引数`T... args`をそのまま返すメソッドです。
ジェネリックなメソッド`pickTwo`のなかで呼ぶと、`T`は`Object`に消去されているので、作られる配列は`Object[]`です。
それを`String[]`で受けると`ClassCastException`になります。
<!-- claim: heap-pollution-run -->

未検査の警告は「この箇所は、コンパイラーが型の安全を保証できない」という知らせです。
警告を消すために`@SuppressWarnings("unchecked")`を付けるのは、安全だと自分で確かめた箇所に限ります。

## 9. 可変長引数と@SafeVarargs

可変長引数は、呼び出し側で配列を作って渡すしくみです。
要素の型が`List<String>`のように具象化できない型だと、7で見たとおり本当の型の配列は作れず、ヒープ汚染の入口になります。
そのため、`@SafeVarargs`を付けないと、宣言と呼び出し側の両方に未検査の警告が出ます（`verify/VarargsWarning.java`）。

<!-- output: varargs-warning-without -->
```
VarargsWarning.java:11: warning: [unchecked] Possible heap pollution from parameterized vararg type List<? extends T>
VarargsWarning.java:20: warning: [unchecked] unchecked generic array creation for varargs parameter of type List<? extends String>[]
```
<!-- /output -->

<!-- claim: varargs-warning-without -->

`verify/SafeVarargsUse.java`は、同じメソッドに`@SafeVarargs`を付けたもので、`-Xlint:unchecked`を付けても警告が出ません。
JLSの9.6.4.7によると、`@SafeVarargs`は「このメソッドの可変長引数の扱いは安全だ」というプログラマーの表明です。
コンパイラーが安全を確かめるわけではありません。
<!-- claim: safevarargs-meaning -->

付けてよいのは、可変長引数の配列を読むだけで、書き換えたり外へ渡したりしないメソッドです。
8の`toArray`のように配列をそのまま返すメソッドに付けると、警告が消えるだけで、例外はそのまま起こります。
標準ライブラリでは`Collections.addAll`などに付いています。
<!-- claim: safevarargs-jdk -->

`@SafeVarargs`を付けられるのは、`static`・`final`・`private`のメソッドとコンストラクターだけです。
オーバーライドできるメソッドに付けるとコンパイルエラーになります（`verify/compile-errors/SafeVarargsOverridable.java`）。
メソッドの注釈はオーバーライドしたメソッドに引き継がれないので、JLSは「オーバーライドが起こる場所では使えない」と説明しています。
<!-- claim: safevarargs-overridable -->

## 10. 型トークンとスーパータイプトークン

実行時に型が必要なときは、型の情報を値として渡します。

### 型トークン（Class<T>）

`Class<T>`の引数を受け取ると、`Array.newInstance`で本当の`String[]`を作ったり、`Class.cast`で実行時に検査するキャストをしたりできます。
`verify/TypeTokens.java`の出力の前半です。

<!-- output: class-token -->
```
配列のクラス: String[]
最初のInteger: 7
```
<!-- /output -->

<!-- claim: class-token -->

ただし`Class`は具象化可能な型しか表せません。
`List<String>.class`とは書けず（クラスリテラルの文法に型引数の場所がない）、`List.class`の型は`Class<List>`です（`verify/compile-errors/ParameterizedClassLiteral.java`・`ClassOfList.java`）。
<!-- claim: class-literal-raw, class-literal-raw-type -->

### スーパータイプトークン

4で見たとおり、スーパークラスに書いた型引数は残ります。
そこで、抽象クラス`TypeRef<T>`の匿名サブクラスを`new TypeRef<List<String>>() { }`のように作り、コンストラクターのなかで`getClass().getGenericSuperclass()`から型引数を取り出します。

<!-- output: super-type-token -->
```
TypeRef<List<String>>: java.util.List<java.lang.String>
TypeRef<Map<String, List<Integer>>>: java.util.Map<java.lang.String, java.util.List<java.lang.Integer>>
```
<!-- /output -->

`List<String>`や`Map<String, List<Integer>>`のような、`Class`では表せない型を実行時に扱えます。
<!-- claim: super-type-token -->

匿名クラスの`{ }`を付けるのは、新しいサブクラスを作り、その宣言に型引数を書くためです。
`{ }`のない`new TypeRef<List<String>>()`は、型引数を宣言に残さないので役に立ちません。
`TypeRef`を抽象クラスにしておくと、書き忘れはコンパイルエラーになります（`verify/compile-errors/TokenWithoutBody.java`）。
<!-- claim: token-abstract -->

この方法にも限界があります。
ジェネリックメソッドのなかで`new TypeRef<T>() { }`と書くと、宣言に書かれているのは`T`なので、記録されるのも型変数`T`です。
呼び出し側が`String`を指定していても、それはわかりません。
<!-- claim: token-type-variable -->

<!-- output: token-type-variable -->
```
ジェネリックメソッドの中で作ると: T（型変数か: true）
```
<!-- /output -->

JSONを読み書きするライブラリJacksonの`TypeReference`も、この形のクラスです。
型パラメーターを1つ持つ抽象クラスで、コンストラクターは`protected`なので、`new TypeReference<List<String>>() {}`のように匿名クラスを作って使います。
`verify/JacksonTypeReference.java`をjackson-coreのJARとともに実行すると、次のように表示します。

<!-- output: jackson-type-reference -->
```
クラス: com.fasterxml.jackson.core.type.TypeReference
抽象クラスか: true
型パラメーター: [T]
コンストラクター: protected []
getTypeの戻り値: java.lang.reflect.Type
```
<!-- /output -->

<!-- claim: jackson-type-reference -->

## 11. ワイルドカード

ジェネリクスは共変でないので、`List<Number>`を受け取るメソッドに`List<Integer>`は渡せません。
ワイルドカードを使うと、受け取れる型の範囲を広げられます。

- 値を取り出すだけの引数（生産者）は`? extends T`にする
- 値を入れるだけの引数（消費者）は`? super T`にする

英語の頭文字を取って「PECS（Producer Extends, Consumer Super）」と覚えられます。
`verify/Wildcards.java`の出力です。

<!-- output: wildcards -->
```
sum(List<Integer>) = 6.0
sum(List<Double>) = 4.0
List<Number>: [1, 4, 9] / List<Object>: [1, 4]
Collections.copy: [10, 20, -]
```
<!-- /output -->

`sum(List<? extends Number>)`には`List<Integer>`も`List<Double>`も渡せます。
`addSquares(List<? super Integer>, int)`には`List<Number>`も`List<Object>`も渡せます。
<!-- claim: wildcards -->

`? extends Number`のリストから取り出した値は`Number`として使えますが、要素を追加することはできません。
実際の型引数が`Integer`か`Double`かわからないからです。

<!-- output: extends-no-add -->
```
ExtendsAdd.java:6: error: incompatible types: Integer cannot be converted to CAP#1
CAP#1 extends Number from capture of ? extends Number
```
<!-- /output -->

`CAP#1`は「何かわからない、`Number`のサブタイプ」を表す、コンパイラーが作った型の名前です（キャプチャ変換）。
<!-- claim: extends-no-add -->

標準ライブラリの`Collections.copy`も、この形で宣言されています。

<!-- output: collections-copy -->
```
public static <T> void copy(java.util.List<? super T>, java.util.List<? extends T>);
```
<!-- /output -->

<!-- claim: collections-copy -->

`Collections.max`の宣言には、型消去への配慮が見えます。
型パラメーターが`<T extends Object & Comparable<? super T>>`と、意味のない`Object`をいちばん左の境界に書いています。
1で見たとおり消去はいちばん左の境界で決まるので、消去後の戻り値は`Comparable`ではなく`Object`になります。

<!-- output: collections-max -->
```
public static <T extends java.lang.Object & java.lang.Comparable<? super T>> T min(java.util.Collection<? extends T>);
descriptor: (Ljava/util/Collection;)Ljava/lang/Object;
public static <T extends java.lang.Object & java.lang.Comparable<? super T>> T max(java.util.Collection<? extends T>);
descriptor: (Ljava/util/Collection;)Ljava/lang/Object;
```
<!-- /output -->

<!-- claim: collections-max -->

## よくある誤解

- 型引数の情報は、実行時にはまったく残らない
  - オブジェクトは型引数を持たないが、フィールド・メソッド・スーパークラスの宣言に書いた型引数はSignature属性に残り、リフレクションで読める（4）
  <!-- claim: signature-attribute, reflection-declarations -->
- `List<String>`のリストに`Integer`を入れると、入れた時点で例外になる
  - raw型を通すと入れるときは何も起きず、取り出して`String`で受けるときに`ClassCastException`になる（8）
  <!-- claim: heap-pollution-run -->
- `instanceof List<String>`は決して書けない
  - 左の式の型から安全に判定できるとき（`Collection<String>`など）は、Java SE 16から書ける（6）
  <!-- claim: instanceof-refine -->
- `@SafeVarargs`を付けると、可変長引数が安全になる
  - 警告を出さなくするだけで、安全かどうかはプログラマーが確かめる（9）
  <!-- claim: safevarargs-meaning -->
- ブリッジメソッドは、ソースに書いたメソッドと同じ働きをするので気にしなくてよい
  - ブリッジメソッドのキャストは、raw型を通した誤りを`ClassCastException`として表に出す。同じシグネチャのメソッドを自分で書くと名前の衝突になる（5）
  <!-- claim: bridge-cce, manual-bridge-clash -->
- 型パラメーターは常に`Object`に消去される
  - 境界があれば、いちばん左の境界に消去される（1）
  <!-- claim: erasure-bounds -->

## 参考資料

- Java言語仕様（Java SE 25）
  - 4.5.1「Type Arguments of Parameterized Types」（型引数とワイルドカード）
  - 4.6「Type Erasure」
  - 4.7「Reifiable Types」（具象化可能な型と、型を消去した理由）
  - 4.8「Raw Types」
  - 4.12.2「Variables of Reference Type」（ヒープ汚染）
  - 6.5.5.1「Simple Type Names」（static文脈での型パラメーター）
  - 8.1.2「Generic Classes and Type Parameters」（`Throwable`の制限）
  - 8.4.8.3「Requirements in Overriding and Hiding」（名前の衝突）
  - 9.6.4.7「@SafeVarargs」
  - 10.5「Array Store Exception」
  - 15.5「Expressions and Run-Time Checks」（自動で入れたキャスト）
  - 15.10.1「Array Creation Expressions」
  - 15.12.4.5「Create Frame, Synchronize, Transfer Control」（ブリッジメソッド）
  - 15.20.2「The instanceof Operator」
  - `https://docs.oracle.com/javase/specs/jls/se25/html/`
- Java仮想マシン仕様（Java SE 25）
  - 4.6「Methods」（`ACC_BRIDGE`・`ACC_SYNTHETIC`）
  - 4.7.9「The Signature Attribute」
  - `https://docs.oracle.com/javase/specs/jvms/se25/html/`
- APIドキュメント（Java SE 25）
  - `java.lang.Class`の`getGenericSuperclass`、`java.lang.reflect.Method`の`isBridge`、`java.lang.reflect.Array`の`newInstance`、`java.util.Collections`
  - `https://docs.oracle.com/en/java/javase/25/docs/api/`
