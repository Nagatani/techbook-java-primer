---
title: final修飾子とイミュータブル
---
> オブジェクト指向プログラミングおよび演習1 第6回
>
> final修飾子は、代入や定義をその時点で最後にするためのものです<br>
> 状態が不変であることは、複雑なプログラミングを進める上でたくさんの恩恵があります

## final修飾子の目的

final修飾子を付けて宣言されたものは、宣言以降、代入、継承、オーバーライドなどによる変更ができないようになります。

変数の定数化の際にも使用されますが、オブジェクト自身が持つメソッドの動作などは可能であり、実際に使用する場合は注意してください。

## クラスにfinal修飾子を付けた場合

```java
public final class ClassName { ... }
```

このクラスは、インスタンス化等は可能だが、**継承** が不可能になります。
他のクラスで、`extends ClassName`とするとエラーとなります。

このクラスを継承して新たにサブクラスを作成できなくなります。

## メソッドにfinal修飾子を付けた場合
```java
public final void methodName(int hoge) { ... }
```

このメソッドは、サブクラスにて**オーバーライド**ができなくなります。

サブクラスにて独自のメソッドとして上書きして欲しくない場合に使用します。

## プリミティブ型の変数(フィールド)に付けた場合
```java
private final int INT_VALUE = 758;
```
この変数（フィールド）は、**代入**が行えなくなり、**値の変更** ができなくなります。

定数値として定義したい場合に使用します。

また、定数値として定義された変数は、すべて大文字で表現しておくと分かりやすくて良いでしょう。

## クラス型の変数(フィールド)に付けた場合
```java
private final String strValue = "hogehoge";
```

この変数（フィールド）は、**再代入** が行えなくなります。

しかし、インスタンスが持つフィールドには代入可能かつインスタンスが持つメソッドにて状態を変化させるメソッドを呼び出した際には、そのメソッドの機能に従い状態が変化します。

フィールドに対して初期値を渡した状態から再代入によって異なるものに変えてほしくない場合に使用します。


以下のソースコードを実行してみましょう

```java
public class IntValue {
    public int value;
    public add(int value) {
        this.value += value;
    }
}
public class FinalSample {
    public static void main(String[] args) {
        final IntValue i = new IntValue();
        i.value = 100;
        System.out.println(i.value);        //100と表示される
        i.add(50);
        System.out.println(i.value);        //150と表示される

        //↓これはエラー
        //i = new IntValue();
    }
}
```

```bash
100
150
```

と表示されます。

## 定数ではないので注意

変数宣言に`final`がついているからと言っても、それはオブジェクトすべてが定数になるわけではありません。

上で示したように、機能として内部の状態を変更する処理が提供されている場合はいくらでも変更が可能となります。

内部の状態を書き換える機能を持つ既存クラスのオブジェクトを定数として扱いたい場合は、いったんそれを諦め、その既存のクラスのなかで定数として必要なものだけを取り出し、列挙型（`enum`）とするなどの対策が必要です。  
定数として必要になる値などを管理するためには、設計段階からの考慮があるとよいです。


----

# イミュータブルなクラス設計のすすめ

ここから先は、まだ講義で取り扱っていない内容を多く含みます。（コレクションのSetやMap、マルチスレッドプログラミング等）

ミュータブル、イミュータブルの概念は、Javaに限らず多数のプログラミング言語で重要な考え方となるため、ここでまとめて説明してしまいます。
利用例として挙げているサンプルコードには、まだ取り扱っていない技術が使われていますが、該当技術について学んだ後に改めて確認してください。

## 安全で信頼性の高いJavaプログラミングのために

Javaプログラミングにおいて、オブジェクトの状態をどのように管理するかは、アプリケーションの品質を左右する重要な要素です。とくに「イミュータブル（不変）」なクラス設計は、コードの安全性、単純さ、そして信頼性を大幅に向上させるための強力なアプローチです。

### 1\. イミュータブル（不変性）とは何か？

イミュータブルなオブジェクトとは、一度作成された後にその内部状態（フィールドの値）が一切変更できないオブジェクトのことです。オブジェクトが持つデータは、インスタンス化の時点で決定され、その後は読み取り専用となります。

Javaの標準ライブラリには、以下のような代表的なイミュータブルクラスがあります。

* `String`
* プリミティブ型のラッパークラス（`Integer`, `Long`, `Double`, `Boolean`など）
* `java.math.BigDecimal`, `java.math.BigInteger`
* Java 8以降の`java.time`パッケージの日時クラス（`LocalDate`, `LocalDateTime`, `Duration`など）
* `java.util.Optional`
* `Collections.unmodifiableList()`などで返される変更不可能なコレクションビュー（ただし、元のコレクションが変更される可能性には注意が必要）

### 2\. なぜイミュータブルなクラス設計が必要なのか？

ミュータブル（可変）なオブジェクト、つまり状態が変更可能なオブジェクトは、一見柔軟性があるように思えますが、プログラムが複雑になるにつれて多くの問題を引き起こす可能性があります。

* 予測困難な状態変化: オブジェクトの状態がプログラムのどこからでも変更されうると、そのオブジェクトが特定の時点でどのような状態にあるかを追跡するのが難しくなります。これはバグの温床となり、デバッグを著しく困難にします。
* 意図しない副作用: ある箇所での状態変更が、プログラムのまったく異なる部分で予期せぬ影響を及ぼすことがあります。
* 並行処理の複雑化: 複数のスレッドが同じミュータブルなオブジェクトを共有する場合、データの競合状態を防ぐために複雑な同期処理が必要になります。これはパフォーマンスの低下やデッドロックのリスクを伴います。

イミュータブルなクラス設計は、これらの問題を根本から解決するために必要とされます。オブジェクトの状態が固定されることで、プログラムの振る舞いが予測しやすくなり、多くの潜在的なバグを未然に防ぐことができます。

### 3\. イミュータブルなクラス設計のメリット

イミュータブルなクラスを採用することで、以下のような大きなメリットが得られます。

1. スレッドセーフティ（Thread Safety）:
    * 状態が変わらないため、複数のスレッドから同時にアクセスされても競合が発生しません。特別な同期処理なしに、スレッド間で安全にオブジェクトを共有できます。これにより、並行処理プログラムの設計が大幅に簡素化されます。
    * 例: イミュータブルな設定情報を複数のスレッドで共有する場合、ロックなしで安全にアクセスできます。
2. 予測可能性とデバッグの容易さ:
    * オブジェクトの状態は初期化された時点から変わらないため、プログラムのどの時点でもその状態を確信できます。これにより、ロジックの追跡やバグの原因特定が容易になります。
3. 安全な共有とキャッシュ:
    * 安心してオブジェクトの参照を共有できます。メソッドに渡したり、他のオブジェクトのフィールドとして保持したりする際に、意図せず状態が変更される心配はありません。
    * 利用例のコードを後述します。
4. コードの単純化と信頼性向上:
    * 状態管理の複雑さが軽減され、コード全体がシンプルになります。副作用の心配が減るため、より堅牢で信頼性の高いプログラムを構築できます。

#### 安全な共有とキャッシュの`HashMap`キーとしての利用例

イミュータブルなオブジェクトは`hashCode()`の結果が変わらないため、`HashMap`のキーや`HashSet`の要素として最適です。

```java
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// イミュータブルなキーの例
final class PointKey {
    private final int x;
    private final int y;

    public PointKey(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointKey pointKey = (PointKey) o;
        return x == pointKey.x && y == pointKey.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y); // 状態が変わらないのでハッシュコードも不変
    }
}

public class ImmutableKeyExample {
    public static void main(String[] args) {
        Map<PointKey, String> map = new HashMap<>();
        PointKey p1 = new PointKey(10, 20);
        map.put(p1, "Value for p1");

        // p1 の状態は変更できないため、map.get(p1) は常に期待通りに動作する
        System.out.println("Value for p1: " + map.get(p1)); // "Value for p1"

        PointKey p2 = new PointKey(10, 20); // p1 と同値の新しいキー
        System.out.println("Value for p2 (equivalent to p1): " + map.get(p2)); // "Value for p1"
    }
}
```

解説: `PointKey`クラスはイミュータブルなので、一度作成されるとその`x`と`y`の値は変わりません。そのため、`hashCode()`と`equals()`の結果も常に一定であり、`HashMap`内でキーとして正しく機能します。


### 4\. ミュータブルなクラスが引き起こす典型的な失敗事例

ミュータブルな設計によって実際にどのような問題が発生するのか、具体的な失敗事例とそのコード、解説を見ていきましょう。

#### 事例1: マルチスレッド環境での競合状態 (Race Condition)

複数のスレッドが共有のミュータブルなオブジェクトに同時にアクセスし、変更しようとすると、予期せぬ結果やデータの不整合が生じます。

ミュータブルなカウンターの例（問題が発生するコード）:

```java
// MutableCounter.java
class MutableCounter {
    private int count = 0;

    public void increment() {
        // この操作はアトミックではない (読み取り、加算、書き込みの3ステップ)
        count++;
    }

    public int getCount() {
        return count;
    }
}

// RaceConditionDemo.java
public class RaceConditionDemo {
    public static void main(String[] args) throws InterruptedException {
        MutableCounter counter = new MutableCounter();
        int numberOfThreads = 10;
        int incrementsPerThread = 10000;

        Thread[] threads = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    counter.increment();
                }
            });
            threads[i].start();
        }

        for (Thread t : threads) {
            t.join(); // 全てのスレッドが終了するのを待つ
        }

        // 期待値: numberOfThreads * incrementsPerThread (例: 10 * 10000 = 100000)
        // 実際の結果は期待値より少なくなる可能性が高い
        System.out.println("Expected count: " + (numberOfThreads * incrementsPerThread));
        System.out.println("Actual count:   " + counter.getCount());
    }
}
```

解説:

* `MutableCounter`の`increment()`メソッド内の`count++`は、単一の操作に見えますが、内部的には「現在の値を読み取る」「値に1を加える」「新しい値を書き戻す」という複数のステップで構成されます。
* 複数のスレッドがこれらのステップに同時に割り込む（インターリーブする）可能性があります。たとえば、スレッドAが値を読み取った後、スレッドBも同じ古い値を読み取り、それぞれがインクリメントして書き戻すと、一方のインクリメントが失われてしまいます。
* 結果: `Actual count`は`Expected count`よりも小さくなることがほとんどです。
* 解決策の方向性:
    * `increment`メソッドを`synchronized`にする。
    * `java.util.concurrent.atomic.AtomicInteger` を使用する。
    * カウンターの状態をイミュータブルな値オブジェクトで表現し、インクリメント操作では新しい値オブジェクトを返す設計にする（関数型スタイル）。

#### 事例2: `HashSet` や `HashMap` のキーとしての不整合

ミュータブルなオブジェクトをハッシュベースのコレクションのキーとして使用し、その後にキーオブジェクトの状態を変更すると、コレクション内でそのキーを見失う可能性があります。

ミュータブルなポイントオブジェクトをキーにする例（問題が発生するコード）:

```java
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

// MutablePoint.java (ミュータブルなポイントクラス)
class MutablePoint {
    private int x;
    private int y;

    public MutablePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public void setX(int x) { this.x = x; } // 状態を変更するセッター
    public void setY(int y) { this.y = y; } // 状態を変更するセッター

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutablePoint that = (MutablePoint) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        // ハッシュコードは可変な状態(x, y)に依存する
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "MutablePoint{x=" + x + ", y=" + y + ", hashCode=" + hashCode() + "}";
    }
}

// MutableKeyInHashSetDemo.java
public class MutableKeyInHashSetDemo {
    public static void main(String[] args) {
        Set<MutablePoint> points = new HashSet<>();
        MutablePoint p1 = new MutablePoint(10, 20);

        System.out.println("p1 (before add): " + p1);
        points.add(p1);
        System.out.println("Set contains p1 (after add): " + points.contains(p1)); // true

        // p1の状態をHashSetに追加した後に変更する
        System.out.println("Modifying p1's x coordinate to 100...");
        p1.setX(100); // p1のハッシュコードが変わる！

        System.out.println("p1 (after modification): " + p1);

        // 状態変更後、HashSetはp1を正しく見つけられない
        // (ハッシュ値が変わったため、異なるバケットを探しに行ってしまう)
        System.out.println("Set contains p1 (after modification): " + points.contains(p1)); // false!

        // p1の元のハッシュ値の場所にはまだ情報が残っているように見えるが、
        // 現在のp1のハッシュ値の場所にはp1は存在しない扱いになる。
        System.out.println("Iterating over the set:");
        for(MutablePoint p : points) {
            System.out.println("  " + p + " - equals p1? " + p.equals(p1));
        }
        // もし同じオブジェクト(p1)を再度addしようとすると、
        // 新しいハッシュ値の場所に格納され、セットサイズが2になる可能性もある
        // points.add(p1);
        // System.out.println("Set size after trying to re-add: " + points.size());
    }
}
```

解説:

* `HashSet`は、オブジェクトを格納する際にその`hashCode()`を使って格納場所（バケット）を決定します。
* `MutablePoint p1`を`HashSet`に追加した後、`p1.setX(100)`によって`p1`の状態が変更されると、`p1.hashCode()`の値も変わってしまいます。
* `HashSet`は、変更後の`p1`を`contains()`で探す際、新しいハッシュ値に基づいてバケットを探しますが、`p1`は古いハッシュ値のバケットに格納されたままです（`HashSet`はキーの変更を検知しません）。
* 結果: `points.contains(p1)`が`false`を返し、あたかも`p1`がセット内に存在しないかのように振る舞います。
* 解決策: `HashMap`や`HashSet`のキーには、イミュータブルなオブジェクトを使用します。上記の「3. イミュータブルなクラス設計のメリット」で示した`PointKey`クラスのような設計が適切です。

#### 事例3: 意図しない外部からの状態変更（防御的コピーの欠如）

クラスが内部にミュータブルなオブジェクト（例: `java.util.Date`）をフィールドとして持ち、その参照をコンストラクタやゲッターでそのままやり取りすると、クラスの外部から内部状態が意図せず変更されてしまう可能性があります。

ミュータブルなDateオブジェクトを持つクラスの例（問題が発生するコード）:

```java
import java.util.Date;

// MutableEventSchedule.java (防御的コピーなし)
class MutableEventSchedule {
    private Date eventDate; // Dateはミュータブルなクラス

    public MutableEventSchedule(Date eventDate) {
        // 渡されたDateオブジェクトの参照をそのまま保持する (危険！)
        this.eventDate = eventDate;
    }

    public Date getEventDate() {
        // 内部のDateオブジェクトの参照をそのまま返す (危険！)
        return eventDate;
    }

    public void displayEventDate() {
        System.out.println("Event Date: " + eventDate + " (hashCode: " + eventDate.hashCode() + ")");
    }
}

// DefensiveCopyProblemDemo.java
public class DefensiveCopyProblemDemo {
    public static void main(String[] args) {
        // 1. コンストラクタ経由での内部状態変更
        Date originalDate = new Date(); // 現在時刻
        System.out.println("Original date (before creating schedule): " + originalDate);

        MutableEventSchedule schedule1 = new MutableEventSchedule(originalDate);
        System.out.print("Schedule1 date (initial): ");
        schedule1.displayEventDate();

        // コンストラクタに渡したoriginalDateオブジェクトの状態を変更する
        originalDate.setTime(originalDate.getTime() + 86400000L); // 1日進める
        System.out.println("Original date (after modification):   " + originalDate);

        // schedule1の内部状態も変わってしまっている！
        System.out.print("Schedule1 date (after modifying originalDate): ");
        schedule1.displayEventDate(); // 意図せず変更されている

        System.out.println("\n---------------------------------------\n");

        // 2. ゲッター経由での内部状態変更
        MutableEventSchedule schedule2 = new MutableEventSchedule(new Date()); // 新しいスケジュール
        System.out.print("Schedule2 date (initial): ");
        schedule2.displayEventDate();

        // ゲッターから取得したDateオブジェクトを変更する
        Date retrievedDate = schedule2.getEventDate();
        retrievedDate.setTime(0); // 1970-01-01 00:00:00 GMT (エポック)
        System.out.println("Retrieved date (after modification):  " + retrievedDate);


        // schedule2の内部状態も変わってしまっている！
        System.out.print("Schedule2 date (after modifying retrievedDate): ");
        schedule2.displayEventDate(); // 意図せず変更されている
    }
}
```

解説:

* `MutableEventSchedule`クラスは、コンストラクタで受け取った`Date`オブジェクトの参照をそのまま`this.eventDate`に格納しています。
* 同様に、`getEventDate()`メソッドは内部の`this.eventDate`の参照をそのまま返しています。
* 問題点1（コンストラクタ経由）: `schedule1`を作成した後、元の`originalDate`オブジェクトを変更すると、`schedule1`が保持している`eventDate`も同じオブジェクトを指しているため、一緒に変更されてしまいます。
* 問題点2（ゲッター経由）: `schedule2.getEventDate()`で取得した`Date`オブジェクト（`retrievedDate`）は、`schedule2`内部の`eventDate`と同一のインスタンスです。そのため、`retrievedDate`を変更すると、`schedule2`の内部状態が直接書き換えられてしまいます。
* 結果: クラスのカプセル化が破られ、外部から意図せずに内部状態が変更されてしまいます。これは予期せぬバグの原因となります。
* 解決策: 防御的コピーを実装します。次のセクションで詳述します。

### 5\. イミュータブルなクラスを設計するためのポイント

効果的にイミュータブルなクラスを設計するためには、以下の指針に従うことが重要です。これらのポイントを適用した総合的なイミュータブルクラスのサンプルを示します。

#### 設計ポイント:

1. クラスを`final`で宣言する:
      * これにより、クラスが継承されてメソッドがオーバーライドされ、不変性が損なわれることを防ぎます。
2. すべてのフィールドを`private final`で宣言する:
      * `private`: 外部からの直接アクセスを防ぎ、カプセル化を保証します。
      * `final`: 一度コンストラクタで初期化された後にフィールドが再代入されることを防ぎます。これは不変性の基本的な要件です。
3. セッターメソッドを提供しない:
      * フィールドの値を変更するためのメソッド（例: `setName()`, `setAge()`など）を一切公開しません。状態はコンストラクタでのみ設定されます。
4. すべてのフィールドをコンストラクタで初期化する:
      * オブジェクトの生成時にすべての状態が決定され、それ以降変更されないようにします。コンストラクタの引数で必要な値をすべて受け取り、フィールドに設定します。
5. 可変（ミュータブル）なオブジェクトをフィールドとして持つ場合は防御的コピーを行う:
      * もしフィールドとしてミュータブルなオブジェクト（例: `Date`オブジェクト、`ArrayList`などのコレクション）を参照している場合、そのオブジェクトを通じて不変性が破られる可能性も考えられます。
      * コンストラクタでの防御的コピー: コンストラクタでミュータブルな引数を受け取る際は、そのオブジェクトのディープコピーを作成して内部フィールドに格納します。元のオブジェクトへの参照をそのまま保持してはいけません。
      * ゲッターメソッドでの防御的コピー: ミュータブルな内部フィールドへのアクセサーメソッド（ゲッター）を提供する場合は、フィールドの直接の参照を返すのではなく、そのコピーを返します。または、`Collections.unmodifiableList()`のように変更不可能なビューを返すことも有効です。
6. （オプション）状態を変更する操作は、新しいインスタンスを返却する:
      * オブジェクトの状態を変更するような操作が必要な場合（たとえば、値を加算する、要素を追加するなど）現在のインスタンスを変更するのではなく、変更後の状態を持つ新しいインスタンスを生成して返します。`String`クラスの`toUpperCase()`や`substring()`メソッドがこの振る舞いをします。これを「write-on-copy」パターンと呼ぶこともあります。

#### 総合的なイミュータブルクラスのサンプルコード: `ImmutableUserProfile`

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

// 1. クラスを final で宣言
public final class ImmutableUserProfile {

    // 2. 全てのフィールドを private final で宣言
    private final String username;
    private final int age;
    private final Date registrationDate; // ミュータブルなオブジェクト
    private final List<String> hobbies;  // ミュータブルなオブジェクト

    // 4. 全てのフィールドをコンストラクタで初期化
    public ImmutableUserProfile(String username, int age, Date registrationDate, List<String> hobbies) {
        // nullチェックやバリデーションはここで行うのが適切
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }
        if (age <= 0) {
            throw new IllegalArgumentException("Age must be positive.");
        }
        if (registrationDate == null) {
            throw new IllegalArgumentException("Registration date cannot be null.");
        }
        if (hobbies == null) {
            throw new IllegalArgumentException("Hobbies list cannot be null, pass an empty list if no hobbies.");
        }

        this.username = username;
        this.age = age;

        // 5. 可変オブジェクトのコンストラクタでの防御的コピー
        this.registrationDate = new Date(registrationDate.getTime()); // Dateオブジェクトのコピーを格納

        // List<String>のディープコピー (String自体はイミュータブルなので要素のコピーは不要)
        this.hobbies = new ArrayList<>(hobbies);
    }

    // ゲッターメソッドのみを提供 (セッターはなし - ポイント3)
    public String getUsername() {
        return username;
    }

    public int getAge() {
        return age;
    }

    // 5. 可変オブジェクトのゲッターでの防御的コピー
    public Date getRegistrationDate() {
        return new Date(this.registrationDate.getTime()); // Dateオブジェクトのコピーを返す
    }

    public List<String> getHobbies() {
        // 変更不可能なリストのビューを返すか、または新しいコピーを返す
        return Collections.unmodifiableList(this.hobbies);
        // もしくは、新しいコピーを返す場合:
        // return new ArrayList<>(this.hobbies);
    }

    // 6. (オプション) 状態を変更する操作は新しいインスタンスを返す
    public ImmutableUserProfile withAge(int newAge) {
        // 他のフィールドは現在のインスタンスの値を使い、ageのみ変更した新しいインスタンスを返す
        return new ImmutableUserProfile(this.username, newAge, this.registrationDate, this.hobbies);
    }

    public ImmutableUserProfile withAddedHobby(String newHobby) {
        if (newHobby == null || newHobby.trim().isEmpty()) {
            return this; // または例外をスロー
        }
        List<String> newHobbies = new ArrayList<>(this.hobbies);
        newHobbies.add(newHobby);
        return new ImmutableUserProfile(this.username, this.age, this.registrationDate, newHobbies);
    }

    // equals と hashCode も適切に実装することが推奨される
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImmutableUserProfile that = (ImmutableUserProfile) o;
        return age == that.age &&
               Objects.equals(username, that.username) &&
               Objects.equals(registrationDate, that.registrationDate) && // Dateのequalsは適切に比較する
               Objects.equals(hobbies, that.hobbies); // Listのequalsは要素ごとに比較する
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, age, registrationDate, hobbies);
    }

    @Override
    public String toString() {
        return "ImmutableUserProfile{" +
               "username='" + username + '\'' +
               ", age=" + age +
               ", registrationDate=" + registrationDate +
               ", hobbies=" + hobbies +
               '}';
    }

    public static void main(String[] args) {
        // 初期データ
        Date regDate = new Date();
        List<String> initialHobbies = new ArrayList<>();
        initialHobbies.add("Reading");
        initialHobbies.add("Hiking");

        // イミュータブルオブジェクトの作成
        ImmutableUserProfile user1 = new ImmutableUserProfile("Alice", 30, regDate, initialHobbies);
        System.out.println("User1 (initial): " + user1);

        // --- 外部からの変更が影響しないことの確認 ---

        // 1. コンストラクタに渡した元のオブジェクトを変更
        regDate.setTime(0); // 元のDateオブジェクトを変更
        initialHobbies.add("Swimming"); // 元のListオブジェクトを変更

        System.out.println("User1 (after modifying original inputs): " + user1);
        // user1 の registrationDate と hobbies は変更されていない (防御的コピーのおかげ)
        System.out.println("User1's registration date from getter: " + user1.getRegistrationDate());
        System.out.println("User1's hobbies from getter: " + user1.getHobbies());


        // 2. ゲッターから取得したオブジェクトを変更しようとする
        Date user1RegDate = user1.getRegistrationDate();
        user1RegDate.setTime(System.currentTimeMillis() + 100000); // 取得したコピーを変更

        List<String> user1Hobbies = user1.getHobbies();
        try {
            user1Hobbies.add("Cycling"); // Collections.unmodifiableList() であれば UnsupportedOperationException
        } catch (UnsupportedOperationException e) {
            System.out.println("Error trying to modify hobbies list: " + e.getMessage());
        }

        System.out.println("User1 (after trying to modify getter results): " + user1);
        // user1 の registrationDate と hobbies は変更されていない (ゲッターでの防御的コピーのおかげ)
        System.out.println("User1's registration date (final check): " + user1.getRegistrationDate());
        System.out.println("User1's hobbies (final check): " + user1.getHobbies());

        // --- 新しいインスタンスを生成するメソッドの利用 ---
        ImmutableUserProfile user2 = user1.withAge(31);
        System.out.println("User1 (original): " + user1); // user1 は変更されない
        System.out.println("User2 (age modified): " + user2);

        ImmutableUserProfile user3 = user1.withAddedHobby("Photography");
        System.out.println("User1 (original): " + user1); // user1 は変更されない
        System.out.println("User3 (hobby added): " + user3);
        System.out.println("User1's hobbies again: " + user1.getHobbies()); // user1 の hobbies は元のまま
    }
}
```

`ImmutableUserProfile`の解説:

* クラス宣言 (`final`): ポイント1に該当。これにより、サブクラスで不変性が破られることを防ぎます。
* フィールド宣言 (`private final`): ポイント2に該当。すべてのフィールドは外部から隠蔽され、一度初期化されると再代入できません。
* セッターなし: ポイント3に該当。状態を変更する手段を提供しません。
* コンストラクタ: ポイント4に該当。すべてのフィールドはコンストラクタを通じて初期化されます。
    * `registrationDate`の防御的コピー: `new Date(registrationDate.getTime())` により、渡された`Date`オブジェクトのコピーを内部に保存します。これにより、コンストラクタ呼び出し後に元の`Date`オブジェクトが変更されても、`ImmutableUserProfile`インスタンスの`registrationDate`は影響を受けません。
    * `hobbies`の防御的コピー: `new ArrayList<>(hobbies)` により、渡されたリストの新しいコピーを作成して内部に保存します。リストの要素である`String`はイミュータブルなので、要素自体のディープコピーは不要です。
* ゲッターメソッド:
    * `getRegistrationDate()`: `new Date(this.registrationDate.getTime())` により、内部の`Date`オブジェクトのコピーを返します。これにより、ゲッターから取得した`Date`オブジェクトを変更しても、内部状態は保護されます。
    * `getHobbies()`: `Collections.unmodifiableList(this.hobbies)` により、変更不可能なリストのビューを返します。このリストに対して追加や削除の操作を行おうとすると`UnsupportedOperationException`がスローされます。あるいは、`new ArrayList<>(this.hobbies)` として新しいコピーを返すことも可能です。
* `withAge()`, `withAddedHobby()` メソッド: ポイント6に該当。これらのメソッドは現在のインスタンスの状態を変更せず、変更された値を持つ新しい`ImmutableUserProfile`インスタンスを返します。これはイミュータブルオブジェクトで状態遷移を表現する一般的な方法です。
* `equals()` と `hashCode()`: イミュータブルオブジェクトは値オブジェクトとしての性質が強いため、これらのメソッドを適切にオーバーライドすることが重要です。状態に基づいて等価性を判断し、一貫したハッシュコードを返すようにします。
* `main`メソッドのデモンストレーション: 防御的コピーが正しく機能し、外部からの変更の影響を受けないこと、および`with...`メソッドが新しいインスタンスを返すことを示しています。

### 6\. まとめ

イミュータブルなクラス設計は、Javaアプリケーションの品質を向上させるための非常に効果的な戦略です。
状態が変わらないという単純な原則が、スレッドセーフティ、予測可能性、デバッグの容易さ、そしてコード全体の堅牢性といった多くの恩恵をもたらします。

ミュータブルな状態が引き起こす潜在的な問題を理解し、上記で示したイミュータブルな設計のポイント（とくに防御的コピーの重要性）を実践することで、より信頼性が高く、保守しやすいコードを書くことができるようになります。新しいクラスを設計する際には、まずイミュータブルにできないかを検討し、イミュータブルにするメリットを最大限に活用することをオススメします。



---

# 練習課題

この練習課題は、現時点の講義内容で取り組むことができる内容にしています。
提出の必要はありません。解答例も後述しています。

## イミュータブルな「レシピ」クラスの作成

### 課題の目的:

* `final`修飾子の適切な使用方法を理解する。
* プリミティブ型および参照型（とくに配列）フィールドを持つイミュータブルなクラスを設計・実装する。
* 防御的コピーの重要性と実装方法を理解する。


### 課題内容:

料理のレシピ情報を保持するイミュータブルなクラス `ImmutableRecipe` を作成してください。このクラスは以下の仕様を満たすものとします。

#### 1.`ImmutableRecipe` クラスの仕様:

* フィールド:
    * `recipeName`（String型）: レシピ名。一度設定したら変更不可。
    * `preparationTimeMinutes`（int型）: 準備にかかる時間（分）。一度設定したら変更不可。
    * `ingredients`（String配列型）: 材料のリスト。一度設定したら、配列の参照先も、配列の要素も変更不可（クラスの外部から）。
* コンストラクタ:
    * `recipeName`, `preparationTimeMinutes`, `ingredients` を引数に取り、各フィールドを初期化します。
    * `ingredients` 配列に対しては、外部からの変更を防ぐために防御的コピーを行ってください。
* メソッド:
    * 各フィールドに対応するゲッターメソッド (`getRecipeName()`, `getPreparationTimeMinutes()`, `getIngredients()`) を作成してください。
    * `getIngredients()` メソッドは、内部の配列の防御的コピーを返却するようにしてください。
    * セッターメソッドは作成しないでください。
    * レシピ情報を整形して表示する `displayRecipeDetails()` メソッドを作成してください（任意）。
* その他:
    * クラス自体が継承されないようにしてください。

#### 2.`RecipeTest` メインクラスの作成と動作確認:

作成した `ImmutableRecipe` クラスをテストするための `RecipeTest` クラスを作成し、以下のシナリオで動作確認を行ってください。実行結果から、`ImmutableRecipe` が正しくイミュータブルに動作していることを確認できるようにします。

* シナリオ1: オブジェクト作成と初期値の確認
    1.  材料の配列 `String[] initialIngredients = {"卵", "牛乳", "砂糖"};` を準備します。
    2.  `ImmutableRecipe` オブジェクト `recipe1` を `"フレンチトースト"`, `15`, `initialIngredients` で作成します。
    3.  `recipe1` の各ゲッターを使って値を取得し、正しく設定されていることをコンソールに出力して確認します。

* シナリオ2: コンストラクタに渡した元の配列の変更テスト
    1.  `initialIngredients` 配列の最初の要素を `"高級卵"` に変更します。
    2.  `recipe1.getIngredients()` を呼び出し、`recipe1` 内部の材料リストが変更されていないことをコンソールに出力して確認します（防御的コピーが正しく行われていれば変更されないはずです）。

* シナリオ3: ゲッターから取得した配列の変更テスト
    1.  `String[] ingredientsFromGetter = recipe1.getIngredients();` として材料リストを取得します。
    2.  `ingredientsFromGetter` 配列の2番目の要素を `"豆乳"` に変更します。
    3.  再度 `recipe1.getIngredients()` を呼び出し、`recipe1` 内部の材料リストが変更されていないことをコンソールに出力して確認します（ゲッターでの防御的コピーが正しく行われていれば変更されないはずです）。

* シナリオ4: 材料を追加するメソッドのテスト
    * `ImmutableRecipe` クラスに、新しい材料を既存の材料リストに追加した新しい `ImmutableRecipe` オブジェクトを返す `addIngredient(String newIngredient)` メソッドを実装してみましょう。
    * このメソッドを呼び出しても、元の `ImmutableRecipe` オブジェクトは変更されないことを確認します。

### 期待される実行結果のポイント:

コンソール出力により、以下の点が明確に示されるようにしてください。

* `ImmutableRecipe` オブジェクト作成後の初期状態。
* 元の `initialIngredients` 配列を変更しても、`recipe1` の材料リストは影響を受けないこと。
* `recipe1` から取得した材料リストのコピーを変更しても、`recipe1` の材料リストは影響を受けないこと。

### ヒント:

* 配列のコピーには `System.arraycopy()` や `Arrays.copyOf()`、またはループを使った手動コピーが利用できます。
* 配列の内容を簡単に出力するには `java.util.Arrays.toString()` メソッドが便利です。

-----

### 解答例

<details><summary>クリックで表示する</summary>
<p>

#### `ImmutableRecipe.java`

```java
import java.util.Arrays; // Arrays.copyOf と Arrays.toString を使用するため

// 課題の仕様: クラス自体が継承されないように final で宣言
public final class ImmutableRecipe {

    // 課題の仕様: recipeName (String型): レシピ名。一度設定したら変更不可。
    // private final で宣言し、外部からの直接アクセスと再代入を防ぐ
    private final String recipeName;

    // 課題の仕様: preparationTimeMinutes (int型): 準備にかかる時間（分）。一度設定したら変更不可。
    // private final で宣言
    private final int preparationTimeMinutes;

    // 課題の仕様: ingredients (String配列型): 材料のリスト。
    // private final で宣言。配列の参照先は変更不可になるが、配列の要素自体は可変なので注意が必要。
    private final String[] ingredients;

    // 課題の仕様: コンストラクタ
    public ImmutableRecipe(String recipeName, int preparationTimeMinutes, String[] ingredients) {
        // nullチェックやバリデーションを行うのが望ましいが、課題の主眼ではないため省略
        this.recipeName = recipeName;
        this.preparationTimeMinutes = preparationTimeMinutes;

        // 課題の仕様: ingredients 配列に対しては、外部からの変更を防ぐために防御的コピーを行う。
        // 渡された配列のコピーを内部状態として保持する。
        // これにより、コンストラクタ呼び出し後に元の配列が変更されても、このオブジェクトの内部状態は影響を受けない。
        if (ingredients != null) {
            this.ingredients = Arrays.copyOf(ingredients, ingredients.length);
        } else {
            // ingredientsがnullの場合、空の配列を割り当てるか、例外をスローするかなどの設計判断が必要。
            // ここでは空の配列を割り当てる。
            this.ingredients = new String[0];
        }
    }

    // 課題の仕様: ゲッターメソッド (recipeName)
    public String getRecipeName() {
        return recipeName;
    }

    // 課題の仕様: ゲッターメソッド (preparationTimeMinutes)
    public int getPreparationTimeMinutes() {
        return preparationTimeMinutes;
    }

    // 課題の仕様: ゲッターメソッド (ingredients)
    // 内部の配列の防御的コピーを返却する。
    // これにより、このメソッドの呼び出し元が返された配列を変更しても、
    // このオブジェクトの内部状態は影響を受けない。
    public String[] getIngredients() {
        // ingredientsがnullでないことを前提とする（コンストラクタで初期化済みのため）
        return Arrays.copyOf(this.ingredients, this.ingredients.length);
    }

    // 課題の仕様: レシピ情報を整形して表示する displayRecipeDetails() メソッド（任意）
    public void displayRecipeDetails() {
        System.out.println("レシピ名: " + recipeName);
        System.out.println("準備時間: " + preparationTimeMinutes + "分");
        System.out.println("材料: " + Arrays.toString(ingredients)); // 配列の内容を文字列で表示
    }

    // (オプション課題) 材料を追加するメソッド
    // このメソッドは現在のオブジェクトを変更せず、新しい材料が追加された「新しい」ImmutableRecipeオブジェクトを返す。
    public ImmutableRecipe addIngredient(String newIngredient) {
        if (newIngredient == null || newIngredient.trim().isEmpty()) {
            // 新しい材料が無効な場合は、現在のインスタンスをそのまま返すか、例外をスローする。
            // ここでは現在のインスタンスを返す。
            return this;
        }

        // 現在の材料リストのコピーを作成
        String[] currentIngredients = this.getIngredients(); // 防御的コピーされたものを取得
        String[] newIngredientsArray = Arrays.copyOf(currentIngredients, currentIngredients.length + 1);
        newIngredientsArray[newIngredientsArray.length - 1] = newIngredient;

        // 新しい材料リストを持つ新しい ImmutableRecipe オブジェクトを生成して返す
        return new ImmutableRecipe(this.recipeName, this.preparationTimeMinutes, newIngredientsArray);
    }

    // toStringメソッドをオーバーライドすると、オブジェクトの内容をデバッグなどで確認しやすくなる（推奨）
    @Override
    public String toString() {
        return "ImmutableRecipe{" +
               "recipeName='" + recipeName + '\'' +
               ", preparationTimeMinutes=" + preparationTimeMinutes +
               ", ingredients=" + Arrays.toString(ingredients) +
               '}';
    }

    // イミュータブルオブジェクトでは、equalsとhashCodeも適切に実装することが推奨されるが、
    // この課題の主要な学習ポイントではないため、ここでは省略する。
    // もし実装する場合、フィールドの値に基づいて等価性を判断し、一貫したハッシュコードを返すようにする。
}
```

#### `RecipeTest.java`

```java
import java.util.Arrays; // Arrays.toString を使用するため

public class RecipeTest {

    public static void main(String[] args) {
        System.out.println("--- シナリオ1: オブジェクト作成と初期値の確認 ---");
        // 1. 材料の配列を準備
        String[] initialIngredients = {"卵", "牛乳", "砂糖"};
        System.out.println("初期材料 (作成前): " + Arrays.toString(initialIngredients));

        // 2. ImmutableRecipe オブジェクト recipe1 を作成
        ImmutableRecipe recipe1 = new ImmutableRecipe("フレンチトースト", 15, initialIngredients);

        // 3. recipe1 の各ゲッターを使って値を取得し、正しく設定されていることを確認
        System.out.println("Recipe1 名前: " + recipe1.getRecipeName());
        System.out.println("Recipe1 準備時間: " + recipe1.getPreparationTimeMinutes() + "分");
        System.out.println("Recipe1 材料 (ゲッター経由): " + Arrays.toString(recipe1.getIngredients()));
        // recipe1.displayRecipeDetails(); // displayRecipeDetailsメソッドで確認も可能

        System.out.println("\n--- シナリオ2: コンストラクタに渡した元の配列の変更テスト ---");
        // 1. initialIngredients 配列の最初の要素を変更
        System.out.println("元の初期材料配列を変更前: initialIngredients[0] = " + initialIngredients[0]);
        initialIngredients[0] = "高級卵";
        System.out.println("元の初期材料配列を変更後: initialIngredients[0] = " + initialIngredients[0]);
        System.out.println("変更後の initialIngredients 全体: " + Arrays.toString(initialIngredients));

        // 2. recipe1 内部の材料リストが変更されていないことを確認
        // (コンストラクタでの防御的コピーが正しければ、recipe1の中身は影響を受けない)
        System.out.println("Recipe1 材料 (元の配列変更後、ゲッター経由): " + Arrays.toString(recipe1.getIngredients()));
        // 想定結果: {"卵", "牛乳", "砂糖"} のまま

        System.out.println("\n--- シナリオ3: ゲッターから取得した配列の変更テスト ---");
        // 1. ゲッターから材料リストを取得
        String[] ingredientsFromGetter = recipe1.getIngredients();
        System.out.println("ゲッターから取得した材料配列 (変更前): " + Arrays.toString(ingredientsFromGetter));

        // 2. 取得した配列の2番目の要素を変更
        if (ingredientsFromGetter.length > 1) {
            ingredientsFromGetter[1] = "豆乳";
            System.out.println("ゲッターから取得した材料配列 (変更後): " + Arrays.toString(ingredientsFromGetter));
        } else {
            System.out.println("ゲッターから取得した配列の要素数が少なく、変更テストをスキップしました。");
        }

        // 3. 再度 recipe1 内部の材料リストが変更されていないことを確認
        // (ゲッターでの防御的コピーが正しければ、recipe1の中身は影響を受けない)
        System.out.println("Recipe1 材料 (ゲッター配列変更後、再度ゲッター経由): " + Arrays.toString(recipe1.getIngredients()));
        // 想定結果: {"卵", "牛乳", "砂糖"} のまま

        System.out.println("\n--- (オプション) シナリオ4: 材料を追加するメソッドのテスト ---");
        System.out.println("Recipe1 (変更前): " + recipe1.toString()); // toString()で確認
        String newIngredient = "バニラエッセンス";
        System.out.println("追加する材料: " + newIngredient);

        // addIngredient メソッドを呼び出し、新しい ImmutableRecipe オブジェクトを取得
        ImmutableRecipe recipe2 = recipe1.addIngredient(newIngredient);

        System.out.println("Recipe1 (addIngredient呼び出し後、元のオブジェクト): " + recipe1.toString());
        // 想定結果: recipe1 は変更されていない
        System.out.println("Recipe1 材料 (addIngredient呼び出し後、元のオブジェクトのゲッター経由): " + Arrays.toString(recipe1.getIngredients()));


        System.out.println("Recipe2 (addIngredientで生成された新しいオブジェクト): " + recipe2.toString());
        // 想定結果: recipe2 には新しい材料が追加されている
        System.out.println("Recipe2 材料 (新しいオブジェクトのゲッター経由): " + Arrays.toString(recipe2.getIngredients()));
    }
}
```

</p>
</details>