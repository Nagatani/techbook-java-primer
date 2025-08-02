<!-- 
校正チャンク情報
================
元ファイル: chapter03-oop-basics.md
チャンク: 14/15
行範囲: 2460 - 2641
作成日時: 2025-08-03 01:46:20

校正時の注意事項:
- 文章の流れは前後のチャンクを考慮してください
- このヘッダーとフッターは校正対象外です
- 校正が完了したらステータスを「completed」に変更してください
================
-->

#### 1. クラスとインスタンスの概念の混乱

問題: クラス名で直接インスタンスメソッドを呼び出そうとしてエラーになる

<span class="listing-number">**サンプルコード3-40**</span>

```java
// エラー例
Student.setName("太郎");  // クラス名で呼び出している
```

解決策:

<span class="listing-number">**サンプルコード3-41**</span>

```java
// 正しい例
Student student = new Student();  // インスタンスを作成
student.setName("太郎");          // インスタンスから呼び出し
```

重要なポイント:

- クラスは設計図、インスタンスは実体
- インスタンスメソッドはnewで作成したオブジェクトから呼び出す
- IDEでのプログラミングの場合は自動補完機能を活用する

#### 2. static vs non-staticの混乱
問題: staticメソッドからインスタンスメンバーにアクセスしようとする

<span class="listing-number">**サンプルコード3-42**</span>

```java
// エラー例
public static void main(String[] args) {
    int result = add(5, 3);  // インスタンスメソッドを直接呼び出し
}
```

解決策:
<span class="listing-number">**サンプルコード3-43**</span>

```java
// 正しい例
public static void main(String[] args) {
    Calculator calc = new Calculator();
    int result = calc.add(5, 3);  // インスタンス経由で呼び出し
}
```

##### 重要なポイント
- staticメソッドはクラスに属し、インスタンスなしで呼び出せる
- インスタンスメソッドはオブジェクトに属し、インスタンスが必要

#### 3. 基本的な構文エラー
問題: Javaの基本構文を間違えて記述する

<span class="listing-number">**サンプルコード3-44**</span>

```java
// よくあるエラー例
System.out.println("Hello")  // セミコロン忘れ
Public class MyClass { }      // 大文字のP
```

解決策:
<span class="listing-number">**サンプルコード3-45**</span>

```java
// 正しい例
System.out.println("Hello");  // セミコロンを追加
public class MyClass { }      // 小文字のp
```

##### 重要なポイント
- すべての文の末尾にはセミコロンが必要
- キーワードは小文字（public、class、void等）
- クラス名は大文字で始める（慣例）

### 関連する共通エラー

以下のエラーも本章の内容に関連します。

- 　`NullPointerException`（→ 付録A.1.1）
    - とくに初期化されていないフィールドへのアクセスで発生しやすい
- コンストラクタエラー（→ 第4章）
    - 本章では基本概念のみ、詳細は第4章で学習
- アクセス修飾子エラー（→ 付録A.2.3）
    - `private`フィールドへの不正アクセス

### デバッグのヒント

#### エラーに遭遇した際の基本的な対処法

1. エラーメッセージを読む
    + 行番号とエラーの種類を確認
2. クラスとインスタンスの区別
    + staticかどうかを確認
3. 初期化の確認
    + すべてのフィールドが初期化されているか確認

### さらに学ぶには

- 付録H: Java共通エラーガイド（基本的なエラーパターン）
- 第4章: より詳細なコンストラクタとフィールドの扱い
- 第14章: 例外処理の体系的な学習

## オブジェクトの等価性とhashCode

オブジェクト指向プログラミングでは、オブジェクト同士が「等しい」かどうかを判定します。Javaでは、`==`演算子と`equals`メソッドの違いを理解することが重要です。

### ==演算子とequalsメソッドの違い

<span class="listing-number">**サンプルコード3-46**</span>

```java
public class StringComparison {
    public static void main(String[] args) {
        String str1 = new String("Hello");
        String str2 = new String("Hello");
        String str3 = str1;
        
        // ==演算子：参照の比較
        System.out.println(str1 == str2);  // false（異なるオブジェクト）
        System.out.println(str1 == str3);  // true（同じオブジェクトを参照）
        
        // equalsメソッド：値の比較
        System.out.println(str1.equals(str2));  // true（同じ値）
        System.out.println(str1.equals(str3));  // true（同じ値）
    }
}
```

### カスタムクラスでのequalsメソッドの実装

独自のクラスで`equals`メソッドを適切に実装する方法を示します。

<span class="listing-number">**サンプルコード3-47**</span>

```java
public class Student {
    private String id;
    private String name;
    
    public Student(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    @Override
    public boolean equals(Object obj) {
        // 同一オブジェクトの場合
        if (this == obj) return true;
        
        // nullまたは異なるクラスの場合
        if (obj == null || getClass() != obj.getClass()) return false;
        
        // 型変換して比較
        Student student = (Student) obj;
        return id != null && id.equals(student.id);
    }
    
    @Override
    public int hashCode() {
        // equalsで使用するフィールドと同じフィールドを使用
        return id != null ? id.hashCode() : 0;
    }
}
```

### equals/hashCodeの契約

`equals`メソッドをオーバーライドする場合、必ず`hashCode`メソッドもオーバーライドしてください。これは以下の契約を満たすために必要です。

1. `equals`で等しいと判定される2つのオブジェクトは、同じ`hashCode`を返す
2. `hashCode`が同じでも、`equals`で等しいとは限らない（ハッシュ衝突）
3. プログラムの実行中、同じオブジェクトの`hashCode`は一貫している

## モダンJavaにおけるオブジェクト指向

Java 21 LTSでは、オブジェクト指向プログラミングをより簡潔に記述できる新機能が追加されています。



<!-- 
================
チャンク 14/15 の終了
校正ステータス: [ ] 未完了 / [ ] 完了
================
-->
