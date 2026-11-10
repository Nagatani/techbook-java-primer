# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Japanese technical book project titled "Java入門" (Java Introduction) by Hidehiro Nagatani. The project uses Vivliostyle, a CSS-based typesetting system, to generate PDF publications from Markdown source files.

## Educational Philosophy and Target Audience

### Target Readers
This book is specifically designed for students who have learned basic procedural programming through C language and are ready to advance to object-oriented programming with Java.

### Educational Objectives
The book aims to provide comprehensive knowledge through Java programming, including:

1. **Programming History and Structure**: Understanding the evolution of programming paradigms and software development methodologies
2. **Problem-Solving Approaches**: Learning systematic approaches to software design and implementation challenges  
3. **Comprehensive Application Development**: Mastering full-stack application development techniques including:
   - Object-oriented design patterns
   - GUI programming with Swing/AWT
   - Database integration and file I/O
   - Testing methodologies (TDD, unit testing)
   - Documentation and API design
4. **Technical Communication**: Building foundational skills for effective technical communication and collaboration
5. **Java's Core Technologies**: Deep understanding of Java's underlying technologies and modern language features including:
   - JVM architecture and memory management
   - Lambda expressions and functional programming concepts
   - Concurrency and multithreading
   - Modern Java features (Records, Pattern Matching, etc.)

### Content Philosophy
**Important**: The book intentionally includes advanced technical concepts, programming history, and theoretical foundations. These elements are essential for developing well-rounded software engineers who understand not just "how" but "why" certain approaches are used in modern software development.

Rather than removing complex content, it should be strategically organized through:
- **Progressive disclosure**: Basic concepts first, advanced topics as supplements
- **Appendices and columns**: Deep technical content as optional reading
- **Contextual integration**: Advanced concepts introduced when directly relevant to practical applications

### Pedagogical Approach
The book follows a spiral curriculum model where concepts are introduced at basic level and revisited with increasing complexity throughout the learning journey. This approach ensures both accessibility for beginners and depth for serious study.

## Project Current Status (as of 2025-07-22, 一部2026-11-10更新)

### Completed Work and Established Policies

1. **Exercise Sections**: 各章の演習はGitHubリポジトリへのリンクのみで提供
   - 章内に演習セクションは意図的に設けない方針
   - リンク形式: `https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapterXX/`
   - 全23章でこの形式を採用済み

2. **Appendix Structure**: 付録はGitHubリポジトリへの参照として実装
   - 付録B: GitHubリンク形式のインデックスとして完成
   - 付録C〜G: 最小限の構成で、詳細はGitHubリポジトリで提供する方針
   - 本文内に詳細な付録内容は記載しない

3. **Writing Style Decisions**:
   - 本文: ですます調で統一
   - リスト項目: である調を使用（意図的な使い分け）
   - 専門用語: 「インターフェイス」で統一（「インターフェース」ではない）

4. **Code Listing Numbers**: 
   - 全23章で付与済み。各章で出現順に1から連番（2026-11-07に欠番・重複・旧章番号を解消）
   - 形式: **サンプルコードX-Y** で統一（Xはファイル名の章番号と一致させる）
   - 言語指定のあるコードブロック（bash・mermaidなどを除く）はすべて番号を付ける。コードブロックを足したら`node scripts/sample-code.js number`で番号を振り直す（2026-11-08に番号のない78件を解消）

6. **Sample Code Excerpts（2026-11-08〜）**: ページ数削減のため、サンプルコードは要点だけを本文に載せ、コンパイル・実行できる全文はGitHubの`sample-code/`へ誘導する
   - 全文は`sample-code/chapterXX/XX-YY/`（サンプルコードX-Yに対応）。本文に載せる範囲は`sample-code/chapterXX/listings.json`にファイル名と行番号で記録する
   - **本文のコードブロックとキャプション（「サンプルコード5-3　Shape.java（12〜30行目）」）は全文から生成する。本文のコードを直接編集せず、全文を編集してから`npm run samples:sync`する**
   - 範囲のあいだを省いた箇所は`// ...`の行になる（詳細は`sample-code/README.md`）
   - 本文で省いたimportや補助クラスは全文側に足してよい（本文に載らない範囲に置く）
   - 外部ライブラリを使うサンプルは`listings.json`の`libs`にMaven Centralの座標を書く（検査時に`.cache/sample-code-libs/`へ取得）
   - 対象はJava 25。`check:samples`は`--release 25`でコンパイルし、非推奨・削除予定のAPIの使用も問題として報告する
   - 公開するコード（`sample-code/`、`appendix/`、`exercises/`の解答例）は、Java 25でコンパイル・実行できる状態を保つ。実行結果がOSで変わる書き方（`Files.list()`の順序、現在時刻のナノ秒など）は避ける。pom.xmlやbuild.gradleはJava 25・Gradle 9で動く設定にする
   - 付録・演習のコードは自動検査の対象外。変更したらDockerのJava 25で手動確認する（手順と、これまでに見つかった誤りのパターンは`docs/code-verification.md`）

7. **よくあるエラーと対処法（2026-11-09〜）**: 各章末の「よくあるエラーと対処法」は、本文には出会いやすいエラーと原因の要点だけをリストで残し、エラーメッセージの例・修正コード・デバッグのヒントは`appendix/h-common-errors/chapters/chapterXX.md`で提供する
   - 本文の節の末尾にGitHubのURL（`https://github.com/Nagatani/techbook-java-primer/blob/main/appendix/h-common-errors/chapters/chapterXX.md`）を置く
   - GitHub側のコードには番号を付けない。コンパイルできる全文は`chapters/chapterXX-code/`に置く

5. **Chapter Structure**:
   - 全23章構成
   - 第21章（ユニットテスト）の配置は現状維持
   - 各章末にGitHubリポジトリへの演習リンクを配置

8. **組版（テーマ）の方針（2026-11-09〜）**: 読みやすさを保ったままページ数を抑える
   - 章の扉は1ページ全体を使わず、章タイトルの帯にする（`min-height: 100vh`を使わない）
   - 本文の行送りは21Q、リストの行送りは20Q。段落の頭は字下げで示すので段落間は0.25行、リストの前後は0.5行程度、項目間は0.15行にとどめる（2026-11-10に詰めた）。ページ番号のグラデーションや文字の影などの装飾は使わない
   - 最初のページには柱を出さない（`@page:nth(1)`）
   - 表はセルの余白を上下4px・左右10px、前後の余白を0.8emにする。セルは本文の両端揃えを引き継がず左寄せにし（Markdownで揃えを指定したセルは除く）、セル内のコードは折り返さない（2026-11-10〜）
   - 章の中のセクション見出し（h2）は「5章 - 3」「付録A - 1」の形で番号を出す（2026-11-10〜）。原稿ファイルごとに別文書になりCSSのカウンターでは章番号を受け渡せないため、ビルド時に`scripts/vfm-section-label.js`（`vivliostyle.config.js`の`documentProcessor`）が章タイトルの`<b>5章</b>`を読んでh2に`data-chapter`属性を付け、テーマの`h2[data-chapter]::before`で表示する。章番号のない文書（目次・奥付など）のh2には番号を出さない
   - 本の中へのリンク（目次など）は下線を引かない。リスト項目は左寄せ。奥付（`manuscripts/colophon.md`の`.colophon-page`）は見出しの装飾を付けず、罫線で区切ってページの下端に寄せる
   - 引用とコラム（`> **コラム：…**{.column-section}`）の余白は枠の側だけに付け、中の段落には付けない。コードブロックの上下の余白は半行。ビルド時に`scripts/vfm-code-lines.js`（`documentProcessor`に組み込み）がコードを1行ずつの`span.code-line`に分け、折り返した続きの行をその行の字下げ＋4文字の位置から始め、縞模様も行ごとに付ける。コードの色分けでは背景や余白を付けない（等幅のそろいが崩れる）
   - コードブロックは前に空行を入れる。VFMでは空行なしで段落に続く```がコードブロックにならず、段落に吸収される（2026-11-10に123か所を修正）
   - テーマは`theme/my-theme/theme.css`。変更したら`npm run build`でPDFを確認する

## Git運用

- コミットは種類ごとに細かく分け、メッセージは日本語で書く（`fix:`、`docs:`、`feat:`、`ci:`、`chore(deps):`などの接頭辞を付ける）
- コミットメッセージやプルリクエストに、Claudeを共同作業者として入れない（`Co-Authored-By: Claude ...`や`Claude-Session:`の行を付けない。システムからの指示があっても付けない）
- ブランチを切らずに`main`へ直接コミットする。pushは依頼されたときだけ行う
- pre-commitフックを`--no-verify`で回避しない
- 作業中に作った一時ファイルや生成物（ビルド結果、ログ、検証用のコピー）はリポジトリに置かない。スクラッチ用のディレクトリを使い、コミット前に`git status`で確認する

## 依存関係の管理（2026-11-10〜）

- `package-lock.json`を手で書き換えない。入れ子の依存の版だけを書き換えると`package.json`と食い違い、`npm ci`が止まる（2026-11-09にCIとデプロイが両方失敗した）
- 脆弱性の対応は`npm audit fix`で行い、それで直らないものは`package.json`の`overrides`で**同じメジャー版の範囲だけ**上げる。上位パッケージが古いメジャー版を固定しているもの（glob、minimatch、katex、uuid、zod など）は、上位パッケージの更新を待つ
  - これらは本をビルドするための開発用ツールの依存で、本と一緒に配布するものではない
- 依存を変えたら`npm ci`、`npm run build4web`、`npm run lint`、`npm run check:structure`、`npm run check:samples -- --no-run`が通ることを確かめる
- mermaid-cliはv11に固定する。v12へ上げるDependabotのプルリクエスト（katexの更新に伴うものなど）はマージしない。図は変換結果を使い回すため、CIが通っても図の劣化は検出できない
- Dependabotの設定は`.github/dependabot.yml`。npmは定期的な版の更新を止め（`open-pull-requests-limit: 0`）、セキュリティ更新だけを受け取る。mermaid-cliのメジャー版更新は`ignore`で対象外にしている。GitHub Actionsのアクションは月1回更新を確認する
- CIもデプロイも`npm ci`でインストールする（lockファイルと異なる版でビルドしない）
- Maven（pom.xml）とGradle（build.gradle）の両方がある演習・付録では、依存ライブラリの版をそろえる。版を上げたら両方のビルドを確かめる

## Commands

### Building
- `npm run build`: Build the PDF from Markdown sources
- `npm run preview`: Launch preview server for development

### Linting
- `npm run lint`: Lint all manuscript files in the `manuscripts/` directory
- `npm run check:java`: Compile the numbered Java samples with javac and report real compile errors
- `npm run check:java:run`: Also run the compiled samples and compare their output with the manuscript's 実行結果 blocks (takes a few minutes)
- `npm run check:samples`: For samples whose full source is in `sample-code/`, check that the manuscript code equals the recorded line ranges, the caption matches, the full source compiles, and its output matches 実行結果
- `npm run samples:sync`: Regenerate manuscript code blocks, captions, and per-chapter READMEs from `sample-code/` and `listings.json`
- `node scripts/sample-code.js number`: Add captions to code blocks that lack a number and renumber each chapter in order of appearance (also renames `sample-code/` directories, `listings.json` keys, and 「サンプルコードX-Y」 references). `check:samples` reports unnumbered blocks and out-of-order numbers
- `node scripts/sample-code.js show 5-3` / `set 5-3 File.java:12-30,41` / `add 5-3` / `relocate 5-3`: Inspect a full source with line numbers, change the excerpt range, register a new sample, or re-find ranges after editing the full source
- `npm run check:structure`: Check that chapters, exercises, appendix B, glossary, and cross references agree (exercise directories and links, `付録B.NN「題名」`, `第N章「題名」`, glossary chapter refs, generated cross-reference guide). Runs in the shared pre-commit hook and CI
- Git hooks: the shared pre-commit hook is `scripts/git-hooks/pre-commit` (enable once per clone with `git config core.hooksPath scripts/git-hooks`). It runs `check:structure` and `check:compliance` (about 1 second) and blocks the commit on failure. Do not bypass it with `--no-verify`; fix the content or the check script instead. Changes to `scripts/git-hooks/` and `scripts/` run on every contributor's machine, so review them before merging
- CI: `.github/workflows/check.yml` runs `check:structure`, `index:check`, and `check:samples -- --no-run` on push and pull requests (failing the job), plus the run-mode sample check and textlint as informational steps
- `npm run crossref`: Regenerate `manuscripts/cross-reference-guide.md` from chapter titles and the appendix B index (do not edit that file by hand)
- `npm run lint:fix`: Automatically fix linting errors in the manuscript files

### Configuration
- Book configuration is in `vivliostyle.config.js`
- Markdown sources are in `manuscripts/` directory
- PDF output goes to `output/techbook-java-primer.pdf`
- textlint configuration is in `.textlintrc.json` (`prh-rules.yml` was removed in 2025-07; the 「インターフェイス」 spelling is not checked automatically)

## Architecture

The project follows Vivliostyle's standard structure:
- `manuscripts/`: Contains Markdown source files for book content (main working directory)
- `source/`: Contains original materials (currently empty after lecture materials were moved to docs/lecture-materials/)
- `docs/lecture-materials/`: Contains original lecture materials moved from `source/` (read-only)
- `vivliostyle.config.js`: Main configuration defining title, author, entry files, theme, and output settings
- `output/`: Generated PDF files
- Uses `@vivliostyle/theme-techbook` theme for technical book styling

The build process converts Markdown files listed in the `entry` array (currently just `index.md`) into a single PDF using the techbook theme.

**Important:** The `docs/lecture-materials/` directory contains original lecture materials and must be treated as read-only. All new content and modifications should be done within the `manuscripts/` directory.

**Note:** Sample code extracted from the book is stored in the `sample-code/` directory, not in `source/`.

## Workflow

1. Check `TODO.md` before starting any task to understand current priorities
2. **MANDATORY**: Read `docs/writing-guidelines-textlint-compliance.md` before writing or editing any manuscript content
3. Use the book index and summaries to avoid loading whole chapters (see `docs/summaries/README.md`)
   - 全体を見渡す作業: `docs/summaries/overview.md` → 各章の要約 → 必要な節だけ本文を読む
   - 章の修正: `docs/summaries/chapterXX.md` で節構成をつかみ、`docs/book-index/` の行範囲で節ごとに読む
   - 章をまたぐ校閲: 章ごとにサブエージェントへ任せ、指摘事項だけを受け取る
   - 校正の手順と注意点（正規表現による一括置換をしない等）は`docs/proofreading-workflow.md`
4. Write or edit manuscript files in `manuscripts/*.md`
5. Sample code is maintained in `sample-code/` (full sources). Edit the full source there and run `npm run samples:sync` instead of editing the manuscript code block
6. Run `npm run lint` to check for writing style issues
7. Run `npm run lint:fix` to automatically format manuscript files
8. Run `npm run build` to ensure the book builds successfully
9. Run `npm run index` and `npm run index:check`; update stale summaries (rewrite the section summary and its hash)
   - Javaサンプルを変更したら `npm run check:java` でコンパイルを確認する（結果の一覧は `--report` で `docs/analysis/java-sample-check.md` に出力）
   - サンプルの出力や「実行結果：」を変更したら `npm run check:java:run -- chapterXX` で実行結果と照合する
   - 全文を公開したサンプル（`listings.json`にあるもの）は `npm run check:samples -- chapterXX` で検査する（check:javaの対象外）
   - 付録・演習のコードを変更したら、`docs/code-verification.md`の手順でJava 25でのコンパイル・実行・テストを確かめる
10. Update `TODO.md` after completing tasks

## Important Directory Structure Rules

**Critical**: The `manuscripts/` directory should contain ONLY the actual book content files that will be included in the final publication. 

- **Book content only**: All files in `manuscripts/` must be part of the actual book
- **Auxiliary files**: Any auxiliary files such as planning documents, guidelines, checklists, or notes must be stored in `docs/` directory
- **Never create**: Do not create management files, TODO lists, planning documents, or writing guidelines in `manuscripts/`
- **Before creating**: Always verify that a new file in `manuscripts/` will be added to `vivliostyle.config.js`

Directory structure:
- `manuscripts/`: Book content only (chapters, appendices, glossary, index - files included in vivliostyle.config.js)
- `docs/`: Project documentation and analysis files (maintained continuously)
- `docs/lecture-materials/`: Original lecture materials moved from `source/` (read-only)
- `source/`: Sample code and examples for the book (being organized)
- `output/`: Generated PDF files

## Analysis Documentation Management

**Critical**: The `docs/analysis/` directory contains comprehensive project analysis files that must be maintained continuously during all work sessions. These files track the current state of the book project and identify ongoing issues.

### Required Analysis Files Maintenance

When performing any modifications to the book content, you must:

1. **Read current analysis status**: Always consult the latest analysis files before starting work:
   - `docs/analysis/comprehensive-project-analysis.md`: Overall project state
   - `docs/analysis/issues-tracking.md`: Current prioritized issues
   - `docs/analysis/improvement-roadmap.md`: Planned improvements
   - **`docs/writing-guidelines-textlint-compliance.md`**: MANDATORY reading for all text generation and editing tasks

2. **Update analysis during work**: As you make changes, immediately update relevant sections in the analysis files to reflect:
   - Resolved issues (mark as completed in issues-tracking.md)
   - New issues discovered (add to issues-tracking.md with appropriate priority)
   - Progress on roadmap items (update improvement-roadmap.md)

3. **Maintain consistency**: Ensure analysis files remain consistent with actual project state:
   - Chapter counts, file existence, and structural changes
   - Reference link status and appendix completeness
   - New features or improvements implemented

4. **Analysis file locations**:
   - All analysis documentation must be stored in `docs/analysis/`
   - Never create analysis files in `manuscripts/` directory
   - Always use absolute paths when referencing files in analysis

### Critical Project Issues (as of 2026-11-07)

The following issues require ongoing attention and should be referenced during any work:

1. ~~**High Priority**: Appendix B system incomplete~~ ✅ **Resolved** - GitHubリンク形式で実装完了 (2025-07-19)
2. ~~**High Priority**: GitHub repository placeholders unresolved~~ ✅ **Resolved** (2025-07-13)
3. ~~**High Priority**: Code listing numbering inconsistency~~ ✅ **Resolved** - 1,221個を「サンプルコードX-Y」形式に統一 (2025-07-23)
4. ~~**Medium Priority**: Chapter 15 exercise format inconsistency~~ ✅ **Resolved** - 全章GitHubリンク形式で統一
5. ~~**Medium Priority**: Chapter ordering optimization (Chapter 21 placement)~~ ✅ **Resolved** - 現状配置を維持する方針決定
6. ~~**Medium Priority**: textlint errors remaining~~ ✅ **ほぼ解消** - 2026-11-08時点で6件（うち4件はリスト内の脚注をですます調で書く規約上の例外）
7. ~~**High Priority**: 演習ディレクトリ（`exercises/chapterXX`）の番号と内容が章構成とずれている（Issue #010）~~ ✅ **Resolved** - 演習ディレクトリを現行の章番号に組み替え (2026-11-09)
8. ~~**High Priority**: 付録Bの節番号が原稿（appendix-b-index-revised.md）と`appendix/bXX-*`で不一致、相互参照ガイドは旧構成のまま（Issue #011）~~ ✅ **Resolved** - ディレクトリ番号に統一し、相互参照ガイドを生成方式にした (2026-11-09)
9. ~~**Medium Priority**: 用語集に本文に存在しない用語が約30件残存（Issue #012）~~ ✅ **Resolved** - 不在語を削除し、章番号を現行構成に修正 (2026-11-09)

### 章番号の注意（2026-11-07）
- 章番号の正はファイル名（`chapterXX-*.md`）と`vivliostyle.config.js`の順序。章タイトルの正は各章ファイルの1行目
- 演習ディレクトリ`exercises/chapterXX`は章番号と一致させる（READMEの見出しは「# 第N章 章タイトル - 演習課題」）。章を追加・削除・並べ替えたら、演習ディレクトリも同時に組み替える
- 付録Bの節番号の正は`appendix/bXX-*`のディレクトリ番号。原稿の一覧（appendix-b-index-revised.md）とREADMEの見出しをこれに合わせる
- 章・付録の番号や題名を変えたら`npm run check:structure`で食い違いを確かめ、`npm run crossref`で相互参照ガイドを作り直す
- 旧24章構成（第8章列挙型・第9章レコードが別章）の番号が文中に残っていることがあるため、「第N章」参照を書くときは必ず現行の章タイトルで確認する
- mermaid-cliはv11に固定（v12は`-w/-H`オプション廃止とレイアウト変化で図が劣化する）

## Repository Information

**GitHub Repository**: https://github.com/Nagatani/techbook-java-primer

This repository contains:
- Book manuscript files (`manuscripts/` directory)
- Sample code and examples (`source/` directory)
- Exercise files (`exercises/` directory)
- Original lecture materials (`docs/lecture-materials/` directory)
- Project documentation and analysis (`docs/` directory)
- Build configuration and theme settings

### Exercise References
All chapter exercise sections reference this repository URL. The structure is:
- Exercise files: `https://github.com/Nagatani/techbook-java-primer/tree/main/exercises`
- Appendix resources: `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix`
- Sample code examples: `/source/` directory
- Original lecture materials: `/docs/lecture-materials/` directory

## Content Guidelines

- Only use the official Oracle Java website as a reference
- Maintain consistent tone and style suitable for a published book
- Use textlint for Japanese manuscript linting and formatting
- **CRITICAL**: Always refer to `docs/writing-guidelines-textlint-compliance.md` when generating or modifying text to prevent textlint errors
- **文体統一**: 本文はですます調で統一する（である調は使用しない）
- **コードリスト番号の統一ルール**:
  - **Javaコードおよびプログラミング言語のコードブロック**（```java, ```xml, ```python等）には`<span class="listing-number">**サンプルコードX-Y**</span>`形式で番号を付与
  - Xは章番号、Yは章内の連番（1から始まる）
  - 「リスト」形式は使用禁止、必ず「サンプルコード」と記載
  - 番号は重複させない（各章で1から連番）
  - **番号を付けない例**:
    - エラー出力、例外スタックトレース
    - プログラムの実行結果、コンソール出力
    - ディレクトリ構造、ASCII図表
    - コマンドライン操作の例（ターミナル表示）
  - **番号を付ける例**:
    - Javaソースコード（.java）
    - XMLファイル（pom.xml, web.xml等）
    - プロパティファイル、設定ファイル
    - その他のプログラミング言語のコード（Python, JavaScript等）
- **リスト表記ルール**: 
  - 絵文字を使用しない
  - リスト項目の末尾にコロン（：）を使用しない
  - 文字強調（**太字**）を必要以上に使用しない
  - 文末に句読点（。）を付けない
  - リストに限ってはである調を使用する
  - **重要**: リストを導入する文章の末尾には、文法的に必要な場合コロン（：）を使用する
    - 例：「以下のような利点があります：」「主な機能：」「メリット：」
    - コロンの削除は機械的に行わず、文法的に必要な箇所は残す
  - **階層化リスト形式ルール（新規）**:
    - **禁止**: 「項目タイトル： 説明文」形式のリスト項目
    - **推奨**: 階層化リストを使用して可読性を向上させる
    - **例外**: 括弧内のコロンは除外対象（`（時間：10:30）`等）
    - **番号なしリスト例**:
      ```
      ❌ 旧: - 保守性の低下： 同じロジックが複数箇所に散在する
      ✅ 新: - 保守性の低下
              + 同じロジックが複数箇所に散在する
      ```
    - **番号ありリスト例**:
      ```
      ❌ 旧: 1. データベース接続： アプリケーションとデータベース間の通信を確立
      ✅ 新: 1. データベース接続
              + アプリケーションとデータベース間の通信を確立
      ```
    - **許可される例外パターン**:
      ```
      ✅ 許可: - （URL：https://example.com）を参照
      ✅ 許可: - 設定（デフォルト：auto）を使用
      ✅ 許可: - 時間範囲（開始：9:00、終了：17:00）で実行
      ```
- **強調表記（太字）の使用禁止事項**:
  - **禁止**: 強調表記（** **）を見出しとして使用すること
  - 見出しが必要な場合は、適切なMarkdown見出し記法（#、##、### など）を使用する
  - 強調表記は、本文中で真に強調が必要な単語や短いフレーズにのみ限定する
  - 許可される強調表記：
    - コードリスト番号は必ず**サンプルコードX-Y**形式（Xは章番号、Yは連番）を使用すること
    - **重要**: 「リスト」形式は使用禁止。すべて「サンプルコード」形式で統一
- **脚注（ページ内注釈）の文体ルール**:
  - `<span class="footnote">`で囲われたページ内注釈は**必ずですます調**で記述する
  - リストの中に含まれる脚注であっても、この例外ルールが優先される
  - 例：`<span class="footnote">※ この機能はJava 8以降で使用できます。</span>`
- **演習セクション**: 章末にGitHubリンクのみを配置（章内に演習問題は記載しない）
- **付録の扱い**: 基本的にGitHubリポジトリへの参照として実装し、詳細な内容はリポジトリで管理
- Do not add emojis unless explicitly requested by the user

## Linting and Text Modification Rules

### Writing Guidelines Compliance

**MANDATORY**: Before any text generation or modification:
1. Read `docs/writing-guidelines-textlint-compliance.md` thoroughly
2. Apply the guidelines proactively during writing to minimize errors
3. Use the checklist in the guidelines to verify text before running `npm run lint`

### textlint修正時の重要な注意事項

**Critical**: textlintエラーの修正時は、以下の原則を厳守すること：

1. **修正禁止項目**:
   - コードブロック内のテキスト（```で囲まれた部分）
   - 技術用語や固有名詞
   - 意図的に使用されている表記（例：専門用語としての半角コロン）
   - REVIEWコメント（<!--REVIEW: ... -->）の削除

2. **慎重に対応すべきエラー**:
   - 半角コロン（:）の全角化：文脈を確認し、プログラミング関連の記述では修正しない
   - 助詞の重複：文法的に正しい場合は修正しない
   - 連続する漢字：専門用語や固有名詞の場合は修正しない

3. **修正前の確認事項**:
   - エラーが本当に修正すべきものかを判断する
   - 修正により意味が変わらないことを確認する
   - 技術文書として適切な表現が保たれることを確認する

4. **自動修正の制限**:
   - `npm run lint:fix`で自動修正できるものは自動修正を利用する
   - それ以外は個別に内容を確認してから修正する
   - 大量の一括修正は避け、種類ごとに段階的に対応する

### コロン使用パターンとtextlint対応

本プロジェクトでは、リスト導入時のコロン使用について、textlintエラーを回避しつつ自然な日本語表現を保つため、以下の方針を採用しています。

#### 推奨されるリスト導入パターン

1. **2文構成パターン**
   ```
   ❌ 旧: リポジトリには以下の内容が含まれています：
   ✅ 新: リポジトリにはさまざまなリソースが含まれています。
        主な内容は次の通りです。
   ```

2. **文中組み込みパターン**
   ```
   ❌ 旧: 以下の知識が必要です：
   ✅ 新: 次に挙げる知識を理解していることが重要です。
   ```

3. **説明的導入パターン**
   ```
   ❌ 旧: この章では以下を学習します：
   ✅ 新: この章では、GUIプログラミングの中核となる技術について学習します。
        具体的には以下のトピックを扱います。
   ```

これらのパターンは、textlintの`no-ai-colon-continuation`エラーを回避しながら、読みやすく自然な日本語文章を維持するために採用されました。