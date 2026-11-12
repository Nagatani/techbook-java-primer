# 第12章 基本課題

## 学習目標

- `stream()`から始め、中間操作（`filter`・`map`・`sorted`・`limit`）と終端操作（`collect`）でデータを処理する
- `Collectors.groupingBy`でグループに分け、グループごとに集める（`summingDouble`など）
- `mapToDouble`で数のストリームにし、`average`の結果（`OptionalDouble`）から値を取り出す
- `partitioningBy`・`reduce`・`flatMap`・`anyMatch`・`allMatch`・`findFirst`を使い分ける

## 課題一覧

このフォルダーのJavaファイルは、`TODO`の箇所を埋めて完成させる雛形です。
課題1と課題2は雛形を完成させ、課題3は新しくファイルを作ります。
解答例は[solutions/](../solutions/)にあります。

### 課題1: 学生データの分析

**ファイル**: `Student.java`、`StudentAnalyzer.java`、`StreamDemo.java`（前半）

学生（名前・年齢・GPA・学部）のリストを、Stream APIで分析します。

**要求仕様**:

- `Student`のコンストラクター・getter・`toString`（`Alice（20歳、GPA 3.8、Computer Science）`の形）を書く
- `getAllNames`は`map`で名前のリストを、`filterByGPA`は`filter`でGPAが指定の値以上の学生を返す
- `groupByDepartment`は`Collectors.groupingBy`で学部ごとに分ける。表示の順序が決まるよう、学部名の順に並ぶ`TreeMap`で返す（`groupingBy(分け方, TreeMap::new, Collectors.toList())`）
- `calculateAverageGPA`は`mapToDouble`と`average`で平均を求める。学生がいないときは0.0を返す
- `getTopStudents`は、GPAの高い順に`sorted`で並べ、`limit`で上位n人に絞る
- どのメソッドも、元のリストを変えない
- `StreamDemo`の前半の`TODO`を埋めて、各メソッドを試す

**実行例**（`StreamDemo`の前半）:

```
=== 学生データ分析 ===
名前: [Alice, Bob, Charlie, David, Eve]
GPA 3.7以上:
  Alice（20歳、GPA 3.8、Computer Science）
  Charlie（21歳、GPA 3.9、Computer Science）
  Eve（20歳、GPA 3.7、Mathematics）
学部ごと:
  Computer Science: [Alice, Charlie]
  Mathematics: [Bob, Eve]
  Physics: [David]
平均GPA: 3.62
GPA上位3人:
  Charlie 3.9
  Alice 3.8
  Eve 3.7
```

### 課題2: 商品の売上の分析

**ファイル**: `Product.java`、`SalesAnalyzer.java`、`StreamDemo.java`（後半）

商品（名前・カテゴリ・価格・数量）のリストから、売上を集計します。

**要求仕様**:

- `Product`の`getTotalSales`は売上金額（価格×数量）を返す。`toString`は`Desk（Furniture、350.0ドル × 10個）`の形にする
- `calculateSalesByCategory`は、`groupingBy`の3つ目の引数に`Collectors.summingDouble(Product::getTotalSales)`を渡して、カテゴリごとの売上の合計を求める（カテゴリ名の順の`TreeMap`で返す）
- `filterByPriceRange`は価格が下限以上・上限以下の商品、`getOutOfStockProducts`は数量が0の商品を返す
- `getTopSellingProducts`は売上金額の多い順に上位n件を返す
- `getProductDescriptions`は`Laptop (Electronics)`の形の文字列のリストを返す
- `StreamDemo`の後半の`TODO`を埋めて、各メソッドを試す

**実行例**（`StreamDemo`の後半）:

```
=== 売上分析 ===
カテゴリごとの売上:
  Education: 0.0
  Electronics: 7250.0
  Furniture: 5750.0
価格が100〜500の商品:
  Desk（Furniture、350.0ドル × 10個）
  Chair（Furniture、150.0ドル × 15個）
在庫切れ: [Book（Education、30.0ドル × 0個）]
売上の上位2件:
  Laptop 6000.0
  Desk 3500.0
商品の説明: [Laptop (Electronics), Mouse (Electronics), Book (Education), Desk (Furniture), Chair (Furniture)]
```

### 課題3: 数のリストのパイプライン

**ファイル**: `NumberPipeline.java`（新しく作る）

1から10までの整数のリストに、いろいろな終端操作を使います。

**要求仕様**:

- `IntStream.rangeClosed(1, 10).boxed()`で、1から10までの`Integer`のリストを作る
- 偶数だけを2乗し、文字列に変えたリストを作る（`filter`→`map`→`map`）
- `Collectors.partitioningBy`で偶数と奇数に分け、それぞれの合計を`reduce(0, Integer::sum)`で求める
- `reduce`で、1から5までの積を求める（初期値を1にする）
- 入れ子のリストを`flatMap(List::stream)`で1つのリストにする
- `anyMatch`で7の倍数があるか、`allMatch`ですべて正の数かを調べる
- `filter`と`findFirst`で、ある数より大きい最初の数を`Optional`で求め、ないときは`orElse`で「なし」と表示する

**実行例**:

```
入力: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
偶数の2乗（文字列）: [4, 16, 36, 64, 100]
奇数: [1, 3, 5, 7, 9] 合計 25
偶数: [2, 4, 6, 8, 10] 合計 30
1から5の積: 120
入れ子のリスト [[1, 2], [3], [], [4, 5, 6]] をならす: [1, 2, 3, 4, 5, 6]
7の倍数がある？: true
すべて正の数？: true
8より大きい最初の数: 9
20より大きい最初の数: なし
```

## コンパイルと実行

```bash
cd exercises/chapter12/basic
javac *.java
java StreamDemo
```

課題3は、作ったファイルのあるフォルダーで`javac`と`java`を実行します。

## ヒント

- `collect(Collectors.toList())`の代わりに、Java 16以降は`toList()`とも書ける。ただし`toList()`が返すリストは変更できない
- 降順に並べるには`Comparator.comparingDouble(Student::getGpa).reversed()`を使う（第11章）
- `average()`は`OptionalDouble`を返す。要素が1つもないと平均がないため。`orElse(0.0)`で、ないときの値を決める
- `partitioningBy`の結果は、キーが`true`と`false`の2つだけの`Map<Boolean, List<...>>`になる
- ストリームは1回しか使えない。同じデータを別の処理に使うときは、もう一度`stream()`を呼ぶ

## 完了チェックリスト

- [ ] 課題1: `filter`・`map`・`sorted`・`limit`をつなげて、元のリストを変えずに結果を作れる
- [ ] 課題1: `groupingBy`で学部ごとに分けられる
- [ ] 課題2: `groupingBy`と`summingDouble`で、グループごとの合計を求められる
- [ ] 課題3: `reduce`・`flatMap`・`partitioningBy`の働きを説明できる
- [ ] 課題3: `findFirst`の結果の`Optional`から、値がないときも安全に値を取り出せる

**次のステップ**: 基本課題が終わったら、[advanced/](../advanced/)の発展課題に進みましょう。
