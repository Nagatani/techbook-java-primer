# 第13章 enumによる型安全な定数管理

## 🎯総合演習プロジェクトへのステップ

本章で学ぶ`enum`は、**総合演習プロジェクト「ToDoリストアプリケーション」** において、タスクの「優先度」や「状態」といった、決まった種類の値を安全に扱うために非常に役立ちます。

- **優先度の表現**: `HIGH`, `NORMAL`, `LOW` といったタスクの優先度を、単なる文字列や数値ではなく、`Priority`という`enum`型として定義します。
  ```java
  public enum Priority {
      HIGH, NORMAL, LOW
  }
  ```
- **型安全性**: `task.setPriority(Priority.HIGH);` のように、`Priority`型で定義された値しか受け付けなくなり、`"Hihg"`のようなスペルミスや、`99`のような無効な値をコンパイル時点で防ぐことができます。
- **可読性の向上**: `if (task.getPriority() == Priority.HIGH)` のように、コードの意図が明確で読みやすくなります。

## 13.1 enumとは？

`enum`（列挙型）は、**決まったいくつかの値だけを取りうる型**を定義するための機能です。例えば、「曜日（月火水木金土日）」や「信号の色（赤青黄）」のように、値の範囲が限定されているものを扱うのに最適です。

### なぜenumが必要なのか？

`enum`がない場合、このような定数は`public static final`なフィールドとして定義することが一般的でした。

```java
// enumがない時代の定数定義
public class Signal {
    public static final int RED = 0;
    public static final int BLUE = 1;
    public static final int YELLOW = 2;
}

// 利用する側
int currentColor = Signal.RED;
```

この方法には、以下のような問題点があります。
-   **型安全でない**: `int`型なので、`100`のような無効な値も代入できてしまう。
-   **意味が不明確**: `currentColor`が「信号の色」を表すという情報が、型からはわからない。
-   **デバッグしにくい**: `System.out.println(currentColor)`と表示しても、`0`としか出力されず、それが「赤」を意味するのかわからない。

`enum`は、これらの問題をすべて解決します。

### 基本的な構文

```java
public enum DayOfWeek {
    SUNDAY,
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY
}
```

これだけで、`DayOfWeek`と��う新しい型が作られ、その型が取りうる値は定義された7つの曜日に限定されます。

```java
public class EnumExample {
    public static void main(String[] args) {
        DayOfWeek today = DayOfWeek.WEDNESDAY;

        System.out.println("今日は " + today + " です。"); // WEDNESDAY

        // 型安全なので、不正な値はコンパイルエラーになる
        // DayOfWeek tomorrow = "Thursday"; // エラー
        // DayOfWeek invalid = 1;           // エラー
    }
}
```

## 13.2 enumとswitch文

`enum`は`switch`文と非常に相性が良く、安全で読みやすいコードを書くことができます。

```java
public class TrafficLight {
    public static void checkSignal(DayOfWeek day) {
        switch (day) {
            case MONDAY:
            case TUESDAY:
            case WEDNESDAY:
            case THURSDAY:
            case FRIDAY:
                System.out.println(day + " は平日です。");
                break;
            case SATURDAY:
            case SUNDAY:
                System.out.println(day + " は週末です。");
                break;
            // defaultは不要な場合が多い（enumの全要素を網羅しているため）
        }
    }

    public static void main(String[] args) {
        checkSignal(DayOfWeek.FRIDAY);    // 平日
        checkSignal(DayOfWeek.SUNDAY);    // 週末
    }
}
```
`case`句では、`DayOfWeek.MONDAY`のように書く必要はなく、`MONDAY`とだけ書ける点に注意してください。

## 13.3 メソッドやフィールドを持つenum

`enum`は単なる定数の集まりではなく、クラスのようにフィールドやメソッドを持たせることができます。

```java
public enum Planet {
    MERCURY (3.303e+23, 2.4397e6),
    VENUS   (4.869e+24, 6.0518e6),
    EARTH   (5.976e+24, 6.37814e6),
    MARS    (6.421e+23, 3.3972e6);

    private final double mass;   // 質量 (kg)
    private final double radius; // 半径 (m)

    // コンストラクタ（privateでなければならない）
    Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
    }

    public double mass() { return mass; }
    public double radius() { return radius; }

    // 万有引力定数
    public static final double G = 6.67300E-11;

    // 表面重力を計算するメソッド
    public double surfaceGravity() {
        return G * mass / (radius * radius);
    }
}

public class PlanetTest {
    public static void main(String[] args) {
        double earthWeight = 75.0; // 地球での体重
        double mass = earthWeight / Planet.EARTH.surfaceGravity();
        for (Planet p : Planet.values()) {
           System.out.printf("%sでの体重は %f です%n", p, p.surfaceGravity() * mass);
        }
    }
}
```
`Planet.values()`は、`enum`の全要素を配列として返す便利なメソッドです。

## まとめ

本章では、型安全な定数を扱うための`enum`について学びました。

-   **`enum`**は、取りうる値が限定された、独自の型を定義する機能です。
-   `int`定数などと比べて、**型安全**で、コードが**読みやすく**なります。
-   `switch`文と組み合わせることで、網羅的で安全な条件分岐を記述できます。
-   クラスのように、**フィールドやメソッドを持たせる**ことができ、より高機能な定数を定義できます。

プログラムの中で「種類」や「状態」を表す値が出てきたら、まず`enum`が使えないか検討することは、高品質なコードを書くための良い習慣です。
