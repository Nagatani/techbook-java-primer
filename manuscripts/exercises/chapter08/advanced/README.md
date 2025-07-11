# 第8章 発展課題：高度なコレクション操作

## 課題概要

基礎課題で学んだコレクションの知識を応用し、より複雑なデータ構造と処理を実装します。実務で遭遇する現実的な問題を解決する能力を養います。

## 課題1：多次元インデックスデータベース

### 目的
- 複数の条件での高速検索
- インデックスの設計と実装
- メモリ効率を考慮したデータ構造

### 要求仕様

1. **データモデル**
   ```java
   public class Employee {
       private String id;
       private String name;
       private String department;
       private String position;
       private int salary;
       private LocalDate hireDate;
       private Set<String> skills;
   }
   ```

2. **インデックス機能**
   - 部署別インデックス
   - 給与範囲インデックス
   - スキル別インデックス
   - 複合条件での検索

3. **実装要件**
   ```java
   public class EmployeeDatabase {
       private Map<String, Employee> primaryIndex; // ID -> Employee
       private Map<String, Set<String>> departmentIndex; // Department -> Set<ID>
       private NavigableMap<Integer, Set<String>> salaryIndex; // Salary -> Set<ID>
       private Map<String, Set<String>> skillIndex; // Skill -> Set<ID>
       
       public List<Employee> findByDepartmentAndSalaryRange(
           String department, int minSalary, int maxSalary) {
           // 複数インデックスを組み合わせた効率的な検索
       }
       
       public List<Employee> findBySkills(Set<String> requiredSkills) {
           // すべての必須スキルを持つ社員を検索
       }
   }
   ```

### 実装のヒント
- TreeMapのsubMapメソッドを活用
- インデックスの一貫性維持
- 遅延評価による最適化

### 評価ポイント
- インデックスの効率的な設計
- 検索パフォーマンスの最適化
- メモリ使用量の考慮
- データ整合性の維持

## 課題2：LRUキャッシュの実装

### 目的
- LinkedHashMapの高度な活用
- アクセス順序の管理
- キャッシュアルゴリズムの実装

### 要求仕様

1. **基本的なLRUキャッシュ**
   ```java
   public class LRUCache<K, V> {
       private final int capacity;
       private final Map<K, V> cache;
       
       public V get(K key) {
           // アクセスされた要素を最新に
       }
       
       public void put(K key, V value) {
           // 容量超過時は最も古い要素を削除
       }
   }
   ```

2. **拡張機能**
   - TTL（Time To Live）サポート
   - 統計情報（ヒット率、ミス率）
   - イベントリスナー（削除時通知）
   - 非同期ロード機能

3. **パフォーマンス要件**
   - get/put操作はO(1)
   - スレッドセーフな実装
   - メモリリーク防止

### 実装のヒント
```java
public class AdvancedLRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int maxEntries;
    private final Map<K, Long> expirationTimes;
    private final CacheStatistics stats;
    
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        // 容量またはTTLに基づく削除判定
    }
    
    public V get(Object key) {
        // TTLチェックと統計更新
        V value = super.get(key);
        if (value != null && !isExpired((K) key)) {
            stats.recordHit();
            return value;
        }
        stats.recordMiss();
        return null;
    }
}
```

### 評価ポイント
- LinkedHashMapの適切な拡張
- TTL機能の正確な実装
- スレッドセーフティの確保
- パフォーマンスとメモリ効率

## 課題3：グラフデータ構造

### 目的
- 複雑なデータ構造の実装
- 隣接リスト表現の理解
- グラフアルゴリズムの基礎

### 要求仕様

1. **グラフの表現**
   ```java
   public class Graph<T> {
       private Map<T, Set<Edge<T>>> adjacencyList;
       
       public static class Edge<T> {
           private T destination;
           private double weight;
       // コンストラクタ、getter/setter
       }
       
       public void addVertex(T vertex) { }
       public void addEdge(T from, T to, double weight) { }
       public Set<T> getNeighbors(T vertex) { }
   }
   ```

2. **基本操作**
   - 頂点と辺の追加・削除
   - 隣接頂点の取得
   - グラフの可視化（toString）

3. **グラフアルゴリズム**
   - 深さ優先探索（DFS）
   - 幅優先探索（BFS）
   - 最短経路探索（Dijkstra）
   - 連結成分の検出

### 実装のヒント
```java
public class GraphAlgorithms<T> {
    public List<T> dfs(Graph<T> graph, T start) {
        List<T> visited = new ArrayList<>();
        Set<T> seen = new HashSet<>();
        dfsHelper(graph, start, seen, visited);
        return visited;
    }
    
    private void dfsHelper(Graph<T> graph, T vertex, 
                          Set<T> seen, List<T> visited) {
        seen.add(vertex);
        visited.add(vertex);
        
        for (Graph.Edge<T> edge : graph.getEdges(vertex)) {
            if (!seen.contains(edge.getDestination())) {
                dfsHelper(graph, edge.getDestination(), seen, visited);
            }
        }
    }
}
```

### 評価ポイント
- 効率的なグラフ表現
- アルゴリズムの正確な実装
- エッジケースの処理
- 汎用性のある設計

## 提出方法

1. 各課題の完全な実装とテストコード
2. パフォーマンステストの結果
3. 時間計算量と空間計算量の分析
4. 実装上の工夫点の説明

## 発展学習の提案

- **並行コレクション**：ConcurrentHashMap、CopyOnWriteArrayListの深い理解
- **カスタムコレクション**：AbstractCollectionを継承した独自実装
- **外部ライブラリ**：Eclipse Collections、Apache Commons Collections
- **データ構造の理論**：B-Tree、Skip List、Bloom Filterなど

## 参考リソース

- Algorithms（Robert Sedgewick、Kevin Wayne）
- Introduction to Algorithms（Thomas H. Cormen他）
- Java Generics and Collections（Maurice Naftalin、Philip Wadler）