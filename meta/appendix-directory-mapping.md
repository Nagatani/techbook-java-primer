# 付録ディレクトリ名マッピング計画

## 現在の構造 → 新しい番号体系

| 現在のディレクトリ名 | 新しいディレクトリ名 | 付録番号 | 参照章 |
|-------------------|------------------|---------|--------|
| jvm-architecture | b01-jvm-architecture | B.1 | 第1章 |
| software-design-principles | b04-software-design-principles | B.4 | 第4章 |
| virtual-method-table | b05-virtual-method-table | B.5 | 第5章 |
| immutability-patterns | b06-immutability-patterns | B.6 | 第6章 |
| collection-internals | b08-collection-internals | B.8 | 第8章 |
| type-erasure-patterns | b09-type-erasure-patterns | B.9 | 第9章 |
| stream-api-internals | b10-stream-api-internals | B.10 | 第10章 |
| interface-evolution | b11-interface-evolution | B.11 | 第11章 |
| records-dop | b12-records-dop | B.12 | 第12章 |
| enum-patterns | b13-enum-patterns | B.13 | 第13章 |
| exception-performance | b14-exception-performance | B.14 | 第14章 |
| nio2-advanced | b15-nio2-advanced | B.15 | 第15章 |
| java-memory-model | b16-java-memory-model | B.16 | 第16章 |
| testing-strategies | b21-testing-strategies | B.21 | 第21章 |

## 実装手順

1. ディレクトリ名の変更（mvコマンド）
2. appendix-b-index-revised.mdの更新
3. 各章の参照パス更新

## 注意事項

- GitHubでのリンク切れを防ぐため、旧ディレクトリ名から新ディレクトリ名へのシンボリックリンクを作成することも検討
- ディレクトリ名変更はアトミックに行い、参照更新も同時に実施