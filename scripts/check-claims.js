#!/usr/bin/env node
/**
 * 本文の各章と付録B（Deep Dive）の主張を、JDKで実際に確かめる。
 *
 *   node scripts/check-claims.js                 本文の章と付録Bの節の台帳をすべて検証する
 *   node scripts/check-claims.js --appendix      付録Bの節だけ（npm run check:deep-dive）
 *   node scripts/check-claims.js --chapters      本文の章だけ（npm run check:claims）
 *   node scripts/check-claims.js b04 ch09        指定した節・章だけ（ch09 は chapter09 の意味）
 *   node scripts/check-claims.js --static        Javaを動かさず、台帳と本文の対応・書式・フラグ名だけを検査する（pre-commit）
 *   node scripts/check-claims.js --update        検証の出力を付録BのREADMEの<!-- output: id -->の欄に書き込む
 *   node scripts/check-claims.js --online        出典のURLが開けるか、anchorの文字がページにあるかも確かめる
 *   node scripts/check-claims.js --report        集計を docs/analysis/deep-dive-claims.md（付録B）と manuscript-claims.md（本文）に書く
 *   node scripts/check-claims.js --verbose       検証の出力と、検証していない主張の候補をすべて表示する
 *   node scripts/check-claims.js ch15 --only=id1,id2   指定した主張だけを検証する（各主張の所要時間も表示する）
 *
 * しくみ（詳しくは docs/guides/claim-verification.md）
 *   - 付録Bの節: appendix/bXX-（題名）/claims.yml に主張（claim）と確かめ方（verify）を書き、
 *     README.md などの主張を述べた段落の直後に <!-- claim: id --> を置く。観察用のプログラムは節の verify/
 *   - 本文の章: docs/claims/chapterXX.yml に主張を書き、本文の文の一部を text に引用して対応づける（原稿には印を置かない）。
 *     観察用のプログラムは docs/claims/chapterXX/（probe はこのディレクトリからの相対パス）。本文の文を書き換えて text が見つからなくなるとエラーになる
 *   - 確かめ方の種類
 *       javac   プローブをコンパイルし、コンパイラーの出力（エラー・警告）に expect があるか。fails: true ならコンパイルが失敗すること
 *       javap   プローブをコンパイルし（releaseで--releaseを変えられる）、javap の出力に expect の正規表現があるか（absent はないか）
 *       run     プローブか節のコードを実行し、出力（標準出力と標準エラー出力）に expect があるか
 *       since   プローブが --release N ではコンパイルでき、N-1 ではできないこと（「Java Nから」の主張）
 *       flag    JVMのフラグが実在し、既定値が value と一致するか
 *       manual  機械では確かめられない主張。sources（出典）と checked（確かめた日）を必須にする
 *   - 検証していない主張の候補（注意）: 付録Bは、印のない段落で数値・Javaの版・性能の言い回しを含むもの。
 *     本文は、Javaの版を述べた行で、台帳のどの text も含まないもの
 *   - 解説とプローブに出てくる -XX: のフラグは、台帳に書かなくても実在を確かめる
 */
const fs = require('fs');
const os = require('os');
const path = require('path');
const { spawnSync } = require('child_process');
const yaml = require('js-yaml');

const ROOT = path.resolve(__dirname, '..');
const APPENDIX = path.join(ROOT, 'appendix');
const MANUSCRIPTS = path.join(ROOT, 'manuscripts');
const CLAIMS_DIR = path.join(ROOT, 'docs', 'claims');
const LIB_CACHE = path.join(ROOT, '.cache', 'sample-code-libs');
const REPORTS = {
  appendix: { file: path.join(ROOT, 'docs', 'analysis', 'deep-dive-claims.md'), title: '付録Bの主張の検証状況', cmd: 'npm run check:deep-dive -- --report' },
  chapter: { file: path.join(ROOT, 'docs', 'analysis', 'manuscript-claims.md'), title: '本文の主張の検証状況', cmd: 'npm run check:claims -- --report' },
};
const KINDS = ['javac', 'javap', 'run', 'since', 'flag', 'manual'];
// --release に指定できるもっとも古い版（JDK 25のjavac）
const MIN_RELEASE = 8;
const DEFAULT_TIMEOUT_MS = 120_000;

const args = process.argv.slice(2);
const opt = {
  static: args.includes('--static'),
  update: args.includes('--update'),
  online: args.includes('--online'),
  report: args.includes('--report'),
  verbose: args.includes('--verbose'),
  // --only id[,id...] で指定した主張だけを検証する
  only: (args.find((a) => a.startsWith('--only=')) || '').slice('--only='.length).split(',').filter(Boolean),
  filters: args.filter((a) => !a.startsWith('--')).map((f) => f.replace(/^chapter(\d\d)$/, 'ch$1')),
  appendix: args.includes('--appendix'),
  chapters: args.includes('--chapters'),
};
// どちらも指定しなければ両方。節・章を指定したときは、その種類だけ
if (!opt.appendix && !opt.chapters) {
  const hasB = opt.filters.some((f) => /^b\d/.test(f));
  const hasCh = opt.filters.some((f) => /^ch\d/.test(f));
  opt.appendix = hasB || !hasCh;
  opt.chapters = hasCh || !hasB;
}

// ---------------------------------------------------------------- 節と文書

/**
 * 検査の単位（付録Bの節と本文の章）。
 *   kind: 'appendix' | 'chapter'、dir: プローブのパスの基準、ledgerFile: 台帳、docs: 主張を書いた文書
 */
function sections() {
  const out = [];
  const pick = (id) => !opt.filters.length || opt.filters.some((f) => id.startsWith(f) || f.startsWith(id));
  if (opt.appendix) {
    for (const d of fs.readdirSync(APPENDIX).filter((x) => /^b\d\d-/.test(x)).sort()) {
      const id = d.slice(0, 3);
      if (!pick(id) && !opt.filters.some((f) => d.startsWith(f))) continue;
      const dir = path.join(APPENDIX, d);
      out.push({ kind: 'appendix', name: d, id, dir, ledgerFile: path.join(dir, 'claims.yml') });
    }
  }
  if (opt.chapters) {
    for (const f of fs.readdirSync(MANUSCRIPTS).filter((x) => /^chapter\d\d-.*\.md$/.test(x)).sort()) {
      const nn = f.slice(7, 9);
      const id = `ch${nn}`;
      if (!pick(id)) continue;
      out.push({ kind: 'chapter', name: `chapter${nn}`, id, dir: path.join(CLAIMS_DIR, `chapter${nn}`),
        ledgerFile: path.join(CLAIMS_DIR, `chapter${nn}.yml`), manuscript: path.join(MANUSCRIPTS, f) });
    }
  }
  return out;
}

/** 節の解説の文書（README.mdと、docs/・examples/の.md）。target/・out/は除く */
function docsOf(sec) {
  if (sec.kind === 'chapter') return [sec.manuscript];
  const out = [];
  const walk = (dir) => {
    for (const e of fs.readdirSync(dir, { withFileTypes: true })) {
      if (e.name.startsWith('.') || ['target', 'build', 'out', 'node_modules'].includes(e.name)) continue;
      const p = path.join(dir, e.name);
      if (e.isDirectory()) walk(p);
      else if (e.name.endsWith('.md')) out.push(p);
    }
  };
  walk(sec.dir);
  return out.sort();
}

function javaFilesOf(sec) {
  const out = [];
  const base = sec.dir;
  if (!fs.existsSync(base)) return out;
  const walk = (dir) => {
    for (const e of fs.readdirSync(dir, { withFileTypes: true })) {
      if (e.name.startsWith('.') || ['target', 'build', 'out'].includes(e.name)) continue;
      const p = path.join(dir, e.name);
      if (e.isDirectory()) walk(p);
      else if (/\.(java|xml|gradle|properties)$/.test(e.name)) out.push(p);
    }
  };
  walk(base);
  return out;
}

/**
 * Markdownを「まとまり」（段落・リスト項目とその子・表・見出し・コードブロック）に分ける。
 * 戻り値は [{ type, line, text }]（lineは1始まり）
 */
function blocks(md) {
  const lines = md.split('\n');
  const out = [];
  let cur = null;
  let fence = null;
  const flush = () => { if (cur) out.push(cur); cur = null; };
  lines.forEach((l, i) => {
    const n = i + 1;
    if (fence) {
      cur.text += '\n' + l;
      if (l.trim().startsWith(fence)) { fence = null; flush(); }
      return;
    }
    const f = l.match(/^\s*(```+|~~~+)/);
    if (f) { flush(); fence = f[1]; cur = { type: 'code', line: n, text: l }; return; }
    if (!l.trim()) { if (cur && cur.type !== 'list') flush(); return; }
    if (/^#{1,6}\s/.test(l)) { flush(); out.push({ type: 'heading', line: n, text: l }); return; }
    if (/^\s*<!--.*-->\s*$/.test(l) && cur) { cur.text += '\n' + l; return; }
    if (/^(\s*)([-*+]|\d+\.)\s/.test(l) && !/^\s/.test(l)) { flush(); cur = { type: 'list', line: n, text: l }; return; }
    if (/^\|/.test(l)) { if (!cur || cur.type !== 'table') { flush(); cur = { type: 'table', line: n, text: l }; } else cur.text += '\n' + l; return; }
    if (cur && (cur.type === 'list' ? /^\s/.test(l) || /^\s*<!--/.test(l) : cur.type === 'para')) { cur.text += '\n' + l; return; }
    if (cur && cur.type === 'list' && !/^\s/.test(l)) flush();
    if (!cur) cur = { type: 'para', line: n, text: l };
    else cur.text += '\n' + l;
  });
  flush();
  // 印だけの行（空行のあとに置いたもの）は、直前のまとまりに付ける
  const merged = [];
  for (const b of out) {
    if (b.type === 'para' && /^(\s*<!--\s*claim:[^>]*-->\s*\n?)+$/.test(b.text) && merged.length) {
      merged[merged.length - 1].text += '\n' + b.text;
    } else merged.push(b);
  }
  return merged;
}

const markerIds = (text) => [...text.replace(/`[^`\n]*`/g, '').matchAll(/<!--\s*claim:\s*([^>]+?)\s*-->/g)]
  .flatMap((m) => m[1].split(/[,\s]+/).filter(Boolean));

// 検証していない主張の候補（数値・フラグ・版・性能の言い回し）
const RISKY = [
  { re: /\d[\d,.]*\s*(倍|ns|μs|ms|ミリ秒|マイクロ秒|ナノ秒|秒|バイト|KB|MB|GB|ビット|％|%|段|サイクル)/, why: '数値' },
  { re: /(Java|JDK)\s*\d+(以降|から|で(導入|追加|正式))/, why: 'Javaの版' },
  { re: /(高速|低速|速く|遅く|速い|遅い|オーバーヘッド)/, why: '性能' },
];

function riskyReasons(text) {
  const prose = text.replace(/`[^`]*`/g, '').replace(/<!--[\s\S]*?-->/g, '');
  return RISKY.filter((r) => r.re.test(prose)).map((r) => r.why);
}

// ---------------------------------------------------------------- 台帳

function loadLedger(sec) {
  const file = sec.ledgerFile;
  if (!fs.existsSync(file)) return null;
  let data;
  try {
    data = yaml.load(fs.readFileSync(file, 'utf8'), { schema: yaml.JSON_SCHEMA }) || {};
  } catch (e) {
    // YAMLの書き間違い（値の中の「: 」など）は、台帳と行だけを示して止める
    const line = e.mark ? e.mark.line + 1 : '?';
    console.log(`台帳 ${path.relative(ROOT, file)} の${line}行目をYAMLとして読めない: ${e.reason || e.message}\n（値に「: 」や「#」を含むときは引用符で囲む）`);
    process.exit(1);
  }
  // defaults: 各主張のverifyに既定で加える項目（libsなど。主張の側に書いたものが優先）
  const claims = (data.claims || []).map((c) => (c.verify && data.defaults && ['javac', 'javap', 'run', 'since'].includes(c.verify.kind)
    ? { ...c, verify: { ...data.defaults, ...c.verify } } : c));
  return { file, jdk: data.jdk, claims, removedFlags: data.removedFlags || [] };
}

function validateLedger(sec, ledger, problems) {
  const seen = new Set();
  const rel = path.relative(ROOT, ledger.file);
  for (const c of ledger.claims) {
    const where = `${rel}: ${c.id || '(idなし)'}`;
    if (!c.id || !/^[a-z0-9][a-z0-9-]*$/.test(c.id)) problems.push({ level: 'error', where, msg: 'idは英小文字・数字・ハイフンで書く' });
    if (seen.has(c.id)) problems.push({ level: 'error', where, msg: 'idが重複している' });
    seen.add(c.id);
    if (!c.claim) problems.push({ level: 'error', where, msg: 'claim（主張の文）がない' });
    const v = c.verify || {};
    if (!KINDS.includes(v.kind)) { problems.push({ level: 'error', where, msg: `verify.kindは${KINDS.join('・')}のどれか` }); continue; }
    if (v.kind === 'manual') {
      if (!c.sources || !c.sources.length) problems.push({ level: 'error', where, msg: 'manualの主張にはsources（出典）が要る' });
      if (!v.checked || !/^\d{4}-\d{2}-\d{2}$/.test(String(v.checked))) problems.push({ level: 'error', where, msg: 'manualの主張にはverify.checked（確かめた日、YYYY-MM-DD）が要る' });
    }
    if (v.kind === 'javac' && !v.probe) problems.push({ level: 'error', where, msg: 'javacにはprobeが要る' });
    if (v.kind === 'javap' && (!v.probe || !v.class)) problems.push({ level: 'error', where, msg: 'javapにはprobeとclassが要る' });
    if (v.kind === 'run' && !v.probe && !v.main) problems.push({ level: 'error', where, msg: 'runにはprobeかmainが要る' });
    if (v.kind === 'since' && (!v.probe || !v.version)) problems.push({ level: 'error', where, msg: 'sinceにはprobeとversionが要る' });
    if (v.kind === 'since' && Number(v.version) < MIN_RELEASE && !(c.sources && c.sources.length)) problems.push({ level: 'error', where, msg: `Java ${MIN_RELEASE}より前の版のsinceは、N-1でコンパイルできないことを確かめられないのでsources（出典）が要る` });
    if (sec.kind === 'chapter' && !c.text && !c.unreferenced) problems.push({ level: 'error', where, msg: '本文の主張にはtext（本文の文の一部の引用）が要る' });
    if (v.kind === 'flag' && !v.flag) problems.push({ level: 'error', where, msg: 'flagにはflagが要る' });
    if (['javac', 'javap', 'run'].includes(v.kind) && !(v.expect || v.absent)) problems.push({ level: 'error', where, msg: 'expectかabsentが要る' });
    for (const p of [].concat(v.probe || [])) {
      if (!fs.existsSync(path.join(sec.dir, p))) problems.push({ level: 'error', where, msg: `プローブ ${p} がない` });
    }
  }
}

// ---------------------------------------------------------------- Java

function libJars(coords = []) {
  return coords.map((coord) => {
    const [g, a, v] = coord.split(':');
    const jar = path.join(LIB_CACHE, `${a}-${v}.jar`);
    if (!fs.existsSync(jar)) {
      fs.mkdirSync(LIB_CACHE, { recursive: true });
      const url = `https://repo1.maven.org/maven2/${g.replace(/\./g, '/')}/${a}/${v}/${a}-${v}.jar`;
      const r = spawnSync('curl', ['-sfL', '-o', jar, url]);
      if (r.status !== 0) { fs.rmSync(jar, { force: true }); throw new Error(`${coord} を取得できない（${url}）`); }
    }
    return jar;
  });
}

let flagTable = null;
/** JVMのフラグの一覧（名前 → { value, kind }）。診断用・実験用も含める */
function flags() {
  if (flagTable) return flagTable;
  const r = spawnSync('java', ['-XX:+UnlockDiagnosticVMOptions', '-XX:+UnlockExperimentalVMOptions', '-XX:+PrintFlagsFinal', '-version'], { encoding: 'utf8' });
  flagTable = new Map();
  if (r.status !== 0 || !r.stdout) return null;
  for (const l of r.stdout.split('\n')) {
    const m = l.match(/^\s*\S+\s+(\w+)\s+:?=\s+(\S*)\s+\{([^}]*)\}/);
    if (m) flagTable.set(m[1], { value: m[2], kind: m[3].trim() });
  }
  return flagTable;
}

/** 解説・プローブ・コードに出てくる -XX: のフラグが実在するかを確かめる */
function checkFlagNames(sec, problems, removed = []) {
  const table = flags();
  if (!table) { problems.push({ level: 'warn', where: sec.name, msg: 'javaが見つからないため、フラグ名の検査を省いた' }); return; }
  const files = [...docsOf(sec), ...javaFilesOf(sec)];
  for (const f of files) {
    const text = fs.readFileSync(f, 'utf8');
    for (const m of text.matchAll(/-XX:[+-]?([A-Za-z]\w+)(=)?/g)) {
      const name = m[1];
      // 台帳のremovedFlagsに書いたフラグは、削除されたことを説明するために書いてよい
      if (table.has(name) || removed.includes(name)) continue;
      const line = text.slice(0, m.index).split('\n').length;
      problems.push({ level: 'error', where: `${path.relative(ROOT, f)}:${line}`, msg: `JVMのフラグ -XX:${name} はJDK 25にない（PrintFlagsFinalに出ない）` });
    }
  }
}

const compiled = new Map();
/** プローブ（または節の src/main/java）を一時ディレクトリにコンパイルする。キャッシュする */
function compileFor(sec, v, tmp) {
  const sources = v.probe ? [].concat(v.probe).map((p) => path.join(sec.dir, p)) : srcFiles(sec);
  const libs = libJars(v.libs || []);
  const release = String(v.release || 25);
  const key = JSON.stringify([sources, libs, v.javac || [], release]);
  if (compiled.has(key)) return compiled.get(key);
  const out = path.join(tmp, `c${compiled.size}`);
  fs.mkdirSync(out, { recursive: true });
  const proc = (v.javac || []).some((o) => o.startsWith('-proc:')) ? [] : ['-proc:none'];
  // release: none は --release を付けず実行中のJDKでコンパイルする（--add-exports と併用するとき）
  const rel = release === 'none' ? [] : ['--release', release];
  const r = spawnSync('javac', ['-J-Duser.language=en', ...rel, '-Xlint:-options', '-encoding', 'UTF-8', ...proc, ...(v.javac || []),
    ...(libs.length ? ['-cp', libs.join(path.delimiter)] : []), '-d', out, ...sources], { encoding: 'utf8' });
  // javacのメッセージのファイル名は、節のディレクトリからの相対パスにする（READMEに載せても環境によらないように）
  const msg = (r.stdout + r.stderr).trim().split(sec.dir + path.sep).join('');
  const res = { ok: r.status === 0, out, cp: [out, ...libs].join(path.delimiter), msg };
  compiled.set(key, res);
  return res;
}

function srcFiles(sec) {
  const base = path.join(sec.dir, 'src', 'main', 'java');
  const out = [];
  const walk = (d) => { for (const e of fs.readdirSync(d, { withFileTypes: true })) { const p = path.join(d, e.name); if (e.isDirectory()) walk(p); else if (e.name.endsWith('.java')) out.push(p); } };
  if (fs.existsSync(base)) walk(base);
  return out;
}

const asList = (x) => (x == null ? [] : [].concat(x));

function matchAll(text, v) {
  const missing = asList(v.expect).filter((re) => !new RegExp(re, 'm').test(text));
  const present = asList(v.absent).filter((re) => new RegExp(re, 'm').test(text));
  return { ok: !missing.length && !present.length, missing, present };
}

function verifyClaim(sec, c, tmp) {
  const v = c.verify;
  if (v.kind === 'manual') return { status: 'manual' };
  if (v.kind === 'flag') {
    const f = (flags() || new Map()).get(v.flag);
    if (!f) return { status: 'fail', msg: `フラグ ${v.flag} がない` };
    if (v.value != null && String(v.value) !== f.value) return { status: 'fail', msg: `${v.flag} の既定値は ${f.value}（台帳は ${v.value}）`, output: `${v.flag} = ${f.value} {${f.kind}}` };
    return { status: 'pass', output: `${v.flag} = ${f.value} {${f.kind}}` };
  }
  if (v.kind === 'since') {
    const n = Number(v.version);
    // Java 8より前の版（7以下）は--releaseに指定できないので、8で通ることだけを確かめる（出典を必須にする）
    const rel = Math.max(n, MIN_RELEASE);
    const ok = compileFor(sec, { ...v, release: rel }, tmp);
    if (!ok.ok) return { status: 'fail', msg: `--release ${rel} でコンパイルできない\n${ok.msg}` };
    if (n - 1 < MIN_RELEASE) return { status: 'pass', output: `--release ${rel} でコンパイルできた（${n - 1}は--releaseに指定できないので、版は出典で確かめる）` };
    const ng = compileFor(sec, { ...v, release: n - 1 }, tmp);
    if (ng.ok) return { status: 'fail', msg: `--release ${n - 1} でもコンパイルできた（Java ${n}より前からある）` };
    const m = matchAll(ng.msg, v);
    if (!m.ok) return { status: 'fail', msg: `--release ${n - 1} のエラーが想定と違う\n${m.missing.map((re) => `/${re}/ がない`).join('\n')}`, output: ng.msg };
    return { status: 'pass', output: `--release ${n} でコンパイルでき、${n - 1} ではできない\n${ng.msg}` };
  }
  const c1 = compileFor(sec, v, tmp);
  if (v.kind === 'javac') {
    if (v.fails && c1.ok) return { status: 'fail', msg: 'コンパイルが通った（失敗するはず）' };
    if (!v.fails && !c1.ok) return { status: 'fail', msg: `コンパイルできない\n${c1.msg}` };
    const m = matchAll(c1.msg, v);
    if (!m.ok) return { status: 'fail', msg: [...m.missing.map((re) => `出力に /${re}/ がない`), ...m.present.map((re) => `出力に /${re}/ がある（ないはず）`)].join('\n'), output: c1.msg };
    return { status: 'pass', output: c1.msg };
  }
  if (!c1.ok) return { status: 'fail', msg: `コンパイルできない\n${c1.msg}` };
  let text;
  const attempts = v.attempts || 1;
  let last;
  for (let i = 0; i < attempts; i++) {
    let r;
    if (v.kind === 'javap') {
      r = spawnSync('javap', [...(v.options || ['-c', '-p']), '-cp', c1.cp, v.class], { encoding: 'utf8' });
    } else {
      const main = v.main || v.class || path.basename([].concat(v.probe)[0], '.java');
      // resources: 節からの相対パスのディレクトリをクラスパスに加える（META-INF/services など）
      const cp = [c1.cp, ...asList(v.resources).map((d) => path.join(sec.dir, d))].join(path.delimiter);
      r = spawnSync('java', ['-Duser.language=en', ...(v.jvm || []), '-cp', cp, main, ...(v.args || []).map(String)],
        { encoding: 'utf8', timeout: v.timeout ? v.timeout * 1000 : DEFAULT_TIMEOUT_MS, maxBuffer: 256 * 1024 * 1024, cwd: tmp });
      if (r.error && r.error.code === 'ETIMEDOUT') return { status: 'fail', msg: '時間内に終わらない（それまでの出力を下に示す）', output: (r.stdout || '') + (r.stderr || '') };
    }
    text = (r.stdout || '') + (r.stderr || '');
    last = matchAll(text, v);
    if (last.ok) break;
  }
  if (!last.ok) {
    const msg = [
      ...last.missing.map((re) => `出力に /${re}/ がない`),
      ...last.present.map((re) => `出力に /${re}/ がある（ないはず）`),
    ].join('\n');
    return { status: 'fail', msg, output: text };
  }
  return { status: 'pass', output: text };
}

/** 出力のうちREADMEに載せる行を選ぶ（show: 正規表現の配列。なければexpectに当たった行） */
function shownOutput(c, output) {
  const v = c.verify;
  const pats = asList(v.show || v.expect).map((re) => new RegExp(re));
  let all = output.split('\n');
  // range: { from, to } で、fromに当たる行からtoに当たる行（または空行）までに絞る（javapの1メソッドだけなど）
  if (v.range) {
    const start = all.findIndex((l) => new RegExp(v.range.from).test(l));
    if (start >= 0) {
      const endRe = v.range.to ? new RegExp(v.range.to) : /^\s*$/;
      let end = all.findIndex((l, i) => i > start && endRe.test(l));
      if (end < 0) end = all.length;
      all = all.slice(start, v.range.to ? end + 1 : end);
    }
  }
  let lines = all.filter((l) => pats.some((p) => p.test(l)));
  // 字下げと連続する空白を詰める（PrintInliningなどは呼び出しの深さで字下げが変わる）
  // keepIndent: true なら字下げと桁を残す（木の表示やJOLの表など）
  lines = v.keepIndent ? lines.map((l) => l.replace(/\s+$/, '')) : lines.map((l) => l.trim().replace(/\s{2,}/g, ' '));
  for (const [from, to] of Object.entries(v.normalize || {})) lines = lines.map((l) => l.replace(new RegExp(from, 'g'), to));
  // JITのログは実行のたびに順序が変わるので、sort: trueで並べ、同じ行をまとめる
  if (v.sort) lines = [...new Set(lines)].sort();
  return lines.join('\n').replace(/\s+$/, '');
}

/** <!-- output: id --> 〜 <!-- /output --> の間のコードブロックを書き換える */
function updateOutputs(docFile, outputs) {
  const text = fs.readFileSync(docFile, 'utf8');
  const next = text.replace(/(<!--\s*output:\s*([\w-]+)\s*-->\n)[\s\S]*?(<!--\s*\/output\s*-->)/g, (all, head, id, tail) => {
    if (!outputs.has(id)) return all;
    return `${head}\`\`\`\n${outputs.get(id)}\n\`\`\`\n${tail}`;
  });
  if (next !== text) { fs.writeFileSync(docFile, next); return true; }
  return false;
}

// ---------------------------------------------------------------- 出典

function checkSources(sec, ledger, problems) {
  for (const c of ledger.claims) {
    for (const s of c.sources || []) {
      if (typeof s !== 'object' || !s.url) continue;
      const r = spawnSync('curl', ['-sfL', '--max-time', '60', '--retry', '2', '--retry-all-errors', '-A', 'Mozilla/5.0 (book-verification)', s.url], { encoding: 'utf8', maxBuffer: 64 * 1024 * 1024 });
      if (r.status !== 0) { problems.push({ level: 'error', where: `${sec.id}: ${c.id}`, msg: `出典 ${s.url} を開けない` }); continue; }
      if (s.anchor) {
        // タグを空白にした文字列と、タグを取り除いただけの文字列の両方で探す（リンクやコードをまたぐ文のため）
        const decode = (t) => t
          .replace(/&nbsp;/g, ' ').replace(/&#(\d+);/g, (m, n) => String.fromCodePoint(+n)).replace(/&#x([0-9a-f]+);/gi, (m, n) => String.fromCodePoint(parseInt(n, 16)))
          .replace(/&(thinsp|ensp|emsp);/g, ' ').replace(/&mdash;/g, '—').replace(/&ndash;/g, '–').replace(/&hellip;/g, '…').replace(/&sect;/g, '§').replace(/&amp;/g, '&').replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&quot;/g, '"')
          .replace(/[\s\u00a0\u2009]+/g, ' ');
        const texts = [r.stdout.replace(/<[^>]+>/g, ' '), r.stdout.replace(/<[^>]+>/g, '')];
        const found = texts.some((t) => decode(t).includes(s.anchor) || t.replace(/\s+/g, ' ').includes(s.anchor));
        if (!found) problems.push({ level: 'error', where: `${sec.id}: ${c.id}`, msg: `出典 ${s.url} に「${s.anchor}」がない` });
      }
    }
  }
}

// ---------------------------------------------------------------- 本文の章

/** 本文の文を照合しやすくする（コードブロックを除き、タグ・インラインコードの記号・強調を外し、行をつなぐ） */
function normalizeProse(text) {
  return text
    .replace(/^(```|~~~)[\s\S]*?^\1\s*$/gm, '')
    // 原稿に使うHTMLのタグだけを外す（<local1> や List<String> の山括弧は残す）
    .replace(/<\/?(span|div|img|br|b|i|small|sup|sub|a|wbr|em|strong|code|p|figure|figcaption)\b[^>]*>/g, '')
    .replace(/[`*]/g, '')
    .replace(/\n/g, '')
    .replace(/\s+/g, ' ');
}
const normalizeQuote = (t) => String(t).replace(/[`*]/g, '').replace(/\s+/g, ' ').trim();

// 本文で「Javaの版」を述べた行（検証していない主張の候補を探す）
const VERSION_RE = /(Java|JDK)\s?\d+(\.\d+)?\s*(以降|から|で(導入|追加|正式|加わ|プレビュー|非推奨|削除)|より前|まで|では)/;

/**
 * 本文の章: 台帳の text が本文にあるかを確かめ、版を述べた行のうち台帳のどの text も含まないものを候補にする。
 * ledger が null（台帳がまだない章）でも候補だけは数える
 */
function chapterAnchors(sec, ledger, row, problems) {
  const md = fs.readFileSync(sec.manuscript, 'utf8');
  const prose = normalizeProse(md);
  const quotes = [];
  for (const c of (ledger ? ledger.claims : [])) {
    for (const t of asList(c.text)) {
      const q = normalizeQuote(t);
      quotes.push(q);
      if (!prose.includes(q)) {
        problems.push({ level: 'error', where: `${path.relative(ROOT, ledger.file)}: ${c.id}`,
          msg: `本文（${path.relative(ROOT, sec.manuscript)}）に「${q.slice(0, 60)}」が見つからない（本文を書き換えたなら、主張を見直してtextを直す）` });
      }
    }
  }
  let fence = false;
  md.split('\n').forEach((line, i) => {
    if (/^\s*(```|~~~)/.test(line)) { fence = !fence; return; }
    if (fence || /^#/.test(line)) return;
    const plain = normalizeProse(line);
    if (!VERSION_RE.test(plain)) return;
    if (quotes.some((q) => plain.includes(q) || q.includes(plain.trim()))) return;
    row.uncovered.push({ where: `${path.relative(ROOT, sec.manuscript)}:${i + 1}`, why: ['Javaの版'], text: plain.trim().slice(0, 70) });
  });
}

// ---------------------------------------------------------------- 本体

function main() {
  const problems = [];
  const summary = [];
  const tmp = fs.mkdtempSync(path.join(os.tmpdir(), 'deep-dive-'));
  try {
    for (const sec of sections()) {
      const ledger = loadLedger(sec);
      const docs = docsOf(sec);
      const row = { sec, claims: 0, pass: 0, fail: 0, manual: 0, simplified: 0, markers: 0, uncovered: [] };
      summary.push(row);
      const markersAt = new Map();
      if (sec.kind === 'chapter') {
        chapterAnchors(sec, ledger, row, problems);
      } else for (const f of docs) {
        const md = fs.readFileSync(f, 'utf8');
        for (const b of blocks(md)) {
          const ids = markerIds(b.text);
          for (const id of ids) {
            if (!markersAt.has(id)) markersAt.set(id, []);
            markersAt.get(id).push(`${path.relative(ROOT, f)}:${b.line}`);
          }
          if (ids.length || ['code', 'heading'].includes(b.type)) continue;
          const why = riskyReasons(b.text);
          if (why.length) row.uncovered.push({ where: `${path.relative(ROOT, f)}:${b.line}`, why, text: b.text.split('\n')[0].slice(0, 70) });
        }
      }
      row.markers = markersAt.size;
      checkFlagNames(sec, problems, ledger ? ledger.removedFlags : []);
      if (!ledger) {
        if (sec.kind === 'appendix' && markersAt.size) problems.push({ level: 'error', where: sec.name, msg: '印（<!-- claim: -->）があるのにclaims.ymlがない' });
        continue;
      }
      validateLedger(sec, ledger, problems);
      const ids = new Set(ledger.claims.map((c) => c.id));
      for (const [id, wheres] of markersAt) {
        if (!ids.has(id)) problems.push({ level: 'error', where: wheres[0], msg: `印のid「${id}」がclaims.ymlにない` });
      }
      for (const c of ledger.claims) {
        if (sec.kind === 'appendix' && !markersAt.has(c.id) && !c.unreferenced) problems.push({ level: 'error', where: `${path.relative(ROOT, ledger.file)}: ${c.id}`, msg: '解説のどこにも印がない（解説から消した主張なら台帳からも消す）' });
      }
      // 手で確かめてから1年を過ぎた主張は、版や公開の状況が変わっていないかを確かめ直す（注意）
      for (const c of ledger.claims) {
        const d = c.verify && c.verify.kind === 'manual' && Date.parse(c.verify.checked);
        if (d && Date.now() - d > 365 * 24 * 3600 * 1000) row.stale = (row.stale || []).concat(c.id);
      }
      row.claims = ledger.claims.length;
      row.manual = ledger.claims.filter((c) => c.verify && c.verify.kind === 'manual').length;
      row.simplified = ledger.claims.filter((c) => c.simplified).length;
      for (const c of ledger.claims) {
        if (c.simplified && !c.note) problems.push({ level: 'error', where: `${path.relative(ROOT, ledger.file)}: ${c.id}`, msg: 'simplified: true の主張には、正確な事実をnoteに書く' });
      }
      if (opt.online) checkSources(sec, ledger, problems);
      if (opt.static) continue;

      const outputs = new Map();
      console.log(`\n## ${sec.name}（主張 ${ledger.claims.length}件）`);
      for (const c of ledger.claims) {
        if (!c.verify || !KINDS.includes(c.verify.kind)) continue;
        if (opt.only.length && !opt.only.includes(c.id)) continue;
        const t0 = Date.now();
        const r = verifyClaim(sec, c, tmp);
        const sec_ = ((Date.now() - t0) / 1000).toFixed(1);
        if (r.status === 'manual') { console.log(`  - ${c.id}: 手で確認（${c.verify.checked}）`); continue; }
        if (r.status === 'pass') {
          row.pass++;
          console.log(`  ✓ ${c.id}${Number(sec_) >= 5 || opt.only.length ? `（${sec_}秒）` : ''}`);
          if (opt.verbose && r.output) console.log(r.output.split('\n').map((l) => '      ' + l).join('\n'));
          if (r.output) outputs.set(c.id, shownOutput(c, r.output));
        } else {
          row.fail++;
          console.log(`  ✗ ${c.id}: ${r.msg.split('\n')[0]}`);
          problems.push({ level: 'error', where: `${sec.id}: ${c.id}`, msg: `${c.claim}\n${r.msg}${r.output ? '\n--- 出力の先頭 ---\n' + r.output.split('\n').slice(0, 15).join('\n') : ''}` });
        }
      }
      if (opt.update && sec.kind === 'appendix') {
        for (const f of docs) if (updateOutputs(f, outputs)) console.log(`  → ${path.relative(ROOT, f)} の出力を更新した`);
      }
    }
  } finally {
    fs.rmSync(tmp, { recursive: true, force: true });
  }

  // 集計
  console.log('\n| 節・章 | 主張 | 機械で確認 | 手で確認 | 簡略化 | 失敗 | 検証していない候補 |');
  console.log('|---|---|---|---|---|---|---|');
  for (const r of summary) {
    const auto = opt.static ? '-' : r.pass;
    console.log(`| ${r.sec.id} | ${r.claims} | ${auto} | ${r.manual} | ${r.simplified} | ${opt.static ? '-' : r.fail} | ${r.uncovered.length} |`);
  }
  const uncovered = summary.reduce((n, r) => n + r.uncovered.length, 0);
  if (uncovered && (opt.verbose || opt.filters.length)) {
    console.log('\n注意: 検証していない主張の候補（数値・フラグ・版・性能の言い回しを含み、印のないまとまり）');
    for (const r of summary) for (const u of r.uncovered) console.log(`  ${u.where} [${u.why.join('・')}] ${u.text}`);
  } else if (uncovered) {
    console.log(`\n注意: 検証していない主張の候補が${uncovered}件（節を指定するか--verboseで一覧を表示）`);
  }
  const stale = summary.flatMap((r) => (r.stale || []).map((id) => `${r.sec.id}: ${id}`));
  if (stale.length) console.log(`\n注意: 手で確かめてから1年を過ぎた主張が${stale.length}件（出典を読み直し、checkedを更新する）\n  ${stale.join('\n  ')}`);
  if (opt.report) writeReport(summary);

  const errors = problems.filter((p) => p.level === 'error');
  if (errors.length) {
    console.log(`\n問題: ${errors.length}件`);
    for (const p of errors) console.log(`\n- ${p.where}\n  ${p.msg.split('\n').join('\n  ')}`);
    process.exit(1);
  }
  console.log('\n問題なし');
}

function writeReport(summary) {
  for (const kind of ['appendix', 'chapter']) {
    const rows = summary.filter((r) => r.sec.kind === kind);
    if (!rows.length) continue;
    const rp = REPORTS[kind];
    const today = new Date().toISOString().slice(0, 10);
    const lines = [
      `# ${rp.title}`,
      '',
      `\`${rp.cmd}\`が生成する（${today}）。手で書き換えない。しくみは\`docs/guides/claim-verification.md\`。`,
      '',
      `| ${kind === 'chapter' ? '章' : '節'} | 主張 | 機械で確認 | 手で確認 | 簡略化 | 失敗 | 検証していない候補 |`,
      '|---|---|---|---|---|---|---|',
      ...rows.map((r) => `| ${r.sec.name} | ${r.claims} | ${opt.static ? '-' : r.pass} | ${r.manual} | ${r.simplified} | ${opt.static ? '-' : r.fail} | ${r.uncovered.length} |`),
      '',
      '## 検証していない主張の候補',
      '',
      kind === 'chapter'
        ? 'Javaの版を述べた行で、台帳のどのtextも含まないもの。主張なら台帳に足す。'
        : '数値・Javaの版・性能の言い回しを含み、主張の印がないまとまり。主張なら台帳に足し、主張でなければ（例・手順の説明など）そのままでよい。',
      '',
    ];
    for (const r of rows) {
      if (!r.uncovered.length) continue;
      lines.push(`### ${r.sec.name}`, '');
      for (const u of r.uncovered) lines.push(`- \`${u.where}\` [${u.why.join('・')}] ${u.text.replace(/\|/g, '\\|')}`);
      lines.push('');
    }
    fs.writeFileSync(rp.file, lines.join('\n'));
    console.log(`\n${path.relative(ROOT, rp.file)} に書いた`);
  }
}

main();
