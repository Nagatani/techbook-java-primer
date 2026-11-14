# Java共通エラーガイド - サンプルコード

このディレクトリには、Java共通エラーガイド（付録H）で解説されているエラーの実例と解決方法を示すサンプルコードが含まれています。

## ディレクトリ構造

```
examples/
├── runtime-errors/          # 実行時エラーのサンプル
│   ├── NullPointerExceptionExample.java
│   └── ArrayIndexOutOfBoundsExceptionExample.java
└── compile-errors/          # コンパイルエラーのサンプル
    └── TypeMismatchExample.java
```

## サンプルコードの実行方法

### 1. コンパイルと実行

実行時エラーのサンプル：
```bash
# コンパイル（パッケージに合わせたディレクトリ構成で out に出力する）
javac -d out runtime-errors/*.java

# 実行
java -cp out examples.runtime.NullPointerExceptionExample
java -cp out examples.runtime.ArrayIndexOutOfBoundsExceptionExample
```

コンパイルエラーのサンプル：
```bash
# コンパイル（一部のコードはコメントアウトされています）
javac -d out compile-errors/TypeMismatchExample.java

# 実行
java -cp out examples.compile.TypeMismatchExample
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

### コンパイルエラー

#### TypeMismatchExample.java
- 基本型の不一致と解決方法
- 参照型の不一致と解決方法
- ジェネリクスの型不一致
- メソッドの戻り値型の問題
- 実用的な型変換の例

## 学習のポイント

1. **エラーメッセージを読む**: 各サンプルでエラーメッセージがどのように表示されるか確認
2. **解決パターンを理解**: 同じエラーに対する複数の解決方法を比較
3. **予防策を学ぶ**: エラーを未然に防ぐコーディング手法を習得
4. **実践的な応用**: 実際の開発で使える実用的なパターンを学習

## 注意事項

- 一部のサンプルコードには、意図的にコンパイルエラーとなるコードが含まれています
- そのようなコードはコメントアウトされており、エラーの内容が説明されています
- 標準出力にJava 25の`IO`クラスを使っているため、実行にはJava 25以降が必要です
- ClassCastException・ConcurrentModificationException・シンボルが見つからないエラー・アクセス制御のエラーは、サンプルコードではなく、`runtime-exceptions/`と`compile-errors/`の各文書の中のコード例で解説しています

## 関連資料

- 付録H本文: エラーの詳細な解説
- 第13章「例外処理」
- 第21章「ユニットテストと品質保証」