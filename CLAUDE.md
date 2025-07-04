# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Japanese technical book project titled "Java入門" (Java Introduction) by Hidehiro Nagatani. The project uses Vivliostyle, a CSS-based typesetting system, to generate PDF publications from Markdown source files.

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

## Content Guidelines

- Only use the official Oracle Java website as a reference
- Maintain consistent tone and style suitable for a published book
- Use textlint for Japanese manuscript linting and formatting