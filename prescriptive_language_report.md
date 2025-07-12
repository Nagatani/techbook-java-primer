# Prescriptive Language Analysis Report

## Executive Summary

This analysis examined prescriptive language patterns across all 23 manuscript chapters to identify areas where the writing tone could be softened. The most common prescriptive patterns found were:

1. **必要です/必要な/必要があります** (necessary/need to) - 282 occurrences
2. **重要です/重要な** (important) - 241 occurrences  
3. **必須** (required/mandatory) - 62 occurrences

## Key Findings

### Chapters with Highest Prescriptive Density

The following chapters have the most prescriptive tone relative to their word count (prescriptive expressions per 1000 words):

1. **Chapter 04** - Classes and Instances: 28.6 per 1000 words
2. **Chapter 14** - Exception Handling: 28.4 per 1000 words
3. **Chapter 06** - Immutability and Final: 27.3 per 1000 words
4. **Chapter 02** - Getting Started: 24.6 per 1000 words
5. **Chapter 03** - OOP Basics: 17.8 per 1000 words

### Chapters with Most Total Prescriptive Expressions

1. **Chapter 03** - OOP Basics: 110 total expressions
2. **Chapter 02** - Getting Started: 76 total expressions
3. **Chapter 16** - Multithreading: 40 total expressions
4. **Chapter 04** - Classes and Instances: 35 total expressions
5. **Chapter 10** - Collections: 35 total expressions

### Pattern Usage Analysis

#### Most Overused Patterns

1. **必要です/必要な/必要があります** (282 occurrences)
   - Found in all 22 chapters
   - Often used in prerequisite sections
   - Frequently appears in learning objectives

2. **重要です/重要な** (241 occurrences)
   - Found in all 22 chapters
   - Commonly used to emphasize concepts
   - Often redundant or could be implied

3. **必須** (62 occurrences)
   - Found in all 22 chapters
   - Strongly prescriptive
   - Often used for prerequisites

#### Less Common but Still Present

- **べき/べきだ/べきです** (should/must) - 31 occurrences in 12 chapters
- **ことが重要** (it's important that) - 13 occurrences in 8 chapters
- **なければなりません/なければならない** (must) - 8 occurrences in 4 chapters

## Recommendations for Tone Softening

### Priority Chapters for Revision

Based on both density and total count, prioritize these chapters:

1. **Chapter 02** - Getting Started (high density + high total)
2. **Chapter 03** - OOP Basics (highest total count)
3. **Chapter 04** - Classes and Instances (highest density)
4. **Chapter 06** - Immutability and Final (high density)
5. **Chapter 14** - Exception Handling (high density)

### Systematic Replacement Strategies

#### For "必要です/必要な"
- Replace with: "役立ちます", "便利です", "〜ことができます"
- Example: "理解する必要があります" → "理解しておくと役立ちます"

#### For "重要です/重要な"
- Often can be removed entirely
- Replace with: "ポイントは", "注目すべき点は", "〜の特徴は"
- Example: "これは重要な概念です" → "これが〜の特徴です"

#### For "必須"
- Replace with: "前提として", "基本的に", "通常は"
- Example: "必須の知識です" → "基本的な知識として"

#### For "べき/べきだ"
- Replace with: "〜するとよいでしょう", "〜することができます", "〜という方法があります"
- Example: "実装すべきです" → "実装するとよいでしょう"

### Common Prescriptive Phrases to Target

Based on the analysis, these specific phrases appear frequently and should be systematically replaced:

1. "理解する必要があります" → "理解しておくと役立ちます"
2. "重要な前提知識" → "前提となる知識"
3. "必須前提" → "前提として"
4. "注意が必要です" → "注意点として"
5. "考える必要があります" → "考えてみましょう"

## Implementation Strategy

1. **Batch Processing**: Use the MultiEdit tool to systematically replace common patterns in each priority chapter
2. **Context Review**: After bulk replacements, review context to ensure meaning is preserved
3. **Style Guide**: Create a style guide in the `meta/` directory to maintain consistency
4. **Progress Tracking**: Update this report after each chapter revision

## Next Steps

1. Start with Chapter 02 and Chapter 03 as they have the highest impact
2. Create replacement templates for common patterns
3. Review prerequisite sections across all chapters (they tend to be highly prescriptive)
4. Consider restructuring learning objectives to be more inviting rather than demanding