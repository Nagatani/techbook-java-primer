# 付録B.01 JVMアーキテクチャとバイトコード

第1章と第2章で、Javaのプログラムはバイトコードにコンパイルされ、JVM（Java仮想マシン）のうえで動くことを学びました。
この節では、クラスファイルの中身、バイトコードの動き方、クラスの読み込みと初期化、メモリの領域、GC、起動を速くするしくみを、JDK 25で実際に観察しながら学びます。

## この節の読み方

この節の解説のうち、事実を述べた段落には`<!-- claim: … -->`という印を付けています（GitHubの画面では見えません）。
印の名前は同じディレクトリの`claims.yml`に対応し、そこにその主張の確かめ方（プログラムの実行、`javap`の出力の照合、JVMのフラグの既定値、出典）を書いています。
リポジトリのルートで`npm run check:deep-dive -- b01`を実行すると、すべての主張をJDK 25で確かめ直せます。
「観察する」の欄に載せた出力は、この検査が実際に出したものです（時刻・番地・再帰の深さなどは実行のたびに変わります）。

この節は、Java仮想マシン仕様（JVMS）とJava言語仕様（JLS）が決めていることと、OpenJDKのJVMであるHotSpotの実装の工夫を分けて書きます。
仕様が決めていることは仕様の節を、HotSpotの実装は観察を根拠にしています。
JITコンパイラーのインライン化と、メソッド呼び出しのしくみ（vtable）は付録B.04で詳しく扱います。

## ファイル構成

### 観察用のプログラム（verify/）

| ファイル | 観察すること |
|---|---|
| `ClassFileHeader.java` | クラスファイルの先頭（マジックナンバーと版） |
| `StackMachine.java` | オペランドスタックとローカル変数（`javap -c -v`で見る） |
| `Dynamic.java`・`Concat.java` | `invokedynamic`（ラムダ式と文字列の連結） |
| `ClassLoaders.java` | クラスローダーの階層、親への委譲、クラスの同一性 |
| `InitOrder.java` | クラスの読み込みと初期化の時期 |
| `Verification.java` | バイトコードの検証（`VerifyError`） |
| `Unloading.java` | クラスのアンロード |
| `MemoryAreas.java` | メモリプール（ヒープとMetaspaceなど）とGCの名前 |
| `StackDepth.java` | スタックの大きさと`StackOverflowError` |
| `HeapExhaustion.java` | ヒープを使い切ったときの`OutOfMemoryError` |
| `EscapeAnalysis.java` | エスケープ解析による割り当ての除去、オブジェクトの大きさ |
| `Hello.java` | GCの選択、CDS、JVMのオプションの確認に使う最小のプログラム |
| `AotCache.java` | AOTキャッシュの作成と利用 |

`verify/`のプログラムはパッケージを持たないので、`verify/`ディレクトリでそのままコンパイル・実行できます。

```bash
cd verify
javac ClassFileHeader.java && java ClassFileHeader
javac StackMachine.java && javap -c -v 'StackMachine$Calc'
javac InitOrder.java && java -Xlog:class+load InitOrder
```

### 体験用のデモ（src/）

- `JVMArchitectureDemo.java`
  - 標準API（`java.lang.management`）で、JVMの情報、クラスローダー、メモリプール、GC、スレッドを表示する
  - スレッドのスタックの深さは、積まれているフレームの数で表示する（バイト数ではない）
- `BytecodeAnalysisDemo.java`
  - バイトコード命令の例となるメソッドを実行し、そのバイトコードをClass-File API（`java.lang.classfile`）でクラスファイルから読んで表示する
  - JITコンパイルの前後、分岐の予測しやすさ、小さなメソッドの呼び出しと直接の計算の時間を比べる（10を参照）

```bash
javac -d out src/main/java/com/example/jvm/*.java
java -cp out com.example.jvm.JVMArchitectureDemo
java -cp out com.example.jvm.BytecodeAnalysisDemo
```

## 1. JVMの全体像

JVMのなかは、大きく3つの部分に分けて説明されます。

```
JVM
├─ クラスローダー
│    ├─ ブートストラップ、プラットフォーム、アプリケーションの3つ（4を参照）
│    └─ 読み込み → リンク（検証・準備・解決） → 初期化
├─ 実行時データ領域（5を参照）
│    ├─ スレッドごと: pcレジスター、JVMスタック、ネイティブメソッドスタック
│    └─ 全スレッドで共有: ヒープ、メソッド領域（実行時定数プールを含む）
└─ 実行エンジン
     ├─ インタープリター
     ├─ JITコンパイラー（C1・C2、7を参照）
     └─ GC（6を参照）
```

JVMSが決めているのは、クラスファイルの形式、命令の意味、クラスの読み込みと初期化の規則、実行時データ領域の役割です。
JITコンパイラーやGCの方式、メモリの具体的な配置は実装に任されています。
この図の実行エンジンの中身は、HotSpotの構成です。
<!-- claim: jvms-runtime-areas -->

## 2. クラスファイル

### しくみ

`javac`が作るクラスファイル（`.class`）は、JVMSのChapter 4が決めるバイナリ形式です。
先頭の4バイトはマジックナンバー`0xCAFEBABE`、続く2バイトずつがマイナーバージョンとメジャーバージョンです。
その後に定数プール、クラスの名前とスーパークラス、フィールド、メソッド（バイトコードを含む）、属性が続きます。
<!-- claim: classfile-magic, javap-constant-pool -->

### 観察する

`verify/ClassFileHeader.java`は、自分自身のクラスファイルを読んで先頭の8バイトを表示します。

<!-- output: classfile-magic -->
```
先頭の8バイト: CAFEBABE00000045
マジックナンバー: CAFEBABE
マイナーバージョン: 0
メジャーバージョン: 69
```
<!-- /output -->

Java 25の`javac`が作るクラスファイルのメジャーバージョンは69（16進数で`0x45`）です。
<!-- claim: classfile-magic -->

Javaの版とメジャーバージョンは、次のように対応します（`java.lang.reflect.ClassFileFormatVersion`で表示したもの）。

<!-- output: classfile-versions -->
```
RELEASE_8 -> 52
RELEASE_11 -> 55
RELEASE_17 -> 61
RELEASE_21 -> 65
RELEASE_25 -> 69
```
<!-- /output -->

Java SE 25のJVMは、メジャーバージョンが45から69までのクラスファイルを実行できます。
<!-- claim: classfile-versions -->

対応していない版のクラスファイルを読み込むと、`UnsupportedClassVersionError`になります。
新しい版の`javac`でコンパイルしたクラスを古いJVMで動かしたときに出るエラーです。
`ClassFileHeader.java`は、版を70にしたクラスファイルを組み立てて、JDK 25に読み込ませます。

<!-- output: unsupported-version -->
```
UnsupportedClassVersionError: Future has been compiled by a more recent version of the Java Runtime (class file version 70.0), this version of the Java Runtime only recognizes class file versions up to 69.0
```
<!-- /output -->

<!-- claim: unsupported-version -->

定数プールは、クラスの名前、メソッドの名前と型、文字列の定数などを番号付きで並べた表です。
`javap -v`で見ると、`IO.println`の呼び出しが定数プールの項目（`Methodref`）への番号で表されていることがわかります。

<!-- output: javap-constant-pool -->
```
minor version: 0
major version: 69
#40 = Methodref #41.#42 // java/lang/IO.println:(Ljava/lang/Object;)V
#41 = Class #43 // java/lang/IO
#42 = NameAndType #44:#45 // println:(Ljava/lang/Object;)V
```
<!-- /output -->

バイトコードの`invokestatic #番号`は、この番号の項目を指します。
実行時には、名前で書かれたこの参照を実際のメソッドに結び付けます（解決）。
<!-- claim: javap-constant-pool -->

`ClassFileHeader.java`の後半は、Java 24で正式な機能になったClass-File API（`java.lang.classfile`、JEP 484）でクラスファイルを読み、クラスの名前とメソッドの一覧を表示します。
`src/`の`BytecodeAnalysisDemo`も、このAPIでメソッドのバイトコードを表示します。
<!-- claim: classfile-api -->

## 3. スタックマシンとしてのバイトコード

### しくみ

JVMは、レジスターではなくスタックを使って計算する「スタックマシン」として定義されています。
メソッドを呼ぶたびにJVMスタックにフレームを1つ積み、フレームはローカル変数の配列とオペランドスタックを持ちます。

- ローカル変数
  - 引数とメソッドのなかの変数を、0番から番号で並べる
- オペランドスタック
  - 計算の途中の値を積む。`iadd`はスタックのうえの2つの`int`を取り出し、和を積む

### 観察する

`verify/StackMachine.java`の`Calc`インターフェイスの3つのメソッドを、`javap -c -v`で見ます。

<!-- output: stack-add -->
```
public static int add(int, int);
stack=2, locals=3, args_size=2
0: iload_0
1: iload_1
2: iadd
3: istore_2
4: iload_2
5: ireturn
public static long widen(long, int);
stack=4, locals=5, args_size=2
0: lload_0
1: iload_2
2: i2l
3: lmul
4: lstore_3
5: lload_3
6: lreturn
public static int expression(int, int, int, int);
stack=3, locals=4, args_size=4
0: iload_0
1: iload_1
2: iadd
3: iload_2
4: iload_3
5: iadd
6: imul
7: ireturn
```
<!-- /output -->

`add`は、`iload_0`と`iload_1`で引数を積み、`iadd`で足し、`istore_2`で変数`c`（ローカル変数の2番）に戻しています。
`stack=2`はオペランドスタックが最大で2つの値を積むこと、`locals=3`はローカル変数を3つ使うことを表します。
この2つの値は`javac`がコンパイル時に求めてクラスファイル（`Code`属性の`max_stack`・`max_locals`）に書き、JVMはこの大きさのフレームを用意します。
`(a + b) * (c + d)`の`expression`では、`a + b`の結果を積んだまま`c`と`d`を積むので、`stack=3`になっています。
<!-- claim: stack-add -->

`widen`の引数は`long a`と`int b`の2つですが、`locals=5`です。
`long`と`double`はローカル変数を2つ使うので、`a`が0番と1番、`b`が2番、`c`が3番と4番を使います。
<!-- claim: stack-long-two-slots -->

インスタンスメソッドでは、ローカル変数の0番に`this`が入ります。
`javap -c -v 'StackMachine$Counter'`で`addBase`を見ると、`aload_0`で`this`を積んでから`getfield`でフィールド`base`を読んでいます。
<!-- claim: stack-this-slot0 -->

### 命令の種類

命令の名前の先頭の文字は、扱う値の型を表します（`i`は`int`、`l`は`long`、`f`は`float`、`d`は`double`、`a`は参照）。

| 種類 | 命令の例 | 用途 |
|---|---|---|
| ロード・ストア | `iload`、`astore` | ローカル変数とオペランドスタックの間で値を移す |
| 算術 | `iadd`、`lmul` | 計算 |
| 型変換 | `i2l`、`i2d` | 型の変換 |
| オブジェクト | `new`、`getfield`、`newarray` | オブジェクトや配列の生成、フィールドの読み書き |
| 制御 | `ifle`、`if_icmpge`、`goto` | 条件分岐とループ |
| メソッド呼び出し | `invokestatic`、`invokevirtual`、`invokeinterface`、`invokespecial`、`invokedynamic` | メソッドの呼び出し |

`invokestatic`などの4つの呼び出し命令が、メソッドの種類とどう対応するかは付録B.04で観察します。

### invokedynamic

`invokedynamic`は、呼び出し先を最初に実行するときに「ブートストラップメソッド」に決めさせる命令です。
Javaでは、ラムダ式と文字列の連結に使われています。
`verify/Dynamic.java`を`javap -c -p -v`で見ると、次の命令とブートストラップメソッドが並びます。

<!-- output: indy-lambda-concat -->
```
2: invokedynamic // InvokeDynamic #0:makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;
0: invokedynamic // InvokeDynamic #1:get:()Ljava/util/function/Supplier;
private static java.lang.String lambda$lazy$0();
0: java/lang/invoke/StringConcatFactory.makeConcatWithConstants
1: java/lang/invoke/LambdaMetafactory.metafactory
```
<!-- /output -->

ラムダ式の本体は`lambda$lazy$0`という`private static`なメソッドになり、`LambdaMetafactory`が実行時にそれを呼ぶ`Supplier`の実装を作ります。
文字列の連結は、`StringConcatFactory`が実行時に連結の方法を決めます。
<!-- claim: indy-lambda-concat -->

文字列の連結は、Java 8までは`StringBuilder`の`append`を連ねたバイトコードでした。
Java 9で、`invokedynamic`の1命令に変わりました（JEP 280）。
`verify/Concat.java`を`javac --release 8`でコンパイルすると、昔の形を見られます。

<!-- output: concat-java9 -->
```
0: new // class java/lang/StringBuilder
3: dup
4: invokespecial // Method java/lang/StringBuilder."<init>":()V
7: ldc // String Hello,
9: invokevirtual // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
12: aload_0
13: invokevirtual // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
16: ldc // String x
18: invokevirtual // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
21: iload_1
22: invokevirtual // Method java/lang/StringBuilder.append:(I)Ljava/lang/StringBuilder;
25: invokevirtual // Method java/lang/StringBuilder.toString:()Ljava/lang/String;
28: areturn
```
<!-- /output -->

連結の方法を決める処理をJDKのライブラリ側に移したので、`javac`が出すバイトコードを変えずに、連結の処理をJDKの側で改良できるようになりました（これがJEP 280の目的です）。
<!-- claim: concat-java9 -->

## 4. クラスの読み込み・リンク・初期化

### しくみ

JVMがクラスを使えるようにするまでには、次の段階があります（JVMSのChapter 5、JLSの12.2〜12.4）。

1. 読み込み（ロード）
   - クラスローダーがクラスファイルのバイト列を見つけ、JVMのなかにクラスを作る
2. リンク
   - 検証（バイトコードが規則を守っているかを確かめる）、準備（`static`フィールドの領域を既定値で用意する）、解決（定数プールの名前の参照を実体に結び付ける）
3. 初期化
   - `static`初期化子と`static`フィールドの初期化式を実行する

### クラスローダーの階層

JDK 25のクラスローダーは、次の3つが組み込まれています。

- ブートストラップクラスローダー
  - `java.base`など中核のモジュールのクラス（`String`、`Object`、`HashMap`など）を読み込む。JVMに組み込まれていて、Javaからは`null`に見える
- プラットフォームクラスローダー
  - `java.sql`など、Java SEとJDKのそのほかのモジュールの一部を読み込む
- アプリケーションクラスローダー（システムクラスローダー）
  - クラスパスとモジュールパスにあるアプリケーションのクラスを読み込む

`verify/ClassLoaders.java`で、どのクラスローダーが読み込んだかと、親のたどり方を表示します。

<!-- output: loader-hierarchy -->
```
java.lang.String -> null（ブートストラップクラスローダー） / java.base
java.sql.Driver -> platform / java.sql
ClassLoaders -> app / 名前のないモジュール
システムクラスローダー: app
その親: platform
さらにその親: null（ブートストラップクラスローダー）
getPlatformClassLoader()と同じ: true
```
<!-- /output -->

アプリケーションクラスローダーの親はプラットフォームクラスローダー（`ClassLoader.getPlatformClassLoader()`）、その親はブートストラップクラスローダーです。
<!-- claim: loader-hierarchy -->

### 親への委譲

クラスローダーは、クラスを頼まれるとまず親に探させ、親が見つけられなかったときだけ自分で探します（`findClass`）。
`ClassLoaders.java`の`TracingLoader`は、`loadClass`と`findClass`が呼ばれるたびに表示します。

<!-- output: loader-delegation -->
```
java.lang.Stringを頼む
loadClass(java.lang.String)
-> null（ブートストラップクラスローダー）
ClassLoaders$Greeterを頼む
loadClass(ClassLoaders$Greeter)
findClass(ClassLoaders$Greeter) ← 親が見つけられなかったので自分で探す
loadClass(java.lang.Object)
-> tracing
```
<!-- /output -->

`String`は親（の親）のブートストラップクラスローダーが見つけるので、`findClass`は呼ばれません。
`ClassLoaders$Greeter`は、親のプラットフォームクラスローダーには見つけられないので、`TracingLoader`が自分で読み込みます。
そのとき、`Greeter`のスーパークラスの`Object`も`TracingLoader`を通して頼んでいます（これも親が見つけます）。
親への委譲によって、アプリケーションが`java.lang.String`という名前のクラスを用意しても、JDKの`String`が置き換えられることはありません。
<!-- claim: loader-delegation -->

### クラスの同一性

実行時のクラスは、名前だけでなく「名前と、それを定義したクラスローダーの組」で区別されます。
同じクラスファイルを2つのクラスローダーで読み込むと、名前が同じ別のクラスになります。

<!-- output: loader-identity -->
```
名前が同じ: true
同じクラス: false
aのGreeterをappのGreeterの型に代入できる: false
```
<!-- /output -->

<!-- claim: loader-identity -->

### 初期化の時期

クラスの初期化（`static`初期化子の実行）は、いつ行うかがJLSの12.4.1で決まっています。
インスタンスの生成、`static`メソッドの呼び出し、`static`フィールドへの代入、定数変数でない`static`フィールドの使用の直前です。
`verify/InitOrder.java`は、いろいろな使い方をして、初期化子が動く時点を表示します。

<!-- output: init-timing -->
```
1. Child.CONSTANTを読む
値: 42
2. Child.classを使う
Child
3. Childの配列を作る
長さ: 3
4. Class.forName(名前, false, ローダー)で読み込む
5. Child.parentValueを読む（Parentで宣言したフィールド）
Parentを初期化した
値: 10
6. Child.counterを読む
Childを初期化した
値: 0
7. new Circle()
Shapeを初期化した
Circleを初期化した
8. Named.PREFIXを読む
Namedを初期化した
値: Namedを初期化した
```
<!-- /output -->

1から4では、どの初期化子も動いていません。
`CONSTANT`のような`static final`で値がコンパイル時に決まるフィールド（定数変数）は、`javac`が値をそのまま埋め込むので、`Child`を使いません。
クラスリテラル（`Child.class`）、配列の生成、`Class.forName(名前, false, ローダー)`も初期化しません。
5の`Child.parentValue`は`Parent`で宣言したフィールドなので、初期化するのは`Parent`だけです。
6で`Child`を初期化します（`Parent`は初期化済みなので、2回目は動きません）。
<!-- claim: init-timing -->

7の`new Circle()`では、`Circle`より先にインターフェイス`Shape`を初期化しています。
クラスを初期化するときは、デフォルトメソッドを持つスーパーインターフェイスも初期化しますが、持たない`Named`は初期化しません。
`Named`は、8で`Named.PREFIX`を使ったときに初めて初期化されます。
<!-- claim: init-superinterface -->

### 読み込みの時期

読み込みの時期は、JVMSが実装に幅を持たせています。
`-Xlog:class+load`を付けると、HotSpotがどの時点でクラスを読み込んだかがわかります。

<!-- output: load-timing -->
```
1. Child.CONSTANTを読む
2. Child.classを使う
InitOrder$Parent source: file:...
InitOrder$Child source: file:...
3. Childの配列を作る
4. Class.forName(名前, false, ローダー)で読み込む
5. Child.parentValueを読む（Parentで宣言したフィールド）
Parentを初期化した
6. Child.counterを読む
Childを初期化した
7. new Circle()
InitOrder$Shape source: file:...
InitOrder$Named source: file:...
InitOrder$Circle source: file:...
Shapeを初期化した
Circleを初期化した
8. Named.PREFIXを読む
Namedを初期化した
値: Namedを初期化した
```
<!-- /output -->

HotSpotは、クラスを使う時点まで読み込みません（遅延読み込み）。
1の定数の参照では`Child`を読み込まず、2のクラスリテラルで`Child`を読み込んでいます（スーパークラスの`Parent`が先です）。
7では`Circle`と一緒に`Shape`と`Named`も読み込みますが、初期化するのは`Shape`だけです。
読み込みと初期化は別の段階であることがわかります。
<!-- claim: load-timing -->

### 検証

JVMは、読み込むクラスファイルが正しい`javac`で作られたものだとは仮定しません。
リンクの段階でバイトコードを検証し、オペランドスタックの型が合わない、存在しない番号のローカル変数を使う、といったコードを拒みます。
`verify/Verification.java`は、`null`を積んで`int`として返すメソッドをClass-File APIで組み立て、読み込ませます（`javac`ではこのようなコードは作れません）。

<!-- output: verify-reject -->
```
組み立てたクラスファイル: 108バイト
読み込めた（まだ検証していない）: Broken
answer()を呼ぶ
VerifyError: Bad type on operand stack
Type null (current frame, stack[0]) is not assignable to integer
```
<!-- /output -->

HotSpotは、クラスを読み込んだ時点では検証せず、使うとき（ここでは`getMethod`でリンクするとき）に検証して`VerifyError`を投げます。
不正なコードは1命令も実行されません。
<!-- claim: verify-reject -->

### アンロード

クラスは、それを定義したクラスローダーがGCで回収されるときに、一緒にアンロードできます。
ブートストラップクラスローダーが読み込んだクラスはアンロードしません。
`verify/Unloading.java`は、自作のクラスローダーで読み込んだクラスへの参照を捨ててから、GCを実行します。

<!-- output: class-unload -->
```
読み込んだ: Unloading$Plugin（クラスローダー: plugin）
--- クラスローダーとクラスへの参照を捨てて、GCを実行する ---
unloading class Unloading$Plugin 0x...
終わり
```
<!-- /output -->

アンロードされると、そのクラスのメタデータが使っていたMetaspace（5を参照）の領域が空きます。
<!-- claim: class-unload -->

## 5. 実行時データ領域

### 仕様が決めていること

JVMSの2.5は、次の実行時データ領域を定めています。

- pcレジスター
  - スレッドごとに1つ。実行中の命令の位置を指す
- JVMスタック
  - スレッドごとに1つ。メソッドの呼び出しごとのフレームを積む
- ネイティブメソッドスタック
  - スレッドごとに1つ。Java以外の言語で書かれたメソッド（ネイティブメソッド）のためのスタック
- ヒープ
  - 全スレッドで共有する。すべてのオブジェクトと配列を置き、GCが回収する
- メソッド領域
  - 全スレッドで共有する。クラスごとの実行時定数プール、フィールドとメソッドの情報、メソッドのコードを置く

<!-- claim: jvms-runtime-areas -->

JVMSは、メソッド領域を「論理的にはヒープの一部」としつつ、置き場所や管理の方法を実装に任せています。

### HotSpotのメモリプール

`verify/MemoryAreas.java`は、JVMが管理しているメモリの領域（メモリプール）と、GCの名前を表示します（名前の順に並べ替えています）。

<!-- output: memory-pools -->
```
GC: G1 Concurrent GC
GC: G1 Old Generation
GC: G1 Young Generation
メモリプール: CodeHeap 'non-nmethods'（ヒープ以外）
メモリプール: CodeHeap 'non-profiled nmethods'（ヒープ以外）
メモリプール: CodeHeap 'profiled nmethods'（ヒープ以外）
メモリプール: Compressed Class Space（ヒープ以外）
メモリプール: G1 Eden Space（ヒープ）
メモリプール: G1 Old Gen（ヒープ）
メモリプール: G1 Survivor Space（ヒープ）
メモリプール: Metaspace（ヒープ以外）
```
<!-- /output -->

ヒープは、G1のEden（新しいオブジェクト）、Survivor（GCを生き延びたオブジェクト）、Old（長く生きているオブジェクト）に分かれています。
クラスのメタデータ（JVMSでメソッド領域に置くとされるクラスの情報）を置くMetaspaceと、JITコンパイラーが作った機械語を置くCodeHeapは、ヒープ以外のメモリです。
<!-- claim: memory-pools -->

`java -XX:+UseSerialGC MemoryAreas`のようにGCを変えると、ヒープの領域の名前も`Eden Space`・`Survivor Space`・`Tenured Gen`に変わります。
<!-- claim: memory-pools-serial -->

### PermGenからMetaspaceへ

Java 7までのHotSpotは、クラスのメタデータをヒープのなかのPermanent Generation（PermGen）に置いていました。
Java 8でPermGenはなくなり（JEP 122）、メタデータはネイティブメモリのMetaspaceに置くようになりました。
PermGenの大きさを決めていたオプション（`MaxPermSize`など）は、JDK 25では「Unrecognized VM option」として起動できません。
<!-- claim: no-permgen -->

Metaspaceの上限（`-XX:MaxMetaspaceSize`）は、既定では設けられていません。
クラスを作り続ける不具合があると、ネイティブメモリを使い続けます。
上限を設けたいときは、`-XX:MaxMetaspaceSize=256m`のように指定します。
<!-- claim: metaspace-unbounded -->

### JVMスタックとStackOverflowError

JVMスタックはスレッドごとにあり、大きさを超えてフレームを積もうとすると`StackOverflowError`になります。
`verify/StackDepth.java`は、スタックの大きさを指定したスレッドで、例外になるまで再帰します。

<!-- output: stack-depth -->
```
スタック512KBのスレッド: 7224段でStackOverflowError
スタック4MBのスレッド: 101842段でStackOverflowError
4MBのほうが深く呼べる: true
```
<!-- /output -->

スタックが大きいほど深く呼べます。
段数は実行のたびに変わります。
スレッドのスタックの大きさは、`Thread`のコンストラクターのほか、`-Xss`（例: `-Xss4m`）で既定値を指定できます。
<!-- claim: stack-depth -->

`-Xss`の既定値はプラットフォームによって異なります。
`java -XX:+PrintFlagsFinal -version`の`ThreadStackSize`（KB単位）で確かめられます。
<!-- claim: xss-default-platform -->

### ヒープとOutOfMemoryError

参照を持ち続けているオブジェクトは、GCが回収できません。
`static`なコレクションに入れたまま消さないと、ヒープの上限に達して`OutOfMemoryError`になります。
`verify/HeapExhaustion.java`を`-Xmx32m`（ヒープの上限を32MBにする）で実行します。
<!-- claim: heap-oom -->

<!-- output: heap-oom -->
```
OutOfMemoryError: Java heap space
1MBの配列を15個持ったところで、ヒープが足りなくなった
```
<!-- /output -->

## 6. ガベージコレクション

### 既定のGCと選ばれ方

JDK 25のHotSpotは、プロセッサーが2つ以上で物理メモリが1792MB以上の「サーバー級」の機械では、G1（Garbage-First）GCを選びます。
`-Xlog:gc`を付けると、選ばれたGCが表示されます。

<!-- claim: default-gc-g1 -->

<!-- output: default-gc-g1 -->
```
[info][gc] Using G1
```
<!-- /output -->

サーバー級でない機械では、シリアルGCを選びます。
`-XX:ActiveProcessorCount=1`でプロセッサーが1つの環境を模擬すると、次のようになります。
CPUを1つしか使えない環境（CPUを制限したコンテナーなど）では、GCを指定しなければシリアルGCになることに注意してください。
<!-- claim: gc-serial-small -->

<!-- output: gc-serial-small -->
```
[info][gc] Using Serial
```
<!-- /output -->

この選び方は、JDK 27で「どの環境でもG1」に変わりました（JEP 523）。
<!-- claim: g1-all-environments -->

### G1のリージョン

世代別GCは、「ほとんどのオブジェクトは短い間しか生きない」という性質（弱い世代別仮説）を利用し、新しいオブジェクトを集めた領域を頻繁に回収します。
<!-- claim: weak-generational -->
G1は、ヒープを同じ大きさの多数のリージョンに分け、Eden・Survivor・Oldをリージョンの集合として扱います（連続した領域ではありません）。
リージョンの大きさは、ヒープの大きさなどから決まります。

<!-- output: g1-regions -->
```
Heap Region Size: 2M
```
<!-- /output -->

<!-- claim: g1-regions -->

### ヒープの大きさ

`-Xmx`を指定しないと、ヒープの上限は物理メモリの4分の1（`-XX:MaxRAMPercentage`の既定値25）になります。
`-XX:MaxRAM=1g`で1GBの機械とみなして`MemoryAreas`を実行すると、上限は256MBです。
<!-- claim: max-heap-quarter -->

<!-- output: max-heap-quarter -->
```
ヒープの上限: 256MB
```
<!-- /output -->

### そのほかのGC

- ZGC（`-XX:+UseZGC`）
  - 停止時間を短く保つGC。JDK 24で世代別でないモードが削除され、世代別のモードだけになった（JEP 490）。JDK 25でZGenerationalを指定すると、無視したという警告が出る
  <!-- claim: zgc-generational-only -->
- CMS（Concurrent Mark Sweep）
  - Java 14で削除された（JEP 363）。JDK 25では`UseConcMarkSweepGC`は認識されないオプションである
  <!-- claim: cms-removed -->

### オブジェクトヘッダー

ヒープの各オブジェクトの先頭には、GCやロック、クラスの情報を持つヘッダーがあります。
Java 25では、ヘッダーを小さくするCompact Object Headers（JEP 519）が正式な機能になりましたが、既定では無効です。
<!-- claim: compact-headers -->

`EscapeAnalysis.java`で、`int`のフィールドを2つ持つレコードの割り当ての量を測ると、既定では1個24バイト、`-XX:+UseCompactObjectHeaders`を付けると16バイトでした（エスケープ解析を止めて測っています。7を参照）。
<!-- claim: compact-headers-size -->

<!-- output: compact-headers-size -->
```
Point 1個あたり: 16バイト
```
<!-- /output -->

## 7. 実行エンジン：インタープリターとJITコンパイラー

HotSpotは、バイトコードをまずインタープリターで実行し、よく実行されるメソッドをJITコンパイラーで機械語にします（mixed mode）。
`java -version`の表示に、そのモードが出ます（`sharing`はCDSを使っていることを表します。8を参照）。

<!-- output: mixed-mode -->
```
OpenJDK 64-Bit Server VM (build ..., mixed mode, sharing)
```
<!-- /output -->

`-Xint`を付けるとインタープリターだけで実行し、表示は`interpreted mode`になります。
<!-- claim: mixed-mode -->

JITコンパイラーには、すぐにコンパイルするC1と、時間をかけて最適化するC2があり、よく実行されるメソッドはC1（レベル3）を経てC2（レベル4）でコンパイルされます（段階的コンパイル、ティアードコンパイル）。
`-XX:+PrintCompilation`で、`EscapeAnalysis::sum`がレベル3と4でコンパイルされる様子が見えます。
段階的コンパイルの段階と時期、メソッドのインライン化の条件は付録B.04で観察します。
<!-- claim: jit-compiles-sum -->

### エスケープ解析

C2は、作ったオブジェクトがメソッドの外へ出るか（エスケープするか）を解析し、出ないオブジェクトのヒープへの割り当てを取り除きます。
`verify/EscapeAnalysis.java`は、ループのなかで`Point`を1000万個作り、その間にヒープに割り当てたバイト数を測ります。
<!-- claim: escape-analysis -->

<!-- output: escape-analysis -->
```
Pointを1000万個作る間にヒープに割り当てたバイト数: 0
```
<!-- /output -->

`-XX:-DoEscapeAnalysis`で解析を止めると、1000万個分の割り当てが戻ります。

<!-- output: escape-analysis-off -->
```
Pointを1000万個作る間にヒープに割り当てたバイト数: 240000000
Point 1個あたり: 24バイト
```
<!-- /output -->

<!-- claim: escape-analysis-off -->

小さなオブジェクトを作るのを性能のために避ける書き方は、まず測って、効果があるかを確かめてからにします。

## 8. 起動を速くするしくみ：CDSとAOTキャッシュ

### CDS（クラスデータ共有）

起動のたびに同じJDKのクラスを読み込んで解析するのは無駄なので、HotSpotは解析済みのクラスをアーカイブに保存して使い回します（Class Data Sharing、CDS）。
JDK 12からは、既定のCDSアーカイブがJDKに入っていて（JEP 341）、何も指定しなくても使われます（`-Xshare:auto`が既定）。
`-Xlog:class+load`で、クラスの読み込み元（`source:`）がわかります。
<!-- claim: cds-default -->

<!-- output: cds-default -->
```
java.lang.Object source: shared objects file
java.lang.String source: shared objects file
Hello source: file:...
```
<!-- /output -->

JDKのクラスは`shared objects file`（CDSのアーカイブ）から、アプリケーションの`Hello`はクラスファイルから読み込まれています。
<!-- claim: cds-default -->

`-Xshare:off`でCDSを止めると、JDKのクラスもモジュールのイメージ（`jrt:/java.base`）から読み込みます。

<!-- output: cds-off -->
```
java.lang.Object source: jrt:/java.base
java.lang.String source: jrt:/java.base
```
<!-- /output -->

<!-- claim: cds-off -->

### AOTキャッシュ（Java 24・25）

AOTキャッシュは、CDSのしくみを使って、アプリケーションのクラスを読み込み・リンク済みの状態で保存するものです。
アプリケーションを1回「訓練の実行」として動かし、そのとき読み込んでリンクしたクラスをキャッシュに保存します。
次からの起動では、キャッシュのクラスを読み込み・リンク済みの状態ですぐに使えます（Java 24、JEP 483）。
Java 25では、`-XX:AOTCacheOutput`を付けた1回の実行で、訓練の実行とキャッシュの作成ができるようになりました（JEP 514）。

```bash
java -XX:AOTCacheOutput=app.aot -cp app.jar com.example.App    # 訓練の実行とキャッシュの作成
java -XX:AOTCache=app.aot -cp app.jar com.example.App          # キャッシュを使って起動
```

`verify/AotCache.java`は、このコマンドを子プロセスとして実行し、キャッシュの有無でクラスの読み込み元を比べます。

<!-- output: aot-cache -->
```
訓練の実行: AOTCache creation is complete: app.aot (大きさ) bytes
キャッシュなし: AotCache$App source: file:.../app.jar
キャッシュあり: AotCache$App source: shared objects file
キャッシュなしで、CDSのアーカイブ以外から読み込んだクラスの数: 9
キャッシュありで、CDSのアーカイブ以外から読み込んだクラスの数: 0
```
<!-- /output -->

キャッシュを使うと、アプリケーションのクラス（`AotCache$App`）もキャッシュ（`shared objects file`）から読み込まれ、CDSのアーカイブ以外から読み込むクラスがなくなりました。
<!-- claim: aot-cache -->

AOTキャッシュを使うときは、クラスパスにJARファイルだけを書きます（ディレクトリは使えません）。
訓練の実行と本番の実行で、クラスパスをそろえます。
<!-- claim: aot-jar-only -->

Java 25では、訓練の実行で集めたメソッドの実行プロファイル（どのメソッドがよく呼ばれたか）もキャッシュに入り、本番の実行でJITコンパイルを早く始められます（JEP 515）。
<!-- claim: aot-profiles -->

## 9. 分析に使う道具

```bash
# クラスの読み込み・アンロード、GC、GCの初期設定のログ
java -Xlog:class+load -Xlog:class+unload -Xlog:gc -Xlog:gc+init ...

# GCのログをファイルに出す
java -Xlog:gc*:file=gc.log:time,uptime ...

# JITコンパイルの様子
java -XX:+PrintCompilation ...

# フラグの既定値（GCの選択やヒープの上限など）
java -XX:+PrintFlagsFinal -version

# OutOfMemoryErrorのときにヒープダンプを取る
java -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=dumps ...

# 動いているJVMのヒープダンプとスレッドダンプ
jcmd <pid> GC.heap_dump heap.hprof
jcmd <pid> Thread.print

# クラスファイルの中身
javap -c -p -v クラス名
```

- JFR（JDK Flight Recorder）は、JVMに組み込まれたプロファイラー（`java -XX:StartFlightRecording ...`）
- JMH（Java Microbenchmark Harness）は、短い処理の時間を正しく測るためのツール
- VisualVM・async-profilerなど、JDKの外のツールもある

## 10. 体験用のデモの実行結果

`BytecodeAnalysisDemo`は、次のような形式で測定結果を表示します（数値は省略しています）。
測定値はCPU・OS・JVMの状態によって大きく変わり、実行するたびにも変わります。
このデモはJITの効果を体験するためのもので、正確に測るにはJMHを使います。

```
=== JIT Compilation Effect Demonstration ===
Cold execution time: ... μs
Hot execution time:  ... μs
Speedup: ...x

=== Branch Prediction and Profile-Guided Optimization ===
Predictable branching time:   ... μs
Unpredictable branching time: ... μs
Unpredictable / predictable: ...x

=== Method Inlining Analysis ===
Via small methods:  ... μs
Direct calculation: ... μs
Methods / direct: ...x
```

インライン化の比較は、小さなメソッドの呼び出しがインライン化されれば、直接計算する書き方との差がなくなることを確かめるためのものです。
`-XX:CompileCommand=dontinline,*InlineCandidate.*`を付けてインライン化を止めた場合と比べてみてください。
時間が短すぎて測れなかったときは、比の代わりに`n/a (too fast to measure)`と表示します。
分岐の比較で差が出るとすれば、CPUの分岐予測が外れることによるものです。
これはCPUの性質で、JVMがメソッドをよく呼ばれると判断してJITコンパイルすることとは別の話です。

## よくある誤解

- クラスはプログラムの起動時にすべて読み込まれる
  - HotSpotは使う時点まで読み込まない。定数変数の参照では読み込みもしない（4「読み込みの時期」）
  <!-- claim: load-timing -->
- クラスを参照すれば`static`初期化子が動く
  - 定数変数の参照、クラスリテラル、配列の生成では初期化しない（4「初期化の時期」）
  <!-- claim: init-timing -->
- 同じ名前のクラスは1つしかない
  - クラスは名前とクラスローダーの組で決まる（4「クラスの同一性」）
  <!-- claim: loader-identity -->
- クラスのメタデータの領域が足りなければ、PermGenを広げる
  - Java 8からPermGenはなく、メタデータはMetaspaceにある（5）
  <!-- claim: no-permgen -->
- `javac`が作ったバイトコードは、そのまま信用して実行される
  - JVMはリンクの段階で検証する（4「検証」）
  <!-- claim: verify-reject -->
- 既定のGCはいつもG1である
  - JDK 25では、プロセッサーが1つの環境ではシリアルGCになる（6）
  <!-- claim: gc-serial-small -->
- バイトコードはいつもインタープリターで実行されるので遅い
  - よく実行されるメソッドはJITコンパイルされる（7）
  <!-- claim: mixed-mode, jit-compiles-sum -->
- 小さなオブジェクトを作るたびにヒープへの割り当てとGCの負担が増える
  - メソッドの外へ出ないオブジェクトは、エスケープ解析で割り当てが取り除かれる（7）
  <!-- claim: escape-analysis -->

## 参考資料

- Java仮想マシン仕様（Java SE 25）
  - Chapter 2 The Structure of the Java Virtual Machine（2.5 Run-Time Data Areas、2.6 Frames）
  - Chapter 4 The class File Format（4.1 The ClassFile Structure、4.4 The Constant Pool、4.7.3 The Code Attribute、4.10 Verification of class Files）
  - Chapter 5 Loading, Linking, and Initializing（5.3 Creation and Loading、5.4 Linking）
  - `https://docs.oracle.com/javase/specs/jvms/se25/html/`
- Java言語仕様（Java SE 25）
  - 12.4.1 When Initialization Occurs、12.7 Unloading of Classes and Interfaces
  - `https://docs.oracle.com/javase/specs/jls/se25/html/jls-12.html`
- `java.lang.ClassLoader`（Java SE 25 API）
  - `https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/ClassLoader.html`
- javaコマンドのリファレンス（Java SE 25）
  - `https://docs.oracle.com/en/java/javase/25/docs/specs/man/java.html`
- HotSpot Virtual Machine Garbage Collection Tuning Guide（Java SE 25）
  - `https://docs.oracle.com/en/java/javase/25/gctuning/`
- JEP
  - JEP 122: Remove the Permanent Generation（`https://openjdk.org/jeps/122`）
  - JEP 248: Make G1 the Default Garbage Collector（`https://openjdk.org/jeps/248`）
  - JEP 280: Indify String Concatenation（`https://openjdk.org/jeps/280`）
  - JEP 341: Default CDS Archives（`https://openjdk.org/jeps/341`）
  - JEP 363: Remove the Concurrent Mark Sweep (CMS) Garbage Collector（`https://openjdk.org/jeps/363`）
  - JEP 483: Ahead-of-Time Class Loading & Linking（`https://openjdk.org/jeps/483`）
  - JEP 484: Class-File API（`https://openjdk.org/jeps/484`）
  - JEP 490: ZGC: Remove the Non-Generational Mode（`https://openjdk.org/jeps/490`）
  - JEP 514: Ahead-of-Time Command-Line Ergonomics（`https://openjdk.org/jeps/514`）
  - JEP 515: Ahead-of-Time Method Profiling（`https://openjdk.org/jeps/515`）
  - JEP 519: Compact Object Headers（`https://openjdk.org/jeps/519`）
  - JEP 523: Make G1 the Default Garbage Collector in All Environments（`https://openjdk.org/jeps/523`）
