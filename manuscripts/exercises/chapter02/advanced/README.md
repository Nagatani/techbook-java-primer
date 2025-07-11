# 第2章 発展課題

## 概要
基礎課題で学んだ内容をさらに発展させ、より実践的なプログラムを作成します。複数のクラスを組み合わせた設計や、アルゴリズムの実装に挑戦します。

## 学習目標
- 複数のクラスを連携させたプログラムを設計できる
- 効率的なアルゴリズムを実装できる
- エラー処理を含む堅牢なプログラムを作成できる
- 実用的なアプリケーションを開発できる

## 課題一覧

### 課題A-1: 成績管理システム
**ファイル名**: `Student.java`、`Subject.java`、`GradeManagement.java`

#### 要求仕様
1. `Student`クラス：
   - 学生ID、氏名、学年を管理
   - 複数の科目の成績を保持
2. `Subject`クラス：
   - 科目名、単位数、成績（0-100）を管理
   - 成績評価（S/A/B/C/D）を判定
3. `GradeManagement`クラス：
   - 複数の学生を管理
   - 学生の追加・削除
   - 成績の登録・更新
   - 成績一覧の表示
   - GPA計算機能

#### 実装のヒント
- 学生は配列またはArrayListで管理
- 成績評価の基準を定数で定義
- GPA計算は4.0スケールを使用

#### 実装例の骨組み
```java
// Student.java
public class Student {
    private String studentId;
    private String name;
    private int grade;
    private Subject[] subjects;
    
    // コンストラクタとメソッド
    public double calculateGPA() {
        // GPA計算ロジック
    }
}

// Subject.java
public class Subject {
    private String name;
    private int credits;
    private int score;
    
    public String getGrade() {
        // S/A/B/C/D判定
    }
}

// GradeManagement.java
public class GradeManagement {
    private Student[] students;
    private int studentCount;
    
    public static void main(String[] args) {
        // メインプログラム
    }
}
```

#### 評価ポイント
- クラス間の責任分担が適切
- データの整合性が保たれている
- ユーザビリティの高いインターフェース

#### 発展学習
- 成績の統計情報（平均、分散など）
- CSVファイルへの出力機能
- 奨学金判定機能

---

### 課題A-2: 図書館管理システム（拡張版）
**ファイル名**: `Book.java`、`Member.java`、`Loan.java`、`LibraryManagement.java`

#### 要求仕様
1. `Book`クラス：
   - 基本情報に加えてジャンル、出版年を追加
   - 貸出可能冊数の管理
2. `Member`クラス：
   - 会員ID、氏名、連絡先
   - 貸出中の本のリスト
   - 貸出上限（5冊）
3. `Loan`クラス：
   - 貸出記録（本、会員、貸出日、返却予定日）
   - 延滞チェック機能
4. `LibraryManagement`クラス：
   - 総合的な図書館運営機能
   - 貸出・返却処理
   - 延滞者リスト
   - 人気図書ランキング

#### 実装のヒント
- 日付は簡易的に日数で管理
- 貸出期間は14日間
- トランザクション的な処理を意識

#### 実装例の骨組み
```java
// Book.java
public class Book {
    private String isbn;
    private String title;
    private String author;
    private String genre;
    private int publishYear;
    private int totalCopies;
    private int availableCopies;
    
    // メソッド
}

// Member.java
public class Member {
    private String memberId;
    private String name;
    private String contact;
    private Loan[] currentLoans;
    private int loanCount;
    
    public boolean canBorrow() {
        return loanCount < 5;
    }
}

// LibraryManagement.java
public class LibraryManagement {
    private Book[] books;
    private Member[] members;
    private Loan[] loans;
    
    public void borrowBook(String memberId, String isbn) {
        // 貸出処理
    }
}
```

#### 評価ポイント
- 複雑な業務ロジックの実装
- データの一貫性維持
- エラーケースの適切な処理

#### 発展学習
- 予約機能の実装
- 罰金計算システム
- 推薦図書機能

---

### 課題A-3: 在庫管理システム
**ファイル名**: `Product.java`、`Inventory.java`、`Transaction.java`、`InventorySystem.java`

#### 要求仕様
1. `Product`クラス：
   - 商品コード、商品名、単価、カテゴリ
2. `Inventory`クラス：
   - 商品と在庫数の管理
   - 安全在庫レベルの設定
   - 在庫警告機能
3. `Transaction`クラス：
   - 入出庫履歴の記録
   - 取引種別（入庫/出庫/棚卸）
4. `InventorySystem`クラス：
   - 在庫の入出庫処理
   - 在庫一覧表示
   - 在庫評価額の計算
   - 発注推奨リスト

#### 実装のヒント
- 在庫は負数にならないよう制御
- 取引履歴は時系列で管理
- 在庫回転率などの指標を計算

#### 評価ポイント
- ビジネスロジックの正確性
- トランザクション管理
- レポート機能の充実

#### 発展学習
- ABC分析の実装
- 在庫予測機能
- バーコード対応

## 提出方法
1. 各システムごとに必要なすべてのJavaファイルを作成
2. システムの使用方法を説明するREADMEを作成
3. 十分なテストケースで動作確認
4. ソースコードとドキュメントを提出

## 注意事項
- オブジェクト指向の原則を守る
- 責任の分離を意識したクラス設計
- 例外処理を適切に実装
- コードの可読性を重視