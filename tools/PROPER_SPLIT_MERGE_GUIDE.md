# 適切な分割・結合ツールガイド

## 概要
`split_manuscript_properly.py`は、原稿ファイルを適切にチャンクに分割し、無駄な改行を入れずに結合できるツールです。

## 問題点の解決
従来の方法では、チャンクの結合時に以下の問題がありました：
- チャンクの境界で無駄な改行が挿入される
- コードブロックの途中で改行が入る
- 元のファイルと異なる改行パターンになる

本ツールはこれらの問題を解決します。

## 使用方法

### 1. ファイルの分割
```bash
python3 tools/split_manuscript_properly.py split manuscripts/chapter03-oop-basics.md
```

オプション：
- `-o, --output-dir`: 出力ディレクトリ（デフォルト: proofreading_chunks）
- `-s, --chunk-size`: チャンクサイズ（デフォルト: 250行）

### 2. チャンクの結合
```bash
python3 tools/split_manuscript_properly.py merge chapter03-oop-basics manuscripts/chapter03-oop-basics_merged.md
```

引数：
- `base_name`: ファイルのベース名（拡張子なし）
- `output_file`: 出力ファイルパス

オプション：
- `-d, --chunks-dir`: チャンクディレクトリ（デフォルト: proofreading_chunks）

## 特徴

### 1. 改行の保持
- 元のファイルの改行パターンを完全に保持
- チャンク境界で余計な改行を追加しない

### 2. コードブロックの保護
- コードブロック（```で囲まれた部分）の途中で分割しない
- コードブロック内で分割が必要な場合、ブロックの終了まで含める

### 3. メタデータ管理
- 各チャンクの情報をJSONファイルで管理
- 分割情報（行範囲、ステータス等）を記録

### 4. ヘッダー・フッターの適切な処理
- 校正用のヘッダー・フッターを追加
- 結合時に正確に除去

## 実装の詳細

### ヘッダー除去ロジック
```python
# ヘッダーの終了を探す
for i, line in enumerate(lines):
    if line.strip() == '-->' and i > 0:
        content_start = i + 2  # ヘッダーの後の空行をスキップ
        break
```

### フッター除去ロジック
```python
# フッターの開始を探す
for i in range(len(lines) - 1, -1, -1):
    if lines[i].strip() == '<!--' and i < len(lines) - 1:
        content_end = i
        # フッター前の空行も除去
        while content_end > 0 and lines[content_end - 1].strip() == '':
            content_end -= 1
        break
```

## 使用例

### 完全な校正ワークフロー
```bash
# 1. ファイルを分割
python3 tools/split_manuscript_properly.py split manuscripts/chapter03-oop-basics.md

# 2. 各チャンクを校正（手動またはツールで）
# ...

# 3. チャンクを結合
python3 tools/split_manuscript_properly.py merge chapter03-oop-basics manuscripts/chapter03-oop-basics.md

# 4. 結果を確認
npm run lint manuscripts/chapter03-oop-basics.md
```

## 注意事項

1. **バックアップ**: 結合前に元のファイルをバックアップすることを推奨
2. **チャンクの整合性**: すべてのチャンクが揃っていることを確認
3. **メタデータファイル**: `*_metadata.json`ファイルが必要

## トラブルシューティング

### 問題: 結合後に改行が異なる
- 原因: チャンクファイルが手動で編集され、改行パターンが変更された
- 解決: エディタの改行設定を確認し、LFで統一する

### 問題: コードブロックが壊れる
- 原因: 手動編集時にコードブロックの開始・終了が不整合
- 解決: 各チャンクでコードブロックの対応を確認