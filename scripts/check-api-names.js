#!/usr/bin/env node
/**
 * 本文のインラインコードに書いたJDKのAPI名（`Objects.equals`、`List.of()`、`Collectors::toList`、`java.util.concurrent`、`Vector` など）が、
 * JDK 25に実在するか、非推奨・削除予定でないかを確かめる。
 *
 *   node scripts/check-api-names.js                 manuscripts/ のすべての原稿
 *   node scripts/check-api-names.js chapter09 ...   ファイル名の先頭が一致する原稿だけ（pre-commitはコミットする原稿を渡す）
 *   node scripts/check-api-names.js --verbose       確かめた参照をすべて表示する
 *
 * - コードブロックの中は対象外（コードは check:samples・check:java がコンパイルする）
 * - 本書のコード（sample-code/・exercises/・appendix/）で宣言したクラスと同じ名前の型は、自作のクラスとみなして調べない
 * - JDKにない型（unknown）は、自作のクラスや擬似コードとみなして数えるだけ。クラス名だけの参照（`Vector`）は、非推奨かどうかだけを見る
 * - 結果: JDKの型にないメンバー（missing）と削除予定（removal）はエラー、非推奨（deprecated）は注意
 * - 本文で意図して古いAPIや擬似的な名前を書くときは docs/claims/api-names.yml に理由とともに書く
 */
const fs = require('fs');
const path = require('path');
const { spawnSync } = require('child_process');
const yaml = require('js-yaml');

const ROOT = path.resolve(__dirname, '..');
const MANUSCRIPTS = path.join(ROOT, 'manuscripts');
const CONFIG = path.join(ROOT, 'docs', 'claims', 'api-names.yml');
const args = process.argv.slice(2);
const verbose = args.includes('--verbose');
const filters = args.filter((a) => !a.startsWith('--')).map((a) => path.basename(a));

function bookClasses() {
  const names = new Set();
  const walk = (dir) => {
    for (const e of fs.readdirSync(dir, { withFileTypes: true })) {
      if (e.name.startsWith('.') || ['node_modules', 'target', 'build', 'out'].includes(e.name)) continue;
      const p = path.join(dir, e.name);
      if (e.isDirectory()) walk(p);
      else if (e.name.endsWith('.java')) {
        for (const m of fs.readFileSync(p, 'utf8').matchAll(/\b(?:class|interface|enum|record)\s+([A-Z]\w*)/g)) names.add(m[1]);
      }
    }
  };
  for (const d of ['sample-code', 'exercises', 'appendix']) walk(path.join(ROOT, d));
  return names;
}

/** 原稿のインラインコードから参照を取り出す。戻り値は [{ kind, ref, where }] */
function references(file) {
  const out = [];
  let fence = false;
  fs.readFileSync(file, 'utf8').split('\n').forEach((line, i) => {
    if (/^\s*(```|~~~)/.test(line)) { fence = !fence; return; }
    if (fence) return;
    for (const m of line.matchAll(/`([^`]+)`/g)) {
      const code = m[1];
      if (/^https?:/.test(code)) continue;
      const where = `${path.relative(ROOT, file)}:${i + 1}`;
      for (const q of code.matchAll(/(?<![\w.])((?:java|javax|jdk)\.[a-z][a-z0-9]*(?:\.[A-Za-z_]\w*)*)/g)) {
        out.push({ kind: 'qualified', ref: q[1].replace(/\.$/, ''), where });
      }
      const noQualified = code.replace(/(?<![\w.])(?:java|javax|jdk)\.[a-z][\w.]*/g, ' ');
      // クラス名だけのインラインコード（`Vector`・`Map.Entry`）は、非推奨かどうかだけを見る
      const bare = code.match(/^([A-Z]\w*(?:\.[A-Z]\w*)*)(?:<.*>)?$/);
      if (bare) { out.push({ kind: 'type', ref: bare[1], where }); continue; }
      for (const r of noQualified.matchAll(/(?<![\w.$])([A-Z]\w*(?:\.[A-Z]\w*)*)(?:\.|::)([A-Za-z_]\w*)/g)) {
        out.push({ kind: 'member', ref: `${r[1]}.${r[2]}`, where });
      }
    }
  });
  return out;
}

function main() {
  const config = fs.existsSync(CONFIG) ? yaml.load(fs.readFileSync(CONFIG, 'utf8')) || {} : {};
  const allow = new Set(Object.keys(config.allow || {}));
  const files = fs.readdirSync(MANUSCRIPTS).filter((f) => f.endsWith('.md'))
    .filter((f) => !filters.length || filters.some((x) => f.startsWith(x.replace(/\.md$/, ''))))
    .sort().map((f) => path.join(MANUSCRIPTS, f));
  const own = bookClasses();
  const refs = files.flatMap(references)
    .filter((r) => r.kind === 'qualified' || !own.has(r.ref.split('.')[0]));
  if (!refs.length) { console.log('APIの参照: なし'); return; }
  const unique = [...new Map(refs.map((r) => [`${r.kind}\t${r.ref}`, r])).keys()];
  const res = spawnSync('java', [path.join(__dirname, 'java', 'ApiNames.java')], { input: unique.join('\n') + '\n', encoding: 'utf8', maxBuffer: 64 * 1024 * 1024 });
  if (res.status !== 0 || !res.stdout) {
    console.log('javaが動かないため、APIの参照の検査を省いた');
    if (res.stderr) console.log(res.stderr.split('\n').slice(0, 5).join('\n'));
    return;
  }
  const result = new Map();
  for (const line of res.stdout.trim().split('\n')) {
    const [status, ref, note] = line.split('\t');
    result.set(ref, { status, note });
  }
  const counts = {};
  const errors = [];
  const notices = [];
  for (const r of refs) {
    const x = result.get(r.ref) || { status: 'unknown', note: '' };
    const status = allow.has(r.ref) ? 'allowed' : x.status;
    counts[status] = (counts[status] || 0) + 1;
    const msg = `${r.where}: \`${r.ref}\` ${x.note}`;
    if (status === 'missing') errors.push(`${msg}（JDK 25にない）`);
    else if (status === 'removal') errors.push(`${msg}（削除予定のAPI）`);
    else if (status === 'deprecated') notices.push(`${msg}（非推奨のAPI）`);
    else if (verbose) console.log(`  ${status} ${msg}`);
  }
  console.log(`APIの参照: ${refs.length}件（${Object.entries(counts).map(([k, v]) => `${k} ${v}`).join('、')}）`);
  if (notices.length) console.log(`\n注意: 非推奨のAPI（本文で意図して書いたなら docs/claims/api-names.yml の allow に理由を書く）\n  ${notices.join('\n  ')}`);
  if (errors.length) {
    console.log(`\n問題: ${errors.length}件（JDK 25にない名前・削除予定のAPI。意図して書いたなら docs/claims/api-names.yml の allow に理由を書く）\n  ${errors.join('\n  ')}`);
    process.exit(1);
  }
  console.log('問題なし');
}

main();
