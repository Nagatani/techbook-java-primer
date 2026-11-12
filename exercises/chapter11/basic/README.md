# 第11章 基本課題

## 学習目標

- ラムダ式で関数型インターフェイスの実装を短く書く
- 標準の関数型インターフェイス（Function・Predicate・Consumer・Supplier・BiFunction・UnaryOperator）を使い分ける
- `@FunctionalInterface`を付けた独自のインターフェイスを作り、デフォルトメソッドで処理を組み合わせる
- メソッド参照（静的メソッド・任意のインスタンスのメソッド・コンストラクター）と、関数の合成（`andThen`・`and`・`negate`）を使う

## 課題一覧

このフォルダーのJavaファイルは、`TODO`の箇所を埋めて完成させる雛形です。
課題1と課題2は雛形を完成させ、課題3は新しくファイルを作ります。
解答例は[solutions/](../solutions/)にあります。

### 課題1: 標準の関数型インターフェイス

**ファイル**: `StandardFunctionalDemo.java`

`null`になっている6つの変数を、ラムダ式で実装します。

**要求仕様**:

- `Function<String, Integer>`は文字列の長さ、`Predicate<Integer>`は偶数かどうかを返す
- `Consumer<String>`は`*** メッセージ ***`の形で表示する
- `Supplier<List<String>>`は、呼ばれるたびに新しい空のリストを返す（1つ目のリストに加えた要素が2つ目に現れないことを確かめる）
- `BiFunction<Integer, Integer, Integer>`は大きいほう、`UnaryOperator<String>`は大文字にした文字列を返す

**実行例**:

```
=== 標準関数型インターフェイスのデモ ===

1. Function<T,R>:
Length of 'Hello': 5

2. Predicate<T>:
Is 4 even? true
Is 5 even? false

3. Consumer<T>:
*** Important Message ***

4. Supplier<T>:
first: [A], second: []

5. BiFunction<T,U,R>:
Max of 10 and 20: 20

6. UnaryOperator<T>:
Uppercase: HELLO WORLD
```

### 課題2: 独自の関数型インターフェイス

**ファイル**: `StringProcessor.java`、`Calculator.java`、`CustomFunctionalDemo.java`

文字列を加工する`StringProcessor`と、2つの数を計算する`Calculator`を完成させます。

**要求仕様**:

- `StringProcessor`のデフォルトメソッド`withTrim`は、前後の空白を除いてから`process`を呼ぶ処理を返す
- `andThen(after)`は、自分の処理の結果を`after`に渡す処理を返す
- `Calculator`の静的メソッド`add`・`subtract`・`multiply`・`divide`は、それぞれの計算をラムダ式で返す。`divide`は0で割ろうとしたら「0では割れません」と表示し、`Double.NaN`を返す
- `CustomFunctionalDemo`では、大文字にする処理・逆順にする処理・べき乗（`Math.pow`）の計算をラムダ式で書く

**実行例**:

```
=== StringProcessorのデモ ===
元の文字列: '  hello world  '
トリム後大文字: 'HELLO WORLD'
複合処理: '[PROCESSED] OLLEH'

=== Calculatorのデモ ===

=== 計算器のデモ ===
10.0 + 5.0 = 15.0
10.0 - 5.0 = 5.0
10.0 * 5.0 = 50.0
10.0 / 5.0 = 2.0
0では割れません
10.0 / 0 = NaN
10.0 ^ 5.0 = 100000.0
```

### 課題3: メソッド参照と関数の合成

**ファイル**: `MethodRefs.java`（新しく作る）

リストの操作に、メソッド参照と合成した関数を渡します。

**要求仕様**:

- `List.of("banana", "Apple", "cherry", "kiwi", "Date")`をコピーした`ArrayList`を使う
- `replaceAll(String::toLowerCase)`で小文字にそろえる（任意のインスタンスのメソッド参照）
- `sort`で辞書順（`Comparator.naturalOrder()`）と、短い順（ラムダ式で書いた`Comparator`）に並べる
- `forEach(IO::println)`で1行ずつ表示する（静的メソッド参照）
- `Predicate`の`and`と`negate`で「4文字以下で、dで始まらない」条件を作り、`removeIf`に渡す
- `Function`の`andThen`で「長さを求めて2倍にする」関数を作る
- `StringBuilder::new`（コンストラクター参照）を`Function<String, StringBuilder>`として使う

**実行例**:

```
元のリスト: [banana, Apple, cherry, kiwi, Date]
小文字に: [banana, apple, cherry, kiwi, date]
辞書順: [apple, banana, cherry, date, kiwi]
短い順: [date, kiwi, apple, banana, cherry]
1つずつ表示:
date
kiwi
apple
banana
cherry
4文字以下でdで始まらない語を削除: [date, apple, banana, cherry]
bananaの長さの2倍: 12
逆順: adbmal
```

## コンパイルと実行

```bash
cd exercises/chapter11/basic
javac *.java
java StandardFunctionalDemo
java CustomFunctionalDemo
```

## ヒント

- ラムダ式の引数の型は、代入先の関数型インターフェイスから推論されるので省略できる
- デフォルトメソッドの中で`process(...)`と書くと、そのデフォルトメソッドを呼んだインスタンスの`process`が呼ばれる。`withTrim`は`s -> process(s.trim())`のように書ける
- `double`を0で割っても例外は起きず、`Infinity`や`NaN`になる。この課題では、0で割る前に確かめる
- `andThen`は「自分の後に相手」、`compose`は「相手の後に自分」の順で処理する

## 完了チェックリスト

- [ ] 課題1: 6種類の標準の関数型インターフェイスをラムダ式で実装できた
- [ ] 課題2: デフォルトメソッドで処理をつなげる仕組みを説明できる
- [ ] 課題3: 3種類のメソッド参照を使い分けられる
- [ ] 課題3: `Predicate`と`Function`を合成して新しい条件・関数を作れる

**次のステップ**: 基本課題が終わったら、[advanced/](../advanced/)の発展課題に進みましょう。
