<!-- 
校正チャンク情報
================
元ファイル: chapter03-oop-basics.md
チャンク: 5/15
行範囲: 747 - 907
作成日時: 2025-08-03 01:46:20

校正時の注意事項:
- 文章の流れは前後のチャンクを考慮してください
- このヘッダーとフッターは校正対象外です
- 校正が完了したらステータスを「completed」に変更してください
================
-->

// 使用例
public class RectangleExample {
    public static void main(String[] args) {
        // オブジェクトの作成
        Rectangle rect = new Rectangle(10.0, 5.0);
        
        // メソッドの呼び出し
        rect.displayInfo();
        
        // メソッドの戻り値を使用
        double area = rect.calculateArea();
        System.out.println("面積は" + area + "です");
        
        // 引数付きメソッドの呼び出し
        rect.resize(2.0);
        rect.displayInfo();
    }
}
```

#### 実行結果

```
幅: 10.0
高さ: 5.0
面積: 50.0
周囲: 30.0
面積は50.0です
2.0倍にリサイズしました
幅: 20.0
高さ: 10.0
面積: 200.0
周囲: 60.0
```

#### メソッドの種類

##### 1. 戻り値がないメソッド（voidメソッド）

<span class="listing-number">**サンプルコード3-29**</span>

```java
public void printMessage() {
    System.out.println("こんにちは！");
}
```

これは戻り値がないメソッドの構文例です。

##### 2. 戻り値があるメソッド

<span class="listing-number">**サンプルコード3-30**</span>

```java
public int add(int a, int b) {
    return a + b;  // int型の値を返す
}
```

これは戻り値があるメソッドの構文例です。

##### 3. 引数がないメソッド

<span class="listing-number">**サンプルコード3-31**</span>

```java
public void showTime() {
    System.out.println("現在時刻: " + new Date());
}
```

これは引数がないメソッドの構文例です。

##### 4. 複数の引数を持つメソッド

<span class="listing-number">**サンプルコード3-32**</span>

```java
public double calculateBMI(double weight, double height) {
    return weight / (height * height);
}
```

これは複数の引数を持つメソッドの構文例です。

### getter/setterメソッドの基本

オブジェクト指向プログラミングでは、フィールドを`private`にして直接アクセスを禁止し、`public`メソッドを通じてアクセスする方法が推奨されます。
これをアクセサメソッド（getter/setter）パターンと呼びます。

値の取得(getter)と設定(setter)をそれぞれ分割して作成することは、「取得はできるけど外部からは設定できないデータ」のような条件のデータを作成できることになります。

<span class="listing-number">**サンプルコード3-10**</span>

```java
public class Person {
    // privateフィールド
    private String name;
    private int age;
    
    // getter：フィールドの値を取得
    public String getName() {
        return name;
    }
    
    // setter：フィールドの値を設定
    public void setName(String name) {
        this.name = name;  // thisは現在のオブジェクトを指す
    }
    
    // 年齢のgetter
    public int getAge() {
        return age;
    }
    
    // 年齢のsetter（簡単な検証付き）
    public void setAge(int age) {
        if (age >= 0 && age <= 150) {
            this.age = age;
        } else {
            System.out.println("無効な年齢です");
        }
    }
}
```

#### 使用例と実行結果

```java
// PersonTest.java
public class PersonTest {
    public static void main(String[] args) {
        Person person = new Person();
        
        // setterを使ってデータを設定
        person.setName("山田太郎");
        person.setAge(25);
        
        // getterを使ってデータを取得
        System.out.println("名前: " + person.getName());
        System.out.println("年齢: " + person.getAge() + "歳");
        
        // 無効な年齢を設定してみる
        person.setAge(200);
    }
}
```

#### 実行結果

```
名前: 山田太郎
年齢: 25歳
無効な年齢です
```

#### getter/setterを使う利点
- フィールドへのアクセスを制御できる
- 値の設定時に検証を行える
- 将来的に実装を変更しても、外部インターフェイスは変わらない
- デバッグ時にアクセスポイントを特定しやすい

> 注意: すべてのフィールドに機械的にgetter/setterを作成するのは避けましょう。本当に外部からアクセスが必要なフィールドにのみ提供し、可能な限りオブジェクトの内部状態は隠蔽することがよい設計です。



<!-- 
================
チャンク 5/15 の終了
校正ステータス: [ ] 未完了 / [ ] 完了
================
-->
