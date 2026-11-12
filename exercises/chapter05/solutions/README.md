# 第5章 継承とポリモーフィズム - 解答例

[基礎課題](../basic/)の解答例です。
課題1は雛形の`TODO`を埋めた形、課題2〜4は新しく作るファイルの解答例で、第5章までに学んだ機能だけを使っています。

## 解答例一覧

| 課題 | ファイル | 主な内容 |
|------|----------|----------|
| 課題1: 動物クラスの継承階層 | `Animal.java`、`Dog.java`、`Cat.java`、`AnimalTest.java` | `super(...)`でのコンストラクターの呼び出し、`makeSound`・`showInfo`のオーバーライド、`super.showInfo()` |
| 課題2: 図形クラスの継承と面積計算 | `Shape.java`、`Rectangle.java`、`Circle.java`、`ShapeTest.java` | `getArea`のオーバーライド、`Shape[]`でまとめて合計を求める |
| 課題3: 従業員クラスの継承階層 | `Employee.java`、`Manager.java`、`Engineer.java`、`EmployeeTest.java` | 手当（`getAllowance`）だけをオーバーライドし、総給与の計算は基底クラスにまとめる |
| 課題4: 車両クラスの継承と多態性 | `Vehicle.java`、`Car.java`、`Motorcycle.java`、`VehicleTest.java` | 種類の名前と固有の情報の表示をオーバーライドし、加速と速度の表示は基底クラスにまとめる |

各`〜Test.java`の`main`の出力は、基礎課題のREADMEにある実行例と同じです。

## 実行方法

```bash
cd exercises/chapter05/solutions
javac *.java
java AnimalTest
java ShapeTest
java EmployeeTest
java VehicleTest
```

## 実装のポイント

- `Animal`のフィールドは`protected`なので、`Dog`や`Cat`のメソッドから`name`と書いて使える
- `Dog`の`showInfo`は、`super.showInfo()`で名前と年齢を表示してから「種類: 犬」を足す。基底クラスの表示を書き直さずに済む
- `Animal[] animals = {dog, cat};`の`animal.makeSound()`は、変数の型（Animal）ではなく、実際のオブジェクト（DogかCat）のメソッドを呼ぶ
- `Dog`だけにある`wagTail`や`fetch`は、`Animal`型の変数からは呼べない。`Dog`型の変数から呼ぶ
- 課題3の`Employee`の`getSalary`は「基本給＋`getAllowance()`」。`Manager`と`Engineer`は`getAllowance`をオーバーライドするだけで、総給与も正しくなる（テンプレートメソッドの考え方）
- 課題4の`Vehicle`の`getName`は「ブランド＋の＋`getType()`」。`getType`を「車」「バイク」にオーバーライドすると、「トヨタの車」「ホンダのバイク」になる

## 後の章の機能を使った書き方

- 課題2の`Shape`の`getArea`は0を返し、サブクラスでのオーバーライドを前提にしている。第7章で学ぶ抽象クラスにすると、オーバーライドを忘れたサブクラスをコンパイルエラーにできる

```java
public abstract class Shape {
    // ...
    public abstract double getArea();
}
```

- 車両や従業員を、要素の数を後から変えられるリスト（`List<Vehicle>`）で持つ書き方は、第9章で学ぶ
