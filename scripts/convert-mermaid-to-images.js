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
 *
 * 図の文字は本文と同じBIZ UDPゴシック（OFL）で描く（2026-11-24〜）。SVGを<img>で載せると
 * テーマで読み込んだ書体を使えないので、図で使う文字だけを切り出した書体をSVGに埋め込む。
 * mmdc（コマンド）は書体の読み込みを待たずに文字の幅を測るので、mermaid-cliをNodeから呼び、
 * ページに書体を読み込み終えてから描かせる。書体は google/fonts の決まったコミットから
 * .cache/fonts/ に取得する。
 */
const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');
const crypto = require('crypto');
const glob = require('glob');
const subsetFont = require('subset-font');

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

// 図の書体（BIZ UDPゴシック）。版を変えたら FONT_REV も変え、すべての図を変換し直す
const FONT_FAMILY = 'BIZ UDPGothic';
const FONT_COMMIT = '6ce172f74aa355ea43eb964fa4a91570a4d3064d';
const FONT_REV = `google/fonts@${FONT_COMMIT}`;
const FONT_DIR = path.join(__dirname, '..', '.cache', 'fonts');
const FONT_FILES = { 400: 'BIZUDPGothic-Regular.ttf', 700: 'BIZUDPGothic-Bold.ttf' };
const fontPath = (weight) => path.join(FONT_DIR, FONT_FILES[weight]);
const FONT_CSS = path.join(FONT_DIR, 'mermaid-font.css');

function ensureFonts() {
  fs.mkdirSync(FONT_DIR, { recursive: true });
  for (const weight of Object.keys(FONT_FILES)) {
    if (fs.existsSync(fontPath(weight))) continue;
    const url = `https://raw.githubusercontent.com/google/fonts/${FONT_COMMIT}/ofl/bizudpgothic/${FONT_FILES[weight]}`;
    console.log(`書体を取得しています: ${FONT_FILES[weight]}`);
    execSync(`curl -sSfL -o "${fontPath(weight)}" "${url}"`, { stdio: 'inherit' });
  }
  // 変換の間だけ使う、書体全体を埋め込んだCSS（描く前のページに読み込ませ、文字の幅をこの書体で測らせる）
  const faces = Object.keys(FONT_FILES).map((weight) =>
    `@font-face{font-family:"${FONT_FAMILY}";font-weight:${weight};` +
    `src:url(data:font/ttf;base64,${fs.readFileSync(fontPath(weight)).toString('base64')}) format("truetype");}`);
  fs.writeFileSync(FONT_CSS, faces.join('\n') + '\n');
}

const decodeEntities = (text) => text
  .replace(/&#x([0-9a-f]+);/gi, (m, h) => String.fromCodePoint(parseInt(h, 16)))
  .replace(/&#(\d+);/g, (m, d) => String.fromCodePoint(Number(d)))
  .replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&quot;/g, '"').replace(/&apos;/g, "'")
  .replace(/&nbsp;/g, '\u00a0').replace(/&amp;/g, '&');

// 図で使う文字だけを切り出した書体を、SVGの末尾に埋め込む
async function embedSubsetFont(svgFile) {
  let svg = fs.readFileSync(svgFile, 'utf8');
  const body = svg.replace(/<style[\s\S]*?<\/style>/g, '');
  const text = decodeEntities(body.replace(/<[^>]+>/g, ''));
  const chars = [...new Set(text + ' ')].join('');
  const weights = /font-weight: ?(bold|[6-9]00)|<b>|<strong>/.test(svg) ? [400, 700] : [400];
  const faces = [];
  for (const weight of weights) {
    const woff2 = await subsetFont(fs.readFileSync(fontPath(weight)), chars, { targetFormat: 'woff2' });
    faces.push(`@font-face{font-family:"${FONT_FAMILY}";font-weight:${weight};` +
      `src:url(data:font/woff2;base64,${woff2.toString('base64')}) format("woff2");}`);
  }
  svg = svg.replace(/<\/svg>\s*$/, `<style>${faces.join('')}</style></svg>`);
  fs.writeFileSync(svgFile, svg);
}

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
  crypto.createHash('sha256').update(`${FORMAT}\n${MMDC_OPTIONS.replace(CONFIG_FILE, '')}\n${FONT_REV}\n${configText}\n${text}`).digest('hex').slice(0, 16);

// 原稿に直接書かれたMermaidのブロックがないか
const inline = glob.sync(path.join(MANUSCRIPTS_DIR, '*.md')).filter((f) => /^\s*```mermaid/m.test(fs.readFileSync(f, 'utf8')));
if (inline.length) {
  console.error('原稿にMermaidのブロックが直接書かれています。docs/diagrams/chapterXX-内容.mmd に移し、');
  console.error('原稿からは images/diagrams/chapterXX-内容.svg を参照してください。');
  for (const f of inline) console.error(`  ${path.relative(process.cwd(), f)}`);
  process.exit(1);
}

// mmdcと同じ設定（-t default -b white -w 1200 -H 800 -c mermaid-config.json）で描く
let browser;
let renderMermaid;
async function render(mmdFile, outputFile) {
  if (!browser) {
    const cli = await import('@mermaid-js/mermaid-cli');
    renderMermaid = cli.renderMermaid;
    const puppeteer = (await import('puppeteer')).default;
    browser = await puppeteer.launch({ headless: 'shell' });
    const fontCss = fs.readFileSync(FONT_CSS, 'utf8');
    const newPage = browser.newPage.bind(browser);
    // mermaid-cliがページを開いた直後に書体を読み込み、読み込み終えるまで待つ
    browser.newPage = async (...args) => {
      const page = await newPage(...args);
      const goto = page.goto.bind(page);
      page.goto = async (...gotoArgs) => {
        const response = await goto(...gotoArgs);
        await page.addStyleTag({ content: fontCss });
        const loaded = await page.evaluate(async (family, weights) => {
          const faces = await Promise.all(weights.map((w) => document.fonts.load(`${w} 16px "${family}"`, 'あA')));
          return faces.every((list) => list.length > 0);
        }, FONT_FAMILY, Object.keys(FONT_FILES));
        if (!loaded) throw new Error('図の書体を読み込めなかった');
        return response;
      };
      return page;
    };
  }
  const mermaidConfig = { theme: 'default', ...JSON.parse(configText) };
  const { data } = await renderMermaid(browser, fs.readFileSync(mmdFile, 'utf8'), 'svg', {
    viewport: { width: 1200, height: 800, deviceScaleFactor: 1 },
    backgroundColor: 'white',
    mermaidConfig,
  });
  fs.writeFileSync(outputFile, Buffer.from(data));
}

async function main() {
console.log('Mermaid図の変換を開始します...');

const mermaidFiles = glob.sync(path.join(DIAGRAMS_DIR, '*.mmd'));
let converted = 0;
const convertedNames = [];
let skipped = 0;
let failed = 0;
let fontsReady = false;

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
    if (!fontsReady) { ensureFonts(); fontsReady = true; }
    await render(mmdFile, outputFile);
    await embedSubsetFont(outputFile);
    convertedHashes[basename] = hash;
    converted++;
    convertedNames.push(basename);
  } catch (error) {
    failed++;
    console.error(`✗ エラー: ${basename}.mmd の変換に失敗しました`, error.message);
  }
}

if (browser) await browser.close();

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
}

main().catch((e) => { console.error(e); process.exit(1); });
