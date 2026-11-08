# 第6章 不変性とfinalキーワード：よくあるエラーと対処法

書籍『Java入門』第6章「不変性とfinalキーワード」で扱う内容に関連して、よく遭遇するエラーとその対処法をまとめています。

不変性とfinalキーワードを学習する際によく遭遇するエラーとその解決方法を説明します。

## 1. finalキーワードの使い方の間違い

### エラー例: final変数の再代入

```java
public class FinalErrorExample {
    public static void main(String[] args) {
        final int number = 10;
        number = 20; // コンパイルエラー
    }
}
```

#### エラーメッセージ
```
error: cannot assign a value to final variable number
```

#### 対処法

コンパイルできる全文：[chapter06-code/FinalCorrectExample/](chapter06-code/FinalCorrectExample/)

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

### エラー例: final変数の未初期化

```java
public class UninitializedFinalExample {
    private final String name; // コンパイルエラー
    
    public UninitializedFinalExample() {
        // nameが初期化されていない
    }
}
```

#### エラーメッセージ
```
error: variable name might not have been initialized
```

#### 対処法

コンパイルできる全文：[chapter06-code/InitializedFinalExample/](chapter06-code/InitializedFinalExample/)

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

## 2. 不変オブジェクトの設計ミス

### エラー例: 可変フィールドの露出

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

#### 問題点

```java
List<String> hobbies = Arrays.asList("読書", "映画鑑賞");
BrokenImmutablePerson person = new BrokenImmutablePerson("太郎", hobbies);

// 不変性が破られる
person.getHobbies().add("ゲーム"); // UnsupportedOperationException（Arrays.asListは要素の追加ができない）
hobbies.set(0, "スポーツ"); // 元オブジェクトの状態が変わる
```

#### 対処法

コンパイルできる全文：[chapter06-code/ImmutablePerson/](chapter06-code/ImmutablePerson/)

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

## 3. 防御的コピーの実装忘れ

### エラー例: 日付オブジェクトの防御的コピー漏れ

コンパイルできる全文：[chapter06-code/BrokenImmutableEvent/](chapter06-code/BrokenImmutableEvent/)

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

#### 問題点

```java
Date date = new Date();
BrokenImmutableEvent event = new BrokenImmutableEvent("会議", date);

// 不変性が破られる
date.setTime(System.currentTimeMillis() + 86400000); // 翌日に変更
event.getEventDate().setTime(0); // イベント日時が変更される
```

#### 対処法

コンパイルできる全文：[chapter06-code/ImmutableEvent/](chapter06-code/ImmutableEvent/)

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

#### モダンなアプローチ（推奨）

コンパイルできる全文：[chapter06-code/ModernImmutableEvent/](chapter06-code/ModernImmutableEvent/)

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

## 4. final配列・コレクションの誤解

### エラー例: final配列の誤用

コンパイルできる全文：[chapter06-code/FinalArrayMisunderstanding/](chapter06-code/FinalArrayMisunderstanding/)

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

#### 対処法（真の不変配列）

コンパイルできる全文：[chapter06-code/ImmutableArray/](chapter06-code/ImmutableArray/)

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

### エラー例: Listの不変性に関する誤解

コンパイルできる全文：[chapter06-code/ListImmutabilityMisunderstanding/](chapter06-code/ListImmutabilityMisunderstanding/)

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
        System.out.println("可変リスト: " + items);
        System.out.println("不変ビュー: " + immutableItems);

        // unmodifiableListは元のリストの「ビュー」なので、元のリストの変更は見えてしまう
        items.add("item3");
        System.out.println("元のリスト変更後の不変ビュー: " + immutableItems);
    }
}
```
実行結果：
```
可変リスト: [item1, item2]
不変ビュー: [item1, item2]
元のリスト変更後の不変ビュー: [item1, item2, item3]
```

## 5. 不変性の破綻パターン

### エラー例: 継承による不変性の破綻

コンパイルできる全文：[chapter06-code/ImmutablePerson2/](chapter06-code/ImmutablePerson2/)

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

#### 対処法

コンパイルできる全文：[chapter06-code/ImmutablePerson3/](chapter06-code/ImmutablePerson3/)

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

## 6. パフォーマンスへの影響

### エラー例: 不必要な防御的コピー

コンパイルできる全文：[chapter06-code/IneffientImmutableClass/](chapter06-code/IneffientImmutableClass/)

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

#### 対処法

コンパイルできる全文：[chapter06-code/EfficientImmutableClass/](chapter06-code/EfficientImmutableClass/)

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

#### 大量データの場合の最適化

コンパイルできる全文：[chapter06-code/OptimizedImmutableClass/](chapter06-code/OptimizedImmutableClass/)

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

## デバッグのヒント

1. 不変性の検証
    + オブジェクトが本当に不変かテストで確認
2. 防御的コピーの確認
    + 入力と出力の両方で漏れなく実装されているか確認
3. finalキーワードの使用
    + IDEの警告を活用してfinalを変更不可能な箇所に使用
4. 不変コレクションの活用
    + `Collections.unmodifiableList()`などの使用

これらのエラーパターンを理解することで、堅牢で保守性の高い不変オブジェクトを設計できるようになります。
