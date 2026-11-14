# 付録C: ソフトウェア工学の理論的基盤 - 実装例

このディレクトリには、付録C「ソフトウェア工学の理論的基盤」で扱う理論的な概念の実装例が含まれています。

## ディレクトリ構造

```
src/main/java/com/example/theory/
├── Main.java        # すべてのデモを呼び出すメインクラス
├── complexity/      # 複雑度とソフトウェアメトリクス
├── patterns/        # デザインパターンの理論的分析
├── algorithms/      # アルゴリズムと構造化プログラミング
├── concurrent/      # 並行プログラミングの理論
├── structures/      # データ構造と抽象データ型
└── specifications/  # 形式手法と仕様記述
```

## コンパイルと実行

このディレクトリ（`appendix/c-theoretical-foundations/`）で、次のコマンドを実行します。
Java 25でコンパイル・実行できます。

```bash
# コンパイル（クラスファイルはout/に出力する）
javac -d out $(find src -name '*.java')

# 実行（-eaでassert文を有効にする）
java -ea -cp out com.example.theory.Main all
```

Windowsのコマンドプロンプトでは、`$(find ...)`の代わりに`dir /s /b src\*.java > sources.txt`でファイルの一覧を作り、`javac -d out @sources.txt`でコンパイルします。

`all`の代わりに次の名前を指定すると、そのデモだけを実行します。

| 名前 | 内容 |
|------|------|
| `complexity` | 循環的複雑度、計算量、結合度、凝集度 |
| `algorithms` | 構造化プログラミング、ループ不変条件、二分探索 |
| `structures` | スタック、動的配列、ハッシュテーブル、リスコフの置換原則 |
| `patterns` | Singleton、Decorator、Observer |
| `concurrent` | 競合状態、デッドロック、メモリモデル |
| `specifications` | クラス不変条件、事前条件 |

### assert文と-eaオプション

事前条件・事後条件・不変条件の検査（`BinarySearchWithSpecs`、`StructuredProgramming.findMaxWithInvariantCheck`、`BankAccountWithInvariants`）と、リスコフの置換原則の検査（`LiskovSubstitution`）は`assert`文で書いています。
`assert`文は、`java`コマンドに`-ea`を付けたときだけ実行されます。
`-ea`を付けないと検査が行われないので、`Main`は注意を表示し、LSPの検査を省きます。

### 並行プログラミングのデモについて

`concurrent`のデッドロックのデモは、実際にデッドロックを起こします。
デッドロックしたスレッドはデーモンスレッドにしてあるため、5秒待ったあと「Threads appear to be deadlocked!」と表示して先へ進み、プログラムは最後まで終わります。
競合状態のデモで表示されるカウンターの値は、実行のたびに変わります。

## パッケージごとの内容

### complexity パッケージ
ソフトウェアの複雑度、結合度、凝集度、計算量に関する実装例です。

- `ComplexityExample.java`
  - 循環的複雑度の高いコードと改善例
- `TimeComplexityExamples.java`
  - O(n²)のバブルソート、O(n log n)のマージソート
  - 空間計算量の比較（in-placeのソートと配列をコピーするソート）
- `CouplingExamples.java`
  - 結合度の段階（データ結合、スタンプ結合、制御結合、共通結合）
- `CohesionExamples.java`
  - 凝集度の段階（機能的凝集、逐次的凝集、偶発的凝集）

### algorithms パッケージ
アルゴリズムの解析と構造化プログラミングの実装例です。

- `StructuredProgramming.java`
  - Böhm–Jacopiniの構造化定理（順次、分岐、反復）
  - ループ不変条件の実装例
- `BinarySearchWithSpecs.java`
  - 形式的な仕様を添えた二分探索
  - 事前条件・事後条件の検証

### structures パッケージ
抽象データ型（ADT）とデータ構造の実装例です。

- `Stack.java` / `ArrayStack.java`
  - スタックADTの抽象仕様と具体的な実装
- `EncapsulationExamples.java`
  - カプセル化の悪い例とよい例
- `LiskovSubstitution.java`
  - リスコフの置換原則（LSP）に違反する例と守る例
- `DynamicArray.java`
  - 動的配列の償却解析
- `HashTable.java`
  - チェイン法によるハッシュテーブルと負荷率の分析

### patterns パッケージ
GoFのデザインパターンを理論の面から分析する実装例です。

- `SingletonPattern.java`
  - Double-checked lockingによるスレッドセーフなSingleton
  - enumによる実装、Bill Pugh Singleton
- `DecoratorPattern.java`
  - Decoratorパターンを関数の合成として見るモデル
- `ObserverPattern.java`
  - Observerパターンとイベント通知

### concurrent パッケージ
並行プログラミングの理論の実装例です。

- `RaceConditionExample.java`
  - データ競合の例
  - Atomicクラスによる解決（synchronizedによる解決はメソッドだけを示す）
- `DeadlockExample.java`
  - コフマンの4条件とデッドロック
  - ロックの取得順序をそろえる回避策
- `MemoryModelExample.java`
  - Javaメモリモデル（JMM）
  - volatileとsynchronizedによる順序の保証

### specifications パッケージ
形式手法と仕様記述の実装例です。

- `BankAccountWithInvariants.java`
  - クラス不変条件の設計と維持
  - 事前条件・事後条件の検証

## 付録Cの学習内容と実装の対応

付録Cの「学習内容」の各項目と、このディレクトリの実装は次のように対応します。

| 学習内容 | 実装 |
|----------|------|
| 1. ソフトウェアクライシスとその解決策 | `complexity/ComplexityExample.java` |
| 2. オブジェクト指向の理論的基盤（ADT、カプセル化、継承） | `structures`パッケージ |
| 3. ソフトウェア品質メトリクス（結合度、凝集度） | `complexity/CouplingExamples.java`、`complexity/CohesionExamples.java` |
| 4. デザインパターンの理論的分析 | `patterns`パッケージ |
| 5. アルゴリズム解析と計算複雑度 | `algorithms`パッケージ、`complexity/TimeComplexityExamples.java`、`structures/DynamicArray.java`、`structures/HashTable.java` |
| 6. 形式手法と仕様記述 | `specifications`パッケージ、`algorithms/BinarySearchWithSpecs.java` |
| 7. 並行プログラミングの理論 | `concurrent`パッケージ |

## 学習のポイント

1. 理論の実践的な価値
    + 各実装は、実際の開発で直面する問題を解くための考え方を示している
2. コードの品質の向上
    + 複雑度、結合度、凝集度を理解すると、保守しやすいコードを書ける
3. 並行性の理解
    + 競合状態やデッドロックの仕組みを理解すると、マルチスレッドのプログラムを安全に組み立てられる
4. 設計の改善
    + デザインパターンを理論の面から理解すると、場面に合ったパターンを選べる

これらの実装例を通じて、ソフトウェア工学の理論的基盤と実践的な応用の両方を理解してください。
