# Bold Text Usage Analysis

## Summary Statistics

- **Total bold text instances**: 1,453 (excluding code listing labels)
- **Unique bold texts**: 1,077
- **Files with bold text**: 34 files

## Usage Patterns by Category

### 1. Technical Terms (811 instances, 55.8%)
Short technical terms and keywords emphasized for importance:
- Programming concepts: `**ラムダ式**`, `**ジェネリクス**`, `**Record**`
- Java keywords: `**final**`, `**enum**`, `**static**`
- Design patterns: `**ビルダーパターン**`, `**ストラテジーパターン**`

### 2. General Emphasis (498 instances, 34.3%)
Longer phrases emphasized for importance:
- Conceptual explanations: `**同じ型の変数や同じメソッド呼び出しが、実行時のオブジェクトの種類によって異なる振る舞いをする**`
- Key principles: `**問題解決能力**`, `**継続的な学習能力**`
- Important notes: `**クラス名とファイル名は完全に一致させる必要があります**`

### 3. Error Handling Guide (66 instances, 4.5%)
Consistent error documentation format:
- `**エラーメッセージ**` (45 occurrences)
- `**原因と対処**` (21 occurrences)

### 4. Terms with Explanatory Parentheses (63 instances, 4.3%)
Technical terms with Japanese or English explanations:
- `**データ指向プログラミング（Data-Oriented Programming, DOP）**`
- `**代数的データ型（Algebraic Data Types）**`
- `**改善後（複雑度: 3）**`

### 5. Exercise and Implementation Guides (15 instances, 1.0%)
Structured learning elements:
- `**目的**` (7 occurrences)
- `**実装のポイント**` (7 occurrences)
- `**実装のヒント**` (1 occurrence)

## Files with Highest Bold Text Usage

1. **glossary.md** (318 instances)
   - Primary use: Technical term definitions
   - Pattern: Each glossary entry title is bolded

2. **chapter16-multithreading.md** (141 instances)
   - Heavy use of error/troubleshooting format
   - Pattern: `**エラー症状**`, `**原因**`, `**対処法**`

3. **chapter19-gui-event-handling.md** (139 instances)
   - Similar error/troubleshooting pattern
   - Many technical GUI terms emphasized

4. **chapter01-introduction.md** (80 instances)
   - Mix of conceptual emphasis and error handling
   - Key principles and philosophy statements

5. **chapter09-records.md** (61 instances)
   - Heavy emphasis on new Java features
   - Pattern matching and record concepts

## Specific Usage Patterns

### Error Documentation Format
Consistent three-part structure:
```
**エラーメッセージ**
[actual error message]

**原因**
[explanation]

**対処法**
[solution]
```

### Section Headers (Not Markdown Headers)
Used for informal subsections:
- `**コードの構造解説**`
- `**実装手順**`
- `**使い分けの指針**`

### Important Notes and Warnings
- `**注意:**`
- `**重要:**`
- `**ポイント**`

### Listing References
While code listing labels (`**リストX-Y**`, `**サンプルコードX-Y**`) were excluded from analysis, they represent a significant pattern for cross-referencing code examples.

## Recommendations

1. **Consistency**: The usage is generally consistent, especially in error documentation format
2. **Overuse in Some Areas**: Some chapters (especially multithreading and GUI) may have excessive bold text that could reduce emphasis effectiveness
3. **Good Practices Observed**:
   - Consistent error documentation format
   - Clear technical term emphasis
   - Logical grouping of related concepts

4. **Areas for Potential Improvement**:
   - Some files have very high bold text density which might reduce readability
   - Consider whether all technical terms need bold emphasis on every occurrence