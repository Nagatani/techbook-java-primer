/**
 * 両端揃え（均等割り付け）で、行の中に意図しない大きな字間ができないようにする。
 *
 * 1. 原稿の改行から生まれるスペースを取り除く
 *    原稿は1文ごとなどで改行しており、HTMLでは改行が半角スペースになる。和文の間に入った
 *    スペースは両端揃えでさらに広がり、「ます。　なお、」のように不自然に空く。
 *    改行を含む空白の前後のどちらかが和文の文字（句読点・括弧を含む）なら、空白を取り除く。
 *    前後とも欧文（「Java 25」のような語の区切り）のときは、スペースのまま残す。
 * 2. インラインコードとURLに、折り返してよい位置（<wbr>）を入れる
 *    `SwingUtilities.invokeLater()` やURLのように途中で折り返せない長い文字列は、
 *    入りきらないと丸ごと次の行に送られ、前の行の空きが字間に配られて大きく開く。
 *    「.」「/」「(」などの後ろで折り返せるようにして、空きを小さくする。
 *
 * コードブロック（pre）の中には手を付けない。
 */

const isElement = (node, tagName) => node && node.type === 'element' && (!tagName || node.tagName === tagName);

// 和文の文字（ひらがな、カタカナ、漢字、全角の記号・句読点・括弧）
const CJK = /[⺀-⿿　-ヿ㐀-䶿一-鿿豈-﫿＀-￯]/;

// 段落の中で文字が続く要素（これ以外の要素をまたいだ空白は、ブロックの区切りなので触らない）
const INLINE = new Set(['a', 'abbr', 'b', 'cite', 'code', 'em', 'i', 'kbd', 'mark', 'q', 'rp', 'rt', 'ruby', 's', 'samp', 'small', 'span', 'strong', 'sub', 'sup', 'u', 'var', 'wbr', 'br']);
const SKIP = new Set(['pre', 'script', 'style', 'svg', 'math']);

const textOf = (node) => (node.type === 'text' ? node.value : (node.children || []).map(textOf).join(''));

// 兄弟をたどって、空白の直前（直後）にある文字を探す。ブロック要素に当たったら null
function neighborChar(parent, index, dir) {
  for (let i = index + dir; i >= 0 && i < parent.children.length; i += dir) {
    const n = parent.children[i];
    if (n.type === 'comment') continue;
    if (n.type === 'text') {
      const t = n.value.trim() ? n.value : '';
      if (!t) continue;
      return dir < 0 ? n.value.replace(/\s+$/, '').slice(-1) || null : n.value.replace(/^\s+/, '')[0] || null;
    }
    if (n.type === 'element') {
      if (!INLINE.has(n.tagName)) return null;
      const t = textOf(n).trim();
      if (!t) continue;
      return dir < 0 ? t.slice(-1) : t[0];
    }
  }
  return null;
}

function removeSegmentBreaks(node, parent, index) {
  const v = node.value;
  if (!v.includes('\n')) return;
  node.value = v.replace(/[ \t]*\n\s*/g, (run, offset) => {
    const before = offset > 0 ? v[offset - 1] : neighborChar(parent, index, -1);
    const end = offset + run.length;
    const after = end < v.length ? v[end] : neighborChar(parent, index, +1);
    // 段落の頭・末尾、ブロック要素との境目の空白はそのまま（表示に影響しない）
    if (before == null || after == null) return run;
    return CJK.test(before) || CJK.test(after) ? '' : ' ';
  });
}

// 折り返してよい位置: 区切りの記号の後ろ（同じ記号が続くときは最後の後ろ）。先頭と末尾には入れない
const CODE_BREAK_AFTER = /[./(,<_=&?#:\-]/;
const URL_PATTERN = /https?:\/\/[\x21-\x7e]+/g;

function splitWithWbr(value, isBreakPoint) {
  const parts = [];
  let buf = '';
  for (let i = 0; i < value.length; i++) {
    buf += value[i];
    const next = value[i + 1];
    if (next !== undefined && next !== value[i] && !/[\s)\]>,;.:]/.test(next) && isBreakPoint(i)) {
      parts.push({ type: 'text', value: buf }, { type: 'element', tagName: 'wbr', properties: {}, children: [] });
      buf = '';
    }
  }
  if (buf) parts.push({ type: 'text', value: buf });
  return parts;
}

// 長い名前（12字以上）は、キャメルケースの区切り（小文字の次の大文字）でも折り返せるようにする
// 例: ArrayIndexOutOfBounds|Exception、Executor|Service。英語の単語の途中では折り返さない。
// 前後に4字以上残る位置に限る（do|InBackground のような短い切れ端を作らない）
const LONG_NAME = 12;
const MIN_PIECE = 4;

function identifierBreakPoint(value) {
  const camelAt = new Array(value.length).fill(false);
  for (const m of value.matchAll(/[A-Za-z0-9$]+/g)) {
    const w = m[0];
    if (w.length < LONG_NAME) continue;
    for (let k = MIN_PIECE - 1; k < w.length - MIN_PIECE; k++) {
      if (/[a-z0-9]/.test(w[k]) && /[A-Z]/.test(w[k + 1])) camelAt[m.index + k] = true;
    }
  }
  return (i) => i > 0 && (CODE_BREAK_AFTER.test(value[i]) || camelAt[i]);
}

function codeBreaks(value) {
  if (/^https?:\/\//.test(value)) return urlBreaks(value);
  return splitWithWbr(value, identifierBreakPoint(value));
}

// 本文にそのまま書いた英字の並び（バッククォートで囲んでいない名前やURL）
const NAME_RUN = /[A-Za-z0-9_$.()[\]<>:/#?&=-]{12,}/g;

function textBreaks(value) {
  const urls = [];
  for (const m of value.matchAll(URL_PATTERN)) urls.push([m.index, m.index + m[0].length]);
  const runs = [];
  for (const m of value.matchAll(NAME_RUN)) {
    if (!/[A-Za-z]/.test(m[0])) continue;
    runs.push([m.index, m.index + m[0].length]);
  }
  if (!urls.length && !runs.length) return null;
  const nameBreak = identifierBreakPoint(value);
  const urlStart = (s) => value.indexOf('//', s) + 2;
  return splitWithWbr(value, (i) => {
    const u = urls.find(([s, e]) => i >= s && i < e);
    if (u) return i >= urlStart(u[0]) && i < u[1] - 1 && CODE_BREAK_AFTER.test(value[i]);
    const r = runs.find(([s, e]) => i > s && i < e - 1);
    return !!r && nameBreak(i);
  });
}

function urlBreaks(value) {
  // URLの部分にだけ入れる
  const ranges = [];
  for (const m of value.matchAll(URL_PATTERN)) ranges.push([m.index, m.index + m[0].length]);
  if (!ranges.length) return null;
  // 「https://」の中では折り返さない
  const inUrl = (i) => ranges.some(([s, e]) => i >= value.indexOf('//', s) + 2 && i < e - 1);
  return splitWithWbr(value, (i) => inUrl(i) && CODE_BREAK_AFTER.test(value[i]));
}

function walk(node, inCode) {
  const children = node.children || [];
  for (let i = 0; i < children.length; i++) {
    const child = children[i];
    if (child.type === 'element') {
      if (SKIP.has(child.tagName)) continue;
      walk(child, inCode || child.tagName === 'code');
      continue;
    }
    if (child.type !== 'text') continue;
    if (!inCode) removeSegmentBreaks(child, node, i);
    const parts = inCode ? codeBreaks(child.value) : textBreaks(child.value);
    if (parts && parts.length > 1) {
      children.splice(i, 1, ...parts);
      i += parts.length - 1;
    }
  }
}

function rehypeLineBreaks() {
  return (tree) => walk(tree, false);
}

module.exports = { rehypeLineBreaks };
