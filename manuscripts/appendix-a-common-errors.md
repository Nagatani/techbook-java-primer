# <b>付録A</b> <span>Java共通エラーガイド</span> <small>よく遭遇するエラーと効果的な対処法</small>

## 本付録の目的

Javaプログラミングで頻繁に遭遇するエラーパターンを体系的にまとめました。各章で特定のトピックに関連するエラーを学習する際、この付録を参照することで、より深い理解が得られます。

## A.1 実行時例外

### A.1.1 NullPointerException

#### 概要
`NullPointerException`は、null参照に対してメソッド呼び出しやフィールドアクセスを行った際に発生する、最も一般的な実行時例外です。

#### 典型的な発生パターン

##### パターン1：初期化忘れ
```java
public class Student {
    private String name;
    
    public void printName() {
        System.out.println(name.length());  // nameがnullの場合エラー
    }
}
```

##### 解決策
```java
public class Student {
    private String name = "";  // デフォルト値で初期化
    
    public void printName() {
        if (name != null) {
            System.out.println(name.length());
        }
    }
}
```

##### パターン2：メソッドの戻り値
```java
String result = findUser("不存在");  // nullが返される可能性
System.out.println(result.toUpperCase());  // NullPointerException
```

##### 解決策
```java
String result = findUser("不存在");
if (result != null) {
    System.out.println(result.toUpperCase());
} else {
    System.out.println("ユーザーが見つかりません");
}
```

#### 予防的プログラミング手法
- フィールドの初期化を徹底する
- メソッドの戻り値をチェックする
- Optionalクラスの活用（Java 8以降）
- Objects.requireNonNull()でのアサーション

### A.1.2 ArrayIndexOutOfBoundsException

#### 概要
配列の有効範囲外のインデックスにアクセスした際に発生する例外です。

#### 典型的なエラー例
```java
int[] numbers = new int[5];
for (int i = 0; i <= numbers.length; i++) {  // i <= lengthが問題
    numbers[i] = i;  // i=5でエラー
}
```

#### 解決策
```java
int[] numbers = new int[5];
for (int i = 0; i < numbers.length; i++) {  // i < lengthに修正
    numbers[i] = i;
}
```

#### 境界チェックのベストプラクティス
- ループ条件は常に`< length`を使用
- 拡張for文の活用で境界エラーを回避
- 配列アクセス前の明示的な境界チェック

### A.1.3 ClassCastException

#### 概要
互換性のない型へのキャストを行った際に発生する例外です。

#### 典型的なエラー例
```java
Object obj = "文字列";
Integer num = (Integer) obj;  // ClassCastException
```

#### 解決策
```java
Object obj = "文字列";
if (obj instanceof Integer) {
    Integer num = (Integer) obj;
} else {
    System.out.println("Integer型ではありません");
}
```

#### 型安全なプログラミング
- instanceof演算子での事前チェック
- ジェネリクスの活用（型安全性を保証するため）
- 不要なキャストを避ける設計

### A.1.4 ConcurrentModificationException

#### 概要
コレクションの反復処理中に、コレクションの構造を変更した際に発生する例外です。

#### 典型的なエラー例
```java
List<String> list = new ArrayList<>();
list.add("A");
list.add("B");
for (String item : list) {
    if (item.equals("A")) {
        list.remove(item);  // ConcurrentModificationException
    }
}
```

#### 解決策
```java
// 解決策1：イテレータを使用
Iterator<String> iter = list.iterator();
while (iter.hasNext()) {
    if (iter.next().equals("A")) {
        iter.remove();
    }
}

// 解決策2：removeIfメソッド（Java 8以降）
list.removeIf(item -> item.equals("A"));
```

## A.2 コンパイルエラー

### A.2.1 型の不一致

#### 概要
変数の型と代入される値の型が一致しない場合に発生するコンパイルエラーです。

#### エラー例
```java
int number = "123";  // String型をint型に代入しようとしている
```

#### 解決策
```java
// 解決策1：型変換
int number = Integer.parseInt("123");

// 解決策2：適切な型を使用
String numberStr = "123";
```

### A.2.2 シンボルが見つからない

#### 概要
変数名、メソッド名、クラス名などが正しく宣言されていない、またはスコープ外で使用された場合に発生します。

#### エラー例
```java
public class Example {
    public void method() {
        System.out.println(message);  // messageが宣言されていない
    }
}
```

#### 解決策
```java
public class Example {
    private String message = "Hello";  // フィールドとして宣言
    
    public void method() {
        System.out.println(message);
    }
}
```

### A.2.3 アクセス制御違反

#### 概要
private、protected修飾子により、アクセスが制限されているメンバーに不正にアクセスした場合に発生します。

#### エラー例
```java
public class Person {
    private String name;
}

public class Main {
    public static void main(String[] args) {
        Person p = new Person();
        p.name = "太郎";  // privateフィールドへの直接アクセス
    }
}
```

#### 解決策
```java
public class Person {
    private String name;
    
    public void setName(String name) {
        this.name = name;
    }
}

public class Main {
    public static void main(String[] args) {
        Person p = new Person();
        p.setName("太郎");  // publicメソッド経由でアクセス
    }
}
```

## A.3 デバッグとトラブルシューティング

### A.3.1 スタックトレースの読み方

#### 基本構造
```
Exception in thread "main" java.lang.NullPointerException
    at com.example.MyClass.method(MyClass.java:25)
    at com.example.Main.main(Main.java:10)
```

#### 読み方のポイント
1. **例外の種類**：最初の行で例外の種類を確認
2. **発生箇所**：atで始まる行で、上から順に呼び出し履歴を確認
3. **行番号**：括弧内の数字がエラー発生行

### A.3.2 効果的なデバッグ手法

#### printデバッグ
```java
System.out.println("変数の値: " + variable);
System.out.println("メソッド開始");
```

#### アサーションの活用
```java
assert list != null : "リストがnullです";
assert index >= 0 && index < list.size() : "インデックスが範囲外";
```

## 章別参照ガイド

このガイドの内容は、以下の章で特に関連があります。

- **第3章**：NullPointerExceptionの基礎
- **第4章**：オブジェクトの初期化とnullチェック
- **第10章**：コレクションとConcurrentModificationException
- **第11章**：ジェネリクスとClassCastException
- **第14章**：例外処理の詳細

各章の「よくあるエラーと対処法」セクションから、より詳細な情報が必要な場合は、この付録の該当セクションを参照してください。