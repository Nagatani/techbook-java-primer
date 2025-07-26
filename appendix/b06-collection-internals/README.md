# ハッシュテーブルとレッドブラックツリーの内部実装

Javaコレクションフレームワークの中核を成すHashMapとTreeMapの内部実装について詳細に学習できるプロジェクトです。

## 概要

HashMapとTreeMapは、Javaプログラムで最も頻繁に使用されるデータ構造の1つです。これらがどのように高性能を実現しているか、衝突処理やツリーのバランシングなど、内部実装の詳細を理解することで、適切なデータ構造の選択と最適化が可能になります。

## なぜコレクション内部実装の理解が重要なのか

### 実際のパフォーマンス問題事例

**問題1: ハッシュ衝突によるパフォーマンス劣化**
```java
// 悪いhashCode実装により性能が著しく低下
public class BadHashCodeExample {
    @Override
    public int hashCode() {
        return 1; // 全オブジェクトが同じハッシュ値 → 全て同じバケットに集中
    }
}
// 結果：HashMap操作がO(1)からO(n)に劣化
```

**問題2: 不適切なデータ構造選択**
```java
// 頻繁な検索が必要なのにArrayListを使用
List<Customer> customers = new ArrayList<>(); // 100万件
Customer target = customers.stream()
    .filter(c -> c.getId().equals(targetId))
    .findFirst().orElse(null); // O(n)の線形検索

// HashMapを使えばO(1)で検索可能
Map<String, Customer> customerMap = new HashMap<>();
Customer target = customerMap.get(targetId); // O(1)
```

### ビジネスインパクト

**実際の障害事例:**
- 某ECサイト: 商品検索でハッシュ衝突が多発し、応答時間が10秒以上に
- 金融システム: 不適切なTreeMap使用でメモリ不足、システムダウン
- ゲームサーバー: プレイヤー検索の最適化不足で同時接続数制限

**最適化による効果:**
- 検索時間: O(n) → O(1)で1000倍高速化
- メモリ使用量: 適切な初期サイズで50%削減
- GC負荷: 効率的なデータ構造でGC時間90%削減

## サンプルコード構成

### 1. HashMap内部実装の詳細
- `HashMapInternals.java`: ハッシュテーブルの仕組みを詳細に学習
  - SimpleHashMap: 簡略化されたHashMap実装
  - ハッシュ関数とバケットインデックス計算
  - 衝突処理（チェーン法）
  - 負荷係数と動的リサイズ
  - hashCode実装品質の影響
  - 性能測定と最適化

### 2. TreeMap内部実装の詳細  
- `TreeMapInternals.java`: 赤黒木の仕組みと平衡維持を学習
  - SimpleRedBlackTree: 赤黒木の実装
  - 回転操作（左回転・右回転）
  - 挿入後の平衡維持
  - 二分探索木との性能比較
  - TreeMap vs HashMap の使い分け
  - メモリ使用量の比較

## 実行方法

### コンパイルと実行
```bash
javac -d . src/main/java/com/example/collections/*.java

# HashMap内部実装のデモ
java com.example.collections.HashMapInternals

# TreeMap内部実装のデモ
java com.example.collections.TreeMapInternals
```

## 学習ポイント

### 1. HashMap（ハッシュテーブル）の仕組み

#### 基本原理
- **直接アドレス計算**: キーのハッシュ値から配列インデックスを直接計算
- **平均O(1)性能**: 理想的には定数時間でアクセス可能
- **分散配置**: ハッシュ関数によりデータを均等に分散

#### ハッシュ関数の重要性
```java
// Java 8のハッシュ関数
private int hash(Object key) {
    if (key == null) return 0;
    int h = key.hashCode();
    return h ^ (h >>> 16); // 上位16ビットと下位16ビットのXOR
}
```

#### 衝突処理とJava 8の改善
- **Java 7以前**: チェーン法（連結リスト）
- **Java 8以降**: チェーン法 + 赤黒木（長いチェーンを木に変換）

#### 負荷係数の影響
```
負荷係数 = 要素数 / バケット数

- 0.75（デフォルト）: 空間効率とアクセス時間のバランス
- 低い値（0.5）: 高速アクセス、メモリ使用量増加
- 高い値（1.0+）: メモリ節約、衝突増加で性能低下
```

### 2. TreeMap（赤黒木）の仕組み

#### 赤黒木の性質
1. 各ノードは赤または黒
2. ルートは黒
3. 葉（NIL）は黒
4. 赤ノードの子は黒
5. 任意のノードから葉までの黒ノード数は同じ

#### 平衡維持の仕組み
```java
// 挿入後の修正例
private void fixAfterInsertion(Node x) {
    x.color = RED; // 新しいノードは赤で挿入
    
    while (x != null && x != root && x.parent.color == RED) {
        // 赤の連続を解消するための修正処理
        // 回転とか色変更を組み合わせて平衡を維持
    }
    root.color = BLACK; // ルートは常に黒
}
```

#### 回転操作
- **左回転**: 右の子を親にして構造を調整
- **右回転**: 左の子を親にして構造を調整
- **目的**: 木の高さのバランスを保つ

### 3. 性能特性の比較

#### HashMap vs TreeMap
| 操作 | HashMap | TreeMap |
|------|---------|---------|
| 挿入 | O(1) 平均 | O(log n) |
| 検索 | O(1) 平均 | O(log n) |
| 削除 | O(1) 平均 | O(log n) |
| 反復 | O(n) 順序なし | O(n) ソート済み |
| 範囲検索 | O(n) | O(log n) |

#### メモリ使用量
- **HashMap**: より少ないメモリ使用量
- **TreeMap**: 追加のポインタとカラー情報で約30%多い

### 4. 実践的な選択指針

#### HashMapを選ぶべき場合
- 高速な挿入・検索・削除が必要
- キーの順序が重要でない
- メモリ使用量を最小化したい
- 一般的なキー/値ストレージ

```java
// 例：ユーザーIDによる高速検索
Map<String, User> userCache = new HashMap<>();
User user = userCache.get(userId); // O(1)
```

#### TreeMapを選ぶべき場合
- ソートされた順序が必要
- 範囲検索を頻繁に行う
- 最小/最大キーの取得が必要
- NavigableMapの機能が必要

```java
// 例：日付範囲でのデータ検索
TreeMap<LocalDate, SalesData> salesMap = new TreeMap<>();
NavigableMap<LocalDate, SalesData> monthData = 
    salesMap.subMap(startDate, endDate); // O(log n)
```

## ベンチマーク結果例

### HashMap性能テスト
```
=== Load Factor Impact ===
Load Factor: 0.5  - Insert: 25ms, Retrieve: 18ms, Collisions: Low
Load Factor: 0.75 - Insert: 32ms, Retrieve: 24ms, Collisions: Medium  
Load Factor: 1.0  - Insert: 45ms, Retrieve: 38ms, Collisions: High
Load Factor: 1.5  - Insert: 78ms, Retrieve: 65ms, Collisions: Very High
```

### hashCode実装の影響
```
=== HashCode Quality Impact ===
Bad HashCode (all same):  Insert: 2847ms, Max chain: 100000
Good HashCode (Objects.hash): Insert: 32ms, Max chain: 3
Performance difference: 89.0x improvement
```

### TreeMap vs HashMap比較
```
=== Performance Comparison (100,000 items) ===
HashMap:  Insert: 45ms, Search: 23ms, Memory: 8MB
TreeMap:  Insert: 156ms, Search: 89ms, Memory: 12MB
HashMap advantage: 3.5x faster insert, 3.9x faster search
```

## 最適化のベストプラクティス

### 1. HashMap最適化
```java
// 適切な初期容量の設定
int expectedSize = 1000;
Map<String, Object> map = new HashMap<>(expectedSize * 4 / 3);

// 良いhashCode実装
@Override
public int hashCode() {
    return Objects.hash(field1, field2, field3);
}
```

### 2. TreeMap最適化
```java
// カスタムComparatorで最適化
TreeMap<String, Object> map = new TreeMap<>(
    String.CASE_INSENSITIVE_ORDER
);

// 一括操作の活用
TreeMap<Integer, String> sortedMap = new TreeMap<>(unsortedMap);
```

### 3. 使い分けの指針
```java
// 検索中心のアプリケーション
Map<String, User> userMap = new HashMap<>(); // O(1) lookup

// ソート済みデータが必要
Map<Date, Event> timeline = new TreeMap<>(); // 自動ソート

// 範囲検索が必要
NavigableMap<Integer, Product> priceRange = 
    new TreeMap<Integer, Product>()
        .subMap(minPrice, maxPrice); // 効率的な範囲検索
```

## 高度なトピック

### 1. Java 8以降の改善
- **Treeification**: 長いチェーンの木化
- **ハッシュ改善**: よりよい分散のためのハッシュ関数改良
- **並列処理**: parallelStream()のサポート

### 2. スレッドセーフ版
- **ConcurrentHashMap**: セグメント分割による並行アクセス
- **Collections.synchronizedMap()**: 同期化ラッパー
- **ConcurrentSkipListMap**: TreeMapの並行版

### 3. メモリ効率化
- **トランポリン最適化**: 小さなオブジェクトの最適化
- **コンパクション**: 削除後の空間再利用
- **世代別GC**: 若い世代での効率的な回収

## 関連技術とアルゴリズム

- **ハッシュ関数**: MD5、SHA、MurmurHash
- **平衡木**: AVL木、B-tree、2-3木
- **並行データ構造**: Lock-free algorithms
- **キャッシュ効率**: CPU cache line considerations
- **分散ハッシュ**: Consistent hashing

このプロジェクトを通じて、コレクションフレームワークの内部動作を深く理解し、実際のアプリケーションで最適なデータ構造を選択する能力を身につけることができます。