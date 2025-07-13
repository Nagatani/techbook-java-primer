# Java入門書 書籍体裁整備計画

## 📋 プロジェクト概要

Vivliostyle CSS組版を活用し、技術書として適切な体裁とデザインを整備します。参考にするのはCSSkumihan_samplesとVivliostyle公式サンプルです。



## 🎯 デザイン要件

### 1. 章見出しデザイン
- **目標**: ページ半分を使う装飾的な章扉
- **参考**: 技術評論社の技術書スタイル
- **実装要素**:
  - 大きなタイポグラフィ
  - グラデーション背景
  - 章番号の装飾
  - アイコンまたは図形要素

### 2. 表紙デザイン
- **目標**: 一般的な技術書レベルの表紙
- **要素**:
  - タイトル、サブタイトル
  - 著者名
  - 出版社風のロゴ/マーク
  - Java言語らしいビジュアル要素

### 3. コードブロック装飾
- **目標**: 可読性の高いコード表示
- **要素**:
  - 行ごとの背景色変更（ストライプ）
  - シンタックスハイライト対応
  - 行番号表示
  - コピー可能な見た目

### 4. 小見出し装飾
- **目標**: 階層が分かりやすい見出し体系
- **要素**:
  - h2-h6の統一されたデザイン
  - アイコンやマーカーの活用
  - 余白の最適化



## 🏗️ 技術実装アプローチ

### フォルダ構成
```
/techbook-java-primer/
├── manuscripts/           # マークダウンファイル
├── assets/               # 画像・アイコン類
│   ├── icons/
│   ├── backgrounds/
│   └── cover/
├── styles/               # カスタムCSS
│   ├── book-theme.css    # メインテーマ
│   ├── chapter-headings.css
│   ├── code-blocks.css
│   ├── cover.css
│   └── typography.css
└── vivliostyle.config.js # 設定ファイル
```

### CSS設計方針

#### 1. 章見出し（Chapter Headings）
```css
/* 章扉デザイン */
h1 {
  break-before: page;
  break-after: page;
  page: chapter-title;
  height: 50vh;
  background: linear-gradient(135deg, #007acc, #005499);
  color: white;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
}

.chapter-number {
  font-size: 3rem;
  font-weight: 300;
  margin-bottom: 1rem;
}

.chapter-title {
  font-size: 2.5rem;
  font-weight: 700;
  line-height: 1.2;
}
```

#### 2. コードブロック装飾
```css
/* ストライプ背景のコードブロック */
pre {
  background: linear-gradient(
    to bottom,
    #f8f9fa 0%,
    #f8f9fa 50%,
    #e9ecef 50%,
    #e9ecef 100%
  );
  background-size: 100% 2em;
  border-left: 4px solid #007acc;
  padding: 1rem;
  overflow-x: auto;
}

code {
  font-family: 'Source Code Pro', 'Consolas', monospace;
  font-size: 0.9rem;
  line-height: 1.5;
}
```

#### 3. 小見出し装飾
```css
/* 階層的な見出しデザイン */
h2 {
  border-left: 5px solid #007acc;
  padding-left: 1rem;
  margin: 2rem 0 1rem 0;
  font-size: 1.5rem;
  color: #333;
}

h3 {
  position: relative;
  padding-left: 1.5rem;
  margin: 1.5rem 0 0.5rem 0;
  font-size: 1.25rem;
  color: #555;
}

h3::before {
  content: "●";
  color: #007acc;
  position: absolute;
  left: 0;
}
```



## 📋 実装フェーズ

### Phase 1: 基盤整備（1-2日）
1. **カスタムCSS環境構築**
   - styles/ディレクトリ作成
   - vivliostyle.config.js更新
   - 基本的なCSS変数定義

2. **タイポグラフィ基盤**
   - フォント選定（Noto Sans JP + Source Code Pro）
   - 基本的な文字サイズ・行高設定
   - 見出し階層の定義

### Phase 2: 章見出しデザイン（2-3日）
1. **章扉レイアウト**
   - ページ分割設定
   - 背景デザイン実装
   - タイポグラフィ調整

2. **章番号システム**
   - CSS countersによる自動採番
   - 装飾的な章番号表示
   - Deep Diveセクションとの連携

### Phase 3: コンテンツ装飾（2-3日）
1. **コードブロック**
   - ストライプ背景実装
   - シンタックスハイライト対応
   - 行番号表示システム

2. **小見出し体系**
   - h2-h6の統一デザイン
   - アイコン・マーカー実装
   - 余白・マージン最適化

### Phase 4: 表紙・仕上げ（1-2日）
1. **表紙デザイン**
   - cover.mdのCSS実装
   - タイトル・著者名レイアウト
   - 技術書らしいビジュアル作成

2. **全体調整**
   - ページマージン・余白調整
   - 印刷最適化
   - PDFエクスポート確認



## 🎨 デザインコンセプト

### カラーパレット
- **プライマリ**: #007acc (Java Blue)
- **セカンダリ**: #005499 (Dark Blue)
- **アクセント**: #ff6b35 (Orange)
- **グレー**: #6c757d, #adb5bd, #e9ecef
- **コード**: #f8f9fa (Light Gray), #343a40 (Dark Gray)

### フォント戦略
- **メイン**: Noto Sans JP（日本語最適化）
- **見出し**: Noto Sans JP Bold
- **コード**: Source Code Pro（プログラミング用）
- **英数字**: Inter（モダンで読みやすい）

### レイアウト原則
1. **可読性重視**: 十分な行間・字間
2. **階層明確化**: 見出しレベルの視覚的区別
3. **技術書らしさ**: プロフェッショナルで信頼感のあるデザイン
4. **Javaアイデンティティ**: 適度にJava言語らしさを表現



## 📊 品質基準

### デザイン品質
- [ ] 市販技術書と同レベルの見た目
- [ ] 階層構造が明確に視認可能
- [ ] コードの可読性が高い
- [ ] 印刷時の美しさを確保

### 技術品質
- [ ] Vivliostyle CLI でのビルド成功
- [ ] PDF出力での文字化けなし
- [ ] ページ分割の適切性
- [ ] 相互参照の動作確認

### ユーザビリティ
- [ ] 目的の情報が素早く見つけられる
- [ ] 疲れにくい読書体験
- [ ] コードのコピー&ペーストしやすさ
- [ ] Deep Diveセクションの判別容易性



## 🚀 期待される効果

### 読者体験の向上
1. **プロフェッショナル感**: 市販技術書並みの品質
2. **可読性**: ストレスのない読書体験
3. **学習効率**: 構造化された情報提示
4. **愛着形成**: 美しいデザインによる所有欲の満足

### 教育的効果
1. **集中力維持**: 疲れにくいレイアウト
2. **理解促進**: 視覚的ヒエラルキーによる構造理解
3. **実践支援**: コピーしやすいコード例
4. **復習効率**: 一目で分かる章構成

この計画により、技術的内容の充実と視覚的魅力を両立した、真にプロフェッショナルなJava入門書が完成します。