# 第7章 解答例

## 概要

第7章では、抽象クラスとインターフェイスについて学習します。SOLID原則に基づいた設計を重視し、継承とポリモーフィズムを活用した実装を行います。

## 解答例一覧

### 1. 図形の抽象化 (Shape系)

- **Shape.java**: 図形の抽象基底クラス
- **Circle.java**: 円クラス（Shapeを継承）
- **Rectangle.java**: 矩形クラス（Shapeを継承）
- **Drawable.java**: 描画可能なオブジェクトのインターフェイス
- **ShapeTest.java**: 図形クラスのテスト

#### 学習ポイント
- 抽象クラスの定義と継承
- 抽象メソッドの実装
- インターフェイスの定義と実装
- 多態性の活用

### 2. ゲームキャラクター (GameCharacter系)

- **GameCharacter.java**: ゲームキャラクターの抽象基底クラス
- **Player.java**: プレイヤークラス（GameCharacterを継承）
- **Enemy.java**: エネミークラス（GameCharacterを継承）
- **GameTest.java**: ゲームシステムのテスト

#### 学習ポイント
- 抽象クラスによる共通機能の実装
- 派生クラスでの特殊化
- Drawableインターフェイスの実装
- 多態性を活用したゲームシステム

### 3. 支払いシステム (Payable系)

- **Payable.java**: 支払い可能なオブジェクトのインターフェイス
- **Employee.java**: 従業員クラス（Payableを実装）
- **Invoice.java**: 請求書クラス（Payableを実装）
- **PayrollTest.java**: 給与計算システムのテスト

#### 学習ポイント
- インターフェイスによる統一的な処理
- デフォルトメソッドの活用
- 異なる種類のオブジェクトの統一処理
- 実世界の問題をオブジェクト指向で解決

### 4. ソートシステム (Sortable系)

- **Sortable.java**: ソート可能なオブジェクトのインターフェイス
- **Student.java**: 学生クラス（Sortableを実装）
- **SortableTest.java**: ソートシステムのテスト

#### 学習ポイント
- 複数の基準でのソート機能
- 列挙型の活用
- Comparableインターフェイスとの連携
- 柔軟なソート設計

## 設計原則

### SOLID原則の適用

1. **Single Responsibility Principle (SRP)**
   - 各クラスは単一の責任を持つ
   - 例: Shape（図形の基本機能）、Employee（従業員情報）

2. **Open/Closed Principle (OCP)**
   - 拡張に対してオープン、修正に対してクローズ
   - 例: 新しい図形クラスを追加してもShapeクラスは変更不要

3. **Liskov Substitution Principle (LSP)**
   - 派生クラスは基底クラスと置き換え可能
   - 例: Shape参照でCircleやRectangleを使用可能

4. **Interface Segregation Principle (ISP)**
   - インターフェイスを機能別に分割
   - 例: Drawable、Payable、Sortableの分離

5. **Dependency Inversion Principle (DIP)**
   - 抽象に依存し、具象に依存しない
   - 例: Payableインターフェイスを通じた処理

## 実行方法

### テストの実行

```bash
# 全テストの実行
./gradlew test

# 特定の章のテストのみ実行
./gradlew test --tests "chapter07.*"
```

### 個別クラスの実行

```bash
# 図形のテスト
java -cp build/classes/java/main:build/classes/java/test chapter07.solutions.ShapeTest

# ゲームシステムのテスト
java -cp build/classes/java/main:build/classes/java/test chapter07.solutions.GameTest
```

## 重要なポイント

### 1. 抽象クラス vs インターフェイス

- **抽象クラス**: 共通実装を持つ場合に使用
- **インターフェイス**: 契約（メソッドの約束）を定義

### 2. 多態性の活用

```java
// 型の統一による処理
Payable[] payables = {employee, invoice};
for (Payable payable : payables) {
    System.out.println(payable.getPaymentDescription());
}
```

### 3. デフォルトメソッド

```java
// インターフェイスでの共通実装
default String getPaymentDescription() {
    return "支払い対象: " + getPaymentName();
}
```

### 4. 列挙型の活用

```java
// ソート基準の明確化
public enum SortCriteria {
    NAME, SCORE, STUDENT_ID
}
```

## 発展的な学習

1. **Factory Pattern**: オブジェクトの生成を抽象化
2. **Strategy Pattern**: アルゴリズムの交換可能性
3. **Template Method Pattern**: 処理の骨組みを定義
4. **Composite Pattern**: 部分と全体の統一的な扱い

これらの解答例を通じて、抽象化設計の重要性と実装方法を理解し、実際のプロジェクトでの応用力を身につけることができます。