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

## Project Current Status (as of 2025-07-22)

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
   - 現在887個のコードブロック中、422個に番号付与済み
   - 形式: **サンプルコードX−Y** で統一
   - 番号付けは継続的に実施中

5. **Chapter Structure**:
   - 全23章構成
   - 第21章（ユニットテスト）の配置は現状維持
   - 各章末にGitHubリポジトリへの演習リンクを配置

## Commands

### Building
- `npm run build`: Build the PDF from Markdown sources
- `npm run preview`: Launch preview server for development

### Linting
- `npm run lint`: Lint all manuscript files in the `manuscripts/` directory
- `npm run lint:fix`: Automatically fix linting errors in the manuscript files

### Configuration
- Book configuration is in `vivliostyle.config.js`
- Markdown sources are in `manuscripts/` directory
- PDF output goes to `output/techbook-java-primer.pdf`
- textlint configuration is in `.textlintrc.json` and `prh-rules.yml`

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
3. Write or edit manuscript files in `manuscripts/*.md`
4. Sample code extracted from the book is maintained in `sample-code/` directory
5. Run `npm run lint` to check for writing style issues
6. Run `npm run lint:fix` to automatically format manuscript files
7. Run `npm run build` to ensure the book builds successfully
8. Update `TODO.md` after completing tasks

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

### Critical Project Issues (as of 2025-07-23)

The following issues require ongoing attention and should be referenced during any work:

1. ~~**High Priority**: Appendix B system incomplete~~ ✅ **Resolved** - GitHubリンク形式で実装完了 (2025-07-19)
2. ~~**High Priority**: GitHub repository placeholders unresolved~~ ✅ **Resolved** (2025-07-13)
3. ~~**High Priority**: Code listing numbering inconsistency~~ ✅ **Resolved** - 1,221個を「サンプルコードX-Y」形式に統一 (2025-07-23)
4. ~~**Medium Priority**: Chapter 15 exercise format inconsistency~~ ✅ **Resolved** - 全章GitHubリンク形式で統一
5. ~~**Medium Priority**: Chapter ordering optimization (Chapter 21 placement)~~ ✅ **Resolved** - 現状配置を維持する方針決定
6. **Medium Priority**: textlint errors remaining (37件) - 複雑な手動修正が必要

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