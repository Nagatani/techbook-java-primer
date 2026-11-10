/**
 * コードブロック（pre > code）の中身を1行ずつ <span class="code-line"> に分ける。
 *
 * - 1行が紙面の幅に収まらず折り返したとき、続きの行をその行の字下げ＋4文字の位置から始める
 *   （行ごとの padding-left と負の text-indent で、ぶら下げインデントにする）
 * - 縞模様を行ごとに付けられる（折り返した行も1行として同じ色になる）
 *
 * シンタックスハイライト（Prism）のあとに実行する。複数行にまたがるトークン（ブロックコメントなど）は、
 * 行の境目でいったん閉じ、次の行で同じ要素を開き直す。
 */

const HANGING = 4; // 折り返した行を、元の行の字下げから何文字下げるか

const isElement = (node, tagName) => node && node.type === 'element' && node.tagName === tagName;

/** 要素の子孫のテキストを、祖先の要素（ハイライトのspanなど）の並びと組にして平らにする */
function flatten(nodes, chain, out) {
  for (const node of nodes) {
    if (node.type === 'text') out.push({ text: node.value, chain });
    else if (node.type === 'element') flatten(node.children || [], [...chain, node], out);
  }
  return out;
}

/** 祖先の並びに沿って入れ子の要素を作り、いちばん内側にテキストを置く */
function wrap(chain, text) {
  let node = { type: 'text', value: text };
  for (let i = chain.length - 1; i >= 0; i--) {
    const { tagName, properties } = chain[i];
    node = { type: 'element', tagName, properties: { ...properties }, children: [node] };
  }
  return node;
}

function leadingColumns(text) {
  let n = 0;
  for (const ch of text) {
    if (ch === ' ') n += 1;
    else if (ch === '\t') n += 4;
    else break;
  }
  return n;
}

function splitLines(code) {
  const lines = [[]];
  for (const { text, chain } of flatten(code.children || [], [], [])) {
    text.split('\n').forEach((piece, i) => {
      if (i > 0) lines.push([]);
      if (piece) lines[lines.length - 1].push({ text: piece, chain });
    });
  }
  // 末尾の改行で生じた空の行は捨てる
  if (lines.length > 1 && lines[lines.length - 1].length === 0) lines.pop();
  return lines.map((segments) => {
    const plain = segments.map((s) => s.text).join('');
    const indent = leadingColumns(plain) + HANGING;
    return {
      type: 'element',
      tagName: 'span',
      properties: {
        className: ['code-line'],
        style: `padding-left: ${indent}ch; text-indent: -${indent}ch;`,
      },
      children: segments.map((s) => wrap(s.chain, s.text)),
    };
  });
}

function walk(node, visit) {
  visit(node);
  for (const child of node.children || []) walk(child, visit);
}

function rehypeCodeLines() {
  return (tree) => {
    walk(tree, (node) => {
      if (!isElement(node, 'pre')) return;
      for (const code of node.children || []) {
        if (isElement(code, 'code')) code.children = splitLines(code);
      }
    });
  };
}

module.exports = { rehypeCodeLines };
