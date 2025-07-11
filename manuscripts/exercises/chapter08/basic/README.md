# 第8章 基礎課題：コレクションフレームワーク

## 課題概要

本章で学んだList、Set、Mapの基本的な使い方を実践的に習得します。各コレクションの特性を理解し、適切な場面で使い分けられるようになることが目標です。

## 課題1：学生管理システム

### 目的
- ArrayListとLinkedListの使い分け
- 基本的なList操作の習得
- Iteratorの使用方法

### 要求仕様

1. **`Student`クラスの作成**
   ```java
   public class Student {
       private String id;
       private String name;
       private int score;
       // コンストラクタ、getter/setterを実装
   }
   ```

2. **`StudentManager`クラスの実装**
   - 学生の追加、削除、検索
   - 成績順でのソート
   - 平均点の計算
   - 成績ランク（A, B, C, D, F）ごとの人数集計

3. **パフォーマンス比較**
   - ArrayListとLinkedListで同じ操作を実行
   - 実行時間を計測して比較

### 実装のヒント
```java
public class StudentManager {
    private List<Student> students;
    
    public StudentManager(boolean useLinkedList) {
        this.students = useLinkedList ? 
            new LinkedList<>() : new ArrayList<>();
    }
    
    public void addStudent(Student student) {
        // 実装
    }
    
    public Student findById(String id) {
        // Iteratorを使った検索
    }
    
    public List<Student> getTopStudents(int n) {
        // 上位n名の学生を返す
    }
}
```

### 評価ポイント
- 適切なListの選択理由
- Iteratorの正しい使用
- nullチェックなどの防御的プログラミング
- 効率的なアルゴリズムの実装

## 課題2：単語カウンター

### 目的
- HashSetとTreeSetの使い分け
- Mapを使った集計処理
- コレクションの組み合わせ

### 要求仕様

1. **テキストファイルの単語解析**
   - ファイルから英文を読み込む
   - 単語の出現回数をカウント
   - 単語の重複を除いた一覧を作成

2. **解析結果の出力**
   - 出現回数の多い順に上位10単語
   - アルファベット順の単語一覧
   - 単語の総数と異なり語数

3. **ストップワードの除外**
   - "the", "a", "an", "is", "are"などの除外
   - 設定ファイルから除外単語を読み込み

### 実装のヒント
```java
public class WordCounter {
    private Map<String, Integer> wordCounts;
    private Set<String> uniqueWords;
    private Set<String> stopWords;
    
    public void analyze(String text) {
        // 単語の抽出と集計
        String[] words = text.toLowerCase().split("\\W+");
        
        for (String word : words) {
            if (!stopWords.contains(word) && !word.isEmpty()) {
                // カウントと重複除去の処理
            }
        }
    }
    
    public List<Map.Entry<String, Integer>> getTopWords(int n) {
        // 出現回数でソートして上位n個を返す
    }
}
```

### 評価ポイント
- HashMapとTreeMapの適切な使い分け
- エントリーセットを使った効率的な処理
- Comparatorを使ったソート
- ストリーム処理の基本的な活用（任意）

## 課題3：商品在庫管理システム

### 目的
- Mapの高度な使い方
- 複合キーの実装
- コレクションのネスト

### 要求仕様

1. **商品と在庫の管理**
   ```java
   public class Product {
       private String id;
       private String name;
       private double price;
       private String category;
   }
   
   public class Stock {
       private Product product;
       private int quantity;
       private String location; // 倉庫の場所
   }
   ```

2. **在庫操作**
   - 商品の入庫・出庫
   - 在庫数の確認
   - 在庫切れ商品の一覧
   - カテゴリ別の在庫金額集計

3. **複数倉庫対応**
   - 倉庫ごとの在庫管理
   - 商品の倉庫間移動
   - 全倉庫の在庫統合表示

### 実装のヒント
```java
public class InventoryManager {
    // 倉庫名 -> (商品ID -> 在庫数)
    private Map<String, Map<String, Integer>> warehouseStocks;
    private Map<String, Product> products;
    
    public void addStock(String warehouse, String productId, int quantity) {
        warehouseStocks.computeIfAbsent(warehouse, k -> new HashMap<>())
            .merge(productId, quantity, Integer::sum);
    }
    
    public Map<String, Double> getCategoryValues() {
        // カテゴリごとの在庫金額を計算
    }
}
```

### 評価ポイント
- ネストしたMapの適切な管理
- computeIfAbsentやmergeなどの便利メソッドの活用
- null安全な実装
- データの整合性保持

## 課題4：重複データクリーナー

### 目的
- Setを使った重複除去
- equalsとhashCodeの実装
- LinkedHashSetの活用

### 要求仕様

1. **CSVデータの重複除去**
   - 顧客データのCSVファイルを読み込み
   - 重複レコードを検出・除去
   - 元の順序を保持したまま出力

2. **重複判定の条件**
   - メールアドレスが同じ
   - または、名前と電話番号の組み合わせが同じ

3. **レポート機能**
   - 除去された重複データの詳細
   - 重複の理由（メール/名前＋電話）
   - 統計情報（元データ数、重複数、最終データ数）

### 実装のヒント
```java
public class Customer {
    private String name;
    private String email;
    private String phone;
    
    @Override
    public boolean equals(Object o) {
        // 重複判定ロジック
    }
    
    @Override
    public int hashCode() {
        // 適切なハッシュコード生成
    }
}

public class DataCleaner {
    public List<Customer> removeDuplicates(List<Customer> customers) {
        // LinkedHashSetを使って順序を保持しつつ重複除去
    }
}
```

### 評価ポイント
- equalsとhashCodeの正しい実装
- 複雑な重複条件の実装
- 効率的な重複検出アルゴリズム
- わかりやすいレポート出力

## 提出方法

1. 各課題のソースコードをパッケージごとに整理
2. 実行可能なMainクラスを作成
3. 実行結果とパフォーマンス測定結果を記載
4. 使用したコレクションの選択理由を説明

## 発展学習の提案

- **Collections API**：便利なユーティリティメソッドの活用
- **並行コレクション**：ConcurrentHashMapなどの学習
- **Google Guava**：より高機能なコレクションライブラリ
- **Apache Commons Collections**：拡張コレクションの活用

## 参考リソース

- Oracle公式チュートリアル：Collections Framework
- Effective Java（Joshua Bloch）：項目26-32
- Java Performance（Charlie Hunt、Binu John）