# Gemini Code Assistant Project Context

This document provides context for the Gemini Code Assistant to understand the project structure, conventions, and primary goals.

## 1. Project Overview

This project is for writing a Java programming primer technical book, based on existing lecture materials. The main goal is to refine the content from the `source/` directory into a cohesive book format in the `manuscripts/` directory, ensuring a consistent tone and style suitable for a published book. It uses Vivliostyle to build the book from Markdown files and `textlint` for linting the manuscript.

**Important:** The `source/` directory contains the original lecture materials and must be treated as **read-only**. All new content and modifications should be done within the `manuscripts/` directory.

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

1.  **Check `TODO.md`:** Before starting any task, review `TODO.md` to understand the current priorities and outstanding issues.
2.  Write or edit the manuscript in the `manuscripts/*.md` files.
3.  Add any supplementary source code or materials to the `source/` directory.
4.  Run `npm run lint` to check for any writing style issues.
5.  After each task or significant change, run `npm run lint:fix` to automatically format the manuscript files.
6.  Finally, run `npm run build` to ensure the book can be built successfully without errors.
7.  **Update `TODO.md`:** After completing a task, update `TODO.md` to reflect the progress (e.g., check off a completed item, add new findings).


## 6. Content Guidelines

- **References:** Only use the official Oracle Java website as a reference. Do not use any other websites or materials as references.