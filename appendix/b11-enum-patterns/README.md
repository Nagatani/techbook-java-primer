# 付録B.11 Enumの高度な活用

第8章で学んだenum（列挙型）が、コンパイルされると何になり、JVMとライブラリがそれをどう扱うのかを観察しながら学ぶ節です。
後半では、enumを使った設計の例（状態機械・ストラテジー・シングルトン・権限・設定）を、体験用のデモで紹介します。

## この節の読み方

この節の解説のうち、事実を述べた段落には`<!-- claim: … -->`という印を付けています（GitHubの画面では見えません）。
印の名前は同じディレクトリの`claims.yml`に対応し、そこにその主張の確かめ方（`javap`の照合、コンパイラーのメッセージ、実行結果、出典）を書いています。
リポジトリのルートで`npm run check:deep-dive -- b11`を実行すると、すべての主張をJDK 25で確かめ直せます。
「観察する」の欄に載せた出力は、この検査が実際に出したものです。

## ファイル構成

### 観察用のプログラム（verify/）

| ファイル | 観察すること |
|---|---|
| `EnumAnatomy.java` | enumのクラスの形、`values()`・`valueOf`・static初期化子の中身（`javap`で見る） |
| `ConstantBodies.java` | 定数ごとの本体を持つenumと、その匿名クラス |
| `ReflectAndSerialize.java` | リフレクションとシリアライズで定数が増えないこと、enumによるシングルトン |
| `InitOrder.java` | 定数が作られる時期と、static初期化子との順序 |
| `EnumSwitch.java` | enumのswitchのコンパイル結果（`javap`で見る） |
| `SeparateCompilation.java` | enumだけを変えて再コンパイルしたときのswitchのふるまい |
| `SealedWithEnum.java` | sealedインターフェイスとenumの定数を組み合わせたswitch |
| `EnumCollections.java` | `EnumSet`と`EnumMap`の内部の表現 |
| `EnumMemory.java` | `EnumSet`・`EnumMap`と`HashSet`・`HashMap`の大きさ（JOLで測る） |
| `OrdinalPitfall.java` | `ordinal()`を保存に使ったときの問題 |
| `GenericGetter.java` | enumのキーで値を取り出す汎用のメソッドの型の問題 |
| `compile-errors/` | コンパイルエラーになる書き方（1つのファイルに1つの誤り） |

`verify/`のプログラムはパッケージを持たないので、`verify/`ディレクトリでそのままコンパイル・実行できます。

```bash
cd verify
javac EnumAnatomy.java && javap -p 'EnumAnatomy$Color'
javac EnumCollections.java && java --add-opens java.base/java.util=ALL-UNNAMED EnumCollections
javac SeparateCompilation.java && java SeparateCompilation
```

`EnumCollections`と`EnumMemory`は、`java.util`の非公開のフィールドを読むので`--add-opens`を付けて実行します。
`EnumMemory`はJOL（`org.openjdk.jol:jol-core:0.17`）をクラスパスに加えて実行します（手順はファイルの先頭に書いてあります）。

### 体験用のデモ（src/）

```
src/main/java/com/example/enumpatterns/
├── Main.java          # デモを選んで実行するメニュー
├── statemachine/      # 状態機械（注文の状態遷移）
├── strategy/          # ストラテジー（価格と税の計算）
├── permission/        # EnumSetによる権限の管理
├── configuration/     # 設定キーと設定の管理
├── factory/           # enumによるシングルトン（コネクションプール）
├── performance/       # EnumSet・EnumMapなどの時間の比較
└── examples/          # 各パターンのデモと総合的な例
```

```bash
cd appendix/b11-enum-patterns
./build.sh                                   # outにコンパイルする
java -cp out com.example.enumpatterns.Main   # メニューから選ぶ
java -cp out com.example.enumpatterns.Main all
```

`Main`には、`state`、`strategy`、`permission`、`config`、`comprehensive`、`performance`、`all`のいずれかを引数として渡せます。
Mavenでは`mvn clean package`のあとに`mvn exec:java -Dexec.args=all`で実行できます。

## 1. enumの正体

### しくみ

Java言語仕様（JLS）の8.9は、enumを「少数の名前付きのインスタンスを定義する、制限された種類のクラス」と定めています。
enumの直接のスーパークラスは`java.lang.Enum<E>`で、定数ごとの本体（2で説明します）を持たないenumは暗黙に`final`です。
`values()`と`valueOf(String)`は、ソースに書かなくても暗黙に宣言されるメソッドです。

### 観察する

`verify/EnumAnatomy.java`の`enum Color { RED, GREEN, BLUE }`を`javap -p`で見ると、次のクラスになっています。

<!-- output: enum-class-shape -->
```
final class EnumAnatomy$Color extends java.lang.Enum<EnumAnatomy$Color> {
  public static final EnumAnatomy$Color RED;
  public static final EnumAnatomy$Color GREEN;
  public static final EnumAnatomy$Color BLUE;
  private static final EnumAnatomy$Color[] $VALUES;
  public static EnumAnatomy$Color[] values();
  public static EnumAnatomy$Color valueOf(java.lang.String);
  private EnumAnatomy$Color();
  private static EnumAnatomy$Color[] $values();
  static {};
}
```
<!-- /output -->

`Color`は`Enum<Color>`を継承する`final`のクラスで、3つの定数は`public static final`のフィールドです。
ソースに書いていない`$VALUES`（すべての定数の配列）、`values()`、`valueOf`、`private`のコンストラクター、static初期化子（`static {}`）があります。
<!-- claim: enum-class-shape -->

`javap -v -p`でフラグを見ると、クラスと定数のフィールドに`ACC_ENUM`が付いています。
`$VALUES`はソースにないものなので`ACC_SYNTHETIC`が付き、引数のないはずのコンストラクターの記述子は`(Ljava/lang/String;I)V`です。
コンパイラーが、定数の名前と番号を受け取る引数を足しています。

<!-- output: enum-flags -->
```
flags: (0x4030) ACC_FINAL, ACC_SUPER, ACC_ENUM
public static final EnumAnatomy$Color RED;
descriptor: LEnumAnatomy$Color;
flags: (0x4019) ACC_PUBLIC, ACC_STATIC, ACC_FINAL, ACC_ENUM
public static final EnumAnatomy$Color GREEN;
descriptor: LEnumAnatomy$Color;
flags: (0x4019) ACC_PUBLIC, ACC_STATIC, ACC_FINAL, ACC_ENUM
public static final EnumAnatomy$Color BLUE;
descriptor: LEnumAnatomy$Color;
flags: (0x4019) ACC_PUBLIC, ACC_STATIC, ACC_FINAL, ACC_ENUM
private static final EnumAnatomy$Color[] $VALUES;
descriptor: [LEnumAnatomy$Color;
flags: (0x101a) ACC_PRIVATE, ACC_STATIC, ACC_FINAL, ACC_SYNTHETIC
private EnumAnatomy$Color();
descriptor: (Ljava/lang/String;I)V
flags: (0x0002) ACC_PRIVATE
```
<!-- /output -->

JLSは、この引数を足すことを「コンパイラーはおそらくそうする」と書くだけで、決めてはいません。
定数を作れるのはそのenumをコンパイルしたコンパイラーだけで、ほかのクラスは`public static final`のフィールドを読むだけだからです。
<!-- claim: enum-flags -->

`values()`と`valueOf`の中身は次のとおりです。

<!-- output: values-clone -->
```
public static EnumAnatomy$Color[] values();
0: getstatic // Field $VALUES:[LEnumAnatomy$Color;
3: invokevirtual // Method "[LEnumAnatomy$Color;".clone:()Ljava/lang/Object;
6: checkcast // class "[LEnumAnatomy$Color;"
9: areturn
public static EnumAnatomy$Color valueOf(java.lang.String);
0: ldc // class EnumAnatomy$Color
2: aload_0
3: invokestatic // Method java/lang/Enum.valueOf:(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;
6: checkcast // class EnumAnatomy$Color
9: areturn
```
<!-- /output -->

`values()`は`$VALUES`の配列を`clone()`して返し、`valueOf`は`Enum.valueOf(Class, String)`に任せています。
<!-- claim: values-clone -->

static初期化子では、宣言の順に定数のオブジェクトを`new`で作り、コンストラクターに名前と番号（0から）を渡しています。
最後に、すべての定数を並べた配列を`$values()`で作って`$VALUES`に入れます。

<!-- output: static-init -->
```
static {};
0: new // class EnumAnatomy$Color
3: dup
4: ldc // String RED
6: iconst_0
7: invokespecial // Method "<init>":(Ljava/lang/String;I)V
10: putstatic // Field RED:LEnumAnatomy$Color;
13: new // class EnumAnatomy$Color
16: dup
17: ldc // String GREEN
19: iconst_1
20: invokespecial // Method "<init>":(Ljava/lang/String;I)V
23: putstatic // Field GREEN:LEnumAnatomy$Color;
26: new // class EnumAnatomy$Color
29: dup
30: ldc // String BLUE
32: iconst_2
33: invokespecial // Method "<init>":(Ljava/lang/String;I)V
36: putstatic // Field BLUE:LEnumAnatomy$Color;
39: invokestatic // Method $values:()[LEnumAnatomy$Color;
42: putstatic // Field $VALUES:[LEnumAnatomy$Color;
45: return
```
<!-- /output -->
<!-- claim: static-init -->

### 説明

`name()`と`ordinal()`が返すのは、このときコンストラクターに渡された名前と番号です。
`values()`は呼ぶたびに配列を複製するので、返された配列を書き換えてもenumには影響しません。
その代わり、ループで何度も`values()`を呼ぶと、そのたびに配列が作られます。

<!-- output: values-new-array -->
```
スーパークラス: java.lang.Enum
isEnum: true
RED ordinal=0
GREEN ordinal=1
BLUE ordinal=2
values()は同じ配列か: false
書き換えたあとのvalues(): [RED, GREEN, BLUE]
```
<!-- /output -->
<!-- claim: values-new-array -->

`valueOf`は名前が一致する定数を返します。
ない名前を渡すと`IllegalArgumentException`、`null`を渡すと`NullPointerException`になります。

<!-- output: valueof-errors -->
```
valueOf("GREEN") == GREEN: true
valueOf("PURPLE"): IllegalArgumentException: No enum constant EnumAnatomy.Color.PURPLE
valueOf(null): NullPointerException: Name is null
```
<!-- /output -->
<!-- claim: valueof-errors -->

## 2. 定数ごとの本体を持つenum

### しくみ

定数の後ろに`{ … }`で本体を書くと、定数ごとにメソッドをオーバーライドできます（第8章）。
JLSの8.9.1によると、この本体はenumのクラスを直接継承する`final`の匿名クラスを宣言します。
本体を持つ定数が1つでもあると、enumのクラスは`final`ではなく`sealed`になり、継承を許すのはそれらの匿名クラスだけです。
<!-- claim: constant-body-class -->

### 観察する

`verify/ConstantBodies.java`の`Op`は、`PLUS`と`MINUS`が本体を持ち、`TIMES`は持ちません。
`javap -v -p 'ConstantBodies$Op'`の一部です。

<!-- output: constant-body-class -->
```
flags: (0x4020) ACC_SUPER, ACC_ENUM
new // class ConstantBodies$Op$1
new // class ConstantBodies$Op$2
new // class ConstantBodies$Op
PermittedSubclasses:
ConstantBodies$Op$1
ConstantBodies$Op$2
```
<!-- /output -->

クラスのフラグから`ACC_FINAL`が消え、`PermittedSubclasses`に`Op$1`と`Op$2`が並んでいます。
static初期化子は、`PLUS`のオブジェクトを`Op`ではなく`Op$1`として作ります。
`Op$1`は`final`で、`Op`を継承しています。

<!-- output: constant-body-final -->
```
final class ConstantBodies$Op$1 extends ConstantBodies$Op {
```
<!-- /output -->
<!-- claim: constant-body-final -->

実行すると、`getClass()`と`getDeclaringClass()`の違いがわかります。

<!-- output: getclass-vs-declaring -->
```
PLUS: 3 + 2 = 5
MINUS: 3 - 2 = 1
TIMES: 3 * 2 = 6
PLUS getClass=ConstantBodies$Op$1 getDeclaringClass=ConstantBodies$Op getClass().isEnum()=false
MINUS getClass=ConstantBodies$Op$2 getDeclaringClass=ConstantBodies$Op getClass().isEnum()=false
TIMES getClass=ConstantBodies$Op getDeclaringClass=ConstantBodies$Op getClass().isEnum()=true
EnumSet.allOf: [PLUS, MINUS, TIMES]
```
<!-- /output -->

### 説明

本体を持つ定数の`getClass()`は匿名クラスを返し、そのクラスの`isEnum()`は`false`です。
enumの型が必要なとき（ログに型名を出す、`Class`をキーにした表を引くなど）は、`getDeclaringClass()`を使います。
`EnumSet`や`valueOf`は`getDeclaringClass()`の型で扱うので、本体の有無にかかわらず使えます。
<!-- claim: getclass-vs-declaring -->

## 3. インスタンスが増えないしくみ

enumの定数は、宣言したもの以外にインスタンスができないことが保証されています。
そのため`==`で比べてよく、シングルトンにも使えます。

### コンパイラーによる禁止

次の書き方は、どれもコンパイルエラーになります。
`verify/compile-errors/`のファイルを`javac`でコンパイルすると確かめられます。

| 書き方 | javacのメッセージ | ファイル |
|---|---|---|
| `new Color()` | `enum classes may not be instantiated` | `NewEnum.java` |
| `public Color() {}` | `modifier public not allowed here` | `PublicConstructor.java` |
| `class MyEnum extends Enum` | `classes cannot directly extend java.lang.Enum` | `ExtendEnum.java` |
| `class MyColor extends Color` | `cannot inherit from final Color`、`enum classes are not extensible` | `SubclassEnum.java` |
| `equals`のオーバーライド | `overridden method is final` | `OverrideEquals.java` |
<!-- claim: no-new, no-public-constructor, no-extend-enum, no-subclass-enum, final-equals -->

enumのコンストラクターは、修飾子を書かなければ`private`です。
`Enum`の`equals`は`final`で、同一性（`==`）で比べます。

### 実行時の3つのしくみ

JLSの8.9は、コンパイルエラーのほかに3つのしくみが定数以外のインスタンスを防ぐと述べています。
`Enum`の`clone`が`final`で（`CloneNotSupportedException`を投げる）、リフレクションによる生成が禁止され、シリアライズで特別に扱われることです。
<!-- claim: three-mechanisms -->

`verify/ReflectAndSerialize.java`で、リフレクションとシリアライズを試します。

<!-- output: reflection-blocked -->
```
Registryのコンストラクター: private ReflectAndSerialize$Registry(java.lang.String,int)
newInstance: IllegalArgumentException: Cannot reflectively create enum objects
ClassSingleton: リフレクションで作ったものは別のオブジェクトか: true
```
<!-- /output -->

`setAccessible(true)`にしても、`Constructor.newInstance`は`IllegalArgumentException`を投げます。
`private`のコンストラクターを持つふつうのクラスでは、リフレクションで2つ目のインスタンスを作れてしまいます。
<!-- claim: reflection-blocked -->

<!-- output: serialization-by-name -->
```
Registry: 読み戻したものは同じ定数か: true
Registry: 読み戻したあとのnext(): 3
ストリームに名前INSTANCEがあるか: true
ストリームにフィールド名countがあるか: false
ClassSingleton: 読み戻したものは同じオブジェクトか: false
```
<!-- /output -->

シリアライズの仕様（Java Object Serialization Specification 1.12）は、enumの定数の直列化の形は名前だけで、フィールドの値は含まないと定めています。
読み戻すときは、その名前で`Enum.valueOf`を呼んで既存の定数を返すので、同じオブジェクトになります。
上の出力でも、ストリームには名前`INSTANCE`があり、フィールド名`count`はありません。
読み戻した定数の`next()`が3を返すのは、同じオブジェクトの`count`をそのまま使っているからです。
`readResolve`を書いていないクラスのシングルトンは、読み戻すと別のオブジェクトになります。
<!-- claim: serialization-by-name -->

### enumによるシングルトン

このため、定数が1つのenumは、リフレクションやシリアライズで2つ目ができないシングルトンになります（`src/`の`factory/DatabaseConnection.java`）。
ただし、enumが保証するのはインスタンスが1つであることと、その生成がスレッドセーフなこと（4）までです。
インスタンスが可変の状態を持つなら、その状態のスレッドセーフ性は別に確保します。
`DatabaseConnection`は、コネクションプールを`BlockingQueue`と`synchronized`で守っています。
シリアライズしてもフィールドは書かれないので、別のJVMで読み戻した定数の状態は、そのJVMで初期化した値です。

## 4. 定数が作られる時期と順序

### しくみ

1で見たとおり、定数はstatic初期化子で作られます。
static初期化子はクラスの初期化のときに動き、クラスの初期化はそのクラスのstaticメソッドを呼ぶ、定数でないstaticフィールドを使う、といった最初の使用の直前に行われます（JLSの12.4.1）。
<!-- claim: init-order -->

クラスの初期化は、ほかのスレッドが同時に初期化しようとする場合も含めて、JVMが同期をとって1回だけ行います（JLSの12.4.2）。
enumの定数の生成はこれに含まれるので、ロックを書かなくてもスレッドセーフに1回だけ行われます。
<!-- claim: init-thread-safe -->

### 観察する

`verify/InitOrder.java`は、コンストラクターとstatic初期化子で表示します。

<!-- output: init-order -->
```
mainの始まり（Levelはまだ初期化されていない）
Level.HIGHを使う
  コンストラクター: LOW
  コンストラクター: HIGH
  static初期化子: BY_CODEを作る
fromCode("L") = LOW, h = HIGH
```
<!-- /output -->

`main`が始まった時点では、まだ定数は作られていません。
`Level.HIGH`を使ったときに、2つの定数のコンストラクターが宣言の順に動き、そのあとで、定数の後に書いた`BY_CODE`のstatic初期化子が動きます。

### 説明

定数はenumの本体の先頭に書く決まりなので、定数のコンストラクターは、ほかのstaticフィールドが初期化される前に動きます。
そのため、コンストラクターから定数でないstaticフィールドを参照することは、JLSの8.9.2で禁止されています。
「コードから定数を引く表」をコンストラクターで作ろうとすると、コンパイルエラーになります（`compile-errors/StaticInConstructor.java`）。

<!-- output: no-static-in-constructor -->
```
verify/compile-errors/StaticInConstructor.java:12: error: illegal reference to static field from initializer
```
<!-- /output -->

<!-- claim: no-static-in-constructor -->

`InitOrder`のように、定数の後に書いたstatic初期化子で`values()`をたどって表を作れば、すべての定数ができてから表を作れます。

## 5. switchとenum

### しくみ

switch式（Java 14〜）で、enumの定数をすべて`case`に書けば`default`は要りません（JLSの14.11.1.1）。
1つでも書き忘れると、コンパイルエラーになります（`compile-errors/SwitchNotExhaustive.java`）。

<!-- output: switch-not-exhaustive -->
```
verify/compile-errors/SwitchNotExhaustive.java:6: error: the switch expression does not cover all possible input values
```
<!-- /output -->

<!-- claim: switch-not-exhaustive -->

ただし、網羅しているかどうかはコンパイルしたときのenumで判断されます。
JLSの13.4.26によると、enumに定数を足したり並べ替えたりしても、すでにコンパイルしたクラスとの互換性は壊れません。
一方で、網羅したswitchが、コンパイルしたときに知らなかった定数に出会うと、実行に失敗することがあります。

### 観察する（コンパイル結果）

`verify/EnumSwitch.java`の`japanese`は、同じファイルの`Color`について定数をすべて書いたswitch式です。

<!-- output: switch-same-nest -->
```
static java.lang.String japanese(EnumSwitch$Color);
0: aload_0
1: invokevirtual // Method EnumSwitch$Color.ordinal:()I
4: tableswitch { // 0 to 2
0: 42
1: 47
2: 52
default: 32
}
32: new // class java/lang/MatchException
35: dup
36: aconst_null
37: aconst_null
38: invokespecial // Method java/lang/MatchException."<init>":(Ljava/lang/String;Ljava/lang/Throwable;)V
41: athrow
```
<!-- /output -->

`ordinal()`の値で`tableswitch`を引き、どの番号にも当たらないとき（`default`）は`MatchException`を投げる枝に進みます。
JLSの15.6は、switch式でどの`case`も当てはまらないときに`MatchException`を投げると定めています。
<!-- claim: switch-same-nest -->

同じ`EnumSwitch.java`の`weekend`は、JDKの`java.time.DayOfWeek`についてのswitch式です。
別のクラスファイルのenumでは、javacは`ordinal()`を直接使わず、合成のクラス`EnumSwitch$1`に表を作ります。

<!-- output: switch-map -->
```
static final int[] $SwitchMap$java$time$DayOfWeek;
0: invokestatic // Method java/time/DayOfWeek.values:()[Ljava/time/DayOfWeek;
3: arraylength
4: newarray int
6: putstatic // Field $SwitchMap$java$time$DayOfWeek:[I
9: getstatic // Field $SwitchMap$java$time$DayOfWeek:[I
12: getstatic // Field java/time/DayOfWeek.SATURDAY:Ljava/time/DayOfWeek;
15: invokevirtual // Method java/time/DayOfWeek.ordinal:()I
18: iconst_1
19: iastore
20: goto 24
23: astore_0
Exception table:
from to target type
9 20 23 Class java/lang/NoSuchFieldError
24 35 38 Class java/lang/NoSuchFieldError
39 50 53 Class java/lang/NoSuchFieldError
54 65 68 Class java/lang/NoSuchFieldError
69 80 83 Class java/lang/NoSuchFieldError
84 96 99 Class java/lang/NoSuchFieldError
100 112 115 Class java/lang/NoSuchFieldError
```
<!-- /output -->

`$SwitchMap$java$time$DayOfWeek`は、`DayOfWeek`の`ordinal()`を添字にして、caseの番号（1から）を入れる`int`の配列です。
表は実行時に、そのとき読み込まれている`DayOfWeek`の`ordinal()`から作られます。
定数がなくなっていたとき（`NoSuchFieldError`）は、その欄を飛ばします。
<!-- claim: switch-map -->

<!-- output: switch-map-use -->
```
static boolean weekend(java.time.DayOfWeek);
0: getstatic // Field EnumSwitch$1.$SwitchMap$java$time$DayOfWeek:[I
3: aload_0
4: invokevirtual // Method java/time/DayOfWeek.ordinal:()I
7: iaload
8: tableswitch { // 1 to 7
```
<!-- /output -->

switchの側は、この表を`ordinal()`で引いた値で`tableswitch`します。
<!-- claim: switch-map-use -->

同じファイルにあるenumなら必ず一緒にコンパイルされるので、表を挟まずに`ordinal()`を使えます。
表を挟むか、どのクラスに作るかは、javacの実装の工夫で、仕様が決めていることではありません。

### 観察する（enumだけを変える）

`verify/SeparateCompilation.java`は、JDKのコンパイラーAPI（`javax.tools`）で、`Color`とそれを使う`Client`を別々にコンパイルします。
そのうえで、`Client`はそのままにして、並べ替えた`Color`、定数を足した`Color`と組み合わせて実行します。

<!-- output: separate-compilation -->
```
== 元のColor
  RED（ordinal=0） expr → 赤
  RED（ordinal=0） stmt → 赤
== 並べ替えたColor（BLUE, GREEN, RED）
  RED（ordinal=2） expr → 赤
  RED（ordinal=2） stmt → 赤
  BLUE（ordinal=0） expr → 青
  BLUE（ordinal=0） stmt → 青
== PURPLEを足したColor
  PURPLE（ordinal=3） expr → java.lang.MatchException
  PURPLE（ordinal=3） stmt → （どのcaseにも当たらない）
== PURPLEを足したColor（Clientは--release 17でコンパイル）
  PURPLE（ordinal=3） expr → java.lang.IncompatibleClassChangeError
  PURPLE（ordinal=3） stmt → （どのcaseにも当たらない）
```
<!-- /output -->

並べ替えても、`$SwitchMap`の表が実行時に名前から作り直されるので、switchの結果は変わりません（`ordinal`の値は変わっています）。
定数`PURPLE`を足すと、定数をすべて書いたswitch式は`MatchException`を投げます。
従来のswitch文は網羅を求められないので、どの`case`にも当たらずに素通りします。
`--release 17`でコンパイルした`Client`では、同じ状況で`IncompatibleClassChangeError`になります。
JEP 441（Java 21）で、enumのswitch式が投げる例外が`MatchException`に変わったためです。
<!-- claim: separate-compilation -->

ライブラリとして配るenumに定数を足すと、利用者のswitch式は、再コンパイルするまで実行時に失敗しうるということです。
再コンパイルすれば、書き漏らした定数はコンパイルエラーとして見つかります。

### sealedインターフェイスとenum

Java 21（JEP 441）からは、`case`に修飾した定数名（`Basic.CIRCLE`）を書けます。
sealedインターフェイスの実装にenumとrecordが混ざっていても、enumの定数とrecordのパターンを並べれば網羅でき、`default`は要りません（`verify/SealedWithEnum.java`）。

<!-- output: sealed-qualified -->
```
円
正方形
5角形
```
<!-- /output -->
<!-- claim: sealed-qualified -->

## 6. EnumSetとEnumMap

### しくみ

APIドキュメントによると、`EnumSet`は内部でビットベクトルとして表され、反復は定数の宣言の順です。
`null`の要素は入れられません。
<!-- claim: enumset-api -->

`EnumMap`は内部で配列として表され、反復はキーの宣言の順です。
<!-- claim: enummap-array -->

どちらも定数の`ordinal()`を、ビットの位置や配列の添字に使います。
`ordinal()`のAPIドキュメントが「`EnumSet`や`EnumMap`のようなデータ構造のためのもの」と書いているのは、このためです（7）。

### 観察する

`verify/EnumCollections.java`は、`EnumSet`の実装のクラスと、非公開のフィールドの値を表示します。

<!-- output: regular-jumbo -->
```
Day（7個）: java.util.RegularEnumSet
Small64（64個）: java.util.RegularEnumSet
Large65（65個）: java.util.JumboEnumSet
```
<!-- /output -->

`EnumSet`は抽象クラスで、`noneOf`や`of`が返すのはその実装です。
OpenJDKでは、定数が64個以下のenumには`RegularEnumSet`、65個以上には`JumboEnumSet`を使います。
どちらを使うかはAPIドキュメントに書かれていない実装の工夫で、使う側はどちらも`EnumSet`として扱えます。
<!-- claim: regular-jumbo -->

<!-- output: enumset-bits -->
```
EnumSet.of(SUN, SAT) = [SAT, SUN], elements = 1100000（2進数）
complementOf = [MON, TUE, WED, THU, FRI], elements = 11111（2進数）
Large65: elementsはlong[2]、[0]=1、[1]=1
```
<!-- /output -->

`RegularEnumSet`は1つの`long`（`elements`）のビットで要素を表し、定数の`ordinal()`番目のビットを立てます。
`SAT`（5）と`SUN`（6）の集合は2進数の`1100000`、その補集合は`11111`です。
`JumboEnumSet`は`long`の配列を使い、65個の定数には2つの`long`を使っています。
`contains`はビットを1つ調べるだけで、`addAll`や`retainAll`は`long`のビット演算になります。
<!-- claim: enumset-bits -->

<!-- output: enummap-array -->
```
EnumMap = {MON=会議, FRI=飲み会}
vals（長さ7）= [会議, null, null, null, 飲み会, null, null]
```
<!-- /output -->

`EnumMap`は、定数の数と同じ長さの配列`vals`を持ち、`ordinal()`の位置に値を入れます。
ハッシュ値の計算も、要素ごとのノードのオブジェクトもありません。

### 大きさ

`verify/EnumMemory.java`は、`Day`の7個の定数をすべて入れた集合とマップの大きさを、JOLで測ります。
どの集合も同じ定数と値を指すので、その分は除いています。
`EnumSet`と`EnumMap`が指すenumの`Class`と「全定数の配列」は、enumごとに1つを共有するので数えていません。

<!-- output: enum-memory -->
```
OFF  SZ               TYPE DESCRIPTION               VALUE
  0   8                    (object header: mark)     0x… (non-biasable; age: 0)
  8   4                    (object header: class)    0x…
 12   4    java.lang.Class EnumSet.elementType       (object)
 16   4   java.lang.Enum[] EnumSet.universe          [(object), (object), (object), (object), (object), (object), (object)]
 20   4                    (alignment/padding gap)
 24   8               long RegularEnumSet.elements   127
Instance size: 32 bytes
EnumSet: 32バイト
HashSet: 384バイト
EnumMap: 88バイト（本体40＋値の配列48）
HashMap: 368バイト
```
<!-- /output -->

この環境（64ビットのJDK 25、圧縮参照あり）では、`EnumSet`はヘッダーと参照2つと`long`1つの32バイトでした。
`HashSet`と`HashMap`は、ハッシュ表の配列と要素ごとのノードを持つので、それぞれ300バイト台になりました。
バイト数はJVMの設定（圧縮参照の有無など）で変わります。
<!-- claim: enum-memory -->

### 速さ

`EnumMap`のAPIドキュメントは、基本の操作は定数時間で、`HashMap`より速い可能性が高い（保証はしない）と書いています。
`EnumSet`については、intのビットフラグの代わりに使えるほど性能がよく、引数も`EnumSet`なら`containsAll`や`retainAll`のような一括の操作も速いと書いています。
<!-- claim: enum-speed-api -->

`src/`の`performance/PerformanceComparison.java`は、`EnumSet`と`HashSet`、`EnumMap`と`HashMap`、enumのswitchとif-elseなどの時間を`System.nanoTime()`で比べます。
少しウォームアップするだけの簡易な測定なので、結果は実行のたびに、また環境によって大きく変わります。
正確に比べるには、JMHなどのベンチマークツールを使います（付録B.12のプロジェクトでJMHを使っています）。

## 7. ordinalとcompareTo

### しくみ

`ordinal()`は宣言の位置（最初の定数が0）を返します。
APIドキュメントは、ほとんどのプログラマーには使い道がなく、`EnumSet`や`EnumMap`のような、enumを使う高度なデータ構造のためのものだと述べています。
<!-- claim: ordinal-api -->

`compareTo`の自然な順序は定数の宣言の順です。
APIが決めているのは結果の符号だけですが、OpenJDKの実装は`ordinal()`の差を返します（`verify/EnumAnatomy.java`）。

<!-- output: compareto-order -->
```
RED.compareTo(BLUE): -2
BLUE.compareTo(RED): 2
```
<!-- /output -->
<!-- claim: compareto-order -->

### 観察する

`verify/OrdinalPitfall.java`は、古い版の`SizeV1`の`ordinal()`と名前を保存し、先頭に定数を足した`SizeV2`で読み戻します（並べ替えの前と後を、2つのenumで表しています）。

<!-- output: ordinal-pitfall -->
```
保存した値: MEDIUM（ordinal=1）
ordinalで読み戻す: SMALL
名前で読み戻す: MEDIUM
コードで読み戻す: MEDIUM
EnumMap: {SMALL=300, LARGE=500}
```
<!-- /output -->

### 説明

`ordinal()`で保存した`MEDIUM`は、定数を足した版では`SMALL`として読み戻されます。
名前（`name()`と`valueOf`）や、明示的なフィールド（`code`）で保存すれば変わりません。
<!-- claim: ordinal-pitfall -->

データベースやファイルに保存する値、ほかのシステムに渡す値には、`ordinal()`を使わないようにします。
`compareTo`も宣言の順に依存するので、並べ替えると大小の比較の結果が変わります。
定数ごとの値の表は、`ordinal()`を添字にした配列を自分で作らず、`EnumMap`を使います。

## 8. enumを使った設計（src/のデモ）

### 状態機械（statemachine/）

注文の状態遷移をenumで実装します（`OrderState.java`）。

```java
OrderState state = OrderState.PENDING;
state = state.confirm();  // CONFIRMEDを返す
state = state.ship();     // SHIPPEDを返す
state = state.cancel();   // IllegalStateExceptionを投げる
```

既定の実装は例外を投げ、各状態は許される遷移のメソッドだけを定数の本体でオーバーライドします（2）。
不正な遷移は、コンパイル時ではなく実行時に`IllegalStateException`として検出されます。
不正な遷移をコンパイル時に防ぐには、状態ごとに別の型を用意し、その型に許される操作だけを持たせる設計（sealedインターフェイスなど）が必要です。

### ストラテジー（strategy/）

if-elseの連鎖を、定数ごとに異なるふるまいを持つenumに置き換えます（`PricingStrategy.java`）。

```java
PricingStrategy strategy = PricingStrategy.PROGRESSIVE;
BigDecimal tax = strategy.calculateTax(amount, context);
```

新しい計算方法は定数として足します。
定数の本体に処理をまとめると、5のswitchを各所に書く必要がなくなります。

### シングルトン（factory/）

`DatabaseConnection`は、定数が`INSTANCE`だけのenumです（3）。
実行にはH2データベースのJDBCドライバーが必要です（`pom.xml`に`runtime`の依存として入れています）。
デモのメニューからは呼び出していません。

### 権限（permission/）

`EnumSet`で権限の集合を表します（6）。

```java
EnumSet<Permission> userPerms = EnumSet.of(READ, WRITE);
boolean canDelete = userPerms.contains(DELETE);  // false
```

`Permission`は、`FULL_CRUD`や`ADMIN`のような複合の権限を、ビットマスクのフィールドで表しています。
`getIncludedPermissions()`は、複合の権限に含まれる単独の権限を返します。

<!-- output: permission-included -->
```
Permissions included in FULL_CRUD:
  - READ: Read access to resources
  - WRITE: Write/modify access to resources
  - EXECUTE: Execute operations
  - DELETE: Delete resources
Permissions included in ADMIN:
  - USER_MANAGE: Manage user accounts
  - ROLE_MANAGE: Manage roles and permissions
  - SYSTEM_CONFIG: Configure system settings
  - AUDIT_VIEW: View audit logs
```
<!-- /output -->
<!-- claim: permission-included -->

### 設定（configuration/）

設定キーをenumで定義し、既定値・型・検証の規則をキーに持たせます（`ConfigKey.java`）。

```java
int port = config.getServerPort();
boolean featureEnabled = config.isFeatureNewUiEnabled();
```

enumは型パラメーターを持てません（`enum Key<T>`は`enums cannot be generic`というコンパイルエラーになります）。
<!-- claim: no-generic-enum -->

そのため、汎用の`config.get(ConfigKey.SERVER_PORT)`は`<T> T get(ConfigKey key)`という形で、戻り値の型は受け取る変数から推論されるだけです。
`String s = config.get(ConfigKey.SERVER_PORT)`のような誤りもコンパイルでき、実行時に`ClassCastException`になります（`verify/GenericGetter.java`で同じ形を試せます）。
型を確実にするには、`getServerPort()`のような型付きのメソッドを使います。
<!-- claim: generic-getter-cce -->

## よくある誤解

- enumは名前の付いたint定数である
  - `Enum`を継承するクラスで、定数はそのオブジェクトである。番号（`ordinal`）はコンストラクターに渡される値の1つにすぎない（1）
  <!-- claim: enum-class-shape, static-init -->
- `values()`は定数の配列をそのまま返す
  - 呼ぶたびに配列を複製して返す（1）
  <!-- claim: values-clone -->
- 定数の`getClass()`はenumの型を返す
  - 定数ごとの本体を持つ定数では匿名クラスを返す。enumの型は`getDeclaringClass()`で得る（2）
  <!-- claim: getclass-vs-declaring -->
- enumのシングルトンなら、その状態もスレッドセーフである
  - 保証されるのは生成が1回だけでスレッドセーフなことまで。可変の状態は別に守る（3・4）
  <!-- claim: init-thread-safe -->
- switch式で定数をすべて書けば、実行時に漏れることはない
  - enumだけをあとで変えると、再コンパイルするまで`MatchException`が起きうる（5）
  <!-- claim: separate-compilation -->
- `EnumSet`には64個までしか入らない
  - 65個以上のenumでは`long`の配列を使う実装になる（6）
  <!-- claim: regular-jumbo -->
- `ordinal()`は定数の識別子として保存してよい
  - 定数を足したり並べ替えたりすると変わる。名前か明示的なフィールドを使う（7）
  <!-- claim: ordinal-pitfall -->

## 参考資料

- Java言語仕様（Java SE 25）
  - 8.9「Enum Classes」（8.9.1 Enum Constants、8.9.2 Enum Body Declarations、8.9.3 Enum Members）
  - 12.4「Initialization of Classes and Interfaces」
  - 13.4.26「Evolution of Enum Classes」
  - 14.11.1.1「Exhaustive Switch Blocks」、15.28「switch Expressions」
  - `https://docs.oracle.com/javase/specs/jls/se25/html/`
- Java仮想マシン仕様（Java SE 25）
  - 4.1「The ClassFile Structure」（`ACC_ENUM`）
  - `https://docs.oracle.com/javase/specs/jvms/se25/html/`
- APIドキュメント（Java SE 25）
  - `java.lang.Enum`、`java.util.EnumSet`、`java.util.EnumMap`、`java.lang.reflect.Constructor`
  - `https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/Enum.html`
- Java Object Serialization Specification 1.12「Serialization of Enum Constants」
  - `https://docs.oracle.com/en/java/javase/25/docs/specs/serialization/serial-arch.html`
- JEP 441: Pattern Matching for switch
  - `https://openjdk.org/jeps/441`
