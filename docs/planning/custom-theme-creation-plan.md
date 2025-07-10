# Vivliostyle 独自テーマ作成計画

## 📋 プロジェクト概要

現在の`@vivliostyle/theme-techbook`が期待通りのデザインにならないため、完全に制御可能な独自テーマを作成します。既存テーマをベースに、よりカスタマイズ性の高いテーマシステムを構築します。



## 🎯 実装アプローチ

### アプローチ1: 既存テーマの複製・改造（推奨）
**メリット**: 
- 安定したベースから開始
- 既存の組版知識を活用
- 段階的なカスタマイズが可能

**実装方法**:
1. `@vivliostyle/theme-techbook`のソースを取得
2. プロジェクト内に独自テーマとして配置
3. 必要な部分のみを段階的に改造

### アプローチ2: create-vivliostyle-themeから新規作成
**メリット**:
- 最新のVivliostyle Themesシステム活用
- クリーンな実装
- 公式ツールサポート

**実装方法**:
1. `npm create vivliostyle-theme`でテーマ雛形作成
2. 技術書向けの機能を一から実装
3. 既存スタイルを移植

### アプローチ3: 完全オリジナル作成
**メリット**:
- 完全な制御
- 不要なコードなし
- 軽量化

**実装方法**:
1. 基本的なVivliostyle CSS設定から開始
2. 段階的に機能を追加
3. 独自の設計思想で実装



## 🛠️ 推奨実装方法（アプローチ1詳細）

### Step 1: 既存テーマの解析と取得

#### 1.1 theme-techbookの内容確認
```bash
# 現在使用中のテーマの場所確認
npm list @vivliostyle/theme-techbook

# テーマファイルの内容確認
cat node_modules/@vivliostyle/theme-techbook/theme.css
cat node_modules/@vivliostyle/theme-techbook/package.json
```

#### 1.2 テーマファイルの複製
```bash
# プロジェクト内にテーマディレクトリ作成
mkdir -p themes/java-techbook-theme
cp -r node_modules/@vivliostyle/theme-techbook/* themes/java-techbook-theme/
```

### Step 2: 独自テーマの基本構造

#### 2.1 ディレクトリ構造
```
themes/java-techbook-theme/
├── theme.css              # メインテーマファイル
├── package.json           # テーマ情報
├── README.md              # テーマ説明
├── example/               # サンプル・テスト用
│   ├── example.md
│   └── vivliostyle.config.js
└── scss/                  # 開発用（オプション）
    ├── _variables.scss
    ├── _base.scss
    ├── _typography.scss
    ├── _layout.scss
    └── theme.scss
```

#### 2.2 package.json設定
```json
{
  "name": "java-techbook-theme",
  "version": "1.0.0",
  "description": "Custom theme for Java programming book",
  "author": "Hidehiro Nagatani",
  "license": "MIT",
  "keywords": ["vivliostyle", "theme", "techbook", "java"],
  "files": ["*.css", "*.md"],
  "vivliostyle": {
    "theme": {
      "name": "Java Techbook Theme",
      "author": "Hidehiro Nagatani",
      "category": "techbook",
      "tags": ["technical", "programming", "java"]
    }
  }
}
```

### Step 3: 段階的カスタマイズ戦略

#### 3.1 Phase 1: 基本設定の置き換え
- CSS変数システムの再定義
- カラーパレットの変更
- フォント設定の最適化

#### 3.2 Phase 2: レイアウトシステムの改良
- ページマージンの調整
- 見出し体系の再設計
- グリッドシステムの実装

#### 3.3 Phase 3: 特殊要素の実装
- コードブロックの完全制御
- 特殊セクション（Deep Dive等）
- 章扉システムの改良

#### 3.4 Phase 4: 印刷最適化
- ページ分割の精密制御
- カラーマネジメント
- フォント代替設定



## 💻 具体的実装計画

### テーマファイル構成

#### themes/java-techbook-theme/theme.css
```css
/**
 * Java Techbook Custom Theme
 * Based on @vivliostyle/theme-techbook with extensive customization
 */

/* ========================================
   基本設定とリセット
   ======================================== */
@charset "UTF-8";

/* カスタムプロパティ（CSS変数）定義 */
:root {
  /* カラーシステム */
  --theme-color-primary: #007acc;
  --theme-color-secondary: #005499;
  --theme-color-accent: #ff6b35;
  
  /* フォントシステム */
  --theme-font-family: 'Noto Sans JP', sans-serif;
  --theme-font-family-code: 'Source Code Pro', monospace;
  
  /* レイアウト */
  --theme-page-width: 182mm;
  --theme-page-height: 257mm;
  --theme-page-margin: 20mm;
  
  /* タイポグラフィ */
  --theme-font-size-base: 14px;
  --theme-line-height-base: 1.7;
}

/* ページ設定 */
@page {
  size: var(--theme-page-width) var(--theme-page-height);
  margin: var(--theme-page-margin);
}

/* 既存テーマの上書き・拡張 */
@import url('./base.css');
@import url('./typography.css');
@import url('./layout.css');
@import url('./components.css');
```

### vivliostyle.config.js の更新
```javascript
module.exports = {
  title: 'Java入門',
  author: 'Hidehiro Nagatani',
  language: 'ja',
  // カスタムテーマを指定
  theme: './themes/java-techbook-theme',
  // または相対パス指定
  style: ['./themes/java-techbook-theme/theme.css'],
  // 既存設定
  entry: [/* ... */],
  entryContext: './manuscripts',
  output: './output/techbook-java-primer.pdf',
};
```



## 🔧 開発・テスト環境

### 1. 開発用スクリプト
```json
// package.json に追加
{
  "scripts": {
    "theme:dev": "cd themes/java-techbook-theme/example && npx vivliostyle preview",
    "theme:build": "cd themes/java-techbook-theme/example && npx vivliostyle build",
    "theme:watch": "nodemon --watch themes/ --ext css,scss -x 'npm run theme:build'"
  }
}
```

### 2. テーマ専用テストファイル
```markdown
<!-- themes/java-techbook-theme/example/example.md -->
# テーマテスト

## 見出しテスト
### h3見出し
#### h4見出し

## コードブロックテスト
```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

## 特殊セクションテスト
> **Deep Dive**: テスト用Deep Diveセクション
```

### 3. Sass/SCSS開発環境（オプション）
```bash
# Sass環境構築
npm install -D sass nodemon

# 開発用監視
npm run sass:watch
```



## 📋 実装チェックリスト

### Phase 1: 基盤構築 ✅
- [ ] 既存テーマの解析と複製
- [ ] 独自テーマディレクトリ構築
- [ ] package.json設定
- [ ] 基本CSS変数システム

### Phase 2: コア機能実装
- [ ] ページ設定の最適化
- [ ] タイポグラフィシステム
- [ ] 見出し階層の再設計
- [ ] 基本レイアウトシステム

### Phase 3: 特殊機能実装
- [ ] コードブロックシステム
- [ ] 章扉デザイン
- [ ] 特殊セクション（Deep Dive等）
- [ ] 表紙システム

### Phase 4: 最適化・テスト
- [ ] 印刷設定の最適化
- [ ] ブラウザ互換性確認
- [ ] パフォーマンス最適化
- [ ] ドキュメント整備



## 🎯 期待される効果

### 1. 完全な制御権
- 全ての見た目要素を自由にカスタマイズ
- 既存テーマの制約からの解放
- 将来的な拡張性の確保

### 2. メンテナンス性向上
- 問題発生時の迅速な対応
- 独自機能の追加・修正
- バージョン管理の完全制御

### 3. 差別化とブランディング
- 他の技術書との差別化
- 独自のビジュアルアイデンティティ
- シリーズ展開時の一貫性

### 4. 学習・教育効果
- Vivliostyle CSS組版の深い理解
- 再利用可能なテーマライブラリ
- コミュニティへの貢献



## 🚀 今後の展開

### 短期目標（1-2週間）
1. 既存テーマの完全複製
2. 基本的なカスタマイズの実装
3. テスト環境の構築

### 中期目標（1ヶ月）
1. 全機能の独自実装
2. 品質・パフォーマンス最適化
3. ドキュメント・サンプル整備

### 長期目標（3ヶ月）
1. オープンソース化
2. 他プロジェクトでの再利用
3. Vivliostyleコミュニティへの貢献

この計画により、完全にカスタマイズ可能な独自テーマシステムを構築し、理想的な書籍デザインを実現できます。