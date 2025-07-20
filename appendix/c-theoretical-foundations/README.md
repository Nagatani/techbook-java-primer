# 付録C: ソフトウェア工学の理論的基盤 - 実装例

このディレクトリには、付録C「ソフトウェア工学の理論的基盤」で解説されている理論的概念の実装例が含まれています。

## ディレクトリ構造

```
src/main/java/com/example/theory/
├── complexity/      # 複雑度とソフトウェアメトリクス
├── patterns/        # デザインパターンの理論的分析
├── algorithms/      # アルゴリズムと構造化プログラミング
├── concurrent/      # 並行プログラミングの理論
├── structures/      # データ構造と抽象データ型
└── specifications/  # 形式手法と仕様記述
```

## パッケージごとの内容

### complexity パッケージ
ソフトウェアの複雑度、結合度、凝集度に関する実装例

- **ComplexityExample.java** (リストAC-1, AC-2)
  - 循環的複雑度の高いコードと改善例
  
- **TimeComplexityExamples.java** (リストAC-22, AC-23, AC-24)
  - O(n²) バブルソート、O(n log n) マージソート
  - 空間複雑度の分析（in-placeソート vs コピーソート）
  
- **CouplingExamples.java** (リストAC-12, AC-13, AC-14, AC-15)
  - 結合度の階層（データ結合、スタンプ結合、制御結合、共通結合）
  
- **CohesionExamples.java** (リストAC-16, AC-17, AC-18)
  - 凝集度の階層（機能的凝集、逐次的凝集、偶発的凝集）

### algorithms パッケージ
アルゴリズム解析と構造化プログラミングの理論

- **StructuredProgramming.java** (リストAC-3, AC-4, AC-5, AC-6)
  - Dijkstraの構造化定理（順次、分岐、反復）
  - ループ不変条件の実装例
  
- **BinarySearchWithSpecs.java** (リストAC-26)
  - 形式的仕様を含む二分探索アルゴリズム
  - 事前条件・事後条件の検証

### structures パッケージ
抽象データ型（ADT）とデータ構造の理論的実装

- **Stack.java** / **ArrayStack.java** (リストAC-7)
  - スタックADTの抽象仕様と具体実装
  
- **EncapsulationExamples.java** (リストAC-8, AC-9)
  - カプセル化の悪い例と良い例
  
- **LiskovSubstitution.java** (リストAC-10, AC-11)
  - リスコフの置換原則（LSP）違反例と遵守例
  
- **DynamicArray.java** (リストAC-24)
  - 動的配列の償却解析
  
- **HashTable.java** (リストAC-25)
  - ハッシュテーブルの理論的実装とパフォーマンス分析

### patterns パッケージ
GoFデザインパターンの理論的分析

- **SingletonPattern.java** (リストAC-19)
  - スレッドセーフなSingleton実装（Double-checked locking）
  - Enumによる実装、Bill Pugh Singleton
  
- **DecoratorPattern.java** (リストAC-20)
  - Decoratorパターンの数学的モデル（関数合成）
  
- **ObserverPattern.java** (リストAC-21)
  - Observerパターンとイベント代数

### concurrent パッケージ
並行プログラミングの理論的基盤

- **RaceConditionExample.java** (リストAC-28)
  - データ競合の定義と実例
  - 同期化とAtomicクラスによる解決
  
- **DeadlockExample.java** (リストAC-29)
  - コフマンの4条件とデッドロック
  - ロック順序統一による回避策
  
- **MemoryModelExample.java** (リストAC-30)
  - Java Memory Model (JMM)
  - volatileとsynchronizedによる順序保証

### specifications パッケージ
形式手法と仕様記述の実装

- **BankAccountWithInvariants.java** (リストAC-27)
  - クラス不変条件の設計と維持
  - 事前条件・事後条件の検証

## 実行方法

各クラスには理論を実証するためのデモンストレーションメソッドが含まれています。

### 例：複雑度の分析
```java
// ComplexityExample の実行
ComplexityExample example = new ComplexityExample();
String result1 = example.processDataComplex(1, true, "Hello World");
String result2 = example.processData(1, true, "Hello World");
```

### 例：並行プログラミングのデモ
```java
// 競合状態のデモンストレーション
RaceConditionExample.demonstrateRaceCondition();

// デッドロックのデモンストレーション
DeadlockExample.demonstrateDeadlock();

// メモリモデルのデモンストレーション
MemoryModelExample.demonstrateMemoryModel();
```

### 例：デザインパターンのデモ
```java
// Decoratorパターンのデモ
DecoratorPattern.demonstrateDecoratorPattern();

// Observerパターンのデモ
ObserverPattern.demonstrateObserverPattern();
```

## 理論と実装の対応

各実装は付録Cの対応するセクションの理論的概念を具体化したものです：

- **C.1**: ソフトウェアクライシスと複雑性管理 → complexityパッケージ
- **C.2**: オブジェクト指向の理論的基盤 → structuresパッケージ
- **C.3**: ソフトウェア品質メトリクス → complexityパッケージ
- **C.4**: 設計パターンの理論的分析 → patternsパッケージ
- **C.5**: アルゴリズム解析と設計 → algorithmsパッケージ
- **C.6**: 形式手法と仕様記述 → specificationsパッケージ
- **C.7**: 並行プログラミングの理論 → concurrentパッケージ

## 学習のポイント

1. **理論の実践的価値**: 各実装は単なる学術的興味ではなく、実際の開発で直面する問題の解決に役立ちます

2. **コードの品質向上**: 複雑度、結合度、凝集度の理解により、より保守性の高いコードを書けるようになります

3. **並行性の理解**: 競合状態やデッドロックの理論的理解により、マルチスレッドプログラムの安全な実装が可能になります

4. **設計の改善**: デザインパターンの数学的モデルを理解することで、より適切なパターンの選択ができます

これらの実装例を通じて、ソフトウェア工学の理論的基盤と実践的応用の両方を理解してください。