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
 * 同じファイルの続いた範囲は1つの枠にまとめる。範囲が1つだけのサンプルは枠を分けず、
 * 行番号だけを付ける（pre.code-numbered）。全文と対応づけられないコードブロック（ファイル全体を載せたもの、
 * listings.json にないもの）は1から順に行番号を付ける。言語を指定していないブロック（実行結果など）と
 * コマンドの例（bash）には付けない。本文のコードが listings.json と食い違うとき（samples:sync の前など）も
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

/**
 * コードブロックを範囲ごとの枠に分けるか、行番号だけを付ける。
 * 返り値は pre に足すクラス（'code-excerpt' は枠に分けたもの、'code-numbered' は行番号だけ）か null
 */
function segmentCode(code, id) {
  const ch = Number(id.split('-')[0]);
  const entry = entryOf(ch, id);
  if (!entry) return null;
  const dir = path.join(chapterDir(ch), entry.dir);
  let groups;
  let layout;
  try {
    groups = groupsOf(dir, entry.excerpt);
    layout = excerptLayout(dir, entry.excerpt);
  } catch {
    return null;
  }
  // 1つのファイルの全体を載せたもの（キャプションに行番号がない）はそのままにする
  if (groups.length === 1 && groups[0].label === entry.excerpt[0].file) return null;
  const lines = (code.children || []).filter((c) => isElement(c, 'span') && hasClass(c, 'code-line'));
  if (lines.length !== layout.length || lines.length !== code.children.length) return null;
  if (lines.some((line, i) => textOf(line) !== layout[i].text)) return null;

  // 各行に全文での行番号を付ける（表示はテーマの .code-line[data-line]::before）
  const next = entry.excerpt.map((seg) => seg.from);
  lines.forEach((line, i) => {
    const { seg } = layout[i];
    if (seg !== null) line.properties = { ...line.properties, dataLine: String(next[seg]++) };
  });
  // 範囲が1つ（続いた範囲を含む）なら枠は分けず、行番号だけにする
  if (groups.length === 1) return 'code-numbered';

  const groupOf = new Map();
  groups.forEach((g, k) => g.segs.forEach((s) => groupOf.set(s, k)));
  const boxes = groups.map((g) => ({
    type: 'element',
    tagName: 'span',
    properties: { className: ['code-segment'] },
    children: [{ type: 'element', tagName: 'b', properties: { className: ['code-segment-label'] }, children: [{ type: 'text', value: g.label }] }],
  }));
  lines.forEach((line, i) => {
    if (layout[i].seg !== null) boxes[groupOf.get(layout[i].seg)].children.push(line);
  });
  code.children = boxes;
  return 'code-excerpt';
}

/** 行番号を付けないブロック（言語を指定していない実行結果など、コマンドの例） */
const UNNUMBERED = ['code-plain', 'language-bash', 'language-shell', 'language-sh', 'language-console'];

/** 全文と対応づけられないコードブロックは、1から順に行番号を付ける */
function numberLines(code) {
  const lines = (code.children || []).filter((c) => isElement(c, 'span') && hasClass(c, 'code-line'));
  if (!lines.length) return false;
  lines.forEach((line, i) => { line.properties = { ...line.properties, dataLine: String(i + 1) }; });
  return true;
}

function walk(node) {
  let id = null;
  for (const child of node.children || []) {
    if (child.type === 'text' && !child.value.trim()) continue;
    if (isElement(child, 'pre') && !UNNUMBERED.some((c) => hasClass(child, c))) {
      const code = (child.children || []).find((c) => isElement(c, 'code'));
      const added = code && ((id && segmentCode(code, id)) || (numberLines(code) && 'code-numbered'));
      if (added) child.properties.className = [...[].concat(child.properties.className || []), added];
    }
    id = captionId(child);
    if (!id) walk(child);
  }
}

function rehypeCodeSegments() {
  return (tree) => walk(tree);
}

module.exports = { rehypeCodeSegments };
