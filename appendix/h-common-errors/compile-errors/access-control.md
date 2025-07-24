# アクセス制御違反エラー

## 概要

アクセス制御違反エラーは、private、protected、package-private（デフォルト）修飾子により、アクセスが制限されているメンバー（フィールド、メソッド、クラス）に不正にアクセスした場合に発生するコンパイルエラーです。

## エラーメッセージの例

```
error: name has private access in Person
error: method calculate() has protected access in Calculator
error: class InternalHelper is not public in package com.example; cannot be accessed from outside package
```

## アクセス修飾子の基本

### アクセスレベル一覧

| 修飾子 | クラス内 | パッケージ内 | サブクラス | 全体 |
|--------|----------|--------------|------------|------|
| private | ○ | × | × | × |
| (default) | ○ | ○ | × | × |
| protected | ○ | ○ | ○ | × |
| public | ○ | ○ | ○ | ○ |

## 典型的な発生パターン

### パターン1：privateフィールドへの直接アクセス

```java
// Person.java
public class Person {
    private String name;
    private int age;
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}

// Main.java
public class Main {
    public static void main(String[] args) {
        Person person = new Person("太郎", 20);
        
        // エラー：privateフィールドへの直接アクセス
        System.out.println(person.name);  // error: name has private access
        person.age = 21;  // error: age has private access
    }
}
```

### パターン2：protectedメンバーへの不正アクセス

```java
// パッケージ: com.example.base
package com.example.base;

public class BaseClass {
    protected void doSomething() {
        System.out.println("処理実行");
    }
}

// パッケージ: com.example.other
package com.example.other;

import com.example.base.BaseClass;

public class OtherClass {
    public void test() {
        BaseClass base = new BaseClass();
        // エラー：異なるパッケージからprotectedメソッドにアクセス
        base.doSomething();  // error: doSomething() has protected access
    }
}
```

### パターン3：パッケージプライベートクラス

```java
// パッケージ: com.example.internal
package com.example.internal;

// パッケージプライベートクラス（publicなし）
class InternalHelper {
    public static void help() {
        System.out.println("内部ヘルパー");
    }
}

// パッケージ: com.example.app
package com.example.app;

import com.example.internal.InternalHelper;  // エラー：クラスが見えない

public class Application {
    public void run() {
        InternalHelper.help();  // コンパイルエラー
    }
}
```

### パターン4：内部クラスのアクセス制御

```java
public class Outer {
    private class PrivateInner {
        void method() {
            System.out.println("内部メソッド");
        }
    }
    
    public PrivateInner getInner() {
        return new PrivateInner();
    }
}

// 別クラスから
public class Test {
    public void test() {
        Outer outer = new Outer();
        // エラー：PrivateInnerは外部から見えない
        Outer.PrivateInner inner = outer.getInner();  // コンパイルエラー
    }
}
```

## 解決策

### 1. ゲッター/セッターの提供

```java
public class Person {
    private String name;
    private int age;
    
    // ゲッター
    public String getName() {
        return name;
    }
    
    // セッター
    public void setName(String name) {
        if (name != null && !name.isEmpty()) {
            this.name = name;
        }
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        if (age >= 0 && age <= 150) {
            this.age = age;
        }
    }
}

// 使用例
Person person = new Person();
person.setName("太郎");
System.out.println(person.getName());
```

### 2. 適切なアクセス修飾子の選択

```java
public class Employee {
    // 読み取り専用にしたい場合
    private final String id;
    
    // サブクラスからアクセス可能にする
    protected String department;
    
    // パッケージ内で共有
    String teamName;
    
    // 完全に公開
    public String title;
    
    public Employee(String id) {
        this.id = id;
    }
    
    // IDは読み取りのみ可能
    public String getId() {
        return id;
    }
}
```

### 3. ファクトリーメソッドパターン

```java
public class Product {
    private String name;
    private double price;
    
    // コンストラクタをprivateに
    private Product(String name, double price) {
        this.name = name;
        this.price = price;
    }
    
    // ファクトリーメソッド
    public static Product create(String name, double price) {
        if (price < 0) {
            throw new IllegalArgumentException("価格は0以上である必要があります");
        }
        return new Product(name, price);
    }
    
    // イミュータブルなアクセス
    public String getName() {
        return name;
    }
    
    public double getPrice() {
        return price;
    }
}
```

### 4. インターフェイスによる公開

```java
// 公開インターフェイス
public interface Service {
    void execute();
    String getStatus();
}

// 実装クラスは非公開
class ServiceImpl implements Service {
    private String status = "準備完了";
    
    @Override
    public void execute() {
        System.out.println("サービス実行");
        status = "実行中";
    }
    
    @Override
    public String getStatus() {
        return status;
    }
}

// ファクトリークラス
public class ServiceFactory {
    public static Service createService() {
        return new ServiceImpl();
    }
}
```

## 高度な解決策

### 1. Builderパターンで複雑なオブジェクト生成

```java
public class Configuration {
    private final String host;
    private final int port;
    private final boolean useSSL;
    
    private Configuration(Builder builder) {
        this.host = builder.host;
        this.port = builder.port;
        this.useSSL = builder.useSSL;
    }
    
    public static class Builder {
        private String host = "localhost";
        private int port = 8080;
        private boolean useSSL = false;
        
        public Builder host(String host) {
            this.host = host;
            return this;
        }
        
        public Builder port(int port) {
            this.port = port;
            return this;
        }
        
        public Builder useSSL(boolean useSSL) {
            this.useSSL = useSSL;
            return this;
        }
        
        public Configuration build() {
            return new Configuration(this);
        }
    }
}

// 使用例
Configuration config = new Configuration.Builder()
    .host("example.com")
    .port(443)
    .useSSL(true)
    .build();
```

### 2. フレンドパターン（パッケージプライベートの活用）

```java
// 同じパッケージ内
package com.example.core;

public class Engine {
    // パッケージ内のみアクセス可能
    void internalStart() {
        System.out.println("エンジン内部始動");
    }
    
    // 公開API
    public void start() {
        internalStart();
        System.out.println("エンジン始動完了");
    }
}

// 同じパッケージ内の協調クラス
package com.example.core;

class EngineController {
    void controlEngine(Engine engine) {
        // 同じパッケージなのでアクセス可能
        engine.internalStart();
    }
}
```

### 3. リフレクションによるアクセス（非推奨）

```java
import java.lang.reflect.Field;

public class ReflectionAccess {
    public static void dangerousAccess() throws Exception {
        Person person = new Person("太郎", 20);
        
        // リフレクションでprivateフィールドにアクセス
        Field nameField = Person.class.getDeclaredField("name");
        nameField.setAccessible(true);  // アクセス制御を無効化
        
        String name = (String) nameField.get(person);
        System.out.println("取得した名前: " + name);
        
        // 値の変更も可能（危険！）
        nameField.set(person, "次郎");
    }
}
```

## テストでのアクセス制御

### 1. パッケージプライベートでテスト容易性向上

```java
// src/main/java/com/example/Calculator.java
package com.example;

public class Calculator {
    // テストのためにパッケージプライベート
    int internalCalculate(int a, int b) {
        return a + b;
    }
    
    public int calculate(int a, int b) {
        return internalCalculate(a, b);
    }
}

// src/test/java/com/example/CalculatorTest.java
package com.example;  // 同じパッケージ

public class CalculatorTest {
    @Test
    public void testInternalCalculate() {
        Calculator calc = new Calculator();
        // パッケージプライベートメソッドをテスト可能
        assertEquals(5, calc.internalCalculate(2, 3));
    }
}
```

### 2. テスト専用アクセサ

```java
public class Service {
    private State state = State.INITIAL;
    
    // 本番コードでは使用しない
    @VisibleForTesting
    State getState() {
        return state;
    }
    
    private enum State {
        INITIAL, RUNNING, STOPPED
    }
}
```

## ベストプラクティス

1. **最小権限の原則**: 必要最小限のアクセスレベルを設定
2. **カプセル化の徹底**: 内部実装を隠蔽し、公開APIを明確に
3. **イミュータブルの活用**: 可能な限り変更不可能なオブジェクトに
4. **インターフェイスの使用**: 実装ではなく抽象に依存
5. **ドキュメント化**: アクセスレベルの理由を明記

## デバッグのコツ

```java
public class AccessDebugger {
    public static void printAccessInfo(Class<?> clazz) {
        System.out.println("クラス: " + clazz.getName());
        System.out.println("修飾子: " + Modifier.toString(clazz.getModifiers()));
        
        // フィールド情報
        for (Field field : clazz.getDeclaredFields()) {
            System.out.println("フィールド: " + field.getName() + 
                " - " + Modifier.toString(field.getModifiers()));
        }
        
        // メソッド情報
        for (Method method : clazz.getDeclaredMethods()) {
            System.out.println("メソッド: " + method.getName() + 
                " - " + Modifier.toString(method.getModifiers()));
        }
    }
}
```

## 関連項目

- [type-mismatch.md](type-mismatch.md)
- [symbol-not-found.md](symbol-not-found.md)
- カプセル化の原則
- オブジェクト指向設計