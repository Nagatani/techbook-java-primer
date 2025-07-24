# Java共通エラーガイド - サンプルコード

このディレクトリには、Java共通エラーガイド（付録H）で解説されているエラーの実例と解決方法を示すサンプルコードが含まれています。

## ディレクトリ構造

```
examples/
├── runtime-errors/          # 実行時エラーのサンプル
│   ├── NullPointerExceptionExample.java
│   ├── ArrayIndexOutOfBoundsExceptionExample.java
│   ├── ClassCastExceptionExample.java
│   └── ConcurrentModificationExceptionExample.java
└── compile-errors/          # コンパイルエラーのサンプル
    ├── TypeMismatchExample.java
    ├── SymbolNotFoundExample.java
    └── AccessControlExample.java
```

## サンプルコードの実行方法

### 1. コンパイルと実行

実行時エラーのサンプル：
```bash
# コンパイル
javac runtime-errors/NullPointerExceptionExample.java

# 実行
java -cp runtime-errors examples.runtime.NullPointerExceptionExample
```

コンパイルエラーのサンプル：
```bash
# コンパイル（一部のコードはコメントアウトされています）
javac compile-errors/TypeMismatchExample.java

# 実行
java -cp compile-errors examples.compile.TypeMismatchExample
```

### 2. 統合開発環境（IDE）での実行

1. IDEでプロジェクトを開く
2. サンプルファイルを選択
3. 右クリックして「実行」を選択

## 各サンプルの内容

### 実行時エラー

#### NullPointerExceptionExample.java
- エラーが発生する典型的なパターン
- デフォルト値での初期化による解決
- nullチェックによる解決
- Optionalを使った解決
- メソッドチェーンでの安全な処理

#### ArrayIndexOutOfBoundsExceptionExample.java
- 境界値エラーの例
- 負のインデックスによるエラー
- 正しいループ条件の書き方
- 境界チェックの実装
- 安全なアクセスメソッドの実装
- 二次元配列での注意点

#### ClassCastExceptionExample.java
- 不正なダウンキャストの例
- instanceof演算子による型チェック
- パターンマッチングの使用（Java 14以降）
- ジェネリクスによる型安全性の確保

#### ConcurrentModificationExceptionExample.java
- 反復処理中の要素削除によるエラー
- イテレータのremove()メソッドの使用
- removeIf()メソッドの使用（Java 8以降）
- ConcurrentCollectionの使用

### コンパイルエラー

#### TypeMismatchExample.java
- 基本型の不一致と解決方法
- 参照型の不一致と解決方法
- ジェネリクスの型不一致
- メソッドの戻り値型の問題
- 実用的な型変換の例

#### SymbolNotFoundExample.java
- 変数の宣言忘れ
- スペルミスによるエラー
- スコープ外での変数使用
- インポート忘れ
- 大文字小文字の間違い

#### AccessControlExample.java
- privateフィールドへの不正アクセス
- protectedメンバーへのアクセス制限
- パッケージプライベートの扱い
- ゲッター/セッターの実装
- ビルダーパターンの実装

## 学習のポイント

1. **エラーメッセージを読む**: 各サンプルでエラーメッセージがどのように表示されるか確認
2. **解決パターンを理解**: 同じエラーに対する複数の解決方法を比較
3. **予防策を学ぶ**: エラーを未然に防ぐコーディング手法を習得
4. **実践的な応用**: 実際の開発で使える実用的なパターンを学習

## 注意事項

- 一部のサンプルコードには、意図的にコンパイルエラーとなるコードが含まれています
- そのようなコードはコメントアウトされており、エラーの内容が説明されています
- Java 8以降の機能を使用している箇所があります
- 実行環境に応じて、適切なJavaバージョンを使用してください

## 関連資料

- 付録H本文: エラーの詳細な解説
- 第14章: 例外処理の基礎
- 第21章: ユニットテストでのエラー検証