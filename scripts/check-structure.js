#!/usr/bin/env node
/**
 * 書籍の構成（章・付録・演習・用語集）のあいだの食い違いを検査する。
 *
 * 章番号と章タイトルの正は manuscripts/chapterXX-*.md の1行目、
 * 付録Bの節番号の正は appendix/bXX-* のディレクトリ名とする。
 * ほかの場所（演習ディレクトリ、本文中の「第N章「…」」、付録Bへの参照、用語集、相互参照ガイド）は
 * これに合っているかを検査するか、ここから生成する。
 *
 *   node scripts/check-structure.js            検査する（問題があれば終了コード1）
 *   node scripts/check-structure.js crossref   相互参照ガイド（manuscripts/cross-reference-guide.md）を生成し直す
 */
const fs = require('fs');
const path = require('path');

const ROOT = path.join(__dirname, '..');
const MS = path.join(ROOT, 'manuscripts');
const read = (p) => fs.readFileSync(path.join(ROOT, p), 'utf8');
const exists = (p) => fs.existsSync(path.join(ROOT, p));
const pad = (n) => String(n).padStart(2, '0');
const stripTags = (h) => h.replace(/<small>.*$/, '').replace(/<[^>]+>/g, '').replace(/^#+\s*/, '').trim();

/* ---------- 正となる情報 ---------- */

function chapters() {
  return fs.readdirSync(MS).filter((f) => /^chapter\d+-.*\.md$/.test(f)).sort().map((file) => {
    const n = Number(file.match(/^chapter(\d+)/)[1]);
    const title = stripTags(read(`manuscripts/${file}`).split('\n')[0]).replace(/^\d+章\s*/, '');
    return { n, file, title };
  });
}

/** 章番号の範囲表記（第3-7章、第3〜7章、第9・10章）を展開する */
function chapterNumbers(text) {
  const out = new Set();
  for (const m of text.matchAll(/第([\d・、\-〜～]+)章/g)) {
    for (const part of m[1].split(/[・、]/)) {
      const r = part.split(/[-〜～]/).map(Number);
      if (r.length === 2) for (let i = r[0]; i <= r[1]; i++) out.add(i);
      else if (r[0]) out.add(r[0]);
    }
  }
  return [...out];
}

/** 付録Bの節: 原稿の一覧（appendix-b-index-revised.md）から番号・題名・ディレクトリ・関連章を読む */
function appendixB() {
  const text = read('manuscripts/appendix-b-index-revised.md');
  const blocks = text.split(/^#### /m).slice(1);
  return blocks.map((b) => {
    const [, num, title] = b.match(/^B\.(\d+)\s+(.+)/);
    const dir = (b.match(/appendix\/(b\d\d-[a-z0-9-]+)\//) || [])[1];
    const rel = (b.match(/関連章\n\s*\+\s*(.+)/) || [])[1] || '';
    return { n: Number(num), title: title.trim(), dir, chapters: chapterNumbers(rel) };
  });
}

/** B以外の付録と、関連する章（各付録の「前提知識」「関連する章」の記載に合わせて保守する） */
const OTHER_APPENDICES = [
  { id: 'A', file: 'appendix-a-environment-setup.md', chapters: [1, 2] },
  { id: 'C', file: 'appendix-c-theoretical-foundations.md', chapters: [3, 4, 7, 13, 15] },
  { id: 'D', file: 'appendix-d-integrated-project.md', chapters: [3, 4, 5, 6, 7, 9, 10, 11, 13, 17, 18, 19] },
  { id: 'E', file: 'appendix-e-database-programming.md', chapters: [4, 9, 13, 14] },
  { id: 'F', file: 'appendix-f-java-time-api.md', chapters: [4, 12, 13, 14] },
  { id: 'G', file: 'appendix-g-build-and-deploy.md', chapters: [22, 23] },
];
const appendixTitle = (file) => stripTags(read(`manuscripts/${file}`).split('\n')[0]).replace(/^付録[A-Z]\s*/, '');

/* ---------- 相互参照ガイドの生成 ---------- */

function crossrefGuide() {
  const chs = chapters();
  const bs = appendixB();
  const others = OTHER_APPENDICES.map((a) => ({ ...a, title: appendixTitle(a.file) }));
  const L = [
    '# 章と付録の相互参照ガイド', '',
    '各章を読み終えたあとに、より深く学ぶための付録をまとめました。',
    'このページは`scripts/check-structure.js crossref`で、各章の見出しと付録の一覧から生成しています。', '',
    '付録Bの各節は、GitHubリポジトリの`appendix/`のディレクトリ（b01〜b15）で提供しています。',
    '各章の「よくあるエラーと対処法」の詳しい内容は、付録H（`appendix/h-common-errors/chapters/`）にあります。', '',
    '## 章ごとの関連付録', '',
  ];
  for (const c of chs) {
    const rel = [
      ...bs.filter((b) => b.chapters.includes(c.n)).map((b) => `付録B.${pad(b.n)}「${b.title}」`),
      ...others.filter((a) => a.chapters.includes(c.n)).map((a) => `付録${a.id}「${a.title}」`),
    ];
    L.push(`### 第${c.n}章 ${c.title}`, '');
    if (rel.length) L.push(...rel.map((r) => `- ${r}`));
    else L.push('- 関連する付録はありません');
    L.push('');
  }
  L.push('## 付録Bの節と関連する章', '');
  for (const b of bs) {
    L.push(`- 付録B.${pad(b.n)}「${b.title}」`, `    + ${b.chapters.map((n) => `第${n}章`).join('・') || '関連章なし'}`);
  }
  return L.join('\n') + '\n';
}

/* ---------- 検査 ---------- */

function walk(dir, pred, out = []) {
  if (!exists(dir)) return out;
  for (const e of fs.readdirSync(path.join(ROOT, dir), { withFileTypes: true })) {
    const p = path.join(dir, e.name);
    if (e.isDirectory()) { if (!['node_modules', '.git', 'backup-mermaid'].includes(e.name)) walk(p, pred, out); }
    else if (pred(p)) out.push(p);
  }
  return out;
}

function check() {
  const problems = [];
  const chs = chapters();
  const byN = new Map(chs.map((c) => [c.n, c]));
  const bs = appendixB();
  const byB = new Map(bs.map((b) => [b.n, b]));

  // 1. 演習ディレクトリ: exercises/chapterXX は章番号と一致し、READMEの見出しが章タイトルと一致する
  for (const c of chs) {
    const dir = `exercises/chapter${pad(c.n)}`;
    if (!exists(dir)) { problems.push(`${dir}: 第${c.n}章の演習ディレクトリがない`); continue; }
    const want = `# 第${c.n}章 ${c.title} - 演習課題`;
    const head = exists(`${dir}/README.md`) ? read(`${dir}/README.md`).split('\n')[0] : '';
    if (head !== want) problems.push(`${dir}/README.md: 見出しは「${want}」にする（現在: ${head || 'なし'}）`);
  }
  for (const d of fs.readdirSync(path.join(ROOT, 'exercises')).filter((d) => /^chapter\d+$/.test(d))) {
    if (!byN.has(Number(d.slice(7)))) problems.push(`exercises/${d}: 対応する章がない`);
  }
  for (const c of chs) {
    for (const [i, line] of read(`manuscripts/${c.file}`).split('\n').entries()) {
      for (const m of line.matchAll(/exercises\/chapter(\d+)/g)) {
        if (Number(m[1]) !== c.n) problems.push(`${c.file}:${i + 1}: 演習リンクが exercises/chapter${m[1]} を指している（chapter${pad(c.n)} にする）`);
      }
    }
  }

  // 2. 付録B: 原稿の一覧の番号・ディレクトリ・READMEの見出しが一致する
  const bdirs = fs.readdirSync(path.join(ROOT, 'appendix')).filter((d) => /^b\d\d-/.test(d));
  for (const b of bs) {
    if (!b.dir) { problems.push(`付録B.${pad(b.n)}: 原稿の一覧にディレクトリのURLがない`); continue; }
    if (b.dir.slice(1, 3) !== pad(b.n)) problems.push(`付録B.${pad(b.n)}: 原稿の一覧が appendix/${b.dir} を指している`);
    if (!bdirs.includes(b.dir)) problems.push(`付録B.${pad(b.n)}: appendix/${b.dir} がない`);
    else if (!read(`appendix/${b.dir}/README.md`).startsWith(`# 付録B.${pad(b.n)} `)) problems.push(`appendix/${b.dir}/README.md: 見出しを「# 付録B.${pad(b.n)} …」にする`);
  }
  for (const d of bdirs) if (!byB.has(Number(d.slice(1, 3)))) problems.push(`appendix/${d}: 付録Bの原稿の一覧にない`);

  // 3. 参照: 「付録B.NN」「第N章「タイトル」」が実在し、題名が一致する
  const files = [
    ...walk('manuscripts', (p) => p.endsWith('.md')),
    ...walk('appendix', (p) => p.endsWith('README.md') || p.endsWith('.md')),
    ...walk('exercises', (p) => p.endsWith('.md')),
  ];
  for (const f of files) {
    const lines = read(f).split('\n');
    lines.forEach((line, i) => {
      const where = `${f}:${i + 1}`;
      for (const m of line.matchAll(/付録B\.(\d+)(?:\s*「([^」]+)」)?/g)) {
        const b = byB.get(Number(m[1]));
        if (!b || m[1].length !== 2) { problems.push(`${where}: 付録B.${m[1]} は存在しない（B.01〜B.${pad(bs.length)}）`); continue; }
        if (m[2] && !b.title.includes(m[2]) && !m[2].includes(b.title)) problems.push(`${where}: 付録B.${m[1]}の題名は「${b.title}」（「${m[2]}」と書いている）`);
      }
      for (const m of line.matchAll(/第(\d+)章「([^」]+)」/g)) {
        const c = byN.get(Number(m[1]));
        if (!c) { problems.push(`${where}: 第${m[1]}章は存在しない`); continue; }
        if (!c.title.startsWith(m[2]) && !m[2].startsWith(c.title)) problems.push(`${where}: 第${m[1]}章の題名は「${c.title}」（「${m[2]}」と書いている）`);
      }
    });
  }

  // 4. 用語集: 本文の項目が指す章に、その用語が出てくる
  const texts = new Map(chs.map((c) => [c.n, read(`manuscripts/${c.file}`)]));
  const gl = read('manuscripts/glossary.md');
  const main = gl.slice(0, gl.indexOf('## 付録関連'));
  for (const line of main.split('\n')) {
    const m = line.match(/^- \*\*(.+?)\*\* - (.+)$/);
    if (!m) continue;
    const keys = glossaryKeys(m[1]);
    for (const n of chapterNumbers(m[2])) {
      const t = texts.get(n);
      if (!t) problems.push(`glossary.md: 「${m[1]}」の第${n}章は存在しない`);
      else if (!keys.some((k) => found(k, t))) problems.push(`glossary.md: 「${m[1]}」が第${n}章「${byN.get(n).title}」に出てこない（検索語: ${keys.join('、')}）`);
    }
  }

  // 5. 相互参照ガイドが最新か
  if (read('manuscripts/cross-reference-guide.md') !== crossrefGuide()) {
    problems.push('manuscripts/cross-reference-guide.md: 章や付録の一覧と合っていない（node scripts/check-structure.js crossref で作り直す）');
  }

  if (problems.length) {
    console.log(problems.join('\n'));
    console.log(`\n問題: ${problems.length}件`);
    process.exit(1);
  }
  console.log(`構成の検査: 問題なし（章 ${chs.length}、付録B ${bs.length}節）`);
}

/** 用語集の見出し語から、本文を探す検索語を作る（「Builder パターン」→ Builder パターン、Builderパターン、Builder） */
const GLOSSARY_ALIASES = {
  'Wrapper クラス': ['ボクシング', 'ラッパークラス'],
  'Lambda式': ['ラムダ式'],
  'Collections フレームワーク': ['コレクションフレームワーク'],
  'Factory パターン': ['ファクトリ'],
  'Builder パターン': ['ビルダーパターン'],
  'テキストブロック': ['"""'],
  'オブジェクト直列化': ['直列化', 'シリアライ'],
  'DI (Dependency Injection)': ['依存性の注入', '依存性注入'],
};
function glossaryKeys(term) {
  const keys = new Set([term, term.replace(/\s+/g, ''), ...(GLOSSARY_ALIASES[term] || [])]);
  for (const part of term.replace(/\(\)/g, '').split(/\s*[(（)）]\s*/)) {
    const p = part.trim();
    if (!p) continue;
    keys.add(p);
    keys.add(p.split(/\s+/)[0]);
    const s = p.replace(/\s*(クラス|インターフェイス|キーワード|メソッド|ブロック|フレームワーク|アクセス修飾子|パターン|ファイル|文)$/, '');
    if (s.length >= 2) keys.add(s);
  }
  return [...keys].filter((k) => k.length >= 2);
}
function found(key, text) {
  if (/^[@A-Za-z0-9_.-]+$/.test(key)) {
    const re = new RegExp(`(?<![A-Za-z0-9_])${key.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')}(?![A-Za-z0-9_])`, key.length > 3 ? 'i' : '');
    return re.test(text);
  }
  return text.includes(key);
}

if (require.main === module) {
  const cmd = process.argv[2];
  if (cmd === 'crossref') {
    fs.writeFileSync(path.join(MS, 'cross-reference-guide.md'), crossrefGuide());
    console.log('manuscripts/cross-reference-guide.md を生成しました');
  } else check();
}

module.exports = { chapters, appendixB, crossrefGuide };
