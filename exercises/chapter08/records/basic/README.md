# 第8章 基本課題（レコード）

## 学習目標

- `record`でデータを持つクラスを1行で宣言し、自動で作られるアクセサー・`equals`・`hashCode`・`toString`を使う
- コンパクトコンストラクターで、作るときに値を検査する
- レコードにメソッドと静的ファクトリメソッドを足す
- レコードの中に別のレコードを持たせ、ビルダーで組み立てる

## 課題一覧

このフォルダーのJavaファイルは、`TODO`の箇所を埋めて完成させる雛形です。
課題1〜4を進めながら、`RecordDemo.java`の各メソッドの`TODO`を埋めて試します。
解答例は[solutions/](../solutions/)にあります。

### 課題1: 点のレコード

**ファイル**: `Point.java`（`RecordDemo`の`demonstratePoint`・`demonstrateRecordCollections`）

**要求仕様**:

- `record Point(double x, double y)`を宣言する
- コンパクトコンストラクターで、xとyが有限の値（`Double.isFinite`）でなければ`IllegalArgumentException`を投げる
- `distanceFromOrigin`・`distanceTo`は距離を返し、`translate`は移動した新しい`Point`を返す。`origin`は原点を返す静的ファクトリメソッド
- `Point`の配列で、原点にいちばん近い点と、半径4以内の点の数を求める

### 課題2: 人のレコード

**ファイル**: `Person.java`（`RecordDemo`の`demonstratePerson`）

**要求仕様**:

- `record Person(String name, LocalDate birthDate, String email)`を宣言する
- コンパクトコンストラクターで、名前が空でないこと、誕生日が未来でないこと、メールアドレスに`@`があることを確かめる
- `getAge`は`Period.between(birthDate, LocalDate.now()).getYears()`で年齢を返す。実行する日で値が変わるので、解答例では日付を引数で受け取る`getAgeOn`も作り、デモでは2026年4月1日の年齢を表示する
- `getEmailDomain`は`@`より後ろを、`getFormattedName`は語の先頭を大文字にした名前を返す
- `of(name, email)`は、誕生日を今日の20年前にした`Person`を返す

### 課題3: DTOのレコード

**ファイル**: `ProductDTO.java`、`ProductEntity.java`（雛形のまま使う。`RecordDemo`の`demonstrateDTO`）

**要求仕様**:

- `record ProductDTO(Long id, String name, double price, String category)`を宣言し、価格が負のときと、商品名・カテゴリが空のときは作らない
- `getPriceWithTax`（税込み）・`getFormattedPrice`（`¥120,000`の形）・`toJson`（`{"id":1,"name":"Laptop",...}`の形）を書く
- `fromEntity`は、変更できるクラス`ProductEntity`から、受け渡しに必要な4つの値だけを取り出して`ProductDTO`を作る
- DTOを作ったあとでエンティティの価格を変えても、DTOの価格が変わらないことを確かめる

### 課題4: 入れ子のレコードとビルダー

**ファイル**: `Address.java`、`Customer.java`（`RecordDemo`の`demonstrateNestedRecords`）

**要求仕様**:

- `record Address(String street, String city, String postalCode, String country)`は、すべての項目が空でなく、郵便番号が数字とハイフンだけであることを確かめる
- `record Customer(String name, String email, Address address, List<String> phoneNumbers)`は、電話番号が1つ以上あることを確かめ、`List.copyOf`で変更できないリストにしてからフィールドに入れる
- `Customer.builder().name(...).email(...).address(...).phoneNumbers(...).build()`の形で組み立てられるよう、`CustomerBuilder`のメソッドを書く
- `getSummary`は「名前 (メール) - 都市, 国」の形、`getPrimaryPhone`は最初の電話番号を返す

`List`（要素を順に並べて持つリスト）は第9章で学びます。この課題では、`List.of(...)`で作ったリストを受け取り、`List.copyOf`でコピーし、`get(0)`で最初の要素を取り出すことだけを使います。

## 実行例

`RecordDemo`の出力です（解答例）。

```
=== Pointレコードのデモ ===
p1: Point[x=3.0, y=4.0]
x座標: 3.0, y座標: 4.0
原点からの距離: 5.0
(6, 8)までの距離: 5.0
(1, -2)だけ移動: Point[x=4.0, y=2.0]（p1は Point[x=3.0, y=4.0] のまま）
原点: Point[x=0.0, y=0.0]
p1.equals(p2): true、p1 == p2: false
hashCodeが等しい: true
作れない点: 座標は有限の値にします: NaN, 0.0

=== Personレコードのデモ ===
person: Person[name=taro yamada, birthDate=2005-06-15, email=taro@example.com]
2026年4月1日の年齢: 20
メールのドメイン: example.com
整えた名前: Taro Yamada
Person.ofで作った人: hanako（example.org）
作れない人: メールアドレスには@が要ります: jiro.example.com

=== DTOパターンのデモ ===
dto: ProductDTO[id=1, name=Laptop, price=120000.0, category=Electronics]
税込み価格: 132,000円
表示用の価格: ¥120,000
JSON: {"id":1,"name":"Laptop","price":120000.0,"category":"Electronics"}
エンティティの価格を変えたあとのDTO: ¥120,000

=== ネストされたRecordのデモ ===
要約: 山田太郎 (yamada@example.com) - 東京都千代田区, 日本
主な電話番号: 03-1234-5678
日本の住所: true
住所:
100-0001 東京都千代田区
千代田1-1
日本
作れない住所: 郵便番号は数字とハイフンで書きます: AB-123

=== Recordを配列で扱う ===
原点にいちばん近い点: Point[x=1.0, y=2.0]
原点から半径4以内の点: 3個
points[1].equals(points[3]): true
```

## コンパイルと実行

```bash
cd exercises/chapter08/records/basic
javac *.java
java RecordDemo
```

## ヒント

- レコードのフィールドは`private final`になり、setterは作られない。値を変えたいときは、新しいレコードを作って返す
- アクセサーは`getX()`ではなく、フィールドと同じ名前の`x()`になる
- コンパクトコンストラクターでは、引数に代入し直すと、その値がフィールドに入る（`phoneNumbers = List.copyOf(phoneNumbers);`）
- コンパクトコンストラクターから呼ぶ補助のメソッドは、`static`にする（まだフィールドに値が入っていないため）
- 正しくない値で例外を投げる`throw`と、受け取る`try`・`catch`は第13章で学ぶ

## 完了チェックリスト

- [ ] 課題1: 自動で作られる`equals`で、別々に作った同じ値の点が等しくなる
- [ ] 課題2: コンパクトコンストラクターで、正しくない値の人を作らせない
- [ ] 課題3: エンティティを変えても、DTOが変わらない
- [ ] 課題4: ビルダーで、入れ子のレコードを組み立てられる
- [ ] レコードにできるクラスと、できないクラス（値を変えたいクラス、継承したいクラス）を説明できる

**次のステップ**: 基本課題が完了したら、[advanced/](../advanced/)の発展課題に進みましょう。
