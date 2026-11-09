#!/usr/bin/env node
/**
 * 原稿のJavaサンプルコードをjavacでコンパイルし、動かないサンプルを洗い出すスクリプト
 *
 * 使い方:
 *   node scripts/check-java-samples.js                 全章を検査し、結果を表示
 *   node scripts/check-java-samples.js chapter10       ファイル名に chapter10 を含む原稿だけ検査
 *   node scripts/check-java-samples.js --run           コンパイルできたサンプルを実行し、直後の「実行結果：」と比べる
 *   node scripts/check-java-samples.js --report        結果を docs/analysis/java-sample-check.md にも書き出す
 *
 * 対象は「サンプルコードX-Y」の番号が付いた ```java ブロックのうち、
 * 行頭にクラス・インターフェイス・列挙型・レコードの宣言があるもの（単体のコンパイル単位）だけ。
 * メソッドや文だけの断片や、クラスの外にメソッドや文を書いた断片は対象外とする。
 *
 * 判定:
 *   OK        単体でコンパイルできる
 *   結合      単体では型が足りないが、同じ章の別のサンプルにある型を補うとコンパイルできる
 *   依存      同じ章の別のサンプルや外部ライブラリにある型が見つからないだけ
 *   意図      コンパイルできないが、コード中のコメントで誤りの例と示している（「コンパイルエラー」「NG」「❌」など）
 *   省略      メソッドの本体を省略している、または複数のファイル（package）を1つに並べている
 *   エラー    それ以外のコンパイルエラー（構文の誤り、型の誤りなど）
 *
 * 1つのサンプルに public な型が複数ある場合（複数ファイルを1つに並べた例）は、
 * mainメソッドを持つ型（なければ最初の型）以外の public を外してからコンパイルする。
 */
const fs = require('fs');
const os = require('os');
const path = require('path');
const { spawnSync } = require('child_process');

const ROOT = path.join(__dirname, '..');
const MANUSCRIPTS = path.join(ROOT, 'manuscripts');
const REPORT = path.join(ROOT, 'docs', 'analysis', 'java-sample-check.md');

const TYPE_DECL = /^(?:public\s+|abstract\s+|final\s+|sealed\s+|non-sealed\s+|static\s+)*(class|interface|enum|record)\s+(\w+)/m;
const PUBLIC_DECL = /^public\s+(?:abstract\s+|final\s+|sealed\s+|non-sealed\s+)*(?:class|interface|enum|record)\s+(\w+)/m;
// 依存不足とみなすエラー（型やパッケージが見つからない）
const MISSING = /error: (cannot find symbol|package [\w.]+ does not exist|static import only from classes and interfaces|method does not override or implement a method from a supertype)/;
// 同じクラスを並べて比較する例（従来の書き方とRecordなど）
const DUPLICATE = /error: duplicate class/;
const CASCADE = /error: (invalid method reference|incompatible types|[\w.<>]+ is not a functional interface|(constructor|method) \S+ in \S+ .*cannot be applied to given types|cannot infer type arguments|Unexpected @FunctionalInterface annotation)/;

function extract(file) {
  const lines = fs.readFileSync(file, 'utf8').split('\n');
  const samples = [];
  let pending = null;
  for (let i = 0; i < lines.length; i++) {
    const m = lines[i].match(/listing-number">\*\*サンプルコード(\d+-\d+)/);
    if (m) { pending = { id: m[1], line: i + 1 }; continue; }
    const open = lines[i].match(/^(\s*)```java\s*$/);
    if (open && pending && i + 1 - pending.line <= 3) {
      const indent = open[1].length;
      const body = [];
      let j = i + 1;
      for (; j < lines.length && !/^\s*```\s*$/.test(lines[j]); j++) body.push(lines[j].slice(Math.min(indent, lines[j].search(/\S|$/))));
      samples.push({ id: pending.id, line: i + 1, code: body.join('\n'), expected: expectedOutput(lines, j + 1) });
      pending = null;
      i = j;
    } else if (lines[i].trim() && !open) {
      if (pending && i + 1 - pending.line > 3) pending = null;
    }
  }
  return samples;
}

/**
 * コードブロックの直後（空行を除いて1行目）にある「実行結果：」の出力ブロックを返す。
 * 「（例）」「（実行毎に異なる）」「（GUI表示）」など、実行して比べられないものは label だけ返す
 */
function expectedOutput(lines, from) {
  let k = from;
  while (k < lines.length && !lines[k].trim()) k++;
  const label = (lines[k] || '').trim();
  if (!/^(#{1,6}\s*)?(\*\*)?(使用例と)?実行結果|実行結果の一例|実行結果は次/.test(label)) return null;
  k++;
  while (k < lines.length && !lines[k].trim()) k++;
  const open = (lines[k] || '').match(/^(\s*)```\w*\s*$/);
  if (!open) return null;
  const out = [];
  for (k++; k < lines.length && !/^\s*```\s*$/.test(lines[k]); k++) out.push(lines[k].slice(Math.min(open[1].length, lines[k].search(/\S|$/))));
  // 比べるのは「実行結果：」「#### 実行結果」などの見出しそのものだけ（「（例）」付きや「一例」などは比べない）
  const comparable = /^(#{1,6}\s*)?(\*\*)?実行結果[:：]?(\*\*)?[:：]?$/.test(label);
  return { label, line: from + 1, text: out.join('\n'), comparable };
}

// 誤りの例であることを示すコメント
const INTENDED = /\/\/.*(コンパイルエラー|エラーになる|エラー例|エラー：|タイプミス|NG|❌|BAD|間違い|誤り)|\/\*[\s\S]*?(コンパイルエラー|NG|❌|BAD)[\s\S]*?\*\//;

/** 行頭にメソッドや文がある（クラスの外に書かれた断片）なら true */
function isFragment(code) {
  let depth = 0;
  let pendingDecl = false;
  for (const raw of code.split('\n')) {
    const line = raw.replace(/\/\/.*$/, '').replace(/"(?:\\.|[^"\\])*"/g, '""');
    const s = line.trim();
    if (depth === 0 && s && !/^(import|package|@|\*|\/\*|\*\/|\})/.test(s) && !TYPE_DECL.test(s)) return true;
    if (depth === 0 && TYPE_DECL.test(s) && pendingDecl) return true;
    if (depth === 0 && TYPE_DECL.test(s) && !s.includes('{') && !/[,(]$|extends|implements/.test(s.replace(/\s+(extends|implements)\s+\S+.*$/, ''))) pendingDecl = true;
    else if (s) { if (pendingDecl && !s.startsWith('{') && !/^(extends|implements|permits)/.test(s)) return true; pendingDecl = false; }
    for (const ch of line) { if (ch === '{') depth++; else if (ch === '}') depth--; }
  }
  return pendingDecl;
}

/** public な型が複数ある場合、1つを残して public を外す */
function normalize(code) {
  const publics = [...code.matchAll(new RegExp(PUBLIC_DECL.source, 'gm'))];
  if (publics.length <= 1) return code;
  const withMain = publics.find((m, i) => {
    const end = i + 1 < publics.length ? publics[i + 1].index : code.length;
    return /public\s+static\s+void\s+main\s*\(/.test(code.slice(m.index, end));
  }) || publics[0];
  let out = code;
  for (const m of [...publics].reverse()) {
    if (m === withMain) continue;
    out = out.slice(0, m.index) + out.slice(m.index).replace(/^public\s+/, '');
  }
  return out;
}

/** mainメソッドを持つトップレベルの型の名前（なければ null） */
function mainClass(code) {
  const m = code.match(/static\s+void\s+main\s*\(\s*(final\s+)?String/);
  if (!m) return null;
  const decls = [...code.slice(0, m.index).matchAll(new RegExp(TYPE_DECL.source, 'gm'))];
  return decls.length ? decls[decls.length - 1][2] : null;
}

const norm = (t) => t.split('\n').map((l) => l.replace(/\s+$/, '')).join('\n').replace(/^\n+|\n+$/g, '');

/**
 * コンパイルできたサンプルを実行し、原稿の実行結果と比べる。
 * 標準入力は空、GUIはheadless、20秒で打ち切る
 */
function run(sample, r) {
  const exp = sample.expected;
  if (!exp || !exp.comparable || !r.main) return null;
  // 標準エラー出力（例外のスタックトレースなど）も原稿の実行結果に含まれるため、出力順を保ったまま合わせて受け取る
  const p = spawnSync('sh', ['-c', 'exec java -Djava.awt.headless=true -Duser.language=ja -Duser.country=JP -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp "$1" "$0" 2>&1', r.main, r.cp || 'out'],
    { cwd: r.dir, encoding: 'utf8', input: '', timeout: 20000 });
  if (p.error || p.signal) return { result: '打切', detail: '20秒以内に終わらない' };
  // JDKやライブラリが標準エラーに出す警告（WARNING: ...）は実行環境によって変わるので比べない
  const actual = norm((p.stdout || '').split('\n').filter((l) => !/^WARNING: /.test(l)).join('\n'));
  const expected = norm(exp.text);
  if (actual === expected) return { result: '一致' };
  // 時刻・処理時間・乱数など、数字だけが違う
  // 絶対パス（実行したディレクトリで変わる）はファイル名だけを比べる
  const digits = (t) => t.replace(/(?<![\w.])\/(?:[^\s/]+\/)+/g, '/').replace(/\d+/g, '0');
  if (digits(actual) === digits(expected)) return { result: '数値差', detail: '数字だけが異なる（時刻・処理時間・乱数など）' };
  if (actual.includes('HeadlessException')) return { result: '実行不可', detail: 'GUIを表示する' };
  if (/(NoSuchFileException|FileNotFoundException|Connection refused|UnknownHostException)/.test(actual)) return { result: '実行不可', detail: '外部のファイルやサーバーが必要' };
  // スタックトレースの「at ...」行は原稿で省略することが多いので比べない
  // 例外メッセージ末尾の「(… are in module java.base of loader 'bootstrap')」も省略されることが多い
  const noTrace = (t) => t.split('\n').filter((l) => !/^\s+(at |\.\.\.)/.test(l)).map((l) => l.replace(/ \([^()]* (is|are) in (unnamed )?module .*\)$/, '')).join('\n');
  if (digits(noTrace(actual)) === digits(noTrace(expected))) return { result: '数値差', detail: 'スタックトレースの省略と数字だけが異なる' };
  if (process.env.SHOW_DIFF) console.error(`--- ${sample.id} 原稿\n${expected}\n--- ${sample.id} 実行\n${actual}\n`);
  const a = actual.split('\n');
  const e = expected.split('\n');
  let n = 0;
  while (n < a.length && n < e.length && a[n] === e[n]) n++;
  return { result: '不一致', detail: `${n + 1}行目: 原稿「${e[n] ?? '（なし）'}」／実行「${a[n] ?? '（なし）'}」`, expectedLine: exp.line };
}

/** トップレベルに宣言された型の名前 */
const typesOf = (code) => [...code.matchAll(new RegExp(TYPE_DECL.source, 'gm'))].map((m) => m[2]);

/** 見つからなかったクラス名（javacの「symbol: class Foo」） */
const missingClasses = (stderr) => [...new Set([...(stderr || '').matchAll(/symbol:\s+class (\w+)/g)].map((m) => m[1]))];

/**
 * サンプルをコンパイルする。extras には、同時にコンパイルする同じ章の別のサンプルを渡す
 */
function compile(sample, workDir, extras = []) {
  const code = normalize(sample.code);
  const dir = fs.mkdtempSync(path.join(workDir, 's'));
  const files = [];
  for (const c of [code, ...extras.map((e) => normalize(e.code))]) {
    const decl = c.match(PUBLIC_DECL) || c.match(TYPE_DECL);
    const file = `${decl[decl.length - 1]}.java`;
    fs.writeFileSync(path.join(dir, file), c);
    files.push(file);
  }
  const r = spawnSync('javac', ['-J-Duser.language=en', '--release', '25', '-proc:none', '-nowarn', '-Xlint:none', '-encoding', 'UTF-8', '-d', path.join(dir, 'out'), ...files], { cwd: dir, encoding: 'utf8' });
  if (r.status === 0) return { status: 'OK', dir, main: mainClass(code) };
  const errors = (r.stderr || '').split('\n').filter((l) => / error: /.test(l));
  const real = errors.filter((l) => !MISSING.test(l));
  const first = (real[0] || errors[0] || '').replace(/^.*?\.java:(\d+): error: /, 'L$1: ');
  // 型が見つからないことに起因する連鎖的なエラー
  const cascade = errors.some((l) => MISSING.test(l)) && real.every((l) => CASCADE.test(l));
  // 本体を「/* ... */」などで省略したメソッド、複数ファイルを並べた例（packageが複数）
  const elided = real.every((l) => /missing return statement/.test(l)) || (sample.code.match(/^package\s/gm) || []).length > 1;
  const status = (!real.length || cascade) ? '依存'
    : (INTENDED.test(sample.code) || real.every((l) => DUPLICATE.test(l))) ? '意図'
    : elided ? '省略' : 'エラー';
  return { status, count: errors.length, first, missing: missingClasses(r.stderr) };
}

/**
 * 見つからない型を、同じ章の別のサンプル（直前に定義されたものを優先）から補ってコンパイルし直す。
 * 補っても型が足りない、または別のエラーになる場合は null
 */
function compileWithProviders(sample, units, workDir, first) {
  const own = new Set(typesOf(sample.code));
  const extras = [];
  const provided = new Set(own);
  let r = first;
  for (let round = 0; round < 4 && r.status === '依存' && r.missing.length; round++) {
    let added = false;
    for (const name of r.missing) {
      if (provided.has(name)) continue;
      const before = units.filter((u) => u.index < sample.index).reverse();
      const after = units.filter((u) => u.index > sample.index);
      const p = [...before, ...after].find((u) => {
        const t = typesOf(u.code);
        return t.includes(name) && t.every((x) => !provided.has(x));
      });
      if (!p) continue;
      extras.push(p);
      typesOf(p.code).forEach((x) => provided.add(x));
      added = true;
    }
    if (!added) break;
    r = compile(sample, workDir, extras);
  }
  if (r.status === 'OK') return { ...r, status: '結合', with: extras.map((e) => e.id) };
  if (extras.length && r.status !== '依存') return { status: '依存', combinedError: r.first, with: extras.map((e) => e.id) };
  return null;
}

function main() {
  const args = process.argv.slice(2);
  const filters = args.filter((a) => !a.startsWith('--'));
  if (spawnSync('javac', ['-version']).status !== 0) { console.error('javac が見つかりません'); process.exit(2); }
  const files = fs.readdirSync(MANUSCRIPTS).filter((f) => /^chapter\d+.*\.md$/.test(f)).sort()
    .filter((f) => !filters.length || filters.some((x) => f.includes(x)));
  const workDir = fs.mkdtempSync(path.join(os.tmpdir(), 'java-samples-'));
  const rows = [];
  const totals = { OK: 0, 結合: 0, 依存: 0, 意図: 0, 省略: 0, エラー: 0, 断片: 0 };
  const combinedErrors = [];
  const runMode = args.includes('--run');
  const runTotals = { 一致: 0, 数値差: 0, 不一致: 0, 実行不可: 0, 打切: 0 };
  const runRows = [];
  // 全文を sample-code/ に置いたサンプルは、scripts/sample-code.js check で全文を検査する
  const { loadManifest } = require('./sample-code.js');
  let published = 0;
  for (const f of files) {
    const manifest = loadManifest(Number(f.match(/^chapter(\d+)/)[1]));
    const all = extract(path.join(MANUSCRIPTS, f)).map((s, index) => ({ ...s, index }));
    const samples = all.filter((s) => !manifest[s.id]);
    published += all.length - samples.length;
    // 単体のコンパイル単位で、意図的な誤りの例ではないもの（ほかのサンプルに型を貸せるもの）
    const units = samples.filter((s) => TYPE_DECL.test(s.code) && !isFragment(s.code) && !INTENDED.test(s.code));
    for (const s of samples) {
      if (!TYPE_DECL.test(s.code) || isFragment(s.code)) { totals.断片++; continue; }
      let r = compile(s, workDir);
      if (r.status === '依存') {
        const c = compileWithProviders(s, units, workDir, r);
        if (c && c.status === '結合') r = c;
        else if (c) combinedErrors.push({ file: f, ...s, ...c });
      }
      totals[r.status]++;
      if (!['OK', '結合'].includes(r.status)) rows.push({ file: f, ...s, ...r });
      else if (runMode) {
        const x = run(s, r);
        if (x) { runTotals[x.result]++; if (!['一致', '数値差'].includes(x.result)) runRows.push({ file: f, ...s, ...x }); }
      }
    }
  }
  fs.rmSync(workDir, { recursive: true, force: true });
  const out = [];
  out.push(`全文を sample-code/ に置いたサンプル ${published}件は対象外（npm run check:samples で検査）`, '');
  out.push(`検査したサンプル: OK ${totals.OK}、結合 ${totals.結合}、依存 ${totals.依存}、意図 ${totals.意図}、省略 ${totals.省略}、エラー ${totals.エラー}（対象外の断片 ${totals.断片}）`, '');
  out.push('| 判定 | サンプル | 原稿の行 | 最初のエラー |', '|---|---|---|---|');
  const order = { エラー: 0, 省略: 1, 意図: 2, 依存: 3 };
  for (const r of rows.sort((a, b) => order[a.status] - order[b.status])) {
    out.push(`| ${r.status} | ${r.id} | ${r.file}:${r.line} | ${r.first.replace(/\|/g, '\\|').slice(0, 120)} |`);
  }
  if (combinedErrors.length) {
    out.push('', '## ほかのサンプルと結合するとエラーになるもの', '');
    out.push('見つからない型を同じ章のサンプルから補うと、別のエラーになるもの。補ったサンプルの版が違うだけのこともある。', '');
    out.push('| サンプル | 補ったサンプル | 原稿の行 | 最初のエラー |', '|---|---|---|---|');
    for (const r of combinedErrors) out.push(`| ${r.id} | ${r.with.join('、')} | ${r.file}:${r.line} | ${r.combinedError.replace(/\|/g, '\\|').slice(0, 120)} |`);
  }
  if (runMode) {
    out.push('', '## 実行結果の照合', '');
    out.push(`原稿の「実行結果：」と比べたサンプル: 一致 ${runTotals.一致}、数値差 ${runTotals.数値差}、不一致 ${runTotals.不一致}、実行不可 ${runTotals.実行不可}、打切 ${runTotals.打切}`, '');
    out.push('| 判定 | サンプル | 実行結果の行 | 内容 |', '|---|---|---|---|');
    for (const r of runRows) out.push(`| ${r.result} | ${r.id} | ${r.file}:${r.expected.line} | ${(r.detail || '').replace(/\|/g, '\\|').slice(0, 160)} |`);
  }
  console.log(out.join('\n'));
  if (args.includes('--report')) {
    const head = [
      '<!-- このファイルは scripts/check-java-samples.js --report が生成します -->',
      '# Javaサンプルコードのコンパイル検査',
      '',
      `javac ${spawnSync('javac', ['-version'], { encoding: 'utf8' }).stdout.trim().replace(/^javac /, '')}（\`--release 21\`）で、番号付きのJavaサンプルのうち単体のコンパイル単位になっているものを1つずつコンパイルした結果。`,
      '「結合」は、単体では型が足りないが、同じ章の別のサンプルにある型を補うとコンパイルできたもの。「依存」は、補っても型が見つからないもの（外部ライブラリなど）。「意図」は、コード中のコメントで誤りの例と示しているもの。「省略」は、メソッドの本体を省略しているか、複数のファイルを1つに並べているもの。「エラー」は、それ以外のコンパイルエラーで、修正が必要な可能性が高い。',
      '',
      '`--run` を付けると、コンパイルできたサンプルのうち直後に「実行結果：」があるものを実行し、出力（標準エラー出力を含む）と比べる。',
      '「（例）」「（GUI表示）」など括弧書きの付いた実行結果は比べない。「数値差」は時刻・処理時間・乱数など数字だけの違い（スタックトレースの省略、絶対パスの違いも含む）で、一覧には載せない。',
      '「実行不可」はGUIを表示するもの、外部のファイルやサーバーが必要なもの。',
      '',
    ];
    fs.writeFileSync(REPORT, head.concat(out).join('\n') + '\n');
    console.log(`\n書き出し: ${path.relative(ROOT, REPORT)}`);
  }
  process.exitCode = totals.エラー ? 1 : 0;
}

module.exports = { extract, isFragment, normalize, mainClass, compile, compileWithProviders, run, typesOf, norm, TYPE_DECL, PUBLIC_DECL, INTENDED, ROOT, MANUSCRIPTS };

if (require.main === module) main();
