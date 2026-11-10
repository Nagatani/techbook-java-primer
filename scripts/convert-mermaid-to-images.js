/**
 * docs/diagrams/*.mmd（Mermaidの図の元データ）を manuscripts/images/diagrams/ のSVGに変換する。
 *
 * 原稿からは同じ名前のSVGを参照する（docs/diagrams/chapter09-hashmap-structure.mmd →
 * ![HashMap内部構造](images/diagrams/chapter09-hashmap-structure.svg)）。
 * 紙面での大きさはビルド時に scripts/diagram-size.js が決める。
 * 本で使わない図の元データは docs/diagrams/unused/ に置く（変換しない）。
 *
 * 原稿の中に ```mermaid のブロックを直接書くことはしない（以前は原稿を書き換えて画像に
 * 置き換えていたため、元データが残らなかった）。見つけたらエラーにする。
 */
const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');
const crypto = require('crypto');
const glob = require('glob');

const DIAGRAMS_DIR = path.join(__dirname, '..', 'docs', 'diagrams');
const IMAGES_DIR = path.join(__dirname, '..', 'manuscripts', 'images', 'diagrams');
const MANUSCRIPTS_DIR = path.join(__dirname, '..', 'manuscripts');

fs.mkdirSync(IMAGES_DIR, { recursive: true });

// 図を描く幅（-w）より広い図をMermaidが縮めないようにする（useMaxWidth: false）。
// 図の文字はどれも同じ大きさで描かれ、紙面での大きさは scripts/diagram-size.js が決める
// 注意: mermaid-cli v12は -w/-H を廃止し、レイアウトも縦長に変わるためv11に固定している
const CONFIG_FILE = path.join(DIAGRAMS_DIR, 'mermaid-config.json');
const MMDC_OPTIONS = `-t default -b white -w 1200 -H 800 -c "${CONFIG_FILE}"`;
const FORMAT = 'svg';

// 変換済みの図の記録（.mmdの内容・設定・変換オプションのハッシュ）
// mmdcの出力はビルドのたびにわずかに変わるため、元の図が変わったときだけ変換する
// すべて変換し直すときは --force を付ける
const HASH_FILE = path.join(DIAGRAMS_DIR, '.converted-hashes.json');
const force = process.argv.includes('--force');
let convertedHashes = {};
try {
  convertedHashes = JSON.parse(fs.readFileSync(HASH_FILE, 'utf8'));
} catch (e) {
  convertedHashes = {};
}
const configText = fs.readFileSync(CONFIG_FILE, 'utf8');
const sourceHash = (text) =>
  crypto.createHash('sha256').update(`${FORMAT}\n${MMDC_OPTIONS.replace(CONFIG_FILE, '')}\n${configText}\n${text}`).digest('hex').slice(0, 16);

// 原稿に直接書かれたMermaidのブロックがないか
const inline = glob.sync(path.join(MANUSCRIPTS_DIR, '*.md')).filter((f) => /^\s*```mermaid/m.test(fs.readFileSync(f, 'utf8')));
if (inline.length) {
  console.error('原稿にMermaidのブロックが直接書かれています。docs/diagrams/chapterXX-内容.mmd に移し、');
  console.error('原稿からは images/diagrams/chapterXX-内容.svg を参照してください。');
  for (const f of inline) console.error(`  ${path.relative(process.cwd(), f)}`);
  process.exit(1);
}

console.log('Mermaid図の変換を開始します...');

const mermaidFiles = glob.sync(path.join(DIAGRAMS_DIR, '*.mmd'));
let converted = 0;
const convertedNames = [];
let skipped = 0;
let failed = 0;

for (const mmdFile of mermaidFiles) {
  const basename = path.basename(mmdFile, '.mmd');
  const outputFile = path.join(IMAGES_DIR, `${basename}.${FORMAT}`);
  const hash = sourceHash(fs.readFileSync(mmdFile, 'utf8'));

  if (!force && convertedHashes[basename] === hash && fs.existsSync(outputFile)) {
    skipped++;
    continue;
  }

  try {
    console.log(`変換中: ${basename}.mmd -> ${basename}.${FORMAT}`);
    execSync(`npx mmdc -i "${mmdFile}" -o "${outputFile}" ${MMDC_OPTIONS}`, { stdio: 'inherit' });
    convertedHashes[basename] = hash;
    converted++;
    convertedNames.push(basename);
  } catch (error) {
    failed++;
    console.error(`✗ エラー: ${basename}.mmd の変換に失敗しました`, error.message);
  }
}

// 元の.mmdがなくなった図の記録は消す
const existing = new Set(mermaidFiles.map((f) => path.basename(f, '.mmd')));
for (const name of Object.keys(convertedHashes)) {
  if (!existing.has(name)) delete convertedHashes[name];
}
fs.writeFileSync(HASH_FILE, JSON.stringify(convertedHashes, Object.keys(convertedHashes).sort(), 2) + '\n');
console.log(`図の変換: ${converted}件を変換、${skipped}件は変更なしのため省略${failed ? `、${failed}件は失敗` : ''}`);
if (failed) process.exit(1);

// 変換した図の文字の大きさと、線と文字の重なりを測る（結果は check:diagrams と紙面の大きさの計算に使う）
const metricsFile = path.join(DIAGRAMS_DIR, '.diagram-metrics.json');
let measured = {};
try { measured = JSON.parse(fs.readFileSync(metricsFile, 'utf8')); } catch (e) { measured = {}; }
const toMeasure = mermaidFiles.map((f) => path.basename(f, '.mmd'))
  .filter((name) => force || converted && convertedNames.includes(name) || !measured[name]);
if (toMeasure.length) {
  execSync(`node "${path.join(__dirname, 'diagram-metrics.js')}" ${toMeasure.join(' ')}`, { stdio: 'inherit' });
}
