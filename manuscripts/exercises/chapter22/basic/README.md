# 第22章 基礎課題: Javadocとライブラリの基本

## 課題1: Javadocコメントの作成

### 背景
適切なドキュメントは、他の開発者（未来の自分を含む）がコードを理解し、正しく使用するために不可欠です。

### 要件
以下の`Library`クラスシステムに対して、完全なJavadocコメントを追加してください。

```java
public class Book {
    private String isbn;
    private String title;
    private String author;
    private int publicationYear;
    private boolean available;
    
    public Book(String isbn, String title, String author, int publicationYear) {
        // コンストラクタの実装
    }
    
    public void borrowBook() {
        // 本を借りる処理
    }
    
    public void returnBook() {
        // 本を返す処理
    }
    
    // getter/setterメソッド
}

public class Library {
    private Map<String, Book> books;
    private Map<String, List<Book>> borrowedBooks;
    
    public Library() {
        // コンストラクタの実装
    }
    
    public void addBook(Book book) {
        // 本を追加する処理
    }
    
    public Book findBookByIsbn(String isbn) {
        // ISBNで本を検索
    }
    
    public List<Book> findBooksByAuthor(String author) {
        // 著者名で本を検索
    }
    
    public void borrowBook(String userId, String isbn) throws BookNotAvailableException {
        // 本を借りる処理
    }
    
    public void returnBook(String userId, String isbn) {
        // 本を返す処理
    }
}
```

### Javadocに含めるべき内容
1. クラスレベルのドキュメント（目的、使用例）
2. すべてのpublicメソッドの説明
3. `@param`、`@return`、`@throws`タグ
4. `@since`、`@author`タグ
5. 使用例を含む`{@code}`ブロック

### 提出物
- Javadocコメントを追加したソースコード
- `javadoc`コマンドで生成したHTMLドキュメント

---

## 課題2: Gsonライブラリの活用

### 背景
外部ライブラリを使用して、JSONデータの処理を行います。

### 要件
Gsonライブラリを使用して、以下の機能を実装してください。

#### 1. データモデルの作成
```java
public class Student {
    private String id;
    private String name;
    private int age;
    private List<Course> courses;
    // コンストラクタ、getter/setter
}

public class Course {
    private String courseId;
    private String courseName;
    private int credits;
    private double grade;
    // コンストラクタ、getter/setter
}
```

#### 2. 実装すべき機能
- `StudentManager`クラスを作成
- 学生情報をJSONファイルに保存する機能
- JSONファイルから学生情報を読み込む機能
- 学生リストをJSON形式で出力する機能

### サンプルJSON
```json
{
  "id": "S001",
  "name": "田中太郎",
  "age": 20,
  "courses": [
    {
      "courseId": "CS101",
      "courseName": "プログラミング基礎",
      "credits": 4,
      "grade": 3.5
    },
    {
      "courseId": "MA201",
      "courseName": "線形代数",
      "credits": 3,
      "grade": 4.0
    }
  ]
}
```

### 実装のヒント
- `GsonBuilder`を使用して整形されたJSON出力
- 日付フォーマットのカスタマイズ
- null値の扱い方

---

## 課題3: Apache Commons Langの活用

### 背景
Apache Commons Langを使用して、文字列処理を効率化します。

### 要件
以下の`TextProcessor`クラスを、Apache Commons Langを使用して実装してください。

```java
public class TextProcessor {
    
    // 文字列が空（null、空文字、空白のみ）かチェック
    public boolean isBlank(String text) {
        // StringUtils.isBlank()を使用
    }
    
    // 文字列を指定文字数で省略（...を追加）
    public String abbreviate(String text, int maxLength) {
        // StringUtils.abbreviate()を使用
    }
    
    // 文字列の最初の文字を大文字に
    public String capitalize(String text) {
        // StringUtils.capitalize()を使用
    }
    
    // 配列を文字列に結合
    public String joinArray(String[] array, String separator) {
        // StringUtils.join()を使用
    }
    
    // 文字列から数字のみを抽出
    public String extractDigits(String text) {
        // StringUtils.getDigits()を使用
    }
}
```

### テストケースも作成
各メソッドに対して、少なくとも3つのテストケースを作成してください。

---

## 課題4: Maven/Gradleプロジェクトの作成

### 背景
実際の開発では、ビルドツールを使用して依存関係を管理します。

### 要件
以下のいずれかを選択して、プロジェクトを作成してください。

#### Mavenの場合
1. `pom.xml`を作成
2. 以下の依存関係を追加：
   - JUnit 5
   - Gson
   - Apache Commons Lang
   - SLF4J + Logback

#### Gradleの場合
1. `build.gradle`を作成
2. 同じ依存関係を追加

### 実装内容
1. 簡単なアプリケーションを作成（課題2と3の機能を統合）
2. `mvn package`または`gradle build`でビルド
3. 実行可能JARファイルの生成

### 提出物
- プロジェクトのディレクトリ構造
- `pom.xml`または`build.gradle`
- ビルドログ
- 生成されたJARファイル

## 提出方法
1. 各課題のソースコードを適切なディレクトリ構造で配置
2. ビルド設定ファイル（pom.xml/build.gradle）を含める
3. 実行結果のスクリーンショット
4. 簡単な実行手順書（README.md）

## 評価基準
- Javadocの完全性と分かりやすさ
- 外部ライブラリの適切な使用
- エラーハンドリングの実装
- ビルドの成功とプロジェクト構造の適切さ