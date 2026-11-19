# 書籍プロジェクト作業用スクリプト

いずれもリポジトリのルートで実行します。npmスクリプトがあるものは、そちらを使ってください。

## 検査

| スクリプト | npmスクリプト | 役割 |
|---|---|---|
| `check-structure.js` | `check:structure`、`crossref` | 章・演習・付録B・用語集・相互参照ガイドの食い違いを検査する。`crossref`で相互参照ガイドを生成する |
| `check-claude-md-compliance.js` | `check:compliance` | CLAUDE.mdの表記ルール（リストのコロン、太字の見出し的使用など）への違反を検出する |
| `check-java-samples.js` | `check:java`、`check:java:run` | 全文を公開していない番号付きサンプルをjavacでコンパイルし、実行結果を照合する |
| `check-public-code.js` | `check:public-code` | `appendix/`と`exercises/`のコードをJava 25でコンパイルし、テストをJUnitで実行する |
| `check-claims.js` | `check:deep-dive`、`check:claims` | 付録B（Deep Dive）と本文の主張を、台帳（付録Bは節の`claims.yml`、本文は`docs/claims/chapterXX.yml`）に書いた方法（`javap`、JVMの実行とログ、フラグの既定値）でJDK 25で確かめる。`--static`は台帳と印の対応とフラグ名だけ（pre-commit）。手順は`docs/guides/claim-verification.md` |
| `sample-code.js` | `check:samples`、`samples:sync`、`samples:extract` | `sample-code/`の全文と本文の抜粋を対応づける（検査、本文への反映、番号の振り直し） |
| `build-book-index.js` | `index`、`index:check` | 節単位の索引（`docs/book-index/`）を作り、要約（`docs/summaries/`）の鮮度を検査する |
| `git-hooks/pre-commit` | なし | 共有のpre-commitフック。`git config core.hooksPath scripts/git-hooks`で有効にする |

## ビルド

| スクリプト | npmスクリプト | 役割 |
|---|---|---|
| `convert-mermaid-to-images.js` | `mermaid:convert`（`build`から呼ばれる） | `docs/diagrams/*.mmd`を`manuscripts/images/diagrams/`のSVGに変換する。元ファイルか設定が変わったときだけ変換する |
| `diagram-size.js` | `check:diagrams`（CI） | 図を紙面に載せる大きさ（いちばん小さい文字が10Qになる倍率、本文の幅と高さ150mmが上限）を決める。直接実行すると、元データ・画像・原稿の対応、図ごとの文字の大きさ（9Q未満は注意）、線と文字の重なりなどを検査する |
| `diagram-metrics.js` | `mermaid:convert`から呼ばれる | 図をブラウザで開き、いちばん小さい文字の大きさ、線が文字の上を通る箇所、文字が箱からはみ出す箇所、ラベルどうしの重なりを測って`docs/diagrams/.diagram-metrics.json`に書く |
| `vfm-section-label.js` | `build`（`vivliostyle.config.js`の`documentProcessor`） | 章タイトルの`<b>5章</b>`を読み、章の中のh2に`data-chapter`属性を付ける（テーマが「5章 - 3」の形で番号を出す）。下の`vfm-*.js`をまとめて組み込む |
| `vfm-code-lines.js` | `build`（`documentProcessor`） | コードブロックを1行ずつの`span.code-line`に分け、折り返した続きの行を字下げ＋4文字の位置から始め、縞模様を行ごとに付ける |
| `vfm-index-entries.js` | `build`（`documentProcessor`） | 索引・用語集（`manuscripts/glossary.md`）の「- **用語** - 第N章」を用語と参照先に分ける |
| `vfm-links.js` | `build`（`documentProcessor`） | 本の中へのリンク`xxx.md#...`を`.html`に直し、本文にインラインコードで書いたURLをリンク（`a.code-url`）にする |
| `vfm-figure-size.js` | `build`（`documentProcessor`） | 図の`<img>`に`diagram-size.js`で決めた幅と高さを書き込む |
| `vfm-line-breaks.js` | `build`（`documentProcessor`） | 両端揃えで字間が大きく開かないようにする。和文に接する原稿の改行（HTMLではスペースになる）を取り除き、インラインコード・URL・本文に書いた長い名前に折り返し位置（`<wbr>`）を入れる |

## 修正の補助

自動で書き換えるため、実行後は`git diff`で全変更箇所を確かめてください。

| スクリプト | npmスクリプト | 役割 |
|---|---|---|
| `fix-claude-md-violations.js` | `fix:compliance` | `check:compliance`の違反のうち、機械的に直せるもの（リスト導入文のコロンなど）を直す。対象ファイルの横に`backup/`を作る。正規表現による書き換えなので、原稿の校正には使わない（`docs/guides/proofreading-workflow.md`） |
| `fix-title-colon-lists.js` | なし | 「- 項目： 説明」形式のリストを階層化リストに変換する（`fix:compliance`の対象外のもの） |

## 削除したスクリプト（2026-11-09）

2025-07〜08に1回だけ使った変換・分析スクリプトと、`tools/`の分割校正ツールは削除しました。
内容は`git log -- scripts tools`で確認できます。

- 番号付け・番号の重複修正（`add_code_listing_numbers.py`ほか）は`sample-code.js number`と`check:samples`に置き換えた
- 構造の分析（`analyze_*.py`）は`build-book-index.js`と`check-structure.js`に置き換えた
- 文体・長文・コロンの一括変換（`fix_dearu_style.py`、`fix_long_sentences.py`、`tools/convert_colon_patterns*.py`ほか）は、文言の欠落（Issue #013）の原因になったため再利用しない。校正は`docs/guides/proofreading-workflow.md`の手順で行う
