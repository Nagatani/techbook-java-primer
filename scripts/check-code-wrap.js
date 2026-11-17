#!/usr/bin/env node
/**
 * 行番号を付けたコードブロックのうち、紙面の幅に収まらず折り返す行を一覧にする。
 *
 * 原稿をビルドと同じ処理（documentProcessor）でHTMLにし、テーマを当ててブラウザー（puppeteer）で描画して、
 * 行（span.code-line）の高さが2行分以上になるものを探す。PDF（Vivliostyle）と同じく、
 * ASCIIだけの行が73桁まで収まり74桁から折り返すように、描画する幅を134mmにしている
 * （本文の幅は132mmだが、ブラウザーとVivliostyleで文字の幅がわずかに違うため。2026-11-17にPDFで較正した）。
 *
 * 使い方:
 *   npm run check:code-wrap                 すべての章
 *   npm run check:code-wrap -- chapter05    ファイル名で絞り込む
 *
 * 折り返す行が見つかったら、全文（sample-code/）を直して `npm run samples:sync` する
 * （行が増減したら listings.json の範囲も直す）。全文のないコードは原稿を直す。
 * 終了コードは、折り返す行があれば1。
 */
const fs = require('fs');
const os = require('os');
const path = require('path');
const ROOT = path.join(__dirname, '..');
const { documentProcessor } = require('./vfm-section-label.js');
const puppeteer = require('puppeteer');

const WIDTH = '134mm';

async function main() {
  const filters = process.argv.slice(2).filter((a) => !a.startsWith('--'));
  const files = fs.readdirSync(path.join(ROOT, 'manuscripts')).filter((f) => f.endsWith('.md'))
    .filter((f) => !filters.length || filters.some((x) => f.includes(x))).sort();
  const tmp = fs.mkdtempSync(path.join(os.tmpdir(), 'code-wrap-'));
  const theme = path.join(ROOT, 'theme/my-theme/theme.css');
  const browser = await puppeteer.launch();
  const found = [];
  let total = 0;
  try {
    const page = await browser.newPage();
    await page.emulateMediaType('print');
    for (const f of files) {
      let html = String(documentProcessor({}, {}).processSync(fs.readFileSync(path.join(ROOT, 'manuscripts', f), 'utf8')));
      html = html.replace('</head>', `<link rel="stylesheet" href="file://${theme}"><style>body{width:${WIDTH};margin:0;padding:0}</style></head>`);
      const file = path.join(tmp, `${f}.html`);
      fs.writeFileSync(file, html);
      await page.goto(`file://${file}`, { waitUntil: 'load' });
      const lines = await page.evaluate(() => [...document.querySelectorAll('pre .code-line[data-line]')].map((line) => {
        const pre = line.closest('pre');
        const prev = pre.previousElementSibling;
        const cap = prev && prev.textContent.match(/サンプルコード(\d+-\d+)/);
        const seg = line.closest('.code-segment');
        const label = seg && seg.querySelector('.code-segment-label');
        const rows = Math.round(line.getBoundingClientRect().height / parseFloat(getComputedStyle(line).lineHeight));
        return { id: cap ? cap[1] : null, label: label ? label.textContent : null, line: Number(line.dataset.line), rows, text: line.textContent };
      }));
      total += lines.length;
      for (const l of lines) if (l.rows > 1) found.push({ file: f, ...l });
    }
  } finally {
    await browser.close();
    fs.rmSync(tmp, { recursive: true, force: true });
  }
  for (const x of found) {
    console.log(`${x.file} サンプルコード${x.id || '（番号なし）'}${x.label ? ` ${x.label}` : ''} ${x.line}行目: ${x.text}`);
  }
  console.log(`行番号付きのコードの行: ${total}、折り返す行: ${found.length}`);
  process.exit(found.length ? 1 : 0);
}

main().catch((e) => { console.error(e); process.exit(2); });
