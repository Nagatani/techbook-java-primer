const fs = require('fs');
const path = require('path');
const glob = require('glob');

// manuscriptsディレクトリのパス
const MANUSCRIPTS_DIR = path.join(__dirname, '..', 'manuscripts');

console.log('figure内の<p class="caption">タグを削除します...');

// すべてのMarkdownファイルを取得（バックアップディレクトリを除く）
const markdownFiles = glob.sync(path.join(MANUSCRIPTS_DIR, '*.md'));

let totalReplacements = 0;

markdownFiles.forEach(mdFile => {
  const filename = path.basename(mdFile);
  const content = fs.readFileSync(mdFile, 'utf8');
  
  // <div class="figure">内の<p class="caption">を削除する正規表現
  // 画像参照の後にある<p class="caption">タグの行全体を削除
  const figureRegex = /(<div class="figure">\s*\n\s*!\[.*?\]\(.*?\)\s*\n)\s*<p class="caption">.*?<\/p>\s*\n(\s*<\/div>)/g;
  
  const modifiedContent = content.replace(figureRegex, '$1$2');
  
  // 変更があった場合のみファイルを更新
  if (content !== modifiedContent) {
    // 変更回数をカウント
    const matches = content.match(figureRegex);
    const replacementCount = matches ? matches.length : 0;
    totalReplacements += replacementCount;
    
    // ファイルを更新
    fs.writeFileSync(mdFile, modifiedContent);
    console.log(`✓ ${filename} - ${replacementCount}箇所の<p class="caption">を削除しました`);
  }
});

console.log(`\n完了！合計 ${totalReplacements} 箇所の<p class="caption">タグを削除しました。`);