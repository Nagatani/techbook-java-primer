# 第6章 基本課題

## 学習目標

- `final`のフィールドだけを持ち、setterのない不変クラスを書く
- 値を変える操作を「新しいオブジェクトを返すメソッド」として書く
- リストやマップを持つクラスで、防御的コピーと変更できないビュー（`Collections.unmodifiableList`など）を使う
- ビルダーで設定を組み立て、作る前に検査する

## 課題一覧

課題1は、このフォルダーの雛形（`ImmutablePoint.java`・`ImmutablePointTest.java`）の`TODO`を埋めて完成させます。
課題2〜4は、ファイルを新しく作ります。
解答例は[solutions/](../solutions/)にあります。

課題2と課題3では、第9章で学ぶ`List`（要素を順に並べて持つリスト）と`Map`（キーと値の組を持つマップ）を使います。
第6章の本文の`ImmutablePerson`と同じように、`new ArrayList<>(元のリスト)`で同じ要素を持つ別のリストを作れることだけがわかれば取り組めます。

### 課題1: 不変の座標クラス

**ファイル**: `ImmutablePoint.java`、`ImmutablePointTest.java`

不変の2次元の座標を表すクラスを完成させます。

**要求仕様**:

- フィールドは`private final double x, y`。値はコンストラクターでだけ設定し、setterは作らない
- クラスを`final`にして、サブクラスで値を変えられないようにする
- `distanceTo`（2点間の距離）・`distanceFromOrigin`（原点からの距離）を返す
- `move`（移動）・`rotate`（原点を中心に回転、角度は度）は、自分を変えずに新しい`ImmutablePoint`を返す
- `toString`は`(3.0, 4.0)`の形、`equals`と`hashCode`はxとyで比べる

**実行例**:

```
=== 不変座標クラステスト ===
座標1: (3.0, 4.0)
座標2: (0.0, 0.0)

座標1から座標2への距離: 5.0

座標1を(2.0, 3.0)移動:
元の座標1: (3.0, 4.0)
新しい座標: (5.0, 7.0)

座標1を90度回転:
元の座標1: (3.0, 4.0)
回転後の座標: (-4.0, 3.0)

座標1の原点からの距離: 5.0

不変性テスト:
取得した値: x=3.0, y=4.0
値変更後のpoint1: (3.0, 4.0)

同じ座標値のオブジェクト比較:
point1.equals(point3): true
point1 == point3: false
```

### 課題2: 不変の人物情報クラス

**ファイル**: `ImmutablePerson.java`、`ImmutablePersonTest.java`（新しく作る）

趣味のリストを持つ、不変の人物情報のクラスを作ります。

**要求仕様**:

- フィールドは`private final`の`name`（String）・`age`（int）・`hobbies`（`List<String>`）
- コンストラクターでは、受け取ったリストを`new ArrayList<>(hobbies)`でコピーしてから持つ（防御的コピー）
- `getHobbies`は`Collections.unmodifiableList(hobbies)`で、変更できないリストとして返す
- `withAge`（年齢を変える）・`withHobby`（趣味を足す）・`withoutHobby`（趣味を除く）は、新しい`ImmutablePerson`を返す
- テストでは、コンストラクターに渡したリストを後から変えても、人物の趣味が変わらないことを確かめる

**実行例**:

```
=== 不変人物情報クラステスト ===
元の人物情報:
名前: 田中太郎
年齢: 25歳
趣味: [読書, 映画鑑賞]

年齢を更新（26歳）:
元の人物: 田中太郎（25歳）
新しい人物: 田中太郎（26歳）

趣味を追加（プログラミング）:
元の趣味: [読書, 映画鑑賞]
新しい趣味: [読書, 映画鑑賞, プログラミング]

趣味を削除（読書）:
新しい趣味: [映画鑑賞, プログラミング]

防御的コピーの確認:
渡したリストに「料理」を足したあと: [読書, 映画鑑賞]
```

### 課題3: 設定値管理クラス

**ファイル**: `ApplicationConfig.java`、`ApplicationConfigTest.java`（新しく作る）

アプリの名前・版・設定値（`Map<String, String>`）を持つ、不変の設定クラスを作ります。

**要求仕様**:

- コンストラクターは`private`にし、入れ子のクラス`Builder`で組み立てる（`new ApplicationConfig.Builder("MyApp", "1.0.0").property("timeout", "30").build()`）
- `build`は、アプリ名が空でないことと、`timeout`が正の整数であることを確かめ、正しくなければ`IllegalStateException`を投げる
- `defaultConfig`は、timeout=30・debug=falseのデフォルトの設定を返す
- `withProperty`は、設定を1つ変えた（なければ足した）新しい`ApplicationConfig`を返す
- 表示の順序が決まるよう、マップは入れた順に並ぶ`LinkedHashMap`で持つ

**実行例**:

```
=== アプリケーション設定管理テスト ===
デフォルト設定:
アプリ名: MyApp
バージョン: 1.0.0
設定値: {timeout=30, debug=false}

設定を更新:
元の設定 - タイムアウト: 30
新しい設定 - タイムアウト: 60

設定を追加:
元の設定: {timeout=30, debug=false}
新しい設定: {timeout=30, debug=false, logging=true}

設定値の検証:
作れませんでした: timeoutは正の整数にします: abc
```

### 課題4: 金額計算クラス

**ファイル**: `Money.java`、`MoneyTest.java`（新しく作る）

円の金額を表す不変のクラスを作ります。

**要求仕様**:

- フィールドは`private final long yen`（円の整数）。負の金額で作ろうとしたら`IllegalArgumentException`を投げる
- `plus`・`minus`・`increaseByPercent`（%増し、1円未満は切り捨て）は、新しい`Money`を返す
- `isGreaterThan`で金額を比べ、`equals`と`hashCode`は金額で比べる
- `toString`は`1,000円`の形にする

**実行例**:

```
=== 金額計算クラステスト ===
金額1: 1,000円
金額2: 500円

加算: 1,000円 + 500円 = 1,500円
減算: 1,000円 - 500円 = 500円
10%増し: 1,000円 → 1,100円
比較: 1,000円 > 500円: true
計算のあとの金額1: 1,000円
1000円どうしは等しい？: true

負になる計算:
計算できませんでした: 金額は0円以上にします: -500
```

## コンパイルと実行

```bash
cd exercises/chapter06/basic
javac *.java
java ImmutablePointTest
```

## ヒント

- `final`のフィールドは、コンストラクターの中で必ず1回だけ値を入れる。入れ忘れるとコンパイルエラーになる
- `move`は`return new ImmutablePoint(x + deltaX, y + deltaY);`のように、計算した値で新しいオブジェクトを作って返す
- `getHobbies`で中身のリストをそのまま返すと、受け取った側が`add`で趣味を足せてしまう。コピーを返すか、変更できないリストにして返す
- 金額を`double`で持つと、`0.1 + 0.2`が`0.30000000000000004`になるような誤差が出る。円の整数で持てば誤差は出ない。円未満を正確に扱うときは、標準ライブラリの`java.math.BigDecimal`を使う
- 例外を投げる`throw new IllegalArgumentException(...)`と、受け取る`try`・`catch`は第13章で学ぶ。この課題では、正しくない値でオブジェクトを作らせないために使う

## 完了チェックリスト

- [ ] 課題1: setterがなく、`move`・`rotate`が新しいオブジェクトを返す
- [ ] 課題2: 渡したリストや返したリストを変えても、人物の趣味が変わらない
- [ ] 課題3: ビルダーで設定を組み立て、正しくない設定では作れない
- [ ] 課題4: どの計算のあとも、元の金額が変わっていない
- [ ] 不変クラスにする利点（共有しても安全、スレッドから同時に読んでも安全）を説明できる

**次のステップ**: 基本課題が完了したら、[advanced/](../advanced/)の発展課題に進みましょう。
