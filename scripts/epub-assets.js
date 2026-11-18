#!/usr/bin/env node
/**
 * 電子書籍（EPUB）に入れる画像を作る（npm run build:epub から呼ぶ）。
 *
 * - 表紙: 原稿の表紙（manuscripts/cover.md と cover-theme.css）をブラウザー（puppeteer）で描画し、
 *   manuscripts/images/epub/cover.jpg にする。高さ2560px（KDPが勧める電子書籍の表紙の大きさ）で、
 *   比はB5の紙面と同じ。BOOK_EDITION の版で描く（正規版ではALPHA版の表示を出さない）
 * - 図: manuscripts/images/diagrams/*.svg を、2倍の解像度で白地のPNGにする
 *   （manuscripts/images/epub/diagrams/*.png）。Mermaidの図は文字をforeignObjectで描いており、
 *   KindleではSVGのままだと文字が出ないため。SVGより新しいPNGがあれば作り直さない
 *
 * images/epub/ はビルドのたびに作る生成物なので、リポジトリには入れない（.gitignore）。
 * あわせて、EPUBの作業用ディレクトリ（.vivliostyle-epub、vivliostyle.config.js の workspaceDir）を空にする。
 * VivliostyleはこのディレクトリをそのままEPUBにまとめるため、前のビルドの残りが入らないようにする。
 */
const fs = require('fs');
const os = require('os');
const path = require('path');
const puppeteer = require('puppeteer');
const { documentProcessor } = require('./vfm-section-label.js');

const ROOT = path.join(__dirname, '..');
const MANUSCRIPTS = path.join(ROOT, 'manuscripts');
const DIAGRAMS = path.join(MANUSCRIPTS, 'images/diagrams');
const OUT = path.join(MANUSCRIPTS, 'images/epub');
const OUT_DIAGRAMS = path.join(OUT, 'diagrams');

// B5（176×250mm）をCSSの96dpiで描いた大きさ
const PAGE_W = Math.round((176 / 25.4) * 96);
const PAGE_H = Math.round((250 / 25.4) * 96);
const COVER_HEIGHT_PX = 2560;

async function renderCover(browser, tmp) {
  let html = String(documentProcessor({}, {}).processSync(fs.readFileSync(path.join(MANUSCRIPTS, 'cover.md'), 'utf8')));
  const theme = path.join(ROOT, 'theme/my-theme/cover-theme.css');
  html = html.replace('<head>', `<head><base href="file://${MANUSCRIPTS}/">`)
    .replace('</head>', `<link rel="stylesheet" href="file://${theme}"></head>`);
  const file = path.join(tmp, 'cover.html');
  fs.writeFileSync(file, html);
  const page = await browser.newPage();
  await page.setViewport({ width: PAGE_W, height: PAGE_H, deviceScaleFactor: COVER_HEIGHT_PX / PAGE_H });
  await page.goto(`file://${file}`, { waitUntil: 'networkidle0' });
  await page.evaluate(() => document.fonts.ready);
  await page.screenshot({ path: path.join(OUT, 'cover.jpg'), type: 'jpeg', quality: 90 });
  await page.close();
  console.log('表紙: manuscripts/images/epub/cover.jpg');
}

async function renderDiagrams(browser) {
  const svgs = fs.readdirSync(DIAGRAMS).filter((f) => f.endsWith('.svg')).sort();
  const page = await browser.newPage();
  let made = 0;
  for (const f of svgs) {
    const src = path.join(DIAGRAMS, f);
    const dest = path.join(OUT_DIAGRAMS, f.replace(/\.svg$/, '.png'));
    if (fs.existsSync(dest) && fs.statSync(dest).mtimeMs >= fs.statSync(src).mtimeMs) continue;
    await page.setViewport({ width: 800, height: 600, deviceScaleFactor: 2 });
    // SVGを直接開き、描かれた大きさで白地に写す
    await page.goto(`file://${src}`, { waitUntil: 'load' });
    const size = await page.evaluate(() => {
      const svg = document.documentElement;
      const r = svg.getBoundingClientRect();
      return { width: Math.ceil(r.width), height: Math.ceil(r.height) };
    });
    await page.setViewport({ width: size.width, height: size.height, deviceScaleFactor: 2 });
    await page.screenshot({ path: dest, type: 'png', omitBackground: false, clip: { x: 0, y: 0, width: size.width, height: size.height } });
    made += 1;
  }
  await page.close();
  console.log(`図: ${svgs.length}枚（作り直したもの ${made}枚）`);
}

async function main() {
  fs.rmSync(path.join(ROOT, '.vivliostyle-epub'), { recursive: true, force: true });
  fs.mkdirSync(OUT_DIAGRAMS, { recursive: true });
  const tmp = fs.mkdtempSync(path.join(os.tmpdir(), 'epub-assets-'));
  const browser = await puppeteer.launch({ headless: true });
  try {
    await renderCover(browser, tmp);
    await renderDiagrams(browser);
  } finally {
    await browser.close();
    fs.rmSync(tmp, { recursive: true, force: true });
  }
}

main().catch((e) => { console.error(e); process.exit(1); });
