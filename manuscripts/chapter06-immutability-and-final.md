# <b>6章</b> <span>不変性とfinalキーワード</span> <small>安全で予測可能なコードの設計</small>

## 本章の学習目標

### この章で学ぶこと

1. 不変性の概念理解
    - 不変オブジェクトの定義と重要性、スレッドセーフティとの関係、実践的メリット
2. finalキーワードの活用
    - final変数・メソッド・クラスの使い分けと適切な使用場面
3. 不変オブジェクトの設計
    - 設計ルール、防御的コピー、ビルダーパターンの活用
4. 実践的な実装技術
    - 可変オブジェクトの安全な扱い、パフォーマンスとのトレードオフ

### この章を始める前に

第4章のカプセル化と第5章の継承を理解していれば準備完了です。

## 不変性の概念と重要性

第4章でカプセル化について学び、第5章で継承の実践的な活用法を習得しました。本章では、オブジェクトの状態を変更できないようにする「不変性（Immutability）」という設計概念と、それを実現するための`final`キーワードについて学習します。

不変性は、一度作成されたオブジェクトの状態が変更できないという性質です。この概念は、バグの少ない堅牢なプログラムを作成するうえで非常に重要な役割を果たします。

### なぜ不変性が重要なのか

可変オブジェクトは、プログラムの複雑性を増大させ、予期しないバグの原因となることがあります。とくに以下のような状況で問題が顕著になります。

#### 1. マルチスレッド環境での競合状態

複数のスレッドが同じ可変オブジェクトのフィールドを同時に更新する場合の競合状態を示します。MutablePointクラスのsetterメソッドは同期化されていないため、複数のスレッドが同時にアクセスすると、期待した結果が得られません。

<span class="listing-number">**サンプルコード6-1**</span>

```java
// 可変オブジェクトの問題例
public class MutablePoint {
    private int x;
    private int y;
    
    public MutablePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public int getX() { return x; }
    public int getY() { return y; }
}

// マルチスレッド環境での競合状態の例
public class ThreadSafetyProblem {
    public static void main(String[] args) {
        MutablePoint point = new MutablePoint(0, 0);
        
        // 複数のスレッドが同じオブジェクトを変更
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                point.setX(point.getX() + 1);
            }
        });
        
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                point.setX(point.getX() + 1);
            }
        });
        
        t1.start();
        t2.start();
        
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            // スレッドが中断された場合の処理
            Thread.currentThread().interrupt();
            System.err.println("スレッドの待機が中断されました");
        }
        
        // 期待値: 2000, 実際の値: 予測不可能（1000～2000の間）
        System.out.println("X座標: " + point.getX());
    }
}
```
実行結果（実行毎に異なる）：
```
X座標: 1328
```

#### 2. 意図しない副作用

以下のコードは、可変オブジェクトの参照を直接返すことで発生する副作用の問題を示しています。getLocationメソッドが内部状態への参照を返すため、呼び出し側が意図せずEmployeeオブジェクトの内部状態を変更できてしまいます。これはカプセル化の破壊につながる深刻な問題です。

<span class="listing-number">**サンプルコード6-2**</span>

```java
// 可変オブジェクトを返すメソッドの問題
public class Employee {
    private MutablePoint location;
    
    public MutablePoint getLocation() {
        return location;  // 可変オブジェクトへの参照を返す
    }
}

// 呼び出し側のコード
Employee emp = new Employee();
MutablePoint loc = emp.getLocation();
loc.setX(100);  // Employeeの内部状態が外部から変更される！
```

### 不変オブジェクトによる解決

これらの問題を解決するのが、不変オブジェクトです。

<span class="listing-number">**サンプルコード6-3**</span>

```java
// 不変オブジェクトの実装
public final class ImmutablePoint {
    private final int x;
    private final int y;
    
    public ImmutablePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    
    // setterメソッドは存在しない
    
    // 値を変更したい場合は新しいオブジェクトを作成
    public ImmutablePoint withX(int newX) {
        return new ImmutablePoint(newX, this.y);
    }
    
    public ImmutablePoint withY(int newY) {
        return new ImmutablePoint(this.x, newY);
    }
}
```

不変オブジェクトの利点。

1. スレッドセーフティ
    + 状態が変更されないため、同期化なしで安全に共有できる
2. 予測可能性
    + 一度作成されたオブジェクトの状態は変わらない
3. キャッシュ可能
    + 状態が変わらないため、安全にキャッシュできる
4. デバッグの容易さ
    + 状態変更のタイミングを追跡する必要がない

## finalキーワードの3つの用途

`final`キーワードは、Javaで「変更不可」を表現するための重要な機能です。`final`は以下の3つの場所で使用でき、それぞれ異なる意味を持ちます。

### final変数（定数と不変フィールド）

`final`を変数に付けると、その変数は一度だけ初期化でき、その後は変更できなくなります。

<span class="listing-number">**サンプルコード6-4**</span>

```java
import java.time.LocalDateTime;
import java.util.List;

public class Constants {
    // クラス定数（static final）
    public static final double PI = 3.14159265359;
    public static final int MAX_CONNECTIONS = 100;
    
    // インスタンス定数（finalフィールド）
    private final String id;
    private final LocalDateTime createdAt;
    
    public Constants(String id) {
        this.id = id;
        this.createdAt = LocalDateTime.now();  // コンストラクタで初期化
    }
    
    // finalローカル変数
    public void processData(List<String> data) {
        final int size = data.size();
        // size = 10;  // コンパイルエラー： final変数は変更不可
        
        data.forEach(item -> {
            // ラムダ式内でローカル変数を使う場合、実質的にfinalである必要がある
            System.out.println(item + " (total: " + size + ")");
        });
    }
}
```

#### final変数の初期化タイミング
- 宣言時
-    + `final int x = 10;`
- コンストラクタ内
-    + インスタンスフィールドの場合
- インスタンス初期化子
-    + インスタンスフィールドの場合
- static初期化子
-    + static finalフィールドの場合

### finalメソッド（オーバーライドの禁止）

`final`をメソッドに付けると、サブクラスでそのメソッドをオーバーライドできなくなります。

<span class="listing-number">**サンプルコード6-5**</span>

```java
public class SecurityManager {
    // テンプレートメソッドパターンでfinalを使用
    public final boolean authenticate(String username, String password) {
        // 認証の基本フローは変更させない
        if (!validateInput(username, password)) {
            return false;
        }
        
        // 具体的な認証方法はサブクラスで実装
        return doAuthenticate(username, password);
    }
    
    // サブクラスでオーバーライド可能
    protected boolean doAuthenticate(String username, String password) {
        // デフォルト実装
        return false;
    }
    
    private boolean validateInput(String username, String password) {
        return username != null && !username.isEmpty() 
            && password != null && !password.isEmpty();
    }
}
```

### finalクラス（継承の禁止）

`final`をクラスに付けると、そのクラスを継承できなくなります。

<span class="listing-number">**サンプルコード6-6**</span>

```java
// Stringクラスのfinalクラスの代表例
public final class String {
    // Stringクラスは継承できない
}

// 不変クラスは通常finalにする
public final class ImmutablePerson {
    private final String name;
    private final int age;
    
    public ImmutablePerson(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    public String getName() { return name; }
    public int getAge() { return age; }
}

// 以下はコンパイルエラー
// class ExtendedPerson extends ImmutablePerson { }
```

finalクラスの利点。
1. セキュリティ
    + 悪意のあるサブクラスによる動作の改変を防ぐ
2. パフォーマンス
    + JVMが最適化しやすい
3. 設計の明確化
    + このクラスは継承を意図していないことを明示

## 不変オブジェクトの設計パターン

不変オブジェクトを正しく設計するには、いくつかの重要なルールを守る必要があります。

### 不変クラスの基本ルール

1. クラスをfinalにする
    + 継承を防ぐ
2. すべてのフィールドをprivate finalにする
    + 外部からの直接アクセスと変更を防ぐ
3. setterメソッドを提供しない
    + 状態変更の手段を提供しない
4. コンストラクタで初期化を完了する
    + すべてのフィールドをコンストラクタで設定
5. 防御的コピーを行う
    + 可変オブジェクトをフィールドに持つ場合

### 実践例：不変な`Person`クラス

<span class="listing-number">**サンプルコード6-7**</span>

```java
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// 完全な不変クラスの例
public final class ImmutablePerson {
    private final String name;
    private final int age;
    private final List<String> hobbies;
    
    public ImmutablePerson(String name, int age, List<String> hobbies) {
        // パラメータの検証
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("名前は必須です");
        }
        if (age < 0 || age > 150) {
            throw new IllegalArgumentException("年齢は0～150の範囲である必要があります");
        }
        
        this.name = name.trim();
        this.age = age;
        // 防御的コピー：外部のリストが変更されても影響を受けない
        this.hobbies = hobbies != null 
            ? new ArrayList<>(hobbies) 
            : new ArrayList<>();
    }
    
    // getterメソッドのみ提供（setterは存在しない）
    public String getName() {
        return name;
    }
    
    public int getAge() {
        return age;
    }
    
    public List<String> getHobbies() {
        // 防御的コピー：内部リストへの参照を返さない
        return new ArrayList<>(hobbies);
    }
    
    // 値を変更したい場合は新しいオブジェクトを作成
    public ImmutablePerson withAge(int newAge) {
        return new ImmutablePerson(this.name, newAge, this.hobbies);
    }
    
    public ImmutablePerson addHobby(String hobby) {
        List<String> newHobbies = new ArrayList<>(this.hobbies);
        newHobbies.add(hobby);
        return new ImmutablePerson(this.name, this.age, newHobbies);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ImmutablePerson that = (ImmutablePerson) obj;
        return age == that.age && 
               Objects.equals(name, that.name) && 
               Objects.equals(hobbies, that.hobbies);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, age, hobbies);
    }
    
    @Override
    public String toString() {
        return "ImmutablePerson{name='" + name + "', age=" + age + 
               ", hobbies=" + hobbies + "}";
    }
}
```

### 不変オブジェクトの使用例

<span class="listing-number">**サンプルコード6-8**</span>

```java
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImmutableExample {
    public static void main(String[] args) {
        // 不変オブジェクトの作成
        List<String> hobbies = Arrays.asList("読書", "映画鑑賞");
        ImmutablePerson person = new ImmutablePerson("田中太郎", 25, hobbies);
        
        // 元のリストを変更してもオブジェクトには影響しない
        hobbies = new ArrayList<>(hobbies);
        hobbies.add("スポーツ");
        System.out.println(person.getHobbies()); // [読書, 映画鑑賞]
        
        // 値を変更したい場合は新しいオブジェクトを作成
        ImmutablePerson olderPerson = person.withAge(26);
        System.out.println("元の人: " + person); // age=25
        System.out.println("新しい人: " + olderPerson); // age=26
        
        // 趣味を追加
        ImmutablePerson personWithNewHobby = person.addHobby("プログラミング");
        System.out.println("元の趣味: " + person.getHobbies());
        System.out.println("新しい趣味: " + personWithNewHobby.getHobbies());
        
        // マルチスレッド環境での安全な共有
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                // 不変オブジェクトはスレッドセーフ
                System.out.println("T1: " + person.getName());
            }
        });
        
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                System.out.println("T2: " + person.getAge());
            }
        });
        
        t1.start();
        t2.start();
    }
}
```
実行結果：
```
[読書, 映画鑑賞]
元の人: ImmutablePerson{name='田中太郎', age=25, hobbies=[読書, 映画鑑賞]}
新しい人: ImmutablePerson{name='田中太郎', age=26, hobbies=[読書, 映画鑑賞]}
元の趣味: [読書, 映画鑑賞]
新しい趣味: [読書, 映画鑑賞, プログラミング]
```

#### 不変オブジェクトの重要な特性

1. スレッドセーフティ
    + 同期化なしで複数のスレッドから安全にアクセス可能
2. 予測可能性
    + オブジェクトの状態が変わらないためデバッグが容易
3. キャッシュ可能
    + 状態が変わらないためHashMapのキーとして安全に使用可能
4. 関数型プログラミングとの親和性
    + 副作用のないコードが書きやすい

## 防御的コピーとビルダーパターン

### 防御的コピーの重要性

不変オブジェクトを設計する際、可変オブジェクト（配列やコレクション）をフィールドに持つ場合はとくに注意が必要です。単に参照を保持するだけでは、外部から内容を変更される可能性があります。

<span class="listing-number">**サンプルコード6-9**</span>

```java
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// 防御的コピーの実装例
public final class DateRange {
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final List<LocalDate> holidays;
    
    public DateRange(LocalDate start, LocalDate end, List<LocalDate> holidays) {
        // パラメータの検証
        if (start == null || end == null) {
            throw new IllegalArgumentException("日付はnullにできません");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("開始日は終了日より前である必要があります");
        }
        
        this.startDate = start;  // LocalDateは不変なのでコピー不要
        this.endDate = end;
        
        // 防御的コピー：外部のリストが変更されても影響を受けない
        this.holidays = holidays != null 
            ? new ArrayList<>(holidays) 
            : new ArrayList<>();
    }
    
    public List<LocalDate> getHolidays() {
        // 防御的コピー：内部リストの参照を返さない
        return new ArrayList<>(holidays);
    }
    
    // 配列の場合の防御的コピー
    public LocalDate[] getHolidaysAsArray() {
        return holidays.toArray(new LocalDate[0]);
    }
}
```
### ビルダーパターンによる不変オブジェクトの構築

複雑な不変オブジェクトを構築する際、コンストラクタの引数が多くなりすぎる問題があります。ビルダーパターンはこの問題を解決します。

<span class="listing-number">**サンプルコード6-10**</span>

```java
import java.time.LocalDate;

// ビルダーパターンを使った不変オブジェクトの構築
public final class Book {
    private final String isbn;
    private final String title;
    private final String author;
    private final String publisher;
    private final int publicationYear;
    private final int pages;
    private final String genre;
    
    // プライベートコンストラクタ
    private Book(Builder builder) {
        this.isbn = builder.isbn;
        this.title = builder.title;
        this.author = builder.author;
        this.publisher = builder.publisher;
        this.publicationYear = builder.publicationYear;
        this.pages = builder.pages;
        this.genre = builder.genre;
    }
    
    // ビルダークラス
    public static class Builder {
        // 必須パラメータ
        private final String isbn;
        private final String title;
        
        // オプションパラメータ（デフォルト値を設定）
        private String author = "Unknown";
        private String publisher = "";
        private int publicationYear = 0;
        private int pages = 0;
        private String genre = "General";
        
        // 必須パラメータのコンストラクタ
        public Builder(String isbn, String title) {
            if (isbn == null || isbn.trim().isEmpty()) {
                throw new IllegalArgumentException("ISBNは必須です");
            }
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("タイトルは必須です");
            }
            this.isbn = isbn;
            this.title = title;
        }
        
        // オプションパラメータのセッター（メソッドチェーン）
        public Builder author(String author) {
            this.author = author;
            return this;
        }
        
        public Builder publisher(String publisher) {
            this.publisher = publisher;
            return this;
        }
        
        public Builder publicationYear(int year) {
            if (year < 0 || year > LocalDate.now().getYear()) {
                throw new IllegalArgumentException("無効な出版年です");
            }
            this.publicationYear = year;
            return this;
        }
        
        public Builder pages(int pages) {
            if (pages < 0) {
                throw new IllegalArgumentException("ページ数は正の値である必要があります");
            }
            this.pages = pages;
            return this;
        }
        
        public Builder genre(String genre) {
            this.genre = genre;
            return this;
        }
        
        // Bookオブジェクトの構築
        public Book build() {
            return new Book(this);
        }
    }
    // getterメソッド（省略）
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    // ... 他のgetterメソッド
}
```

### ビルダーパターンの使用例

<span class="listing-number">**サンプルコード6-11**</span>

```java
public class BuilderExample {
    public static void main(String[] args) {
        // ビルダーパターンによる不変オブジェクトの構築
        Book book1 = new Book.Builder("978-4-123456-78-9", "Javaプログラミング入門")
            .author("山田太郎")
            .publisher("技術評論社")
            .publicationYear(2024)
            .pages(350)
            .genre("プログラミング")
            .build();
        
        // 必須パラメータのみで構築
        Book book2 = new Book.Builder("978-4-987654-32-1", "データベース設計")
            .build();
        
        // 一部のオプションパラメータを設定
        Book book3 = new Book.Builder("978-4-111111-11-1", "アルゴリズム入門")
            .author("鈴木花子")
            .pages(280)
            .build();
        
        // メソッドチェーンによる可読性の高いコード
        Book textbook = new Book.Builder("978-4-222222-22-2", "オブジェクト指向設計")
            .author("佐藤一郎")
            .publisher("オライリー・ジャパン")
            .publicationYear(2023)
            .pages(450)
            .genre("ソフトウェア工学")
            .build();
    }
}
```
実行結果：
```
Book 1: Javaプログラミング入門 by 山田太郎
Book 2: データベース設計 by Unknown
Book 3: アルゴリズム入門 by 鈴木花子
Textbook: オブジェクト指向設計 by 佐藤一郎
```

#### ビルダーパターンの利点
1. 可読性
    + どのパラメータに何を設定しているかが明確
2. 柔軟性
    + 必須パラメータとオプションパラメータを明確に区別
3. 不変性の保証
    + 構築後は変更不可能
4. バリデーション
    + 構築時に値の妥当性を検証

## まとめ

本章では、Javaにおける不変性とfinalキーワードの重要性について学習しました。

### 学習した主な内容
1. 不変オブジェクトの概念
    + 一度作成されたら状態が変更できないオブジェクト
2. finalキーワードの3つの用途
    + 変数、メソッド、クラスへの適用
3. 防御的コピー
    + 可変オブジェクトを安全に扱う技術
4. ビルダーパターン
    + 複雑な不変オブジェクトを構築する設計パターン

### 不変性の利点

1. スレッドセーフティ
    + 同期化なしで安全に共有可能
2. 予測可能性
    + 状態が変わらない　→　動作を予測しやすい
3. キャッシュ可能性
    + 安全にキャッシュできる
4. デバッグの容易さ
    + 状態変更のタイミングを追跡する必要がない

不変性は、堅牢で保守性の高いJavaプログラムを作成するための重要な設計原則です。
とくに並行プログラミングや大規模システムの開発において、その価値が発揮されます。

### Java 14以降の不変性サポート

Java 14以降では、Recordクラスにより不変データクラスの作成がより簡潔になりました。

<span class="listing-number">**サンプルコード6-12**</span>

```java
// Java 14以降：Recordを使った不変クラス
public record ImmutablePoint(int x, int y) {
    // コンストラクタ、getter、equals、hashCode、toStringが自動生成される
}

// 使用例
ImmutablePoint point = new ImmutablePoint(10, 20);
System.out.println(point.x()); // 10
System.out.println(point.y()); // 20
```
実行結果：
```
10
20
```

## 章末演習

### 演習課題へのアクセス
本章の演習課題は、GitHubリポジトリで提供されています。<br>
`https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapter06/`

### 課題構成
- 本章の基本概念の理解確認
- 応用的な実装練習
- 実践的な総合問題

詳細な課題内容と実装のヒントは、各課題フォルダ内のREADME.mdを参照してください。

### 課題の概要

1. 基礎課題
    + 不変なPointクラス - 基本的な不変オブジェクトの実装
2. 発展課題
    + 不変コレクション - 防御的コピーを使った複雑な不変性の実現
3. チャレンジ課題
    + ビルダーパターン - 不変オブジェクトの柔軟な構築

詳細な課題内容と実装のヒントは、GitHubリポジトリの各課題フォルダ内のREADME.mdを参照してください。

#### 次のステップ

基礎課題が完了したら、第7章「抽象クラスとインターフェイス」に進みましょう。

> ※ 本章の発展的な内容については、付録B.05「不変性の設計パターン」（`https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b05-immutability-patterns/`）を参照してください。

## よくあるエラーと対処法

不変性とfinalキーワードを学習する際によく遭遇するエラーとその解決方法を説明します。

### 1. finalキーワードの使い方の間違い

#### エラー例: final変数の再代入

<span class="listing-number">**サンプルコード6-13**</span>

```java
public class FinalErrorExample {
    public static void main(String[] args) {
        final int number = 10;
        number = 20; // コンパイルエラー
    }
}
```

##### エラーメッセージ
```
Error: Cannot assign a value to final variable number
```

##### 対処法

<span class="listing-number">**サンプルコード6-14**</span>

```java
public class FinalCorrectExample {
    public static void main(String[] args) {
        final int number = 10; // 宣言と同時に初期化
        // number = 20; // 再代入しない
        
        // 新しい値が必要な場合は新しい変数を作成
        final int newNumber = 20;
        System.out.println("元の値: " + number);
        System.out.println("新しい値: " + newNumber);
    }
}
```
実行結果：
```
元の値: 10
新しい値: 20
```

#### エラー例: final変数の未初期化

<span class="listing-number">**サンプルコード6-15**</span>

```java
public class UninitializedFinalExample {
    private final String name; // コンパイルエラー
    
    public UninitializedFinalExample() {
        // nameが初期化されていない
    }
}
```

##### エラーメッセージ
```
Error: Variable name might not have been initialized
```

##### 対処法

<span class="listing-number">**サンプルコード6-16**</span>

```java
public class InitializedFinalExample {
    private final String name;
    
    public InitializedFinalExample(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("名前は必須です");
        }
        this.name = name; // コンストラクタで初期化
    }
    
    public String getName() {
        return name;
    }
}
```

### 2. 不変オブジェクトの設計ミス

#### エラー例: 可変フィールドの露出

<span class="listing-number">**サンプルコード6-17**</span>

```java
public final class BrokenImmutablePerson {
    private final String name;
    private final List<String> hobbies; // 問題: 可変コレクション
    
    public BrokenImmutablePerson(String name, List<String> hobbies) {
        this.name = name;
        this.hobbies = hobbies; // 問題: 直接代入
    }
    
    public List<String> getHobbies() {
        return hobbies; // 問題: 直接返却
    }
}
```

##### 問題点

<span class="listing-number">**サンプルコード6-18**</span>

```java
List<String> hobbies = Arrays.asList("読書", "映画鑑賞");
BrokenImmutablePerson person = new BrokenImmutablePerson("太郎", hobbies);

// 不変性が破られる
person.getHobbies().add("ゲーム"); // RuntimeException
hobbies.set(0, "スポーツ"); // 元オブジェクトの状態が変わる
```

##### 対処法

<span class="listing-number">**サンプルコード6-19**</span>

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ImmutablePerson {
    private final String name;
    private final List<String> hobbies;
    
    public ImmutablePerson(String name, List<String> hobbies) {
        this.name = name;
        // 防御的コピーを実行
        this.hobbies = Collections.unmodifiableList(
            new ArrayList<>(hobbies)
        );
    }
    
    public String getName() {
        return name;
    }
    
    public List<String> getHobbies() {
        // 防御的コピーを返却
        return Collections.unmodifiableList(hobbies);
    }
}
```

### 3. 防御的コピーの実装忘れ

#### エラー例: 日付オブジェクトの防御的コピー漏れ

<span class="listing-number">**サンプルコード6-20**</span>

```java
import java.util.Date;

public final class BrokenImmutableEvent {
    private final String title;
    private final Date eventDate; // 問題: Dateは可変
    
    public BrokenImmutableEvent(String title, Date eventDate) {
        this.title = title;
        this.eventDate = eventDate; // 問題: 直接代入
    }
    
    public Date getEventDate() {
        return eventDate; // 問題: 直接返却
    }
}
```

##### 問題点

<span class="listing-number">**サンプルコード6-21**</span>

```java
Date date = new Date();
BrokenImmutableEvent event = new BrokenImmutableEvent("会議", date);

// 不変性が破られる
date.setTime(System.currentTimeMillis() + 86400000); // 翌日に変更
event.getEventDate().setTime(0); // イベント日時が変更される
```

##### 対処法

<span class="listing-number">**サンプルコード6-22**</span>

```java
import java.util.Date;

public final class ImmutableEvent {
    private final String title;
    private final Date eventDate;
    
    public ImmutableEvent(String title, Date eventDate) {
        this.title = title;
        // 防御的コピー（入力時）
        this.eventDate = new Date(eventDate.getTime());
    }
    
    public String getTitle() {
        return title;
    }
    
    public Date getEventDate() {
        // 防御的コピー（出力時）
        return new Date(eventDate.getTime());
    }
}
```

##### モダンなアプローチ（推奨）

<span class="listing-number">**サンプルコード6-23**</span>

```java
import java.time.LocalDateTime;

public final class ModernImmutableEvent {
    private final String title;
    private final LocalDateTime eventDateTime;
    
    public ModernImmutableEvent(String title, LocalDateTime eventDateTime) {
        this.title = title;
        this.eventDateTime = eventDateTime; // LocalDateTimeは不変
    }
    
    public String getTitle() {
        return title;
    }
    
    public LocalDateTime getEventDateTime() {
        return eventDateTime; // そのまま返却可能
    }
}
```

### 4. final配列・コレクションの誤解

#### エラー例: final配列の誤用

<span class="listing-number">**サンプルコード6-24**</span>

```java
public class FinalArrayMisunderstanding {
    public static void main(String[] args) {
        final int[] numbers = {1, 2, 3, 4, 5};
        
        // これは可能（配列の要素は変更可能）
        numbers[0] = 10; // OK
        
        // これはエラー（配列参照の変更は不可）
        // numbers = new int[]{6, 7, 8}; // コンパイルエラー
        
        System.out.println(java.util.Arrays.toString(numbers)); // [10, 2, 3, 4, 5]
    }
}
```
実行結果：
```
[10, 2, 3, 4, 5]
```

##### 対処法（真の不変配列）

<span class="listing-number">**サンプルコード6-25**</span>

```java
import java.util.Arrays;

public final class ImmutableArray {
    private final int[] numbers;
    
    public ImmutableArray(int[] numbers) {
        // 防御的コピー
        this.numbers = Arrays.copyOf(numbers, numbers.length);
    }
    
    public int get(int index) {
        return numbers[index];
    }
    
    public int size() {
        return numbers.length;
    }
    
    public int[] toArray() {
        // 防御的コピーを返却
        return Arrays.copyOf(numbers, numbers.length);
    }
}
```

#### エラー例: Listの不変性に関する誤解

<span class="listing-number">**サンプルコード6-26**</span>

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListImmutabilityMisunderstanding {
    public static void main(String[] args) {
        final List<String> items = new ArrayList<>();
        items.add("item1"); // OK（要素の追加は可能）
        items.add("item2"); // OK
        
        // items = new ArrayList<>(); // コンパイルエラー
        
        // 本当に不変にしたい場合
        final List<String> immutableItems = Collections.unmodifiableList(items);
        // immutableItems.add("item3"); // UnsupportedOperationException
    }
}
```
実行結果：
```
可変リスト: [item1, item2]
不変ビュー: [item1, item2]
元のリスト変更後の不変ビュー: [item1, item2, item3]
```

### 5. 不変性の破綻パターン

#### エラー例: 継承による不変性の破綻

<span class="listing-number">**サンプルコード6-27**</span>

```java
public class ImmutablePerson {
    private final String name;
    private final int age;
    
    public ImmutablePerson(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    public String getName() { return name; }
    public int getAge() { return age; }
}

// 問題: 継承によって不変性が破綻
public class MutableEmployee extends ImmutablePerson {
    private String department;
    
    public MutableEmployee(String name, int age, String department) {
        super(name, age);
        this.department = department;
    }
    
    public void setDepartment(String department) {
        this.department = department; // 状態が変更可能
    }
    
    public String getDepartment() { return department; }
}
```

##### 対処法

<span class="listing-number">**サンプルコード6-28**</span>

```java
public final class ImmutablePerson { // finalクラス
    private final String name;
    private final int age;
    
    public ImmutablePerson(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    public String getName() { return name; }
    public int getAge() { return age; }
    
    // 状態変更が必要な場合は新しいオブジェクトを作成
    public ImmutablePerson withAge(int newAge) {
        return new ImmutablePerson(this.name, newAge);
    }
}
```

### 6. パフォーマンスへの影響

#### エラー例: 不必要な防御的コピー

<span class="listing-number">**サンプルコード6-29**</span>

```java
import java.util.ArrayList;
import java.util.List;

public final class IneffientImmutableClass {
    private final List<String> items;
    
    public IneffientImmutableClass(List<String> items) {
        this.items = new ArrayList<>(items); // 防御的コピー
    }
    
    public List<String> getItems() {
        return new ArrayList<>(items); // 毎回新しいリストを作成
    }
    
    public String getItem(int index) {
        List<String> copy = new ArrayList<>(items); // 不必要なコピー
        return copy.get(index);
    }
}
```

##### 対処法

<span class="listing-number">**サンプルコード6-30**</span>

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class EfficientImmutableClass {
    private final List<String> items;
    
    public EfficientImmutableClass(List<String> items) {
        this.items = Collections.unmodifiableList(new ArrayList<>(items));
    }
    
    public List<String> getItems() {
        return items; // 不変リストなのでそのまま返却
    }
    
    public String getItem(int index) {
        return items.get(index); // 直接アクセス
    }
    
    public int size() {
        return items.size(); // 直接アクセス
    }
}
```

##### 大量データの場合の最適化

<span class="listing-number">**サンプルコード6-31**</span>

```java
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class OptimizedImmutableClass {
    private final List<String> items;
    private volatile List<String> cachedView; // 遅延初期化（volatileはスレッド間での可視性を保証）
    
    public OptimizedImmutableClass(Collection<String> items) {
        this.items = new ArrayList<>(items);
    }
    
    public List<String> getItems() {
        if (cachedView == null) {
            synchronized (this) {
                if (cachedView == null) {
                    cachedView = Collections.unmodifiableList(items);
                }
            }
        }
        return cachedView;
    }
}
```

### デバッグのヒント

1. 不変性の検証
    + オブジェクトが本当に不変かテストで確認
2. 防御的コピーの確認
    + 入力と出力の両方で漏れなく実装されているか確認
3. finalキーワードの使用
    + IDEの警告を活用してfinalを変更不可能な箇所に使用
4. 不変コレクションの活用
    + `Collections.unmodifiableList()`などの使用

これらのエラーパターンを理解することで、堅牢で保守性の高い不変オブジェクトを設計できるようになります。