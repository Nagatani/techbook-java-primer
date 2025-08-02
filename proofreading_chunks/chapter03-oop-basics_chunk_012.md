<!-- 
校正チャンク情報
================
元ファイル: chapter03-oop-basics.md
チャンク: 12/15
行範囲: 2078 - 2268
作成日時: 2025-08-03 01:46:20

校正時の注意事項:
- 文章の流れは前後のチャンクを考慮してください
- このヘッダーとフッターは校正対象外です
- 校正が完了したらステータスを「completed」に変更してください
================
-->

#### 定数の定義

<span class="listing-number">**サンプルコード3-39**</span>

   ```java
   public class Constants {
       public static final double PI = 3.14159265359;
       public static final int MAX_RETRY_COUNT = 3;
   }
   ```

   これは定数定義の例です。

### staticの設計上の注意点

1. 過度な使用を避ける
   - staticメソッドはテストが困難になる場合がある
   - オブジェクト指向の柔軟性が失われる
2. 状態を持たせない
   - staticフィールドは慎重に使用する
   - 可変なstaticフィールドは避ける
3. 適切な責任分担
   - オブジェクトの振る舞いはインスタンスメソッドを使う
   - ユーティリティ的な処理はstaticメソッドを使う

### まとめ

static修飾子は強力な機能ですが、オブジェクト指向プログラミングの基本原則と相反する場合があります。
適切な場面で適切に使用することで、よりよい設計のプログラムを作成できます。
第7章以降では、インターフェイスや抽象クラスと組み合わせた、より高度なstatic活用パターンを学習します。









## オブジェクトの配列

### オブジェクト配列の基本

第2章で基本的な配列の使い方を学習しました。ここでは、オブジェクト指向プログラミングの文脈で重要な「オブジェクトの配列」について学習します。

プリミティブ型の配列とは異なり、オブジェクトの配列は参照の配列として実装されます。これは、配列の各要素がオブジェクトそのものではなく、オブジェクトへの参照を保持することを意味します。

### オブジェクト配列の宣言と初期化

<span class="listing-number">**サンプルコード3-22**</span>

```java
// オブジェクト配列の宣言
Student[] students;  // Studentクラスの配列

// 配列の初期化（参照の配列を作成）
students = new Student[5];  // 5人分のStudent参照を保持できる配列

// 各要素にオブジェクトを代入
students[0] = new Student("田中太郎", 20, 3.5);
students[1] = new Student("佐藤花子", 21, 3.8);
// students[2]〜students[4]はnullのまま
```

これはオブジェクト配列の宣言と初期化の構文例です。

#### 重要なポイント

- `new Student[5]`はStudentオブジェクト5個を作るのではなく、Studentへの参照5個分の配列を作る
- 初期化直後の各要素は`null`である
- 各要素に対して個別に`new`でオブジェクトを作成して代入する必要がある

### オブジェクト配列の操作

<span class="listing-number">**サンプルコード3-23**</span>

```java
public class StudentArrayExample {
    public static void main(String[] args) {
        // オブジェクト配列の作成と初期化
        Student[] students = new Student[3];
        
        // 各要素にオブジェクトを代入
        students[0] = new Student("田中太郎", 20, 3.5);
        students[1] = new Student("佐藤花子", 21, 3.8);
        students[2] = new Student("鈴木一郎", 19, 3.2);
        
        // オブジェクト配列の反復処理
        System.out.println("学生一覧:");
        for (Student student : students) {
            student.display();  // 各オブジェクトのメソッドを呼び出し
        }
        
        // GPAの平均を計算
        double totalGpa = 0;
        for (Student student : students) {
            totalGpa += student.getGpa();
        }
        double averageGpa = totalGpa / students.length;
        System.out.println("\n平均GPA: " + averageGpa);
    }
}
```

#### Studentクラスの定義（簡略版）
実行に必要なStudentクラスを以下に示します。
```java
class Student {
    private String name;
    private int age;
    private double gpa;
    
    public Student(String name, int age, double gpa) {
        this.name = name;
        this.age = age;
        this.gpa = gpa;
    }
    
    public void display() {
        System.out.println("名前: " + name + ", 年齢: " + age + ", GPA: " + gpa);
    }
    
    public double getGpa() {
        return gpa;
    }
}
```

#### 実行結果

```
学生一覧:
名前: 田中太郎, 年齢: 20, GPA: 3.5
名前: 佐藤花子, 年齢: 21, GPA: 3.8
名前: 鈴木一郎, 年齢: 19, GPA: 3.2

平均GPA: 3.5
```

### オブジェクト配列のnullチェック

オブジェクト配列を操作する際のもっとも重要な注意点は、nullチェックです。

配列を`new`で初期化を行うと、配列の要素それぞれの初期値は`null`です。
すべての要素に実体化されたオブジェクトが格納されているとは限らないので、配列の要素の中身を走査する場合は必ずnullチェックを行いましょう。


<span class="listing-number">**サンプルコード3-24**</span>

```java
public class NullCheckExample {
    public static void main(String[] args) {
        Student[] students = new Student[5];
        students[0] = new Student("田中", 20, 3.5);
        students[1] = new Student("佐藤", 21, 3.8);
        // students[2]〜[4]はnull
        
        // nullチェックを忘れるとNullPointerExceptionが発生
        for (int i = 0; i < students.length; i++) {
            if (students[i] != null) {  // nullチェックが必須
                students[i].display();
            }
        }
    }
}
```

#### 実行結果

```
名前: 田中, 年齢: 20, GPA: 3.5
名前: 佐藤, 年齢: 21, GPA: 3.8
```

### クラス型配列とプリミティブ型配列の違い

クラス型の配列の初期値は`null`でしたが、プリミティブ型の配列の場合は、それぞれの型の初期値が代入されています。
プリミティブ型を指定した配列には、`null`は入りません。

<span class="listing-number">**サンプルコード3-25**</span>

```java
// プリミティブ型の配列
int[] numbers = new int[5];     // 各要素は0で初期化される
System.out.println(numbers[0]); // 0が表示される

// オブジェクトの配列
Student[] students = new Student[5]; // 各要素はnullで初期化される
System.out.println(students[0]);     // nullが表示される
```

これは配列の初期値の違いを示す構文例です。



<!-- 
================
チャンク 12/15 の終了
校正ステータス: [ ] 未完了 / [ ] 完了
================
-->
