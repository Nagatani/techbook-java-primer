# 第2章 基礎課題

## 概要
第2章では、Javaの基本文法として制御構造、配列、メソッド、そしてクラスとオブジェクトの基礎を学習しました。これらの基礎課題では、学習した概念を実践的に活用し、より構造化されたプログラムを作成します。

## 学習目標
- 条件分岐と繰り返し処理を適切に使用できる
- 配列を使ったデータ処理ができる
- メソッドによる処理の分割と再利用ができる
- 基本的なクラスとオブジェクトを作成できる

## 課題一覧

### 課題2-1: Personクラスの実装
**ファイル名**: `Person.java`、`PersonTest.java`

#### 要求仕様
1. `Person`クラスを作成し、以下のフィールドを持たせる：
   - name（String型）：名前
   - age（int型）：年齢  
   - address（String型）：住所
2. コンストラクタを実装する
3. 各フィールドのgetter/setterメソッドを実装する
4. 自己紹介をする`introduce()`メソッドを実装する
5. `PersonTest`クラスでPersonオブジェクトを作成し、動作を確認する

#### 実装のヒント
- フィールドはprivateで宣言
- getterは値を返し、setterは値を設定
- introduceメソッドは情報を整形して出力

#### 実装例の骨組み
```java
// Person.java
public class Person {
    private String name;
    private int age;
    private String address;
    
    // コンストラクタ
    public Person(String name, int age, String address) {
        // 実装
    }
    
    // getter/setterメソッド
    public String getName() {
        // 実装
    }
    
    // introduceメソッド
    public void introduce() {
        // 実装
    }
}

// PersonTest.java
public class PersonTest {
    public static void main(String[] args) {
        // Personオブジェクトの作成と使用
    }
}
```

#### 評価ポイント
- カプセル化が適切に実装されている
- メソッドの命名が適切
- オブジェクトの操作が正しい

#### 発展学習
- 年齢の妥当性チェック（setterで0以上をチェック）
- 複数のコンストラクタ（オーバーロード）
- toStringメソッドの実装

---

### 課題2-2: 配列を使った成績処理
**ファイル名**: `ScoreProcessor.java`

#### 要求仕様
1. 10人分のテストの点数を配列で管理
2. 以下の処理を行うメソッドを作成：
   - 平均点を計算する`calculateAverage()`
   - 最高点を見つける`findMaxScore()`
   - 最低点を見つける`findMinScore()`
   - 合格者数を数える`countPassed()`（60点以上が合格）
3. 結果を見やすく表示する

#### 実装のヒント
- メソッドは配列を引数として受け取る
- 拡張for文を活用
- staticメソッドとして実装

#### 実装例の骨組み
```java
public class ScoreProcessor {
    public static void main(String[] args) {
        int[] scores = {85, 92, 78, 65, 89, 73, 94, 60, 52, 88};
        
        // 各メソッドを呼び出して結果を表示
        double average = calculateAverage(scores);
        System.out.println("平均点: " + average);
        // 他のメソッドも呼び出す
    }
    
    public static double calculateAverage(int[] scores) {
        // 実装
    }
    
    // 他のメソッドも定義
}
```

#### 評価ポイント
- 各メソッドが単一の責任を持つ
- 配列の操作が正しい
- メソッドの戻り値が適切

#### 発展学習
- 標準偏差の計算
- 成績分布のヒストグラム表示
- ソート機能の実装

---

### 課題2-3: 簡単な図書管理システム
**ファイル名**: `Book.java`、`LibrarySystem.java`

#### 要求仕様
1. `Book`クラスを作成：
   - title（書名）、author（著者）、isbn（ISBN）、isAvailable（貸出可能）
2. `LibrarySystem`クラスで以下の機能を実装：
   - 本の登録（最大10冊）
   - 本の一覧表示
   - 本の貸出
   - 本の返却
   - 本の検索（タイトルで検索）
3. メニュー形式で操作できるようにする

#### 実装のヒント
- Book配列で図書を管理
- メニューはswitch文で実装
- 貸出・返却はisAvailableフラグで管理

#### 実装例の骨組み
```java
// Book.java
public class Book {
    private String title;
    private String author;
    private String isbn;
    private boolean isAvailable;
    
    // コンストラクタとメソッド
}

// LibrarySystem.java
public class LibrarySystem {
    private static Book[] books = new Book[10];
    private static int bookCount = 0;
    
    public static void main(String[] args) {
        // メニュー表示と処理
    }
    
    public static void registerBook(String title, String author, String isbn) {
        // 実装
    }
    
    // 他のメソッドも実装
}
```

#### 評価ポイント
- クラス設計が適切
- 機能が正しく動作する
- ユーザーインターフェースが使いやすい

#### 発展学習
- 貸出履歴の記録
- 延滞管理機能
- ジャンル別分類

---

### 課題2-4: 文字列処理の練習
**ファイル名**: `StringProcessor.java`

#### 要求仕様
1. 文字列を受け取って以下の処理を行うメソッドを作成：
   - 文字列を逆順にする`reverseString()`
   - 回文かどうか判定する`isPalindrome()`
   - 単語数を数える`countWords()`（空白で区切られた単語）
   - 母音の数を数える`countVowels()`
2. 各メソッドの動作を確認するテストケースを作成

#### 実装のヒント
- StringBuilderを活用
- 文字列の各文字にアクセス
- 正規表現やsplitメソッドの活用

#### 実装例の骨組み
```java
public class StringProcessor {
    public static void main(String[] args) {
        String test1 = "Hello World";
        String test2 = "きつつき";
        
        // 各メソッドのテスト
        System.out.println("逆順: " + reverseString(test1));
        // 他のテストも実行
    }
    
    public static String reverseString(String str) {
        // 実装
    }
    
    // 他のメソッドも実装
}
```

#### 評価ポイント
- 文字列操作が正確
- エッジケース（空文字列など）の考慮
- 効率的なアルゴリズム

#### 発展学習
- 大文字小文字を無視した回文判定
- 最も長い単語を見つける
- 文字の出現頻度を数える

## 提出方法
1. 各課題のJavaファイルを作成
2. 十分なテストを行い、さまざまな入力で動作確認
3. クラスやメソッドにJavaDocコメントを追加
4. ソースコード（.javaファイル）のみを提出

## 注意事項
- クラス設計の基本原則を守る
- メソッドは単一の責任を持つよう設計
- 適切なアクセス修飾子を使用
- エラー処理を適切に行う