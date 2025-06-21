---
title: Objectクラスのメソッドのオーバーライド
---

> オブジェクト指向プログラミングおよび演習1 第5回
>
> java.lang.Objectはすべてのクラスのスーパークラス


## `Object`クラス

このクラスは、Javaのすべてのクラスの頂点に立つ特別なクラスで、私たちが作るどんなクラスも、意識していなくても必ず`Object`クラスを継承しています。

`Object`クラスには、すべてのオブジェクトが共通して持っている基本的なメソッドがいくつか定義されています。
その中でもとくに重要なのが、今回解説する`toString()`メソッドと`equals()`メソッドです。これらのメソッドを正しく理解し、必要に応じて「オーバーライド（上書き）」することで、より分かりやすく、意図した通りのプログラムを書くことができます。

### 1\. `Object`クラスとメソッドのオーバーライド

#### 1.1 `Object`クラスって何？

明示的に他のクラスを継承していなくても、自動的に`Object`クラスのサブクラスになります。
つまり、`Object`クラスはJavaのクラス階層の「ご先祖様」のような存在です。

```java
// このように書いたつもりがなくても…
class MyClass {
    // ...
}

// Javaコンパイラは以下のように解釈しています
class MyClass extends Object {
    // ...
}
```

`Object`クラスが親であるということは、`Object`クラスが持っているメソッドは、あなたが作るどんなクラスでも利用できるということです。

#### 1.2 メソッドのオーバーライドって何？

オーバーライドとは、親クラスで定義されているメソッドを、子クラスで独自の処理に書き換えることを言います。親クラスのメソッドの基本的な機能は引き継ぎつつ、子クラス特有の振る舞いをさせたい場合に使います。

今回学ぶ`toString()`と`equals()`は、`Object`クラスですでに定義されていますが、そのまま使うと私たちの意図通りに動かないことがあります。そのため、これらをオーバーライドして、クラスごとに適切な動作をさせることが重要になります。

### 2\. `toString()`メソッドのオーバーライド

#### 2.1 `Object`クラスの`toString()`メソッドの働き

`Object`クラスに定義されている`toString()`メソッドは、そのオブジェクトの「文字列表現」を返します。しかし、デフォルトの`toString()`メソッドが返すのは、通常「クラス名@ハッシュコードの16進数表現」という、人間にとってはあまり分かりやすくない文字列です。

```java
class Book {
    String title;
    String author;

    Book(String title, String author) {
        this.title = title;
        this.author = author;
    }
}

public class Main {
    public static void main(String[] args) {
        Book book1 = new Book("Java入門", "山田太郎");
        System.out.println(book1); // book1.toString() が暗黙的に呼び出される
        // 出力例: Book@15db9742 (環境によって変わります)
    }
}
```

この出力では、`Book`オブジェクトがどんなタイトルで誰の著作なのか分かりません。

#### 2.2 なぜ`toString()`をオーバーライドするの？

`toString()`メソッドをオーバーライドする主な理由は、**オブジェクトの情報を人間にとって分かりやすい形式で表現するため**です。これは以下のような場面で非常に役立ちます。

  * **デバッグ**: `System.out.println()`などでオブジェクトの状態を確認したいとき。
  * **ロギング**: オブジェクトの情報をログファイルに出力したいとき。
  * **他のメソッドでの利用**: オブジェクトを文字列として扱いたい場合に、`toString()`が暗黙的に利用されることがあります。

#### 2.3 `toString()`をオーバーライドする方法

`toString()`メソッドをオーバーライドするには、クラス内で以下のように定義します。

  * アクセス修飾子は `public`
  * 戻り値の型は `String`
  * メソッド名は `toString`
  * 引数はなし

メソッドの前に `@Override` というアノテーションを付けることが推奨されます。これは、コンパイラに対して「このメソッドは親クラスのメソッドをオーバーライドしているつもりだ」と伝えるもので、もしタイプミスなどで正しくオーバーライドできていない場合にエラーを教えてくれます。

**コードサンプル:**

先ほどの`Book`クラスに`toString()`メソッドをオーバーライドしてみましょう。

```java
class Book {
    String title;
    String author;

    Book(String title, String author) {
        this.title = title;
        this.author = author;
    }

    // toString()メソッドをオーバーライド
    @Override
    public String toString() {
        return "書籍情報[タイトル: " + title + ", 著者: " + author + "]";
    }
}

public class Main {
    public static void main(String[] args) {
        Book book1 = new Book("Java入門", "山田太郎");
        System.out.println(book1);
        // 出力: 書籍情報[タイトル: Java入門, 著者: 山田太郎]

        Book book2 = new Book("スッキリわかるJava", "中山清喬");
        System.out.println(book2);
        // 出力: 書籍情報[タイトル: スッキリわかるJava, 著者: 中山清喬]
    }
}
```

#### 2.4 `toString()`をオーバーライドする際のポイント

  * **有用な情報を含める**: そのオブジェクトを識別するために重要なフィールドの値を含めましょう。
  * **簡潔にする**: あまりに長大な文字列を返すと、かえって読みにくくなることがあります。
  * **書式は自由**: このように表示しなければならないということはありません。一般的には `クラス名[フィールド名1=値1, フィールド名2=値2, ...]` のような形式がよく使われます。

### 3\. `equals()`メソッドのオーバーライド

#### 3.1 `Object`クラスの`equals()`メソッドの働き

`Object`クラスに定義されている`equals(Object obj)`メソッドは、引数で渡されたオブジェクト (`obj`) と自分自身が「等しい」かどうかを判定し、`true`または`false`を返します。

しかし、`Object`クラスのデフォルトの`equals()`メソッドは、**`==`演算子と同じように、2つの参照がメモリ上で同じオブジェクトを指しているかどうか**を比較します。

```java
class Point {
    int x;
    int y;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

public class Main {
    public static void main(String[] args) {
        Point p1 = new Point(10, 20);
        Point p2 = new Point(10, 20);
        Point p3 = p1; // p3はp1と同じオブジェクトを参照

        System.out.println("p1 == p2: " + (p1 == p2));         // 出力: p1 == p2: false
        System.out.println("p1.equals(p2): " + p1.equals(p2)); // 出力: p1.equals(p2): false (Objectクラスのequalsが使われるため)

        System.out.println("p1 == p3: " + (p1 == p3));         // 出力: p1 == p3: true
        System.out.println("p1.equals(p3): " + p1.equals(p3)); // 出力: p1.equals(p3): true
    }
}
```

`p1`と`p2`は、`x`座標も`y`座標も同じ値を持っていますが、メモリ上では別々の場所に作られた異なるインスタンスです。そのため、`Object`クラスの`equals()`メソッドでは`false`と判定されてしまいます。

#### 3.2 なぜ`equals()`をオーバーライドするの？

`equals()`メソッドをオーバーライドする主な理由は、**オブジェクトの「内容」や「状態」が等しいかどうかを判定するため**です。参照が異なっていても、持っているデータの値が同じであれば「等しい」とみなしたい場合にオーバーライドします。

たとえば、先ほどの`Point`クラスの例では、`x`座標と`y`座標が同じであれば、それらは「同じ点」として扱いたいです。

#### 3.3 `equals()`をオーバーライドする方法と守るべきルール (契約)

`equals()`メソッドをオーバーライドする際は、以下のルール（契約と呼ばれます）を守る必要があります。これらを守らないと、コレクション（`ArrayList`や`HashMap`など）が予期せぬ動作をすることがあります。

  * **反射性 (Reflexivity)**: `x.equals(x)` は常に `true` でなければならない (nullでない参照 `x` に対して）。
  * **対称性 (Symmetry)**: `x.equals(y)` が `true` ならば、`y.equals(x)` も `true` でなければならない。
  * **推移性 (Transitivity)**: `x.equals(y)` が `true` であり、かつ `y.equals(z)` が `true` ならば、`x.equals(z)` も `true` でなければならない。
  * **一貫性 (Consistency)**: `equals` の比較に使われる情報が変更されない限り、何度 `x.equals(y)` を呼び出しても常に同じ結果を返さなければならない。
  * **非null性 (Non-nullity)**: どんなnullでない参照 `x` に対しても、`x.equals(null)` は常に `false` でなければならない。

`equals()`メソッドをオーバーライドする一般的な手順は以下の通りです。

1.  `@Override` アノテーションを付ける。
2.  引数のオブジェクトが `null` かどうかチェックする (`obj == null`)。`null`なら `false` を返す。
3.  引数のオブジェクトが自分自身と同じインスタンスかどうかチェックする (`this == obj`)。同じなら `true` を返す。
4.  引数のオブジェクトが比較可能な型かどうかチェックする。一般的には `getClass() != obj.getClass()` や `!(obj instanceof MyClass)` を使う。型が異なれば `false` を返す。
      * `getClass()`を使うと、完全に同じクラスでないと比較できません。
      * `instanceof`を使うと、サブクラスのインスタンスも比較対象に含めることができますが、対称性が崩れる可能性も考えられるので注意してください（一般的には`getClass()`が推奨されます）。
5.  引数のオブジェクトを自分のクラス型にキャストする。
6.  オブジェクトの重要なフィールド同士を比較する。すべてのフィールドが等しければ `true` を、そうでなければ `false` を返す。
      * プリミティブ型（`int`, `double`など）は `==` で比較。
      * オブジェクト型（`String` や他のクラスのインスタンスなど）は、そのオブジェクトの `equals()` メソッドで比較する（`Objects.equals(field1, other.field1)` を使うと `null` 安全な比較ができます）。

**コードサンプル:**

`Point`クラスに`equals()`メソッドをオーバーライドしてみましょう。

```java
import java.util.Objects; // Objects.equals を使うためにインポート

class Point {
    int x;
    int y;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // equals()メソッドをオーバーライド
    @Override
    public boolean equals(Object obj) {
        // 1. 自分自身との比較
        if (this == obj) {
            return true;
        }
        // 2. nullチェックと比較対象の型チェック
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        // 3. 型キャスト
        Point other = (Point) obj;
        // 4. フィールドの比較
        return this.x == other.x && this.y == other.y;
    }

    // toString()も分かりやすくしておきましょう
    @Override
    public String toString() {
        return "Point[x=" + x + ", y=" + y + "]";
    }
}

public class Main {
    public static void main(String[] args) {
        Point p1 = new Point(10, 20);
        Point p2 = new Point(10, 20);
        Point p3 = new Point(30, 40);
        Point p4 = p1;

        System.out.println("p1: " + p1);
        System.out.println("p2: " + p2);
        System.out.println("p3: " + p3);

        System.out.println("p1.equals(p2): " + p1.equals(p2)); // 出力: p1.equals(p2): true (xとyが同じなので)
        System.out.println("p1.equals(p3): " + p1.equals(p3)); // 出力: p1.equals(p3): false (xとyが異なるので)
        System.out.println("p1.equals(p4): " + p1.equals(p4)); // 出力: p1.equals(p4): true (同じインスタンスなので)
        System.out.println("p1.equals(null): " + p1.equals(null)); // 出力: p1.equals(null): false
        System.out.println("p1.equals(\"text\"): " + p1.equals("text")); // 出力: p1.equals("text"): false (型が異なるので)
    }
}
```

これで、`x`座標と`y`座標が同じであれば、`p1`と`p2`は`equals()`メソッドによって「等しい」と判定されるようになりました。

#### 3.4 `equals()` と `hashCode()` の重要な関係

**`equals()`メソッドをオーバーライドする際には、必ず`hashCode()`メソッドもオーバーライドしなければなりません。**

**`hashCode()`メソッドとは？**

`hashCode()`メソッドは、そのオブジェクトのハッシュコード値（整数）を返します。このハッシュコードは、`HashMap`、`HashSet`、`Hashtable`といった「ハッシュベース」のコレクションでオブジェクトを効率的に格納・検索するために使われます。

**なぜ`equals()`とセットでオーバーライドするの？**

`Object`クラスの仕様では、以下の重要なルールが定められています。

  * **`equals()`メソッドで等しいと判断される2つのオブジェクトは、必ず同じ`hashCode()`値を返さなければならない。**

もし`equals()`だけをオーバーライドして`hashCode()`をオーバーライドしないと、このルールが破られる可能性もあります。その結果、`HashMap`などにオブジェクトを格納した際に、`equals()`では等しいはずのオブジェクトが別物として扱われたり、正しく検索できなくなったりする問題が発生します。

**`hashCode()`をオーバーライドする方法**

`hashCode()`の計算方法はいくつかありますが、一般的には以下の点を考慮します。

  * `equals()`の比較に使用したフィールドを使ってハッシュコードを生成する。
  * `equals()`で等しいオブジェクトが同じハッシュコードを返すようにする。
  * できるだけ異なるハッシュコードが生成されるように工夫する（ハッシュの衝突を避けるため）。

Java 7以降では、`java.util.Objects`クラスの`hash(Object... values)`メソッドを使うと、簡単に適切なハッシュコードを生成できます。

**コードサンプル (`Point`クラスに`hashCode()`を追加):**

```java
import java.util.Objects;

class Point {
    int x;
    int y;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Point point = (Point) obj;
        return x == point.x && y == point.y;
    }

    // hashCode()メソッドをオーバーライド
    @Override
    public int hashCode() {
        // equals()で比較に使ったフィールド (x と y) を使ってハッシュコードを生成
        return Objects.hash(x, y);
        // 古い書き方 (Objects.hashがない場合):
        // int result = 17;
        // result = 31 * result + x;
        // result = 31 * result + y;
        // return result;
    }

    @Override
    public String toString() {
        return "Point[x=" + x + ", y=" + y + "]";
    }
}

public class Main {
    public static void main(String[] args) {
        Point p1 = new Point(10, 20);
        Point p2 = new Point(10, 20);

        System.out.println("p1.equals(p2): " + p1.equals(p2)); // true
        System.out.println("p1.hashCode(): " + p1.hashCode());
        System.out.println("p2.hashCode(): " + p2.hashCode());
        // p1とp2がequalsでtrueなら、hashCodeも同じ値になるはず

        // HashMapなどで正しく動作するか確認 (簡単のためここでは省略)
    }
}
```

### 4\. 練習問題

ここまでの内容を理解できたか、簡単な練習問題で試してみましょう。

#### 練習問題1: `toString()`のオーバーライド

`Student`クラスがあります。このクラスには、学生の名前（`name`）と学籍番号（`studentId`）を保持するフィールドがあります。
`Student`オブジェクトを`System.out.println()`で表示したときに、「`学生:[名前], 学籍番号:[学籍番号]`」という形式で表示されるように、`toString()`メソッドをオーバーライドしてください。

```java
class Student {
    String name;
    String studentId;

    public Student(String name, String studentId) {
        this.name = name;
        this.studentId = studentId;
    }

    // TODO:ここにtoString()メソッドをオーバーライドしてください
}

public class Practice1 {
    public static void main(String[] args) {
        Student student1 = new Student("鈴木一郎", "S001");
        Student student2 = new Student("佐藤花子", "S002");

        System.out.println(student1); // 期待される出力例: 学生: 鈴木一郎, 学籍番号: S001
        System.out.println(student2); // 期待される出力例: 学生: 佐藤花子, 学籍番号: S002
    }
}
```

#### 練習問題2: `equals()`と`hashCode()`のオーバーライド

`Rectangle`（長方形）クラスがあります。このクラスには、幅（`width`）と高さ（`height`）を保持するフィールドがあります。
2つの`Rectangle`オブジェクトが、幅と高さがそれぞれ等しい場合に「等しい」と判定されるように、`equals()`メソッドをオーバーライドしてください。また、それに合わせて`hashCode()`メソッドも適切にオーバーライドしてください。

```java
// import java.util.Objects; // 必要ならインポートしてください

class Rectangle {
    int width;
    int height;

    public Rectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }

    // TODO: ここにequals()メソッドをオーバーライドしてください

    // TODO: ここにhashCode()メソッドをオーバーライドしてください

    // 確認用にtoString()も書いておくと便利です
    @Override
    public String toString() {
        return "Rectangle[width=" + width + ", height=" + height + "]";
    }
}

public class Practice2 {
    public static void main(String[] args) {
        Rectangle r1 = new Rectangle(10, 20);
        Rectangle r2 = new Rectangle(10, 20);
        Rectangle r3 = new Rectangle(20, 10);
        Rectangle r4 = r1;

        System.out.println("r1.equals(r2): " + r1.equals(r2)); // 期待: true
        System.out.println("r1.equals(r3): " + r1.equals(r3)); // 期待: false
        System.out.println("r1.equals(r4): " + r1.equals(r4)); // 期待: true
        System.out.println("r1.equals(null): " + r1.equals(null)); // 期待: false

        if (r1.equals(r2)) {
            System.out.println("r1とr2のハッシュコードが等しいか: " + (r1.hashCode() == r2.hashCode())); // 期待: true
        }
    }
}
```

### 5\. まとめ

今回は、Javaの`Object`クラスの基本的なメソッドである`toString()`と`equals()`（そして関連する`hashCode()`）のオーバーライドについて学びました。

* **`toString()`**: オブジェクトの文字列表現を分かりやすくするためにオーバーライドします。デバッグやログ出力に便利です。
* **`equals()`**: オブジェクトの内容が等しいかを比較するためにオーバーライドします。`Object`クラスのデフォルトは参照比較です。
* **`hashCode()`**: `equals()`をオーバーライドしたら、必ずセットでオーバーライドします。ハッシュベースのコレクションで正しく動作するために不可欠です。

これらのメソッドを適切にオーバーライドすることで、より意図した通りに動作するクラスを作成できます。
