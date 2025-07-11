# 第4章 応用課題：複数クラスの連携とカプセル化

## 概要

本課題では、複数のクラスが連携するシステムの設計を通じて、より実践的なカプセル化の技術を学習します。クラス間の責任分担、適切な公開インターフェースの設計、データの一貫性維持などを実践します。

## 学習目標

- 複数クラス間の適切な責任分担
- クラス間の依存関係の管理
- カプセル化を保ちながらのオブジェクト連携
- 集約とコンポジションの実装

## 課題1：従業員管理システム

### 要求仕様

従業員と部署を管理するシステムを実装してください。

#### Employeeクラス

```java
public class Employee {
    // フィールド（すべてprivate）
    // - employeeId (String): 従業員ID（不変）
    // - name (String): 氏名
    // - email (String): メールアドレス
    // - salary (double): 給与
    // - department (Department): 所属部署
    // - hireDate (LocalDate): 入社日（不変）
    
    // メソッド
    // - 標準的なgetter/setter
    // - changeDepartment(Department newDept): 部署異動
    // - calculateYearsOfService(): 勤続年数計算
    // - isEligibleForPromotion(): 昇進資格判定（勤続3年以上）
}
```

#### Departmentクラス

```java
public class Department {
    // フィールド（すべてprivate）
    // - departmentCode (String): 部署コード（不変）
    // - name (String): 部署名
    // - manager (Employee): 部門長
    // - employees (List<Employee>): 所属従業員リスト
    // - budget (double): 部署予算
    
    // メソッド
    // - addEmployee(Employee emp): 従業員追加
    // - removeEmployee(String employeeId): 従業員削除
    // - getTotalSalary(): 部署の総給与額
    // - getEmployeeCount(): 所属人数
    // - setManager(Employee manager): 部門長設定
}
```

### 実装のヒント

1. **双方向の関連の管理**：
   ```java
   // Employeeクラスのメソッド
   public void changeDepartment(Department newDept) {
       if (this.department != null) {
           this.department.removeEmployee(this.employeeId);
       }
       this.department = newDept;
       if (newDept != null) {
           newDept.addEmployee(this);
       }
   }
   ```

2. **防御的コピー**：
   ```java
   // Departmentクラスのメソッド
   public List<Employee> getEmployees() {
       // 内部リストの直接参照を返さない
       return new ArrayList<>(employees);
   }
   ```

3. **ビジネスルールの実装**：
   ```java
   public boolean addEmployee(Employee emp) {
       // 予算チェック
       if (getTotalSalary() + emp.getSalary() > budget) {
           return false; // 予算オーバー
       }
       // 重複チェック
       if (employees.stream().anyMatch(e -> e.getEmployeeId().equals(emp.getEmployeeId()))) {
           return false;
       }
       employees.add(emp);
       return true;
   }
   ```

### テストシナリオ

```java
public class EmployeeManagementTest {
    public static void main(String[] args) {
        // 部署の作成
        Department sales = new Department("SALES001", "営業部", 10000000);
        Department engineering = new Department("ENG001", "技術部", 15000000);
        
        // 従業員の作成
        Employee emp1 = new Employee("EMP001", "田中太郎", "tanaka@example.com", 
                                    400000, LocalDate.of(2020, 4, 1));
        Employee emp2 = new Employee("EMP002", "佐藤花子", "sato@example.com", 
                                    450000, LocalDate.of(2019, 4, 1));
        
        // 部署への配属
        sales.addEmployee(emp1);
        sales.addEmployee(emp2);
        
        // 部門長の設定
        sales.setManager(emp2);
        
        // 部署異動
        emp1.changeDepartment(engineering);
        
        // 情報表示
        System.out.println("営業部の人数: " + sales.getEmployeeCount());
        System.out.println("技術部の人数: " + engineering.getEmployeeCount());
        System.out.println("営業部の総給与: " + sales.getTotalSalary());
    }
}
```

## 課題2：図書館管理システム

### 要求仕様

図書館の蔵書と貸出を管理するシステムを実装してください。

#### Bookクラス（課題1で作成したものを拡張）

```java
public class Book {
    // 既存のフィールドに加えて
    // - bookId (String): 図書ID（不変）
    // - status (BookStatus): 貸出状態
    // - borrower (Member): 借りている会員
    // - dueDate (LocalDate): 返却期限
    
    // 追加メソッド
    // - isAvailable(): 貸出可能かどうか
    // - borrowBy(Member member, int days): 貸出処理
    // - returnBook(): 返却処理
}
```

#### Memberクラス

```java
public class Member {
    // フィールド
    // - memberId (String): 会員ID（不変）
    // - name (String): 氏名
    // - email (String): メールアドレス
    // - membershipType (MembershipType): 会員種別
    // - borrowedBooks (List<Book>): 借りている本のリスト
    // - borrowHistory (List<BorrowRecord>): 貸出履歴
    
    // メソッド
    // - canBorrow(): 貸出可能かどうか（上限チェック）
    // - borrowBook(Book book): 本を借りる
    // - returnBook(String bookId): 本を返す
    // - getBorrowLimit(): 貸出上限数（会員種別による）
}
```

#### Libraryクラス

```java
public class Library {
    // フィールド
    // - books (Map<String, Book>): 蔵書マップ
    // - members (Map<String, Member>): 会員マップ
    // - borrowRecords (List<BorrowRecord>): 貸出記録
    
    // メソッド
    // - registerBook(Book book): 蔵書登録
    // - registerMember(Member member): 会員登録
    // - borrowBook(String memberId, String bookId, int days): 貸出処理
    // - returnBook(String memberId, String bookId): 返却処理
    // - searchAvailableBooks(String keyword): 貸出可能な本の検索
    // - getMemberBorrowHistory(String memberId): 会員の貸出履歴
}
```

### 実装の工夫点

1. **enum型の活用**：
   ```java
   public enum BookStatus {
       AVAILABLE("貸出可能"),
       BORROWED("貸出中"),
       RESERVED("予約済み"),
       MAINTENANCE("メンテナンス中");
       
       private final String displayName;
       
       BookStatus(String displayName) {
           this.displayName = displayName;
       }
       
       public String getDisplayName() {
           return displayName;
       }
   }
   ```

2. **トランザクション的な処理**：
   ```java
   public boolean borrowBook(String memberId, String bookId, int days) {
       Member member = members.get(memberId);
       Book book = books.get(bookId);
       
       // 事前チェック
       if (member == null || book == null) return false;
       if (!book.isAvailable()) return false;
       if (!member.canBorrow()) return false;
       
       // 貸出処理（両方のオブジェクトを更新）
       book.borrowBy(member, days);
       member.borrowBook(book);
       
       // 貸出記録の作成
       borrowRecords.add(new BorrowRecord(member, book, LocalDate.now(), days));
       
       return true;
   }
   ```

## 課題3：在庫管理システム

### 要求仕様

商品の在庫と注文を管理するシステムを実装してください。

#### Warehouseクラス

```java
public class Warehouse {
    // 在庫を管理し、複数の注文を処理
    // - inventory (Map<Product, Integer>): 商品と在庫数のマップ
    // - pendingOrders (Queue<Order>): 処理待ち注文のキュー
    // - processedOrders (List<Order>): 処理済み注文のリスト
    
    // メソッド
    // - addStock(Product product, int quantity): 在庫追加
    // - checkAvailability(Order order): 注文の在庫確認
    // - processOrder(Order order): 注文処理
    // - getStockLevel(Product product): 在庫数確認
}
```

#### Orderクラス

```java
public class Order {
    // 注文情報を管理
    // - orderId (String): 注文ID（不変）
    // - customer (Customer): 顧客
    // - orderItems (List<OrderItem>): 注文明細
    // - orderDate (LocalDateTime): 注文日時
    // - status (OrderStatus): 注文状態
    
    // メソッド
    // - addItem(Product product, int quantity): 商品追加
    // - removeItem(String productId): 商品削除
    // - getTotalAmount(): 合計金額計算
    // - validateOrder(): 注文の妥当性確認
}
```

### 高度な実装要件

1. **スレッドセーフティの考慮**（発展的）：
   ```java
   public synchronized boolean processOrder(Order order) {
       // 在庫の確認と減算を原子的に実行
       for (OrderItem item : order.getOrderItems()) {
           int currentStock = inventory.getOrDefault(item.getProduct(), 0);
           if (currentStock < item.getQuantity()) {
               return false; // 在庫不足
           }
       }
       
       // すべての商品が確保できる場合のみ処理
       for (OrderItem item : order.getOrderItems()) {
           int currentStock = inventory.get(item.getProduct());
           inventory.put(item.getProduct(), currentStock - item.getQuantity());
       }
       
       order.setStatus(OrderStatus.PROCESSED);
       processedOrders.add(order);
       return true;
   }
   ```

2. **イベント通知の実装**（発展的）：
   ```java
   public interface StockListener {
       void onLowStock(Product product, int currentLevel);
       void onOutOfStock(Product product);
   }
   
   public class Warehouse {
       private List<StockListener> listeners = new ArrayList<>();
       
       public void addStockListener(StockListener listener) {
           listeners.add(listener);
       }
       
       private void notifyLowStock(Product product, int level) {
           for (StockListener listener : listeners) {
               if (level == 0) {
                   listener.onOutOfStock(product);
               } else if (level < product.getReorderLevel()) {
                   listener.onLowStock(product, level);
               }
           }
       }
   }
   ```

## 評価ポイント

1. **クラス設計の適切性**（30点）
   - 責任の分離が明確
   - 適切な関連の実装
   - 循環参照の回避

2. **カプセル化の実装**（30点）
   - 内部状態の適切な隠蔽
   - 防御的プログラミングの実践
   - 不変性の活用

3. **ビジネスロジックの正確性**（25点）
   - 要求仕様の完全な実装
   - エッジケースの考慮
   - データの整合性維持

4. **コードの拡張性**（15点）
   - 将来の変更への対応しやすさ
   - インターフェースの適切な設計
   - 疎結合な実装

## 発展学習の提案

1. **デザインパターンの適用**：
   - Observerパターンによるイベント通知
   - Strategyパターンによる価格計算戦略の切り替え
   - Facadeパターンによる複雑な処理の簡素化

2. **並行処理への対応**：
   - スレッドセーフなコレクションの使用
   - 読み書きロックの実装
   - 非同期処理の導入

3. **永続化の実装**：
   - データベースへの保存機能
   - トランザクション管理
   - キャッシュの実装

これらの応用課題を通じて、実践的なオブジェクト指向設計とカプセル化の技術を身につけてください。