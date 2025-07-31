# 推奨コマンド一覧

## ビルドコマンド
- `npm run build`: PDFを生成（出力先: output/techbook-java-primer.pdf）
- `npm run build4web`: Web公開用HTML生成（出力先: dist/）
- `npm run preview`: ローカルプレビューサーバー起動（http://localhost:13000）

## リンティング・フォーマット
- `npm run lint`: 原稿のtextlintチェック（manuscripts/*.md）
- `npm run lint:fix`: textlintエラーの自動修正
- `npm run lintadx`: 付録のtextlintチェック（appendix/**/*.md）
- `npm run lintadx:fix`: 付録のtextlintエラー自動修正

## 開発フロー
1. `npm run preview` でプレビューサーバー起動
2. 原稿編集
3. `npm run lint` でチェック
4. `npm run lint:fix` で自動修正
5. `npm run build` でPDF生成して確認

## Git操作
- `git status`: 変更状態確認
- `git diff`: 変更内容確認
- `git add .`: 変更をステージング
- `git commit -m "メッセージ"`: コミット
- `git push`: リモートへプッシュ

## システムユーティリティ（macOS）
- `ls -la`: ファイル一覧（隠しファイル含む）
- `find . -name "*.md"`: Markdownファイル検索
- `grep -r "検索語" manuscripts/`: 原稿内テキスト検索
- `open output/techbook-java-primer.pdf`: PDF開く（macOS）