# Gemini Code Assistant Project Context

This document provides context for the Gemini Code Assistant to understand the project structure, conventions, and primary goals.

## 1. Project Overview

This project is for writing a Java programming primer technical book. It uses Vivliostyle to build a book from Markdown files and `textlint` for linting the manuscript.

## 2. Key Technologies

- **[Vivliostyle](https://vivliostyle.org/):** Used for generating the book from Markdown source files. The configuration can be found in `vivliostyle.config.js`.
- **[textlint](https://textlint.github.io/):** Used for linting and formatting the Japanese manuscript. The configuration is in `.textlintrc.json` and `prh-rules.yml`.
- **[Markdown](https://daringfireball.net/projects/markdown/):** The format used for writing the manuscript.
- **[npm](https://www.npmjs.com/):** Used for managing dependencies and running scripts.

## 3. Directory Structure

- `manuscripts/`: Contains the main manuscript files in Markdown format. This is the entry point for Vivliostyle.
- `source/`: Contains source code, exercises, and other materials referenced in the book.
- `output/`: The directory where the generated book (e.g., PDF) will be placed. This directory is not tracked by git.
- `node_modules/`: Contains the npm packages for the project. This directory is not tracked by git.

## 4. Common Commands

The following commands are defined in `package.json` and are frequently used:

- **`npm run build`**: Builds the book using Vivliostyle.
- **`npm run preview`**: Starts a live preview server for the book.
- **`npm run lint`**: Lints all manuscript files in the `manuscripts/` directory.
- **`npm run lint:fix`**: Automatically fixes linting errors in the manuscript files.

## 5. Workflow

1.  Write or edit the manuscript in the `manuscripts/*.md` files.
2.  Add any supplementary source code or materials to the `source/` directory.
3.  Run `npm run lint` to check for any writing style issues.
4.  Run `npm run preview` to see the changes live.
5.  Run `npm run build` to generate the final book output in the `output/` directory.
