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

// 版（BOOK_EDITION）: alpha はWeb配布のALPHA版（既定）、release は出版する正規版。
// 表紙・奥付の出し分けは scripts/vfm-edition.js、PDFの名前は下の output
const { currentEdition } = require('./scripts/vfm-edition.js');
const edition = currentEdition();

// 出力先（BOOK_TARGET）: pdf は通常のPDF（既定）、kdp はAmazon KDPのペーパーバックの本文PDF、
// epub は電子書籍（scripts/vfm-epub.js）。kdp と epub は正規版（BOOK_EDITION=release）で作る。
// kdp では1ページ目を白地の本扉にし（kdp-title-theme.css）、のどを広げる（kdp-theme.css）。
// epub では紙面のテーマを使わず epub-theme.css だけを当て、表紙を画像にし（scripts/epub-assets.js が作る）、
// 目次を原稿（table-of-contents.md）ではなくVivliostyleが作るもの（リーダーの目次にも使われる）にする。
const { currentTarget } = require('./scripts/vfm-epub.js');
const target = currentTarget();
if (target !== 'pdf' && edition !== 'release') {
  throw new Error(`BOOK_TARGET=${target} は BOOK_EDITION=release と組み合わせてください（npm run build:${target}）`);
}
// my-theme のディレクトリ（package.json があるパッケージ）の外に置く。中に置くとパッケージ全体がEPUBに入る
const EPUB_THEME = './theme/epub-theme.css';
// KDP向けのテーマを各文書のテーマの後ろに重ねる。EPUBではどの文書も epub-theme.css だけにする
const withTarget = (theme) => {
  if (target === 'epub') return EPUB_THEME;
  if (target !== 'kdp') return theme;
  return [...(Array.isArray(theme) ? theme : [theme]), './theme/my-theme/kdp-theme.css'];
};
const outputPath = {
  kdp: './output/techbook-java-primer-kdp.pdf',
  epub: './output/techbook-java-primer.epub',
  pdf: edition === 'alpha' ? './output/techbook-java-primer.pdf' : `./output/techbook-java-primer-${edition}.pdf`,
}[target];

// 前付け（表紙・目次）のノンブルはローマ数字にし、本文は第1章を1ページとして数え直す
const frontMatter = ['table-of-contents.md'];
const bodyStart = 'chapter01-introduction.md';

// 表紙と目次
const frontEntries = target === 'epub'
  ? [
    { rel: 'cover', title: '表紙', imageSrc: 'images/epub/cover.jpg', imageAlt: '表紙', theme: EPUB_THEME },
    { rel: 'contents', title: '目次', theme: EPUB_THEME },
  ]
  : [{
    path: 'cover.md',
    title: target === 'kdp' ? '扉' : '表紙',
    theme: target === 'kdp'
      ? ['./theme/my-theme/cover-theme.css', './theme/my-theme/kdp-title-theme.css']
      : './theme/my-theme/cover-theme.css'
  }];
const bodyFiles = target === 'epub' ? markdownFiles.filter((file) => !frontMatter.includes(file)) : markdownFiles;

module.exports = {
  title: '大学2年生のための実践的プログラミング入門 オブジェクト指向言語 Java',
  author: 'Hidehiro Nagatani',
  language: 'ja',
  size: 'B5',
  theme: target === 'epub' ? EPUB_THEME : './theme/my-theme',
  entry: [
    ...frontEntries,
    ...bodyFiles.map(file => {
      // 用語集に専用CSSを適用
      if (file === 'glossary.md') {
        return {
          path: file,
          theme: withTarget('./theme/my-theme/glossary-theme.css')
        };
      }
      if (frontMatter.includes(file)) {
        return { path: file, theme: withTarget(['./theme/my-theme', './theme/my-theme/frontmatter-theme.css']) };
      }
      if (file === bodyStart) {
        return { path: file, theme: withTarget(['./theme/my-theme', './theme/my-theme/body-start-theme.css']) };
      }
      // その他は通常のテーマを使用
      return target === 'pdf'
        ? { path: file }
        : { path: file, theme: withTarget('./theme/my-theme') };
    })
  ],
  ...(target === 'epub' ? {
    // リーダーの目次（nav）と本の中の目次。章（h1）と節（h2）まで載せる
    toc: { title: '目次', sectionDepth: 2 },
    // src は entryContext（manuscripts）から見たパスにする（設定ファイルからのパスだとEPUBの中で表紙画像と結び付かない）
    cover: { src: 'images/epub/cover.jpg', name: '表紙' },
    // 図はPNG（images/epub/）を使うので、元のSVGと表紙の模様は入れない
    copyAsset: { excludes: ['images/diagrams/**', 'images/cover-motif.svg'] },
    // EPUBには作業用ディレクトリの中身がそのまま入る。PDFのビルドが残したファイルが混ざらないように
    // 専用のディレクトリを使い、scripts/epub-assets.js がビルドの前に空にする
    workspaceDir: '.vivliostyle-epub',
  } : {}),

  documentProcessor,
  entryContext: './manuscripts',
  output: target === 'epub' ? { path: outputPath, format: 'epub' } : outputPath
};