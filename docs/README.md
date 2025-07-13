# ドキュメント管理ディレクトリ

このディレクトリには、書籍プロジェクトの基本情報と管理資料が格納されています。

## プロジェクト概要

**書籍名**: 大学2年生のためのプログラミング入門オブジェクト指向言語Java
**著者**: Hidehiro Nagatani
**対象読者**: Java初学者から中級者まで
**前提知識**: 基本的なプログラミング概念（C言語程度）

## 最新の構成情報（2024年12月）

### 章構成
- **全24章** + 付録4件
- **総ページ数（推定）**: 600-700ページ
- **Java対応バージョン**: Java 8 - Java 17

### 主要な特徴
- **現代的なJava機能の包括的カバー**: var型推論、Text Blocks、Switch式、Optional、java.time API
- **実践的なプロジェクト例**: データベースプログラミング、GUI開発、統合プロジェクト
- **段階的学習設計**: 基礎から応用まで自然な流れ

## ディレクトリ構成

### `/planning/`
書籍の計画・設計に関するドキュメント
- `book_structure_plan.md` - 最新の24章構成と学習フロー
- `book-design-plan.md` - 書籍デザイン計画
- `instructor-guide.md` - 指導者向けガイド
- `learner-guide.md` - 学習者向けガイド

### `/guidelines/`
執筆・編集時のガイドライン
- `code-styling-guide.md` - コードスタイリングガイド
- `column-guidelines.md` - コラム執筆ガイドライン
- `bullet-point-usage-guide.md` - 書式ガイドライン

### `/reference/`
技術的な分析資料と参考情報
- `source-content-inventory.md` - 全73ファイルの内容分析
- `book-content-mapping.md` - 章と内容の対応関係
- `technical-deep-dive-analysis.md` - 技術的詳細分析
- `appendix-reference-*.md` - 付録システム関連の分析・ガイドライン
- `source-analysis-framework.md` - コンテンツ分析フレームワーク

## プロジェクト状態

### 完成状況
**✅ 執筆完了** - 2024年12月時点で全章の執筆が完了
**✅ 構造最適化完了** - 章番号統一、参照修正、現代的機能追加
**✅ 内容充実化完了** - データベース章追加、GUI章拡充、統合プロジェクト追加

### ビルド設定
- **設定ファイル**: `vivliostyle.config.js`
- **テーマ**: `custom-theme.css`
- **出力先**: `output/techbook-java-primer.pdf`

## 使用方法

### ビルド実行
```bash
npm run build        # PDF生成
npm run preview      # プレビュー表示
npm run lint         # 校正チェック
npm run lint:fix     # 自動修正
```

### 開発作業
```bash
# 開発用コマンド
npm run dev          # 開発サーバー起動
```

## 主要改善履歴（2024年12月）

### 構造的改善
- **章番号の統一**: セクション番号とサンプルコード番号を章番号に整合
- **章間参照の修正**: 正しい章番号への参照に統一
- **演習セクションの配置統一**: 全章で章末に配置

### 内容の現代化
- **現代Java機能の追加**: var型推論、Text Blocks、Switch式
- **重要概念の強化**: Optional型、java.time API、実践的例外処理
- **データベース章の新規追加**: JDBC、SQL、DAO、トランザクション

### 実践性の向上
- **GUI章の大幅拡充**: JTable、JTree等の高度なコンポーネント実装
- **統合プロジェクトの追加**: 複数章の技術を組み合わせた実践例
- **サンプルコードの充実**: 業界標準レベルの実装例

## 注意事項

- このディレクトリの内容は書籍の最終成果物には含まれません
- 実際の書籍内容は `manuscripts/` ディレクトリに格納されています
- 管理ファイルは定期的に整理され、最新の状況を反映しています

---

**最終更新**: 2024年12月
**プロジェクト管理**: Claude Codeを使用した自動化された改善プロセス