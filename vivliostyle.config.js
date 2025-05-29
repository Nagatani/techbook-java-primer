module.exports = {
  title: 'Java入門', // 書籍のタイトル（任意）
  author: '永谷栄宏', // 著者名（任意）
  language: 'ja', // 言語設定
  // entry: 'manuscript.md', // 単一のMarkdownファイルの場合
  entry: [ // 複数のMarkdownファイルを順番に結合する場合
    'index.md',
    // 'chapter2.md', // 他のファイルがあれば追加
  ],
  entryContext: './manuscripts', // Markdownファイルがサブディレクトリにある場合
  output: './output/techbook-java-primer.pdf', // 出力ファイル名と場所を指定（デフォルトは vivliostyle-output.pdf）
  theme: '@vivliostyle/theme-techbook', // テーマを指定（後述）
  workspaceDir: '.vivliostyle', // 一時ファイルの出力先
  // tableOfContents: true, // 目次を自動生成するかどうか (テーマ側での対応も必要)
};