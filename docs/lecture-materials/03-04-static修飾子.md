---
title: static修飾子
---

>オブジェクト指向プログラミングおよび演習1 第3回
>
>クラスのフィールドやメソッドをクラスをインスタンス化することなく使用したい場合はstatic修飾子を使う

クラスのフィールドやメソッドをクラスをインスタンス化することなく使用したい場合は`static`修飾子を使いますが、注意点も多数あります。

## static
毎度おなじみmainメソッドに指定してある修飾子です。

これは、そのメソッドやフィールドが **静的** であることを表しています。  
「静的である」とは、プログラム実行時にメモリへすべて展開され、共有されます。


**staticでない**フィールドやメソッドは、生成されたオブジェクト（インスタンス）ごとに存在するものでした。

たとえば、`apple` オブジェクトの `price` と `orange` オブジェクトの `price` は別々の値を持つことができました。

しかし、時には特定のオブジェクトに属するのではなく、**クラス全体で共有したい情報**や、**オブジェクトを生成しなくても利用したい機能**が必要になることがあります。

その際に使うのが `static` 修飾子です。`static` は「静的」と訳され、プログラムの開始時にメモリ上へ展開されるイメージです（インスタンスメンバーは `new` されるまで存在しない「動的」なもの）。

### staticなフィールド、メソッド
それぞれ、**クラスフィールド** 、**クラスメソッド** と呼ばれます。

#### クラスフィールド

クラスに直接関連付けられた変数です。

このクラスからいくつのオブジェクトが生成されても、`static`フィールドは**クラスにただ1つだけ**存在し、**すべてのオブジェクトから共有**されます。メモリ上の「共有スペース」のようなイメージです。

* アクセス方法:
    - `クラス名.フィールド名` でアクセスするのが一般的です（例: `RegisterSystem.totalSalesToday`）。オブジェクトから `オブジェクト名.フィールド名` でもアクセス可能ですが、そのフィールドが `static` であると分かりにくくなるため、クラス名でのアクセスが推奨されます。

クラスフィールドは、共有という特性上、気をつけなければいけない点た多数あります。この部分は後半でサンプルコードを踏まえて解説します。

#### クラスメソッド

クラスに直接関連付けられたメソッドです。

**オブジェクトを生成 (`new`) しなくても**、`クラス名.メソッド名()` という形式で呼び出すことができます。

`static`メソッドは特定のオブジェクトに関連付けられていません。そのため、`static` メソッドの内部からは、`static` でないフィールド（インスタンス変数）や `static` でないメソッド（インスタンスメソッド）を**直接呼び出すことはできません**。
なぜなら、どのオブジェクトのインスタンス変数やインスタンスメソッドを指しているのか特定できないからです（`this` キーワードが使えません）。
ただし、`static` なフィールドや他の `static` なメソッドは呼び出せます。

* 考えられる用途
    * **ユーティリティメソッド:** 特定のオブジェクトの状態に依存しない、汎用的な補助機能を提供する場合（例: `Math.sqrt()`、`Integer.parseInt()` など）。
    * **ファクトリメソッド（応用）:** オブジェクトの生成処理をカプセル化する場合に使用する。
    * **`main` メソッド:** プログラムの開始点となる `main` メソッドは、JVMがオブジェクト生成前に呼び出す必要があるため `static` です。


### クラスフィールドの注意点
クラスフィールドは、プログラム実行時にすべてがメモリに展開され、**共有** されます。
共有されるということは、インスタンスを複数作成して、それぞれのインスタンスからクラスフィールドに対して操作を加えた場合、相互に影響しあうこととなります。

`new`演算子を使って、複数のインスタンスを作成したとしても、クラスフィールドに対する操作は、すべてのインスタンスに影響を及ぼしてしまいます。

サンプルソースを以下に載せておきます。

ファイル名: `MyClass.java`

```java
public class MyClass {
  public static int value;   //このフィールドは、すべてのインスタンスで共有される
  public int getValue() { return value; }
}
```

ファイル名: `MyClassTest.java`

```java
public class MyClassTest {
  public static void main(String[] args) {

    MyClass.value = 1;

    MyClass m1 = new MyClass();
    MyClass m2 = new MyClass();

    System.out.println("MyClassの値：" + MyClass.value);
    System.out.println("MyClass m1の値：" + m1.getValue());
    System.out.println("MyClass m2の値：" + m2.getValue());

    m1.value = 2;
    System.out.println("m1.value = 2;を実行");
    System.out.println("MyClassの値：" + MyClass.value);
    System.out.println("MyClass m1の値：" + m1.getValue());
    System.out.println("MyClass m2の値：" + m2.getValue());

    m2.value = 3;
    System.out.println("m2.value = 3;を実行");
    System.out.println("MyClassの値：" + MyClass.value);
    System.out.println("MyClass m1の値：" + m1.getValue());
    System.out.println("MyClass m2の値：" + m2.getValue());

  }
}
```

実行結果は以下のようになります。

```
$ javac MyClassTest.java
$ java MyClassTest
MyClassの値：1
MyClass m1の値：1
MyClass m2の値：1
m1.value = 2;を実行
MyClassの値：2
MyClass m1の値：2
MyClass m2の値：2
m2.value = 3;を実行
MyClassの値：3
MyClass m1の値：3
MyClass m2の値：3
```

すべてのインスタンスで共有されていることがわかると思います。


## mainメソッドを見てみよう
javaコマンドで実行しているmainメソッドを持つクラスは、インスタンス化してない状態で実行できています。

mainメソッドには、必ずstatic修飾子が付きます。
つまり、プログラム実行時にインスタンス化をしなくても勝手にメモリに展開されているためインスタンス化の必要がありません。


## まとめ
staticは静的なフィールドやメソッドを作成したい場合に使用します。

staticは便利な反面、影響範囲を良く理解しないと危険です。
乱用は避けましょう。

## 補足: staticインポート

staticで修飾されたフィールドやメソッドなどのメンバーにアクセスする際、必ずクラス名を先に書く必要があります。それ自体は問題ないと思いますが、クラス名を毎回書くことで可読性が下がる場合も存在します。  
そこで、staticインポートを使うことで、クラス名を書かなくても直接staticメンバへアクセスできるようになります。

たとえば、`java.lang.Math.PI`を使用する場合を想定します。

```java
public class Sample {
    public static void main(String[] args) {
        System.out.println(Math.PI);  // Mathはjava.langのパッケージなので、別途インポートは必要ありませんが「Math」は毎回書かなければいけません。
    }
}
```

以下のようにstaticインポートを書くことで、`Math.PI`を`PI`だけで使用することができるようになります。

```java
import static java.lang.Math.PI;  // Math.PIを staticインポート

public class Sample {
    public static void main(String[] args) {
        System.out.println(PI);
    }
}
```

また、クラス内のstaticメンバーをまとめてすべてインポートする例を次に示します。

```java
import static java.lang.Math.*;
public class Sample {
    public static void main(String[] args) {
        for (int deg = 0; deg < 360; deg++) {
            double theta = toRadians(deg);
            double r = cos(theta);
            System.out.println(r);
        }
    }
}
```

`toRadians`、`cos`、`PI`はそれぞれ`java.lang.Math`のstaticメンバーです。
個別にインポートしなくても使用できていることが確認できます。

staticインポートは、頻繁にstaticメンバーにアクセスする必要がある場合に使用しましょう。  
ただし、使いすぎると名前空間が煩雑になり可読性と保守性が低下します。適切な使用を心がけ、ソースコードの可読性向上を意識しましょう。

