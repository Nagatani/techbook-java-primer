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
- `source/`: Contains source code, exercises, and other materials referenced in the book (read-only)
- `vivliostyle.config.js`: Main configuration defining title, author, entry files, theme, and output settings
- `output/`: Generated PDF files
- Uses `@vivliostyle/theme-techbook` theme for technical book styling

The build process converts Markdown files listed in the `entry` array (currently just `index.md`) into a single PDF using the techbook theme.

**Important:** The `source/` directory contains original lecture materials and must be treated as read-only. All new content and modifications should be done within the `manuscripts/` directory.

## Workflow

1. Check `TODO.md` before starting any task to understand current priorities
2. **MANDATORY**: Read `docs/writing-guidelines-textlint-compliance.md` before writing or editing any manuscript content
3. Write or edit manuscript files in `manuscripts/*.md`
4. Add supplementary materials to `source/` directory if needed
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
- `source/`: Original lecture materials and reference code (read-only)
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

### Critical Project Issues (as of current analysis)

The following issues require ongoing attention and should be referenced during any work:

1. **High Priority**: Appendix B system incomplete (18 sections planned, only 1 exists)
2. ~~**High Priority**: GitHub repository placeholders unresolved~~ ✅ **Resolved** (2025-07-13)
3. **Medium Priority**: Chapter 15 exercise format inconsistency
4. **Medium Priority**: Chapter ordering optimization (Chapter 21 placement)

## Repository Information

**GitHub Repository**: https://github.com/Nagatani/techbook-java-primer

This repository contains:
- Book manuscript files (`manuscripts/` directory)
- Exercise source code and examples (`source/` directory)
- Project documentation and analysis (`docs/` directory)
- Build configuration and theme settings

### Exercise References
All chapter exercise sections reference this repository URL. The structure is:
- Exercise files: `https://github.com/Nagatani/techbook-java-primer/tree/main/exercises`
- Appendix resources: `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix`
- Source code examples: `/source/` directory

## Content Guidelines

- Only use the official Oracle Java website as a reference
- Maintain consistent tone and style suitable for a published book
- Use textlint for Japanese manuscript linting and formatting
- **CRITICAL**: Always refer to `docs/writing-guidelines-textlint-compliance.md` when generating or modifying text to prevent textlint errors
- **文体統一**: 本文はですます調で統一する（である調は使用しない）
- **リスト表記ルール**: 
  - 絵文字を使用しない
  - リスト項目の末尾にコロン（：）を使用しない
  - 文字強調（**太字**）を必要以上に使用しない
  - 文末に句読点（。）を付けない
  - リストに限ってはである調を使用する
  - **重要**: リストを導入する文章の末尾には、文法的に必要な場合コロン（：）を使用する
    - 例：「以下のような利点があります：」「主な機能：」「メリット：」
    - コロンの削除は機械的に行わず、文法的に必要な箇所は残す
- **強調表記（太字）の使用禁止事項**:
  - **禁止**: 強調表記（** **）を見出しとして使用すること
  - 見出しが必要な場合は、適切なMarkdown見出し記法（#、##、### など）を使用する
  - 強調表記は、本文中で真に強調が必要な単語や短いフレーズにのみ限定する
  - 許可される強調表記：
    - コードリスト番号（**リストX-Y**、**サンプルコードX-Y**）のみ
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