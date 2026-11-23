# 第8章 Javaの特殊なクラス形式：よくあるエラーと対処法

書籍『Java入門』第8章「Javaの特殊なクラス形式」で扱う内容に関連して、よく遭遇するエラーとその対処法をまとめています。

## enum定数の大文字小文字を間違える

### 原因と対処法

```java
// 間違い
if (status == Status.active) {  // コンパイルエラー
    // 処理
}

// 正しい
if (status == Status.ACTIVE) {
    // 処理
}
```

## switch文のcaseにenumの型名を付ける（Java 20以前のエラー）

### 原因と対処法

本書が対象とするJava 25では、列挙型のswitchの`case`に`Status.ACTIVE`のような型名付きの定数を書いても、エラーにはなりません。
エラーになるのは、Java 20以前のコンパイラーでコンパイルしたときです。
Java 21から型名付きでも書けるようになりましたが、switchの対象が列挙型であることは明らかなので、定数名だけを書くのが一般的です。
古いバージョンのJavaでも動かすコードや、古い書籍・記事のコードを読むときは、この違いに注意してください。

```java
// Java 20以前ではコンパイルエラー（Java 21以降は書けるが冗長）
switch (status) {
    case Status.ACTIVE:
        break;
}

// 一般的な書き方（どのバージョンでも使える）
switch (status) {
    case ACTIVE:
        break;
}
```

## Recordのアクセサーをgetの形で呼ぶ

### エラー例

```java
public class RecordAccessorError {
    record Student(String name, int score) {}

    public static void main(String[] args) {
        Student s = new Student("田中", 80);
        IO.println(s.getName());  // コンパイルエラー
    }
}
```

### エラーメッセージ

```
error: cannot find symbol
  symbol:   method getName()
  location: variable s of type Student
```

### 原因と対処法

Recordが自動で作る値の取り出し方（アクセサー）は、`name()`のようにフィールドと同じ名前のメソッドです。
getterの`getName()`は作られません。

コンパイルできる全文：[chapter08-code/RecordAccessor/](chapter08-code/RecordAccessor/)

```java
public class RecordAccessor {
    record Student(String name, int score) {}

    public static void main(String[] args) {
        Student s = new Student("田中", 80);
        // Recordの値は、フィールドと同じ名前のメソッドで取り出す
        IO.println(s.name() + ": " + s.score() + "点");
    }
}
```

実行結果：
```
田中: 80点
```

## コンパクトコンストラクターでフィールドに代入する

### エラー例

```java
public class CompactConstructorError {
    record Student(String name, int score) {
        Student {
            if (score < 0) {
                throw new IllegalArgumentException("点数は0以上にしてください");
            }
            this.name = name;  // コンパイルエラー
        }
    }
}
```

### エラーメッセージ

```
error: cannot assign a value to final variable name
```

### 原因と対処法

コンパクトコンストラクター（引数の括弧を書かないコンストラクター）では、フィールドへの代入はコンパイラーが本体のあとで行います。
本体には値の確認だけを書き、`this.name = name;`とは書きません。
値を整えてから入れたいときは、引数（`name`）に代入し直します。

コンパイルできる全文：[chapter08-code/CompactConstructor/](chapter08-code/CompactConstructor/)

```java
public class CompactConstructor {
    record Student(String name, int score) {
        // コンパクトコンストラクターには値の確認だけを書く。
        // フィールドへの代入は、この本体のあとでコンパイラーが行う
        Student {
            if (score < 0) {
                throw new IllegalArgumentException("点数は0以上にしてください");
            }
            // 引数の値を整えたいときは、引数に代入し直す
            name = name.strip();
        }
    }

    public static void main(String[] args) {
        IO.println(new Student("  田中 ", 80));
        try {
            new Student("佐藤", -1);
        } catch (IllegalArgumentException e) {
            IO.println("エラー: " + e.getMessage());
        }
    }
}
```

実行結果：
```
Student[name=田中, score=80]
エラー: 点数は0以上にしてください
```

## sealedインターフェイスの実装を増やしたときの書き足し忘れ

### エラー例

`permits`に書いていない型が実装すると、コンパイルエラーになります。

```java
public class SealedPermitsError {
    sealed interface Shape permits Circle, Square {}
    record Circle(double radius) implements Shape {}
    record Square(double side) implements Shape {}
    record Triangle(double base, double height) implements Shape {}  // コンパイルエラー
}
```

`permits`に書き足しても、`default`のないswitchにcaseを書き足し忘れると、そのswitchがコンパイルエラーになります。

```java
static double area(Shape shape) {
    return switch (shape) {  // コンパイルエラー（Triangleのcaseがない）
        case Circle c -> Math.PI * c.radius() * c.radius();
        case Square s -> s.side() * s.side();
    };
}
```

### エラーメッセージ

```
error: class is not allowed to extend sealed class: Shape (as it is not listed in its 'permits' clause)
```

```
error: the switch expression does not cover all possible input values
```

### 原因と対処法

sealedインターフェイスを実装できるのは、`permits`に並べた型だけです。
実装を増やすときは`permits`に書き足し、その型を扱う`default`のないswitchにもcaseを書き足します。
switchのエラーは、書き足すべき場所をコンパイラーが教えてくれているものなので、`default`を足して消すのではなく、caseを書き足します。

コンパイルできる全文：[chapter08-code/SealedShapes/](chapter08-code/SealedShapes/)

```java
public class SealedShapes {
    // 実装を増やすときは、permitsにも書き足す
    sealed interface Shape permits Circle, Square, Triangle {}
    record Circle(double radius) implements Shape {}
    record Square(double side) implements Shape {}
    record Triangle(double base, double height) implements Shape {}

    // 実装を増やしたら、defaultのないswitchにもcaseを書き足す
    static double area(Shape shape) {
        return switch (shape) {
            case Circle c -> Math.PI * c.radius() * c.radius();
            case Square s -> s.side() * s.side();
            case Triangle t -> t.base() * t.height() / 2;
        };
    }

    public static void main(String[] args) {
        IO.println(area(new Square(3)));
        IO.println(area(new Triangle(4, 5)));
    }
}
```

実行結果：
```
9.0
10.0
```

## staticでない内部クラスをstaticメソッドから直接作る

### エラー例

```java
public class InnerClassError {
    class Counter {
        int count;
    }

    public static void main(String[] args) {
        Counter counter = new Counter();  // コンパイルエラー
    }
}
```

### エラーメッセージ

```
error: non-static variable this cannot be referenced from a static context
```

### 原因と対処法

staticでない内部クラス（メンバークラス）のオブジェクトは、外側のクラスのオブジェクトに結び付いています。
staticの`main`には外側のオブジェクト（`this`）がないので、`new Counter()`とは書けません。
`outer.new Counter()`のように外側のオブジェクトから作るか、外側のオブジェクトを使わないなら`static`を付けた静的ネストクラスにします。

コンパイルできる全文：[chapter08-code/InnerClass/](chapter08-code/InnerClass/)

```java
public class InnerClass {
    class Counter {
        int count;
    }

    // 外側のインスタンスを必要としないなら、staticを付けた静的ネストクラスにする
    static class Totalizer {
        int total;
    }

    public static void main(String[] args) {
        // staticでない内部クラスは、外側のインスタンスから作る
        InnerClass outer = new InnerClass();
        InnerClass.Counter counter = outer.new Counter();
        counter.count++;
        IO.println("count = " + counter.count);

        Totalizer totalizer = new Totalizer();
        totalizer.total += 10;
        IO.println("total = " + totalizer.total);
    }
}
```

実行結果：
```
count = 1
total = 10
```

## 匿名クラスから、実質的にfinalでないローカル変数を書き換える

### エラー例

```java
import java.util.Comparator;

public class EffectivelyFinalError {
    public static void main(String[] args) {
        int compared = 0;
        Comparator<String> byLength = new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                compared++;  // コンパイルエラー
                return Integer.compare(a.length(), b.length());
            }
        };
    }
}
```

### エラーメッセージ

```
error: local variables referenced from an inner class must be final or effectively final
```

### 原因と対処法

ローカルクラスや匿名クラスから参照できるローカル変数は、`final`か、一度も代入し直さない（実質的にfinalな）変数だけです。
匿名クラスのなかで値を数えたいときは、配列の要素やオブジェクトのフィールドに持たせます。
変数そのものは書き換えないので、実質的にfinalのままです。
同じ制限は、第11章で学ぶラムダ式にも当てはまります。

コンパイルできる全文：[chapter08-code/EffectivelyFinal/](chapter08-code/EffectivelyFinal/)

```java
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EffectivelyFinal {
    public static void main(String[] args) {
        // 数えたい値は、配列の要素などのオブジェクトのなかに持たせる
        // （変数countsそのものは書き換えないので、実質的にfinal）
        int[] counts = {0};
        Comparator<String> byLength = new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                counts[0]++;
                return Integer.compare(a.length(), b.length());
            }
        };

        List<String> words = new ArrayList<>(List.of("banana", "fig", "apple"));
        words.sort(byLength);
        IO.println(words);
        IO.println("比べた回数が0より多い: " + (counts[0] > 0));
    }
}
```

実行結果：
```
[fig, apple, banana]
比べた回数が0より多い: true
```
