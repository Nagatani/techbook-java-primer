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
2. Write or edit manuscript files in `manuscripts/*.md`
3. Add supplementary materials to `source/` directory if needed
4. Run `npm run lint` to check for writing style issues
5. Run `npm run lint:fix` to automatically format manuscript files
6. Run `npm run build` to ensure the book builds successfully
7. Update `TODO.md` after completing tasks

## Important Directory Structure Rules

**Critical**: The `manuscripts/` directory should contain ONLY the actual book content files that will be included in the final publication. 

- **Book content only**: All files in `manuscripts/` must be part of the actual book
- **Auxiliary files**: Any auxiliary files such as planning documents, guidelines, checklists, or notes must be stored in `meta/` directory
- **Never create**: Do not create management files, TODO lists, planning documents, or writing guidelines in `manuscripts/`
- **Before creating**: Always verify that a new file in `manuscripts/` will be added to `vivliostyle.config.js`

Directory structure:
- `manuscripts/`: Book content only (chapters, appendices, glossary, index - files included in vivliostyle.config.js)
- `meta/`: Auxiliary files for book creation (planning documents, guidelines, cross-reference guides, etc.)
- `source/`: Original lecture materials and reference code (read-only)
- `output/`: Generated PDF files

## Content Guidelines

- Only use the official Oracle Java website as a reference
- Maintain consistent tone and style suitable for a published book
- Use textlint for Japanese manuscript linting and formatting
- Do not add emojis unless explicitly requested by the user