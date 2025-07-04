module.exports = {
  title: 'Java入門',
  author: 'Hidehiro Nagatani',
  language: 'ja',
  entry: [
    'cover.md',
    'foreword.md',
    'chapter01.md',
    'chapter02.md',
    'chapter03.md',
    'chapter04.md',
    'chapter05.md',
    'chapter06.md',
    'chapter07.md',
    'chapter08-collections.md',
    'chapter09-generics.md',
    'chapter10-advanced-collections.md',
    'chapter11-lambda.md',
    'chapter12-records.md',
    'chapter13-enum.md',
    'chapter19-exceptions.md', // chapter14 in plan
    'chapter14-file-io.md', // chapter15 in plan
    'chapter20-multithreading.md', // chapter16 in plan
    'chapter11-gui-construction.md',
    'chapter12-gui-event-handling.md',
    'chapter13-advanced-gui.md',
    'chapter15-testing.md',
    'chapter16-documentation.md',
    'chapter17-build-and-deploy.md',
    'appendix-javabeans.md',
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
    './custom-styles.css'
  ],
};