# 付録B.10 レコードとデータ指向プログラミング

第8章で学んだレコード・sealed・パターンマッチングが、どのようなクラスとバイトコードになり、どのような規則で動くのかを、実際に観察しながら学ぶ節です。
後半では、これらを組み合わせてデータと処理を分けて書く「データ指向プログラミング」（Data-Oriented Programming, DOP）の考え方を扱います。

## この節の読み方

この節の解説のうち、事実を述べた段落には`<!-- claim: … -->`という印を付けています（GitHubの画面では見えません）。
印の名前は同じディレクトリの`claims.yml`に対応し、そこにその主張の確かめ方（コンパイラーの出力、`javap`の出力、実行結果、出典）を書いています。
リポジトリのルートで`npm run check:deep-dive -- b10`を実行すると、すべての主張をJDK 25で確かめ直せます。
「観察する」として載せた出力は、この検査が実際に出したものです。

## ファイル構成

### 観察用のプログラム（verify/）

| ファイル | 観察すること |
|---|---|
| `RecordAnatomy.java` | レコードがコンパイルされたクラスの形（`javap`とリフレクションで見る） |
| `CompactConstructor.java` | コンパクトコンストラクターでフィールドに値が入る時期 |
| `RecordEquality.java` | 暗黙の`equals`による浮動小数点数・配列の比べ方、アクセサーとの関係 |
| `ShallowImmutability.java` | 浅い不変性と、`List.copyOf`による防御的コピー |
| `CircularRecord.java` | 自分自身を含むレコードの`hashCode`・`toString` |
| `SealedShapes.java` | `PermittedSubclasses`属性、パターンのswitchのバイトコード |
| `RecordSerialization.java` | シリアライズしたレコードを読み戻すときの正準コンストラクター |
| `RecordLayout.java` | オブジェクトのメモリ上の配置（JOLを使う） |
| `Feature*.java` | 各機能が使えるようになった版（`javac --release`で比べる） |

`verify/compile-errors/`には、コンパイルエラーになる書き方を1ファイルに1つずつ置いています。
`verify/`のプログラムはパッケージを持たないので、`verify/`ディレクトリでそのままコンパイル・実行できます。

```bash
cd verify
javac RecordAnatomy.java && javap -p 'RecordAnatomy$Point'
javac RecordEquality.java && java RecordEquality
javac -J-Duser.language=en compile-errors/NonExhaustiveSwitch.java
javac --release 15 FeatureRecord.java
```

`RecordLayout.java`はJOL（Java Object Layout）を使います。
`jol-core-0.17.jar`をMaven Centralから取得し、次のように実行します。

```bash
javac -cp jol-core-0.17.jar RecordLayout.java
java -Djol.magicFieldOffset=true -Djdk.attach.allowAttachSelf=true -cp jol-core-0.17.jar:. RecordLayout
```

### 体験用のデモ（src/）

- `RecordsAndDOPDemo.java`
  - レコードの基本、図形の処理、ECサイトのドメインモデル、ネストしたパターン、式の木（代数的データ型）、検証付きのレコード
- `DataPipelineDemo.java`
  - イベントソーシング、売上データの集計、メトリクスの処理、APIレスポンスの変換、ページング
- `RecordOptimizationDemo.java`
  - オブジェクトのプール、独自の形式のシリアライズ、`MethodHandle`のキャッシュ、並行処理での共有
- `RecordsAndDOPSimple.java`・`DataPipelineSimple.java`
  - 上の2つと同じ内容を、sealedとレコードパターンを使わず、`instanceof`とキャストで書いた版
  - 読み比べると、レコードパターンで分岐と分解が簡潔になることがわかる

```bash
javac -d out src/main/java/com/example/records/*.java
java -cp out com.example.records.RecordsAndDOPDemo
java -cp out com.example.records.DataPipelineDemo
java -cp out com.example.records.RecordOptimizationDemo
java -cp out com.example.records.RecordsAndDOPSimple
java -cp out com.example.records.DataPipelineSimple
```

## 1. 機能が入った版

この節で扱う機能は、プレビュー機能として何度か試されたあと、次の版で正式な機能になりました。

| 機能 | 正式になった版 | JEP |
|---|---|---|
| レコード | Java 16 | JEP 395 |
| sealedクラス・インターフェイス | Java 17 | JEP 409 |
| switchのパターンマッチング | Java 21 | JEP 441 |
| レコードパターン | Java 21 | JEP 440 |
| 名前のないパターン変数（`_`） | Java 22 | JEP 456 |
<!-- claim: version-record, version-sealed, version-patterns, version-unnamed -->

`verify/Feature*.java`を、その版の1つ前の`--release`でコンパイルすると、次のように拒まれます（1つ目の`--release 15`はレコード、最後の`--release 21`は`_`の例です）。

<!-- output: version-record -->
```
FeatureRecord.java:5: error: records are not supported in -source 15
(use -source 16 or higher to enable records)
```
<!-- /output -->

<!-- output: version-unnamed -->
```
FeatureUnnamed.java:8: error: unnamed variables are not supported in -source 21
(use -source 22 or higher to enable unnamed variables)
```
<!-- /output -->

レコードはJava 14と15で、sealedはJava 15と16で、プレビュー機能として先に提供されていました。
<!-- claim: version-preview -->

## 2. レコードの正体をjavapで見る

### しくみ

レコードの宣言は、コンパイラーがふつうのクラスに書き換える糖衣構文ではなく、言語仕様（JLS 8.10）に決められた種類のクラスです。
それでも、コンパイルされたクラスファイルは、ほぼふつうのクラスと同じ形をしています。

### 観察する

`verify/RecordAnatomy.java`の`record Point(int x, int y) { }`を、`javap -p`で見ます。

<!-- output: record-class-shape -->
```
Compiled from "RecordAnatomy.java"
final class RecordAnatomy$Point extends java.lang.Record {
private final int x;
private final int y;
RecordAnatomy$Point(int, int);
public final java.lang.String toString();
public final int hashCode();
public final boolean equals(java.lang.Object);
public int x();
public int y();
}
```
<!-- /output -->

### 説明

- クラスは`final`で、スーパークラスは`java.lang.Record`である
- コンポーネントごとに、`private final`のフィールドと、同じ名前の`public`のアクセサー（`x()`）がある
- 暗黙の正準コンストラクター（全コンポーネントを受け取るコンストラクター）は、レコードと同じアクセス修飾子を持つ（ここではパッケージアクセス）
<!-- claim: record-class-shape -->

アクセサーの名前は`getX()`ではなく、コンポーネントと同じ`x()`です。

`equals`・`hashCode`・`toString`は、`public final`のメソッドとして暗黙に宣言されます。
<!-- claim: record-generated-methods -->

この3つのメソッドの中身を`javap -c -v`で見ると、どれも`invokedynamic`の1命令だけです。

<!-- output: record-indy -->
```
1: invokedynamic // InvokeDynamic #0:toString:(LRecordAnatomy$Point;)Ljava/lang/String;
1: invokedynamic // InvokeDynamic #0:hashCode:(LRecordAnatomy$Point;)I
2: invokedynamic // InvokeDynamic #0:equals:(LRecordAnatomy$Point;Ljava/lang/Object;)Z
0: #46 REF_invokeStatic java/lang/runtime/ObjectMethods.bootstrap:(...)
#42 x;y
#44 REF_getField RecordAnatomy$Point.x:I
#45 REF_getField RecordAnatomy$Point.y:I
```
<!-- /output -->

`invokedynamic`は、最初に実行されたときにブートストラップメソッドを呼び、実際に実行する処理を作ってもらう命令です。
レコードの場合、ブートストラップメソッドは`java.lang.runtime.ObjectMethods.bootstrap`で、引数としてコンポーネントの名前（`x;y`）と、フィールドを読むメソッドハンドル（`REF_getField`）を受け取ります。
フィールドを比べたり文字列を組み立てたりする処理はクラスファイルには書かれず、実行時にJDKが作ります。
<!-- claim: record-indy -->

クラスファイルには`Record`属性もあり、コンポーネントの名前と型が記録されます。
`Class.isRecord()`や`Class.getRecordComponents()`は、この情報をもとに答えます。
<!-- claim: record-attribute -->

`RecordAnatomy`を実行すると、リフレクションでも同じことが確かめられます。

<!-- output: record-reflection -->
```
isRecord: true
スーパークラス: java.lang.Record
クラスの修飾子: static final
フィールド: private final int x
フィールド: private final int y
コンポーネント: x アクセサー: x()
コンポーネント: y アクセサー: y()
toString: Point[x=1, y=2]
```
<!-- /output -->

クラスのなかに書いたレコード（入れ子のレコード）は、`static`を書かなくても暗黙に`static`になります。
<!-- claim: record-reflection -->

レコードのフィールドは、リフレクションでも書き換えられません。
ふつうのクラスの`final`フィールドは`setAccessible(true)`のあとに`Field.set`で書き換えられますが、レコードのフィールドでは`IllegalAccessException`になります。

<!-- output: record-final-reflection -->
```
ふつうのクラスのfinalフィールド: 書き換えられた（x=99）
レコードのフィールド: IllegalAccessException
```
<!-- /output -->
<!-- claim: record-final-reflection -->

### 宣言の制約

レコードの形が決まっているので、宣言にはふつうのクラスにない制約があります。
`verify/compile-errors/`のファイルをコンパイルすると、次のエラーになります。

- `extends`節を書けない（`RecordExtends.java`、構文の誤りとして`'{' expected`になる）
  <!-- claim: error-record-extends -->
- ふつうのクラスは`java.lang.Record`を直接継承できない（`ExtendRecordClass.java`、「classes cannot directly extend Record」）
  <!-- claim: error-extend-record -->
- コンポーネント以外のインスタンスフィールドを宣言できない（`RecordInstanceField.java`、「field declaration must be static」）。`static`フィールドは宣言できる
  <!-- claim: error-instance-field -->
- フィールドに代入するメソッド（setter）を書けない（`RecordSetter.java`、「cannot assign a value to final variable x」）
  <!-- claim: error-setter -->
- 明示的に宣言するアクセサーは`public`でなければならない（`AccessorNotPublic.java`、「accessor method must be public」）
  <!-- claim: error-accessor-public -->
- メソッドのなかで宣言したローカルレコードは暗黙に`static`なので、囲むメソッドのローカル変数を使えない（`LocalRecordCapture.java`）
  <!-- claim: error-local-capture -->

一方で、インターフェイスの実装、`static`のフィールドとメソッド、インスタンスメソッド、入れ子の型の宣言はできます。

## 3. コンパクトコンストラクター

### しくみ

正準コンストラクターは、仮引数の並びを省いた「コンパクトコンストラクター」の形でも書けます。
本体には引数の検査や正規化だけを書き、フィールドへの代入はコンパイラーに任せます。

```java
record Range(int low, int high) {
    Range {
        if (low > high) {           // low・highは暗黙の仮引数
            int tmp = low;
            low = high;             // 仮引数には代入できる
            high = tmp;
        }
        if (high - low > 100) {
            throw new IllegalArgumentException("範囲が広すぎる: " + low + ".." + high);
        }
    }   // ここで this.low = low; this.high = high; が補われる
}
```

### 観察する

`verify/CompactConstructor.java`の`Range`のコンストラクターを`javap -c -p`で見ると、フィールドへの代入（`putfield`）は検査と入れ替えのあと、`return`の直前にあります。

<!-- output: compact-assign-at-end -->
```
6: if_icmple 15
10: istore_3
12: istore_1
14: istore_2
18: bipush 100
20: if_icmple 38
37: athrow
40: putfield // Field low:I
45: putfield // Field high:I
48: return
```
<!-- /output -->

<!-- output: compact-run -->
```
Range[low=1, high=5]
Range[low=3, high=9]
例外: 範囲が広すぎる: 0..1000
```
<!-- /output -->

### 説明

コンパクトコンストラクターの本体に書いた`low`・`high`は、フィールドではなく暗黙の仮引数です。
本体の最後の文が終わったあとで、仮引数の値がフィールドに代入されます。
そのため、本体のなかで仮引数を書き換えれば（正規化）、書き換えた値がフィールドに入ります。
例外を投げれば、フィールドに値が入る前にオブジェクトの生成が止まります。
<!-- claim: compact-assign-at-end, compact-run -->

本体で`this.low = ...`のようにフィールドへ代入すると、コンパイルエラーになります（`compile-errors/CompactAssignField.java`）。

<!-- output: error-compact-assign -->
```
CompactAssignField.java:5: error: cannot assign a value to final variable x
```
<!-- /output -->
<!-- claim: error-compact-assign -->

## 4. equals・hashCode・toStringの規則

### しくみ

`Record.equals`のAPIドキュメントは、暗黙の`equals`の動きを次のように決めています。

- 同じレコードクラスのインスタンスで、すべてのコンポーネントが等しいときに`true`を返す
- 参照型のコンポーネントは`Objects.equals(this.c, r.c)`で比べる
- 基本型のコンポーネントは、ラッパークラスの`compare`（`double`なら`Double.compare`）が0を返すかで比べる

### 観察する

`verify/RecordEquality.java`は、`double`のコンポーネントを持つ`Measure`で、`==`と`equals`を比べます。

<!-- output: equals-floating -->
```
0.0 == -0.0: true
Measure(0.0).equals(Measure(-0.0)): false
Double.compare(0.0, -0.0) == 0: false
NaN == NaN: false
Measure(NaN).equals(Measure(NaN)): true
Double.compare(NaN, NaN) == 0: true
```
<!-- /output -->

### 説明

`==`では`0.0`と`-0.0`は等しく、`NaN`は自分自身とも等しくありません。
レコードの`equals`は`Double.compare`の規則に従うので、その逆になります。
`NaN`を持つレコードも自分自身と等しくなるので、`HashSet`のキーにしても見失いません。
<!-- claim: equals-floating -->

配列のコンポーネントは`Objects.equals`、つまり配列の参照で比べられます。
中身が同じ配列を持つ2つのレコードは、等しくなりません。

<!-- output: equals-array -->
```
配列の中身が同じ: true
Tags.equals: false
```
<!-- /output -->
<!-- claim: equals-array -->

配列を中身で比べたいときは、`equals`と`hashCode`を自分で書くか、変更できない`List`をコンポーネントにします。

### アクセサーとequalsの関係

暗黙の`equals`・`hashCode`・`toString`は、アクセサーを呼ばずに、フィールドの値を直接使います（2で見た`REF_getField`）。
`verify/RecordEquality.java`の`Name`は、アクセサー`value()`を大文字にして返すように書いています。

<!-- output: equals-uses-fields -->
```
アクセサーの値が同じ: true
Name.equals: false
toString: Name[value=duke]
```
<!-- /output -->

アクセサーの値は同じでも、フィールドの値（`duke`と`DUKE`）が違うので、等しくなりません。
`Record.equals`は、「アクセサーの値を正準コンストラクターに渡して作ったコピーは、元のレコードと等しい」ことを約束として求めています。
この`Name`では`new Name(n.value())`が`n`と等しくならないので、約束を破っています。
アクセサーを明示的に書くときは、フィールドの値をそのまま返すか、防御的コピーを返すだけにします。
<!-- claim: equals-uses-fields -->

### hashCodeとtoString

`hashCode`の計算方法と`toString`の書式は、仕様では決まっていません。
`Point[x=1, y=2]`という今の書式を解析して値を取り出すコードは書かないようにします。
`hashCode`は、同じプログラムを実行し直したときに同じ値になるとも限りません。
<!-- claim: hash-tostring-unspecified -->

## 5. 浅い不変性

### しくみ

レコードのフィールドは`final`なので、作ったあとで別のオブジェクトを指すようには変えられません。
しかし、フィールドが指す先のオブジェクト（`ArrayList`や配列）が変更できるものなら、その中身は変えられます。
これを「浅い不変性」と呼びます（第6章で学んだ防御的コピーが必要になる理由です）。

### 観察する

`verify/ShallowImmutability.java`は、`List`を持つ`Team`と、コンパクトコンストラクターで`List.copyOf`する`SafeTeam`を比べます。

<!-- output: shallow-immutable -->
```
Team: Team[name=A, members=[Alice, Bob]]
hashCodeが変わった: true
HashSetのcontains: false
Team: Team[name=A, members=[Alice, Bob, Carol]]
SafeTeam: SafeTeam[name=B, members=[Alice]]
SafeTeamのリストは変更できない: java.lang.UnsupportedOperationException
Scores.values()[0]: 99
ScoresのtoString: Scores[values=[I@...]
```
<!-- /output -->

### 説明

- `Team`は、作ったあとに元のリストへ要素を足すと、レコードの中身も変わる。`hashCode`も変わるので、`HashSet`に入れたあとで変えると`contains`で見つからなくなる
- アクセサーで得たリストにも要素を足せる
- `SafeTeam`は、コンパクトコンストラクターで変更できないコピー（`List.copyOf`）に置き換えているので、元のリストを変えても影響せず、アクセサーで得たリストも変更できない
- 配列のコンポーネントは、アクセサーで得た配列の要素を書き換えられる。`toString`も中身ではなく`[I@…`の形になる
<!-- claim: shallow-immutable -->

### 自分自身を含むレコード

変更できるリストを持つレコードは、自分自身をそのリストに入れて循環を作れます。
すると、暗黙の`hashCode`と`toString`は自分を何度もたどり、`StackOverflowError`になります（`verify/CircularRecord.java`）。

<!-- output: circular -->
```
hashCode: StackOverflowError
toString: StackOverflowError
SafeNode: SafeNode[value=root, children=[]]
```
<!-- /output -->

`SafeNode`のようにコンパクトコンストラクターで`List.copyOf`しておけば、作ったあとに元のリストを変えても子は変わらないので、循環を作れません。
<!-- claim: circular -->

`Node(String value, Node parent)`のように自分と同じレコードを直接持つだけなら、循環は作れません。
フィールドは作るときにしか決められないので、参照する相手は先に作られていなければならないからです。

## 6. sealedとパターンマッチング

### しくみ

`sealed`を付けたクラスやインターフェイスは、`permits`に書いた型だけに継承・実装を許します。
レコードは暗黙に`final`なので、そのまま`sealed`なインターフェイスを実装できます。
コンパイラーは許可された型の一覧を知っているので、switchがすべての場合を扱っているか（網羅しているか）を確かめられます。

```java
sealed interface Shape permits Circle, Square, Rect { }
record Circle(double radius) implements Shape { }
record Square(double side) implements Shape { }
record Rect(double width, double height) implements Shape { }

static double area(Shape shape) {
    return switch (shape) {         // defaultがなくても網羅的
        case Circle(double r) -> Math.PI * r * r;
        case Square(double s) -> s * s;
        case Rect(double w, double h) -> w * h;
    };
}
```

### 観察する：PermittedSubclasses属性

`verify/SealedShapes.java`の`Shape`を`javap -v`で見ると、許可したサブクラスの一覧が`PermittedSubclasses`属性として書かれています。

<!-- output: sealed-permitted -->
```
PermittedSubclasses:
SealedShapes$Circle
SealedShapes$Square
SealedShapes$Rect
```
<!-- /output -->

<!-- claim: sealed-permitted -->

JVMは、この一覧にないクラスが`Shape`を実装しようとすると、クラスを読み込む段階で`IncompatibleClassChangeError`を投げて拒みます。
コンパイラーの検査だけでなく、実行時にも許可した型しか現れないことが保証されます。
<!-- claim: sealed-jvm-check -->

`sealed`な型を直接継承・実装するクラスは、`final`・`sealed`・`non-sealed`のどれかを付けなければなりません（`compile-errors/SealedSubclassModifier.java`、「sealed, non-sealed or final modifiers expected」）。
<!-- claim: error-sealed-modifier -->

### 観察する：網羅性の検査

`compile-errors/NonExhaustiveSwitch.java`は、`Rect`の`case`を書き忘れたswitch式です。

<!-- output: error-not-exhaustive -->
```
NonExhaustiveSwitch.java:12: error: the switch expression does not cover all possible input values
```
<!-- /output -->

`default`を書いていないので、型を足したときにswitchを直し忘れても、コンパイラーが知らせてくれます。
`default`を書くと、この検査が働かなくなります。
<!-- claim: error-not-exhaustive -->

### 観察する：switchのバイトコード

`SealedShapes`の`area`を`javap -c -p`で見ると、次の命令が並びます。

<!-- output: switch-bytecode -->
```
2: invokestatic // Method java/util/Objects.requireNonNull:(Ljava/lang/Object;)Ljava/lang/Object;
11: invokedynamic // InvokeDynamic #0:typeSwitch:(LSealedShapes$Shape;I)I
16: tableswitch { // 0 to 2
44: new // class java/lang/MatchException
200: new // class java/lang/MatchException
```
<!-- /output -->

- 最初に`Objects.requireNonNull`でセレクターを調べるので、`null`を渡すと`NullPointerException`になる
- `invokedynamic`の`typeSwitch`（ブートストラップは`java.lang.runtime.SwitchBootstraps.typeSwitch`）が、セレクターの型から当てはまる`case`の番号を求め、`tableswitch`でそこへ飛ぶ
- どの番号にも当たらないとき（`default`の行き先）は`MatchException`を投げる
<!-- claim: switch-bytecode -->

`MatchException`を作る箇所は2つあります。
1つ目（`default`の行き先）は上のとおりで、2つ目は、次に見るアクセサーの呼び出しが例外で終わったときに、その例外を原因（cause）として包んで投げるためのものです。
バイトコードの例外表は、アクセサーを呼ぶ命令の範囲を、この2つ目の箇所に結び付けています。
例外表の各行は、範囲の始まり・終わり・飛び先・受け取る例外の型の順に並んでいます。

<!-- output: accessor-exception-wrap -->
```
60 63 199 Class java/lang/Throwable
105 108 199 Class java/lang/Throwable
146 149 199 Class java/lang/Throwable
165 168 199 Class java/lang/Throwable
```
<!-- /output -->
<!-- claim: accessor-exception-wrap -->

コンパイルしたときには網羅していても、あとで`Shape`に許可する型を足して`Shape`だけをコンパイルし直すと、古いswitchのクラスファイルは新しい型を知りません。
その型のインスタンスが来たときに投げられるのが、この`MatchException`です。
<!-- claim: match-exception-separate -->

### 観察する：レコードパターンはアクセサーを呼ぶ

同じバイトコードを見ると、`case Circle(double r)`の分解は、アクセサー`radius()`の呼び出しになっています。

<!-- output: record-pattern-accessor -->
```
60: invokevirtual // Method SealedShapes$Circle.radius:()D
105: invokevirtual // Method SealedShapes$Square.side:()D
146: invokevirtual // Method SealedShapes$Rect.width:()D
165: invokevirtual // Method SealedShapes$Rect.height:()D
```
<!-- /output -->

`equals`がフィールドを直接読むのとは違い、レコードパターンは仕様（JLS 14.30.2）でアクセサーを呼ぶと決められています。
<!-- claim: record-pattern-accessor -->

`SealedShapes`の`Circle`は、アクセサーが呼ばれると表示するように書いてあります。

<!-- output: sealed-run -->
```
permitted: [Circle, Square, Rect]
radius()が呼ばれた
Circle: 3.141592653589793
Square: 4.0
Rect: 6.0
null: NullPointerException
```
<!-- /output -->
<!-- claim: sealed-run -->

4で見たとおり、アクセサーはフィールドの値をそのまま返すように書きます。
そうしておけば、`equals`とパターンで見える値が食い違いません。

## 7. データ指向プログラミング

レコード・sealed・パターンマッチングを組み合わせると、次の考え方でプログラムを組み立てられます。
これは仕様で決まったものではなく、設計の考え方です。
JEP 441は、switchのパターンマッチングによって、複雑な「データ指向の問い合わせ」を簡潔かつ安全に書けるようになると述べています。
<!-- claim: dop-jep441 -->

- データは、不変のレコードとして、処理を持たない形で表す
- データが取りうる形の一覧を、`sealed`な型で閉じる
- 処理は、データの外の関数として、パターンのswitchで形ごとに書く
- 状態の変化は、元のレコードを変えず、新しいレコードを作って表す

6の`area`は、この形の例です。
オブジェクト指向の書き方では、`Shape`に`area()`を宣言して各クラスでオーバーライドします（第5章）。
どちらがよいかは、何が増えるかで決まります。

- 型が増えることが多い（新しい図形を足す）なら、オーバーライドで書くと、既存のコードを変えずに型を足せる
- 処理が増えることが多い（面積・周の長さ・描画・保存などを足す）なら、switchで書くと、既存の型を変えずに処理を足せる。型を足したときは、網羅性の検査が直す場所を教えてくれる

`RecordsAndDOPDemo`の式の木（`Const`・`Var`・`Add`・`Mul`）は、処理（評価・簡約・表示）が増えていく典型的な例です。

```java
sealed interface Expr permits Const, Var, Add, Mul { }

static double eval(Expr expr, Map<String, Double> env) {
    return switch (expr) {
        case Const(var value) -> value;
        case Var(var name) -> env.getOrDefault(name, 0.0);
        case Add(var left, var right) -> eval(left, env) + eval(right, env);
        case Mul(var left, var right) -> eval(left, env) * eval(right, env);
    };
}
```

レコードパターンは入れ子にでき、使わないコンポーネントは`_`で受けられます。
パターンのなかに定数（`"Japan"`など）は書けないので、変数で受けて`when`で比べます。

```java
case Person(var name, _, Address(_, var city, var country, _), _)
        when country.equals("Japan") -> name + " lives in " + city;
```

## 8. シリアライズ

### しくみ

ふつうの`Serializable`なクラスは、読み戻すときにそのクラスのコンストラクターを呼ばずにオブジェクトを作り、フィールドに直接値を入れます。
そのため、コンストラクターに書いた検査を通らないオブジェクトが作られることがあります。
レコードは、ストリームから読んだ値で正準コンストラクターを呼んでオブジェクトを作ります（Java Object Serialization Specificationの1.13）。

### 観察する

`verify/RecordSerialization.java`は、同じ検査をコンストラクターに書いたレコード`Range`とふつうのクラス`OldRange`を書き込み、読み戻します。
途中で、バイト列の`low`の値を書き換えて`low > high`のデータも読ませます。

<!-- output: serialization-ctor -->
```
書き込み: Range
Rangeの正準コンストラクター(1, 5)
読み込み: Range
Rangeの正準コンストラクター(1, 5)
結果: Range[low=1, high=5]
読み込み: 書き換えたRange
Rangeの正準コンストラクター(9, 5)
InvalidObjectException: low > high
書き込み: OldRange
OldRangeのコンストラクター(1, 5)
読み込み: OldRange
結果: 1..5
読み込み: 書き換えたOldRange
結果: 9..5（コンストラクターの検査を通らずに読み込まれる）
RangeのserialVersionUID: 0
```
<!-- /output -->

### 説明

`Range`を読み戻すときには正準コンストラクターが呼ばれ、書き換えたデータは検査で拒まれて`InvalidObjectException`になります。
`OldRange`ではコンストラクターが呼ばれず、検査を通らない`9..5`がそのまま作られます。
<!-- claim: serialization-ctor -->

`serialVersionUID`を宣言しないレコードの`serialVersionUID`は0Lで、レコードでは`serialVersionUID`の一致は検査されません。
<!-- claim: serial-version-uid -->

ほかにも、レコードのシリアライズには次の規則があります。

- 互換性は、コンポーネントの名前と型で決まる。ストリームにないコンポーネントには、型の既定値（`null`や0）が正準コンストラクターに渡される
- レコードに`writeObject`・`readObject`などを書いても無視される。読み戻すときの検査は正準コンストラクターに書く
- コンポーネントからレコード自身への循環は、読み戻すと保たれない（コンポーネントを先に読み、そのあとでレコードを作るため）
<!-- claim: serialization-rules -->

## 9. メモリ上の配置

レコードのオブジェクトは、同じフィールドを持つふつうのクラスのオブジェクトと同じように配置されます。
`verify/RecordLayout.java`は、同じコンポーネントを違う順に宣言した`Mixed1`・`Mixed2`と、同じフィールドを持つふつうのクラス`MixedClass`の配置を、JOLで表示します。

<!-- output: layout-reorder -->
```
RecordLayout$Mixed1 object internals:
0 8 (object header: mark) N/A
8 4 (object header: class) N/A
12 1 byte Mixed1.a N/A
13 1 byte Mixed1.c N/A
14 2 (alignment/padding gap)
16 8 long Mixed1.b N/A
24 4 java.lang.String Mixed1.d N/A
28 4 (object alignment gap)
Instance size: 32 bytes
RecordLayout$Mixed2 object internals:
0 8 (object header: mark) N/A
8 4 (object header: class) N/A
12 1 byte Mixed2.a N/A
13 1 byte Mixed2.c N/A
14 2 (alignment/padding gap)
16 8 long Mixed2.b N/A
24 4 java.lang.String Mixed2.d N/A
28 4 (object alignment gap)
Instance size: 32 bytes
大きさ: Mixed1 = 32 bytes
大きさ: Mixed2 = 32 bytes
大きさ: MixedClass = 32 bytes
```
<!-- /output -->

`Mixed1`（`byte, long, byte, String`の順）も`Mixed2`（`String, byte, byte, long`の順）も、HotSpotがフィールドを並べ替えて同じ配置にしています。
コンポーネントを宣言する順序は、オブジェクトの大きさに影響しません。
同じフィールドを持つふつうのクラスとも大きさは同じで、レコードだから小さくなるわけではありません。
この実行では圧縮された参照（compressed oops）が有効で、`String`への参照は4バイトでした。
<!-- claim: layout-reorder -->

JOLは`sun.misc.Unsafe.objectFieldOffset`でフィールドの位置を調べますが、このメソッドはレコードのフィールドを受け付けず、例外（「can't get field offset on a record class」）を投げます。
そのため、レコードを調べるときは`-Djol.magicFieldOffset=true`を付けて、別の方法で位置を求めさせます。
<!-- claim: unsafe-record-offset -->

## 10. 処理時間を測るデモ

`RecordsAndDOPDemo`と`RecordOptimizationDemo`は、次のような形式で処理時間を表示します（数値の部分は省いています）。

```
=== Record vs Traditional Class Performance ===
Creation - Traditional: (時間) ms, Record: (時間) ms
Equals - Traditional: (時間) ms, Record: (時間) ms
ToString - Traditional: (時間) ms, Record: (時間) ms

=== Serialization Performance ===
Standard serialization (round trip): (時間) ms
Fast serialization (round trip): (時間) ms
Speedup: (倍率)x
```

時間は空回し（ウォームアップ）のあとに`System.nanoTime()`で測っていますが、JVM・CPU・負荷などの環境で変わり、どちらが短いかが入れ替わることもあります。
このデモは傾向を体験するためのもので、正確に比べるにはJMH（Java Microbenchmark Harness）を使います。
2と9で見たとおり、レコードのフィールドとメソッドはふつうのクラスと同じ形なので、レコードにしただけで処理時間やメモリが大きく変わる理由はありません。

## 11. これから：値オブジェクト（Project Valhalla）

JEP 401は、オブジェクトの同一性を持たない「値オブジェクト」を導入する提案です。
値オブジェクトどうしの`==`はフィールドの値で比べられ、JVMはその表現を工夫してよい（たとえば配列に平坦に並べる）ことになります。
レコードは`final`でフィールドもすべて`final`なので、`value record`として値オブジェクトにしやすい候補として挙げられています。
2026年11月の時点で、JEP 401はプレビュー機能としてJDK 28に統合されています（JDK 25には含まれていません）。
JVMがどう表現するかは実装に任されていて、平坦化などが必ず行われるとは約束されていません。
<!-- claim: value-objects-jep -->

`value record`はJava 25ではコンパイルできません（`compile-errors/ValueRecord.java`）。
<!-- claim: value-record-not-in-25 -->

## よくある誤解

- レコードのアクセサーは`getX()`という名前になる
  - コンポーネントと同じ`x()`である（2）
  <!-- claim: record-class-shape -->
- レコードは、中身まで含めて不変である
  - フィールドは`final`だが、参照先の`List`や配列は変えられる（5）
  <!-- claim: shallow-immutable -->
- アクセサーを書き換えれば、`equals`の比べ方も変わる
  - 暗黙の`equals`はフィールドの値を直接比べる。アクセサーをフィールドと違う値を返すように書くと、`Record.equals`の約束を破る（4）
  <!-- claim: equals-uses-fields -->
- レコードの`equals`は、`double`を`==`で比べる
  - `Double.compare`の規則で比べるので、`0.0`と`-0.0`は等しくなく、`NaN`どうしは等しい（4）
  <!-- claim: equals-floating -->
- 配列のコンポーネントは中身で比べられる
  - 参照で比べられる（4）
  <!-- claim: equals-array -->
- レコードにすると、ふつうのクラスよりメモリが少なくて済む
  - 同じフィールドを持つふつうのクラスと同じ大きさである（9）
  <!-- claim: layout-reorder -->
- sealedな型のswitchにも`default`を書いておくほうが安全である
  - `default`を書くと網羅性の検査が働かず、型を足したときにコンパイラーが知らせてくれなくなる（6）
  <!-- claim: error-not-exhaustive -->
- レコードの互換性は`serialVersionUID`で管理する
  - レコードでは`serialVersionUID`の一致は検査されず、コンポーネントの名前と型で決まる（8）
  <!-- claim: serial-version-uid, serialization-rules -->
- `toString`の出力を解析すれば、コンポーネントの値を取り出せる
  - `toString`の書式は仕様で決まっておらず、変わることがある（4）
  <!-- claim: hash-tostring-unspecified -->

## レコードの使いどころ

向いているのは、値の組そのものに意味があるデータです。

- メソッドの戻り値として複数の値をまとめて返す
- データ転送オブジェクト（DTO）や、イベント、設定値の組
- 金額や座標のような値オブジェクト（`record Money(BigDecimal amount, Currency currency)`）
- `sealed`な型と組み合わせた、取りうる形が決まったデータ（式の木、処理の結果など）

向いていないのは、次のようなものです。

- 作ったあとに状態が変わっていくもの（買い物かごなど）。レコードのフィールドは代入できない
- 外に見せる値と内部の表現を変えたいもの。レコードはコンポーネントがそのまま`public`のアクセサーになる
- ほかのクラスを継承したいもの。レコードは`extends`を書けない

## 参考資料

- Java言語仕様（Java SE 25）
  - 8.10「Record Classes」
  - 8.1.1.2「sealed, non-sealed, and final Classes」
  - 14.11.1.1「Exhaustive Switch Blocks」
  - 14.30.2「Pattern Matching」
  - `https://docs.oracle.com/javase/specs/jls/se25/html/`
- Java仮想マシン仕様（Java SE 25）
  - 4.7.30「The Record Attribute」
  - 4.7.31「The PermittedSubclasses Attribute」
  - `https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html`
- APIドキュメント（Java SE 25）
  - `java.lang.Record`
  - `java.lang.runtime.ObjectMethods`
  - `java.lang.runtime.SwitchBootstraps`
  - `java.lang.reflect.Field`
  - `https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/Record.html`
- Java Object Serialization Specification（Java SE 25）
  - 1.13「Serialization of Records」
  - `https://docs.oracle.com/en/java/javase/25/docs/specs/serialization/serial-arch.html`
- JEP
  - JEP 395: Records（`https://openjdk.org/jeps/395`）
  - JEP 409: Sealed Classes（`https://openjdk.org/jeps/409`）
  - JEP 440: Record Patterns（`https://openjdk.org/jeps/440`）
  - JEP 441: Pattern Matching for switch（`https://openjdk.org/jeps/441`）
  - JEP 456: Unnamed Variables & Patterns（`https://openjdk.org/jeps/456`）
  - JEP 401: Value Objects (Preview)（`https://openjdk.org/jeps/401`）
