# 第8章 解答例

## 概要

第8章では、Javaコレクションフレームワークについて学習します。適切なコレクションクラスの選択、効率的なデータ操作、Comparatorの活用などを重視した実装を行います。

## 解答例一覧

### 1. 学生管理システム (StudentManager系)

- **StudentManager.java**: 学生管理システムのメインクラス
- **StudentManagerTest.java**: 学生管理システムのテスト

#### 使用コレクション
- `HashMap<String, Student>`: ID別の高速検索
- `TreeSet<Student>`: 成績順の自動ソート
- `Map<String, List<Student>>`: 学科別のグループ化
- `ArrayList<Student>`: 登録順序の保持

#### 学習ポイント
- 適切なコレクションクラスの選択
- 複数のインデックスによる効率的な検索
- Stream APIを活用した統計計算
- Comparatorによる複合ソート

### 2. 図書館管理システム (LibrarySystem系)

- **LibrarySystem.java**: 図書館管理システムのメインクラス
- **LibrarySystemTest.java**: 図書館管理システムのテスト

#### 使用コレクション
- `HashMap<String, Book>`: 書籍のID別管理
- `Map<String, List<Book>>`: カテゴリ別・著者別インデックス
- `Queue<String>`: 予約待ちキュー
- `Map<String, List<LoanRecord>>`: 利用者別貸出記録

#### 学習ポイント
- Queueインターフェイスの活用（予約システム）
- 複数のインデックスによる効率的な検索
- 日付操作とStreamAPIの組み合わせ
- 列挙型を活用した設計

### 3. ショッピングカートシステム (ShoppingCart系)

- **ShoppingCart.java**: ショッピングカートのメインクラス
- **ShoppingCartTest.java**: ショッピングカートのテスト

#### 使用コレクション
- `LinkedHashMap<String, CartItem>`: 追加順序を保持したカート
- `HashMap<String, Product>`: 商品管理
- `Set<String>`: 適用済み割引の管理
- `Map<String, List<Product>>`: カテゴリ別商品管理

#### 学習ポイント
- BigDecimalを使った正確な金額計算
- LinkedHashMapによる順序保持
- 複雑な割引計算ロジック
- 在庫管理とカート操作の整合性

### 4. データ処理システム (DataProcessor系)

- **DataProcessor.java**: データ処理システムのメインクラス
- **DataProcessorTest.java**: データ処理システムのテスト

#### 使用コレクション
- `ArrayList<DataEntry>`: データの順序保持
- `HashMap<String, DataEntry>`: ID別の高速検索
- `TreeMap<Double, List<DataEntry>>`: 値による範囲検索
- `TreeMap<Date, List<DataEntry>>`: 時刻による範囲検索

#### 学習ポイント
- TreeMapによる範囲検索の実装
- 統計計算とキャッシュ機能
- 複数のインデックスによる効率的な検索
- エラーハンドリングと処理結果の管理

## コレクション選択の指針

### 1. List系

| コレクション | 特徴 | 使用場面 |
|-------------|------|----------|
| ArrayList | 高速なランダムアクセス | 検索が多い、サイズ変更が少ない |
| LinkedList | 高速な挿入・削除 | 頻繁な挿入・削除が発生する |
| Vector | 同期化されたArrayList | マルチスレッド環境（非推奨） |

### 2. Set系

| コレクション | 特徴 | 使用場面 |
|-------------|------|----------|
| HashSet | 高速な検索・追加・削除 | 順序不要、重複排除 |
| LinkedHashSet | 挿入順序を保持 | 順序が重要、重複排除 |
| TreeSet | 自動ソート | ソート済み、重複排除 |

### 3. Map系

| コレクション | 特徴 | 使用場面 |
|-------------|------|----------|
| HashMap | 高速な検索・追加・削除 | 順序不要、キー-値ペア |
| LinkedHashMap | 挿入順序を保持 | 順序が重要、キー-値ペア |
| TreeMap | 自動ソート | ソート済み、範囲検索 |

### 4. Queue系

| コレクション | 特徴 | 使用場面 |
|-------------|------|----------|
| ArrayDeque | 両端操作が高速 | スタック、キュー |
| LinkedList | Queueインターフェイス実装 | キュー操作 |
| PriorityQueue | 優先度付きキュー | 優先度に基づく処理 |

## 実行方法

### テストの実行

```bash
# 全テストの実行
./gradlew test

# 特定の章のテストのみ実行
./gradlew test --tests "chapter08.*"
```

### 個別システムの実行

```bash
# 学生管理システムのテスト
java -cp build/classes/java/main:build/classes/java/test chapter08.solutions.StudentManagerTest

# 図書館システムのテスト
java -cp build/classes/java/main:build/classes/java/test chapter08.solutions.LibrarySystemTest
```

## 重要なポイント

### 1. コレクションの選択基準

- **検索頻度**: 検索が多い場合はHashMap, HashSet
- **順序の重要性**: 順序が重要な場合はLinkedHashMap, LinkedHashSet
- **ソートの必要性**: 自動ソートが必要な場合はTreeMap, TreeSet
- **範囲検索**: 範囲検索が必要な場合はTreeMap

### 2. Comparatorの活用

```java
// 複合ソート
Comparator<Student> comparator = 
    Comparator.comparingInt(Student::getScore)
             .reversed()
             .thenComparing(Student::getName);
```

### 3. Stream APIとの組み合わせ

```java
// 統計計算
DoubleSummaryStatistics stats = 
    students.stream()
           .mapToDouble(Student::getScore)
           .summaryStatistics();
```

### 4. パフォーマンスの考慮

- 大量データの場合は適切なコレクションの選択が重要
- 頻繁な検索にはHashMapを使用
- 範囲検索にはTreeMapを使用
- 順序が重要な場合はLinkedHashMapを使用

## 発展的な学習

1. **カスタムコレクション**: 特定の用途に最適化されたコレクションの実装
2. **並行コレクション**: ConcurrentHashMapなどのスレッドセーフなコレクション
3. **イミュータブルコレクション**: 不変コレクションの活用
4. **ストリーム処理**: より複雑なデータ処理パイプラインの構築

これらの解答例を通じて、適切なコレクションクラスの選択方法と効率的なデータ操作手法を身につけることができます。