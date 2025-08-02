# 校正作業の指示方法

## 基本的な指示例

### 1. シンプルな校正依頼
```
「第5章を校正してください。tools/proofreading_workflow.mdに従ってください」
```

### 2. 具体的な校正依頼
```
「manuscripts/chapter05-inheritance-and-polymorphism.mdを校正してください。
以下の手順で実施してください：
1. tools/proofreading_workflow.mdの手順に従う
2. ファイルが大きい場合は分割ツールを使用
3. 日本語の自然さを重視
4. textlintエラーも可能な範囲で修正」
```

### 3. 特定の問題に焦点を当てた校正
```
「第6章のtextlintエラーを修正してください。
特に以下の点に注意：
- コロンパターンのエラー
- リスト項目の文体統一（である調）
- tools/proofreading_workflow.mdを参照」
```

## 推奨される指示フォーマット

### 完全版（最も確実）
```
日本語技術書の校正をお願いします。

対象ファイル: manuscripts/chapterXX-title.md
参照ドキュメント: 
- tools/proofreading_workflow.md（作業手順）
- tools/proofreading_checklist.md（確認項目）
- tools/README_PROOFREADING.md（ツールの使い方）

注意事項:
- 助詞（より、から、ため等）の変更は慎重に
- 自動校正の結果は必ず確認
- 大規模ファイルは分割して作業

実施内容:
1. 冗長表現の簡潔化
2. textlintエラーの修正（可能な範囲で）
3. 日本語として自然な表現への改善
```

### 簡易版（基本的な校正）
```
manuscripts/chapterXX.mdを校正してください。
tools/proofreading_workflow.mdの手順に従い、
特に「より」「から」などの助詞の変更には注意してください。
```

### 最小版（ツールを知っている前提）
```
第X章を標準の校正ワークフローで校正してください。
```

## キーワードとトリガー

以下のキーワードを含めると、適切な校正手順が呼び出されやすくなります：

- 「proofreading_workflow.md」
- 「校正ワークフロー」
- 「標準の校正手順」
- 「助詞の変更に注意」
- 「大規模ファイルは分割」

## 具体的な使用例

### 例1: 新しい章の校正
```
「第10章（ジェネリクス）の原稿が完成しました。
tools/proofreading_workflow.mdに従って校正してください。
特に技術用語は変更せず、日本語の自然さを改善してください。」
```

### 例2: textlintエラーの対応
```
「npm run lintで多数のエラーが出ています。
第7章のtextlintエラーを、proofreading_workflow.mdの
優先順位に従って修正してください。」
```

### 例3: 部分的な校正
```
「第3章の1000行目から1500行目あたりで、
日本語が不自然な箇所があります。
該当部分をproofreading_checklistに従って校正してください。」
```

## 注意事項の伝え方

### 必ず伝えるべきこと
1. 対象ファイルのパス
2. 参照すべきワークフロードキュメント
3. 特に注意すべき点（あれば）

### 避けるべき指示
- 「全部自動で修正して」（危険）
- 「とにかく早く」（品質低下）
- 「textlintエラーを全部消して」（不適切な修正の恐れ）

## セッション開始時の確認

新しいセッションでは、以下を確認してもらうと良いでしょう：

```
「校正作業を始める前に、以下を確認してください：
1. tools/proofreading_workflow.mdが存在するか
2. 前回の校正での注意点（proofreading_session_summary.md）
3. プロジェクトのガイドライン（CLAUDE.md）」
```

## トラブルシューティング用の指示

### ツールが見つからない場合
```
「tools/ディレクトリに校正用のPythonスクリプトがあるはずです。
manuscript_proofreader.pyやefficient_proofreading.pyを確認してください。」
```

### 前回の失敗を避けたい場合
```
「前回『より』を『から』に誤って変換した問題がありました。
proofreading_checklistの『やってはいけないこと』を
必ず確認してから作業してください。」
```