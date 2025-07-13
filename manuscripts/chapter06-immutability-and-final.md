# 第6章 不変性とfinalキーワード

## 本章の学習目標

### 前提知識

本章を学習するためには、いくつかの大切な前提知識があるとよいでしょう。まずポイントとして、第5章までに学習したオブジェクト指向プログラミングの基本概念を理解していることが挙げられます。特に、クラスとインスタンスの関係、カプセル化の大切さ、パッケージとアクセス制御のメカニズムについての深い理解が役立ちます。また、オブジェクトの状態（フィールド）とその変更がプログラムの動作に与える影響について、実践的な経験を持っているとよいでしょう。

さらに、より深い学習を希望する学生には、マルチスレッド環境での共有データの問題や、オブジェクトの予期しない変更によるバグを経験したことがあると、不変性の大切さをより深く理解できるでしょう。大規模なプログラムでのデータの一貫性維持の難しさを理解していると、不変オブジェクトの設計思想がより鮮明になります。

### 知識理解目標

本章では、Javaプログラミングにおける不変性（Immutability）とfinalキーワードの概念を深く理解することが目標です。不変性は、堅牢で保守性の高いソフトウェアを構築するための設計アプローチの一つです。関数型プログラミングでは不変性が中心的な概念となりますが、オブジェクト指向でも状況に応じて活用されます。finalキーワードの3つの用途（変数、メソッド、クラス）それぞれの意味と効果を理解し、適切に使い分けられます。また、不変オブジェクトがスレッドセーフティ、キャッシング、関数型プログラミングなどの高度な概念とどのように関連するかを学びます。

### 技能習得目標

技能習得の面では、不変オブジェクトを正しく設計・実装できるようになることが目標です。これには、finalフィールドの宣言、コンストラクタでの初期化、setterメソッドを持たない設計、防御的コピーの実装などが含まれます。また、既存の可変クラスを不変クラスに変換する技術、ビルダパターンを使った不変オブジェクトの構築方法も習得します。final変数を使った定数の定義、finalメソッドによる継承の制御、finalクラスによる拡張の防止など、finalキーワードのすべての側面を実践的に使いこなせます。

### 設計能力目標

設計能力の観点からは、不変性を活用した安全で予測可能なシステム設計ができるようになることが目標です。これは、副作用を最小化したメソッド設計、状態変更を局所化したアーキテクチャ、不変性と可変性のトレードオフを考慮した設計判断などを含みます。値オブジェクトパターン、不変コレクションの活用、関数型プログラミングスタイルの採用など、モダンなJavaプログラミングで求められる設計スキルを身につけます。

### 到達レベルの指標

最終的な到達レベルとしては、以下のことができます：
- finalキーワードの3つの用途を正しく理解し、状況に応じて適切に使い分けられる
- 完全な不変オブジェクトを設計・実装し、その利点を最大限に活用できる
- スレッドセーフなコードを不変性を使って実現できる
- 不変性と性能のトレードオフを理解し、適切なバランスを取った設計ができる
- Java標準ライブラリの不変クラス（String、Integer等）の設計思想を理解し、同様の品質のコードを書ける



## 6.1 不変性の概念と重要性

第4章でカプセル化について学び、第5章で継承の高度な活用法を習得しました。本章では、オブジェクトの状態を変更できないようにする「不変性（Immutability）」という設計概念と、それを実現するための`final`キーワードについて学習します。

不変性は、一度作成されたオブジェクトの状態が変更できないという性質です。この概念は、バグの少ない堅牢なプログラムを作成する上で非常に重要な役割を果たします。

### なぜ不変性が重要なのか

可変オブジェクトは、プログラムの複雑性を増大させ、予期しないバグの原因となることがあります。特に以下のような状況で問題が顕著になります：

**1. マルチスレッド環境での競合状態**

以下のコードは、可変オブジェクトがマルチスレッド環境でどのような問題を引き起こすかを示しています：

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
    public static void main(String[] args) throws InterruptedException {
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
        t1.join();
        t2.join();
        
        // 期待値: 2000, 実際の値: 予測不可能（1000～2000の間）
        System.out.println("X座標: " + point.getX());
    }
}
```

**2. 意図しない副作用**

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

これらの問題を解決するのが、不変オブジェクトです：

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

**不変オブジェクトの利点**：

1. **スレッドセーフティ**: 状態が変更されないため、同期化なしで安全に共有できる
2. **予測可能性**: 一度作成されたオブジェクトの状態は変わらない
3. **キャッシュ可能**: 状態が変わらないため、安全にキャッシュできる
4. **デバッグの容易さ**: 状態変更のタイミングを追跡する必要がない

## 6.2 finalキーワードの3つの用途

`final`キーワードは、Javaで「変更不可」を表現するための重要な機能です。`final`は以下の3つの場所で使用でき、それぞれ異なる意味を持ちます：

### 6.2.1 final変数（定数と不変フィールド）

`final`を変数に付けると、その変数は一度だけ初期化でき、その後は変更できなくなります。

```java
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

**final変数の初期化タイミング**：
- **宣言時**: `final int x = 10;`
- **コンストラクタ内**: インスタンスフィールドの場合
- **インスタンス初期化子**: インスタンスフィールドの場合
- **static初期化子**: static finalフィールドの場合

### 6.2.2 finalメソッド（オーバーライドの禁止）

`final`をメソッドに付けると、サブクラスでそのメソッドをオーバーライドできなくなります。

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

### 6.2.3 finalクラス（継承の禁止）

`final`をクラスに付けると、そのクラスを継承できなくなります。

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

**finalクラスの利点**：
1. **セキュリティ**: 悪意のあるサブクラスによる動作の改変を防ぐ
2. **パフォーマンス**: JVMが最適化しやすい
3. **設計の明確化**: このクラスは継承を意図していないことを明示

## 6.3 不変オブジェクトの設計パターン

不変オブジェクトを正しく設計するには、いくつかの重要なルールを守る必要があります。

### 6.3.1 不変クラスの基本ルール

1. **クラスをfinalにする**: 継承を防ぐ
2. **すべてのフィールドをprivate finalにする**: 外部からの直接アクセスと変更を防ぐ
3. **setterメソッドを提供しない**: 状態変更の手段を提供しない
4. **コンストラクタで初期化を完了する**: すべてのフィールドをコンストラクタで設定
5. **防御的コピーを行う**: 可変オブジェクトをフィールドに持つ場合

### 6.3.2 実践例：不変な`Person`クラス

<span class="listing-number">**サンプルコード6-2**</span>

```java
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

### 6.3.3 不変オブジェクトの使用例

<span class="listing-number">**サンプルコード6-3**</span>

```java
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

**不変オブジェクトの重要な特性**：

1. **スレッドセーフティ**: 同期化なしで複数のスレッドから安全にアクセス可能
2. **予測可能性**: オブジェクトの状態が変わらないためデバッグが容易
3. **キャッシュ可能**: 状態が変わらないためHashMapのキーとして安全に使用可能
4. **関数型プログラミングとの親和性**: 副作用のないコードが書きやすい

## 6.4 防御的コピーとビルダーパターン

### 6.4.1 防御的コピーの重要性

不変オブジェクトを設計する際、可変オブジェクト（配列やコレクション）をフィールドに持つ場合は特に注意が必要です。単に参照を保持するだけでは、外部から内容を変更される可能性があります。

<span class="listing-number">**サンプルコード6-4**</span>

```java
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
### 6.4.2 ビルダーパターンによる不変オブジェクトの構築

複雑な不変オブジェクトを構築する際、コンストラクタの引数が多くなりすぎる問題があります。ビルダーパターンはこの問題を解決します。

<span class="listing-number">**サンプルコード6-5**</span>

```java
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
}```


### 6.4.3 ビルダーパターンの使用例

<span class="listing-number">**サンプルコード6-6**</span>

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

**ビルダーパターンの利点**：
1. **可読性**: どのパラメータに何を設定しているかが明確
2. **柔軟性**: 必須パラメータとオプションパラメータを明確に区別
3. **不変性の保証**: 構築後は変更不可能
4. **バリデーション**: 構築時に値の妥当性を検証

## 6.5 まとめ

本章では、Javaにおける不変性とfinalキーワードの重要性について学習しました。

**学習した主な内容**：
1. **不変オブジェクトの概念**: 一度作成されたら状態が変更できないオブジェクト
2. **finalキーワードの3つの用途**: 変数、メソッド、クラスへの適用
3. **防御的コピー**: 可変オブジェクトを安全に扱う技術
4. **ビルダーパターン**: 複雑な不変オブジェクトを構築する設計パターン

**不変性の利点**：
- **スレッドセーフティ**: 同期化なしで安全に共有可能
- **予測可能性**: 状態が変わらないため動作が予測しやすい
- **キャッシュ可能性**: 安全にキャッシュできる
- **デバッグの容易さ**: 状態変更のタイミングを追跡する必要がない

不変性は、堅牢で保守性の高いJavaプログラムを作成するための重要な設計原則です。特に並行プログラミングや大規模システムの開発において、その価値が発揮されます。

## 6.6 章末演習


本章で学んだ不変性とfinalキーワードを実践的な課題で確認しましょう。

### 演習課題へのアクセス

本書の演習課題は、以下のGitHubリポジトリで提供されています：

**リポジトリ**: `https://github.com/[your-repo]/java-primer-exercises`

### 第6章の課題構成

```
exercises/chapter06/
├── basic/              # 基礎課題（推奨）
│   ├── README.md       # 詳細な課題説明
│   └── ImmutableClass/ # 不変クラス設計課題
├── advanced/           # 発展課題（推奨）
├── challenge/          # チャレンジ課題（任意）
└── solutions/          # 解答例（実装後に参照）
```

### 学習の目標

本章の演習を通じて以下のスキルを習得します：
- finalキーワードの3つの用途（変数、メソッド、クラス）の適切な使用
- 完全な不変オブジェクトの設計と実装
- 防御的コピーによるデータの保護
- スレッドセーフなコードの作成

### 課題の概要

1. **基礎課題**: 不変なPointクラス - 基本的な不変オブジェクトの実装
2. **発展課題**: 不変コレクション - 防御的コピーを使った複雑な不変性の実現
3. **チャレンジ課題**: ビルダーパターン - 不変オブジェクトの柔軟な構築

詳細な課題内容と実装のヒントは、GitHubリポジトリの各課題フォルダ内のREADME.mdを参照してください。

**次のステップ**: 基礎課題が完了したら、第7章「抽象クラスとインターフェイス」に進みましょう。

## より深い理解のために

本章で学んだ不変性の基本概念をさらに深く理解したい方は、GitHubリポジトリの付録資料を参照してください：

**付録リソース**: `/appendix/b06-immutability-patterns/`

この付録では以下の高度なトピックを扱います：

- **完全な不変性の実現**: 防御的コピー、ビルダパターンとの組み合わせ
- **Copy-on-Writeパターン**: 効率的な構造共有、永続的データ構造
- **イミュータブルコレクション**: カスタム実装、Trieベースのマップ
- **関数型プログラミング**: レンズパターン、Redux風の状態管理
- **パフォーマンス最適化**: オブジェクトプーリング、遅延評価

これらの技術は、並行プログラミングや大規模システムの設計において大切な役割を果たします。