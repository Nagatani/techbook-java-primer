# 第8章 基本課題

## 🎯 学習目標
- コレクションフレームワークの基本概念と階層構造の理解
- List、Set、Mapの特性と使い分け
- ArrayList、LinkedList、HashSet、TreeSet、HashMap、TreeMapの実装の違い
- 効率的なデータ構造選択の判断基準
- Iteratorを使った安全な要素操作
- カスタムComparatorとComparableの実装による高度なソート機能

## 📝 課題一覧

### 課題1: 学生管理システム（List活用）
**ファイル名**: `Student.java`, `StudentManager.java`, `StudentManagerTest.java`

学生情報を管理するシステムを作成し、Listの特性を活用してください。

**要求仕様**:
- Student クラス（ID、名前、成績）
- StudentManager でリスト操作（追加、削除、検索、ソート）
- ArrayList と LinkedList の性能比較
- 成績順、名前順でのソート機能

**実行例**:
```
=== 学生管理システム（List活用）===
学生を追加:
ID: 001, 名前: 田中太郎, 成績: 85
ID: 002, 名前: 佐藤花子, 成績: 92
ID: 003, 名前: 鈴木一郎, 成績: 78

名前で検索（田中）: 田中太郎（成績: 85）

成績順ソート（降順）:
1位: 佐藤花子（92点）
2位: 田中太郎（85点）
3位: 鈴木一郎（78点）

ArrayList vs LinkedList 性能テスト:
ArrayList 追加時間: 15ms
LinkedList 追加時間: 8ms
ArrayList アクセス時間: 2ms
LinkedList アクセス時間: 45ms
```

**評価ポイント**:
- List インターフェイスの理解
- ArrayList と LinkedList の使い分け
- Comparator を使ったソート



### 課題2: 単語カウンター（Map活用）
**ファイル名**: `WordCounter.java`, `WordCounterTest.java`

テキストの単語出現頻度を集計するシステムを作成し、Mapの特性を活用してください。

**要求仕様**:
- テキストから単語を抽出して出現回数をカウント
- HashMap と TreeMap の使い分け
- 頻度順、アルファベット順でのソート
- 統計情報の提供（総単語数、ユニーク単語数など）

**実行例**:
```
=== 単語カウンター（Map活用）===
入力テキスト: "Java is fun. Java is powerful. Programming with Java is exciting."

単語出現頻度（HashMap使用）:
Java: 3回
is: 3回
fun: 1回
powerful: 1回
Programming: 1回
with: 1回
exciting: 1回

アルファベット順（TreeMap使用）:
Java: 3回
Programming: 1回
exciting: 1回
fun: 1回
is: 3回
powerful: 1回
with: 1回

出現頻度順（上位3位）:
1位: Java（3回）
1位: is（3回）
3位: fun（1回）

統計情報:
総単語数: 9個
ユニーク単語数: 7個
平均出現回数: 1.29回
```

**評価ポイント**:
- Map インターフェイスの理解
- HashMap と TreeMap の特性理解
- EntrySet を使った効率的な処理



### 課題3: 重複除去システム（Set活用）
**ファイル名**: `DuplicateRemover.java`, `DuplicateRemoverTest.java`

データの重複を除去するシステムを作成し、Setの特性を活用してください。

**要求仕様**:
- 文字列リストから重複を除去
- HashSet と TreeSet の使い分け
- カスタムオブジェクトの重複判定
- 集合演算（和集合、積集合、差集合）の実装

**実行例**:
```
=== 重複除去システム（Set活用）===
元のリスト: [apple, banana, apple, orange, banana, grape]

HashSet で重複除去（挿入順不保証）:
結果: [banana, orange, apple, grape]
処理時間: 5ms

TreeSet で重複除去（自動ソート）:
結果: [apple, banana, grape, orange]
処理時間: 12ms

集合演算テスト:
セットA: [Java, Python, C++]
セットB: [Python, JavaScript, Java]

和集合: [Java, Python, C++, JavaScript]
積集合: [Java, Python]
差集合（A-B）: [C++]
差集合（B-A）: [JavaScript]

カスタムオブジェクト重複除去:
Person{name='田中', age=25}
Person{name='佐藤', age=30}
Person{name='鈴木', age=25}
```

**評価ポイント**:
- Set インターフェイスの理解
- HashSet と TreeSet の性能差理解
- equals() と hashCode() の重要性理解



### 課題4: 在庫管理システム（複合コレクション活用）
**ファイル名**: `Product.java`, `InventoryManager.java`, `InventoryManagerTest.java`

商品在庫を管理するシステムを作成し、複数のコレクションを組み合わせて活用してください。

**要求仕様**:
- Product クラス（ID、名前、価格、カテゴリ）
- カテゴリ別商品管理（Map<String, List<Product>>）
- 価格帯別集計（TreeMap使用）
- 在庫検索・フィルタリング機能

**実行例**:
```
=== 在庫管理システム（複合コレクション活用）===
商品登録:
[家電] ノートPC（150,000円）
[家電] スマートフォン（80,000円）
[書籍] Java入門（3,000円）
[書籍] Python基礎（2,500円）
[食品] りんご（500円）

カテゴリ別在庫:
家電カテゴリ: 2商品
  - ノートPC: 150,000円
  - スマートフォン: 80,000円
書籍カテゴリ: 2商品
  - Java入門: 3,000円
  - Python基礎: 2,500円
食品カテゴリ: 1商品
  - りんご: 500円

価格帯別集計:
0-1,000円: 1商品
1,001-10,000円: 2商品
10,001-100,000円: 1商品
100,001円以上: 1商品

検索結果（価格5,000円以下）:
Java入門（3,000円）
Python基礎（2,500円）
りんご（500円）

統計情報:
総商品数: 5個
平均価格: 47,200円
最高価格: 150,000円（ノートPC）
最低価格: 500円（りんご）
```

**評価ポイント**:
- 複数コレクションの組み合わせ活用
- ネストしたコレクション構造の設計
- 効率的な検索・集計処理の実装

## 💡 ヒント

### 課題1のヒント
- Comparator.comparing() でソート条件を指定
- System.currentTimeMillis() で処理時間測定
- リストサイズが大きい場合の性能差に注目

### 課題2のヒント
- String.split() と String.toLowerCase() で単語抽出
- Map.getOrDefault() で初期値設定
- Stream API で値によるソートも可能

### 課題3のヒント
- Set の contains() は O(1) または O(log n)
- Collections の集合演算メソッドを活用
- equals() と hashCode() の一貫性が重要

### 課題4のヒント
- Map.computeIfAbsent() でネストした構造を効率的に構築
- TreeMap で価格帯をキーとした自動ソート
- Stream API でフィルタリングと集計を効率化

## 🔍 コレクションフレームワークのポイント

1. **List**: 順序付き、重複許可、インデックスアクセス
2. **Set**: 重複なし、数学的集合演算
3. **Map**: キー・値ペア、キーの重複なし
4. **実装選択**: ArrayList vs LinkedList, HashMap vs TreeMap
5. **時間計算量**: O(1), O(log n), O(n) の理解
6. **Iterator**: 安全な要素操作、ConcurrentModificationException回避

## ✅ 完了チェックリスト

- [ ] 課題1: List の特性を活かした学生管理ができている
- [ ] 課題2: Map を使った効率的な単語集計ができている
- [ ] 課題3: Set による重複除去と集合演算ができている
- [ ] 課題4: 複合コレクションで複雑なデータ管理ができている
- [ ] コレクションの時間計算量を理解している
- [ ] 適切なコレクション選択ができている

**次のステップ**: 基本課題が完了したら、advanced/の発展課題でより複雑なデータ構造活用に挑戦しましょう！