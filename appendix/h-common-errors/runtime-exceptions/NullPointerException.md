# NullPointerException

## 概要

`NullPointerException`は、null参照に対してメソッド呼び出しやフィールドアクセスを行った際に発生する、最も一般的な実行時例外です。Javaプログラミングにおいて最も頻繁に遭遇するエラーの一つです。

## 発生原因

1. **未初期化のフィールド**: オブジェクトのフィールドが初期化されていない
2. **メソッドの戻り値**: nullを返すメソッドの戻り値を使用
3. **配列の要素**: 初期化されていない配列要素へのアクセス
4. **コレクションの要素**: コレクションから取得した要素がnull

## 典型的な発生パターン

### パターン1：初期化忘れ

```java
public class Student {
    private String name;  // 初期化されていない
    
    public void printName() {
        // nameがnullのため、NullPointerExceptionが発生
        System.out.println(name.length());
    }
}
```

### パターン2：メソッドの戻り値

```java
public class UserService {
    public User findUser(String id) {
        // ユーザーが見つからない場合、nullを返す
        if (!userExists(id)) {
            return null;
        }
        return loadUser(id);
    }
}

// 使用例
User user = userService.findUser("unknown");
System.out.println(user.getName());  // NullPointerException
```

### パターン3：配列要素

```java
String[] names = new String[5];
// 配列は作成されたが、要素は初期化されていない
System.out.println(names[0].toUpperCase());  // NullPointerException
```

### パターン4：チェーンメソッド呼び出し

```java
public class Order {
    private Customer customer;
    
    public String getCustomerCity() {
        // customerまたはgetAddress()がnullの可能性
        return customer.getAddress().getCity();
    }
}
```

## 解決策

### 1. Nullチェック

```java
public void printName() {
    if (name != null) {
        System.out.println(name.length());
    } else {
        System.out.println("名前が設定されていません");
    }
}
```

### 2. デフォルト値の設定

```java
public class Student {
    private String name = "";  // 空文字列で初期化
    
    public Student() {
        // コンストラクタでも初期化可能
        this.name = "未設定";
    }
}
```

### 3. Optionalの使用（Java 8以降）

```java
public Optional<User> findUser(String id) {
    if (!userExists(id)) {
        return Optional.empty();
    }
    return Optional.of(loadUser(id));
}

// 使用例
userService.findUser("unknown")
    .ifPresent(user -> System.out.println(user.getName()));
```

### 4. Objects.requireNonNull()の使用

```java
public void setName(String name) {
    // nullが渡された場合、即座にNullPointerExceptionを発生
    this.name = Objects.requireNonNull(name, "名前はnullにできません");
}
```

### 5. 防御的プログラミング

```java
public String getCustomerCity() {
    if (customer != null && customer.getAddress() != null) {
        return customer.getAddress().getCity();
    }
    return "不明";
}
```

## 予防的プログラミング手法

### 1. イミュータブルオブジェクトの活用

```java
public final class Student {
    private final String name;
    
    public Student(String name) {
        this.name = Objects.requireNonNull(name);
    }
    
    // getterのみ提供
    public String getName() {
        return name;
    }
}
```

### 2. ビルダーパターンの活用

```java
public class Student {
    private final String name;
    private final Integer age;
    
    private Student(Builder builder) {
        this.name = Objects.requireNonNull(builder.name, "名前は必須です");
        this.age = builder.age;  // nullも許容
    }
    
    public static class Builder {
        private String name;
        private Integer age;
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder age(Integer age) {
            this.age = age;
            return this;
        }
        
        public Student build() {
            return new Student(this);
        }
    }
}
```

### 3. アノテーションの活用

```java
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UserService {
    @Nonnull
    public User getUser(@Nonnull String id) {
        User user = findUser(id);
        if (user == null) {
            throw new IllegalArgumentException("ユーザーが見つかりません: " + id);
        }
        return user;
    }
    
    @Nullable
    private User findUser(String id) {
        // nullを返す可能性がある
        return userRepository.find(id);
    }
}
```

## デバッグのコツ

### 1. スタックトレースの読み方

```
Exception in thread "main" java.lang.NullPointerException
    at com.example.Student.printName(Student.java:25)
    at com.example.Main.main(Main.java:10)
```

- 最初の行（Student.java:25）がエラー発生箇所
- その行で使用している変数のいずれかがnull

### 2. デバッガの活用

1. エラーが発生する行にブレークポイントを設定
2. 各変数の値を確認
3. nullになっている変数を特定
4. その変数がnullになる原因を遡って調査

### 3. ログの活用

```java
public void processOrder(Order order) {
    logger.debug("注文処理開始: order={}", order);
    if (order != null) {
        logger.debug("顧客情報: customer={}", order.getCustomer());
        // 処理を続行
    }
}
```

## ベストプラクティス

1. **早期のnullチェック**: メソッドの開始時点でパラメータをチェック
2. **適切な初期化**: フィールドは宣言時またはコンストラクタで初期化
3. **Optionalの活用**: nullを返す可能性があるメソッドではOptionalを使用
4. **ドキュメント化**: nullを返す可能性がある場合は、Javadocに明記
5. **単体テストの作成**: null値に対するテストケースを必ず含める

## 関連項目

- [ClassCastException](ClassCastException.md)
- [ArrayIndexOutOfBoundsException](ArrayIndexOutOfBoundsException.md)
- デバッグ手法ガイド