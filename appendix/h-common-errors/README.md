# 付録H: Java共通エラーガイド

## 概要

本付録では、Javaプログラミングで頻繁に遭遇するエラーパターンを体系的にまとめています。各章で特定のトピックに関連するエラーを学習する際、この付録を参照することで、より深い理解が得られます。

## ディレクトリ構造

```
h-common-errors/
├── README.md                    # この文書
├── runtime-exceptions/          # 実行時例外の詳細
│   ├── NullPointerException.md
│   ├── ArrayIndexOutOfBoundsException.md
│   ├── ClassCastException.md
│   └── ConcurrentModificationException.md
├── compile-errors/              # コンパイルエラーの詳細
│   ├── type-mismatch.md
│   ├── symbol-not-found.md
│   └── access-control.md
├── debugging/                   # デバッグ手法
│   ├── stack-trace-reading.md
│   └── debugging-techniques.md
└── examples/                    # エラーサンプルと修正例
    ├── runtime-errors/
    └── compile-errors/
```

## 内容

### 1. 実行時例外
- NullPointerException
- ArrayIndexOutOfBoundsException  
- ClassCastException
- ConcurrentModificationException

### 2. コンパイルエラー
- 型の不一致
- シンボルが見つからない
- アクセス制御違反

### 3. デバッグとトラブルシューティング
- スタックトレースの読み方
- 効果的なデバッグ手法

## 章別参照ガイド

このガイドの内容は、以下の章で特に関連があります：

- **第3章**：NullPointerExceptionの基礎
- **第4章**：オブジェクトの初期化とnullチェック
- **第10章**：コレクションとConcurrentModificationException
- **第11章**：ジェネリクスとClassCastException
- **第14章**：例外処理の詳細

## 利用方法

1. エラーメッセージを確認する
2. 該当するエラーの種類を特定する
3. 本付録の対応するセクションを参照する
4. 提供される解決策を適用する
5. 予防的プログラミング手法を学ぶ

## 更新履歴

- 2025-07: 初版作成