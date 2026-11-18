# 付録B.06 コレクションの内部実装

第9章で使った`HashMap`・`ArrayList`・`TreeMap`などが、内部でどのようなデータ構造を持ち、要素を足したときにどう形を変えるかを、JDKの内部を実際に観察しながら学ぶ節です。

## この節の読み方

この節の解説のうち、事実を述べた段落には`<!-- claim: … -->`という印を付けています（GitHubの画面では見えません）。
印の名前は同じディレクトリの`claims.yml`に対応し、そこにその主張の確かめ方（観察用のプログラムの出力、フラグの既定値、出典）を書いています。
リポジトリのルートで`npm run check:deep-dive -- b06`を実行すると、すべての主張をJDK 25で確かめ直せます。
「観察する」の欄に載せた出力は、この検査が実際に出したものです。

## ファイル構成

### 観察用のプログラム（verify/）

| ファイル | 観察すること |
|---|---|
| `HashMapTable.java` | `HashMap`の表の長さと閾値、バケットの決め方、反復の順序 |
| `Treeify.java` | 衝突が多いバケットが赤黒木に変わる時点と、連結リストに戻る時点 |
| `ArrayListGrowth.java` | `ArrayList`の内部の配列の伸び方 |
| `LinkedOrder.java` | `LinkedHashMap`の入れた順・使った順と、LRUキャッシュ |
| `TreeMapShape.java` | `TreeMap`の赤黒木の形と高さ、`compareTo`で決まる「同じキー」 |
| `Unmodifiable.java` | `List.of`などの実装クラス、`null`の扱い、ビューとコピー |
| `FailFast.java` | 反復の途中で変更したときの`ConcurrentModificationException` |
| `EntrySizes.java` | JOLで測る、1要素あたりのオブジェクトの大きさ |
| `compile-errors/NewHashMapBefore19.java` | `HashMap.newHashMap`が使える版（コンパイルできないことを確かめるプローブ） |

`verify/`のプログラムはパッケージを持たないので、`verify/`ディレクトリでそのままコンパイル・実行できます。
コレクションの非公開のフィールドを読むプログラムは、`--add-opens`を付けて実行します。

```bash
cd verify
javac HashMapTable.java
java --add-opens java.base/java.util=ALL-UNNAMED HashMapTable
```

`EntrySizes.java`はJOL（Java Object Layout）を使います。
Maven Centralから`org.openjdk.jol:jol-core:0.17`のJARを取得し、クラスパスに加えて実行します。

```bash
javac -cp jol-core-0.17.jar EntrySizes.java
java -Djdk.attach.allowAttachSelf=true -cp jol-core-0.17.jar:. EntrySizes
```

JOLは実行時に、エージェントの動的な読み込みや`sun.misc.Unsafe`についての警告を表示しますが、測定の結果には影響しません。

### 体験用のデモ（src/）

- `HashMapInternals.java`
  - しくみを見るために自作した簡略版の`SimpleHashMap`（`java.util.HashMap`ではない）で、負荷係数・`hashCode`の質・リサイズの影響を表示する
  - 木への変換は省いているので、すべてのキーが同じバケットに入ると連結リストが5000個つながる
- `TreeMapInternals.java`
  - 自作の`SimpleRedBlackTree`に1〜10を昇順に入れ、回転と色の変化を1件ずつ表示する
  - 平衡しない二分探索木との高さの比較、`TreeMap`と`HashMap`の時間の比較、メモリの比較をする

`SimpleRedBlackTree`は`TreeMap`と同じ手順で平衡を保つので、1〜10を入れ終えた木は、7で観察する本物の`TreeMap`の木と同じ形・同じ色になります。
<!-- claim: simple-rbt-same-shape -->

```bash
javac -d out src/main/java/com/example/collections/*.java
java -cp out com.example.collections.HashMapInternals
java -cp out com.example.collections.TreeMapInternals
```

## 1. 仕様が決めていることと、実装の工夫

コレクションのAPIドキュメントが決めているのは、操作の結果と、計算量のおおよその約束です。
たとえば`HashMap`の`get`と`put`は、ハッシュ関数が要素をバケットに散らばらせる限り、要素数によらない時間で済むと書かれています。
<!-- claim: hashmap-constant-time -->

`ArrayList`のAPIは、末尾への追加が「ならして定数時間」であることだけを決めており、配列をどれだけ伸ばすかは決めていません。
<!-- claim: arraylist-policy -->

この節で観察する表の長さ・閾値・木に変える個数・配列の伸び方は、OpenJDKの実装（`java.util`のクラスの非公開の定数とフィールド）です。
仕様ではないので将来の版で変わることがあり、プログラムがこれらの値に頼ってはいけません。
この節では、実装の値を観察で確かめ、仕様の約束はAPIドキュメントを出典にして、2つを分けて書きます。

## 2. HashMapの表の大きさ

### しくみ

`HashMap`は、内部に「バケット」の配列（表、`table`）を持ちます。
キーの`hashCode()`から表の位置を決め、そこに「キーと値の組」（ノード）を入れます。
要素が増えて表が混んでくると、表を大きく作り直します。

作り直す目安が負荷係数（load factor）です。
要素数が「表の長さ×負荷係数」（閾値、threshold）を超えると、表の長さを2倍にして、すべてのノードを新しい表に配置し直します。
<!-- claim: hashmap-resize -->

### 観察する

`verify/HashMapTable.java`は、`HashMap`の非公開のフィールド`table`と`threshold`をリフレクションで読みます。
既定のコンストラクターで作った直後は、まだ表がありません。

<!-- output: hashmap-lazy-table -->
```
作った直後: 表=null（まだ作っていない）
1件目を入れたあと: 表の長さ=16 閾値=12
```
<!-- /output -->

既定のコンストラクターは、最初の要素を入れるときに長さ16の表を作ります。
負荷係数の既定値は0.75なので、閾値は16×0.75=12です。
<!-- claim: hashmap-lazy-table -->

要素を1つずつ足して、表の長さが変わった時点を表示すると、次のようになります。

<!-- output: hashmap-resize -->
```
1件目: 表の長さ 0 -> 16（閾値 12）
13件目: 表の長さ 16 -> 32（閾値 24）
25件目: 表の長さ 32 -> 64（閾値 48）
49件目: 表の長さ 64 -> 128（閾値 96）
97件目: 表の長さ 128 -> 256（閾値 192）
193件目: 表の長さ 256 -> 512（閾値 384）
385件目: 表の長さ 512 -> 1024（閾値 768）
769件目: 表の長さ 1024 -> 2048（閾値 1536）
```
<!-- /output -->

要素数が閾値を超えた時点（13件目、25件目…）で、表の長さが2倍になっています。
<!-- claim: hashmap-resize -->

### 初期容量を指定する

入れる件数が先にわかっているなら、作り直しを避けられます。
ただし`new HashMap<>(100)`の100は「表の長さ」の指定で、「入れる件数」ではありません。

<!-- output: hashmap-presize -->
```
new HashMap<>(100): 1件目のあと 表の長さ=128 閾値=96
new HashMap<>(100): 100件入れたあと 表の長さ=256 作り直した回数=1
HashMap.newHashMap(100): 1件目のあと 表の長さ=256 閾値=192
HashMap.newHashMap(100): 100件入れたあと 表の長さ=256 作り直した回数=0
```
<!-- /output -->

表の長さは2のべき乗に切り上げられるので、`new HashMap<>(100)`の表は128になります。
閾値は128×0.75=96なので、100件を入れる途中の97件目で作り直しが起きます。
`HashMap.newHashMap(100)`は、100件を入れても閾値を超えない長さ（256）で表を作ります。
<!-- claim: hashmap-presize -->

`HashMap.newHashMap`はJava 19で加わったメソッドです。
`verify/compile-errors/NewHashMapBefore19.java`を`javac --release 18`でコンパイルすると、`cannot find symbol`のエラーになります。
<!-- claim: newhashmap-since-19 -->

### 負荷係数の意味

負荷係数を大きくすると表が小さく保たれ、メモリは減りますが、1つのバケットに入るノードが増えて探す手間が増えます。
小さくすると、その逆になります。
APIドキュメントは、既定の0.75を時間と空間の釣り合いのよい値として説明しています。
<!-- claim: hashmap-load-factor -->

自作の`SimpleHashMap`で負荷係数を変えたときのバケットの埋まり方は、`HashMapInternals`の「Load Factor Impact Demo」で見られます。

## 3. バケットの決め方

### しくみ

表の長さは常に2のべき乗なので、`HashMap`はハッシュ値の下位のビットだけを取り出して（`hash & (表の長さ - 1)`）バケットを決めます。
長さ16なら下位4ビットです。
下位のビットだけを使うと、上位のビットだけが違うキーがすべて同じバケットに集まってしまいます。
そこで`HashMap`は、`hashCode()`の上位16ビットを下位16ビットに排他的論理和で混ぜてから使います。
<!-- claim: hashmap-spread -->

```java
// java.util.HashMapのhashメソッドと同じ計算
static int hash(Object key) {
    int h;
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
```

### 観察する

`Integer`の`hashCode()`は値そのものです。
<!-- claim: integer-hashcode -->

`HashMapTable`は、上位16ビットだけが違う8つのキー（`0x10000`〜`0x80000`）を入れ、それぞれのノードが入ったバケットを表から探します。

<!-- output: hashmap-spread -->
```
キー 65536 (0x10000): バケット 1（hashCode & 15 は 0）
キー 131072 (0x20000): バケット 2（hashCode & 15 は 0）
キー 196608 (0x30000): バケット 3（hashCode & 15 は 0）
キー 262144 (0x40000): バケット 4（hashCode & 15 は 0）
キー 327680 (0x50000): バケット 5（hashCode & 15 は 0）
キー 393216 (0x60000): バケット 6（hashCode & 15 は 0）
キー 458752 (0x70000): バケット 7（hashCode & 15 は 0）
キー 524288 (0x80000): バケット 8（hashCode & 15 は 0）
```
<!-- /output -->

`hashCode()`の下位4ビットはどれも0ですが、上位のビットを混ぜたおかげで、8つのキーは別々のバケットに入っています。
<!-- claim: hashmap-spread -->

### 反復の順序

`HashMap`をたどると、表のバケットの順にノードが出てきます。

<!-- output: hashmap-iteration-order -->
```
入れた順: 100 3 17 50
取り出した順: 17(バケット1) 50(バケット2) 3(バケット3) 100(バケット4)
```
<!-- /output -->

取り出した順は、入れた順でもキーの大小の順でもありません。
APIドキュメントも、`HashMap`は順序を保証しないと書いています。
順序が必要なら、`LinkedHashMap`（入れた順）か`TreeMap`（キーの順）を使います。
<!-- claim: hashmap-iteration-order -->

### hashCodeが等しいキー

`String`の`hashCode()`の計算式はAPIドキュメントで決まっています（`s[0]*31^(n-1) + … + s[n-1]`）。
そのため、`"Aa"`と`"BB"`のように、違う文字列でも`hashCode()`が等しくなることがあります。

<!-- output: string-hash-collision -->
```
"Aa".hashCode()=2112 "BB".hashCode()=2112
Aa: バケット 0 / BB: バケット 0 / バケット0のノード数 2
get("BB")=2
```
<!-- /output -->

2つのノードは同じバケットにつながり、`get`はハッシュ値が等しいノードを`equals()`で比べて正しいほうを返します。
<!-- claim: string-hash-collision -->

`HashMap`は`null`のキーを1つだけ持てます。
`null`のハッシュ値は0として扱われるので、バケット0に入ります。
<!-- claim: hashmap-null-key -->

<!-- output: hashmap-null-key -->
```
nullのキー: バケット 0 get(null)=0
```
<!-- /output -->

## 4. 衝突が多いバケットの赤黒木

### しくみ

1つのバケットにノードが集まると、連結リストを先頭からたどる時間が延びます。
そこで`HashMap`は、1つのバケットのノードが増えすぎたら、そのバケットを赤黒木（`HashMap$TreeNode`）に変えます。
この改良は、JEP 180としてJava 8で入りました。
<!-- claim: jep180-java8 -->

OpenJDKの`HashMap`には、切り替えの目安として次の非公開の定数があります。

- `TREEIFY_THRESHOLD`（8）
  - 1つのバケットのノードがこの数を超えたら木にする
- `MIN_TREEIFY_CAPACITY`（64）
  - 表の長さがこれより短いうちは、木にせず表を広げる
- `UNTREEIFY_THRESHOLD`（6）
  - 表を広げて木が2つに分かれたとき、この数以下になった側は連結リストに戻す

これらの値の働きを、次の観察で確かめます。

### 観察する

`verify/Treeify.java`は、`hashCode()`がすべて42のキーを1つずつ足し、バケットの先頭のノードのクラスとノードの数を表示します。

<!-- output: treeify-small-table -->
```
8個目: 表の長さ16 バケット10=HashMap$Node×8
9個目: 表の長さ32 バケット10=HashMap$Node×9
10個目: 表の長さ64 バケット42=HashMap$Node×10
11個目: 表の長さ64 バケット42=HashMap$TreeNode×11
```
<!-- /output -->

既定の長さ16の表では、9個目で木にするかわりに表を32に広げ、10個目で64に広げます。
表の長さが64になったあとの11個目で、バケットが木（`TreeNode`）に変わります。
<!-- claim: treeify-small-table -->

最初から長さ64の表（`new HashMap<>(64)`）なら、8個を超えた9個目で木に変わります。
<!-- claim: treeify-large-table -->

<!-- output: treeify-large-table -->
```
8個目: 表の長さ64 バケット42=HashMap$Node×8
9個目: 表の長さ64 バケット42=HashMap$TreeNode×9
```
<!-- /output -->

### 連結リストに戻る時点

表を広げると、1つのバケットのノードは、元の位置と「元の位置＋元の長さ」の2つに分かれます。
`hashCode()`が0と64のキーは、長さ64の表では同じバケット0に入り、長さ128の表では0と64に分かれます。

<!-- output: untreeify-split -->
```
hashCode 0が5個・64が6個: 表の長さ64 バケット0=HashMap$TreeNode×11
表を広げたあと: 表の長さ128 バケット0=HashMap$Node×5 バケット64=HashMap$Node×6
hashCode 0が4個・64が7個: 表の長さ64 バケット0=HashMap$TreeNode×11
表を広げたあと: 表の長さ128 バケット0=HashMap$Node×4 バケット64=HashMap$TreeNode×7
```
<!-- /output -->

分かれた側のノードが6個以下なら連結リスト（`Node`）に戻り、7個なら木のままです。
<!-- claim: untreeify-split -->

削除で戻る時点は、個数では決まりません。
`HashMap`は削除のたびに木の形を調べ、小さくなりすぎた木を連結リストに戻します。
同じ12個の木から1つずつ消しても、消す順によって戻る時点が変わります。

<!-- output: untreeify-remove-shape -->
```
小さいキーから削除（残りの個数:形） 11:木 10:木 9:木 8:木 7:木 6:リスト 5:リスト 4:リスト 3:リスト 2:リスト 1:リスト
大きいキーから削除（残りの個数:形） 11:木 10:木 9:木 8:木 7:木 6:木 5:木 4:木 3:リスト 2:リスト 1:リスト
```
<!-- /output -->

<!-- claim: untreeify-remove-shape -->

### Comparableでないキーの木

赤黒木で探す範囲を半分ずつ絞るには、キーどうしの大小を比べなければなりません。
`HashMap`は、ハッシュ値が等しいキーの大小を、キーが`Comparable`を実装していれば`compareTo`で決めます。
`Comparable`でないキーは大小を決められないので、木の両側を探すことになります。
`Treeify`は、同じバケットにある1000個のキーを1つずつ`get`し、呼ばれた`equals`の回数を数えます。

<!-- output: treebin-comparable -->
```
Comparableでないキー: getあたりのequalsの回数 平均 501.5
Comparableなキー: getあたりのequalsの回数 平均 10.4
```
<!-- /output -->

`Comparable`なキーでは約10回（1000の対数程度）で見つかりますが、`Comparable`でないキーでは平均で約500回、つまり連結リストをたどるのと同じ程度の手間がかかります。
木に変える改良は、すべての衝突を救うわけではありません。
`hashCode()`をきちんと実装し、同じハッシュ値のキーを作らないことが先です。
<!-- claim: treebin-comparable -->

## 5. ArrayListの配列の伸び方

### しくみ

`ArrayList`は要素を配列（`elementData`）に入れます。
配列の長さ（容量）が足りなくなると、より長い配列を作って要素をコピーします。
コピーには要素数に比例する時間がかかりますが、たまにしか起きないので、末尾への追加はならして定数時間になります。

### 観察する

`verify/ArrayListGrowth.java`は、要素を足しながら`elementData`の長さを読みます。

<!-- output: arraylist-growth -->
```
new ArrayList<>() の直後: 配列の長さ=0
new ArrayList<>() に100件足したときの配列の長さ: 10 15 22 33 49 73 109
new ArrayList<>(0) に10件足したときの配列の長さ: 1 2 3 4 6 9 13
new ArrayList<>(100) の直後: 配列の長さ=100
11件入れたあと: 配列の長さ=15
trimToSize()のあと: 配列の長さ=11
clear()のあと: size=0 配列の長さ=11
```
<!-- /output -->

APIドキュメントは`new ArrayList<>()`を「初期容量10の空のリストを作る」と説明していますが、実装は最初の`add`まで長さ0の配列を持ち、最初の`add`で長さ10にします。
その後は、元の長さに半分を足した長さ（約1.5倍）に広げます。
`new ArrayList<>(0)`から始めると、1・2・3・4・6・9と少しずつ広がります。
`trimToSize()`は配列を要素数まで縮めますが、`clear()`は要素を消すだけで配列を縮めません。
<!-- claim: arraylist-growth -->

中間への挿入や先頭の削除では、後ろの要素をすべて1つずつずらすので、要素数に比例する時間がかかります。
第9章の`ArrayList`と`LinkedList`の比較は、この配列の構造から来ています。
<!-- claim: arraylist-policy -->

## 6. LinkedHashMapの2つの順序

`LinkedHashMap`は`HashMap`の表に加えて、すべてのノードを双方向の連結リストでつなぎ、反復の順序を保ちます（ノードの中身は10で観察します）。
並び順は、既定では入れた順で、コンストラクターの3つめの引数に`true`を渡すと使った順（`get`などで使ったノードを末尾へ移す）になります。
<!-- claim: entry-fields, linkedhashmap-orders -->

<!-- output: linkedhashmap-orders -->
```
入れた順（既定）: [A, B, C]
使った順（get("A")のあと）: [B, C, A]
LRUキャッシュ（上限3、Aを使ってからDを入れる）: [C, A, D]
```
<!-- /output -->

入れた順の`LinkedHashMap`では、すでにあるキーに入れ直しても（`put("B", 20)`）順は変わりません。
使った順の`LinkedHashMap`で`removeEldestEntry`をオーバーライドすると、上限を超えたときにいちばん長く使われていない組を捨てるLRUキャッシュになります。
<!-- claim: linkedhashmap-orders -->

## 7. TreeMapと赤黒木

### しくみ

`TreeMap`は、赤黒木によるキーの順のマップです。
APIドキュメントは、`containsKey`・`get`・`put`・`remove`が要素数の対数に比例する時間で済むことを保証しています。
<!-- claim: treemap-rbt -->

赤黒木は、各ノードに赤か黒の色を付けた二分探索木で、次の性質を保ちます。

1. 各ノードは赤か黒である
2. 根は黒である
3. 葉（子のない位置、NIL）は黒とみなす
4. 赤のノードの子は黒である（赤が2つ続かない）
5. どのノードから葉までのどの経路にも、同じ数の黒のノードがある

4と5から、根から葉までのいちばん長い経路は、いちばん短い経路の2倍を超えません。
そのため、要素数nの赤黒木の高さは2×log2(n+1)以下に収まります。
要素を入れて性質が崩れたら、色の塗り替えと回転（親子を入れ替える操作）で直します。
<!-- claim: treemap-rbt, treemap-height -->

### 観察する

`verify/TreeMapShape.java`は、`TreeMap`の非公開のフィールド`root`と、各ノード（`TreeMap$Entry`）の`left`・`right`・`color`を読みます。
1〜10を昇順に入れたときの木は次のとおりです（`L:`は左の子、`R:`は右の子）。

<!-- output: treemap-shape-small -->
```
4(黒)
├─L:2(黒)
│ ├─L:1(黒)
│ └─R:3(黒)
└─R:6(黒)
· ├─L:5(黒)
· └─R:8(赤)
· · ├─L:7(黒)
· · └─R:9(黒)
· · · └─R:10(赤)
```
<!-- /output -->

ただの二分探索木なら、昇順に入れると右に一直線に伸びて高さ10になります。
`TreeMap`では回転によって4が根になり、高さは5に収まっています。
<!-- claim: treemap-shape-small -->

件数を増やして、木の高さと赤黒木の性質を調べます。

<!-- output: treemap-height -->
```
n=1000: 高さ=17（log2(n+1)=10.0、2×log2(n+1)=19.9） 根は黒=true 赤が2つ続く箇所=0 黒の高さがそろう=true
n=1000000: 高さ=37（log2(n+1)=19.9、2×log2(n+1)=39.9） 根は黒=true 赤が2つ続く箇所=0 黒の高さがそろう=true
```
<!-- /output -->

100万件を昇順に入れても、高さは37で、2×log2(n+1)の39.9を超えていません。
1件を探すのに、たかだか37個のノードを比べれば済みます。
<!-- claim: treemap-height -->

### 「同じキー」を決めるのはcompareTo

`TreeMap`と`TreeSet`は、2つのキーが同じかどうかを`equals`ではなく`compareTo`（または`Comparator`）で決めます。

<!-- output: treemap-compareto -->
```
1.0と1.00: equals=false compareTo=0 HashSetの要素数=2 TreeSetの要素数=1
CASE_INSENSITIVE_ORDERのTreeSet: [java]
```
<!-- /output -->

`BigDecimal`の1.0と1.00は、`equals`では違う値（桁数が違う）ですが、`compareTo`では等しい値です。
そのため`HashSet`には2つ入り、`TreeSet`には1つしか入りません。
大文字と小文字を区別しない`Comparator`の`TreeSet`では、`"Java"`と`"JAVA"`は最初の`"java"`と同じ要素とみなされます。
APIドキュメントは、順序づけが`equals`と食い違わない（consistent with equals）ことを求めています。
<!-- claim: treemap-compareto -->

### subMapはビュー

`subMap`・`headMap`・`tailMap`は、元の木の一部を見せるビューで、コピーではありません。

<!-- output: treemap-submap-view -->
```
subMap(20〜40): [20, 30, 40]
元に25を足し、subMapから30を消したあと: 元=[10, 20, 25, 40, 50] subMap=[20, 25, 40]
subMapの範囲外のput(45): IllegalArgumentException
```
<!-- /output -->

元に足した25はsubMapに表れ、subMapから消した30は元からも消えています。
範囲外のキーをsubMapに入れようとすると、`IllegalArgumentException`になります。
<!-- claim: treemap-submap-view -->

## 8. 変更できないコレクション

### 実装クラス

`List.of`などのファクトリメソッドは、`java.util.ImmutableCollections`の入れ子のクラスを返します。

<!-- output: immutable-classes -->
```
List.of() -> java.util.ImmutableCollections$ListN
List.of(1) -> java.util.ImmutableCollections$List12
List.of(1, 2) -> java.util.ImmutableCollections$List12
List.of(1, 2, 3) -> java.util.ImmutableCollections$ListN
Set.of(1, 2) -> java.util.ImmutableCollections$Set12
Set.of(1, 2, 3) -> java.util.ImmutableCollections$SetN
Map.of(1, 2) -> java.util.ImmutableCollections$Map1
Map.of(1, 2, 3, 4) -> java.util.ImmutableCollections$MapN
Stream.toList() -> java.util.ImmutableCollections$ListN
Arrays.asList(1, 2, 3) -> java.util.Arrays$ArrayList
Collections.unmodifiableList -> java.util.Collections$UnmodifiableRandomAccessList
```
<!-- /output -->

`Set.of`・`Map.of`も、要素の数によって実装クラスを使い分けています。
<!-- claim: immutable-classes -->

要素が1つか2つのリスト（`List12`）は要素を2つのフィールドで持ち、それ以外（`ListN`）は配列で持ちます。

<!-- output: immutable-fields -->
```
List12のフィールド: Object e0; Object e1;
ListNのフィールド: Object[] elements; boolean allowNulls;
```
<!-- /output -->

`ListN`は`null`を許すかどうかのフラグ（`allowNulls`）を持っており、次で見るように、作り方によって`null`の扱いが変わります。
<!-- claim: immutable-fields -->

### nullと重複

`List.of`などで作ったリストは`null`を要素にできません。
`null`を探す`contains(null)`も、`false`を返すのではなく`NullPointerException`になります。
一方、`Stream.toList()`が返すリストは同じ`ListN`ですが、`null`を含められます。
<!-- claim: immutable-null -->

<!-- output: immutable-null -->
```
List.of(1, null) -> NullPointerException
List.of(1, 2).contains(null) -> NullPointerException
Stream.of(1, null).toList() -> [1, null]
new ArrayList<>(List.of(1)).contains(null) -> false
ConcurrentHashMapにnullの値をput -> NullPointerException
```
<!-- /output -->

スレッドから同時に使うための`ConcurrentHashMap`も、`HashMap`と違って`null`をキーにも値にもできません。
<!-- claim: chm-null -->

`Set.of`は重複した要素を渡すと`IllegalArgumentException`になります。
重複を含みうるコレクションから作るときは、重複を1つにまとめる`Set.copyOf`を使います。
<!-- claim: set-of-duplicates -->

`Set.of`・`Map.of`の反復の順序は決まっておらず、変わることがあります。
<!-- claim: set-of-order -->

### ビューとコピー

<!-- output: view-vs-copy -->
```
元にBを足したあと: unmodifiableList=[A, B] List.copyOf=[A]
変更できないリストのList.copyOfは同じオブジェクト: true
```
<!-- /output -->

`Collections.unmodifiableList`は元のリストのビューで、ビューを通した変更はできませんが、元を変更すると見える中身も変わります。
`List.copyOf`はコピーを作るので、元の変更の影響を受けません。
`List.copyOf`に変更できないリストを渡すと、コピーせずに同じオブジェクトを返します。
<!-- claim: view-vs-copy -->

`Arrays.asList`が返すリスト（`Arrays$ArrayList`）は、元の配列と中身を共有します。
`set`で置き換えると元の配列も変わり、`add`のように大きさを変える操作は`UnsupportedOperationException`になります。
<!-- claim: arrays-aslist -->

## 9. fail-fastなイテレーター

`ArrayList`や`HashMap`は、要素の追加・削除のような構造の変更の回数を数えています（`ArrayList`では`AbstractList`から受け継いだ`modCount`フィールド）。
イテレーターは作ったときの回数を覚えておき、`next()`のたびに比べて、違っていれば`ConcurrentModificationException`を投げます（fail-fast）。
<!-- claim: modcount -->

<!-- output: failfast -->
```
拡張for文でBをremove: ConcurrentModificationException
拡張for文でCをremove: 例外なし 結果=[A, B, D]
Iterator.removeでBを消す: 結果=[A, C, D]
removeIfでBを消す: 結果=[A, C, D]
```
<!-- /output -->

拡張for文の本体で`list.remove`を呼ぶと、スレッドが1つしかなくても例外になります。
ただし、最後から2番目の要素（C）を消したときは、`hasNext()`が要素数だけを見て`false`を返すので、例外にならずにループが終わります。
最後の要素（D）は調べられないまま残るので、例外が出ないことは正しく動いたことを意味しません。
反復しながら消すときは、`Iterator.remove`か`removeIf`を使います。
<!-- claim: failfast -->

APIドキュメントは、fail-fastな振る舞いを「最善の努力」としており、例外が必ず出るとは約束していません。
この例外はバグを見つけるためのもので、プログラムの正しさを例外に頼ってはいけません。
<!-- claim: failfast -->

<!-- output: failfast-map -->
```
HashMapのkeySetをたどりながらput: ConcurrentModificationException
すでにあるキーの値だけを置き換える: 例外なし 結果={A=10, B=10}
```
<!-- /output -->

`HashMap`のキーをたどりながら新しいキーを入れると例外になります。
すでにあるキーの値を置き換えるだけなら、構造は変わらないので例外になりません。
<!-- claim: failfast-map -->

## 10. メモリの使い方

### 観察する

`verify/EntrySizes.java`は、JOLで`HashMap$Node`のフィールドの配置を表示します。

<!-- output: node-layout -->
```
OFF SZ TYPE DESCRIPTION VALUE
0 8 (object header: mark) N/A
8 4 (object header: class) N/A
12 4 int Node.hash N/A
16 4 java.lang.Object Node.key N/A
20 4 java.lang.Object Node.value N/A
24 4 java.util.HashMap.Node Node.next N/A
28 4 (object alignment gap)
Instance size: 32 bytes
```
<!-- /output -->

既定の設定のJDK 25では、オブジェクトの先頭に12バイトのヘッダー（マーク8バイトとクラス4バイト）があります。
続いて`hash`の`int`と、`key`・`value`・`next`の参照（それぞれ4バイト）が並び、合わせて28バイトです。
オブジェクトの大きさは8バイトの倍数にそろえるので、`HashMap$Node`は1つ32バイトになります。
<!-- claim: node-layout -->

ほかのコレクションの1要素ごとのオブジェクトは次のとおりです。

<!-- output: entry-sizes -->
```
HashMap$Node: 32バイト（ヘッダー12バイト）
HashMap$TreeNode: 56バイト（ヘッダー12バイト）
LinkedHashMap$Entry: 40バイト（ヘッダー12バイト）
TreeMap$Entry: 40バイト（ヘッダー12バイト）
LinkedList$Node: 24バイト（ヘッダー12バイト）
```
<!-- /output -->

木になったバケットの`TreeNode`は56バイトあり、ふつうのノードの2倍近い大きさです。
<!-- claim: entry-sizes -->

大きさの違いは、フィールドの数の違いです（スーパークラスから受け継いだフィールドを含めて並べています）。

<!-- output: entry-fields -->
```
HashMap$Node: hash key value next
HashMap$TreeNode: hash key value next before after parent left right prev red
LinkedHashMap$Entry: hash key value next before after
TreeMap$Entry: key value left right parent color
LinkedList$Node: item next prev
```
<!-- /output -->

`LinkedHashMap$Entry`は`HashMap$Node`に前後のノードへの参照（`before`・`after`）を足したもので、これがすべてのノードをつなぐ双方向の連結リストになります。
`TreeMap$Entry`はキー・値・左右と親への参照・色を持ちます。
`TreeNode`は`LinkedHashMap$Entry`を継承し、さらに木のための参照を持ちます。
<!-- claim: entry-fields -->

`Integer`を1000件入れたコレクション全体の大きさから、キーと値の`Integer`の分を引くと、次のようになります。

<!-- output: collection-totals -->
```
HashMap: 40256バイト（1件あたり約40バイト）
LinkedHashMap: 48272バイト（1件あたり約48バイト）
TreeMap: 40048バイト（1件あたり約40バイト）
ArrayList: 4976バイト（1件あたり約5バイト）
LinkedList: 24032バイト（1件あたり約24バイト）
```
<!-- /output -->

`HashMap`は、ノードの32バイトに加えて、表の配列の分を使います。
1000件を入れると、769件目で表の長さが2048になっており、その参照の配列が1件あたり約8バイトになります。
`TreeMap`は表を持たない代わりにノードが大きく、1件あたりの大きさは`HashMap`とほぼ同じです。
`ArrayList`は参照（4バイト）を並べた配列だけなので、伸ばしたときの余りを含めても1件あたり約5バイトで済みます。
<!-- claim: collection-totals, hashmap-resize -->

### コンパクトなオブジェクトヘッダー

JDK 25では、ヘッダーを8バイトに縮める「コンパクトなオブジェクトヘッダー」が製品の機能になりました（JEP 519）。
既定では無効で、`-XX:+UseCompactObjectHeaders`を付けると有効になります。
<!-- claim: compact-headers-default -->

<!-- output: compact-headers -->
```
HashMap$Node: 24バイト（ヘッダー8バイト）
HashMap$TreeNode: 56バイト（ヘッダー8バイト）
LinkedHashMap$Entry: 32バイト（ヘッダー8バイト）
TreeMap$Entry: 32バイト（ヘッダー8バイト）
LinkedList$Node: 24バイト（ヘッダー8バイト）
HashMap: 32248バイト（1件あたり約32バイト）
LinkedHashMap: 40264バイト（1件あたり約40バイト）
TreeMap: 32048バイト（1件あたり約32バイト）
ArrayList: 4976バイト（1件あたり約5バイト）
LinkedList: 24024バイト（1件あたり約24バイト）
```
<!-- /output -->

ヘッダーが4バイト減ると、`HashMap$Node`は24バイトになり、1000件の`HashMap`は1件あたり約32バイトになります。
`TreeNode`や`LinkedList$Node`のように、8バイトの倍数にそろえる都合で大きさが変わらないものもあります。
<!-- claim: compact-headers -->

`TreeMapInternals`の「Memory Usage Comparison」は、`Runtime`の空きメモリの差から大きさを見積もります。
ガベージコレクションの動きで値がぶれるので、目安として見てください。
正確な大きさは、`EntrySizes`のようにJOLで測ります。

## 11. 時間を測るデモ

`HashMapInternals`と`TreeMapInternals`は、処理時間を表示します。
数回の空回し（ウォームアップ）のあとに`System.nanoTime()`で測っていますが、結果はJVM・CPU・負荷などの環境で変わり、実行するたびにも変わります。
このデモは傾向を体験するためのもので、正確に比べるにはJMH（Java Microbenchmark Harness）を使います。
時間以外の値（衝突の統計、木の高さ、回転の様子など）は、実行するたびに同じ結果になります。

## よくある誤解

- `HashMap`の`get`はいつでも要素数によらない時間で済む
  - 同じハッシュ値のキーが集まると、1つのバケットをたどる時間が延びる。木に変わっても、キーが`Comparable`でなければ連結リストと同じ程度の手間がかかる（4）
  <!-- claim: treebin-comparable -->
- `new HashMap<>(100)`なら100件を入れても表を作り直さない
  - 100は表の長さの指定で、閾値は96になる。件数で指定するなら`HashMap.newHashMap(100)`を使う（2）
  <!-- claim: hashmap-presize -->
- `Integer`のキーなら`HashMap`は小さい順に並ぶ
  - バケットの順に出てくるだけで、順序は保証されない（3）
  <!-- claim: hashmap-iteration-order -->
- 木になったバケットは、6個以下に減ると必ず連結リストに戻る
  - 6個が目安になるのは表を広げて木が分かれるときで、削除では木の形で決まる（4）
  <!-- claim: untreeify-split, untreeify-remove-shape -->
- `ConcurrentModificationException`は、複数のスレッドから使ったときだけ起きる
  - 1つのスレッドでも、反復の途中でコレクションを変更すれば起きる。逆に、例外が出なくても正しいとは限らない（9）
  <!-- claim: failfast -->
- `TreeSet`は`equals`で重複を判断する
  - `compareTo`（または`Comparator`）で判断する（7）
  <!-- claim: treemap-compareto -->
- `Collections.unmodifiableList`で包めば、中身は変わらない
  - ビューなので、元のリストを変更すれば中身も変わる。変わらないものが要るなら`List.copyOf`を使う（8）
  <!-- claim: view-vs-copy -->

## 参考資料

- Java SE 25のAPIドキュメント（`java.base`モジュールの`java.util`パッケージ）
  - `HashMap`・`LinkedHashMap`・`TreeMap`・`ArrayList`・`List`・`Set`・`Collections`・`ConcurrentModificationException`
  - `https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/package-summary.html`
- Java SE 25のAPIドキュメント（`ConcurrentHashMap`）
  - `https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/ConcurrentHashMap.html`
- Java SE 25のAPIドキュメント（`String.hashCode`）
  - `https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/String.html`
- JEP 180: Handle Frequent HashMap Collisions with Balanced Trees
  - `https://openjdk.org/jeps/180`
- JEP 519: Compact Object Headers
  - `https://openjdk.org/jeps/519`
