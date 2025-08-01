#!/usr/bin/env node

/**
 * CLAUDE.md ルール違反を自動チェックするスクリプト
 * 
 * 実行方法:
 *   node scripts/check-claude-md-compliance.js
 *   npm run check:compliance
 */

const fs = require('fs');
const path = require('path');
const glob = require('glob');

// ルール違反パターンの定義
const VIOLATION_PATTERNS = [
    {
        id: 'bold-colon-list',
        pattern: /- \*\*[^*]+\*\*：/g,
        description: 'リスト項目での強調（**）とコロン（：）の組み合わせ使用',
        severity: 'error',
        suggestion: '強調表記を削除し、自然な文章に変更してください'
    },
    {
        id: 'colon-continuation',
        pattern: /(以下.*：|次.*：|下記.*：)$/gm,
        description: '「以下〜：」「次〜：」などのコロンで終わるリスト導入',
        severity: 'error',
        suggestion: '「次のようになります。」「以下の通りです。」などの自然な表現に変更してください'
    },
    {
        id: 'bold-as-heading',
        pattern: /^\*\*[^*]+\*\*$/gm,
        description: '強調表記（**）を見出しとして使用',
        severity: 'error',
        suggestion: '適切なMarkdown見出し記法（#、##、###）を使用してください'
    },
    {
        id: 'incorrect-code-listing',
        pattern: /\*\*リスト\d+-\d+\*\*/g,
        description: 'コードリスト番号での「リスト」形式使用',
        severity: 'error',
        suggestion: '「サンプルコードX-Y」形式に統一してください'
    },
    {
        id: 'bold-info-prefix',
        pattern: /\*\*(注意|重要|ポイント|警告)\*\*/g,
        description: '太字の情報プレフィックス（**注意**、**重要**など）',
        severity: 'warning',
        suggestion: 'より自然な表現を検討してください'
    },
    {
        id: 'list-colon-ending',
        pattern: /^- [^：]*：$/gm,
        description: 'リスト項目の末尾コロン使用',
        severity: 'warning',
        suggestion: 'リスト項目の末尾にコロンは使用しないでください'
    }
];

// 除外パターン（正当な使用例）
const EXCLUSION_PATTERNS = [
    /\*\*サンプルコード\d+-\d+\*\*/,  // 正しいコードリスト番号
    /<span class="listing-number">\*\*サンプルコード\d+-\d+\*\*<\/span>/, // HTML形式のコードリスト
    /\*\*[^*]+\*\* - 第\d+章/,  // 用語集の項目
    /> \*\*[^*]+\*\*/,  // 引用内の強調（ノート、警告など）
    /<!-- .* -->/,  // コメント内
    /```[\s\S]*?```/g  // コードブロック内
];

class ComplianceChecker {
    constructor() {
        this.violations = [];
        this.fileCount = 0;
        this.checkedFiles = [];
    }

    /**
     * 指定されたテキストが除外パターンに該当するかチェック
     */
    isExcluded(text, match, lineContent) {
        return EXCLUSION_PATTERNS.some(pattern => {
            if (pattern.global) {
                pattern.lastIndex = 0; // グローバルパターンのインデックスをリセット
            }
            return pattern.test(lineContent) || pattern.test(text);
        });
    }

    /**
     * ファイルをチェックしてルール違反を検出
     */
    checkFile(filePath) {
        const content = fs.readFileSync(filePath, 'utf8');
        const lines = content.split('\n');
        
        this.fileCount++;
        this.checkedFiles.push(filePath);

        VIOLATION_PATTERNS.forEach(rule => {
            const matches = [...content.matchAll(rule.pattern)];
            
            matches.forEach(match => {
                // マッチした位置の行番号を計算
                const beforeMatch = content.slice(0, match.index);
                const lineNumber = beforeMatch.split('\n').length;
                const lineContent = lines[lineNumber - 1];

                // 除外パターンをチェック
                if (this.isExcluded(content, match, lineContent)) {
                    return;
                }

                this.violations.push({
                    file: filePath,
                    line: lineNumber,
                    column: match.index - beforeMatch.lastIndexOf('\n'),
                    rule: rule.id,
                    severity: rule.severity,
                    description: rule.description,
                    suggestion: rule.suggestion,
                    content: lineContent.trim(),
                    match: match[0]
                });
            });
        });
    }

    /**
     * 指定されたディレクトリ内のMarkdownファイルをチェック
     */
    checkDirectory(directory = 'manuscripts') {
        const pattern = path.join(directory, '**/*.md');
        const files = glob.sync(pattern);

        console.log(`\n🔍 CLAUDE.mdルール準拠性チェックを開始します...`);
        console.log(`📁 対象ディレクトリ: ${directory}`);
        console.log(`📄 対象ファイル数: ${files.length}`);

        files.forEach(file => {
            this.checkFile(file);
        });
    }

    /**
     * チェック結果を整理して表示
     */
    generateReport() {
        console.log(`\n✅ チェック完了`);
        console.log(`📊 チェック結果:`);
        console.log(`   - チェックしたファイル数: ${this.fileCount}`);
        console.log(`   - 検出された違反数: ${this.violations.length}`);

        if (this.violations.length === 0) {
            console.log(`\n🎉 CLAUDE.mdルール違反は検出されませんでした！`);
            return true;
        }

        // 重要度別に違反をグループ化
        const errorViolations = this.violations.filter(v => v.severity === 'error');
        const warningViolations = this.violations.filter(v => v.severity === 'warning');

        console.log(`   - エラー: ${errorViolations.length}`);
        console.log(`   - 警告: ${warningViolations.length}`);

        // エラーを表示
        if (errorViolations.length > 0) {
            console.log(`\n❌ エラー (${errorViolations.length}件):`);
            this.displayViolations(errorViolations);
        }

        // 警告を表示
        if (warningViolations.length > 0) {
            console.log(`\n⚠️  警告 (${warningViolations.length}件):`);
            this.displayViolations(warningViolations);
        }

        // サマリー
        console.log(`\n📋 ファイル別サマリー:`);
        const fileStats = this.getFileStats();
        Object.entries(fileStats).forEach(([file, stats]) => {
            if (stats.total > 0) {
                console.log(`   ${file}: ${stats.errors}エラー, ${stats.warnings}警告`);
            }
        });

        return errorViolations.length === 0; // エラーがなければtrue
    }

    /**
     * 違反を表示
     */
    displayViolations(violations) {
        violations.forEach((violation, index) => {
            console.log(`\n${index + 1}. ${violation.file}:${violation.line}:${violation.column}`);
            console.log(`   ルール: ${violation.rule}`);
            console.log(`   内容: ${violation.description}`);
            console.log(`   該当: ${violation.match}`);
            console.log(`   行: ${violation.content}`);
            console.log(`   💡 ${violation.suggestion}`);
        });
    }

    /**
     * ファイル別統計を取得
     */
    getFileStats() {
        const stats = {};
        
        this.checkedFiles.forEach(file => {
            stats[file] = { errors: 0, warnings: 0, total: 0 };
        });

        this.violations.forEach(violation => {
            const file = violation.file;
            stats[file].total++;
            if (violation.severity === 'error') {
                stats[file].errors++;
            } else {
                stats[file].warnings++;
            }
        });

        return stats;
    }

    /**
     * CI/CD用のJSON形式でレポートを出力
     */
    generateJsonReport(outputPath = 'compliance-report.json') {
        const report = {
            timestamp: new Date().toISOString(),
            summary: {
                totalFiles: this.fileCount,
                totalViolations: this.violations.length,
                errors: this.violations.filter(v => v.severity === 'error').length,
                warnings: this.violations.filter(v => v.severity === 'warning').length
            },
            violations: this.violations,
            fileStats: this.getFileStats()
        };

        fs.writeFileSync(outputPath, JSON.stringify(report, null, 2));
        console.log(`\n📄 詳細レポートを ${outputPath} に出力しました`);
    }
}

// メイン処理
function main() {
    const checker = new ComplianceChecker();
    
    // コマンドライン引数の処理
    const args = process.argv.slice(2);
    const directory = args[0] || 'manuscripts';
    const jsonOutput = args.includes('--json');
    const jsonPath = args.includes('--output') ? 
        args[args.indexOf('--output') + 1] : 'compliance-report.json';

    try {
        checker.checkDirectory(directory);
        const success = checker.generateReport();

        if (jsonOutput) {
            checker.generateJsonReport(jsonPath);
        }

        // CIで使用する場合の終了コード
        process.exit(success ? 0 : 1);

    } catch (error) {
        console.error(`❌ エラーが発生しました: ${error.message}`);
        process.exit(1);
    }
}

// スクリプトが直接実行された場合のみmain()を呼び出し
if (require.main === module) {
    main();
}

module.exports = { ComplianceChecker, VIOLATION_PATTERNS };