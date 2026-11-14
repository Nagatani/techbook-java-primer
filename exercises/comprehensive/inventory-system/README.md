# 総合課題2: 在庫管理システム

## プロジェクト概要

中小企業向けの包括的な在庫管理システムを開発します。商品マスター管理から入出庫処理、発注管理まで、実際の業務で使用される本格的なシステムを構築します。

## 学習目標

- エンタープライズアプリケーションの設計
- 複雑なデータ関係の管理
- バッチ処理とリアルタイム更新
- 業務フローの理解とシステム化
- 大量データの効率的処理

## 要件定義

### 機能要件

#### 1. マスター管理機能
- **商品マスター**: 商品コード、名称、規格、価格、在庫上下限
- **取引先マスター**: 仕入先・販売先の管理
- **倉庫マスター**: 複数倉庫・ロケーション管理
- **ユーザー管理**: 権限付きユーザー管理

#### 2. 在庫管理機能
- **現在庫照会**: リアルタイム在庫状況表示
- **在庫移動**: 倉庫間・ロケーション間移動
- **棚卸機能**: 実地棚卸と差異調整
- **ロット管理**: 製造日・有効期限管理

#### 3. 入出庫管理機能
- **入庫処理**: 発注品の受入登録
- **出庫処理**: 販売・生産出庫の登録
- **予約管理**: 入出庫予定の管理
- **履歴追跡**: 在庫移動の完全な追跡

#### 4. 発注管理機能
- **自動発注**: 発注点を下回った商品の自動検出
- **発注書作成**: 発注書の生成と印刷
- **納期管理**: 発注から納品までの進捗管理
- **仕入実績**: 仕入価格・数量の実績管理

#### 5. レポート機能
- **在庫一覧**: 各種条件での在庫レポート
- **入出庫履歴**: 期間指定での移動履歴
- **回転率分析**: 商品回転率の計算
- **ABC分析**: 売上・利益貢献度分析

### 非機能要件

#### パフォーマンス
- **応答性**: 検索処理2秒以内
- **データ量**: 10万件以上の商品、100万件の取引履歴
- **同時利用**: 10ユーザー同時アクセス対応

#### 信頼性
- **データ整合性**: トランザクション処理による一貫性保証
- **バックアップ**: 日次自動バックアップ
- **ログ管理**: 操作履歴の完全記録

## アーキテクチャ設計

### パッケージ構成

```
inventory.system/
├── model/                     # ドメインモデル
│   ├── Product.java          # 商品
│   ├── Inventory.java        # 在庫
│   ├── Transaction.java      # 取引
│   ├── Warehouse.java        # 倉庫
│   ├── Supplier.java         # 取引先
│   └── PurchaseOrder.java    # 発注
├── view/                     # プレゼンテーション層
│   ├── MainFrame.java        # メインウィンドウ
│   ├── ProductManagement.java # 商品管理画面
│   ├── InventoryView.java    # 在庫照会画面
│   ├── TransactionForm.java  # 入出庫登録
│   └── ReportViewer.java     # レポート表示
├── controller/               # コントローラー層
│   ├── ProductController.java
│   ├── InventoryController.java
│   ├── TransactionController.java
│   └── ReportController.java
├── service/                  # ビジネスロジック層
│   ├── InventoryService.java
│   ├── TransactionService.java
│   ├── OrderService.java
│   └── ReportService.java
├── dao/                      # データアクセス層
│   ├── ProductDAO.java
│   ├── InventoryDAO.java
│   ├── TransactionDAO.java
│   └── DatabaseManager.java
├── batch/                    # バッチ処理
│   ├── AutoOrderBatch.java   # 自動発注
│   ├── InventoryUpdate.java  # 在庫更新
│   └── ReportGeneration.java # レポート生成
└── util/                     # ユーティリティ
    ├── DateTimeUtil.java
    ├── NumberUtil.java
    └── SecurityUtil.java
```

### 主要クラス設計

#### Productクラス（商品マスター）
```java
public class Product {
    private String productCode;      // 商品コード
    private String productName;      // 商品名
    private String specification;    // 規格
    private BigDecimal unitPrice;    // 単価
    private String unit;             // 単位
    private int reorderPoint;        // 発注点
    private int maxStock;           // 最大在庫
    private Category category;       // カテゴリ
    private Supplier defaultSupplier; // 主要仕入先
    private boolean isActive;        // 有効フラグ
    
    // JAN/EANコード、重量、サイズ等の詳細情報
}
```

#### Inventoryクラス（在庫情報）
```java
public class Inventory {
    private String inventoryId;
    private Product product;
    private Warehouse warehouse;
    private String location;         // ロケーション
    private int quantity;           // 数量
    private String lotNumber;       // ロット番号
    private LocalDate manufacturingDate; // 製造日
    private LocalDate expiryDate;   // 有効期限
    private InventoryStatus status; // 在庫状態
    private LocalDateTime lastUpdated;
    
    // 在庫評価額、保留数量等
}
```

#### TransactionServiceクラス
```java
public class TransactionService {
    private TransactionDAO transactionDAO;
    private InventoryDAO inventoryDAO;
    
    @Transactional
    public void processInbound(InboundTransaction transaction) 
            throws ServiceException {
        // 入庫処理：在庫増加、履歴記録
        validateTransaction(transaction);
        updateInventory(transaction);
        recordTransaction(transaction);
        checkReorderPoint(transaction.getProduct());
    }
    
    @Transactional
    public void processOutbound(OutboundTransaction transaction) 
            throws ServiceException {
        // 出庫処理：在庫減少、履歴記録
        validateAvailability(transaction);
        allocateInventory(transaction);
        updateInventory(transaction);
        recordTransaction(transaction);
    }
}
```

## UI設計

### メインダッシュボード

```
┌─────────────────────────────────────────────────────────────┐
│ 在庫管理システム - [ユーザー名] [ログアウト]                     │
├─────────────────────────────────────────────────────────────┤
│ [商品管理] [在庫照会] [入出庫] [発注管理] [レポート] [設定]      │
├─────────────────────────────────────────────────────────────┤
│ ┌─クイックアクセス─┐ ┌─アラート─────────────────────┐   │
│ │入庫登録     →  │ │!  発注点以下: 15商品              │   │
│ │出庫登録     →  │ │!  期限間近:   8商品               │   │
│ │在庫照会     →  │ │   発注提案:  12商品               │   │
│ │発注作成     →  │ └─────────────────────────────────┘   │
│ └─────────────────┘                                         │
│ ┌─本日の実績───────────────────────────────────────┐   │
│ │入庫件数: 23件   出庫件数: 31件   在庫移動: 5件       │   │
│ │総在庫金額: ¥12,450,000  前日比: +2.3%              │   │
│ └─────────────────────────────────────────────────┘   │
│ ┌─在庫状況グラフ──────────────────────────────────┐   │
│ │[月次推移グラフ]    [カテゴリ別在庫]    [回転率]      │   │
│ └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### 在庫照会画面

```java
public class InventoryView extends JPanel {
    private JTable inventoryTable;
    private InventoryTableModel tableModel;
    private JTextField searchField;
    private JComboBox<Warehouse> warehouseFilter;
    private JComboBox<Category> categoryFilter;
    
    // フィルタリング機能
    private void applyFilters() {
        String searchText = searchField.getText();
        Warehouse warehouse = (Warehouse) warehouseFilter.getSelectedItem();
        Category category = (Category) categoryFilter.getSelectedItem();
        
        List<InventoryView> filtered = inventoryService.search(
            searchText, warehouse, category);
        tableModel.updateData(filtered);
    }
    
    // 在庫詳細表示
    private void showInventoryDetail(String productCode) {
        InventoryDetailDialog dialog = new InventoryDetailDialog(
            this, productCode);
        dialog.setVisible(true);
    }
}
```

## 実装フェーズ

### フェーズ1: 基盤構築（2週間）

#### Week 1: データ層の構築
- [ ] データベース設計とテーブル作成
- [ ] 基本エンティティクラスの実装
- [ ] DAO層の実装とテスト
- [ ] データベース接続とトランザクション管理

#### Week 2: ビジネスロジック層
- [ ] サービスクラスの実装
- [ ] 在庫更新ロジックの実装
- [ ] バリデーション機能の実装
- [ ] 例外ハンドリングの実装

### フェーズ2: 基本機能実装（2週間）

#### Week 3: 基本CRUD機能
- [ ] 商品マスター管理画面
- [ ] 在庫照会機能
- [ ] 基本的な入出庫処理
- [ ] 検索・フィルタリング機能

#### Week 4: 入出庫機能
- [ ] 入庫処理の完全実装
- [ ] 出庫処理の完全実装
- [ ] 在庫移動機能
- [ ] ロット管理機能

### フェーズ3: 発注・レポート機能（1週間）

#### Week 5: 高度機能
- [ ] 自動発注機能
- [ ] 発注書生成
- [ ] レポート機能
- [ ] バッチ処理

### フェーズ4: 最終調整（1週間）

#### Week 6: 品質向上
- [ ] パフォーマンス最適化
- [ ] エラーハンドリング強化
- [ ] ユーザビリティ改善
- [ ] 総合テスト

## テスト戦略

### 単体テスト例

```java
@Test
public void testInventoryUpdate() {
    // Given
    Product product = createTestProduct();
    Inventory inventory = new Inventory(product, warehouse, 100);
    
    // When
    inventoryService.updateQuantity(inventory.getId(), 150);
    
    // Then
    Inventory updated = inventoryDAO.findById(inventory.getId());
    assertThat(updated.getQuantity()).isEqualTo(150);
}

@Test
public void testReorderPointDetection() {
    // 発注点以下の商品検出テスト
    List<Product> products = orderService.findProductsBelowReorderPoint();
    
    assertThat(products).hasSize(3);
    assertThat(products).extracting("productCode")
                       .contains("PROD001", "PROD002", "PROD003");
}
```

### 統合テスト
```java
@Test
@Transactional
public void testCompleteInboundProcess() {
    // 入庫処理の完全フローテスト
    InboundTransaction transaction = createInboundTransaction();
    
    transactionService.processInbound(transaction);
    
    // 在庫が増加していることを確認
    Inventory inventory = inventoryDAO.findByProduct(transaction.getProduct());
    assertThat(inventory.getQuantity()).isEqualTo(initialQuantity + transaction.getQuantity());
    
    // 取引履歴が記録されていることを確認
    List<Transaction> history = transactionDAO.findByProduct(transaction.getProduct());
    assertThat(history).hasSize(1);
}
```

## データベース設計

### 主要テーブル構成

```sql
-- 商品マスター
CREATE TABLE products (
    product_code VARCHAR(20) PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    specification VARCHAR(200),
    unit_price DECIMAL(10,2),
    unit VARCHAR(10),
    reorder_point INTEGER,
    max_stock INTEGER,
    category_id INTEGER,
    supplier_id INTEGER,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- 在庫テーブル
CREATE TABLE inventory (
    inventory_id VARCHAR(36) PRIMARY KEY,
    product_code VARCHAR(20) REFERENCES products(product_code),
    warehouse_id INTEGER REFERENCES warehouses(warehouse_id),
    location VARCHAR(20),
    quantity INTEGER NOT NULL,
    lot_number VARCHAR(50),
    manufacturing_date DATE,
    expiry_date DATE,
    status VARCHAR(20),
    last_updated TIMESTAMP
);

-- 取引履歴
CREATE TABLE transactions (
    transaction_id VARCHAR(36) PRIMARY KEY,
    transaction_type VARCHAR(10) NOT NULL, -- INBOUND/OUTBOUND
    product_code VARCHAR(20) REFERENCES products(product_code),
    warehouse_id INTEGER REFERENCES warehouses(warehouse_id),
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10,2),
    reference_no VARCHAR(50),
    transaction_date TIMESTAMP,
    created_by VARCHAR(50)
);
```

## 高度機能（オプション）

### 発展機能
1. **バーコード対応**: JANコード読み取り機能
2. **モバイル対応**: ハンディターミナル連携
3. **予測分析**: 需要予測とAI活用
4. **多言語対応**: 国際展開向け機能
5. **API提供**: 他システムとの連携

### 技術的改善
1. **分散処理**: 複数拠点での分散在庫管理
2. **リアルタイム同期**: WebSocket活用
3. **キャッシュ最適化**: Redis等の活用
4. **監査ログ**: 完全な操作履歴追跡

## 学習ポイント

### ビジネススキル
- **業務分析**: 在庫管理業務の理解
- **要件定義**: ステークホルダーとの調整
- **データ設計**: 業務に最適なデータ構造
- **運用設計**: システム運用の考慮

### 技術スキル
- **エンタープライズ設計**: 大規模システムアーキテクチャ
- **トランザクション処理**: データ整合性の保証
- **パフォーマンス**: 大量データの効率処理
- **セキュリティ**: 企業データの保護

この課題を通じて、実際の企業システム開発で求められる本格的な技術力を身につけることができます。