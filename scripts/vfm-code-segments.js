/**
 * 全文（sample-code/）から複数の範囲を抜き出したサンプルコードを、範囲ごとの枠に分けて見せる。
 *
 * 本文のコードブロックは、listings.json の範囲を「// ...」の行や空行でつないだもの
 * （scripts/sample-code.js の excerptLayout）。そのままでは、どこからどこまでが
 * 全文の何行目かがキャプションの行番号の並びからしか読み取れない。そこでビルド時に
 *
 * - 範囲ごとに <span class="code-segment"> で囲み、テーマで別々の枠にする
 * - 枠の頭に「Shape.java　30〜41行目」のような見出し（b.code-segment-label）を付ける
 * - 各行に全文での行番号（data-line 属性）を付け、テーマで行の左に出す
 * - 範囲のあいだに入れた「// ...」の行と空行は取り除く（枠の切れ目が省略を表す）
 *
 * 同じファイルの続いた範囲は1つの枠にまとめる。範囲が1つだけのサンプルは何もしない
 * （キャプションに行番号がある）。本文のコードが listings.json と食い違うとき（samples:sync の前など）も
 * 何もしない。行を span.code-line に分ける処理（vfm-code-lines.js）のあとに実行する。
 */
const path = require('path');
const { excerptLayout, separatorOf, readLines, loadManifest, chapterDir } = require('./sample-code.js');

const CAPTION = /サンプルコード(\d+)-(\d+)/;

const isElement = (node, tagName) => node && node.type === 'element' && node.tagName === tagName;
const hasClass = (node, name) => [].concat((node.properties || {}).className || []).includes(name);

function textOf(node) {
  if (node.type === 'text') return node.value;
  return (node.children || []).map(textOf).join('');
}

const manifests = new Map();
function entryOf(ch, id) {
  if (!manifests.has(ch)) manifests.set(ch, loadManifest(ch));
  return manifests.get(ch)[id];
}

/** キャプションの段落ならサンプル番号（"5-3"）を返す */
function captionId(node) {
  if (!isElement(node, 'p')) return null;
  const span = (node.children || []).find((c) => isElement(c, 'span') && hasClass(c, 'listing-number'));
  const m = span && textOf(span).match(CAPTION);
  return m ? `${m[1]}-${m[2]}` : null;
}

/** excerpt を枠ごとにまとめる。返り値は [{ segs: [添字...], label }] */
function groupsOf(dir, excerpt) {
  const groups = [];
  excerpt.forEach((seg, i) => {
    const last = groups[groups.length - 1];
    if (last && separatorOf(excerpt[i - 1], seg) === 'none' && excerpt[i - 1].file === seg.file && excerpt[i - 1].to + 1 === seg.from) {
      last.segs.push(i);
    } else {
      groups.push({ segs: [i] });
    }
  });
  for (const g of groups) {
    const first = excerpt[g.segs[0]];
    const lastSeg = excerpt[g.segs[g.segs.length - 1]];
    const total = readLines(path.join(dir, first.file)).length;
    const whole = first.from === 1 && lastSeg.to === total;
    const range = first.from === lastSeg.to ? `${first.from}行目` : `${first.from}〜${lastSeg.to}行目`;
    g.label = whole ? first.file : `${first.file}　${range}`;
  }
  return groups;
}

function segmentCode(code, id) {
  const ch = Number(id.split('-')[0]);
  const entry = entryOf(ch, id);
  if (!entry || entry.excerpt.length < 2) return false;
  const dir = path.join(chapterDir(ch), entry.dir);
  const groups = groupsOf(dir, entry.excerpt);
  if (groups.length < 2) return false;
  let layout;
  try {
    layout = excerptLayout(dir, entry.excerpt);
  } catch {
    return false;
  }
  const lines = (code.children || []).filter((c) => isElement(c, 'span') && hasClass(c, 'code-line'));
  if (lines.length !== layout.length || lines.length !== code.children.length) return false;
  if (lines.some((line, i) => textOf(line) !== layout[i].text)) return false;

  const groupOf = new Map();
  groups.forEach((g, k) => g.segs.forEach((s) => groupOf.set(s, k)));
  const boxes = groups.map((g) => ({
    type: 'element',
    tagName: 'span',
    properties: { className: ['code-segment'] },
    children: [{ type: 'element', tagName: 'b', properties: { className: ['code-segment-label'] }, children: [{ type: 'text', value: g.label }] }],
  }));
  // 各行に全文での行番号を付ける（表示はテーマの .code-line[data-line]::before）
  const next = entry.excerpt.map((seg) => seg.from);
  lines.forEach((line, i) => {
    const { seg } = layout[i];
    if (seg === null) return;
    line.properties = { ...line.properties, dataLine: String(next[seg]++) };
    boxes[groupOf.get(seg)].children.push(line);
  });
  code.children = boxes;
  return true;
}

function walk(node) {
  let id = null;
  for (const child of node.children || []) {
    if (child.type === 'text' && !child.value.trim()) continue;
    if (isElement(child, 'pre') && id) {
      const code = (child.children || []).find((c) => isElement(c, 'code'));
      if (code && segmentCode(code, id)) {
        const cls = [].concat(child.properties.className || []);
        child.properties.className = [...cls, 'code-excerpt'];
      }
    }
    id = captionId(child);
    if (!id) walk(child);
  }
}

function rehypeCodeSegments() {
  return (tree) => walk(tree);
}

module.exports = { rehypeCodeSegments };
