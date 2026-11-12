# 第6章 不変性とfinalキーワード - 解答例

[基礎課題](../basic/)の解答例です。
課題1は雛形の`TODO`を埋めた形、課題2〜4は新しく作るファイルの解答例です。
第6章までに学んだ機能に加えて、第6章の本文と同じく、第9章で学ぶ`List`・`Map`を最小限だけ使っています。

## 解答例一覧

| 課題 | ファイル | 主な内容 |
|------|----------|----------|
| 課題1: 不変の座標クラス | `ImmutablePoint.java`、`ImmutablePointTest.java` | `final`のクラスとフィールド、新しいオブジェクトを返す`move`・`rotate` |
| 課題2: 不変の人物情報クラス | `ImmutablePerson.java`、`ImmutablePersonTest.java` | コンストラクターでの防御的コピー、変更できないリストを返すgetter、`with`系のメソッド |
| 課題3: 設定値管理クラス | `ApplicationConfig.java`、`ApplicationConfigTest.java` | ビルダーと`build`での検査、`withProperty` |
| 課題4: 金額計算クラス | `Money.java`、`MoneyTest.java` | 円の整数で持つ金額、負の金額を作らせないコンストラクター |

各`〜Test.java`の`main`の出力は、基礎課題のREADMEにある実行例と同じです。

## 実行方法

```bash
cd exercises/chapter06/solutions
javac *.java
java ImmutablePointTest
java MoneyTest
```

## 実装のポイント

- `ImmutablePoint`の`rotate(90)`は、`Math.cos`が0にごく近い小さな値を返すため、計算結果は-4.0や3.0ちょうどではない。`toString`で小数点以下1桁に丸めて表示している
- `ImmutablePerson`のコンストラクターで`this.hobbies = hobbies;`と書くと、呼び出し側が持っているリストと同じものを指す。呼び出し側がリストに足すと、不変のはずの人物の趣味も変わる
- `withHobby`は、自分のリストのコピーに足してから新しい人物を作る。自分のリストに直接`add`すると、元の人物が変わってしまう
- `ApplicationConfig`のコンストラクターを`private`にしたので、外からは`Builder`か`defaultConfig`でしか作れない。どの経路でも`build`の検査を通る
- `Money`の`increaseByPercent`は`yen * (100 + percent) / 100`。先に掛けてから割るので、整数どうしの割り算でも1円未満だけが切り捨てられる

## 後の章の機能を使った書き方

- `List`・`Map`・`ArrayList`・`LinkedHashMap`の使い方は第9章で学ぶ。`List.copyOf(hobbies)`を使うと、コピーと変更できないリストへの変換を1回でできる（ただし`null`の要素は入れられない）
- `ImmutablePoint`や`Money`のように、値を持つだけの不変クラスは、第8章で学ぶレコードで短く書ける

```java
public record ImmutablePoint(double x, double y) {
    public ImmutablePoint move(double deltaX, double deltaY) {
        return new ImmutablePoint(x + deltaX, y + deltaY);
    }
}
```

- 例外（`throw`・`try`・`catch`）は第13章で学ぶ
