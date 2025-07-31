# タスク完了時のチェックリスト

## 原稿編集後の必須確認事項

### 1. textlintチェック
```bash
npm run lint
```
- エラーがある場合は`npm run lint:fix`で自動修正
- 自動修正できないエラーは手動で修正

### 2. ビルド確認
```bash
npm run build
```
- PDFが正常に生成されることを確認
- エラーが出た場合は修正

### 3. プレビュー確認
```bash
npm run preview
```
- ブラウザで表示確認
- レイアウト崩れがないか確認

### 4. 分析ファイルの更新
- `docs/analysis/issues-tracking.md`: 解決した問題をマーク
- `docs/analysis/comprehensive-project-analysis.md`: 大きな構造変更時
- `docs/analysis/improvement-roadmap.md`: ロードマップの進捗更新

### 5. TODO.mdの更新
- 完了したタスクにチェック
- 新たに発見した課題を追加

### 6. コミット前の最終確認
- `git status`で変更ファイル確認
- `git diff`で変更内容確認
- 不要なファイルが含まれていないか確認

## 重要な注意事項
- **必須**: `docs/writing-guidelines-textlint-compliance.md`を参照して執筆
- コードブロック内のテキストはtextlint修正対象外
- 技術用語・固有名詞のtextlintエラーは無視可能