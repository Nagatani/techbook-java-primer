const fs = require('fs');
const path = require('path');
const glob = require('glob');

// 設定
const MANUSCRIPTS_DIR = path.join(__dirname, '..', 'manuscripts');

console.log('図番号の削除を開始します...');

// manuscripts/*.mdファイルを取得
const markdownFiles = glob.sync(path.join(MANUSCRIPTS_DIR, '*.md'));

let totalUpdated = 0;

markdownFiles.forEach(mdFile => {
  const filename = path.basename(mdFile);
  const content = fs.readFileSync(mdFile, 'utf8');
  
  // 図番号を含む画像参照を検出する正規表現
  // 例: ![図1-1 JVMアーキテクチャ](images/...) -> ![JVMアーキテクチャ](images/...)
  const figureRegex = /!\[図\d+-\d+\s+(.+?)\]/g;
  
  let hasChanges = false;
  const modifiedContent = content.replace(figureRegex, (match, caption) => {
    hasChanges = true;
    return `![${caption}]`;
  });
  
  if (hasChanges) {
    // カウント
    const matches = content.match(figureRegex);
    const updateCount = matches ? matches.length : 0;
    
    // ファイルを更新
    fs.writeFileSync(mdFile, modifiedContent);
    console.log(`✓ ${filename}: ${updateCount}個の図番号を削除しました`);
    totalUpdated += updateCount;
  }
});

console.log(`\n完了: 合計${totalUpdated}個の図番号を削除しました`);