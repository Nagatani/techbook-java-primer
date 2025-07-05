# 第13章 Enum 解答例

この章では、Javaの列挙型（Enum）の様々な使用方法を学習します。

## 実装内容

### 1. Status.java - 基本的なEnum使用例
- システムの状態を表現するEnum
- 状態遷移の制御
- 終了状態の判定
- 日本語表示名の提供

**重要ポイント：**
- Switch式を使用した現代的な記述
- 状態遷移ルールの実装
- Enumの基本的な使用方法

### 2. Planet.java - 値を持つEnum
- 太陽系の惑星の物理的特性を管理
- 質量と半径を持つEnum定数
- 重力計算などの複雑な処理

**重要ポイント：**
- Enumに値を持たせる方法
- 物理計算の実装
- 実用的なデータ管理

### 3. OperationEnum.java - 抽象メソッドを持つEnum
- 計算機の四則演算を表現
- 各定数で異なる処理を実装
- 演算子の優先順位管理

**重要ポイント：**
- 抽象メソッドの実装
- 各Enum定数での異なる振る舞い
- 例外処理の実装

### 4. StateMachine.java - 状態遷移システム
- **EnumSetとEnumMapの高性能活用**
- 複雑な状態遷移ルールの管理
- 遷移履歴の記録

**重要ポイント：**
- **EnumSet**: 高性能なEnum専用Set実装
- **EnumMap**: 高性能なEnum専用Map実装
- 状態遷移テーブルの実装
- 複雑なビジネスロジックの管理

## 学習のポイント

1. **Enumの基本概念**
   - 定数の型安全な定義
   - switch文での使用

2. **値を持つEnum**
   - コンストラクタの定義
   - インスタンス変数の管理

3. **抽象メソッドを持つEnum**
   - 各定数での異なる実装
   - ポリモーフィズムの活用

4. **EnumSetとEnumMapの活用**
   - 高性能なEnum専用コレクション
   - ビット演算による最適化
   - 実用的なデータ構造設計

## 実行方法

```bash
# コンパイル
javac *.java

# テスト実行（JUnit 5が必要）
java -cp .:junit-platform-console-standalone-1.8.2.jar org.junit.platform.console.ConsoleLauncher --scan-classpath

# 個別実行例
java -cp . StatusTest
java -cp . PlanetTest
java -cp . OperationEnumTest
java -cp . StateMachineTest
```

## 設計パターン

### 状態パターン（State Pattern）
StateMachineクラスは状態パターンの実装例です：
- 状態ごとに異なる振る舞い
- 状態遷移の制御
- 状態の履歴管理

### 戦略パターン（Strategy Pattern）
OperationEnumクラスは戦略パターンの実装例です：
- 異なる演算アルゴリズムの実装
- 実行時の戦略選択
- 拡張可能な設計

## 応用例

1. **ワークフローエンジン**
   - 承認プロセスの管理
   - 状態遷移の制御

2. **ゲーム開発**
   - キャラクターの状態管理
   - アイテムの種類管理

3. **設定管理**
   - 環境設定の管理
   - 機能フラグの管理

## 注意点

- EnumSetとEnumMapは通常のSetやMapより高速
- Enumは本質的にシングルトンパターン
- switch文でEnumを使用する際は、すべての値を処理することを推奨