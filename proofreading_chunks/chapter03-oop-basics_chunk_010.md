<!-- 
校正チャンク情報
================
元ファイル: chapter03-oop-basics.md
チャンク: 10/12
行範囲: 2125 - 2368
作成日時: 2025-08-02 14:34:01

校正時の注意事項:
- 文章の流れは前後のチャンクを考慮してください
- このヘッダーとフッターは校正対象外です
- 校正が完了したらステータスを「completed」に変更してください
================
-->

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
- 各要素に対して個別に`new`でオブジェクトを作成して代入する

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

実行に必要なStudentクラスの定義（簡略版）：
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

実行結果：
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
すべての要素に実体化されたオブジェクトが格納されるとは限らないので、配列の要素の中身を走査する場合は必ずnullチェックを行いましょう。


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

実行結果：
```
名前: 田中, 年齢: 20, GPA: 3.5
名前: 佐藤, 年齢: 21, GPA: 3.8
```

### クラス型配列とプリミティブ型配列の違い

クラス型の配列の初期値は`null`でしたが、プリミティブ型の配列の場合は、それぞれの型の初期値が代入されます。
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

### まとめ

オブジェクト配列は、複数のオブジェクトを一括管理するための重要なデータ構造です。プリミティブ型の配列とは異なり、参照の配列であることを理解し、nullチェックを忘れずに行うことが重要です。

より柔軟なオブジェクト管理が必要な場合は、第10章で学習するコレクションフレームワーク（`ArrayList`など）の使用を検討してください。









## 章末演習

### 演習課題へのアクセス
本章の演習課題は、GitHubリポジトリで提供されます。<br>
`https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapter03/`

### 課題構成
- 本章の基本概念の理解確認
- 応用的な実装練習
- 実践的な総合問題

詳細な課題内容と実装のヒントは、各課題フォルダ内のREADME.mdを参照してください。

#### 実装する機能
1. 基本的な計算メソッド
   - add(int a, int b) - 2つの整数の和
   - subtract(int a, int b) - 2つの整数の差
   - multiply(int a, int b) - 2つの整数の積

2. オーバーロードの実践
   - add(double a, double b) - 小数の和
   - add(int a, int b, int c) - 3つの整数の和
   - add(String a, String b) - 文字列の連結

3. 実装のポイント
   - メソッドの戻り値の型に注意
   - 引数の個数や型によるオーバーロードの理解
   - mainメソッドから各メソッドを呼び出して動作確認

#### コード構造のヒント
<span class="listing-number">**サンプルコード3-26**</span>

```java
public class MethodsPractice {
    // ここに各メソッドを実装
    
    public static void main(String[] args) {
        // メソッドの呼び出しと結果の表示
    }
}
```

#### StudentScores.java - オブジェクトの状態管理
目的： クラス設計とオブジェクトの基本操作を実践

##### 実装する機能
1. フィールドの定義
   - `name(String)` - 学生名
   - `mathScore(int)` - 数学の点数
   - `englishScore(int)` - 英語の点数
   - `scienceScore(int)` - 理科の点数

2. コンストラクタの実装
   - 引数なしコンストラクタ（デフォルト値設定）
   - 全フィールドを初期化するコンストラクタ

3. メソッドの実装
   - `getAverage()` - 3教科の平均点を計算
   - `getTotal()` - 3教科の合計点を計算
   - `displayInfo()` - 学生情報を整形して表示

4. カプセル化の実践
   - フィールドはprivateで定義
   - 読み取り専用のゲッター、検証付きセッターを実装
   - 点数の妥当性チェック（0-100の範囲）

### 発展課題への橋渡し

基礎課題を完了したら、発展課題で以下の概念を学びます。

#### Product.java - 状態変化を伴うオブジェクト
##### 新しく学ぶ概念
- オブジェクトの状態変化（在庫の増減）
- メソッド間の連携（sell → reduceStock）
- エラーハンドリングの基礎（在庫不足の処理）

##### 実装のアイデア
- 在庫数に応じた販売可否判定
- 在庫補充メソッド
- 販売可能数の計算

#### FuelExpenseCalculator.java - 複数オブジェクトの管理
##### 新しく学ぶ概念
- 複数のオブジェクトを配列で管理
- オブジェクト間の計算と集計
- より複雑なビジネスロジックの実装



<!-- 
================
チャンク 10/12 の終了
校正ステータス: [ ] 未完了 / [ ] 完了
================
-->
