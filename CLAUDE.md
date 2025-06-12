# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Japanese technical book project titled "Java入門" (Java Introduction) by Hidehiro Nagatani. The project uses Vivliostyle, a CSS-based typesetting system, to generate PDF publications from Markdown source files.

## Commands

### Building
- `npm run build`: Build the PDF from Markdown sources
- `npm run preview`: Launch preview server for development

### Configuration
- Book configuration is in `vivliostyle.config.js`
- Markdown sources are in `manuscripts/` directory
- PDF output goes to `output/techbook-java-primer.pdf`

## Architecture

The project follows Vivliostyle's standard structure:
- `manuscripts/`: Contains Markdown source files for book content
- `vivliostyle.config.js`: Main configuration defining title, author, entry files, theme, and output settings
- `output/`: Generated PDF files
- Uses `@vivliostyle/theme-techbook` theme for technical book styling

The build process converts Markdown files listed in the `entry` array (currently just `index.md`) into a single PDF using the techbook theme.