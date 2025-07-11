const markdownFiles = [
  'cover.md',
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
  'chapter12-advanced-collections.md',
  'chapter13-lambda-and-functional-interfaces.md',
  'chapter14-exception-handling.md',
  'chapter15-file-io.md',
  'chapter16-multithreading.md',
  'chapter17-network-programming.md',
  'chapter21-unit-testing.md',
  'chapter22-documentation-and-libraries.md',
  'chapter23-build-and-deploy.md',
  // 付録
  'appendix-index.md', // 付録一覧
  'appendix-a-environment-setup.md', // 付録A: 開発環境の構築
  'appendix-b-index-revised.md', // 付録B: 技術的詳細解説（Deep Dive）インデックス
  'appendix-c-theoretical-foundations.md', // 付録C: ソフトウェア工学の理論的基盤
  'appendix-d-javabeans.md', // 付録D: JavaBeansの仕様
  'appendix-e-gui-programming.md', // 付録E: Swingを使ったGUIプログラミング
  'cross-reference-guide.md',
  'glossary.md',
  'colophon.md',
];



module.exports = {
  title: '大学2年生のためのプログラミング入門 オブジェクト指向言語 Java',
  author: 'Hidehiro Nagatani',
  language: 'ja',
  theme: './custom-theme.css',

  entry: markdownFiles.map(file => ({
    path: file
  })),
  
  entryContext: './manuscripts',
  output: './output/techbook-java-primer.pdf',
  toc: true
};