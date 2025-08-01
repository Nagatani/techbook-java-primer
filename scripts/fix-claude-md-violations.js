#!/usr/bin/env node

/**
 * CLAUDE.md ルール違反を自動修正するスクリプト
 * 
 * 実行方法:
 *   node scripts/fix-claude-md-violations.js
 *   npm run fix:compliance
 */

const fs = require('fs');
const path = require('path');
const glob = require('glob');
const { ComplianceChecker } = require('./check-claude-md-compliance');

// 自動修正パターンの定義
const AUTO_FIX_PATTERNS = [
    {
        id: 'colon-continuation-basic',
        pattern: /以下のような(.+)があります：$/gm,
        replacement: '以下のような$1があります。',
        description: '基本的なコロン導入文の修正'
    },
    {
        id: 'colon-continuation-list',
        pattern: /以下(.*)：$/gm,
        replacement: '以下$1は次の通りです。',
        description: 'リスト導入でのコロン修正'
    },
    {
        id: 'colon-continuation-next',
        pattern: /次のような(.+)です：$/gm,
        replacement: '次のような$1があります。',
        description: '「次のような〜：」パターンの修正'
    },
    {
        id: 'incorrect-code-listing',
        pattern: /\*\*リスト(\d+-\d+)\*\*/g,
        replacement: '**サンプルコード$1**',
        description: 'コードリスト番号の「リスト」→「サンプルコード」修正'
    },
    {
        id: 'list-colon-technical-terms',
        pattern: /^(\s*)- \*\*([^*]+)\*\*：(.+)$/gm,
        replacement: '$1- $2：$3',
        description: 'リスト項目の技術用語強調とコロンの修正'
    }
    // 注意: title-colon-list は階層化が必要なため自動修正対象外
    // 手動で「- タイトル\n    + 説明文」形式に変更してください
];

// より複雑な修正パターン（手動確認推奨）
const MANUAL_FIX_SUGGESTIONS = [
    {
        pattern: /- \*\*[^*]+\*\*：/g,
        suggestion: 'リスト項目の強調表記とコロンを削除し、自然な文章に変更してください',
        example: '- **保守性の低下**：同じロジック... → - 保守性の低下により、同じロジック...'
    },
    {
        pattern: /^\*\*[^*]+\*\*$/gm,
        suggestion: '強調表記を見出し記法に変更してください',
        example: '**重要なポイント** → ### 重要なポイント'
    }
];

class ComplianceFixier {
    constructor() {
        this.fixes = [];
        this.manualReviewNeeded = [];
        this.processedFiles = 0;
    }

    /**
     * ファイルの自動修正を実行
     */
    fixFile(filePath) {
        let content = fs.readFileSync(filePath, 'utf8');
        let modified = false;
        const originalContent = content;

        console.log(`🔧 修正中: ${filePath}`);

        // 自動修正パターンを適用
        AUTO_FIX_PATTERNS.forEach(pattern => {
            const matches = content.match(pattern.pattern);
            if (matches) {
                content = content.replace(pattern.pattern, pattern.replacement);
                modified = true;
                
                this.fixes.push({
                    file: filePath,
                    pattern: pattern.id,
                    description: pattern.description,
                    matches: matches.length
                });

                console.log(`   ✅ ${pattern.description}: ${matches.length}箇所修正`);
            }
        });

        // 手動確認が必要な項目をチェック
        MANUAL_FIX_SUGGESTIONS.forEach(suggestion => {
            const matches = content.match(suggestion.pattern);
            if (matches) {
                this.manualReviewNeeded.push({
                    file: filePath,
                    pattern: suggestion.pattern.source,
                    matches: matches,
                    suggestion: suggestion.suggestion,
                    example: suggestion.example
                });
            }
        });

        // ファイルを更新
        if (modified) {
            fs.writeFileSync(filePath, content);
            this.processedFiles++;
            
            // バックアップファイルを作成
            const backupDir = path.join(path.dirname(filePath), 'backup');
            fs.mkdirSync(backupDir, { recursive: true });
            const backupPath = path.join(backupDir, path.basename(filePath) + '.backup.' + Date.now());
            fs.writeFileSync(backupPath, originalContent);
            console.log(`   💾 バックアップ: ${path.relative(process.cwd(), backupPath)}`);
        }
    }

    /**
     * 指定されたディレクトリ内のファイルを修正
     */
    fixDirectory(directory = 'manuscripts') {
        const pattern = path.join(directory, '**/*.md');
        const files = glob.sync(pattern);

        console.log(`\n🛠️  CLAUDE.mdルール違反の自動修正を開始します...`);
        console.log(`📁 対象ディレクトリ: ${directory}`);
        console.log(`📄 対象ファイル数: ${files.length}`);

        files.forEach(file => {
            this.fixFile(file);
        });
    }

    /**
     * 修正結果のレポートを生成
     */
    generateReport() {
        console.log(`\n✅ 自動修正完了`);
        console.log(`📊 修正結果:`);
        console.log(`   - 処理したファイル数: ${this.processedFiles}`);
        console.log(`   - 適用した修正数: ${this.fixes.length}`);
        console.log(`   - 手動確認が必要な項目: ${this.manualReviewNeeded.length}`);

        if (this.fixes.length > 0) {
            console.log(`\n🔧 適用した修正:`);
            const fixByPattern = {};
            this.fixes.forEach(fix => {
                if (!fixByPattern[fix.pattern]) {
                    fixByPattern[fix.pattern] = { description: fix.description, count: 0, files: [] };
                }
                fixByPattern[fix.pattern].count += fix.matches;
                fixByPattern[fix.pattern].files.push(fix.file);
            });

            Object.entries(fixByPattern).forEach(([pattern, info]) => {
                console.log(`   - ${info.description}: ${info.count}箇所`);
                console.log(`     ファイル: ${[...new Set(info.files)].join(', ')}`);
            });
        }

        if (this.manualReviewNeeded.length > 0) {
            console.log(`\n⚠️  手動確認が必要な項目:`);
            this.manualReviewNeeded.forEach((item, index) => {
                console.log(`\n${index + 1}. ${item.file}`);
                console.log(`   パターン: ${item.pattern}`);
                console.log(`   該当箇所: ${item.matches.length}箇所`);
                console.log(`   💡 ${item.suggestion}`);
                if (item.example) {
                    console.log(`   例: ${item.example}`);
                }
            });
        }
    }

    /**
     * 修正後の検証を実行
     */
    verifyFixes() {
        console.log(`\n🔍 修正結果を検証中...`);
        const checker = new ComplianceChecker();
        checker.checkDirectory('manuscripts');
        
        const success = checker.generateReport();
        
        if (success) {
            console.log(`\n🎉 すべての自動修正可能な違反が解決されました！`);
        } else {
            console.log(`\n⚠️  一部の違反は手動修正が必要です。上記のレポートを確認してください。`);
        }

        return success;
    }
}

// バックアップファイルのクリーンアップ
function cleanupBackups(directory = 'manuscripts', olderThanDays = 7) {
    const pattern = path.join(directory, '**/*.backup.*');
    const backupFiles = glob.sync(pattern);
    const cutoffTime = Date.now() - (olderThanDays * 24 * 60 * 60 * 1000);
    
    let cleanedCount = 0;
    backupFiles.forEach(file => {
        const stats = fs.statSync(file);
        if (stats.mtime.getTime() < cutoffTime) {
            fs.unlinkSync(file);
            cleanedCount++;
        }
    });

    if (cleanedCount > 0) {
        console.log(`\n🧹 ${olderThanDays}日以上古いバックアップファイル ${cleanedCount}個 を削除しました`);
    }
}

// メイン処理
function main() {
    const fixer = new ComplianceFixier();
    
    // コマンドライン引数の処理
    const args = process.argv.slice(2);
    const directory = args[0] || 'manuscripts';
    const skipVerify = args.includes('--skip-verify');
    const cleanup = args.includes('--cleanup');

    try {
        // バックアップのクリーンアップ（オプション）
        if (cleanup) {
            cleanupBackups(directory);
        }

        // 自動修正を実行
        fixer.fixDirectory(directory);
        fixer.generateReport();

        // 修正結果の検証（オプション）
        if (!skipVerify) {
            fixer.verifyFixes();
        }

        console.log(`\n💡 推奨される次のステップ:`);
        console.log(`   1. git diff で変更内容を確認`);
        console.log(`   2. npm run lint で全体をチェック`);
        console.log(`   3. 手動確認が必要な項目があれば修正`);
        console.log(`   4. 変更をコミット`);

    } catch (error) {
        console.error(`❌ エラーが発生しました: ${error.message}`);
        process.exit(1);
    }
}

// スクリプトが直接実行された場合のみmain()を呼び出し
if (require.main === module) {
    main();
}

module.exports = { ComplianceFixier, AUTO_FIX_PATTERNS };