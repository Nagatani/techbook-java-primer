const markdownFiles = [
  'table-of-contents.md',
  'learning-path-guide.md',
  'chapter01-introduction.md',
  'chapter02-getting-started.md',
  'chapter03-oop-basics.md',
  'chapter04-classes-and-instances.md',
  'chapter05-inheritance-and-polymorphism.md',
  'chapter06-immutability-and-final.md',
  'chapter07-abstract-classes-and-interfaces.md',
  'chapter08-enums.md',
  'chapter09-records.md',
  'chapter10-collections.md',
  'chapter11-generics.md',
  'chapter12-lambda-and-functional-interfaces.md',
  'chapter13-advanced-collections.md',
  'chapter14-exception-handling.md',
  'chapter15-file-io.md',
  'chapter16-multithreading.md',
  'chapter17-network-programming.md',
  'chapter18-gui-basics.md',
  'chapter19-gui-event-handling.md',
  'chapter20-advanced-gui.md',
  'chapter21-unit-testing.md',
  'chapter22-documentation-and-libraries.md',
  'chapter23-build-and-deploy.md',
  // 付録
  'appendix-index.md', // 付録一覧
  'appendix-a-environment-setup.md', // 付録A: 開発環境の構築
  'appendix-b-index-revised.md', // 付録B: 技術的詳細解説（Deep Dive）インデックス
  'appendix-c-theoretical-foundations.md', // 付録C: ソフトウェア工学の理論的基盤
  'appendix-d-integrated-project.md', // 付録D: 統合プロジェクト - 学生管理システム
  'appendix-e-database-programming.md', // 付録E: データベースプログラミング
  'appendix-f-java-time-api.md', // 付録F: java.time API完全ガイド
  'cross-reference-guide.md',
  'glossary.md',
  'colophon.md',
];



module.exports = {
  title: '大学2年生のためのプログラミング入門 オブジェクト指向言語 Java',
  author: 'Hidehiro Nagatani',
  language: 'ja',
  theme: './styles/custom-theme.css',

  entry: [
    // 表紙を最初に配置（専用CSS使用）
    { 
      path: 'cover.md', 
      title: '表紙',
      theme: './styles/cover-theme.css'
    },
    // 目次（専用CSS使用）
    {
      path: 'table-of-contents.md',
      title: '目次',
      theme: './styles/toc-theme.css'
    },
    // 残りのファイル（learning-path-guide.mdを除外）
    ...markdownFiles.slice(2).map(file => {
      // 用語集に専用CSSを適用
      if (file === 'glossary.md') {
        return {
          path: file,
          theme: './styles/glossary-theme.css'
        };
      }
      // その他は通常のテーマを使用
      return {
        path: file
      };
    })
  ],
  
  entryContext: './manuscripts',
  output: './output/techbook-java-primer.pdf'
};