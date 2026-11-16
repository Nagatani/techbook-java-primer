const markdownFiles = [
  'table-of-contents.md',
  'chapter01-introduction.md',
  'chapter02-getting-started.md',
  'chapter03-oop-basics.md',
  'chapter04-classes-and-instances.md',
  'chapter05-inheritance-and-polymorphism.md',
  'chapter06-immutability-and-final.md',
  'chapter07-abstract-classes-and-interfaces.md',
  'chapter08-special-classes.md',
  'chapter09-collections.md',
  'chapter10-generics.md',
  'chapter11-lambda-and-functional-interfaces.md',
  'chapter12-advanced-collections.md',
  'chapter13-exception-handling.md',
  'chapter14-file-io.md',
  'chapter15-multithreading.md',
  'chapter16-network-programming.md',
  'chapter17-gui-basics.md',
  'chapter18-event-basics.md',
  'chapter19-event-advanced.md',
  'chapter20-advanced-gui.md',
  'chapter21-unit-testing.md',
  'chapter22-documentation-and-libraries.md',
  'chapter23-build-and-deploy.md',
  'chapter24-database-programming.md',
  'chapter25-integrated-project-design.md',
  'chapter26-integrated-project-technology.md',
  // 付録
  'appendix-index.md', // 付録一覧
  'appendix-a-environment-setup.md', // 付録A: 開発環境の構築
  'appendix-b-index-revised.md', // 付録B: 技術的な詳細解説（Deep Dive）
  'appendix-c-theoretical-foundations.md', // 付録C: ソフトウェア工学の理論的基盤
  'appendix-d-build-and-deploy.md', // 付録D: 高度なビルドとデプロイメント技術
  'appendix-e-common-errors.md', // 付録E: Javaのよくあるエラーと対処法
  'appendix-f-swing-advanced.md', // 付録F: Swingの発展的な機能
  'appendix-g-library-examples.md', // 付録G: 外部ライブラリの使用例
  'appendix-h-advanced-patterns.md', // 付録H: 並行処理とネットワークの発展的なパターン
  'cross-reference-guide.md',
  'glossary.md',
  'colophon.md',
];

// 章の中のセクション見出し（h2）に章番号を付ける（表示はテーマのh2::before）
const { documentProcessor } = require('./scripts/vfm-section-label.js');

module.exports = {
  title: '大学2年生のための実践的プログラミング入門 オブジェクト指向言語 Java',
  author: 'Hidehiro Nagatani',
  language: 'ja',
  theme: './theme/my-theme',
  size: 'B5',
  entry: [
    { 
      path: 'cover.md', 
      title: '表紙',
      theme: './theme/my-theme/cover-theme.css'
    },
    ...markdownFiles.map(file => {
      // 用語集に専用CSSを適用
      if (file === 'glossary.md') {
        return {
          path: file,
          theme: './theme/my-theme/glossary-theme.css'
        };
      }
      // その他は通常のテーマを使用
      return {
        path: file
      };
    })
  ],
  
  documentProcessor,
  entryContext: './manuscripts',
  output: './output/techbook-java-primer.pdf'
};