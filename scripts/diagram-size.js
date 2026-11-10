/**
 * 図（manuscripts/images/diagrams/）を紙面に載せる大きさを決める。
 *
 * 図は横幅いっぱいに合わせず、元の大きさに一定の倍率を掛けて載せる。こうすると、
 * どの図も中のいちばん小さい文字が本文と同じ10Qになる（文字の大きさは diagram-metrics.js が測る）。ただし本文の幅と、
 * ページに収まる高さを上限にし、それを超える図だけを縮める。縮めた結果、文字が
 * 小さくなりすぎる図は、図の書き方（分割、向きの変更など）を見直す対象になる。
 *
 * ビルド時は scripts/vfm-figure-size.js がこの計算で <img> に幅を書き込む。
 * `npm run check:diagrams` は、元データ（docs/diagrams/*.mmd）と画像と原稿の対応、
 * 各図の文字の大きさを検査する。
 */
const fs = require('fs');
const path = require('path');

const ROOT = path.join(__dirname, '..');
const MANUSCRIPTS = path.join(ROOT, 'manuscripts');
const IMAGES_DIR = path.join(MANUSCRIPTS, 'images', 'diagrams');
const DIAGRAMS_DIR = path.join(ROOT, 'docs', 'diagrams');

// PNGの場合の mermaid-cli の --scale（図の1pxを2pxで描く）と、図の文字の大きさ（px）
// いまはSVGで出力している（SVGの大きさは viewBox から読む）
const PNG_SCALE = 2;
const DIAGRAM_FONT_PX = 16;

// 紙面での大きさ（mm）。本文の文字は10Q（2.5mm）
const TARGET_FONT_MM = 2.5; // 図の中でいちばん小さい文字の大きさの目安（10Q、本文と同じ）
const MIN_FONT_MM = 2.25; // これより小さくなる図は見直しの対象（9Q）

// 図ごとの測定結果（scripts/diagram-metrics.js が書く）。いちばん小さい文字の大きさと、線が文字の上を通る箇所
const METRICS_FILE = path.join(DIAGRAMS_DIR, '.diagram-metrics.json');
let metrics = {};
try { metrics = JSON.parse(fs.readFileSync(METRICS_FILE, 'utf8')); } catch (e) { metrics = {}; }
const MAX_WIDTH_MM = 130; // 本文の幅（版型はISOのB5で幅176mm、左右の余白22mmずつ）
const MAX_HEIGHT_MM = 150; // 見出しやキャプションと同じページに収まる高さ

/** 図の大きさ（図の座標でのpx）を返す。読めなければ null */
function naturalSize(file) {
  const buf = fs.readFileSync(file);
  if (file.endsWith('.png')) {
    if (buf.toString('ascii', 1, 4) !== 'PNG') return null;
    return { width: buf.readUInt32BE(16) / PNG_SCALE, height: buf.readUInt32BE(20) / PNG_SCALE };
  }
  if (file.endsWith('.svg')) {
    const head = buf.toString('utf8', 0, 4096);
    const vb = head.match(/viewBox="[\d.-]+[\s,]+[\d.-]+[\s,]+([\d.]+)[\s,]+([\d.]+)"/);
    if (vb) return { width: Number(vb[1]), height: Number(vb[2]) };
  }
  return null;
}

/** 紙面での幅・高さ（mm）と、図の中でいちばん小さい文字の大きさ（mm）を返す */
function layout({ width, height, fontPx = DIAGRAM_FONT_PX }) {
  const mmPerPx = TARGET_FONT_MM / fontPx;
  let w = width * mmPerPx;
  let limitedBy = null;
  if (w > MAX_WIDTH_MM) { w = MAX_WIDTH_MM; limitedBy = 'width'; }
  if (w * (height / width) > MAX_HEIGHT_MM) { w = MAX_HEIGHT_MM * (width / height); limitedBy = 'height'; }
  const fontMm = (w / width) * fontPx;
  return { widthMm: w, heightMm: w * (height / width), fontMm, limitedBy, tooSmall: fontMm < MIN_FONT_MM - 1e-9 };
}

/** 原稿の画像の参照（images/diagrams/...）から紙面の大きさを求める */
function layoutForSrc(src) {
  const file = path.join(MANUSCRIPTS, src);
  if (!fs.existsSync(file)) return null;
  const size = naturalSize(file);
  if (!size) return null;
  const m = metrics[path.basename(src).replace(/\.(png|svg)$/, '')] || {};
  const fontPx = m.minFont || DIAGRAM_FONT_PX;
  return { ...layout({ ...size, fontPx }), ...size, fontPx, overlaps: m.overlaps || [], overflows: m.overflows || [], collisions: m.collisions || [] };
}

module.exports = { layout, layoutForSrc, naturalSize, IMAGES_DIR, DIAGRAMS_DIR, MANUSCRIPTS, MIN_FONT_MM, TARGET_FONT_MM };

// ---- npm run check:diagrams ----
if (require.main === module) {
  const glob = require('glob');
  const strict = process.argv.includes('--strict');
  const problems = [];
  const warnings = [];

  const sources = new Set(glob.sync('*.mmd', { cwd: DIAGRAMS_DIR }).map((f) => path.basename(f, '.mmd')));
  const images = new Set(fs.readdirSync(IMAGES_DIR).filter((f) => /\.(png|svg)$/.test(f)));
  const refs = new Map(); // 画像ファイル名 -> 原稿の場所
  for (const md of glob.sync('*.md', { cwd: MANUSCRIPTS }).sort()) {
    fs.readFileSync(path.join(MANUSCRIPTS, md), 'utf8').split('\n').forEach((line, i) => {
      for (const m of line.matchAll(/images\/diagrams\/([^)"\s]+)/g)) refs.set(m[1], `${md}:${i + 1}`);
    });
  }

  for (const [img, where] of refs) {
    const base = img.replace(/\.(png|svg)$/, '');
    if (!images.has(img)) problems.push(`${where}: 画像 ${img} がない（npm run mermaid:convert で作る）`);
    if (!sources.has(base)) problems.push(`${where}: 画像 ${img} の元データ docs/diagrams/${base}.mmd がない`);
  }
  for (const base of sources) {
    if (![...refs.keys()].some((img) => img.replace(/\.(png|svg)$/, '') === base)) {
      warnings.push(`docs/diagrams/${base}.mmd は原稿から参照されていない（使わないなら docs/diagrams/unused/ へ）`);
    }
  }
  for (const img of images) {
    if (!refs.has(img)) warnings.push(`manuscripts/images/diagrams/${img} は原稿から参照されていない`);
  }

  const rows = [];
  for (const [img, where] of [...refs].sort((a, b) => a[1].localeCompare(b[1], 'en', { numeric: true }))) {
    const l = layoutForSrc(`images/diagrams/${img}`);
    if (!l) continue;
    const q = (mm) => (mm * 4).toFixed(1); // 1Q = 0.25mm
    const note = l.limitedBy === 'height' ? '高さに合わせて縮小' : l.limitedBy === 'width' ? '幅に合わせて縮小' : '';
    rows.push(`  ${img.padEnd(40)} ${l.widthMm.toFixed(0).padStart(4)}×${l.heightMm.toFixed(0).padStart(3)}mm  文字${q(l.fontMm).padStart(5)}Q  ${note}`);
    if (l.overlaps.length) {
      (strict ? problems : warnings).push(`${where}: ${img} は線が文字の上を通っている（${l.overlaps.slice(0, 3).join('、')}${l.overlaps.length > 3 ? ' など' : ''}）`);
    }
    if (l.overflows.length) {
      (strict ? problems : warnings).push(`${where}: ${img} は文字が箱からはみ出している（${l.overflows.slice(0, 3).join('、')}${l.overflows.length > 3 ? ' など' : ''}）`);
    }
    if (l.collisions.length) {
      (strict ? problems : warnings).push(`${where}: ${img} はラベルの文字どうしが重なっている（${l.collisions.slice(0, 2).join('、')}${l.collisions.length > 2 ? ' など' : ''}）`);
    }
    if (l.tooSmall) {
      (strict ? problems : warnings).push(
        `${where}: ${img} は${note}で文字が${q(l.fontMm)}Qになる（${q(MIN_FONT_MM)}Q未満）。図を分ける・並べる向きを変えるなどを検討する`);
    }
  }

  console.log(`図の大きさ（図の中でいちばん小さい文字の目安は${(TARGET_FONT_MM * 4).toFixed(0)}Q、本文は10Q、下限は${(MIN_FONT_MM * 4).toFixed(0)}Q）`);
  console.log(rows.join('\n'));
  for (const w of warnings) console.log(`注意: ${w}`);
  for (const p of problems) console.log(`問題: ${p}`);
  console.log(problems.length ? `図の検査: 問題 ${problems.length}件` : `図の検査: 問題なし（図 ${refs.size}、注意 ${warnings.length}件）`);
  process.exit(problems.length ? 1 : 0);
}
