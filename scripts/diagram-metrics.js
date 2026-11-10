/**
 * 図（manuscripts/images/diagrams/*.svg）をブラウザで開き、次の2つを測る。
 *
 * 1. 図の中でいちばん小さい文字の大きさ（図の座標でのpx）
 *    scripts/diagram-size.js が、この文字が目安の大きさになるように紙面での大きさを決める
 * 2. 線（矢印、関係線、ライフラインなど）が文字の上を通っている箇所
 *    文字の下に背景（ノードの箱、注釈の箱）が後から描かれていて線が隠れる場合は数えない。
 *    辺のラベルはその辺自身が通るのが普通なので、ラベルを通る線が2本以上のときだけ数える
 *
 * 3. 文字が自分の箱（ノード、注釈、参加者の箱）からはみ出している箇所
 * 4. 別々のラベルの文字どうしが重なっている、またはすき間なく接している箇所（8px未満。ラベルの背景どうしがくっつく）
 *
 * 結果は docs/diagrams/.diagram-metrics.json に書く（npm run mermaid:convert が変換のあとに呼ぶ）。
 *   node scripts/diagram-metrics.js [図の名前...]   名前を省くとすべての図を測る
 */
const fs = require('fs');
const path = require('path');
const puppeteer = require('puppeteer');

const ROOT = path.join(__dirname, '..');
const IMAGES_DIR = path.join(ROOT, 'manuscripts', 'images', 'diagrams');
const OUT = path.join(ROOT, 'docs', 'diagrams', '.diagram-metrics.json');

// ブラウザの中で実行する測定
function measure() {
  const svg = document.querySelector('svg');
  const vb = svg.viewBox.baseVal;
  const scale = vb && vb.width ? svg.getBoundingClientRect().width / vb.width : 1;
  const toSvg = (r) => ({ x0: r.left / scale, y0: r.top / scale, x1: r.right / scale, y1: r.bottom / scale });

  // 文字の箱と大きさ
  const texts = [];
  const walker = document.createTreeWalker(svg, NodeFilter.SHOW_TEXT);
  let minFont = Infinity;
  for (let n = walker.nextNode(); n; n = walker.nextNode()) {
    const t = n.textContent.trim();
    if (!t) continue;
    const el = n.parentElement;
    const cs = getComputedStyle(el);
    if (cs.visibility === 'hidden' || cs.display === 'none' || Number(cs.opacity) === 0) continue;
    const range = document.createRange();
    range.selectNodeContents(n);
    for (const r of range.getClientRects()) {
      if (r.width < 1 || r.height < 1) continue;
      const fs = parseFloat(cs.fontSize);
      if (fs) minFont = Math.min(minFont, fs);
      const isEdgeLabel = !!el.closest('.edgeLabel, .edgeLabels, .relation-label, .edgeTerminals');
      // 白い縁取り（paint-order: stroke）のある文字は、下を通る線が文字の部分だけ隠れる
      const halo = /stroke/.test(cs.paintOrder || '') && parseFloat(cs.strokeWidth) >= 3 && /255,\s*255,\s*255|#fff/i.test(cs.stroke);
      // ガントチャートで棒の外に出した名前は、はみ出しとして数えない
      const outside = /taskTextOutside/.test(el.getAttribute('class') || '');
      texts.push({ text: t.slice(0, 30), box: toSvg(r), el, isEdgeLabel, halo, outside });
    }
  }

  // 線（塗りのないpath・line・polyline）
  const strokes = [...svg.querySelectorAll('path, line, polyline')].filter((el) => {
    const cs = getComputedStyle(el);
    if (cs.stroke === 'none' || cs.visibility === 'hidden' || cs.display === 'none') return false;
    if (parseFloat(cs.strokeWidth) === 0) return false;
    if (el.closest('marker, defs, .label, .nodeLabel')) return false;
    const fill = cs.fill;
    const filled = fill && fill !== 'none' && !/rgba\(.*,\s*0\)$/.test(fill);
    if (filled && el.tagName === 'path' && !/link|relation|messageLine|transition|edge|actor-line|lifeline/.test(el.getAttribute('class') || '')) return false;
    return true;
  });

  // 文字の上に後から描かれた背景（塗りのある図形）があるか
  const fillers = [...svg.querySelectorAll('rect, polygon, path, circle, ellipse')].filter((el) => {
    const cs = getComputedStyle(el);
    const fill = cs.fill;
    return fill && fill !== 'none' && !/rgba\(.*,\s*0\)$/.test(fill) && Number(cs.fillOpacity) > 0.5 && !el.closest('marker, defs');
  });
  const coversAfter = (stroke, box) => fillers.some((f) => {
    if (f === stroke) return false;
    if (!(stroke.compareDocumentPosition(f) & Node.DOCUMENT_POSITION_FOLLOWING)) return false;
    const b = toSvg(f.getBoundingClientRect());
    return b.x0 <= box.x0 + 1 && b.y0 <= box.y0 + 1 && b.x1 >= box.x1 - 1 && b.y1 >= box.y1 - 1;
  });
  // HTMLのラベル（foreignObject）は背景色を持つことがある
  const htmlBackground = (el) => {
    for (let e = el; e && e !== svg; e = e.parentElement) {
      const bg = getComputedStyle(e).backgroundColor;
      if (bg && bg !== 'transparent' && !/rgba\(.*,\s*0\)$/.test(bg)) return true;
      if (e.tagName.toLowerCase() === 'foreignobject') break;
    }
    return false;
  };

  const hits = new Map(); // 文字の番号 -> 通る線の数
  strokes.forEach((s) => {
    let len = 0;
    try { len = s.getTotalLength(); } catch (e) { return; }
    if (!len) return;
    const ctm = s.getScreenCTM();
    const pts = [];
    for (let d = 0; d <= len; d += 2) {
      const p = s.getPointAtLength(d);
      const q = new DOMPoint(p.x, p.y).matrixTransform(ctm);
      pts.push({ x: q.x / scale, y: q.y / scale });
    }
    texts.forEach((t, i) => {
      if (t.halo) return;
      const b = t.box;
      const inside = pts.some((p) => p.x > b.x0 + 1 && p.x < b.x1 - 1 && p.y > b.y0 + 2 && p.y < b.y1 - 2);
      if (!inside) return;
      if (coversAfter(s, b)) return;
      if (htmlBackground(t.el) && !(s.compareDocumentPosition(t.el) & Node.DOCUMENT_POSITION_PRECEDING)) return;
      hits.set(i, (hits.get(i) || 0) + 1);
    });
  });
  const overlaps = [];
  hits.forEach((count, i) => {
    const t = texts[i];
    if (t.isEdgeLabel && count < 2) return;
    overlaps.push(t.text);
  });
  // 文字が、自分の箱（外側のグループにある、大きさのある塗りの図形）からはみ出していないか
  const overflows = [];
  texts.forEach((t) => {
    if (t.outside) return;
    const b = t.box;
    let g = t.el.closest('g');
    for (let depth = 0; g && g !== svg && depth < 4; depth++, g = g.parentElement.closest('g')) {
      if (g.classList.contains('cluster') || g.classList.contains('clusters') || g.classList.contains('root')) return;
      const shapes = [...g.children].filter((c) => /^(rect|polygon|path|circle|ellipse)$/.test(c.tagName) && fillers.includes(c))
        .map((c) => toSvg(c.getBoundingClientRect()))
        .filter((r) => r.x1 - r.x0 > 6 && r.y1 - r.y0 > 6);
      if (!shapes.length) continue;
      const ok = shapes.some((r) => r.x0 <= b.x0 + 2 && r.y0 <= b.y0 + 2 && r.x1 >= b.x1 - 2 && r.y1 >= b.y1 - 2);
      if (!ok) overflows.push(t.text);
      return;
    }
  });
  // 別々のラベルの文字どうしの重なり（同じラベルの行どうしは数えない）
  const labelOf = (el) => el.closest('.label, .edgeLabel, .nodeLabel, g.note, .noteText, text') || el;
  const collisions = [];
  for (let i = 0; i < texts.length; i++) {
    for (let j = i + 1; j < texts.length; j++) {
      const a = texts[i], b = texts[j];
      if (labelOf(a.el) === labelOf(b.el) || a.el.closest('g') === b.el.closest('g') && a.el.closest('foreignObject') && a.el.closest('foreignObject') === b.el.closest('foreignObject')) continue;
      const w = Math.min(a.box.x1, b.box.x1) - Math.max(a.box.x0, b.box.x0);
      const h = Math.min(a.box.y1, b.box.y1) - Math.max(a.box.y0, b.box.y0);
      if (w > -8 && h > 3) collisions.push(`${a.text}／${b.text}`);
    }
  }
  return { minFont: Number.isFinite(minFont) ? minFont : null, overlaps: [...new Set(overlaps)], overflows: [...new Set(overflows)], collisions: [...new Set(collisions)] };
}

async function main() {
  const only = process.argv.slice(2);
  const files = fs.readdirSync(IMAGES_DIR).filter((f) => f.endsWith('.svg'))
    .filter((f) => !only.length || only.includes(f.replace(/\.svg$/, '')));
  let metrics = {};
  try { metrics = JSON.parse(fs.readFileSync(OUT, 'utf8')); } catch (e) { metrics = {}; }
  const browser = await puppeteer.launch({ headless: true });
  try {
    const page = await browser.newPage();
    for (const f of files) {
      const svg = fs.readFileSync(path.join(IMAGES_DIR, f), 'utf8');
      await page.setContent(`<!doctype html><html><body style="margin:0">${svg}</body></html>`, { waitUntil: 'load' });
      const m = await page.evaluate(measure);
      metrics[f.replace(/\.svg$/, '')] = m;
    }
  } finally {
    await browser.close();
  }
  const existing = new Set(fs.readdirSync(IMAGES_DIR).filter((f) => f.endsWith('.svg')).map((f) => f.replace(/\.svg$/, '')));
  for (const k of Object.keys(metrics)) if (!existing.has(k)) delete metrics[k];
  fs.writeFileSync(OUT, JSON.stringify(metrics, Object.keys(metrics).sort().reduce((a, k) => a.concat(k, Object.keys(metrics[k]), 'minFont', 'overlaps', 'overflows', 'collisions'), []), 2) + '\n');
  console.log(`図の測定: ${files.length}件`);
}

main().catch((e) => { console.error(e); process.exit(1); });
