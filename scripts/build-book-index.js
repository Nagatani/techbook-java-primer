#!/usr/bin/env node
/**
 * 書籍の機械的な索引を生成し、内容要約の鮮度を検査するスクリプト
 *
 * 使い方:
 *   node scripts/build-book-index.js                 全章・全付録の索引を docs/book-index/ に生成
 *   node scripts/build-book-index.js chapter09       ファイル名に chapter09 を含む原稿だけ生成
 *   node scripts/build-book-index.js --check         docs/summaries/ の要約が原稿と一致しているか検査
 *
 * 索引はスクリプトで再生成できる事実だけを載せる（見出し・行範囲・サンプルコード番号・章参照など）。
 * 内容の要約は docs/summaries/ に別ファイルで置き、frontmatter の節ハッシュで鮮度を管理する。
 */
const fs = require('fs');
const path = require('path');
const crypto = require('crypto');

const ROOT = path.join(__dirname, '..');
const MANUSCRIPTS = path.join(ROOT, 'manuscripts');
const INDEX_DIR = path.join(ROOT, 'docs', 'book-index');
const SUMMARY_DIR = path.join(ROOT, 'docs', 'summaries');

const stripTags = (s) => s.replace(/<[^>]+>/g, ' ').replace(/\s+/g, ' ').trim();
const hash = (lines) => crypto.createHash('sha256').update(lines.join('\n')).digest('hex').slice(0, 8);

/** 原稿を解析し、H2単位の節（H3を含む）を返す */
function parse(file) {
  const lines = fs.readFileSync(file, 'utf8').replace(/\n$/, '').split('\n');
  let inCode = false;
  let title = '';
  const sections = [];
  const seen = {};
  lines.forEach((line, i) => {
    if (/^\s*(> ?)?```/.test(line)) { inCode = !inCode; return; }
    if (inCode) return;
    const m = line.match(/^(#{1,3}) (.+)$/);
    if (!m) return;
    const level = m[1].length;
    const text = stripTags(m[2]);
    if (level === 1) { title = text; return; }
    if (level === 2) {
      seen[text] = (seen[text] || 0) + 1;
      const key = seen[text] > 1 ? `${text} (${seen[text]})` : text;
      sections.push({ key, start: i + 1, subs: [] });
    } else if (sections.length) {
      sections[sections.length - 1].subs.push({ text, line: i + 1 });
    }
  });
  sections.forEach((s, idx) => {
    s.end = idx + 1 < sections.length ? sections[idx + 1].start - 1 : lines.length;
    const body = lines.slice(s.start - 1, s.end);
    const text = body.join('\n');
    s.hash = hash(body);
    s.listings = [...text.matchAll(/listing-number">\*\*サンプルコード(\d+-\d+)/g)].map((x) => x[1]);
    s.chapterRefs = [...new Set([...text.matchAll(/第\d+(?:-\d+)?章/g)].map((x) => x[0]))];
    s.codeBlocks = Math.floor((text.match(/^\s*(> ?)?```/gm) || []).length / 2);
    s.footnotes = (text.match(/class="footnote"/g) || []).length;
    s.columns = (text.match(/\{\.column-section\}/g) || []).length;
  });
  return { title, lines: lines.length, sections };
}

/** 同じ見出し名が複数回出る場合の警告（重複内容の兆候） */
function duplicateWarnings(sections) {
  return sections.filter((s) => / \(\d+\)$/.test(s.key)).map((s) => s.key);
}

function renderIndex(rel, info) {
  const out = [];
  out.push('<!-- このファイルは scripts/build-book-index.js が自動生成します。手で編集しないでください -->');
  out.push(`# 索引: ${info.title}`, '');
  out.push(`- 原稿: \`${rel}\`（${info.lines}行）`);
  const total = info.sections.reduce((a, s) => a + s.listings.length, 0);
  out.push(`- 節（H2）: ${info.sections.length}、サンプルコード: ${total}`);
  const dups = duplicateWarnings(info.sections);
  if (dups.length) out.push(`- 警告: 同名の節が複数あります（内容重複の可能性）: ${dups.join('、')}`);
  out.push('');
  out.push('| 節 | 行 | ハッシュ | サンプルコード | コード | 脚注 | コラム | 章参照 |');
  out.push('|---|---|---|---|---|---|---|---|');
  for (const s of info.sections) {
    const listings = s.listings.length > 1 ? `${s.listings[0]}〜${s.listings[s.listings.length - 1]}` : (s.listings[0] || '');
    out.push(`| ${s.key} | ${s.start}–${s.end} | ${s.hash} | ${listings} | ${s.codeBlocks} | ${s.footnotes} | ${s.columns} | ${s.chapterRefs.join(' ')} |`);
  }
  out.push('', '## 小見出し（H3）', '');
  for (const s of info.sections) {
    out.push(`- ${s.key}（L${s.start}）`);
    for (const h of s.subs) out.push(`    + ${h.text}（L${h.line}）`);
  }
  return out.join('\n') + '\n';
}

function targetFiles(filters) {
  const all = fs.readdirSync(MANUSCRIPTS).filter((f) => /^(chapter|appendix-[a-h]-).*\.md$/.test(f)).sort();
  return filters.length ? all.filter((f) => filters.some((x) => f.includes(x))) : all;
}

/** 要約ファイルの frontmatter（source と sections の key/hash）を読む */
function readSummaryMeta(file) {
  const text = fs.readFileSync(file, 'utf8');
  const fm = text.match(/^---\n([\s\S]*?)\n---/);
  if (!fm) return null;
  const source = (fm[1].match(/^source:\s*(.+)$/m) || [])[1];
  const sections = [...fm[1].matchAll(/- key:\s*"(.+?)"\s*\n\s*hash:\s*([0-9a-f]+)/g)].map((m) => ({ key: m[1], hash: m[2] }));
  return { source: source && source.trim(), sections };
}

function check() {
  if (!fs.existsSync(SUMMARY_DIR)) { console.log('docs/summaries/ がありません'); return 0; }
  let problems = 0;
  for (const f of fs.readdirSync(SUMMARY_DIR).filter((x) => x.endsWith('.md') && !['README.md', 'overview.md'].includes(x)).sort()) {
    const meta = readSummaryMeta(path.join(SUMMARY_DIR, f));
    if (!meta || !meta.source) { console.log(`✗ ${f}: frontmatter に source がありません`); problems++; continue; }
    const src = path.join(ROOT, meta.source);
    if (!fs.existsSync(src)) { console.log(`✗ ${f}: 原稿 ${meta.source} がありません`); problems++; continue; }
    const current = new Map(parse(src).sections.map((s) => [s.key, s.hash]));
    const recorded = new Map(meta.sections.map((s) => [s.key, s.hash]));
    const stale = [...recorded].filter(([k, h]) => current.has(k) && current.get(k) !== h).map(([k]) => k);
    const removed = [...recorded.keys()].filter((k) => !current.has(k));
    const missing = [...current.keys()].filter((k) => !recorded.has(k));
    if (!stale.length && !removed.length && !missing.length) { console.log(`✓ ${f}: 最新`); continue; }
    problems++;
    console.log(`✗ ${f}`);
    if (stale.length) console.log(`    要約が古い節: ${stale.join('、')}`);
    if (removed.length) console.log(`    原稿から消えた節: ${removed.join('、')}`);
    if (missing.length) console.log(`    要約がない節: ${missing.join('、')}`);
  }
  return problems;
}

const args = process.argv.slice(2);
if (args.includes('--check')) {
  process.exitCode = check() ? 1 : 0;
} else {
  fs.mkdirSync(INDEX_DIR, { recursive: true });
  for (const f of targetFiles(args)) {
    const info = parse(path.join(MANUSCRIPTS, f));
    const out = path.join(INDEX_DIR, f);
    fs.writeFileSync(out, renderIndex(`manuscripts/${f}`, info));
    console.log(`生成: ${path.relative(ROOT, out)}（節 ${info.sections.length}）`);
  }
}
