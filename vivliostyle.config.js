module.exports = {
  title: 'Java入門',
  author: 'Hidehiro Nagatani',
  language: 'ja',
  entry: [
    'cover.md',
    'learning-path-guide.md',
    'chapter01-introduction.md',
    'chapter02-getting-started.md',
    // 第3章 - オブジェクト指向の考え方
    'chapter03-oop-basics.md',
    'chapter03a-oop-introduction.md',
    'chapter03b-oop-concepts.md',
    'chapter03c-classes-and-objects.md',
    'chapter03d-java-basics.md',
    'chapter03e-operators.md',
    'chapter03f-control-structures.md',
    'chapter03h-static-modifier.md',
    'chapter03i-arrays.md',
    'chapter03j-exercises.md',
    // 第4章 - クラスとインスタンス
    'chapter04-classes-and-instances.md',
    'chapter04a-encapsulation-basics.md',
    'chapter04b-exercises.md',
    'chapter05-inheritance-and-polymorphism.md',
    'chapter06-immutability-and-final.md',
    'chapter07-abstract-classes-and-interfaces.md',
    'chapter08-collections.md',
    'chapter09-generics.md',
    'chapter10-advanced-collections.md',
    'chapter11-lambda-and-functional-interfaces.md',
    'chapter12-records.md',
    'chapter13-enums.md',
    'chapter14-exception-handling.md',
    'chapter15-file-io.md',
    // 第16章 マルチスレッドプログラミング
    'chapter16-multithreading.md',
    'chapter16a-thread-basics.md',
    'chapter16b-executor-framework.md',
    'chapter16c-concurrent-utilities.md',
    'chapter16d-practical-concurrency.md',
    'chapter16e-exercises.md',
    'chapter17-gui-basics.md',
    // 第18章 GUIのイベント処理
    'chapter18-gui-event-handling.md',
    'chapter18a-event-basics.md',
    'chapter18b-event-types.md',
    'chapter18c-advanced-events.md',
    'chapter18d-exercises.md',
    'chapter19-advanced-gui.md',
    'chapter20-unit-testing.md',
    'chapter21-documentation-and-libraries.md',
    'chapter22-build-and-deploy.md',
    // 付録
    'appendix-index.md', // 付録一覧
    'appendix-a-environment-setup.md', // 付録A: 開発環境の構築
    // 付録B: 技術的詳細解説（Deep Dive）
    'appendix-b-index.md',
    // 基礎概念（第1-2章関連）
    'appendix-b01-language-design.md',
    'appendix-b02-jvm-architecture.md',
    'appendix-b03-language-evolution.md',
    'appendix-b04-compiler-ast.md',
    // オブジェクト指向概念（第3-7章関連）
    'appendix-b05-programming-paradigms.md',
    'appendix-b06-software-design.md',
    'appendix-b07-virtual-method-table.md',
    'appendix-b08-immutability-patterns.md',
    // コレクションとジェネリクス（第8-10章関連）
    'appendix-b09-collection-internals.md',
    'appendix-b10-type-erasure.md',
    'appendix-b11-stream-api-internals.md',
    // モダンJava機能（第11-13章関連）
    'appendix-b12-enum-patterns.md',
    // 例外処理とI/O（第14-15章関連）
    'appendix-b13-exception-performance.md',
    'appendix-b14-nio2-advanced.md',
    // 並行処理（第16章関連）
    'appendix-b15-concurrent-distributed.md',
    'appendix-b16-java-memory-model.md',
    'appendix-b17-memory-performance.md',
    // 高度なトピック（第20-22章関連）
    'appendix-b18-testing-strategies.md',
    // 付録C: 上級技術解説
    'appendix-c-theoretical-foundations.md', // ソフトウェア工学の理論的基盤
    'appendix-d-javabeans.md', // 付録D: JavaBeansの仕様
    'cross-reference-guide.md',
    'glossary.md',
    'colophon.md',
  ],
  entryContext: './manuscripts',
  output: './output/techbook-java-primer.pdf',
  theme: '@vivliostyle/theme-techbook',
  workspaceDir: '.vivliostyle',
  toc: true,
  style: [
    '@vivliostyle/theme-techbook',
    './styles/book-theme.css',
    './styles/code-blocks.css',
    './styles/special-sections.css',
    './styles/cover.css'
  ],
};