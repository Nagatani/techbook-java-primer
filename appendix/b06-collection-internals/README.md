# 付録B.06 コレクションの内部実装

Javaコレクションフレームワークの中核を成すHashMapとTreeMapの内部実装（ハッシュテーブルと赤黒木）について詳細に学習できるプロジェクトです。

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
// 結果：1つのバケットのチェーンをたどることになり、検索がO(1)からO(n)に劣化する
// Java 8以降のHashMapは長いチェーンを赤黒木に変えるが、O(log n)に抑えられるのは
// キーがComparableを実装している場合だけ（このクラスのようにComparableでないキーはO(n)のままになりうる）
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

## サンプルコード構成

### 1. HashMap内部実装の詳細
- `HashMapInternals.java`: ハッシュテーブルの仕組みを詳細に学習
  - SimpleHashMap: 簡略化されたHashMap実装（赤黒木への変換は省略）
  - ハッシュ関数とバケットインデックス計算
  - 衝突処理（チェーン法）
  - 負荷係数と動的リサイズ
  - hashCode実装品質の影響
  - 標準のHashMapとの比較

### 2. TreeMap内部実装の詳細  
- `TreeMapInternals.java`: 赤黒木の仕組みと平衡維持を学習
  - SimpleRedBlackTree: 赤黒木の実装
  - 回転操作（左回転・右回転）
  - 挿入後の平衡維持（昇順に挿入して回転を観察する）
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

Java 8以降のHashMapは、次の値（`java.util.HashMap`の定数）で連結リストと赤黒木を切り替えます。

- 1つのバケットのチェーンが8（`TREEIFY_THRESHOLD`）を超えると、赤黒木に変える
- ただし表の大きさが64（`MIN_TREEIFY_CAPACITY`）未満のときは、木にせず表を広げる
- 削除やリサイズで6（`UNTREEIFY_THRESHOLD`）以下になると、連結リストに戻す

#### 負荷係数の影響
```
負荷係数 = 要素数 / バケット数

- 0.75（デフォルト）: 空間効率とアクセス時間のバランス
- 低い値（0.5）: 高速アクセス、メモリ使用量増加
- 高い値（1.0+）: メモリ節約、衝突増加で性能低下
```

要素数が「バケット数 × 負荷係数」（閾値）を超えると、表を2倍に広げて要素を配置し直します。

### 2. TreeMap（赤黒木）の仕組み

#### 赤黒木の性質
1. 各ノードは赤または黒
2. ルートは黒
3. 葉（NIL）は黒
4. 赤ノードの子は黒
5. 各ノードから、その子孫の葉までのどの経路にも、同じ数の黒ノードがある

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
| 範囲検索（k件） | O(n) | O(log n + k) |

#### メモリ使用量
- **HashMap**: エントリ（ハッシュ値・キー・値・次への参照）に加えて、バケットの配列を持つ
- **TreeMap**: エントリにキー・値・左右と親への参照・色を持ち、配列は持たない
- エントリ1件あたりの大きさは同じ程度で、どちらが小さいかは要素数や表の埋まり具合で変わる。`TreeMapInternals`のメモリ比較で目安を確かめられる

### 4. 実践的な選択指針

#### HashMapを選ぶべき場合
- 高速な挿入・検索・削除が必要
- キーの順序が重要でない
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
    salesMap.subMap(startDate, true, endDate, false); // 開始位置をO(log n)で探す
```

## 実行結果について

各デモは処理時間を表示します。時間は数回の空回し（ウォームアップ）のあとに`System.nanoTime()`で測っていますが、結果はJVM・CPU・負荷などの環境で変わります。正確に比べるにはJMH（Java Microbenchmark Harness）で測ります。

時間以外の値（衝突統計、木の高さ、回転の様子など）は、実行するたびに同じ結果になります。

## 最適化のベストプラクティス

### 1. HashMap最適化
```java
// 要素数がわかっているなら、リサイズが起きない大きさで作る（Java 19以降）
int expectedSize = 1000;
Map<String, Object> map = HashMap.newHashMap(expectedSize);

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
Map<LocalDate, Event> timeline = new TreeMap<>(); // 自動ソート

// 範囲検索が必要
TreeMap<Integer, Product> productsByPrice = new TreeMap<>();
// ...商品を追加したあとで
NavigableMap<Integer, Product> priceRange = 
    productsByPrice.subMap(minPrice, true, maxPrice, true); // 効率的な範囲検索
```

## 高度なトピック

### 1. Java 8以降の改善
- **Treeification**: 長いチェーンの木化（上の「衝突処理とJava 8の改善」を参照）
- **ハッシュ改善**: 上位ビットを下位ビットに混ぜるハッシュ関数（`h ^ (h >>> 16)`）

### 2. スレッドセーフ版
- **ConcurrentHashMap**: Java 8以降は、バケット単位のCAS（比較して交換する操作）とロックで並行アクセスを扱う（Java 7まではセグメント分割）
- **Collections.synchronizedMap()**: 同期化ラッパー
- **ConcurrentSkipListMap**: TreeMapの並行版

## 関連技術とアルゴリズム

- **ハッシュ関数**: MD5、SHA、MurmurHash
- **平衡木**: AVL木、B-tree、2-3木
- **並行データ構造**: Lock-free algorithms
- **キャッシュ効率**: CPU cache line considerations
- **分散ハッシュ**: Consistent hashing

このプロジェクトを通じて、コレクションフレームワークの内部動作を深く理解し、実際のアプリケーションで最適なデータ構造を選択する能力を身につけることができます。
