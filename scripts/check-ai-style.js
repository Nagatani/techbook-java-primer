#!/usr/bin/env node
/**
 * AIが書いた文章らしさを検出し、見直す候補を一覧にする（情報のみ。失敗扱いにしない）
 *
 *   npm run check:style                  全章の集計と、注意の多い段落の上位
 *   npm run check:style -- chapter07     章を絞る（複数可）
 *   npm run check:style -- --all         上位だけでなくすべての候補を出す
 *   npm run check:style -- --kind 判断   分類で絞る（抽象語・定型句・冒頭・締め・判断・三連・単調・学習目標・箇条書き など）
 *   npm run check:style -- --report      docs/analysis/ai-style-report.md に書き出す
 *
 * ルールは docs/guides/ai-style-rules.yml。どの候補も「使ってはいけない」ではなく、
 * 著者の判断・具体例・経験に置き換えられないかを見直すためのもの。
 */
'use strict';

const fs = require('fs');
const path = require('path');
const yaml = require('js-yaml');

const ROOT = path.join(__dirname, '..');
const MANUSCRIPTS = path.join(ROOT, 'manuscripts');
const RULES_FILE = path.join(ROOT, 'docs/guides/ai-style-rules.yml');
const REPORT_FILE = path.join(ROOT, 'docs/analysis/ai-style-report.md');

const args = process.argv.slice(2);
const opt = {
    all: args.includes('--all'),
    report: args.includes('--report'),
    kind: (() => { const i = args.indexOf('--kind'); return i >= 0 ? args[i + 1] : null; })(),
    top: (() => { const i = args.indexOf('--top'); return i >= 0 ? Number(args[i + 1]) : 5; })(),
};
const filters = args.filter((a, i) => !a.startsWith('--') && args[i - 1] !== '--kind' && args[i - 1] !== '--top');

const rules = yaml.load(fs.readFileSync(RULES_FILE, 'utf8'));
const words = (rules.words || []).map(w => ({ ...w, re: new RegExp(w.pattern, 'g') }));
const sentenceRules = (rules.sentences || []).map(s => ({ ...s, re: new RegExp(s.pattern) }));
const objectivesRe = new RegExp(rules.objectives.pattern);
const P = rules.paragraph;
const S = rules.section;
const exceptions = rules.exceptions || [];

// 本文の文字だけを残す（記法・HTML・インラインコードを除く）
function plain(text) {
    return text
        .replace(/<span class="footnote">/g, '（')
        .replace(/<\/span>/g, '）')
        .replace(/<[^>]+>/g, '')
        .replace(/`[^`]*`/g, 'X')
        .replace(/!\[[^\]]*\]\([^)]*\)/g, '')
        .replace(/\[([^\]]*)\]\([^)]*\)/g, '$1')
        .replace(/\*\*([^*]+)\*\*/g, '$1')
        .replace(/\{\.[\w-]+\}/g, '')
        .trim();
}

// 文末の形
const ENDINGS = ['のです', 'でしょう', 'ません', 'ました', 'できます', 'しています', 'ています', 'します', 'ります', 'れます', 'います', 'ます', 'です'];
function ending(sentence) {
    const s = sentence.replace(/[）」』]*。$/, '');
    return ENDINGS.find(e => s.endsWith(e)) || 'その他';
}

function isException(file, text) {
    return exceptions.some(e => e.file === file && text.includes(e.text));
}

// 原稿を段落・リスト・節に分ける
function parse(file) {
    const lines = fs.readFileSync(path.join(MANUSCRIPTS, file), 'utf8').split('\n');
    const blocks = []; // {type: 'para'|'list', lines: [{no, text}], section, h3}
    const sections = []; // {title, line, prose, list}
    let inCode = false;
    let cur = null;
    let section = null;
    let h3 = null;
    const flush = () => { if (cur) blocks.push(cur); cur = null; };
    lines.forEach((raw, i) => {
        const no = i + 1;
        if (/^\s*(```|~~~)/.test(raw)) { inCode = !inCode; flush(); return; }
        if (inCode) return;
        const h = raw.match(/^(#{1,6})\s+(.*)/);
        if (h) {
            flush();
            const title = plain(h[2]);
            if (h[1].length <= 2) { section = { title, line: no, prose: 0, list: 0 }; sections.push(section); h3 = null; }
            else h3 = title;
            return;
        }
        let line = raw.replace(/^>\s?/, '');
        if (line.trim() === '' || /^\s*\|/.test(line) || /^\s*<\/?(div|figure|img|table|p)\b/.test(line) || /^\s*!\[/.test(line)) { flush(); return; }
        const isList = /^\s*([-+*]|\d+\.)\s+/.test(line);
        const type = isList ? 'list' : 'para';
        if (isList) line = line.replace(/^\s*([-+*]|\d+\.)\s+/, '');
        if (!cur || cur.type !== type || isList) {
            // リストは1項目を1ブロックにする。続きの行（字下げ）は同じ項目
            if (!(cur && cur.type === 'list' && !isList && /^\s{2,}/.test(raw))) { flush(); cur = { type, lines: [], section, h3 }; }
        }
        const text = plain(line);
        if (!text) return;
        cur.lines.push({ no, text });
        if (section) section[type === 'list' ? 'list' : 'prose']++;
    });
    flush();
    return { blocks, sections };
}

// 段落を文に分け、文ごとに始まりの行番号を持たせる
function sentencesOf(block) {
    const out = [];
    for (const { no, text } of block.lines) {
        const parts = text.match(/[^。]+。?/g) || [];
        for (const p of parts) if (p.trim()) out.push({ no, text: p.trim() });
    }
    return out;
}

function checkFile(file) {
    const findings = [];
    const add = (no, kind, text, hint, para) => {
        if (isException(file, text)) return;
        findings.push({ file, no, kind, text: text.length > 70 ? text.slice(0, 70) + '…' : text, hint, para });
    };
    const { blocks, sections } = parse(file);
    blocks.forEach((b, bi) => {
        // 「本章の学習目標」の下に「この章で学ぶこと」「この章を始める前に」があるときは前者だけを見る
        const inObjectives = b.h3 ? /この章で学ぶこと/.test(b.h3) : !!(b.section && /学習目標|この章で学ぶこと/.test(b.section.title));
        if (b.type === 'list') {
            if (inObjectives && b.lines.length) {
                const t = b.lines[0].text;
                if (objectivesRe.test(t)) add(b.lines[0].no, '学習目標', t, rules.objectives.hint, bi);
            }
            for (const { no, text } of b.lines) for (const w of words) { w.re.lastIndex = 0; if (w.re.test(text)) add(no, w.kind, text, w.hint, bi); }
            return;
        }
        const ss = sentencesOf(b);
        // 語
        const teikuCount = {};
        for (const s of ss) {
            for (const w of words) {
                w.re.lastIndex = 0;
                const m = s.text.match(w.re);
                if (!m) continue;
                if (w.kind === '定型句') { teikuCount[w.pattern] = (teikuCount[w.pattern] || 0) + m.length; continue; }
                add(s.no, w.kind, s.text, w.hint, bi);
            }
        }
        // 定型句は1回でも出す。ただし「することで」はよく使う言い回しなので、1段落に何度も出たときだけ
        for (const [pat, n] of Object.entries(teikuCount)) {
            const w = words.find(x => x.pattern === pat);
            const s = ss.find(x => new RegExp(pat).test(x.text));
            const frequentOnly = pat === 'することで';
            if (frequentOnly && n < P['max-teiku-per-paragraph']) continue;
            add(s.no, w.kind, s.text, w.hint + (n > 1 ? `（この段落に${n}回）` : ''), bi);
        }
        // 文の形
        ss.forEach((s, si) => {
            for (const r of sentenceRules) {
                if (!r.re.test(s.text)) continue;
                if (r.id === 'generic-closing' && si !== ss.length - 1) continue; // 段落末の文だけ
                if (r.id === 'opening-declaration' && si !== 0) continue; // 段落頭の文だけ
                add(s.no, r.kind, s.text, r.hint, bi);
            }
        });
        // 同じ文末の連続
        let run = 1;
        for (let i = 1; i <= ss.length; i++) {
            const same = i < ss.length && ending(ss[i].text) === ending(ss[i - 1].text) && ending(ss[i].text) !== 'その他';
            if (same) { run++; continue; }
            if (run >= P['same-ending-run']) {
                const first = ss[i - run];
                add(first.no, '単調', `「〜${ending(first.text)}」で終わる文が${run}つ続く: ${first.text}`, '文末か文の長さを変える。2文をつなぐ、問いかける、体言で受けるなど', bi);
            }
            run = 1;
        }
        // 文の長さのばらつき
        if (ss.length >= P['min-sentences-for-cv']) {
            const len = ss.map(s => s.text.length);
            const mean = len.reduce((a, b) => a + b, 0) / len.length;
            const sd = Math.sqrt(len.reduce((a, b) => a + (b - mean) ** 2, 0) / len.length);
            if (sd / mean <= P['max-length-cv']) add(ss[0].no, '単調', `${ss.length}文の長さがほぼ同じ（平均${Math.round(mean)}字）: ${ss[0].text}`, '短い文を混ぜる。要点を一言で言い切る文を置く', bi);
        }
    });
    // 節の箇条書きの割合
    for (const s of sections) {
        const total = s.prose + s.list;
        // 学習目標・まとめ（別に見る）・よくあるエラー（方針で箇条書きにしている）は除く
        if (total < S['min-lines'] || /学習目標|この章で学ぶこと|^まとめ$|よくあるエラー/.test(s.title)) continue;
        const ratio = s.list / total;
        if (ratio >= S['max-list-ratio']) add(s.line, '箇条書き', `節「${s.title}」の${Math.round(ratio * 100)}%が箇条書き`, '理由・比較・因果のある項目は段落に戻す', -1);
    }
    // まとめが箇条書きだけか
    const summary = sections.find(s => s.title === 'まとめ');
    if (summary && summary.list > 0 && summary.prose <= 2) add(summary.line, 'まとめ', `「まとめ」が箇条書き${summary.list}行と前後の${summary.prose}文だけ`, '要点の再掲より、判断の基準や著者の考えを段落で書く', -1);
    return findings;
}

// 実行
const files = fs.readdirSync(MANUSCRIPTS)
    .filter(f => /^chapter\d+.*\.md$/.test(f))
    .filter(f => filters.length === 0 || filters.some(x => f.startsWith(x) || f.includes(x)))
    .sort();

const all = files.flatMap(checkFile).filter(f => !opt.kind || f.kind === opt.kind);
const kinds = [...new Set(all.map(f => f.kind))].sort();

const out = [];
out.push('# AIらしさの見直し候補', '');
out.push(`\`npm run check:style\`の出力（${new Date().toISOString().slice(0, 10)}）。ルールは\`docs/guides/ai-style-rules.yml\`。`);
out.push('どれも見直す候補で、文脈上ふさわしいものは直さなくてよい。', '');
out.push('## 章ごとの件数', '');
out.push(`| 章 | 計 | ${kinds.join(' | ')} |`);
out.push(`|---|---:|${kinds.map(() => '---:').join('|')}|`);
for (const f of files) {
    const fs_ = all.filter(x => x.file === f);
    out.push(`| ${f.match(/^chapter\d+/)[0]} | ${fs_.length} | ${kinds.map(k => fs_.filter(x => x.kind === k).length || '').join(' | ')} |`);
}
const tot = kinds.map(k => all.filter(x => x.kind === k).length);
out.push(`| 計 | ${all.length} | ${tot.join(' | ')} |`, '');

out.push(opt.all ? '## すべての候補' : `## 候補の多い段落（各章の上位${opt.top}）`, '');
for (const f of files) {
    const fs_ = all.filter(x => x.file === f);
    if (!fs_.length) continue;
    let shown;
    if (opt.all) shown = fs_;
    else {
        const byPara = new Map();
        for (const x of fs_) { const k = x.para; if (!byPara.has(k)) byPara.set(k, []); byPara.get(k).push(x); }
        const groups = [...byPara.entries()].filter(([k]) => k !== -1).sort((a, b) => b[1].length - a[1].length).slice(0, opt.top).map(([, v]) => v);
        const structural = byPara.get(-1) || [];
        shown = [...structural, ...groups.flat()];
    }
    out.push(`### ${f}`, '');
    for (const x of shown.sort((a, b) => a.no - b.no)) out.push(`- ${f}:${x.no}【${x.kind}】${x.text}`, `    + ${x.hint}`);
    out.push('');
}

const text = out.join('\n');
if (opt.report) {
    fs.writeFileSync(REPORT_FILE, text);
    console.log(`${path.relative(ROOT, REPORT_FILE)} に書き出しました（候補${all.length}件）`);
} else {
    console.log(text);
}
