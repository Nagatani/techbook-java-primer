# 第8章 レコード - 解答例

[基礎課題](../basic/)の解答例です。
基礎課題の雛形の`TODO`を埋めた形で書いてあり、第8章までに学んだ機能に加えて、第9章で学ぶ`List`を最小限だけ使っています。

## 解答例一覧

| 課題 | ファイル | 主な内容 |
|------|----------|----------|
| 課題1: 点のレコード | `Point.java` | コンパクトコンストラクターでの検査、新しいレコードを返す`translate`、静的ファクトリメソッド`origin` |
| 課題2: 人のレコード | `Person.java` | `LocalDate`と`Period`での年齢、文字列の加工 |
| 課題3: DTOのレコード | `ProductDTO.java`、`ProductEntity.java` | 変更できるクラスから、必要な値だけを持つ不変のレコードを作る |
| 課題4: 入れ子のレコードとビルダー | `Address.java`、`Customer.java` | レコードの中のレコード、`List.copyOf`での防御的コピー、ビルダー |
| デモ | `RecordDemo.java` | 課題1〜4を試す |

`RecordDemo`の出力は、基礎課題のREADMEにある実行例と同じです。

## 実行方法

```bash
cd exercises/chapter08/records/solutions
javac *.java
java RecordDemo
```

## 実装のポイント

- `Point`の`translate`は、レコードのフィールドを変えられないので、移動した値で新しい`Point`を作って返す。`p1`は変わらない
- `Person`の`getAge`は今日の日付を使うので、実行する日で結果が変わる。日付を受け取る`getAgeOn`を分けておくと、決まった日付で結果を確かめられる（第21章のテストでも使う考え方）
- `ProductDTO.fromEntity`で作ったDTOは、エンティティの値をコピーして持つ。あとでエンティティの`setPrice`を呼んでも、DTOの価格は変わらない
- `Customer`のコンパクトコンストラクターの`phoneNumbers = List.copyOf(phoneNumbers);`は、引数に代入し直している。コンパクトコンストラクターの最後に、引数の値がフィールドに入る
- `Address`の検査で使う`isBlank`は`static`にする。コンパクトコンストラクターの中では、まだフィールドに値が入っていない

## 後の章の機能を使った書き方

- 点の配列を原点に近い順に並べるには、第11章で学ぶ`Comparator`を使って`Arrays.sort(points, Comparator.comparingDouble(Point::distanceFromOrigin))`と書ける
- レコードは`equals`と`hashCode`が自動で作られるので、第9章で学ぶ`HashSet`や`HashMap`のキーにそのまま使える
