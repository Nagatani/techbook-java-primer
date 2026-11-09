#!/usr/bin/env node
/**
 * 本文のサンプルコードと、GitHubで公開する全文（sample-code/）を対応づけるツール
 *
 * 全文は sample-code/chapterXX/XX-YY/ に置き、ディレクトリごとに javac でコンパイルできる。
 * 本文に載せる範囲は sample-code/chapterXX/listings.json に、ファイルと行番号で記録する。
 *
 *   {
 *     "5-3": {
 *       "dir": "05-03",
 *       "main": "ShapeDemo",                  mainメソッドを持つクラス（パッケージ付きの完全名、なければ null）
 *       "excerpt": [                          本文に載せる範囲（上から順に連結したものが本文のコードと完全に一致する）
 *         { "file": "Shape.java", "from": 1, "to": 12 },
 *         { "file": "Shape.java", "from": 30, "to": 41 },
 *         { "file": "ShapeDemo.java", "from": 3, "to": 15 }
 *       ],
 *       "libs": ["com.google.code.gson:gson:2.11.0"]   外部ライブラリ（Maven Centralから取得してクラスパスに加える。省略可）
 *     }
 *   }
 *
 * 範囲と範囲のあいだには、本文では次の行が入る（"sep" で個別に指定もできる）。
 *   同じファイルで行が続かない場合    省略を示す「// ...」の行（字下げは前後の行の深いほうに合わせる）  sep: "elision"
 *   別のファイルに移る場合            空行1行                                                       sep: "blank"
 *   何も入れない                                                                                    sep: "none"
 *
 * キャプションは listings.json から生成する。
 *   **サンプルコード5-3**　Shape.java（1〜12、30〜41行目）、ShapeDemo.java（3〜15行目）
 *   ファイル全体を載せた場合は行番号を付けない。
 *
 * 使い方:
 *   node scripts/sample-code.js check [chapterXX]     本文と全文の一致、キャプション、コンパイル、実行結果を検査する
 *   node scripts/sample-code.js check --no-run        実行結果の照合を省く
 *   node scripts/sample-code.js sync [chapterXX]      listings.json から本文のキャプションとコードブロック、各章の README.md を作り直す
 *                                                     （本文のコードは全文から生成するので、コードを直すときは全文を編集して sync する）
 *   node scripts/sample-code.js show 5-3              全文を行番号付きで表示する
 *   node scripts/sample-code.js set 5-3 Shape.java:1-12,30-41 ShapeDemo.java:3-15
 *                                                     本文に載せる範囲を listings.json に書き、本文に反映する
 *   node scripts/sample-code.js add 20-7 [--libs=g:a:v,...]  コンパイルできない本文のコードも全文として登録する（このあと全文を直す）
 *   node scripts/sample-code.js relocate 20-7         全文を編集して行がずれたら、本文と同じ並びを探して範囲を直す
 *   node scripts/sample-code.js number [chapterXX]    番号のないコードブロックに番号を付け、章ごとに出現順の連番へ振り直す
 *                                                     （listings.json、ディレクトリ名、本文中の参照も直す。check でも番号を検査する）
 *   node scripts/sample-code.js extract [chapterXX]   本文のサンプルのうち、単体（または同じ章のサンプルと合わせて）
 *                                                     コンパイルできるもので、まだ listings.json にないものを全文として書き出す
 */
const fs = require('fs');
const os = require('os');
const path = require('path');
const { spawnSync } = require('child_process');
const L = require('./check-java-samples.js');

const SAMPLES = path.join(L.ROOT, 'sample-code');
const REPO = 'https://github.com/Nagatani/techbook-java-primer/tree/main/sample-code';
const ELISION = '// ...';

const pad = (n) => String(n).padStart(2, '0');
const chapterOf = (file) => Number(file.match(/^chapter(\d+)/)[1]);
const chapterDir = (ch) => path.join(SAMPLES, `chapter${pad(ch)}`);
const dirName = (id) => id.split('-').map(pad).join('-');

function manuscriptFiles(filters) {
  return fs.readdirSync(L.MANUSCRIPTS).filter((f) => /^chapter\d+.*\.md$/.test(f)).sort()
    .filter((f) => !filters.length || filters.some((x) => f.includes(x)));
}

function loadManifest(ch) {
  const p = path.join(chapterDir(ch), 'listings.json');
  return fs.existsSync(p) ? JSON.parse(fs.readFileSync(p, 'utf8')) : {};
}

function saveManifest(ch, manifest) {
  const ids = Object.keys(manifest).sort((a, b) => Number(a.split('-')[1]) - Number(b.split('-')[1]));
  const sorted = Object.fromEntries(ids.map((id) => [id, manifest[id]]));
  fs.mkdirSync(chapterDir(ch), { recursive: true });
  // excerpt の各範囲は1行にまとめて、差分を読みやすくする
  const json = JSON.stringify(sorted, null, 2).replace(/\{\s+"file": ("[^"]+"),\s+"from": (\d+),\s+"to": (\d+)(,\s+"sep": ("\w+"))?\s+\}/g,
    (m, f, a, b, s, sep) => `{ "file": ${f}, "from": ${a}, "to": ${b}${sep ? `, "sep": ${sep}` : ''} }`);
  fs.writeFileSync(path.join(chapterDir(ch), 'listings.json'), json + '\n');
}

const readLines = (file) => fs.readFileSync(file, 'utf8').replace(/\n$/, '').split('\n');

/** listings.json の excerpt から、本文に載るはずのコードを組み立てる */
function renderExcerpt(dir, excerpt) {
  const out = [];
  excerpt.forEach((seg, i) => {
    const lines = readLines(path.join(dir, seg.file));
    if (seg.from < 1 || seg.to > lines.length || seg.from > seg.to) throw new Error(`${seg.file} に ${seg.from}〜${seg.to}行目はない（${lines.length}行）`);
    const part = lines.slice(seg.from - 1, seg.to);
    if (i > 0) {
      const prev = excerpt[i - 1];
      const sep = seg.sep || (prev.file !== seg.file ? 'blank' : prev.to + 1 === seg.from ? 'none' : 'elision');
      if (sep === 'blank') out.push('');
      else if (sep === 'elision') {
        // 字下げは、省いた箇所の前後の行のうち深いほうに合わせる
        const before = out[out.length - 1].match(/^\s*/)[0];
        const after = part[0].match(/^\s*/)[0];
        out.push((before.length > after.length ? before : after) + ELISION);
      }
    }
    out.push(...part);
  });
  return out.join('\n');
}

/** キャプション（サンプル番号のあとに続く部分） */
function captionOf(dir, excerpt) {
  const byFile = [];
  for (const seg of excerpt) {
    let f = byFile.find((x) => x.file === seg.file);
    if (!f) byFile.push(f = { file: seg.file, ranges: [] });
    f.ranges.push(seg);
  }
  return byFile.map(({ file, ranges }) => {
    const total = readLines(path.join(dir, file)).length;
    if (ranges.length === 1 && ranges[0].from === 1 && ranges[0].to === total) return file;
    return `${file}（${ranges.map((r) => (r.from === r.to ? `${r.from}` : `${r.from}〜${r.to}`)).join('、')}行目）`;
  }).join('、');
}

const captionLine = (id, caption) => `<span class="listing-number">**サンプルコード${id}**　${caption}</span>`;

const LIB_CACHE = path.join(L.ROOT, '.cache', 'sample-code-libs');

/** "group:artifact:version" の JAR を Maven Central から取得し、パスを返す */
function libJars(libs = []) {
  return libs.map((coord) => {
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

/** javac でディレクトリ内の .java をまとめてコンパイルする（出力は out へ） */
const JAVA_RELEASE = '25';

function compileDir(dir, outDir, entry = {}) {
  const files = fs.readdirSync(dir).filter((f) => f.endsWith('.java'));
  const jars = libJars(entry.libs);
  const cp = jars.length ? ['-cp', jars.join(path.delimiter)] : [];
  // アノテーションプロセッサ（Lombokなど）を使うサンプルだけ -proc:none を外す
  const proc = entry.processor ? ['-proc:full'] : ['-proc:none'];
  // 本書はJava 25を対象にする。非推奨・削除予定のAPIの警告だけを出して問題として報告する
  const r = spawnSync('javac', ['-J-Duser.language=en', '--release', JAVA_RELEASE, ...proc, '-Xlint:-options,deprecation,removal', '-encoding', 'UTF-8', ...cp, '-d', outDir, ...files], { cwd: dir, encoding: 'utf8' });
  const stderr = r.stderr || '';
  const deprecations = stderr.split('\n').filter((l) => /warning: \[(deprecation|removal)\]/.test(l)).map((l) => l.replace(/^.*?([^/]+\.java:\d+)/, '$1'));
  return { ok: r.status === 0, error: stderr.split('\n').find((l) => / error: /.test(l)) || stderr, deprecations };
}

/** mainメソッドを持つクラスの完全名 */
function findMain(dir, prefer = []) {
  const all = fs.readdirSync(dir).filter((x) => x.endsWith('.java')).sort();
  for (const f of [...prefer.filter((x) => all.includes(x)), ...all.filter((x) => !prefer.includes(x))]) {
    const code = fs.readFileSync(path.join(dir, f), 'utf8');
    const m = L.mainClass(code);
    if (!m) continue;
    const pkg = code.match(/^package\s+([\w.]+)\s*;/m);
    return pkg ? `${pkg[1]}.${m}` : m;
  }
  return null;
}

// ---------------------------------------------------------------- extract

/**
 * 1つのコードブロックを、public な型ごとのファイルに分ける。
 * 返り値は [{ file, content(lines), from, to }]。本文のブロックは各ファイルの from〜to を空行1行でつないだものになる
 */
function splitUnits(code) {
  const lines = code.split('\n');
  const decls = [];
  let depth = 0;
  lines.forEach((raw, i) => {
    const line = raw.replace(/\/\/.*$/, '').replace(/"(?:\\.|[^"\\])*"/g, '""').replace(/'(?:\\.|[^'\\])'/g, "''");
    if (depth === 0) {
      const m = line.match(L.PUBLIC_DECL) || (/^\S/.test(line) && line.match(L.TYPE_DECL));
      if (m && line.search(/\S/) === 0) decls.push({ i, name: m[m.length - 1], pub: /^public\s/.test(line) });
    }
    for (const ch of line) { if (ch === '{') depth++; else if (ch === '}') depth--; }
  });
  const publics = decls.filter((d) => d.pub);
  const primary = publics[0] || decls.find((d) => L.mainClass(code)) || decls[0];
  const firstName = publics.length ? publics[0].name : (L.mainClass(code) || primary.name);
  if (publics.length <= 1) return [{ file: `${firstName}.java`, content: lines, from: 1, to: lines.length }];
  // 2つ目以降の public 型の直前にあるコメント・アノテーションは、その型のファイルに含める
  const starts = publics.slice(1).map((d) => {
    let s = d.i;
    while (s - 1 >= 0 && /^\s*(\/\/|\/\*|\*|@)/.test(lines[s - 1]) && lines[s - 1].trim()) s--;
    return { s, name: d.name };
  });
  const header = [];
  for (const l of lines) { if (/^(package|import)\s/.test(l) || (!l.trim() && header.length)) header.push(l); else if (l.trim()) break; }
  while (header.length && !header[header.length - 1].trim()) header.pop();
  const units = [];
  const bounds = [{ s: 0, name: publics[0].name }, ...starts];
  for (let k = 0; k < bounds.length; k++) {
    let e = (k + 1 < bounds.length ? bounds[k + 1].s : lines.length) - 1;
    // 次の型とのあいだの空行1行は区切り（sep: blank）として扱い、それ以外の空行はファイル末尾に残す
    if (k + 1 < bounds.length) {
      if (lines[e] !== undefined && !lines[e].trim()) e--; else return null;
    }
    const body = lines.slice(bounds[k].s, e + 1);
    if (k === 0) units.push({ file: `${bounds[k].name}.java`, content: body, from: 1, to: body.length });
    else {
      const head = header.length ? [...header, ''] : [];
      // 本文のブロック内の pkg/import は1つ目のファイルにだけあるため、2つ目以降はそれを補う
      units.push({ file: `${bounds[k].name}.java`, content: [...head, ...body], from: head.length + 1, to: head.length + body.length });
    }
  }
  return units;
}

function extractCommand(filters) {
  if (spawnSync('javac', ['-version']).status !== 0) { console.error('javac が見つかりません'); process.exit(2); }
  const work = fs.mkdtempSync(path.join(os.tmpdir(), 'sample-code-'));
  let added = 0;
  const skipped = [];
  for (const f of manuscriptFiles(filters)) {
    const ch = chapterOf(f);
    const manifest = loadManifest(ch);
    const samples = L.extract(path.join(L.MANUSCRIPTS, f)).map((s, index) => ({ ...s, index }));
    const units = samples.filter((s) => L.TYPE_DECL.test(s.code) && !L.isFragment(s.code) && !L.INTENDED.test(s.code));
    const planned = new Map();
    for (const s of samples) {
      if (manifest[s.id] || !L.TYPE_DECL.test(s.code) || L.isFragment(s.code)) continue;
      let r = L.compile(s, work);
      if (r.status === '依存') { const c = L.compileWithProviders(s, units, work, r); if (c && c.status === '結合') r = c; }
      if (!['OK', '結合'].includes(r.status)) continue;
      const split = splitUnits(s.code);
      if (!split) { skipped.push(`${s.id}（型の区切りに空行がない）`); continue; }
      planned.set(s.id, { s, split, with: r.with || [] });
    }
    // 1回目: 各サンプル自身のファイル、2回目: 型を借りるサンプルのファイルを補う
    const filesOf = (id) => {
      if (planned.has(id)) return planned.get(id).split.map((u) => ({ file: u.file, text: u.content.join('\n') + '\n' }));
      if (manifest[id]) {
        const dir = path.join(chapterDir(ch), manifest[id].dir);
        return fs.readdirSync(dir).filter((x) => x.endsWith('.java')).map((x) => ({ file: x, text: fs.readFileSync(path.join(dir, x), 'utf8') }));
      }
      return [];
    };
    for (const [id, p] of planned) {
      const dir = path.join(chapterDir(ch), dirName(id));
      fs.rmSync(dir, { recursive: true, force: true });
      fs.mkdirSync(dir, { recursive: true });
      const own = new Set();
      for (const u of filesOf(id)) { fs.writeFileSync(path.join(dir, u.file), u.text); own.add(u.file); }
      for (const pid of p.with) for (const u of filesOf(pid)) if (!own.has(u.file) && !fs.existsSync(path.join(dir, u.file))) fs.writeFileSync(path.join(dir, u.file), u.text);
      const c = compileDir(dir, path.join(work, `c${id}`));
      if (!c.ok) { skipped.push(`${id}（全文がコンパイルできない: ${c.error.trim()}）`); fs.rmSync(dir, { recursive: true, force: true }); continue; }
      const entry = { dir: dirName(id), main: findMain(dir, p.split.map((u) => u.file)), excerpt: p.split.map((u) => ({ file: u.file, from: u.from, to: u.to })) };
      if (p.with.length) entry.uses = p.with;
      if (renderExcerpt(dir, entry.excerpt) !== p.s.code) { skipped.push(`${id}（本文と一致しない）`); fs.rmSync(dir, { recursive: true, force: true }); continue; }
      manifest[id] = entry;
      added++;
    }
    if (Object.keys(manifest).length) saveManifest(ch, manifest);
  }
  fs.rmSync(work, { recursive: true, force: true });
  console.log(`書き出したサンプル: ${added}`);
  if (skipped.length) console.log(`書き出さなかったもの:\n  ${skipped.join('\n  ')}`);
  syncCommand(filters);
}

// ---------------------------------------------------------------- sync

function syncCommand(filters) {
  let changed = 0;
  for (const f of manuscriptFiles(filters)) {
    const ch = chapterOf(f);
    const manifest = loadManifest(ch);
    if (!Object.keys(manifest).length) continue;
    const file = path.join(L.MANUSCRIPTS, f);
    const text = fs.readFileSync(file, 'utf8');
    const lines = text.split('\n');
    for (let i = 0; i < lines.length; i++) {
      const m = lines[i].match(/^(\s*)<span class="listing-number">\*\*サンプルコード(\d+-\d+)\*\*[^<]*<\/span>/);
      const e = m && manifest[m[2]];
      if (!e) continue;
      const dir = path.join(chapterDir(ch), e.dir);
      lines[i] = m[1] + captionLine(m[2], captionOf(dir, e.excerpt));
      // キャプションの直後（3行以内）のコードブロックを、全文の範囲で置き換える
      let k = i + 1;
      while (k < lines.length && k <= i + 3 && !/^\s*```java\s*$/.test(lines[k])) k++;
      if (k > i + 3 || k >= lines.length) continue;
      const indent = lines[k].match(/^\s*/)[0];
      let end = k + 1;
      while (end < lines.length && !/^\s*```\s*$/.test(lines[end])) end++;
      const body = renderExcerpt(dir, e.excerpt).split('\n').map((l) => (l ? indent + l : l));
      lines.splice(k + 1, end - k - 1, ...body);
    }
    const next = lines.join('\n');
    if (next !== text) { fs.writeFileSync(file, next); changed++; }
    writeReadme(ch, f, manifest);
  }
  console.log(`キャプションとコードを更新した原稿: ${changed}`);
}

function writeReadme(ch, manuscript, manifest) {
  const title = (fs.readFileSync(path.join(L.MANUSCRIPTS, manuscript), 'utf8').match(/^#\s+(.+)$/m) || [, `第${ch}章`])[1];
  const rows = Object.entries(manifest).map(([id, e]) => {
    const dir = path.join(chapterDir(ch), e.dir);
    const files = fs.readdirSync(dir).filter((x) => x.endsWith('.java')).sort();
    const run = e.main ? `\`java -cp out ${e.main}\`` : '（mainメソッドなし）';
    const libs = (e.libs || []).length ? `<br>外部ライブラリ: ${e.libs.join(', ')}` : '';
    return `| [${id}](./${e.dir}/) | ${files.join('<br>')} | ${run}${libs} |`;
  });
  const md = [
    '<!-- このファイルは scripts/sample-code.js sync が生成します -->',
    `# 第${ch}章のサンプルコード`,
    '',
    `「${title.replace(/<[^>]+>/g, '').replace(/^\d+章\s*/, '')}」の章のサンプルコードです。`,
    '',
    `本文の「サンプルコード${ch}-N」の全文です。ディレクトリ名がサンプルコードの番号に対応します。`,
    '本文には全文の一部だけを載せていることがあります。本文のキャプションに、ファイル名と掲載した行の範囲を示しています。',
    '',
    '各ディレクトリで次のようにコンパイルし、表の「実行」のコマンドで実行します（Java 21以降）。',
    '',
    '```',
    'javac -d out *.java',
    '```',
    '',
    '外部ライブラリを使うサンプルは、表に示したライブラリ（Maven Centralの「グループ:名前:バージョン」）のJARを `-cp` に加えてコンパイル・実行します。',
    '',
    '| サンプルコード | ファイル | 実行 |',
    '|---|---|---|',
    ...rows,
    '',
  ];
  fs.writeFileSync(path.join(chapterDir(ch), 'README.md'), md.join('\n'));
}

// ---------------------------------------------------------------- check

function checkCommand(filters, args) {
  const runMode = !args.includes('--no-run');
  const work = fs.mkdtempSync(path.join(os.tmpdir(), 'sample-code-check-'));
  const problems = [];
  const totals = { 掲載: 0, 一致: 0, 数値差: 0, 実行不可: 0, 照合なし: 0 };
  for (const f of manuscriptFiles(filters)) {
    const ch = chapterOf(f);
    problems.push(...numberingProblems(f));
    const manifest = loadManifest(ch);
    if (!Object.keys(manifest).length) continue;
    const file = path.join(L.MANUSCRIPTS, f);
    const text = fs.readFileSync(file, 'utf8');
    const samples = new Map(L.extract(file).map((s) => [s.id, s]));
    const dirs = new Set(fs.readdirSync(chapterDir(ch)).filter((d) => fs.statSync(path.join(chapterDir(ch), d)).isDirectory()));
    for (const [id, e] of Object.entries(manifest)) {
      totals.掲載++;
      const where = `${id}（${f}）`;
      const dir = path.join(chapterDir(ch), e.dir);
      dirs.delete(e.dir);
      if (!fs.existsSync(dir)) { problems.push(`${where}: ディレクトリ ${e.dir} がない`); continue; }
      const s = samples.get(id);
      if (!s) { problems.push(`${where}: 本文にサンプルコードがない`); continue; }
      let rendered;
      try { rendered = renderExcerpt(dir, e.excerpt); } catch (err) { problems.push(`${where}: ${err.message}`); continue; }
      if (rendered !== s.code) {
        const a = rendered.split('\n');
        const b = s.code.split('\n');
        let n = 0;
        while (n < a.length && n < b.length && a[n] === b[n]) n++;
        problems.push(`${where}: 本文のコードが全文の範囲と一致しない（本文の${n + 1}行目: 本文「${b[n] ?? '（なし）'}」／全文「${a[n] ?? '（なし）'}」）`);
      }
      const want = captionLine(id, captionOf(dir, e.excerpt));
      if (!text.includes(want)) problems.push(`${where}: キャプションが listings.json と合わない（sync で直せる）`);
      const out = path.join(work, `${ch}-${e.dir}`);
      fs.cpSync(dir, out, { recursive: true });
      let c;
      try { c = compileDir(out, path.join(out, 'out'), e); } catch (err) { problems.push(`${where}: ${err.message}`); continue; }
      if (!c.ok) { problems.push(`${where}: 全文がコンパイルできない: ${c.error.trim()}`); continue; }
      for (const d of c.deprecations) problems.push(`${where}: 非推奨・削除予定のAPIを使っている: ${d}`);
      const found = findMain(dir, e.excerpt.map((x) => x.file));
      if (e.main !== found) problems.push(`${where}: main が ${found} なのに listings.json は ${e.main}`);
      if (!runMode) continue;
      const x = L.run(s, { dir: out, main: e.main, cp: ['out', ...libJars(e.libs)].join(path.delimiter) });
      if (!x) { totals.照合なし++; continue; }
      if (x.result === '一致' || x.result === '数値差' || x.result === '実行不可') totals[x.result]++;
      else problems.push(`${where}: 実行結果が本文と異なる（${x.result}: ${x.detail}）`);
    }
    for (const d of dirs) problems.push(`chapter${pad(ch)}/${d}: listings.json にないディレクトリ`);
  }
  fs.rmSync(work, { recursive: true, force: true });
  console.log(`全文を公開しているサンプル: ${totals.掲載}${runMode ? `（実行結果 一致 ${totals.一致}、数値差 ${totals.数値差}、実行不可 ${totals.実行不可}、照合なし ${totals.照合なし}）` : ''}`);
  if (problems.length) { console.log(`問題 ${problems.length}件:\n  ${problems.join('\n  ')}`); process.exitCode = 1; } else console.log('問題なし');
}

// ---------------------------------------------------------------- number

// 番号を付けないコードブロック（コマンド操作、実行結果、図）。言語指定のないブロックも付けない
const UNNUMBERED_LANGS = new Set(['', 'bash', 'sh', 'shell', 'console', 'mermaid', 'plaintext']);
const CAPTION = /<span class="listing-number">\*\*サンプルコード(\d+)-(\d+)\*\*/;

/**
 * 原稿のコードブロックを列挙する。引用（> ）の中のブロックも対象にする。
 * caption はブロックの直前（空行をはさんで3行以内）にあるキャプション行の位置
 */
function scanBlocks(lines) {
  const blocks = [];
  for (let i = 0; i < lines.length; i++) {
    const open = lines[i].match(/^((?:\s*>)*\s*)```(\S*)\s*$/);
    if (!open) continue;
    const prefix = open[1];
    let end = i + 1;
    while (end < lines.length && !/^(?:\s*>)*\s*```\s*$/.test(lines[end])) end++;
    let caption = null;
    for (let k = i - 1; k >= 0 && k >= i - 3; k--) {
      if (CAPTION.test(lines[k])) { caption = k; break; }
      if (lines[k].replace(/^[\s>]*/, '')) break;
    }
    blocks.push({ open: i, end, lang: open[2], prefix, caption });
    i = end;
  }
  return blocks;
}

/** 番号を付けるべきなのに付いていないブロック、番号の飛び・重複・章番号の誤りを返す */
function numberingProblems(f) {
  const ch = chapterOf(f);
  const lines = fs.readFileSync(path.join(L.MANUSCRIPTS, f), 'utf8').split('\n');
  const problems = [];
  let expected = 1;
  for (const b of scanBlocks(lines)) {
    if (b.caption === null) {
      if (!UNNUMBERED_LANGS.has(b.lang)) problems.push(`${f}:${b.open + 1}: \`\`\`${b.lang} のブロックに番号がない（number で付けられる）`);
      continue;
    }
    const [, x, y] = lines[b.caption].match(CAPTION);
    if (Number(x) !== ch || Number(y) !== expected) problems.push(`${f}:${b.caption + 1}: サンプルコード${x}-${y} は ${ch}-${expected} のはず（number で振り直せる）`);
    expected++;
  }
  const captions = lines.filter((l) => CAPTION.test(l)).length;
  if (captions !== expected - 1) problems.push(`${f}: コードブロックに続かないキャプションがある（${captions - expected + 1}件）`);
  return problems;
}

/**
 * 番号のないコードブロックに番号を付け、章ごとに出現順の連番に振り直す。
 * listings.json のキー、sample-code/ のディレクトリ名、本文中の「サンプルコードX-Y」の参照も合わせて直す
 */
function numberCommand(filters) {
  const all = manuscriptFiles([]);
  const maps = new Map(); // ch -> Map(old id -> new id)
  for (const f of manuscriptFiles(filters)) {
    const ch = chapterOf(f);
    const file = path.join(L.MANUSCRIPTS, f);
    const lines = fs.readFileSync(file, 'utf8').split('\n');
    // 1. 番号のないブロックにキャプションを入れる（後ろから入れて行番号をずらさない）
    const blocks = scanBlocks(lines);
    let added = 0;
    for (const b of [...blocks].reverse()) {
      if (b.caption !== null || UNNUMBERED_LANGS.has(b.lang)) continue;
      const blank = b.prefix.replace(/\s+$/, '');
      const before = b.open > 0 && lines[b.open - 1].replace(/^[\s>]*/, '') ? [blank] : [];
      lines.splice(b.open, 0, ...before, `${b.prefix}<span class="listing-number">**サンプルコード${ch}-0**</span>`, blank);
      added++;
    }
    // 2. 出現順に振り直す
    const map = new Map();
    let n = 0;
    for (const b of scanBlocks(lines)) {
      if (b.caption === null) continue;
      const [, x, y] = lines[b.caption].match(CAPTION);
      const id = `${ch}-${++n}`;
      const old = `${x}-${y}`;
      if (y !== '0') {
        if (map.has(old)) throw new Error(`${f}: サンプルコード${old} が重複している`);
        map.set(old, id);
      }
      lines[b.caption] = lines[b.caption].replace(CAPTION, `<span class="listing-number">**サンプルコード${id}**`);
    }
    fs.writeFileSync(file, lines.join('\n'));
    maps.set(ch, map);
    const moved = [...map].filter(([a, b]) => a !== b).length;
    console.log(`${f}: 番号を付けた ${added}件、番号が変わった ${moved}件（全${n}件）`);
    // 3. listings.json とディレクトリ
    const manifest = loadManifest(ch);
    if (!Object.keys(manifest).length) continue;
    const next = {};
    const renames = [];
    for (const [id, e] of Object.entries(manifest)) {
      const to = map.get(id);
      if (!to) throw new Error(`${f}: listings.json の ${id} が本文にない`);
      if (dirName(to) !== e.dir) renames.push([e.dir, dirName(to)]);
      next[to] = { ...e, dir: dirName(to) };
    }
    for (const [a] of renames) fs.renameSync(path.join(chapterDir(ch), a), path.join(chapterDir(ch), `.renumber-${a}`));
    for (const [a, b] of renames) fs.renameSync(path.join(chapterDir(ch), `.renumber-${a}`), path.join(chapterDir(ch), b));
    saveManifest(ch, next);
  }
  // 4. 本文中の参照（キャプション以外の「サンプルコードX-Y」）
  for (const f of all) {
    const file = path.join(L.MANUSCRIPTS, f);
    const text = fs.readFileSync(file, 'utf8');
    const next = text.split('\n').map((line) => (CAPTION.test(line) ? line : line.replace(/サンプルコード(\d+)-(\d+)/g, (m, x, y) => {
      const to = maps.get(Number(x))?.get(`${x}-${y}`);
      return to ? `サンプルコード${to}` : m;
    }))).join('\n');
    if (next !== text) fs.writeFileSync(file, next);
  }
  syncCommand(filters);
  console.log('本文中の「サンプルコードX-Y」の参照は直しました。「12-1と12-2」のように番号だけの参照は手で確かめてください');
}

/** 全文を行番号付きで表示する（抜粋の範囲を決めるため） */
function showCommand(ids) {
  for (const id of ids) {
    const ch = Number(id.split('-')[0]);
    const e = loadManifest(ch)[id];
    if (!e) { console.log(`${id}: listings.json にない`); continue; }
    const dir = path.join(chapterDir(ch), e.dir);
    console.log(`=== ${id} main=${e.main} excerpt=${e.excerpt.map((x) => `${x.file}:${x.from}-${x.to}${x.sep ? `(${x.sep})` : ''}`).join(' ')}`);
    const own = [...new Set(e.excerpt.map((x) => x.file))];
    for (const f of own) {
      console.log(`--- ${f}`);
      readLines(path.join(dir, f)).forEach((l, n) => console.log(`${String(n + 1).padStart(3)}| ${l}`));
    }
    const others = fs.readdirSync(dir).filter((x) => x.endsWith('.java') && !own.includes(x));
    if (others.length) console.log(`（ほかのファイル: ${others.join(', ')}）`);
  }
}

/**
 * 本文に載せる範囲を指定して listings.json を更新し、本文に反映する
 *   set 5-1 PaymentProcessor.java:3-4,17-42,84 ShapeDemo.java:5-9
 */
function setCommand([id, ...specs]) {
  const ch = Number(id.split('-')[0]);
  const manifest = loadManifest(ch);
  const e = manifest[id];
  if (!e) { console.error(`${id}: listings.json にない`); process.exit(1); }
  const excerpt = [];
  for (const spec of specs) {
    const [file, ranges] = spec.split(':');
    if (!fs.existsSync(path.join(chapterDir(ch), e.dir, file))) { console.error(`${file} がない`); process.exit(1); }
    const total = readLines(path.join(chapterDir(ch), e.dir, file)).length;
    for (const r of (ranges || `1-${total}`).split(',')) {
      const [a, b] = r.split('-').map(Number);
      excerpt.push({ file, from: a, to: b || a });
    }
  }
  renderExcerpt(path.join(chapterDir(ch), e.dir), excerpt); // 範囲が不正なら保存前に止める
  e.excerpt = excerpt;
  e.main = findMain(path.join(chapterDir(ch), e.dir), excerpt.map((x) => x.file));
  saveManifest(ch, manifest);
  const f = manuscriptFiles([`chapter${pad(ch)}`])[0];
  syncCommand([f]);
  console.log(`${id}: ${renderExcerpt(path.join(chapterDir(ch), e.dir), excerpt).split('\n').length}行`);
}

/**
 * 本文のコードを、コンパイルできるかどうかにかかわらず全文として登録する。
 * 登録後に全文へ import や足りないクラスを書き足し、relocate で範囲を合わせ直す
 *   add 20-7 [--libs=group:artifact:version,...]
 */
function addCommand([id], args) {
  const ch = Number(id.split('-')[0]);
  const f = manuscriptFiles([`chapter${pad(ch)}`])[0];
  const s = L.extract(path.join(L.MANUSCRIPTS, f)).find((x) => x.id === id);
  if (!s) { console.error(`${id}: 本文にない`); process.exit(1); }
  const manifest = loadManifest(ch);
  if (manifest[id]) { console.error(`${id}: すでに listings.json にある`); process.exit(1); }
  const split = splitUnits(s.code);
  if (!split) { console.error(`${id}: 型の区切りに空行がない`); process.exit(1); }
  const dir = path.join(chapterDir(ch), dirName(id));
  fs.mkdirSync(dir, { recursive: true });
  for (const u of split) fs.writeFileSync(path.join(dir, u.file), u.content.join('\n') + '\n');
  const entry = { dir: dirName(id), main: findMain(dir, split.map((u) => u.file)), excerpt: split.map((u) => ({ file: u.file, from: u.from, to: u.to })) };
  const libs = (args.find((a) => a.startsWith('--libs=')) || '').replace('--libs=', '');
  if (libs) entry.libs = libs.split(',');
  manifest[id] = entry;
  saveManifest(ch, manifest);
  console.log(`${id}: ${split.map((u) => u.file).join(', ')} を書き出した`);
}

/**
 * 全文を編集して行がずれたあと、本文のコードと同じ並びを全文の中から探して from/to を更新する
 */
function relocateCommand(ids) {
  for (const id of ids) {
    const ch = Number(id.split('-')[0]);
    const f = manuscriptFiles([`chapter${pad(ch)}`])[0];
    const s = L.extract(path.join(L.MANUSCRIPTS, f)).find((x) => x.id === id);
    const manifest = loadManifest(ch);
    const e = manifest[id];
    const book = s.code.split('\n');
    let k = 0;
    e.excerpt.forEach((seg, i) => {
      if (i > 0) {
        const prev = e.excerpt[i - 1];
        const sep = seg.sep || (prev.file !== seg.file ? 'blank' : prev.to + 1 === seg.from ? 'none' : 'elision');
        if (sep !== 'none') k++;
      }
      const n = seg.to - seg.from + 1;
      const want = book.slice(k, k + n).join('\n');
      const lines = readLines(path.join(chapterDir(ch), e.dir, seg.file));
      const starts = [];
      for (let a = 0; a + n <= lines.length; a++) if (lines.slice(a, a + n).join('\n') === want) starts.push(a + 1);
      if (!starts.length) throw new Error(`${id}: ${seg.file} に本文の${k + 1}行目からの${n}行が見つからない`);
      // 候補が複数あるときは、もとの位置にいちばん近いもの
      const from = starts.reduce((best, x) => (Math.abs(x - seg.from) < Math.abs(best - seg.from) ? x : best));
      seg.from = from;
      seg.to = from + n - 1;
      k += n;
    });
    e.main = findMain(path.join(chapterDir(ch), e.dir), e.excerpt.map((x) => x.file));
    saveManifest(ch, manifest);
    syncCommand([f]);
  }
}

function main() {
  const [cmd, ...rest] = process.argv.slice(2);
  const filters = rest.filter((a) => !a.startsWith('--'));
  if (cmd === 'extract') extractCommand(filters);
  else if (cmd === 'sync') syncCommand(filters);
  else if (cmd === 'check') checkCommand(filters, rest);
  else if (cmd === 'show') showCommand(filters);
  else if (cmd === 'set') setCommand(filters);
  else if (cmd === 'add') addCommand(filters, rest);
  else if (cmd === 'relocate') relocateCommand(filters);
  else if (cmd === 'number') numberCommand(filters);
  else { console.error('使い方: node scripts/sample-code.js check|sync|extract [chapterXX]'); process.exit(2); }
}

if (require.main === module) main();

module.exports = { renderExcerpt, captionOf, loadManifest, chapterDir, REPO };
