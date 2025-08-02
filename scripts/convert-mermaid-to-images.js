const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');
const glob = require('glob');

// 設定
const DIAGRAMS_DIR = path.join(__dirname, '..', 'docs', 'diagrams');
const IMAGES_DIR = path.join(__dirname, '..', 'manuscripts', 'images', 'diagrams');
const MANUSCRIPTS_DIR = path.join(__dirname, '..', 'manuscripts');
const MANUSCRIPTS_BACKUP_DIR = path.join(__dirname, '..', 'manuscripts', 'backup-mermaid');

// 画像出力ディレクトリを作成
if (!fs.existsSync(IMAGES_DIR)) {
  fs.mkdirSync(IMAGES_DIR, { recursive: true });
}

// バックアップディレクトリを作成
if (!fs.existsSync(MANUSCRIPTS_BACKUP_DIR)) {
  fs.mkdirSync(MANUSCRIPTS_BACKUP_DIR, { recursive: true });
}

console.log('Mermaid図の変換を開始します...');

// 1. docs/diagrams/*.mmdファイルをPNG画像に変換
const mermaidFiles = glob.sync(path.join(DIAGRAMS_DIR, '*.mmd'));

mermaidFiles.forEach(mmdFile => {
  const basename = path.basename(mmdFile, '.mmd');
  const outputFile = path.join(IMAGES_DIR, `${basename}.png`);
  
  try {
    console.log(`変換中: ${basename}.mmd -> ${basename}.png`);
    
    // mermaid-cliを使用してPNGに変換（高解像度・大きめのサイズ）
    execSync(`npx mmdc -i "${mmdFile}" -o "${outputFile}" -t default -b white -w 1200 -H 800 --scale 2`, {
      stdio: 'inherit'
    });
    
    console.log(`✓ 完了: ${basename}.png`);
  } catch (error) {
    console.error(`✗ エラー: ${basename}.mmd の変換に失敗しました`, error.message);
  }
});

// 2. manuscripts/*.mdファイル内のMermaidブロックを画像参照に置換
const markdownFiles = glob.sync(path.join(MANUSCRIPTS_DIR, '*.md'));

markdownFiles.forEach(mdFile => {
  const filename = path.basename(mdFile);
  const content = fs.readFileSync(mdFile, 'utf8');
  
  // Mermaidコードがあるかチェック
  if (!content.includes('```mermaid')) {
    return;
  }
  
  console.log(`\n処理中: ${filename}`);
  
  // Mermaidブロックを検出する正規表現（figcaptionとp class="caption"の両方に対応）
  const mermaidBlockRegex1 = /<div class="figure">\s*\n?\s*```mermaid([\s\S]*?)```\s*\n?\s*<figcaption>(.*?)<\/figcaption>\s*\n?\s*<\/div>/g;
  const mermaidBlockRegex2 = /<div class="figure">\s*\n?\s*```mermaid([\s\S]*?)```\s*\n?\s*<p class="caption">(.*?)<\/p>\s*\n?\s*<\/div>/g;
  
  let hasChanges = false;
  let modifiedContent = content;
  let match;
  let blockIndex = 1;
  
  // 両方のパターンを試す
  const patterns = [
    { regex: mermaidBlockRegex1, captionTag: 'figcaption' },
    { regex: mermaidBlockRegex2, captionTag: 'p class="caption"' }
  ];
  
  for (const pattern of patterns) {
    pattern.regex.lastIndex = 0; // 正規表現の位置をリセット
    while ((match = pattern.regex.exec(content)) !== null) {
    const fullMatch = match[0];
    const mermaidCode = match[1].trim();
    const caption = match[2];
    
    // 章番号を取得（例: chapter03-oop-basics.md -> 03）
    const chapterMatch = filename.match(/chapter(\d+)-/);
    const chapterNum = chapterMatch ? chapterMatch[1] : '00';
    
    // 図番号を削除（例: 図3-1 概念図 -> 概念図）
    const cleanCaption = caption.replace(/^図\d+-\d+\s+/, '');
    
    // 図の番号からファイル名を生成（例: 図3-1 -> figure03-01）
    const figureMatch = caption.match(/図(\d+)-(\d+)/);
    let imageFileName;
    
    if (figureMatch) {
      imageFileName = `figure${chapterNum}-${figureMatch[2].padStart(2, '0')}.png`;
    } else {
      imageFileName = `figure${chapterNum}-${blockIndex.toString().padStart(2, '0')}.png`;
    }
    
    // 一時的なMermaidファイルを作成
    const tempMmdFile = path.join(DIAGRAMS_DIR, `temp-${imageFileName.replace('.png', '.mmd')}`);
    fs.writeFileSync(tempMmdFile, mermaidCode);
    
    // 画像に変換
    const outputImagePath = path.join(IMAGES_DIR, imageFileName);
    try {
      execSync(`npx mmdc -i "${tempMmdFile}" -o "${outputImagePath}" -t default -b white -w 1200 -H 800 --scale 2`, {
        stdio: 'pipe'
      });
      
      // 画像参照に置換（キャプションタグは生成しない、図番号も削除）
      const imageTag = `<div class="figure">\n\n![${cleanCaption}](images/diagrams/${imageFileName})\n</div>`;
      modifiedContent = modifiedContent.replace(fullMatch, imageTag);
      hasChanges = true;
      
      console.log(`✓ ${filename} の Mermaid図を ${imageFileName} に変換しました`);
    } catch (error) {
      console.error(`✗ エラー: ${filename} の Mermaid図の変換に失敗しました`, error.message);
    } finally {
      // 一時ファイルを削除
      if (fs.existsSync(tempMmdFile)) {
        fs.unlinkSync(tempMmdFile);
      }
    }
    
    blockIndex++;
    }
  }
  
  // 変更があった場合はファイルを更新
  if (hasChanges) {
    // バックアップを作成
    const backupFile = path.join(MANUSCRIPTS_BACKUP_DIR, `${filename}.backup.${Date.now()}`);
    fs.copyFileSync(mdFile, backupFile);
    
    // ファイルを更新
    fs.writeFileSync(mdFile, modifiedContent);
    console.log(`✓ ${filename} を更新しました（バックアップ: ${path.basename(backupFile)}）`);
  }
});

console.log('\nMermaid図の変換が完了しました！');
console.log(`画像は ${IMAGES_DIR} に保存されました`);