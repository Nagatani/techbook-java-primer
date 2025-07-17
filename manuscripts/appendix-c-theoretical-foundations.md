# 付録C: ソフトウェア工学の理論的基盤<small>設計原則と品質メトリクス</small>

## 本付録の目的

この付録では、オブジェクト指向プログラミングとソフトウェア設計の理論的基盤について詳しく解説します。ソフトウェア工学の基本原則、設計理論、品質メトリクスなどを通じて、なぜ特定のプログラミング手法が推奨されるのかを理論的に理解することを目的としています。

## 章とのマッピング

各セクションは本文の設計概念と理論的に連動しています：

- **C.1 ソフトウェアクライシス**: 第3章（OOP必要性）の理論的背景
- **C.2 設計原則**: 第4章（カプセル化）・第8章（設計パターン）の基礎理論
- **C.3 複雑度理論**: 第6章（パッケージ）・第7章（継承）の設計指針
- **C.4 品質メトリクス**: 第22章（テスト）の理論的基盤
- **C.5 アーキテクチャ理論**: 第21章（MVC）の設計思想
- **C.6 形式手法**: 第14章（例外処理）の契約理論



## C.1 ソフトウェアクライシスとその解決策

### C.1.1 ソフトウェアクライシスの詳細分析

1960年代から1970年代にかけて顕在化したソフトウェアクライシスは、単なる技術的問題ではなく、システム工学的な課題でした。

#### プロジェクト失敗の典型的パターン

**IBM System/360 OS開発（1960年代）**
- 予算：当初5000万ドル → 最終5億ドル（10倍超過）
- 期間：2年予定 → 4年以上
- 品質：リリース後も深刻なバグが多数発見

**原因分析**：
1. **複雑性の指数的増大**: 機能数の増加に対してバグ数が指数的に増加
2. **コミュニケーションコスト**: チームサイズnに対してコミュニケーション経路がn(n-1)/2で増加
3. **変更の波及効果**: 1箇所の変更が予期しない複数ヵ所に影響

#### ソフトウェアの複雑性メトリクス

**循環的複雑度（Cyclomatic Complexity）**
<span class="listing-number">**リストAC-1**</span>

```java
// 複雑度が高いコード例（複雑度: 6）
public String processData(int type, boolean flag, String data) {
    if (type == 1) {                    // +1
        if (flag) {                     // +1
            if (data.length() > 10) {   // +1
                return "Type1-Long";
            } else {                    // +1
                return "Type1-Short";
            }
        } else {                        // +1
            return "Type1-Default";
        }
    } else if (type == 2) {             // +1
        return "Type2";
    }
    return "Unknown";
}
```

**改善後（複雑度: 3）**
<span class="listing-number">**リストAC-2**</span>

```java
public String processData(int type, boolean flag, String data) {
    switch (type) {                     // +1
        case 1:
            return processType1(flag, data);
        case 2:
            return "Type2";
        default:
            return "Unknown";
    }
}

private String processType1(boolean flag, String data) {
    if (!flag) return "Type1-Default"; // +1
    return data.length() > 10          // +1
        ? "Type1-Long" 
        : "Type1-Short";
}
```

### C.1.2 構造化プログラミングの理論的基盤

#### Dijkstraの構造化定理

任意のプログラムは以下の3つの制御構造の組み合わせで表現可能：

1. **順次処理（Sequence）**
<span class="listing-number">**リストAC-3**</span>

```java
statement1;
statement2;
statement3;
```

2. **分岐処理（Selection）**
<span class="listing-number">**リストAC-4**</span>

```java
if (condition) {
    statementA;
} else {
    statementB;
}
```

3. **反復処理（Iteration）**
<span class="listing-number">**リストAC-5**</span>

```java
while (condition) {
    statements;
}
```

#### 形式的証明と不変条件

**ループ不変条件の例**
<span class="listing-number">**リストAC-6**</span>

```java
// 配列の最大値を求めるアルゴリズム
public int findMax(int[] array) {
    if (array.length == 0) throw new IllegalArgumentException();
    
    int max = array[0];
    int i = 1;
    
    // ループ不変条件: max は array[0..i-1] の最大値
    while (i < array.length) {
        if (array[i] > max) {
            max = array[i];
        }
        i++;
        // 不変条件は保持される
    }
    
    return max; // max は array[0..length-1] の最大値
}
```



## C.2 オブジェクト指向の理論的基盤

### C.2.1 抽象データ型（ADT）の理論

#### ADTの数学的定義

抽象データ型は以下の要素で構成される：
- **キャリア集合（Carrier Set）**: データの値域
- **操作集合（Operation Set）**: データに対する操作
- **公理系（Axiom System）**: 操作の性質を定義する規則

**スタックADTの例**
<span class="listing-number">**リストAC-7**</span>

```java
// スタックの抽象仕様
public interface Stack<T> {
    // 操作
    void push(T item);      // push: Stack × T → Stack
    T pop();               // pop: Stack → T × Stack  
    T peek();              // peek: Stack → T
    boolean isEmpty();     // isEmpty: Stack → Boolean
    
    // 公理（コメントで表現）
    // 1. pop(push(s, x)) = s
    // 2. peek(push(s, x)) = x
    // 3. isEmpty(new Stack()) = true
    // 4. isEmpty(push(s, x)) = false
}

// 具体実装
public class ArrayStack<T> implements Stack<T> {
    private T[] array;
    private int top;
    
    @SuppressWarnings("unchecked")
    public ArrayStack(int capacity) {
        array = (T[]) new Object[capacity];
        top = -1;
    }
    
    public void push(T item) {
        if (top >= array.length - 1) {
            throw new StackOverflowError();
        }
        array[++top] = item;
    }
    
    public T pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        T item = array[top];
        array[top--] = null; // メモリリーク防止
        return item;
    }
    
    public T peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return array[top];
    }
    
    public boolean isEmpty() {
        return top == -1;
    }
}
```

### C.2.2 カプセル化の理論的意義

#### 情報隠蔽の原則（Parnas, 1972）

**モジュール分割の基準**：
- 各モジュールは変更されやすい設計決定を隠蔽する
- インターフェイスは安定的で最小限に保つ
- 実装の詳細は外部から見えないようにする

**悪い例：実装詳細の露出**
<span class="listing-number">**リストAC-8**</span>

```java
public class BadBankAccount {
    public double[] transactionHistory; // 実装詳細の露出
    public int transactionCount;        // 不変条件の破綻可能性
    
    public void addTransaction(double amount) {
        // 配列サイズチェックなし - バグの温床
        transactionHistory[transactionCount++] = amount;
    }
}
```

**良い例：適切なカプセル化**
<span class="listing-number">**リストAC-9**</span>

```java
public class GoodBankAccount {
    private List<Transaction> transactions; // 実装詳細を隠蔽
    private double balance;
    
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        transactions.add(new Transaction("DEPOSIT", amount));
        balance += amount;
        // 不変条件: balance = sum of all transactions
    }
    
    public double getBalance() {
        return balance; // 計算結果のコピーを返す
    }
    
    public List<Transaction> getTransactionHistory() {
        return List.copyOf(transactions); // 不変ビューを返す
    }
}
```

### C.2.3 継承とリスコフの置換原則

#### LSP（Liskov Substitution Principle）の形式的定義

型Sが型Tのサブタイプである場合、プログラム内でT型のオブジェクトが使われている箇所は、すべてS型のオブジェクトで置き換え可能でなければならない。

**LSP違反の例**
<span class="listing-number">**リストAC-10**</span>

```java
class Rectangle {
    protected int width, height;
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    public int getArea() {
        return width * height;
    }
}

class Square extends Rectangle {
    @Override
    public void setWidth(int width) {
        this.width = this.height = width; // 前提条件を変更
    }
    
    @Override
    public void setHeight(int height) {
        this.width = this.height = height; // 前提条件を変更
    }
}

// LSP違反を示すテスト
public void testLSPViolation() {
    Rectangle rect = new Square(); // サブタイプで置換
    rect.setWidth(5);
    rect.setHeight(4);
    
    // Rectangle では 20 を期待するが、Square では 16 になる
    assert rect.getArea() == 20; // 失敗！
}
```

**LSP遵守の設計**
<span class="listing-number">**リストAC-11**</span>

```java
interface Shape {
    int getArea();
    Shape resize(double factor);
}

class Rectangle implements Shape {
    private final int width, height;
    
    public Rectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    public int getArea() {
        return width * height;
    }
    
    public Shape resize(double factor) {
        return new Rectangle(
            (int)(width * factor), 
            (int)(height * factor)
        );
    }
}

class Square implements Shape {
    private final int side;
    
    public Square(int side) {
        this.side = side;
    }
    
    public int getArea() {
        return side * side;
    }
    
    public Shape resize(double factor) {
        return new Square((int)(side * factor));
    }
}
```



## C.3 ソフトウェア品質メトリクス

### C.3.1 結合度（Coupling）の分類

#### 結合度の階層（弱い順）

1. **データ結合（Data Coupling）**
<span class="listing-number">**リストAC-12**</span>

```java
// 最も弱い結合 - 基本データのみを渡す
public double calculateTax(double income, double rate) {
    return income * rate;
}
```

2. **スタンプ結合（Stamp Coupling）**
<span class="listing-number">**リストAC-13**</span>

```java
// データ構造を渡すが、全体を使用
public double calculateTax(TaxInfo taxInfo) {
    return taxInfo.getIncome() * taxInfo.getRate();
}
```

3. **制御結合（Control Coupling）**
<span class="listing-number">**リストAC-14**</span>

```java
// 悪い例：制御フラグを渡す
public void processData(Data data, int mode) {
    if (mode == 1) {
        // 処理A
    } else if (mode == 2) {
        // 処理B
    }
}

// 良い例：ポリモーフィズムを使用
public interface DataProcessor {
    void process(Data data);
}
```

4. **共通結合（Common Coupling）**
<span class="listing-number">**リストAC-15**</span>

```java
// 悪い例：グローバル変数への依存
public class OrderProcessor {
    public void processOrder() {
        GlobalConfig.getInstance().getDatabase().save(order);
    }
}

// 良い例：依存性注入
public class OrderProcessor {
    private final Database database;
    
    public OrderProcessor(Database database) {
        this.database = database;
    }
    
    public void processOrder(Order order) {
        database.save(order);
    }
}
```

### C.3.2 凝集度（Cohesion）の分類

#### 凝集度の階層（強い順）

1. **機能的凝集（Functional Cohesion）**
<span class="listing-number">**リストAC-16**</span>

```java
// 単一の機能に特化
public class PrimeChecker {
    public boolean isPrime(int number) {
        if (number < 2) return false;
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) return false;
        }
        return true;
    }
}
```

2. **逐次的凝集（Sequential Cohesion）**
<span class="listing-number">**リストAC-17**</span>

```java
// 出力が次の入力になる処理の連鎖
public class DataProcessor {
    public ProcessedData process(RawData raw) {
        CleanedData cleaned = clean(raw);
        ValidatedData validated = validate(cleaned);
        return transform(validated);
    }
    
    private CleanedData clean(RawData raw) { /* */ }
    private ValidatedData validate(CleanedData cleaned) { /* */ }
    private ProcessedData transform(ValidatedData validated) { /* */ }
}
```

3. **偶発的凝集（Coincidental Cohesion）** - 避けるべき
<span class="listing-number">**リストAC-18**</span>

```java
// 悪い例：関連のない機能の寄せ集め
public class Utilities {
    public String formatDate(Date date) { /* */ }
    public double calculateTax(double income) { /* */ }
    public void sendEmail(String to, String subject) { /* */ }
    public int[] sortArray(int[] array) { /* */ }
}
```



## 🔄 C.4 設計パターンの理論的分析

### C.4.1 GoFパターンの分類理論

#### 生成パターン（Creational Patterns）
オブジェクトの生成に関する問題を解決：

**Singletonパターンの理論的分析**
<span class="listing-number">**リストAC-19**</span>

```java
// スレッドセーフなSingleton実装
public class DatabaseConnection {
    private static volatile DatabaseConnection instance;
    private static final Object lock = new Object();
    
    private DatabaseConnection() {
        // 初期化処理
    }
    
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }
}

// Enum による実装（推奨）
public enum DatabaseConnection {
    INSTANCE;
    
    public void executeQuery(String sql) {
        // クエリ実行
    }
}
```

**理論的考察**：
- メモリ可視性の問題（volatile修飾子の必要性）
- Double-checked lockingパターンの正当性
- 初期化のthread-safety

#### 構造パターン（Structural Patterns）

**Decoratorパターンの数学的モデル**
<span class="listing-number">**リストAC-20**</span>

```java
// Component = 基本機能の抽象化
interface Coffee {
    double getCost();
    String getDescription();
}

// ConcreteComponent = 基本実装
class SimpleCoffee implements Coffee {
    public double getCost() { return 1.0; }
    public String getDescription() { return "Simple Coffee"; }
}

// Decorator = 装飾の抽象化
abstract class CoffeeDecorator implements Coffee {
    protected Coffee coffee;
    
    public CoffeeDecorator(Coffee coffee) {
        this.coffee = coffee;
    }
}

// ConcreteDecorator = 具体的な装飾
class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) { super(coffee); }
    
    public double getCost() {
        return coffee.getCost() + 0.5; // f(x) = x + 0.5
    }
    
    public String getDescription() {
        return coffee.getDescription() + ", Milk";
    }
}

// 使用例: 関数合成 g(f(x))
Coffee coffee = new MilkDecorator(
    new SugarDecorator(
        new SimpleCoffee()
    )
);
```

#### 振る舞いパターン（Behavioral Patterns）

**Observerパターンとイベント代数**
<span class="listing-number">**リストAC-21**</span>

```java
// Subject = 観察される対象
public class Stock {
    private String symbol;
    private double price;
    private List<Observer> observers = new ArrayList<>();
    
    public void addObserver(Observer observer) {
        observers.add(observer);
    }
    
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }
    
    public void setPrice(double price) {
        this.price = price;
        notifyObservers(); // イベント発火
    }
    
    private void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(this);
        }
    }
}

// Observer = 観察者の抽象化
interface Observer {
    void update(Stock stock);
}

// ConcreteObserver = 具体的な観察者
class StockDisplay implements Observer {
    public void update(Stock stock) {
        System.out.println("Stock price updated: " + stock.getPrice());
    }
}
```

**理論的考察**：
- イベント-リスナパターンの数学的モデル
- 通知の順序保証性
- メモリリークの防止（WeakReference）



## 📐 C.5 アルゴリズム解析と設計

### C.5.1 計算複雑度理論

#### 時間複雑度の厳密な定義

**大O記法（Big-O Notation）**
f(n) = O(g(n)) ⟺ ∃c > 0, ∃n₀ > 0, ∀n ≥ n₀: f(n) ≤ c·g(n)

<span class="listing-number">**リストAC-22**</span>

```java
// O(n²) の例：バブルソート
public void bubbleSort(int[] arr) {
    int n = arr.length;
    for (int i = 0; i < n - 1; i++) {        // n-1 回
        for (int j = 0; j < n - i - 1; j++) { // (n-1) + (n-2) + ... + 1 回
            if (arr[j] > arr[j + 1]) {
                int temp = arr[j];
                arr[j] = arr[j + 1];
                arr[j + 1] = temp;
            }
        }
    }
    // 総比較回数: Σ(i=1 to n-1) i = n(n-1)/2 = O(n²)
}

// O(n log n) の例：マージソート
public void mergeSort(int[] arr, int left, int right) {
    if (left < right) {
        int mid = left + (right - left) / 2;
        
        mergeSort(arr, left, mid);      // T(n/2)
        mergeSort(arr, mid + 1, right); // T(n/2)
        merge(arr, left, mid, right);   // O(n)
    }
    // 漸化式: T(n) = 2T(n/2) + O(n)
    // 解: T(n) = O(n log n) （マスター定理より）
}
```

#### 空間複雑度の分析

<span class="listing-number">**リストAC-23**</span>

```java
// O(1) 空間：in-place ソート
public void quickSortIterative(int[] arr) {
    Stack<Integer> stack = new Stack<>();
    stack.push(0);
    stack.push(arr.length - 1);
    
    while (!stack.isEmpty()) {
        int high = stack.pop();
        int low = stack.pop();
        
        if (low < high) {
            int pivot = partition(arr, low, high);
            stack.push(low);
            stack.push(pivot - 1);
            stack.push(pivot + 1);
            stack.push(high);
        }
    }
    // 空間: O(log n) (スタックの最大深度)
}

// O(n) 空間：配列をコピーするソート
public int[] mergeSortWithCopy(int[] arr) {
    if (arr.length <= 1) return arr.clone();
    
    int mid = arr.length / 2;
    int[] left = mergeSortWithCopy(Arrays.copyOf(arr, mid));
    int[] right = mergeSortWithCopy(Arrays.copyOfRange(arr, mid, arr.length));
    
    return merge(left, right);
    // 空間: O(n) (各レベルでn要素の配列を作成)
}
```

### C.5.2 データ構造の理論的分析

#### 動的配列（ArrayList）の償却解析

<span class="listing-number">**リストAC-24**</span>

```java
public class DynamicArray<T> {
    private Object[] array;
    private int size;
    private int capacity;
    
    public DynamicArray() {
        capacity = 1;
        array = new Object[capacity];
        size = 0;
    }
    
    public void add(T element) {
        if (size == capacity) {
            resize(); // O(n) だが頻度は低い
        }
        array[size++] = element; // O(1)
    }
    
    private void resize() {
        capacity *= 2; // 容量を倍増
        Object[] newArray = new Object[capacity];
        System.arraycopy(array, 0, newArray, 0, size);
        array = newArray;
    }
}
```

**償却解析**：
- resize操作の頻度：1, 2, 4, 8, 16, ..., 2ⁱ
- n回のadd操作でresizeはlog₂(n) 回発生
- 総コスト：n + (1 + 2 + 4 + ... + n) < n + 2n = 3n
- 償却時間計算量：O(3n/n) = O(1)

#### ハッシュテーブルの理論

<span class="listing-number">**リストAC-25**</span>

```java
public class HashTable<K, V> {
    private static class Entry<K, V> {
        K key;
        V value;
        Entry<K, V> next; // チェイン法
        
        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
    
    private Entry<K, V>[] table;
    private int size;
    private int capacity;
    private static final double LOAD_FACTOR = 0.75;
    
    @SuppressWarnings("unchecked")
    public HashTable(int initialCapacity) {
        this.capacity = initialCapacity;
        this.table = new Entry[capacity];
    }
    
    public void put(K key, V value) {
        if (size >= capacity * LOAD_FACTOR) {
            resize(); // 負荷率を一定以下に保つ
        }
        
        int index = hash(key) % capacity;
        Entry<K, V> entry = table[index];
        
        // 既存キーの更新
        while (entry != null) {
            if (entry.key.equals(key)) {
                entry.value = value;
                return;
            }
            entry = entry.next;
        }
        
        // 新しいエントリの追加
        Entry<K, V> newEntry = new Entry<>(key, value);
        newEntry.next = table[index];
        table[index] = newEntry;
        size++;
    }
    
    private int hash(K key) {
        // 良いハッシュ関数は重要
        return Math.abs(key.hashCode());
    }
}
```

**理論的考察**：
- 期待検索時間：O(1 + α)（αは負荷率）
- 最悪時間：O(n)（すべてのキーが同じバケットにハッシュ）
- 全域ハッシュ（Universal Hashing）による期待性能保証



## C.6 形式手法と仕様記述

### C.6.1 事前条件・事後条件による仕様記述

#### ホーア論理（Hoare Logic）

{P} S {Q} の形式で表現：
- P：事前条件（Precondition）
- S：文（Statement）
- Q：事後条件（Postcondition）

<span class="listing-number">**リストAC-26**</span>

```java
/**
 * 二分探索アルゴリズム
 * @param arr ソート済み配列
 * @param target 探索する値
 * @return 見つかった場合はインデックス、見つからない場合は-1
 * 
 * 事前条件: arr != null && isSorted(arr)
 * 事後条件: 
 *   result >= 0 => arr[result] == target
 *   result == -1 => ∀i. arr[i] != target
 */
public int binarySearch(int[] arr, int target) {
    int left = 0;
    int right = arr.length - 1;
    
    // ループ不変条件:
    // target が存在するなら arr[left..right] の範囲内にある
    while (left <= right) {
        int mid = left + (right - left) / 2;
        
        if (arr[mid] == target) {
            return mid; // 事後条件を満たす
        } else if (arr[mid] < target) {
            left = mid + 1;
        } else {
            right = mid - 1;
        }
    }
    
    return -1; // 事後条件を満たす
}
```

### C.6.2 不変条件（Invariant）の設計

#### クラス不変条件の例

<span class="listing-number">**リストAC-27**</span>

```java
public class BankAccount {
    private double balance;
    private List<Transaction> transactions;
    
    // クラス不変条件:
    // balance == transactions.stream().mapToDouble(Transaction::getAmount).sum()
    
    public BankAccount(double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
        this.balance = initialBalance;
        this.transactions = new ArrayList<>();
        if (initialBalance > 0) {
            transactions.add(new Transaction("INITIAL", initialBalance));
        }
        // 不変条件の確立
        assert checkInvariant();
    }
    
    public void deposit(double amount) {
        // 事前条件
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        
        // 操作
        transactions.add(new Transaction("DEPOSIT", amount));
        balance += amount;
        
        // 事後条件と不変条件の維持
        assert checkInvariant();
    }
    
    public void withdraw(double amount) {
        // 事前条件
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (balance < amount) {
            throw new InsufficientFundsException("Insufficient balance");
        }
        
        // 操作
        transactions.add(new Transaction("WITHDRAW", -amount));
        balance -= amount;
        
        // 事後条件と不変条件の維持
        assert checkInvariant();
    }
    
    private boolean checkInvariant() {
        double calculatedBalance = transactions.stream()
            .mapToDouble(Transaction::getAmount)
            .sum();
        return Math.abs(balance - calculatedBalance) < 0.001; // 浮動小数点誤差を考慮
    }
}
```



## C.7 並行プログラミングの理論

### C.7.1 競合状態（Race Condition）の形式的分析

#### データ競合の定義

2つのメモリアクセス操作が以下の条件を満たすとき、データ競合が発生：
1. 同じメモリ位置にアクセス
2. 少なくとも一方が書き込み操作
3. 同期機構によって順序付けされていない
4. 異なるスレッドで実行

<span class="listing-number">**リストAC-28**</span>

```java
public class RaceConditionExample {
    private int counter = 0; // 共有状態
    
    // データ競合が発生する危険なメソッド
    public void unsafeIncrement() {
        // この操作は原子的でない:
        // 1. counter の読み取り
        // 2. 値の増加
        // 3. counter への書き込み
        counter++; // 複数のスレッドで同時実行すると競合状態
    }
    
    // 同期化による競合状態の解決
    public synchronized void safeIncrement() {
        counter++; // synchronized により原子性を保証
    }
    
    // Atomicクラスによる解決
    private AtomicInteger atomicCounter = new AtomicInteger(0);
    
    public void atomicIncrement() {
        atomicCounter.incrementAndGet(); // ハードウェアレベルでの原子性
    }
}
```

### C.7.2 デッドロックの理論的分析

#### コフマンの4条件

デッドロックが発生するための必要十分条件：
1. **相互排除（Mutual Exclusion）**: リソースは同時に1つのプロセスのみが使用
2. **保持と待機（Hold and Wait）**: プロセスは1つ以上のリソースを保持しながらほかのリソースを待機
3. **非プリエンプション（No Preemption）**: リソースは強制的に奪取できない
4. **循環待機（Circular Wait）**: プロセス間でリソース待機の循環が存在

<span class="listing-number">**リストAC-29**</span>

```java
public class DeadlockExample {
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();
    
    // デッドロックが発生する可能性のあるコード
    public void method1() {
        synchronized (lock1) {
            System.out.println("Thread " + Thread.currentThread().getId() + " acquired lock1");
            
            try { Thread.sleep(100); } catch (InterruptedException e) {}
            
            synchronized (lock2) { // 循環待機の原因
                System.out.println("Thread " + Thread.currentThread().getId() + " acquired lock2");
            }
        }
    }
    
    public void method2() {
        synchronized (lock2) {
            System.out.println("Thread " + Thread.currentThread().getId() + " acquired lock2");
            
            try { Thread.sleep(100); } catch (InterruptedException e) {}
            
            synchronized (lock1) { // 循環待機の原因
                System.out.println("Thread " + Thread.currentThread().getId() + " acquired lock1");
            }
        }
    }
    
    // デッドロック回避策: ロック順序の統一
    public void safeMethod1() {
        synchronized (lock1) { // 常に lock1 を先に取得
            synchronized (lock2) {
                // 処理
            }
        }
    }
    
    public void safeMethod2() {
        synchronized (lock1) { // 常に lock1 を先に取得
            synchronized (lock2) {
                // 処理
            }
        }
    }
}
```

### C.7.3 メモリモデルと順序保証

#### Java Memory Model (JMM)

<span class="listing-number">**リストAC-30**</span>

```java
public class MemoryModelExample {
    private boolean ready = false;
    private int value = 0;
    
    // Writer スレッド
    public void writer() {
        value = 42;      // 1
        ready = true;    // 2
        // リオーダリングにより 2 が 1 より先に実行される可能性
    }
    
    // Reader スレッド
    public void reader() {
        while (!ready) { // 3
            Thread.yield();
        }
        System.out.println(value); // 4: 0 が出力される可能性
    }
    
    // volatile による順序保証
    private volatile boolean readyVolatile = false;
    private int valueVolatile = 0;
    
    public void writerVolatile() {
        valueVolatile = 42;      // 1
        readyVolatile = true;    // 2: volatile write
        // happens-before 関係により 1 は 2 より先に実行
    }
    
    public void readerVolatile() {
        while (!readyVolatile) { // 3: volatile read
            Thread.yield();
        }
        System.out.println(valueVolatile); // 4: 必ず 42 が出力
        // happens-before 関係により 2 は 3 より先、3 は 4 より先
    }
}
```



## まとめ：理論と実践の統合

この付録で扱った理論的基盤は、単なる学術的興味にとどまらず、実際のソフトウェア開発において以下のような実践的価値を提供します：

### 理論の実践的応用

1. **設計判断の根拠**: なぜ特定の設計パターンや原則が推奨されるのか
2. **パフォーマンス予測**: アルゴリズムの計算複雑度による性能見積もり
3. **バグの予防**: 形式的仕様による論理的正確性の検証
4. **並行性の制御**: 競合状態やデッドロックの理論的理解にもとづく安全な実装

### 継続学習への基盤

これらの理論的知識は、新しい技術やパラダイムを学ぶ際の基礎となります：

- **関数型プログラミング**: ラムダ計算や圏論の理解
- **分散システム**: CAP定理や分散コンセンサスアルゴリズム
- **機械学習**: 最適化理論や統計学習理論
- **量子コンピューティング**: 量子力学と計算理論の融合

理論と実践のバランスを保ちながら、この知識を実際のプロジェクトで活用してください。

## 参考文献

- "Introduction to Algorithms" by Cormen, Leiserson, Rivest, and Stein
- "The Art of Computer Programming" by Donald E. Knuth
- "Software Engineering: A Practitioner's Approach" by Roger Pressman
- "Concurrent Programming in Java" by Doug Lea
- "Formal Methods: State of the Art and New Directions" by Jeannette Wing