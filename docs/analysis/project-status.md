# プロジェクトの現状

最終更新：2026-11-19

旧`comprehensive-project-analysis.md`の冒頭にあった現状の要約を、この文書に移しました（2026-11-17）。
旧文書の本文（2025-07時点の旧24章構成の分析）は2026-11-18に削除しました（gitの履歴に残っています）。
作業の状態は`TODO.md`、課題は`docs/analysis/issues-tracking.md`で管理します。

## 構成

- 本文は全26章（2026-11-16に旧付録Eを第24章「データベースプログラミング」、旧付録Dを第25・26章「統合プロジェクト」として本文に移した）。章タイトルの正は`manuscripts/chapterXX-*.md`の1行目、順序の正は`vivliostyle.config.js`。章ごとの節構成は`docs/summaries/`、全体の見取り図は`docs/summaries/overview.md`
- 付録はA〜Hの8つ（A 開発環境の構築、B 技術的な詳細解説（Deep Dive）、C ソフトウェア工学の理論的基盤、D 高度なビルドとデプロイメント技術、E Javaのよくあるエラーと対処法、F Swingの発展的な機能、G 外部ライブラリの使用例、H 並行処理とネットワークの発展的なパターン）。2026-11-16に再編し、java.timeは第6章に編入した。中身は`appendix/`にあり、付録Bの節は`appendix/b01`〜`b15`のディレクトリ番号を正とする
- 演習は`exercises/chapter01`〜`chapter26`（章番号と一致）。統合プロジェクトの初版のコードは`sample-code/integrated-project/`。本文の章末には演習へのリンクだけを置く
- サンプルコードは「サンプルコードX-Y」の番号で、本文には抜粋を載せ、全文を`sample-code/`に置く。対象はJava 25で、標準入出力は`IO`クラス。各章の「よくあるエラーと対処法」の詳細は付録E（`appendix/e-common-errors/`）
- 図はMermaidの元データ`docs/diagrams/*.mmd`（67枚）から作るSVG

## 検査

- `npm run check:structure`・`check:compliance`・`check-claims.js --static`・`check:api-names`（pre-commitフック。主張とAPI名はコミットする節・章・原稿だけ）
- `check:deep-dive`：付録Bの15節の主張610件（機械で確認506件、手で確認104件）をJDK 25で検証する（2026-11-19〜。節ごとの集計は`docs/analysis/deep-dive-claims.md`、しくみは`docs/guides/claim-verification.md`）
- `check:claims`：本文の主張を同じしくみで検証する。全章の版の主張と第15章の全体の計約220件（`docs/claims/`、集計は`docs/analysis/manuscript-claims.md`）。ほかの章への広げ方はIssue #034
- `check:api-names`：本文のインラインコードに書いたJDKのAPI名（約1,100件）がJDK 25に実在し、削除予定でないか
- `check:samples`・`check:java`・`check:public-code`・`check:diagrams`・`index:check`・`lint`
