# 第9章 基本課題

## 学習目標

- List・Set・Mapの特性を理解し、目的に合わせて使い分ける
- ArrayListとHashMapを組み合わせて、順序と検索の速さを両立させる
- HashSet・LinkedHashSet・TreeSet、HashMap・LinkedHashMap・TreeMapの順序の違いを確かめる
- 要素の比べ方（equals・hashCode）がコレクションの動きに与える影響を理解する

## 課題一覧

このフォルダーのJavaファイルは、`TODO`の箇所を埋めて完成させる雛形です。
課題1と課題2は雛形を完成させ、課題3と課題4は新しくファイルを作ります。
解答例は[solutions/](../solutions/)にあります。

### 課題1: 学生管理（ListとMapの組み合わせ）

**ファイル**: `Student.java`、`StudentManager.java`、`StudentManagerDemo.java`

学生の一覧をArrayListで登録順に持ち、同じ学生をHashMapで学生IDから引けるようにします。

**要求仕様**:

- `Student`のコンストラクター、`toString`（`学生ID: S001, 名前: 山田太郎, 学年: 2, 成績: 85.5`の形）、学生IDで比べる`equals`と`hashCode`を書く
- `StudentManager`は、追加・IDでの検索・全員の表示・成績の高い順の表示・学年での絞り込み・平均・削除を、リストとマップの両方を正しく保つように書く
- 登録済みのIDを追加しようとしたら、追加せずにメッセージを出す
- 成績順の表示では、元のリストの順序を変えないよう、コピーを並べ替える
- `StudentManagerDemo`の`TODO`を埋めて、各機能を試す

**実行例**（学生を4人と、IDが重複する1人を追加した場合）:

```
=== 学生管理システムデモ ===

登録済みのIDです: S001

--- 全学生一覧 ---
=== 学生一覧 ===
学生ID: S001, 名前: 山田太郎, 学年: 2, 成績: 85.5
学生ID: S002, 名前: 鈴木花子, 学年: 1, 成績: 92.0
学生ID: S003, 名前: 佐藤次郎, 学年: 2, 成績: 78.0
学生ID: S004, 名前: 高橋美咲, 学年: 3, 成績: 88.5

--- 学生検索 ---
S001: 学生ID: S001, 名前: 山田太郎, 学年: 2, 成績: 85.5
S009: null

--- 成績順表示 ---
=== 成績順学生一覧 ===
学生ID: S002, 名前: 鈴木花子, 学年: 1, 成績: 92.0
学生ID: S004, 名前: 高橋美咲, 学年: 3, 成績: 88.5
学生ID: S001, 名前: 山田太郎, 学年: 2, 成績: 85.5
学生ID: S003, 名前: 佐藤次郎, 学年: 2, 成績: 78.0

--- 2年生の学生 ---
山田太郎
佐藤次郎

--- 平均成績 ---
平均: 86.00

--- 学生削除 ---
S003を削除: true
S003をもう一度削除: false
残りの人数: 3
```

### 課題2: ショッピングカート（Mapでの数量管理）

**ファイル**: `Product.java`、`CartItem.java`、`ShoppingCart.java`、`ShoppingCartDemo.java`

商品IDをキーにしたMapで、カートの中の商品と数量を管理します。

**要求仕様**:

- `Product`は在庫を持ち、`reduceStock`は在庫が足りないとき`false`を返す
- `CartItem`は小計（単価×数量）を計算する
- `ShoppingCart`の`addItem`は、同じ商品がすでにあれば数量を増やす。カートの数量の合計が在庫を超えるときは追加せず`false`を返す
- `updateQuantity`は数量を変え、0を指定したら商品をカートから除く
- `displayCart`は商品を入れた順に並べ、最後に合計金額を出す（`items`を`LinkedHashMap`で作ると、入れた順が保たれる）
- `getTotalItemCount`は商品の種類の数ではなく、数量の合計を返す

**実行例**:

```
=== ショッピングカートシステムデモ ===

--- 商品在庫 ---
P001: ノートパソコン（80000円、在庫5個）
P002: マウス（2000円、在庫10個）
P003: キーボード（5000円、在庫8個）

--- カート作成 ---
カートは空？: true

--- 商品追加 ---
ノートパソコン×1: true
マウス×2: true

--- カート内容 ---
=== ショッピングカート (山田太郎) ===
ノートパソコン 80000円 × 1 = 80000円
マウス 2000円 × 2 = 4000円
合計: 84000円

--- 同じ商品を追加 ---
=== ショッピングカート (山田太郎) ===
ノートパソコン 80000円 × 1 = 80000円
マウス 2000円 × 5 = 10000円
合計: 90000円

--- 数量更新 ---
=== ショッピングカート (山田太郎) ===
ノートパソコン 80000円 × 1 = 80000円
マウス 2000円 × 1 = 2000円
合計: 82000円

--- 在庫チェック ---
ノートパソコン×10: false

--- 商品削除 ---
=== ショッピングカート (山田太郎) ===
マウス 2000円 × 1 = 2000円
キーボード 5000円 × 1 = 5000円
合計: 7000円

--- カート統計 ---
総アイテム数: 2
合計金額: 7000円

--- カートクリア ---
カートは空？: true
```

### 課題3: 単語カウンター（Map）

**ファイル**: `WordCounter.java`（新しく作る）

英文の単語ごとの出現回数を数えます。

**要求仕様**:

- 文を空白で区切り、ピリオドを除いて小文字にそろえる
- `HashMap<String, Integer>`で回数を数える（`getOrDefault`を使う）
- `TreeMap`に移して、アルファベット順に表示する
- 最も多く出た単語（同じ回数なら全部）、総単語数、異なる単語の数を表示する

**実行例**:

```
入力: Java is fun. Java is powerful. Programming with Java is exciting.

アルファベット順（TreeMap）:
exciting: 1回
fun: 1回
is: 3回
java: 3回
powerful: 1回
programming: 1回
with: 1回

出現回数の多い単語:
is（3回）
java（3回）

総単語数: 11
異なる単語の数: 7
```

### 課題4: 重複の除去と集合演算（Set）

**ファイル**: `SetOperations.java`（新しく作る）

Setで重複を除き、2つの集合の和・積・差を求めます。

**要求仕様**:

- 重複のあるリストを`LinkedHashSet`と`TreeSet`に入れ、順序の違いを確かめる
- 和集合は`addAll`、積集合は`retainAll`、差集合は`removeAll`で求める。元の集合を壊さないよう、コピーに対して操作する
- 結果の順序が実行のたびに同じになるよう、表示する集合は`TreeSet`にする

**実行例**:

```
元のリスト: [apple, banana, apple, orange, banana, grape]
LinkedHashSet（最初に出た順）: [apple, banana, orange, grape]
TreeSet（辞書順）: [apple, banana, grape, orange]

A: [C, Java, Python]
B: [Java, JavaScript, Python]
和集合: [C, Java, JavaScript, Python]
積集合: [Java, Python]
差集合（A-B）: [C]
差集合（B-A）: [JavaScript]
```

## コンパイルと実行

課題1と課題2の雛形は`package chapter09.basic;`を宣言しているので、`exercises/`フォルダーでコンパイルします。

```bash
cd exercises
javac -d out chapter09/basic/*.java
java -cp out chapter09.basic.StudentManagerDemo
java -cp out chapter09.basic.ShoppingCartDemo
```

課題3と課題4は、作ったファイルのあるフォルダーで`javac`と`java`を実行します。

## ヒント

- 成績の高い順に並べるには、第8章で学んだ匿名クラスで`Comparator<Student>`を書き、`list.sort(...)`に渡す（`Double.compare(b.getScore(), a.getScore())`で降順になる）。ラムダ式や`Comparator.comparing`での書き方は第11章で学ぶ
- 削除では、マップから取り出した学生をリストからも除く。`equals`を学生IDで比べるように書いてあれば、`list.remove(student)`で同じ学生が除かれる
- HashMap・HashSetの順序は決まっていない。表示の順序を決めたいときはLinkedHashMap（入れた順）かTreeMap（キーの順）を選ぶ

## 完了チェックリスト

- [ ] 課題1: リストとマップの両方を矛盾なく更新できている
- [ ] 課題1: 元のリストを変えずに成績順に表示できている
- [ ] 課題2: 同じ商品の追加で数量が増え、在庫を超える追加を断れる
- [ ] 課題3: Mapで単語の回数を数え、TreeMapで並べられる
- [ ] 課題4: 集合演算を、元の集合を壊さずに行える
- [ ] HashMap・LinkedHashMap・TreeMapの順序の違いを説明できる

**次のステップ**: 基本課題が終わったら、[advanced/](../advanced/)の発展課題に進みましょう。
