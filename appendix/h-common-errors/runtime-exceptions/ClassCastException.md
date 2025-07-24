# ClassCastException

## 概要

`ClassCastException`は、互換性のない型へのキャストを行った際に発生する実行時例外です。コンパイル時には検出されず、実行時に初めて発覚するため、適切な型チェックが重要です。

## 発生原因

1. **ダウンキャストの失敗**: 親クラスから子クラスへの不正なキャスト
2. **インターフェイス実装の誤認**: 実装していないインターフェイスへのキャスト
3. **ジェネリクスの型消去**: 実行時の型情報喪失による誤ったキャスト
4. **Objectからの不適切なキャスト**: Object型から特定の型への誤ったキャスト

## 典型的な発生パターン

### パターン1：不正なダウンキャスト

```java
class Animal {
    void makeSound() {
        System.out.println("動物の鳴き声");
    }
}

class Dog extends Animal {
    void wagTail() {
        System.out.println("しっぽを振る");
    }
}

class Cat extends Animal {
    void scratch() {
        System.out.println("ひっかく");
    }
}

// 使用例
Animal animal = new Cat();
Dog dog = (Dog) animal;  // ClassCastException: CatをDogにキャストできない
```

### パターン2：Objectからのキャスト

```java
Object obj = "文字列";
Integer num = (Integer) obj;  // ClassCastException: StringをIntegerにキャストできない
```

### パターン3：コレクションでの型の混在

```java
List list = new ArrayList();  // raw type使用（非推奨）
list.add("文字列");
list.add(123);

for (Object item : list) {
    String str = (String) item;  // 2番目の要素でClassCastException
    System.out.println(str.toUpperCase());
}
```

### パターン4：インターフェイスへの誤ったキャスト

```java
interface Flyable {
    void fly();
}

class Bird implements Flyable {
    public void fly() {
        System.out.println("飛ぶ");
    }
}

class Fish {
    void swim() {
        System.out.println("泳ぐ");
    }
}

// 使用例
Object obj = new Fish();
Flyable flyable = (Flyable) obj;  // ClassCastException: FishはFlyableを実装していない
```

## 解決策

### 1. instanceof演算子による型チェック

```java
public void processAnimal(Animal animal) {
    if (animal instanceof Dog) {
        Dog dog = (Dog) animal;
        dog.wagTail();
    } else if (animal instanceof Cat) {
        Cat cat = (Cat) animal;
        cat.scratch();
    } else {
        animal.makeSound();  // 共通メソッドのみ使用
    }
}
```

### 2. パターンマッチング（Java 14以降）

```java
public void processAnimal(Animal animal) {
    if (animal instanceof Dog dog) {  // 型チェックとキャストを同時に実行
        dog.wagTail();
    } else if (animal instanceof Cat cat) {
        cat.scratch();
    }
}
```

### 3. switch式でのパターンマッチング（Java 17以降）

```java
public String describeAnimal(Animal animal) {
    return switch (animal) {
        case Dog dog -> "犬: " + dog.getName();
        case Cat cat -> "猫: " + cat.getName();
        case null -> "動物がいません";
        default -> "不明な動物";
    };
}
```

### 4. ジェネリクスの適切な使用

```java
// 型安全なコレクション
List<String> strings = new ArrayList<>();
strings.add("文字列1");
strings.add("文字列2");
// strings.add(123);  // コンパイルエラー

// 型安全な反復処理
for (String str : strings) {
    System.out.println(str.toUpperCase());  // キャスト不要
}
```

### 5. Class型を使った安全なキャスト

```java
public <T> Optional<T> safeCast(Object obj, Class<T> clazz) {
    if (clazz.isInstance(obj)) {
        return Optional.of(clazz.cast(obj));
    }
    return Optional.empty();
}

// 使用例
Object obj = "文字列";
Optional<String> str = safeCast(obj, String.class);
Optional<Integer> num = safeCast(obj, Integer.class);  // 空のOptional
```

## 高度な解決策

### 1. ビジターパターン

```java
interface AnimalVisitor<T> {
    T visitDog(Dog dog);
    T visitCat(Cat cat);
}

abstract class Animal {
    abstract <T> T accept(AnimalVisitor<T> visitor);
}

class Dog extends Animal {
    @Override
    <T> T accept(AnimalVisitor<T> visitor) {
        return visitor.visitDog(this);
    }
}

class Cat extends Animal {
    @Override
    <T> T accept(AnimalVisitor<T> visitor) {
        return visitor.visitCat(this);
    }
}

// 使用例：型安全な処理
AnimalVisitor<String> describer = new AnimalVisitor<>() {
    public String visitDog(Dog dog) {
        return "犬がしっぽを振っています";
    }
    public String visitCat(Cat cat) {
        return "猫が毛づくろいをしています";
    }
};
```

### 2. 型トークンパターン

```java
public class TypeSafeMap {
    private Map<Class<?>, Object> map = new HashMap<>();
    
    public <T> void put(Class<T> type, T value) {
        map.put(type, value);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type) {
        return (T) map.get(type);  // 型安全性はAPIで保証
    }
}

// 使用例
TypeSafeMap container = new TypeSafeMap();
container.put(String.class, "文字列");
container.put(Integer.class, 123);

String str = container.get(String.class);  // キャスト不要
Integer num = container.get(Integer.class);  // キャスト不要
```

### 3. ファクトリーメソッドパターン

```java
public abstract class AnimalFactory {
    public static Animal createAnimal(String type) {
        return switch (type.toLowerCase()) {
            case "dog" -> new Dog();
            case "cat" -> new Cat();
            default -> throw new IllegalArgumentException("Unknown animal type: " + type);
        };
    }
    
    @SuppressWarnings("unchecked")
    public static <T extends Animal> T createAnimal(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create animal", e);
        }
    }
}
```

## デバッグのコツ

### 1. エラーメッセージの解読

```
Exception in thread "main" java.lang.ClassCastException: 
    class com.example.Cat cannot be cast to class com.example.Dog
    at com.example.Main.main(Main.java:15)
```

- 実際の型（Cat）と期待した型（Dog）が明記される
- 発生箇所の行番号を確認

### 2. デバッグ時の型確認

```java
public void debugCast(Object obj) {
    System.out.println("オブジェクトの型: " + obj.getClass().getName());
    System.out.println("Dogのインスタンス? " + (obj instanceof Dog));
    System.out.println("Catのインスタンス? " + (obj instanceof Cat));
    
    // 継承階層の確認
    Class<?> clazz = obj.getClass();
    System.out.println("継承階層:");
    while (clazz != null) {
        System.out.println("  " + clazz.getName());
        clazz = clazz.getSuperclass();
    }
}
```

### 3. ロギングの活用

```java
private static final Logger logger = LoggerFactory.getLogger(MyClass.class);

public void processObject(Object obj) {
    logger.debug("処理対象オブジェクト: {}, 型: {}", obj, obj.getClass().getName());
    
    if (obj instanceof ExpectedType) {
        ExpectedType expected = (ExpectedType) obj;
        logger.debug("正常にキャスト完了");
    } else {
        logger.warn("期待した型ではありません。実際の型: {}", obj.getClass().getName());
    }
}
```

## ベストプラクティス

1. **ジェネリクスの活用**: 型安全性をコンパイル時に保証
2. **instanceof使用の習慣化**: キャスト前に必ず型チェック
3. **ポリモーフィズムの活用**: キャストを避け、共通インターフェイスを使用
4. **適切な例外処理**: ClassCastExceptionをキャッチして適切に処理
5. **コードレビューでの確認**: キャスト操作は特に注意深くレビュー

## 関連項目

- [NullPointerException](NullPointerException.md)
- [IllegalArgumentException](IllegalArgumentException.md)
- ジェネリクスガイド
- 型安全性のベストプラクティス