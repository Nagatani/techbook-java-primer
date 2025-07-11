# 付録参照マッピング表

## 現在の参照と実際のリソースの対応

| 章 | 現在の参照 | 実際のリソース | 修正方針 |
|---|-----------|---------------|----------|
| 第1章 | 付録B.1「言語設計とプラットフォーム」 | なし | 作成または削除 |
| 第1章 | 付録B.17「JVMアーキテクチャとバイトコード」 | `/appendix/jvm-architecture/` | パスに変更 |
| 第2章 | 付録B.01「言語設計とプラットフォーム」 | なし | 作成または削除 |
| 第2章 | 付録B.02「メモリ管理とパフォーマンス」 | なし | 作成または削除 |
| 第3章 | 付録B.03「プログラミングパラダイムの進化」 | `/manuscripts/appendix-b03-programming-paradigms.md` | パスに変更 |
| 第4章 | 付録B.05「ソフトウェア設計原則」 | `/appendix/software-design-principles/` | パスに変更 |
| 第5章 | 付録B.14「仮想メソッドテーブル」 | `/appendix/virtual-method-table/` | パスに変更 |
| 第6章 | 付録B.18「不変性の設計パターン」 | `/appendix/immutability-patterns/` | パスに変更 |
| 第8章 | 付録B.10「ハッシュテーブル」 | `/appendix/collection-internals/` | パスに変更 |
| 第9章 | `/appendix/type-erasure-patterns/` | ✓ 既に修正済み | - |
| 第10章 | `/appendix/stream-api-internals/` | ✓ 既に修正済み | - |
| 第11章 | `/appendix/programming-paradigms/` | 作成予定 | 現状維持 |
| 第11章 | `/manuscripts/appendix-b03-programming-paradigms.md` | ✓ 存在する | - |
| 第13章 | 付録B.20「Enumsを使った高度な設計パターン」 | `/appendix/enum-patterns/` | パスに変更 |
| 第14章 | 付録B.21「例外処理のパフォーマンス」 | `/appendix/exception-performance/` | パスに変更 |
| 第15章 | `/appendix/nio2-advanced/` | ✓ 既に修正済み | - |
| 第16章 | 付録B.09「Java Memory Model」 | `/appendix/java-memory-model/` | パスに変更 |
| 第22章 | 付録B.22「オープンソースエコシステム」 | なし | 削除 |
| 第23章 | 付録B.16「CI/CDパイプライン」 | なし | 削除 |

## 修正優先度

### 高優先度（実際のリソースが存在する）
1. 第1章: JVMアーキテクチャ
2. 第3章: プログラミングパラダイム
3. 第4章: ソフトウェア設計原則
4. 第5章: 仮想メソッドテーブル
5. 第6章: 不変性パターン
6. 第8章: コレクション内部実装
7. 第13章: Enumパターン
8. 第14章: 例外処理パフォーマンス
9. 第16章: Javaメモリモデル

### 低優先度（リソースが存在しない）
- 第1章: 言語設計とプラットフォーム
- 第2章: メモリ管理とパフォーマンス
- 第22章: オープンソースエコシステム
- 第23章: CI/CDパイプライン

これらは参照を削除するか、簡単な説明に置き換える。