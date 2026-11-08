#!/usr/bin/env node
/**
 * 原稿のJavaサンプルコードをjavacでコンパイルし、動かないサンプルを洗い出すスクリプト
 *
 * 使い方:
 *   node scripts/check-java-samples.js                 全章を検査し、結果を表示
 *   node scripts/check-java-samples.js chapter10       ファイル名に chapter10 を含む原稿だけ検査
 *   node scripts/check-java-samples.js --report        結果を docs/analysis/java-sample-check.md にも書き出す
 *
 * 対象は「サンプルコードX-Y」の番号が付いた ```java ブロックのうち、
 * 行頭にクラス・インターフェイス・列挙型・レコードの宣言があるもの（単体のコンパイル単位）だけ。
 * メソッドや文だけの断片や、クラスの外にメソッドや文を書いた断片は対象外とする。
 *
 * 判定:
 *   OK        単体でコンパイルできる
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
      samples.push({ id: pending.id, line: i + 1, code: body.join('\n') });
      pending = null;
      i = j;
    } else if (lines[i].trim() && !open) {
      if (pending && i + 1 - pending.line > 3) pending = null;
    }
  }
  return samples;
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

function compile(sample, workDir) {
  const code = normalize(sample.code);
  const decl = code.match(PUBLIC_DECL) || code.match(TYPE_DECL);
  const name = decl[decl.length - 1];
  const dir = fs.mkdtempSync(path.join(workDir, 's'));
  fs.writeFileSync(path.join(dir, `${name}.java`), code);
  const r = spawnSync('javac', ['-J-Duser.language=en', '--release', '21', '-proc:none', '-nowarn', '-Xlint:none', '-encoding', 'UTF-8', '-d', path.join(dir, 'out'), `${name}.java`], { cwd: dir, encoding: 'utf8' });
  if (r.status === 0) return { status: 'OK' };
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
  return { status, count: errors.length, first };
}

function main() {
  const args = process.argv.slice(2);
  const filters = args.filter((a) => !a.startsWith('--'));
  if (spawnSync('javac', ['-version']).status !== 0) { console.error('javac が見つかりません'); process.exit(2); }
  const files = fs.readdirSync(MANUSCRIPTS).filter((f) => /^chapter\d+.*\.md$/.test(f)).sort()
    .filter((f) => !filters.length || filters.some((x) => f.includes(x)));
  const workDir = fs.mkdtempSync(path.join(os.tmpdir(), 'java-samples-'));
  const rows = [];
  const totals = { OK: 0, 依存: 0, 意図: 0, 省略: 0, エラー: 0, 断片: 0 };
  for (const f of files) {
    for (const s of extract(path.join(MANUSCRIPTS, f))) {
      if (!TYPE_DECL.test(s.code) || isFragment(s.code)) { totals.断片++; continue; }
      const r = compile(s, workDir);
      totals[r.status]++;
      if (r.status !== 'OK') rows.push({ file: f, ...s, ...r });
    }
  }
  fs.rmSync(workDir, { recursive: true, force: true });
  const out = [];
  out.push(`検査したサンプル: OK ${totals.OK}、依存 ${totals.依存}、意図 ${totals.意図}、省略 ${totals.省略}、エラー ${totals.エラー}（対象外の断片 ${totals.断片}）`, '');
  out.push('| 判定 | サンプル | 原稿の行 | 最初のエラー |', '|---|---|---|---|');
  const order = { エラー: 0, 省略: 1, 意図: 2, 依存: 3 };
  for (const r of rows.sort((a, b) => order[a.status] - order[b.status])) {
    out.push(`| ${r.status} | ${r.id} | ${r.file}:${r.line} | ${r.first.replace(/\|/g, '\\|').slice(0, 120)} |`);
  }
  console.log(out.join('\n'));
  if (args.includes('--report')) {
    const head = [
      '<!-- このファイルは scripts/check-java-samples.js --report が生成します -->',
      '# Javaサンプルコードのコンパイル検査',
      '',
      `javac ${spawnSync('javac', ['-version'], { encoding: 'utf8' }).stdout.trim().replace(/^javac /, '')}（\`--release 21\`）で、番号付きのJavaサンプルのうち単体のコンパイル単位になっているものを1つずつコンパイルした結果。`,
      '「依存」は、同じ章の別のサンプルや外部ライブラリにある型が見つからないだけのもの。「意図」は、コード中のコメントで誤りの例と示しているもの。「省略」は、メソッドの本体を省略しているか、複数のファイルを1つに並べているもの。「エラー」は、それ以外のコンパイルエラーで、修正が必要な可能性が高い。',
      '',
    ];
    fs.writeFileSync(REPORT, head.concat(out).join('\n') + '\n');
    console.log(`\n書き出し: ${path.relative(ROOT, REPORT)}`);
  }
  process.exitCode = totals.エラー ? 1 : 0;
}

main();
