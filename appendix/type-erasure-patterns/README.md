# 型消去（Type Erasure）とブリッジメソッド

Javaジェネリクスの最も重要かつ複雑な側面である型消去（Type Erasure）について、実行可能なサンプルコードとともに学習できるプロジェクトです。

## 概要

型消去は、Javaが後方互換性を保ちながらジェネリクスを導入するために採用したメカニズムです。コンパイル時には型情報が存在しますが、実行時には削除（消去）されるため、多くの制限と特殊な振る舞いが生じます。

## なぜ型消去を理解する必要があるのか

### 実際に遭遇する問題

- **実行時型情報の喪失**: `List<String>`と`List<Integer>`が実行時に区別できない
- **配列作成の制限**: `new T[10]`のようなジェネリック配列が作成できない
- **メソッドオーバーロードの制限**: 型消去後に同じシグネチャになるメソッドが共存できない

### ビジネスへの影響

- **フレームワーク開発での課題**: Jackson、Spring、Hibernateなどでの型解決問題
- **実際の障害事例**: 型安全性の喪失によるClassCastException
- **API設計の制約**: 型パラメータの表現力不足

## サンプルコード構成

### 1. 基本的な型消去の動作
- `GenericBox.java`: 型消去のプロセスと境界付き型パラメータ
- `GenericService.java`: 実行時型情報の喪失による制限

### 2. ブリッジメソッドの仕組み
- `BridgeMethodExample.java`: コンパイラが生成するブリッジメソッドの実例
- リフレクションによるブリッジメソッドの確認

### 3. 型消去の回避策
- `TypeToken.java`: 型トークンパターン（Super Type Token）
- `TypeReference.java`: Jackson風の型参照パターン
- `ReflectionTypeInfo.java`: リフレクションによる型情報の取得

### 4. ジェネリック配列の問題と回避策
- `GenericArrayWorkarounds.java`: 配列作成の制限とその解決方法

## 実行方法

### 全体のデモンストレーション
```bash
javac -d . src/main/java/com/example/typeerasure/*.java
java com.example.typeerasure.TypeErasureDemo
```

### 個別クラスの実行
```bash
# ジェネリック配列の回避策
java com.example.typeerasure.GenericArrayWorkarounds

# リフレクションによる型分析
java com.example.typeerasure.ReflectionTypeInfo

# ブリッジメソッドの確認
java com.example.typeerasure.BridgeMethodExample
```

## 学習ポイント

### 1. 型消去の基本メカニズム
- コンパイル時とランタイムでの型情報の違い
- 境界付き型パラメータの消去ルール
- Raw型との互換性

### 2. ブリッジメソッドの理解
- なぜブリッジメソッドが必要なのか
- コンパイラが自動生成するメソッド
- リフレクションでの確認方法

### 3. 実践的な回避策
- 型トークンパターンの活用
- Class<T>を使った型安全な設計
- リフレクションによる型情報の取得

### 4. 制限事項と設計指針
- ジェネリック配列が作れない理由
- メソッドオーバーロードの制限
- 代替設計パターン

## 関連技術

- **フレームワークでの応用**: Jackson、Gson、Spring Framework
- **ライブラリ設計**: Guava、Apache Commons
- **実行時型操作**: リフレクション、アノテーション処理

## 参考資料

- Java Language Specification (JLS) §4.6 Type Erasure
- Effective Java 第3版 Item 28: Prefer lists to arrays
- Neal Gafter's Super Type Token pattern

このプロジェクトを通じて、Javaジェネリクスの内部動作を深く理解し、実際の開発で遭遇する問題を解決する技術を身につけることができます。