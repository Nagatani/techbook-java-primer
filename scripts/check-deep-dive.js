#!/usr/bin/env node
/**
 * 付録B（Deep Dive）の解説の主張を、JDKで実際に確かめる。
 *
 *   node scripts/check-deep-dive.js               すべての節の台帳（claims.yml）を検証する
 *   node scripts/check-deep-dive.js b04 b07       指定した節だけ
 *   node scripts/check-deep-dive.js --static      Javaを動かさず、台帳と印の対応・書式・フラグ名だけを検査する（pre-commit）
 *   node scripts/check-deep-dive.js --update      検証の出力をREADMEの<!-- output: id -->の欄に書き込む
 *   node scripts/check-deep-dive.js --online      出典のURLが開けるか、anchorの文字がページにあるかも確かめる
 *   node scripts/check-deep-dive.js --report      節ごとの集計を docs/analysis/deep-dive-claims.md に書く
 *   node scripts/check-deep-dive.js --verbose     検証の出力をすべて表示する
 *
 * しくみ（詳しくは docs/guides/deep-dive-verification.md）
 *   - 各節の appendix/bXX-（題名）/claims.yml に、解説の主張（claim）とその確かめ方（verify）を書く
 *   - README.md などの解説では、主張を述べた段落・リスト項目・表の直後に <!-- claim: id --> を置く
 *   - 確かめ方の種類
 *       javac   プローブをコンパイルし、コンパイラーの出力（エラー・警告）に expect があるか。fails: true ならコンパイルが失敗すること
 *       javap   プローブ（verify/のjavaファイル）をコンパイルし（releaseで--releaseを変えられる）、javap の出力に expect の正規表現があるか（absent はないか）
 *       run     プローブか節のコードを実行し、出力（標準出力と標準エラー出力）に expect があるか
 *       flag    JVMのフラグが実在し、既定値が value と一致するか
 *       manual  機械では確かめられない主張。sources（出典）と checked（確かめた日）を必須にする
 *   - 印のない段落で、数値・Javaの版・性能の言い回しを含むものを「検証していない主張の候補」として示す（注意）
 *   - 解説とプローブに出てくる -XX: のフラグは、台帳に書かなくても実在を確かめる
 */
const fs = require('fs');
const os = require('os');
const path = require('path');
const { spawnSync } = require('child_process');
const yaml = require('js-yaml');

const ROOT = path.resolve(__dirname, '..');
const APPENDIX = path.join(ROOT, 'appendix');
const LIB_CACHE = path.join(ROOT, '.cache', 'sample-code-libs');
const REPORT = path.join(ROOT, 'docs', 'analysis', 'deep-dive-claims.md');
const KINDS = ['javac', 'javap', 'run', 'flag', 'manual'];
const DEFAULT_TIMEOUT_MS = 120_000;

const args = process.argv.slice(2);
const opt = {
  static: args.includes('--static'),
  update: args.includes('--update'),
  online: args.includes('--online'),
  report: args.includes('--report'),
  verbose: args.includes('--verbose'),
  filters: args.filter((a) => !a.startsWith('--')),
};

// ---------------------------------------------------------------- 節と文書

function sections() {
  return fs.readdirSync(APPENDIX)
    .filter((d) => /^b\d\d-/.test(d))
    .filter((d) => !opt.filters.length || opt.filters.some((f) => d.startsWith(f)))
    .sort()
    .map((d) => ({ name: d, id: d.slice(0, 3), dir: path.join(APPENDIX, d) }));
}

/** 節の解説の文書（README.mdと、docs/・examples/の.md）。target/・out/は除く */
function docsOf(sec) {
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
  const walk = (dir) => {
    for (const e of fs.readdirSync(dir, { withFileTypes: true })) {
      if (e.name.startsWith('.') || ['target', 'build', 'out'].includes(e.name)) continue;
      const p = path.join(dir, e.name);
      if (e.isDirectory()) walk(p);
      else if (/\.(java|xml|gradle|properties)$/.test(e.name)) out.push(p);
    }
  };
  walk(sec.dir);
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
  const file = path.join(sec.dir, 'claims.yml');
  if (!fs.existsSync(file)) return null;
  const data = yaml.load(fs.readFileSync(file, 'utf8'), { schema: yaml.JSON_SCHEMA }) || {};
  return { file, jdk: data.jdk, claims: data.claims || [] };
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
function checkFlagNames(sec, problems) {
  const table = flags();
  if (!table) { problems.push({ level: 'warn', where: sec.name, msg: 'javaが見つからないため、フラグ名の検査を省いた' }); return; }
  const files = [...docsOf(sec), ...javaFilesOf(sec)];
  for (const f of files) {
    const text = fs.readFileSync(f, 'utf8');
    for (const m of text.matchAll(/-XX:[+-]?([A-Za-z]\w+)(=)?/g)) {
      const name = m[1];
      if (table.has(name)) continue;
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
  const r = spawnSync('javac', ['-J-Duser.language=en', '--release', release, '-Xlint:-options', '-encoding', 'UTF-8', ...proc, ...(v.javac || []),
    ...(libs.length ? ['-cp', libs.join(path.delimiter)] : []), '-d', out, ...sources], { encoding: 'utf8' });
  const res = { ok: r.status === 0, out, cp: [out, ...libs].join(path.delimiter), msg: (r.stdout + r.stderr).trim() };
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
      r = spawnSync('java', ['-Duser.language=en', ...(v.jvm || []), '-cp', c1.cp, main, ...(v.args || []).map(String)],
        { encoding: 'utf8', timeout: v.timeout ? v.timeout * 1000 : DEFAULT_TIMEOUT_MS, maxBuffer: 256 * 1024 * 1024, cwd: tmp });
      if (r.error && r.error.code === 'ETIMEDOUT') return { status: 'fail', msg: '時間内に終わらない' };
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
  let lines = output.split('\n').filter((l) => pats.some((p) => p.test(l)));
  // 字下げと連続する空白を詰める（PrintInliningなどは呼び出しの深さで字下げが変わる）
  lines = lines.map((l) => l.trim().replace(/\s{2,}/g, ' '));
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
      const r = spawnSync('curl', ['-sfL', '--max-time', '30', '-A', 'Mozilla/5.0 (book-verification)', s.url], { encoding: 'utf8', maxBuffer: 64 * 1024 * 1024 });
      if (r.status !== 0) { problems.push({ level: 'error', where: `${sec.id}: ${c.id}`, msg: `出典 ${s.url} を開けない` }); continue; }
      if (s.anchor) {
        const plain = r.stdout.replace(/<[^>]+>/g, ' ').replace(/&nbsp;/g, ' ').replace(/\s+/g, ' ');
        if (!plain.includes(s.anchor)) problems.push({ level: 'error', where: `${sec.id}: ${c.id}`, msg: `出典 ${s.url} に「${s.anchor}」がない` });
      }
    }
  }
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
      const row = { sec, claims: 0, pass: 0, fail: 0, manual: 0, markers: 0, uncovered: [] };
      summary.push(row);
      const markersAt = new Map();
      for (const f of docs) {
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
      checkFlagNames(sec, problems);
      if (!ledger) {
        if (markersAt.size) problems.push({ level: 'error', where: sec.name, msg: '印（<!-- claim: -->）があるのにclaims.ymlがない' });
        continue;
      }
      validateLedger(sec, ledger, problems);
      const ids = new Set(ledger.claims.map((c) => c.id));
      for (const [id, wheres] of markersAt) {
        if (!ids.has(id)) problems.push({ level: 'error', where: wheres[0], msg: `印のid「${id}」がclaims.ymlにない` });
      }
      for (const c of ledger.claims) {
        if (!markersAt.has(c.id) && !c.unreferenced) problems.push({ level: 'error', where: `${path.relative(ROOT, ledger.file)}: ${c.id}`, msg: '解説のどこにも印がない（解説から消した主張なら台帳からも消す）' });
      }
      row.claims = ledger.claims.length;
      row.manual = ledger.claims.filter((c) => c.verify && c.verify.kind === 'manual').length;
      if (opt.online) checkSources(sec, ledger, problems);
      if (opt.static) continue;

      const outputs = new Map();
      console.log(`\n## ${sec.name}（主張 ${ledger.claims.length}件）`);
      for (const c of ledger.claims) {
        if (!c.verify || !KINDS.includes(c.verify.kind)) continue;
        const r = verifyClaim(sec, c, tmp);
        if (r.status === 'manual') { console.log(`  - ${c.id}: 手で確認（${c.verify.checked}）`); continue; }
        if (r.status === 'pass') {
          row.pass++;
          console.log(`  ✓ ${c.id}`);
          if (opt.verbose && r.output) console.log(r.output.split('\n').map((l) => '      ' + l).join('\n'));
          if (r.output) outputs.set(c.id, shownOutput(c, r.output));
        } else {
          row.fail++;
          console.log(`  ✗ ${c.id}: ${r.msg.split('\n')[0]}`);
          problems.push({ level: 'error', where: `${sec.id}: ${c.id}`, msg: `${c.claim}\n${r.msg}${r.output ? '\n--- 出力の先頭 ---\n' + r.output.split('\n').slice(0, 15).join('\n') : ''}` });
        }
      }
      if (opt.update) {
        for (const f of docs) if (updateOutputs(f, outputs)) console.log(`  → ${path.relative(ROOT, f)} の出力を更新した`);
      }
    }
  } finally {
    fs.rmSync(tmp, { recursive: true, force: true });
  }

  // 集計
  console.log('\n| 節 | 主張 | 機械で確認 | 手で確認 | 失敗 | 印のない候補 |');
  console.log('|---|---|---|---|---|---|');
  for (const r of summary) {
    const auto = opt.static ? '-' : r.pass;
    console.log(`| ${r.sec.id} | ${r.claims} | ${auto} | ${r.manual} | ${opt.static ? '-' : r.fail} | ${r.uncovered.length} |`);
  }
  const uncovered = summary.reduce((n, r) => n + r.uncovered.length, 0);
  if (uncovered && (opt.verbose || opt.filters.length)) {
    console.log('\n注意: 検証していない主張の候補（数値・フラグ・版・性能の言い回しを含み、印のないまとまり）');
    for (const r of summary) for (const u of r.uncovered) console.log(`  ${u.where} [${u.why.join('・')}] ${u.text}`);
  } else if (uncovered) {
    console.log(`\n注意: 検証していない主張の候補が${uncovered}件（節を指定するか--verboseで一覧を表示）`);
  }
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
  const today = new Date().toISOString().slice(0, 10);
  const lines = [
    '# 付録Bの主張の検証状況',
    '',
    `\`npm run check:deep-dive -- --report\`が生成する（${today}）。手で書き換えない。しくみは\`docs/guides/deep-dive-verification.md\`。`,
    '',
    '| 節 | 主張 | 機械で確認 | 手で確認 | 失敗 | 印のない候補 |',
    '|---|---|---|---|---|---|',
    ...summary.map((r) => `| ${r.sec.name} | ${r.claims} | ${opt.static ? '-' : r.pass} | ${r.manual} | ${opt.static ? '-' : r.fail} | ${r.uncovered.length} |`),
    '',
    '## 印のない候補',
    '',
    '数値・Javaの版・性能の言い回しを含み、主張の印がないまとまり。主張なら台帳に足し、主張でなければ（例・手順の説明など）そのままでよい。',
    '',
  ];
  for (const r of summary) {
    if (!r.uncovered.length) continue;
    lines.push(`### ${r.sec.name}`, '');
    for (const u of r.uncovered) lines.push(`- \`${u.where}\` [${u.why.join('・')}] ${u.text.replace(/\|/g, '\\|')}`);
    lines.push('');
  }
  fs.writeFileSync(REPORT, lines.join('\n'));
  console.log(`\n${path.relative(ROOT, REPORT)} に書いた`);
}

main();
