# 総合課題1: 家計簿アプリケーション

## 📋 プロジェクト概要

個人の収支管理を効率化する家計簿アプリケーションを開発します。日常的な金銭管理を支援し、支出パターンの分析や予算管理機能を提供します。

## 🎯 学習目標

- オブジェクト指向設計の実践
- GUIアプリケーションの総合開発
- ファイルI/Oによるデータ永続化
- 例外処理とデータ検証
- テスト駆動開発の実践

## 📊 要件定義

### 機能要件

#### 1. 基本機能
- **収支入力**: 日付、金額、カテゴリ、メモの記録
- **データ表示**: 取引履歴の一覧表示と検索
- **カテゴリ管理**: 収入・支出カテゴリの追加・編集・削除
- **データ保存**: ファイル形式でのデータ永続化

#### 2. 分析機能
- **月次レポート**: 月別の収支サマリー
- **カテゴリ別分析**: カテゴリごとの支出割合
- **グラフ表示**: 棒グラフ・円グラフでの視覚化
- **支出トレンド**: 時系列での支出推移

#### 3. 予算管理機能
- **予算設定**: カテゴリごとの月次予算設定
- **予算アラート**: 予算超過時の警告表示
- **達成率表示**: 予算に対する進捗状況

#### 4. データ管理機能
- **インポート/エクスポート**: CSVファイルでのデータ交換
- **バックアップ**: データファイルのバックアップ作成
- **データ整合性**: 重複排除とデータ検証

### 非機能要件

#### パフォーマンス
- **応答性**: 1秒以内での画面遷移
- **データ量**: 10,000件以上の取引データに対応
- **メモリ使用量**: 100MB以下での動作

#### ユーザビリティ
- **直感的操作**: 3クリック以内での主要機能アクセス
- **キーボードショートカット**: 頻繁な操作のショートカット提供
- **エラーガイダンス**: 分かりやすいエラーメッセージ

## 🏗 アーキテクチャ設計

### パッケージ構成

```
personal.finance/
├── model/                  # データモデル
│   ├── Transaction.java   # 取引データ
│   ├── Category.java      # カテゴリ
│   ├── Budget.java        # 予算
│   └── Account.java       # 口座情報
├── view/                  # GUI層
│   ├── MainWindow.java    # メインウィンドウ
│   ├── TransactionForm.java # 取引入力フォーム
│   ├── ReportPanel.java   # レポート表示
│   └── BudgetPanel.java   # 予算設定
├── controller/            # コントローラー層
│   ├── TransactionController.java
│   ├── CategoryController.java
│   └── ReportController.java
├── service/              # ビジネスロジック
│   ├── TransactionService.java
│   ├── ReportService.java
│   └── BudgetService.java
├── dao/                  # データアクセス層
│   ├── FileTransactionDAO.java
│   └── CategoryDAO.java
└── util/                 # ユーティリティ
    ├── DateUtil.java
    ├── CurrencyUtil.java
    └── ValidationUtil.java
```

### クラス設計例

#### Transactionクラス
```java
public class Transaction {
    private String id;
    private LocalDate date;
    private BigDecimal amount;
    private Category category;
    private String description;
    private boolean isIncome;
    
    // コンストラクタ、getter、setter
    // equals、hashCode、toString
}
```

#### TransactionServiceクラス
```java
public class TransactionService {
    private TransactionDAO dao;
    
    public void addTransaction(Transaction transaction) throws ServiceException;
    public List<Transaction> getTransactionsByPeriod(LocalDate start, LocalDate end);
    public BigDecimal calculateBalance(LocalDate date);
    public Map<Category, BigDecimal> getCategoryTotals(YearMonth month);
}
```

## 🖥 UI設計

### メインウィンドウ構成

```
┌─────────────────────────────────────────┐
│ ファイル(F) 編集(E) 表示(V) ツール(T) ヘルプ(H) │
├─────────────────────────────────────────┤
│ [新規] [編集] [削除] | [レポート] [予算]     │
├─────────────────────────────────────────┤
│ 検索: [________] カテゴリ: [_____] 期間: [...] │
├─────────────────────────────────────────┤
│ ┌─取引履歴─────────────────────────────┐ │
│ │日付      │金額     │カテゴリ  │備考    │ │
│ │2024-01-15│-1,200  │食費     │昼食    │ │
│ │2024-01-14│-800    │交通費   │電車代  │ │
│ │...       │...     │...      │...     │ │
│ └─────────────────────────────────────┘ │
├─────────────────────────────────────────┤
│ 当月収入: ¥150,000 | 支出: ¥89,234 | 残高: ¥60,766 │
└─────────────────────────────────────────┘
```

### 取引入力フォーム

```java
public class TransactionForm extends JDialog {
    private JTextField amountField;
    private JComboBox<Category> categoryCombo;
    private JDateChooser dateChooser;
    private JTextArea descriptionArea;
    private JRadioButton incomeRadio, expenseRadio;
    
    // バリデーション機能
    private boolean validateInput() {
        // 金額、日付、カテゴリの妥当性チェック
    }
}
```

## 📈 実装フェーズ

### フェーズ1: 基本機能実装（2週間）

#### Week 1: データモデルとDAO
- [ ] Transactionクラスの実装
- [ ] Categoryクラスの実装
- [ ] FileTransactionDAOの実装
- [ ] 基本的なCRUD操作のテスト

#### Week 2: GUI基盤
- [ ] MainWindowの基本レイアウト
- [ ] TransactionFormの実装
- [ ] 基本的な取引登録機能
- [ ] 取引履歴表示機能

### フェーズ2: 分析・レポート機能（1週間）

#### Week 3: レポート機能
- [ ] ReportServiceの実装
- [ ] 月次サマリー計算
- [ ] カテゴリ別分析機能
- [ ] 基本的なグラフ表示

### フェーズ3: 高度機能（1週間）

#### Week 4: 予算管理と最終調整
- [ ] Budget機能の実装
- [ ] CSV入出力機能
- [ ] エラーハンドリング強化
- [ ] ユーザビリティ改善

## 🧪 テスト戦略

### 単体テスト
```java
@Test
public void testTransactionCreation() {
    Transaction transaction = new Transaction(
        LocalDate.now(),
        new BigDecimal("1000"),
        Category.FOOD,
        "ランチ",
        false
    );
    
    assertThat(transaction.getAmount()).isEqualTo(new BigDecimal("1000"));
    assertThat(transaction.isIncome()).isFalse();
}

@Test
public void testCategoryTotalCalculation() {
    // テストデータ作成
    List<Transaction> transactions = createTestTransactions();
    
    // カテゴリ別集計テスト
    Map<Category, BigDecimal> totals = 
        reportService.calculateCategoryTotals(transactions);
    
    assertThat(totals.get(Category.FOOD)).isEqualTo(new BigDecimal("5000"));
}
```

### 統合テスト
- ファイルI/O機能のテスト
- GUI操作シナリオのテスト
- データ整合性のテスト

## 📁 データファイル形式

### transactions.json
```json
{
  "transactions": [
    {
      "id": "uuid-1",
      "date": "2024-01-15",
      "amount": -1200,
      "categoryId": "cat-food",
      "description": "昼食",
      "isIncome": false
    }
  ],
  "categories": [
    {
      "id": "cat-food",
      "name": "食費",
      "color": "#FF6B6B",
      "isIncome": false
    }
  ]
}
```

## 🎨 高度機能（オプション）

### 機能拡張案
1. **グラフの高度化**: JFreeChartを使用した本格的なグラフ
2. **口座管理**: 複数口座対応
3. **定期取引**: 月次給与等の自動登録
4. **通貨換算**: 外貨取引対応
5. **クラウド同期**: オンラインバックアップ

### 技術的改善案
1. **データベース対応**: H2やSQLite使用
2. **プラグイン機能**: 機能の動的追加
3. **多言語対応**: 国際化機能
4. **テーマ機能**: カスタマイズ可能なUI

## 📝 学習ポイント

### 実世界スキル
- **要件分析**: ユーザーニーズの理解
- **データ設計**: 効率的なデータ構造
- **UI/UX設計**: 使いやすいインターフェイス
- **テスト設計**: 品質保証の考え方

### 技術スキル
- **MVCパターン**: 責任分離の実践
- **例外処理**: 堅牢なエラーハンドリング
- **ファイルI/O**: データ永続化技術
- **テスト**: JUnitを使用した品質管理

この課題を通じて、実際の業務で求められるアプリケーション開発スキルを総合的に習得できます。