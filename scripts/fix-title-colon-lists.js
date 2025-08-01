#!/usr/bin/env node

/**
 * 「項目タイトル： 説明文」形式のリストを階層化リストに変換するスクリプト
 */

const fs = require('fs');
const path = require('path');
const glob = require('glob');

/**
 * 括弧内のコロンかどうかをチェック
 */
function isColonInParentheses(text, colonIndex) {
    const brackets = [
        ['（', '）'], ['(', ')'], ['【', '】'], ['[', ']'], 
        ['『', '』'], ['「', '」'], ['〈', '〉'], ['<', '>'],
        ['{', '}'], ['〔', '〕']
    ];
    
    for (const [open, close] of brackets) {
        let openIndex = text.lastIndexOf(open, colonIndex);
        if (openIndex !== -1) {
            let closeIndex = text.indexOf(close, colonIndex);
            if (closeIndex !== -1 && openIndex < colonIndex && colonIndex < closeIndex) {
                return true;
            }
        }
    }
    return false;
}

// 「項目タイトル： 説明文」パターンを階層化リストに変換
function fixTitleColonLists(content) {
    let modified = content;
    const lines = content.split('\n');
    const newLines = [];
    
    for (let i = 0; i < lines.length; i++) {
        const line = lines[i];
        
        // 番号なしリスト項目のパターン: - タイトル： 説明文
        const unorderedMatch = line.match(/^(\s*[-\*\+])\s+([^：\n]+)：\s*([^\n]*)$/);
        if (unorderedMatch) {
            const colonIndex = line.indexOf('：');
            // 括弧内のコロンは変換しない
            if (!isColonInParentheses(line, colonIndex)) {
                const [, listMarker, title, description] = unorderedMatch;
                if (description.trim()) {
                    // 説明文がある場合は階層化
                    newLines.push(`${listMarker} ${title.trim()}`);
                    newLines.push(`${listMarker}    + ${description.trim()}`);
                } else {
                    // 説明文がない場合はコロンだけ削除
                    newLines.push(`${listMarker} ${title.trim()}`);
                    // 次の行が説明文かチェック
                    if (i + 1 < lines.length && lines[i + 1].trim() && !lines[i + 1].match(/^\s*[-\*\+\d]/)) {
                        i++; // 次の行をスキップ
                        newLines.push(`${listMarker}    + ${lines[i].trim()}`);
                    }
                }
                continue;
            }
        }
        
        // 番号ありリスト項目のパターン: 1. タイトル： 説明文
        const orderedMatch = line.match(/^(\s*)(\d+\.)\s+([^：\n]+)：\s*([^\n]*)$/);
        if (orderedMatch) {
            const colonIndex = line.indexOf('：');
            // 括弧内のコロンは変換しない
            if (!isColonInParentheses(line, colonIndex)) {
                const [, indent, number, title, description] = orderedMatch;
                if (description.trim()) {
                    // 説明文がある場合は階層化
                    newLines.push(`${indent}${number} ${title.trim()}`);
                    newLines.push(`${indent}    + ${description.trim()}`);
                } else {
                    // 説明文がない場合はコロンだけ削除
                    newLines.push(`${indent}${number} ${title.trim()}`);
                    // 次の行が説明文かチェック
                    if (i + 1 < lines.length && lines[i + 1].trim() && !lines[i + 1].match(/^\s*[-\*\+\d]/)) {
                        i++; // 次の行をスキップ
                        newLines.push(`${indent}    + ${lines[i].trim()}`);
                    }
                }
                continue;
            }
        }
        
        // マッチしない行はそのまま保持
        newLines.push(line);
    }
    
    return newLines.join('\n');
}

// ファイルを処理
function processFile(filePath) {
    try {
        const content = fs.readFileSync(filePath, 'utf8');
        const fixed = fixTitleColonLists(content);
        
        if (content !== fixed) {
            // バックアップを作成
            const backupDir = path.join(path.dirname(filePath), 'backup');
            fs.mkdirSync(backupDir, { recursive: true });
            const backupPath = path.join(backupDir, path.basename(filePath) + '.backup.' + Date.now());
            fs.writeFileSync(backupPath, content);
            
            // 修正版を保存
            fs.writeFileSync(filePath, fixed);
            
            console.log(`✅ 修正完了: ${filePath}`);
            console.log(`📁 バックアップ: ${path.relative(process.cwd(), backupPath)}`);
            
            // 修正内容の統計
            const originalMatches = content.match(/(\s*[-\*\+]|\d+\.)\s+[^：\n]+：\s*[^\n]/gm) || [];
            console.log(`📊 修正項目数: ${originalMatches.length}件`);
            
            return true;
        } else {
            console.log(`⏭️  変更なし: ${filePath}`);
            return false;
        }
    } catch (error) {
        console.error(`❌ エラー: ${filePath}`, error.message);
        return false;
    }
}

// メイン処理
function main() {
    const manuscriptsDir = path.join(__dirname, '..', 'manuscripts');
    const files = glob.sync('*.md', { cwd: manuscriptsDir });
    
    console.log('🔧 タイトルコロン形式リストの階層化修正を開始...');
    console.log(`📂 対象ディレクトリ: ${manuscriptsDir}`);
    console.log(`📄 対象ファイル数: ${files.length}`);
    console.log();
    
    let modifiedCount = 0;
    let totalCount = 0;
    
    for (const file of files) {
        const filePath = path.join(manuscriptsDir, file);
        totalCount++;
        
        if (processFile(filePath)) {
            modifiedCount++;
        }
    }
    
    console.log();
    console.log('📋 処理完了サマリー:');
    console.log(`   - 処理ファイル数: ${totalCount}`);
    console.log(`   - 修正ファイル数: ${modifiedCount}`);
    console.log(`   - 未変更ファイル数: ${totalCount - modifiedCount}`);
    
    if (modifiedCount > 0) {
        console.log();
        console.log('✨ 修正が完了しました！');
        console.log('💡 次のステップ:');
        console.log('   1. npm run check:compliance で結果を確認');
        console.log('   2. git diff で変更内容を確認');
        console.log('   3. 問題がなければコミット');
    }
}

if (require.main === module) {
    main();
}

module.exports = { fixTitleColonLists };