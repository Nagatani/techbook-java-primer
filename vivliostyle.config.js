module.exports = {
  title: 'Java入門',
  author: 'Hidehiro Nagatani',
  language: 'ja',
  entry: [
    'index.md',
    // 'chapter2.md', // 他のファイルがあれば追加
  ],
  entryContext: './manuscripts',
  output: './output/techbook-java-primer.pdf',
  theme: '@vivliostyle/theme-techbook',
  workspaceDir: '.vivliostyle',
  toc: true,
  style: './custom-styles.css',
};