# プロジェクト構造

## ディレクトリ構成

### 主要ディレクトリ
- `manuscripts/`: 書籍原稿（Markdownファイル） - **本番コンテンツのみ**
- `docs/`: プロジェクトドキュメント・分析ファイル
  - `docs/analysis/`: プロジェクト分析ファイル
  - `docs/planning/`: 計画ドキュメント
  - `docs/lecture-materials/`: 元講義資料（読み取り専用）
  - `docs/guidelines/`: ガイドライン文書
  - `docs/reference/`: 参照資料
- `source/`: サンプルコード（整理中）
- `sample-code/`: 書籍から抽出したサンプルコード
- `exercises/`: 演習ファイル（GitHub公開用）
- `appendix/`: 付録ファイル（基本的に参照のみ）
- `theme/`: Vivliostyleカスタムテーマ
- `output/`: 生成されたPDF
- `dist/`: 生成されたHTML（Web公開用）
- `assets/`: 画像等のアセット

### 重要な設定ファイル
- `vivliostyle.config.js`: ビルド設定（章構成定義）
- `package.json`: 依存関係とスクリプト定義
- `.textlintrc.json`: textlint設定
- `CLAUDE.md`: プロジェクト固有の指示
- `TODO.md`: 現在の改善計画

## ディレクトリ使用ルール
- `manuscripts/`には書籍コンテンツのみ（管理ファイル作成禁止）
- 分析・ガイドライン等は`docs/`に配置
- 新規ファイルは`vivliostyle.config.js`への追加確認必須