# 削除の候補

今後の校閲作業では使わない資料です（2026-11-17に整理）。著者が確認してから削除します。
作業の手順や現状の説明としては使いません（旧24章構成の章番号や、いまは使わない手順を含みます）。

2026-11-18に、内容がすべて片付いているか、いまの構成と合わない2025年の分析・計画・手順書（44ファイル）を削除しました。
削除したファイルはgitの履歴に残っています（`git log --diff-filter=D --name-only -- docs/deletion-candidates`）。

ここにある資料は、スクリプトやビルドから参照されていません。
`TODO.md`と`docs/analysis/issues-resolved.md`の完了済みの記録に、ここの資料へのリンクが残っています（削除するとリンクが切れますが、作業には影響しません）。

## 確認してから判断するもの

いまの作業には使いませんが、別の用途で読み返す可能性があるものです。

| ファイル | 内容と判断の目安 |
|---|---|
| `analysis/comprehensive-project-analysis.md` | 2025-07の包括的な分析。冒頭の現状の要約は`docs/analysis/project-status.md`に移した。本文は記録としてだけ価値がある |
| `analysis/diagram-candidates.md`、`diagrams/diagram-recommendations.md` | 図を加える箇所の候補（2026-11-11に63枚まで加えた）。まだ図にしていない候補を今後使うなら残す |
| `planning/instructor-guide.md`、`planning/learner-guide.md` | 指導者向け・学習者向けのガイド（2025-07、旧章番号）。授業の資料や書籍の補助資料として作り直すなら土台になる |
| `learning-path-guide.md` | 学習パスのガイド（旧章番号）。同上 |
| `github-code-organization-standards.md` | GitHubのコード配置の基準（2025-07）。`sample-code/README.md`と`CLAUDE.md`に後継の規則がある |
