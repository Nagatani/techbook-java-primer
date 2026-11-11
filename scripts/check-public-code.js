#!/usr/bin/env node
/**
 * 付録（appendix/）と演習（exercises/）の公開コードを、Java 25でコンパイルし、テストを実行する。
 *
 *   node scripts/check-public-code.js                 すべてのまとまりをコンパイルし、テストを実行する
 *   node scripts/check-public-code.js --no-test       コンパイルだけ
 *   node scripts/check-public-code.js appendix/b12    パスの先頭が一致するまとまりだけ
 *   node scripts/check-public-code.js --list          見つけたまとまりの一覧を表示する
 *
 * まとまりの決め方
 *   - .javaファイルの package 宣言からソースのルートを求め、同じルートのファイルを1つのまとまりにする
 *   - ルートが src/main/java か src/test/java のときは、その親（プロジェクト）を1つのまとまりにし、mainとtestをまとめてコンパイルする
 *
 * 外部ライブラリ（JUnit、Mockito、JMH、SLF4J、Gson、Commons）は、下の LIBS をMaven Centralから
 * .cache/sample-code-libs/ に取得し、すべてのまとまりのクラスパスに加える。
 * テストは JUnit Platform Console Standalone で、まとまりごとに一時ディレクトリをカレントにして実行する。
 *
 * 検査の対象外にするまとまりは、EXCLUDE に理由とともに書く。
 */
const fs = require('fs');
const os = require('os');
const path = require('path');
const { spawnSync } = require('child_process');

const ROOT = path.resolve(__dirname, '..');
const TARGETS = ['appendix', 'exercises'];
const LIB_CACHE = path.join(ROOT, '.cache', 'sample-code-libs');
const JAVA_RELEASE = '25';
const TEST_TIMEOUT_MS = 180_000;

// すべてのまとまりのクラスパスに加えるライブラリ（pom.xml・build.gradleと版をそろえる必要はない。コンパイルとテストの実行に使うだけ）
const LIBS = [
  'org.junit.platform:junit-platform-console-standalone:1.13.4', // JUnit Jupiter（api・params・engine）とsuiteを含む
  'org.mockito:mockito-core:5.20.0',
  'org.mockito:mockito-junit-jupiter:5.20.0',
  'net.bytebuddy:byte-buddy:1.17.7',
  'net.bytebuddy:byte-buddy-agent:1.17.7',
  'org.objenesis:objenesis:3.3',
  'org.openjdk.jmh:jmh-core:1.37',
  'org.slf4j:slf4j-api:2.0.17',
  'com.google.code.gson:gson:2.11.0',
  'org.apache.commons:commons-lang3:3.18.0',
  'commons-cli:commons-cli:1.9.0',
];

// 検査しないまとまり（パス → 理由）
const EXCLUDE = {};

// まとまりごとに javac へ渡す追加のオプション（READMEのコンパイル手順に合わせる）
// --add-exports は --release と併用できないため、指定したまとまりでは --release の代わりに実行中のJDK（25）でコンパイルする
const JAVAC_OPTIONS = {
  // FalseSharingDemoがJDK内部の@Contendedを使う（READMEの手順も同じオプションを付ける）
  'appendix/b14-java-memory-model': ['--add-exports', 'java.base/jdk.internal.vm.annotation=ALL-UNNAMED'],
};

function libJars() {
  return LIBS.map((coord) => {
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

function walk(dir, out = []) {
  for (const e of fs.readdirSync(dir, { withFileTypes: true })) {
    if (e.name.startsWith('.') || ['node_modules', 'target', 'build', 'out'].includes(e.name)) continue;
    const p = path.join(dir, e.name);
    if (e.isDirectory()) walk(p, out);
    else if (e.name.endsWith('.java')) out.push(p);
  }
  return out;
}

/** .javaファイルをまとまりに分ける。戻り値は [{ dir, files }]（dirはリポジトリからの相対パス） */
function findUnits() {
  const units = new Map();
  for (const t of TARGETS) {
    for (const file of walk(path.join(ROOT, t))) {
      const src = fs.readFileSync(file, 'utf8');
      const m = src.match(/^\s*package\s+([\w.]+)\s*;/m);
      let root = path.dirname(file);
      if (m) {
        const pkgPath = m[1].split('.').join(path.sep);
        if (root.endsWith(path.sep + pkgPath)) root = root.slice(0, -pkgPath.length - 1);
      }
      const rel = path.relative(ROOT, root).split(path.sep).join('/');
      const proj = rel.match(/^(.*)\/src\/(main|test)\/java$/);
      const key = proj ? proj[1] : rel;
      if (!units.has(key)) units.set(key, []);
      units.get(key).push(file);
    }
  }
  return [...units.entries()].map(([dir, files]) => ({ dir, files })).sort((a, b) => a.dir.localeCompare(b.dir, 'en', { numeric: true }));
}

const isTest = (file) => /@Test\b|@ParameterizedTest\b/.test(fs.readFileSync(file, 'utf8'));

function compile(unit, out, cp) {
  fs.mkdirSync(out, { recursive: true });
  const argsFile = path.join(out, '..', 'sources.txt');
  fs.writeFileSync(argsFile, unit.files.map((f) => `"${f}"`).join('\n'));
  const extra = JAVAC_OPTIONS[unit.dir];
  const release = extra ? extra : ['--release', JAVA_RELEASE];
  const r = spawnSync('javac', [...release, '-encoding', 'UTF-8', '-Xlint:deprecation,removal', '-proc:none',
    '-cp', cp, '-d', out, `@${argsFile}`], { encoding: 'utf8' });
  const msg = (r.stdout + r.stderr).trim();
  return { ok: r.status === 0, warn: /warning:/.test(msg), msg };
}

function runTests(unit, out, cp, workDir) {
  // リソース（src/test/resources・src/main/resources）があればクラスパスに加える
  const res = ['src/main/resources', 'src/test/resources'].map((d) => path.join(ROOT, unit.dir, d)).filter((d) => fs.existsSync(d));
  const r = spawnSync('java', ['-jar', libJars()[0], 'execute', '--class-path', [out, ...res, cp].join(path.delimiter),
    '--scan-class-path', out, '--disable-banner', '--details=summary', '--details-theme=ascii'],
  { cwd: workDir, encoding: 'utf8', timeout: TEST_TIMEOUT_MS });
  const text = (r.stdout || '') + (r.stderr || '');
  const found = text.match(/(\d+) tests found/);
  const failed = text.match(/(\d+) tests failed/);
  return { ok: r.status === 0, timedOut: r.error && r.error.code === 'ETIMEDOUT', found: found ? +found[1] : 0, failed: failed ? +failed[1] : 0, text };
}

function main() {
  const args = process.argv.slice(2);
  const noTest = args.includes('--no-test');
  const filters = args.filter((a) => !a.startsWith('--'));
  let units = findUnits().filter((u) => !filters.length || filters.some((f) => u.dir.startsWith(f.replace(/\/$/, ''))));
  if (args.includes('--list')) {
    for (const u of units) console.log(`${u.dir}（${u.files.length}ファイル${u.files.some(isTest) ? '、テストあり' : ''}）`);
    return;
  }
  const cp = libJars().join(path.delimiter);
  const tmp = fs.mkdtempSync(path.join(os.tmpdir(), 'public-code-'));
  const problems = [];
  let compiled = 0; let tested = 0; let testCount = 0;
  try {
    for (const [i, u] of units.entries()) {
      if (EXCLUDE[u.dir]) { console.log(`- ${u.dir}: 対象外（${EXCLUDE[u.dir]}）`); continue; }
      const base = path.join(tmp, String(i));
      const out = path.join(base, 'classes');
      const c = compile(u, out, cp);
      if (!c.ok) { problems.push({ dir: u.dir, kind: 'コンパイルエラー', msg: c.msg }); console.log(`✗ ${u.dir}: コンパイルエラー`); continue; }
      if (c.warn) problems.push({ dir: u.dir, kind: '非推奨・削除予定のAPI', msg: c.msg });
      compiled++;
      if (noTest || !u.files.some(isTest)) { console.log(`✓ ${u.dir}${c.warn ? '（警告あり）' : ''}`); continue; }
      const work = path.join(base, 'work');
      fs.mkdirSync(work);
      const t = runTests(u, out, cp, work);
      tested++; testCount += t.found;
      if (!t.ok) {
        problems.push({ dir: u.dir, kind: t.timedOut ? 'テストが時間内に終わらない' : `テストの失敗（${t.failed}/${t.found}件）`, msg: t.text.split('\n').filter((l) => /✘|MethodSource|=>|Exception|expected/.test(l)).slice(0, 15).join('\n') });
        console.log(`✗ ${u.dir}: テスト ${t.timedOut ? '時間切れ' : `${t.failed}/${t.found}件失敗`}`);
      } else console.log(`✓ ${u.dir}（テスト${t.found}件）`);
    }
  } finally {
    fs.rmSync(tmp, { recursive: true, force: true });
  }
  console.log(`\nまとまり: ${units.length}、コンパイルできた: ${compiled}${noTest ? '' : `、テストを実行した: ${tested}（${testCount}件）`}`);
  if (problems.length) {
    console.log(`\n問題: ${problems.length}件`);
    for (const p of problems) console.log(`\n## ${p.dir}: ${p.kind}\n${p.msg.split('\n').slice(0, 20).join('\n')}`);
    process.exit(1);
  }
  console.log('問題なし');
}

main();
