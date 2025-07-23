# 書籍プロジェクト作業用スクリプト

このディレクトリには、Java入門書プロジェクトの校正・校閲作業で使用したPythonスクリプトが含まれています。

## スクリプト一覧

### 分析用スクリプト
- `analyze_book_structure.py` - 書籍全体の構造を分析（章数、コードブロック数、演習セクション等）
- `find_dearu_usage.py` - である調の使用箇所を検出（リスト項目を除く）
- `find_duplicate_listings.py` - コードリスト番号の重複を検出
- `analyze_code_block_types.py` - コードブロックの言語タイプ別の番号付与状況を分析
- `analyze_none_blocks.py` - 番号なしブロックの内容分類
- `analyze_code_blocks_detailed.py` - コードブロックの詳細な分類と分析

### 修正用スクリプト
- `fix_dearu_style.py` - である調をですます調に自動修正
- `add_code_listing_numbers.py` - 番号なしコードブロックに番号を付与
- `fix_chapter12_listings.py` - chapter12の誤った番号（13-X）を修正
- `restore_sample_code_format.py` ⭐ - **正しい形式**：「サンプルコードX-Y」形式に統一

### 廃止されたスクリプト
- `fix_duplicate_listings.py` ❌ - 誤って「リストX.Y」形式に変更してしまうため使用禁止
- `fix_long_sentences.py` - 長文修正（未完成、手動対応が必要）

## 重要な注意事項

### コードリスト番号の形式
**正しい形式**: `<span class="listing-number">**サンプルコードX-Y**</span>`
- X: 章番号
- Y: 章内の連番（1から始まる）
- ハイフン（-）で区切る
- **対象**: Javaコードおよびプログラミング言語のコードブロックのみ

**誤った形式**: ~~`**リストX.Y**`~~ （使用禁止）

### 番号付与のルール
#### 番号を付けるもの
- Javaソースコード（.java）
- XMLファイル（pom.xml, web.xml等）
- プロパティファイル、設定ファイル
- その他のプログラミング言語のコード（Python, JavaScript, C等）

#### 番号を付けないもの
- エラー出力、例外スタックトレース
- プログラムの実行結果、コンソール出力
- ディレクトリ構造、ASCII図表
- コマンドライン操作の例（ターミナル表示）

### 作業時の注意
1. コードリスト番号は必ず「サンプルコード」形式を使用する
2. 「リスト」形式は使用しない
3. エラー出力や実行結果には番号を付けない
4. 修正作業前に必ずCLAUDE.mdの最新ルールを確認する

## 使用例

```bash
# である調の検出
python3 scripts/find_dearu_usage.py

# である調の自動修正
python3 scripts/fix_dearu_style.py

# コードリスト番号の付与
python3 scripts/add_code_listing_numbers.py

# サンプルコード形式への復元（必要な場合）
python3 scripts/restore_sample_code_format.py
```