# 削除の候補

今後の校閲作業では使わない資料です（2026-11-17に整理）。著者が確認してから削除します。
作業の手順や現状の説明としては使いません（旧24章構成の章番号や、いまは使わない手順を含みます）。

元の場所と同じ構成で置いています（`analysis/`・`planning/`・`reference/`・`diagrams/`）。
ここにある資料は、スクリプトやビルドから参照されていません。
`TODO.md`と`docs/analysis/issues-resolved.md`の完了済みの記録に、ここの資料へのリンクが残っています（削除するとリンクが切れますが、作業には影響しません）。

## そのまま削除してよいもの

2025-07〜08の分析・計画で、内容はすべて片付いているか、いまの構成と合わないものです。

| ファイル | 内容 |
|---|---|
| `analysis/appendix-b-reference-mapping.md` | 付録Bの参照の統合表（旧構成） |
| `analysis/bold-text-usage-analysis.md` | 太字の使用状況の分析 |
| `analysis/book-structure-analysis-phase1.md`〜`phase2.md`、`book-structure-correction-plan.md` | 書籍構造の分析と修正計画（旧24章） |
| `analysis/book-structure.json` | 構造分析の生成データ（17,000行） |
| `analysis/build-system-verification-report.md` | 2025年のビルドの動作確認 |
| `analysis/chapter-specific-improvements.md`、`duplicate-errors-analysis.md`、`error-section-restructuring-proposal.md`、`error-sections-analysis.md` | 「よくあるエラーと対処法」の再編の検討（付録Eへの移行で完了） |
| `analysis/complete-section-analysis.md`、`content-analysis-report.md`、`comprehensive-analysis-summary.md`、`structure-analysis-report.md`、`manuscript-heading-structure-report.md` | 旧24章構成の節・見出しの分析 |
| `analysis/content-reduction-candidates.md`、`content-reduction-implementation-plan.md`、`detailed-reduction-rationale.md` | 内容削減の候補と計画（2025-07） |
| `analysis/exercise-system-assessment.md` | 演習の評価（2026-11に演習を作り直したため古い） |
| `analysis/improvement-roadmap.md`、`next-phase-plan.md` | 2025年の作業計画（2026-11-16に閉じた。今後の作業は`TODO.md`） |
| `analysis/issues-tracking-2025.md` | 課題管理の文書から分けた2025年の作業サマリーと計画（アクションプラン・品質指標など）。Issue #001・#002の詳細も含む |
| `analysis/proofreading-summary.md`、`textlint-sentence-ending-analysis.md` | 2025年の校正・textlintの記録 |
| `appendix-g-chapter23-consolidation-plan.md`、`duplicate-content-consolidation-plan.md` | 重複の整理計画（2025-07） |
| `directory-structure-clarification.md`、`github-repository-improvement-plan.md` | 2025年のディレクトリ・リポジトリの計画 |
| `mandatory-workflow.md`、`manuscript-modification-protocol.md` | 旧来の作業手順（いまは`CLAUDE.md`と`docs/guides/`） |
| `planning/book-design-plan.md`、`planning/book_structure_plan.md` | 2025年の体裁・構成の計画 |
| `planning/exercise-review-brief.md` | 演習の発展課題・チャレンジ課題を作り直す手順（2026-11-12に作業が完了） |
| `reference/`の9ファイル | 付録の参照と旧`source/`ディレクトリの対応表・監査（旧構成） |
| `textlint-modification-log.md` | 2025-07のtextlint修正ログ |

## 確認してから判断するもの

いまの作業には使いませんが、別の用途で読み返す可能性があるものです。

| ファイル | 内容と判断の目安 |
|---|---|
| `analysis/comprehensive-project-analysis.md` | 2025-07の包括的な分析。冒頭の現状の要約は`docs/analysis/project-status.md`に移した。本文は記録としてだけ価値がある |
| `analysis/diagram-candidates.md`、`diagrams/diagram-recommendations.md` | 図を加える箇所の候補（2026-11-11に63枚まで加えた）。まだ図にしていない候補を今後使うなら残す |
| `planning/instructor-guide.md`、`planning/learner-guide.md` | 指導者向け・学習者向けのガイド（2025-07、旧章番号）。授業の資料や書籍の補助資料として作り直すなら土台になる |
| `learning-path-guide.md` | 学習パスのガイド（旧章番号）。同上 |
| `github-code-organization-standards.md` | GitHubのコード配置の基準（2025-07）。`sample-code/README.md`と`CLAUDE.md`に後継の規則がある |
